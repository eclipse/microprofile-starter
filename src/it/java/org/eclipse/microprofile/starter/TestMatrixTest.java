/*
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.eclipse.microprofile.starter;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.utils.Commands;
import org.eclipse.microprofile.starter.utils.MPSpec;
import org.eclipse.microprofile.starter.utils.SpecSelection;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.eclipse.microprofile.starter.utils.Commands.cleanWorkspace;
import static org.eclipse.microprofile.starter.utils.Commands.download;
import static org.eclipse.microprofile.starter.utils.Commands.getWorkspaceDir;
import static org.eclipse.microprofile.starter.utils.Commands.processStopper;
import static org.eclipse.microprofile.starter.utils.Commands.runCommand;
import static org.eclipse.microprofile.starter.utils.Commands.unzip;
import static org.eclipse.microprofile.starter.utils.Logs.archiveLog;
import static org.eclipse.microprofile.starter.utils.Logs.checkLog;
import static org.eclipse.microprofile.starter.utils.ReadmeParser.parseReadme;
import static org.eclipse.microprofile.starter.utils.WebpageTester.testWeb;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * MicroProfile Starter runtimes servers smoke tests.
 *
 * The goal is to make sure all our runtimes can be built and run without errors
 * and that the example applications show expected outputs.
 *
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.WAR)
public class TestMatrixTest {

    private static final Logger LOGGER = Logger.getLogger(TestMatrixTest.class.getName());

    public static final String TMP = getWorkspaceDir();

    public static final String API_URL = "http://127.0.0.1:9090/api";
    final Client client = ClientBuilder.newBuilder().build();

    WebTarget target;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void before() {
        target = client.target(API_URL);
    }

    public void testRuntime(String supportedServer, String artifactId, SpecSelection specSelection, int[] additionalPotsToCheck) throws IOException, InterruptedException {
        LOGGER.info("Testing server: " + supportedServer + ", config: " + specSelection.toString());

        Process pB = null;
        Process pA = null;
        File unzipLog = null;
        File buildLogA = null;
        File buildLogB = null;
        File runLogA = null;
        File runLogB = null;

        try {
            // Cleanup
            cleanWorkspace(artifactId);

            // Download
            LOGGER.info("Downloading...");
            String location = TMP + File.separator + artifactId + ".zip";
            download(client, supportedServer, artifactId, specSelection, location);

            // Unzip
            unzipLog = unzip(location, artifactId);

            // Parse README
            File directoryA = null;
            File directoryB = null;
            String[][] buildCmdRunCmd = null;
            if (specSelection.hasServiceB) {
                directoryA = new File(TMP + File.separator + artifactId + File.separator + "service-a" + File.separator);
                directoryB = new File(TMP + File.separator + artifactId + File.separator + "service-b" + File.separator);
                File readmeB = new File(directoryB, "readme.md");
                buildCmdRunCmd = parseReadme(readmeB, false);
            } else {
                directoryA = new File(TMP + File.separator + artifactId + File.separator);
            }
            File readmeA = new File(directoryA, "readme.md");
            String[][] buildCmdRunCmdWebAddr = parseReadme(readmeA, true);

            // Build
            LOGGER.info("Build might take many minutes if the whole Internet is being downloaded.");
            buildLogA = new File(directoryA.getAbsolutePath() + File.separator + directoryA.getName() + "-build.log");
            if (specSelection.hasServiceB) {
                buildLogB = new File(directoryB.getAbsolutePath() + File.separator + directoryB.getName() + "-build.log");
            }
            ExecutorService buildService = Executors.newFixedThreadPool(2);
            buildService.submit(new Commands.ProcessRunner(directoryA, buildLogA, buildCmdRunCmdWebAddr[0], 20));
            if (specSelection.hasServiceB) {
                buildService.submit(new Commands.ProcessRunner(directoryB, buildLogB, buildCmdRunCmd[0], 20));
            }
            buildService.shutdown();
            buildService.awaitTermination(30, TimeUnit.MINUTES);

            assertTrue(buildLogA.exists());
            checkLog(this.getClass().getCanonicalName(), testName.getMethodName(), "Build log", buildLogA);
            if (specSelection.hasServiceB) {
                assertTrue(buildLogB.exists());
                checkLog(this.getClass().getCanonicalName(), testName.getMethodName(), "Build log", buildLogB);
            }

            // Run Service A (and Service B)
            LOGGER.info("Running...");
            runLogA = new File(directoryA.getAbsolutePath() + File.separator + directoryA.getName() + "-run.log");
            pA = runCommand(buildCmdRunCmdWebAddr[1], directoryA, runLogA);
            if (specSelection.hasServiceB) {
                runLogB = new File(directoryB.getAbsolutePath() + File.separator + directoryB.getName() + "-run.log");
                pB = runCommand(buildCmdRunCmd[1], directoryB, runLogB);
            }

            // Test web pages
            LOGGER.info("Testing web page content...");
            String homePage = buildCmdRunCmdWebAddr[2][0];
            String urlBase = homePage.replace("index.html", "");
            testWebPages(urlBase, homePage, supportedServer, specSelection);

            LOGGER.info("Terminate and scan logs...");
            // This is a move that makes it flush on my Linux x86_64 OpenJDK J9/HotSpot 11. Does no harm on Win.
            pA.getInputStream().available();
            if (specSelection.hasServiceB) {
                pB.getInputStream().available();
            }

            processStopper(pA, artifactId);
            if (specSelection.hasServiceB) {
                processStopper(pB, artifactId);
            }

            checkLog(this.getClass().getCanonicalName(), testName.getMethodName(), "Runtime log", runLogA);
            if (specSelection.hasServiceB) {
                checkLog(this.getClass().getCanonicalName(), testName.getMethodName(), "Runtime log", runLogB);
            }
            LOGGER.info("Gonna wait for ports closed...");
            // Release ports
            assertTrue("Main ports are still open",
                    Commands.waitForTcpClosed("localhost", Commands.parsePort(urlBase), 60));
            if (additionalPotsToCheck != null) {
                for (int port : additionalPotsToCheck) {
                    assertTrue("Ports are still open",
                            Commands.waitForTcpClosed("localhost", port, 30));
                }
            }
        } finally {
            client.close();
            // Make sure processes are down even if there was an exception / failure
            if (pA != null) {
                processStopper(pA, artifactId);

            }
            if (specSelection.hasServiceB && pB != null) {
                processStopper(pB, artifactId);
            }
            // Archive logs no matter what
            archiveLog(this.getClass().getCanonicalName(), testName.getMethodName(), unzipLog);
            archiveLog(this.getClass().getCanonicalName(), testName.getMethodName(), buildLogA);
            archiveLog(this.getClass().getCanonicalName(), testName.getMethodName(), runLogA);
            if (specSelection.hasServiceB) {
                archiveLog(this.getClass().getCanonicalName(), testName.getMethodName(), buildLogB);
                archiveLog(this.getClass().getCanonicalName(), testName.getMethodName(), runLogB);
            }
            cleanWorkspace(artifactId);
        }
    }

    public void testWebPages(String urlBase, String homePage, String supportedServer,
                             SpecSelection specSelection) throws IOException, InterruptedException {
        // First landing page
        testWeb(homePage, 60, "MicroProfile");
        String specialUrlBase;
        if (supportedServer.equalsIgnoreCase("TOMEE")) {
            // Tomee has a special case of prepending context
            specialUrlBase = urlBase + "data/";
        } else if (supportedServer.equalsIgnoreCase("WILDFLY")) {
            // Wildfly has a special port
            specialUrlBase = urlBase.replace(SupportedServer.WILDFLY.getPortServiceA(), "9990");
        } else {
            specialUrlBase = urlBase;
        }
        // Spec by spec test
        if (specSelection == SpecSelection.EMPTY) {
            // Verify that links are present on the index.html page
            testWeb(homePage, 10, MPSpec.DEFAULT.urlContent[0][0]);
            // Verify content
            testWeb(urlBase + MPSpec.DEFAULT.urlContent[0][0], 5, MPSpec.DEFAULT.urlContent[0][1]);
        } else if (specSelection == SpecSelection.ALL) {
            // Verify content
            testWeb(urlBase + MPSpec.DEFAULT.urlContent[0][0], 5, MPSpec.DEFAULT.urlContent[0][1]);
            testWeb(urlBase + MPSpec.CONFIG.urlContent[0][0], 5, MPSpec.CONFIG.urlContent[0][1]);
            testWeb(urlBase + MPSpec.CONFIG.urlContent[1][0], 5, MPSpec.CONFIG.urlContent[1][1]);
            testWeb(urlBase + MPSpec.FAULT_TOLERANCE.urlContent[0][0], 5, MPSpec.FAULT_TOLERANCE.urlContent[0][1]);
            testWeb(specialUrlBase + MPSpec.HEALTH_CHECKS.urlContent[0][0], 5, MPSpec.HEALTH_CHECKS.urlContent[0][1]);
            testWeb(urlBase + MPSpec.METRICS.urlContent[0][0], 5, MPSpec.METRICS.urlContent[0][1]);
            testWeb(specialUrlBase + MPSpec.METRICS.urlContent[1][0], 10, MPSpec.METRICS.urlContent[1][1]);
            testWeb(urlBase + MPSpec.JWT_AUTH.urlContent[0][0], 5, MPSpec.JWT_AUTH.urlContent[0][1]);
            if (supportedServer.equalsIgnoreCase("TOMEE")) {
                testWeb(specialUrlBase + MPSpec.OPEN_API.urlContent[0][0], 5, MPSpec.OPEN_API.urlContent[0][1]);
            } else {
                testWeb(urlBase + MPSpec.OPEN_API.urlContent[0][0], 5, MPSpec.OPEN_API.urlContent[0][1]);
            }
            testWeb(urlBase + MPSpec.REST_CLIENT.urlContent[0][0], 5, MPSpec.REST_CLIENT.urlContent[0][1]);
        } else if (specSelection == SpecSelection.ALL_BUT_JWT_REST) {
            // Verify content
            testWeb(urlBase + MPSpec.DEFAULT.urlContent[0][0], 5, MPSpec.DEFAULT.urlContent[0][1]);
            testWeb(urlBase + MPSpec.CONFIG.urlContent[0][0], 5, MPSpec.CONFIG.urlContent[0][1]);
            testWeb(urlBase + MPSpec.CONFIG.urlContent[1][0], 5, MPSpec.CONFIG.urlContent[1][1]);
            testWeb(urlBase + MPSpec.FAULT_TOLERANCE.urlContent[0][0], 5, MPSpec.FAULT_TOLERANCE.urlContent[0][1]);
            testWeb(specialUrlBase + MPSpec.HEALTH_CHECKS.urlContent[0][0], 5, MPSpec.HEALTH_CHECKS.urlContent[0][1]);
            testWeb(urlBase + MPSpec.METRICS.urlContent[0][0], 5, MPSpec.METRICS.urlContent[0][1]);
            testWeb(specialUrlBase + MPSpec.METRICS.urlContent[1][0], 5, MPSpec.METRICS.urlContent[1][1]);
            if (supportedServer.equalsIgnoreCase("TOMEE")) {
                testWeb(specialUrlBase + MPSpec.OPEN_API.urlContent[0][0], 5, MPSpec.OPEN_API.urlContent[0][1]);
            } else {
                testWeb(urlBase + MPSpec.OPEN_API.urlContent[0][0], 5, MPSpec.OPEN_API.urlContent[0][1]);
            }
        } else if (specSelection == SpecSelection.JWT_REST) {
            // Verify content
            testWeb(urlBase + MPSpec.DEFAULT.urlContent[0][0], 5, MPSpec.DEFAULT.urlContent[0][1]);
            testWeb(urlBase + MPSpec.JWT_AUTH.urlContent[0][0], 5, MPSpec.JWT_AUTH.urlContent[0][1]);
            testWeb(urlBase + MPSpec.REST_CLIENT.urlContent[0][0], 5, MPSpec.REST_CLIENT.urlContent[0][1]);
        } else if (specSelection == SpecSelection.CONFIG) {
            testWeb(urlBase + MPSpec.DEFAULT.urlContent[0][0], 5, MPSpec.DEFAULT.urlContent[0][1]);
            testWeb(urlBase + MPSpec.CONFIG.urlContent[0][0], 5, MPSpec.CONFIG.urlContent[0][1]);
            testWeb(urlBase + MPSpec.CONFIG.urlContent[1][0], 5, MPSpec.CONFIG.urlContent[1][1]);
        } else if (specSelection == SpecSelection.FAULT_TOLERANCE) {
            testWeb(urlBase + MPSpec.FAULT_TOLERANCE.urlContent[0][0], 5, MPSpec.FAULT_TOLERANCE.urlContent[0][1]);
        } else if (specSelection == SpecSelection.HEALTH_CHECKS) {
            testWeb(specialUrlBase + MPSpec.HEALTH_CHECKS.urlContent[0][0], 5, MPSpec.HEALTH_CHECKS.urlContent[0][1]);
        } else if (specSelection == SpecSelection.JWT_AUTH) {
            testWeb(urlBase + MPSpec.JWT_AUTH.urlContent[0][0], 5, MPSpec.JWT_AUTH.urlContent[0][1]);
        } else if (specSelection == SpecSelection.METRICS) {
            testWeb(urlBase + MPSpec.METRICS.urlContent[0][0], 5, MPSpec.METRICS.urlContent[0][1]);
            testWeb(specialUrlBase + MPSpec.METRICS.urlContent[1][0], 5, MPSpec.METRICS.urlContent[1][1]);
        } else if (specSelection == SpecSelection.OPEN_API) {
            if (supportedServer.equalsIgnoreCase("TOMEE")) {
                testWeb(specialUrlBase + MPSpec.OPEN_API.urlContent[0][0], 5, MPSpec.OPEN_API.urlContent[0][1]);
            } else {
                testWeb(urlBase + MPSpec.OPEN_API.urlContent[0][0], 5, MPSpec.OPEN_API.urlContent[0][1]);
            }
        } else if (specSelection == SpecSelection.OPEN_TRACING) {
            // No example for this one. Would need Jeger etc.
            testWeb(urlBase + MPSpec.DEFAULT.urlContent[0][0], 5, MPSpec.DEFAULT.urlContent[0][1]);
        } else if (specSelection == SpecSelection.REST_CLIENT) {
            testWeb(urlBase + MPSpec.REST_CLIENT.urlContent[0][0], 5, MPSpec.REST_CLIENT.urlContent[0][1]);
        } else {
            throw new IllegalArgumentException(
                    "Unexpected SpecSelection enum value. Have you updated SpecSelection?");
        }
    }


    @Test
    @RunAsClient
    @InSequence(0)
    public void apiAccessibleSanity() {
        Response response = target.request().get();
        assertEquals("MicroProfile Starter REST API should be available", Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    @RunAsClient
    @InSequence(1)
    public void thorntailEmpty() throws IOException, InterruptedException {
        testRuntime("THORNTAIL_V2", "thorntail",
                SpecSelection.EMPTY, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void thorntailAll() throws IOException, InterruptedException {
        testRuntime("THORNTAIL_V2", "thorntail",
                SpecSelection.ALL, new int[]{9990, 8180, 10090});
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void thorntailAllButJWTRest() throws IOException, InterruptedException {
        testRuntime("THORNTAIL_V2", "thorntail",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(4)
    public void thorntailJWTRest() throws IOException, InterruptedException {
        testRuntime("THORNTAIL_V2", "thorntail",
                SpecSelection.JWT_REST, new int[]{9990, 8180, 10090});
    }

    @Test
    @RunAsClient
    @InSequence(5)
    public void payaraEmpty() throws IOException, InterruptedException {
        testRuntime("PAYARA_MICRO", "payara",
                SpecSelection.EMPTY, new int[]{6900});
    }

    @Test
    @RunAsClient
    @InSequence(6)
    public void payaraAll() throws IOException, InterruptedException {
        testRuntime("PAYARA_MICRO", "payara",
                SpecSelection.ALL, new int[]{6900, 6901, 8180});
    }

    @Test
    @RunAsClient
    @InSequence(7)
    public void payaraAllButJWTRest() throws IOException, InterruptedException {
        testRuntime("PAYARA_MICRO", "payara",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{6900});
    }

    @Test
    @RunAsClient
    @InSequence(8)
    public void payaraJWTRest() throws IOException, InterruptedException {
        testRuntime("PAYARA_MICRO", "payara",
                SpecSelection.JWT_REST, new int[]{6900, 6901, 8180});
    }

    @Test
    @RunAsClient
    @InSequence(9)
    public void libertyEmpty() throws IOException, InterruptedException {
        testRuntime("LIBERTY", "liberty",
                SpecSelection.EMPTY, new int[]{8181, 9080, 8543, 9443});
    }

    @Test
    @RunAsClient
    @InSequence(10)
    public void libertyAll() throws IOException, InterruptedException {
        testRuntime("LIBERTY", "liberty",
                SpecSelection.ALL, new int[]{8181, 9080, 8543, 9443, 9444, 8281, 9081});
    }

    @Test
    @RunAsClient
    @InSequence(11)
    public void libertyAllButJWTRest() throws IOException, InterruptedException {
        testRuntime("LIBERTY", "liberty",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{8181, 9080, 8543, 9443});
    }

    @Test
    @RunAsClient
    @InSequence(12)
    public void libertyJWTRest() throws IOException, InterruptedException {
        testRuntime("LIBERTY", "liberty",
                SpecSelection.JWT_REST, new int[]{8181, 9080, 8543, 9443, 9444, 8281, 9081});
    }

    @Test
    @RunAsClient
    @InSequence(13)
    public void helidonEmpty() throws IOException, InterruptedException {
        testRuntime("HELIDON", "helidon",
                SpecSelection.EMPTY, new int[]{});
    }

    @Test
    @RunAsClient
    @InSequence(14)
    public void helidonAll() throws IOException, InterruptedException {
        testRuntime("HELIDON", "helidon",
                SpecSelection.ALL, new int[]{8180});
    }

    @Test
    @RunAsClient
    @InSequence(15)
    public void helidonAllButJWTRest() throws IOException, InterruptedException {
        testRuntime("HELIDON", "helidon",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{});
    }

    @Test
    @RunAsClient
    @InSequence(16)
    public void helidonJWTRest() throws IOException, InterruptedException {
        testRuntime("HELIDON", "helidon",
                SpecSelection.JWT_REST, new int[]{8180});
    }

    @Test
    @RunAsClient
    @InSequence(17)
    public void kumuluzeeEmpty() throws IOException, InterruptedException {
        testRuntime("KUMULUZEE", "kumuluzee",
                SpecSelection.EMPTY, new int[]{});
    }

    @Test
    @RunAsClient
    @InSequence(18)
    public void kumuluzeeAll() throws IOException, InterruptedException {
        testRuntime("KUMULUZEE", "kumuluzee",
                SpecSelection.ALL, new int[]{8180});
    }

    @Test
    @RunAsClient
    @InSequence(19)
    public void kumuluzeeAllButJWTRest() throws IOException, InterruptedException {
        testRuntime("KUMULUZEE", "kumuluzee",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{});
    }

    @Test
    @RunAsClient
    @InSequence(20)
    public void kumuluzeeJWTRest() throws IOException, InterruptedException {
        testRuntime("KUMULUZEE", "kumuluzee",
                SpecSelection.JWT_REST, new int[]{8180});
    }

    @Test
    @RunAsClient
    @InSequence(21)
    public void tomeeEmpty() throws IOException, InterruptedException {
        testRuntime("TOMEE", "tomee",
                SpecSelection.EMPTY, new int[]{8009, 8005});
    }

    @Test
    @RunAsClient
    @InSequence(22)
    public void tomeeAll() throws IOException, InterruptedException {
        testRuntime("TOMEE", "tomee",
                SpecSelection.ALL, new int[]{8009, 8005, 8180, 8109, 8105});
    }

    @Test
    @RunAsClient
    @InSequence(23)
    public void tomeeAllButJWTRest() throws IOException, InterruptedException {
        testRuntime("TOMEE", "tomee",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{8009, 8005});
    }

    @Test
    @RunAsClient
    @InSequence(24)
    public void tomeeJWTRest() throws IOException, InterruptedException {
        testRuntime("TOMEE", "tomee",
                SpecSelection.JWT_REST, new int[]{8009, 8005, 8180, 8109, 8105});
    }

    @Test
    @RunAsClient
    @InSequence(25)
    public void quarkusEmpty() throws IOException, InterruptedException {
        testRuntime("QUARKUS", "quarkus",
                SpecSelection.EMPTY, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(26)
    public void quarkusAll() throws IOException, InterruptedException {
        testRuntime("QUARKUS", "quarkus",
                SpecSelection.ALL, new int[]{9990, 8180, 10090});
    }

    @Test
    @RunAsClient
    @InSequence(27)
    public void quarkusAllButJWTRest() throws IOException, InterruptedException {
        testRuntime("QUARKUS", "quarkus",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(28)
    public void quarkusJWTRest() throws IOException, InterruptedException {
        testRuntime("QUARKUS", "quarkus",
                SpecSelection.JWT_REST, new int[]{9990, 8180, 10090});
    }

    @Test
    @RunAsClient
    @InSequence(29)
    public void wildflyEmpty() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.EMPTY, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(30)
    public void wildflyAll() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.ALL, new int[]{9990, 8180, 10090});
    }

    @Test
    @RunAsClient
    @InSequence(31)
    public void wildflyAllButJWTRest() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(32)
    public void wildflyJWTRest() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.JWT_REST, new int[]{9990, 8180, 10090});
    }

    @Test
    @RunAsClient
    @InSequence(33)
    public void wildflyConfig() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.CONFIG, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(34)
    public void wildflyFaultTolerance() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.FAULT_TOLERANCE, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(35)
    public void wildflyHealthchecks() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.HEALTH_CHECKS, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(36)
    public void wildflyJWTAuth() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.JWT_AUTH, new int[]{9990, 8180, 10090});
    }

    @Test
    @RunAsClient
    @InSequence(37)
    public void wildflyMetrics() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.METRICS, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(38)
    public void wildflyOpenAPI() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.OPEN_API, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(39)
    public void wildflyOpenTracing() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.OPEN_TRACING, new int[]{9990});
    }

    @Test
    @RunAsClient
    @InSequence(40)
    public void wildflyRestClient() throws IOException, InterruptedException {
        testRuntime("WILDFLY", "wildfly",
                SpecSelection.REST_CLIENT, new int[]{9990, 8180, 10090});
    }
}