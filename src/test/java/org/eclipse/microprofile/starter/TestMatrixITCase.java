/*
 * Copyright (c) 2017 - 2022 Contributors to the Eclipse Foundation
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

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.BuildTool;
import org.eclipse.microprofile.starter.utils.Commands;
import org.eclipse.microprofile.starter.utils.MPSpecGET;
import org.eclipse.microprofile.starter.utils.MPSpecPOST;
import org.eclipse.microprofile.starter.utils.SpecSelection;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.eclipse.microprofile.starter.utils.Commands.IS_THIS_WINDOWS;
import static org.eclipse.microprofile.starter.utils.Commands.cleanWorkspace;
import static org.eclipse.microprofile.starter.utils.Commands.getWorkspaceDir;
import static org.eclipse.microprofile.starter.utils.Commands.linuxCmdCleaner;
import static org.eclipse.microprofile.starter.utils.Commands.processStopper;
import static org.eclipse.microprofile.starter.utils.Commands.runCommand;
import static org.eclipse.microprofile.starter.utils.Commands.unzip;
import static org.eclipse.microprofile.starter.utils.Commands.windowsCmdCleaner;
import static org.eclipse.microprofile.starter.utils.Logs.archiveLog;
import static org.eclipse.microprofile.starter.utils.Logs.checkLog;
import static org.eclipse.microprofile.starter.utils.ReadmeParser.parseReadme;
import static org.eclipse.microprofile.starter.utils.WebpageTester.testWeb;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MicroProfile Starter runtimes servers smoke tests.
 *
 * The goal is to make sure all our runtimes can be built and run without errors
 * and that the example applications show expected outputs.
 *
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@QuarkusTest
public class TestMatrixITCase {

    private static final Logger LOGGER = Logger.getLogger(TestMatrixITCase.class);

    public static final String WORKSPACE_DIR = getWorkspaceDir();

    public void testRuntime(TestInfo testInfo, String supportedServer, String artifactId, SpecSelection specSelection,
                            int[] additionalPortsToCheck, BuildTool buildTool)
            throws IOException, InterruptedException {
        final String cn = testInfo.getTestClass().get().getCanonicalName();
        final String mn = testInfo.getTestMethod().get().getName();
        LOGGER.info("Testing server: " + supportedServer + ", config: " + specSelection.toString() + ", buildTool: " + buildTool.toString());
        Process pB = null;
        Process pA = null;
        File unzipLog = null;
        File buildLogA = null;
        File buildLogB = null;
        File runLogA = null;
        File runLogB = null;
        File directoryA = null;
        File directoryB = null;

        try {
            // Cleanup
            cleanWorkspace(artifactId);

            // Download
            LOGGER.info("Downloading...");
            String location = WORKSPACE_DIR + File.separator + artifactId + ".zip";
            download(supportedServer, artifactId, specSelection, buildTool, location);

            // Unzip
            unzipLog = unzip(location, artifactId);

            // Parse README
            String[][] buildCmdRunCmd = null;
            if (specSelection.hasServiceB) {
                directoryA = new File(WORKSPACE_DIR + File.separator + artifactId + File.separator + "service-a" + File.separator);
                directoryB = new File(WORKSPACE_DIR + File.separator + artifactId + File.separator + "service-b" + File.separator);
                File readmeB = new File(directoryB, "readme.md");
                buildCmdRunCmd = parseReadme(readmeB, false);
            } else {
                directoryA = new File(WORKSPACE_DIR + File.separator + artifactId + File.separator);
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
            checkLog(cn, mn, "Build log", buildLogA);
            if (specSelection.hasServiceB) {
                assertTrue(buildLogB.exists());
                checkLog(cn, mn, "Build log", buildLogB);
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
            if (buildTool == BuildTool.GRADLE && supportedServer.equalsIgnoreCase("LIBERTY")) {
                Commands.runCommand(new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "libertyStop"}, directoryA, runLogA);
            }
            if (specSelection.hasServiceB) {
                processStopper(pB, artifactId);
                if (buildTool == BuildTool.GRADLE && supportedServer.equalsIgnoreCase("LIBERTY")) {
                    Commands.runCommand(new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "libertyStop"}, directoryB, runLogB);
                }
            }
            checkLog(cn, mn, "Runtime log", runLogA);
            if (specSelection.hasServiceB) {
                checkLog(cn, mn, "Runtime log", runLogB);
            }
            LOGGER.info("Gonna wait for ports closed...");
            // Release ports
            assertTrue(Commands.waitForTcpClosed("localhost", Commands.parsePort(urlBase), 90), "Main ports are still open");
            if (additionalPortsToCheck != null) {
                for (int port : additionalPortsToCheck) {
                    assertTrue(Commands.waitForTcpClosed("localhost", port, 60), "Ports are still open");
                }
            }
        } finally {
            // Make sure processes are down even if there was an exception / failure
            if (pA != null) {
                processStopper(pA, artifactId);
            }
            if (specSelection.hasServiceB && pB != null) {
                processStopper(pB, artifactId);
            }
            // Archive logs no matter what
            archiveLog(cn, mn, unzipLog);
            archiveLog(cn, mn, buildLogA);
            archiveLog(cn, mn, runLogA);
            if (specSelection.hasServiceB) {
                archiveLog(cn, mn, buildLogB);
                archiveLog(cn, mn, runLogB);
            }
            // We can't trust servers to stop the daemon correctly. Usually it's Liberty who doesn't stop it. Could be others.
            if (buildTool == BuildTool.GRADLE) {
                Commands.runCommand(new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "--stop"}, directoryA, runLogA);
                if (specSelection.hasServiceB) {
                    Commands.runCommand(new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "--stop"}, directoryB, runLogB);
                }
            }
            if (IS_THIS_WINDOWS) {
                windowsCmdCleaner("defaultServer", "gradle-launcher", "GradleDaemon", "wrapper/dists/gradle", "gradle-wrapper");
            } else {
                linuxCmdCleaner("defaultServer", "gradle-launcher", "GradleDaemon", "wrapper/dists/gradle", "gradle-wrapper");
            }
            cleanWorkspace(artifactId);
        }
    }

    public void testWebPages(String urlBase, String homePage, String supportedServer,
                             SpecSelection specSelection) throws IOException, InterruptedException {
        // First landing page
        testWeb(homePage, IS_THIS_WINDOWS ? 120 : 60, "MicroProfile");
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
            testWeb(homePage, 10, MPSpecGET.DEFAULT.urlContent[0][0]);
            // Verify content
            testWeb(urlBase + MPSpecGET.DEFAULT.urlContent[0][0], 5, MPSpecGET.DEFAULT.urlContent[0][1]);
        } else if (specSelection == SpecSelection.ALL) {
            // Verify content
            testWeb(urlBase + MPSpecGET.DEFAULT.urlContent[0][0], 5, MPSpecGET.DEFAULT.urlContent[0][1]);
            testWeb(urlBase + MPSpecGET.CONFIG.urlContent[0][0], 5, MPSpecGET.CONFIG.urlContent[0][1]);
            testWeb(urlBase + MPSpecGET.CONFIG.urlContent[1][0], 5, MPSpecGET.CONFIG.urlContent[1][1]);
            testWeb(urlBase + MPSpecGET.FAULT_TOLERANCE.urlContent[0][0], 5, MPSpecGET.FAULT_TOLERANCE.urlContent[0][1]);
            testWeb(specialUrlBase + MPSpecGET.HEALTH_CHECKS.urlContent[0][0], 5, MPSpecGET.HEALTH_CHECKS.urlContent[0][1]);
            testWeb(urlBase + MPSpecGET.METRICS.urlContent[0][0], 5, MPSpecGET.METRICS.urlContent[0][1]);
            testWeb(specialUrlBase + MPSpecGET.METRICS.urlContent[1][0], 10, MPSpecGET.METRICS.urlContent[1][1]);
            testWeb(urlBase + MPSpecGET.JWT_AUTH.urlContent[0][0], 5, MPSpecGET.JWT_AUTH.urlContent[0][1]);
            if (supportedServer.equalsIgnoreCase("TOMEE")) {
                testWeb(specialUrlBase + MPSpecGET.OPEN_API.urlContent[0][0], 5, MPSpecGET.OPEN_API.urlContent[0][1]);
            } else {
                testWeb(urlBase + MPSpecGET.OPEN_API.urlContent[0][0], 5, MPSpecGET.OPEN_API.urlContent[0][1]);
            }
            testWeb(urlBase + MPSpecGET.REST_CLIENT.urlContent[0][0], 5, MPSpecGET.REST_CLIENT.urlContent[0][1]);
        } else if (specSelection == SpecSelection.ALL_BUT_JWT_REST) {
            // Verify content
            testWeb(urlBase + MPSpecGET.DEFAULT.urlContent[0][0], 5, MPSpecGET.DEFAULT.urlContent[0][1]);
            testWeb(urlBase + MPSpecGET.CONFIG.urlContent[0][0], 5, MPSpecGET.CONFIG.urlContent[0][1]);
            testWeb(urlBase + MPSpecGET.CONFIG.urlContent[1][0], 5, MPSpecGET.CONFIG.urlContent[1][1]);
            testWeb(urlBase + MPSpecGET.FAULT_TOLERANCE.urlContent[0][0], 5, MPSpecGET.FAULT_TOLERANCE.urlContent[0][1]);
            testWeb(specialUrlBase + MPSpecGET.HEALTH_CHECKS.urlContent[0][0], 5, MPSpecGET.HEALTH_CHECKS.urlContent[0][1]);
            testWeb(urlBase + MPSpecGET.METRICS.urlContent[0][0], 5, MPSpecGET.METRICS.urlContent[0][1]);
            testWeb(specialUrlBase + MPSpecGET.METRICS.urlContent[1][0], 5, MPSpecGET.METRICS.urlContent[1][1]);
            if (supportedServer.equalsIgnoreCase("TOMEE")) {
                testWeb(specialUrlBase + MPSpecGET.OPEN_API.urlContent[0][0], 5, MPSpecGET.OPEN_API.urlContent[0][1]);
            } else {
                testWeb(urlBase + MPSpecGET.OPEN_API.urlContent[0][0], 5, MPSpecGET.OPEN_API.urlContent[0][1]);
            }
        } else if (specSelection == SpecSelection.JWT_REST) {
            // Verify content
            testWeb(urlBase + MPSpecGET.DEFAULT.urlContent[0][0], 5, MPSpecGET.DEFAULT.urlContent[0][1]);
            testWeb(urlBase + MPSpecGET.JWT_AUTH.urlContent[0][0], 5, MPSpecGET.JWT_AUTH.urlContent[0][1]);
            testWeb(urlBase + MPSpecGET.REST_CLIENT.urlContent[0][0], 5, MPSpecGET.REST_CLIENT.urlContent[0][1]);
        } else if (specSelection == SpecSelection.CONFIG) {
            testWeb(urlBase + MPSpecGET.DEFAULT.urlContent[0][0], 5, MPSpecGET.DEFAULT.urlContent[0][1]);
            testWeb(urlBase + MPSpecGET.CONFIG.urlContent[0][0], 5, MPSpecGET.CONFIG.urlContent[0][1]);
            testWeb(urlBase + MPSpecGET.CONFIG.urlContent[1][0], 5, MPSpecGET.CONFIG.urlContent[1][1]);
        } else if (specSelection == SpecSelection.FAULT_TOLERANCE) {
            testWeb(urlBase + MPSpecGET.FAULT_TOLERANCE.urlContent[0][0], 5, MPSpecGET.FAULT_TOLERANCE.urlContent[0][1]);
        } else if (specSelection == SpecSelection.HEALTH_CHECKS) {
            testWeb(specialUrlBase + MPSpecGET.HEALTH_CHECKS.urlContent[0][0], 5, MPSpecGET.HEALTH_CHECKS.urlContent[0][1]);
        } else if (specSelection == SpecSelection.JWT_AUTH) {
            testWeb(urlBase + MPSpecGET.JWT_AUTH.urlContent[0][0], 5, MPSpecGET.JWT_AUTH.urlContent[0][1]);
        } else if (specSelection == SpecSelection.METRICS) {
            testWeb(urlBase + MPSpecGET.METRICS.urlContent[0][0], 5, MPSpecGET.METRICS.urlContent[0][1]);
            testWeb(specialUrlBase + MPSpecGET.METRICS.urlContent[1][0], 5, MPSpecGET.METRICS.urlContent[1][1]);
        } else if (specSelection == SpecSelection.OPEN_API) {
            if (supportedServer.equalsIgnoreCase("TOMEE")) {
                testWeb(specialUrlBase + MPSpecGET.OPEN_API.urlContent[0][0], 5, MPSpecGET.OPEN_API.urlContent[0][1]);
            } else {
                testWeb(urlBase + MPSpecGET.OPEN_API.urlContent[0][0], 5, MPSpecGET.OPEN_API.urlContent[0][1]);
            }
        } else if (specSelection == SpecSelection.OPEN_TRACING) {
            // No example for this one. Would need Jaeger etc.
            testWeb(urlBase + MPSpecGET.DEFAULT.urlContent[0][0], 5, MPSpecGET.DEFAULT.urlContent[0][1]);
        } else if (specSelection == SpecSelection.REST_CLIENT) {
            testWeb(urlBase + MPSpecGET.REST_CLIENT.urlContent[0][0], 5, MPSpecGET.REST_CLIENT.urlContent[0][1]);
        } else if (specSelection == SpecSelection.GRAPHQL) {
            testWeb(urlBase + MPSpecPOST.GRAPHQL.url, 5, MPSpecPOST.GRAPHQL.expectedContent, MPSpecPOST.GRAPHQL.payload);
        } else {
            throw new IllegalArgumentException(
                    "Unexpected SpecSelection enum value. Have you updated SpecSelection?");
        }
    }

    public static void download(String supportedServer, String artifactId, SpecSelection specSelection,
                                BuildTool buildTool, String location) {
        final String path = "/api/project?supportedServer=" +
                supportedServer + specSelection.queryParam + "&artifactId=" + artifactId + "&buildTool=" + buildTool;
        final io.restassured.response.Response response = given()
                .when()
                .get(path);
        LOGGER.info("from " + path);
        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode(), "Download failed.");
        try (FileOutputStream out = new FileOutputStream(location); InputStream in = response.getBody().asInputStream()) {
            in.transferTo(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void apiAccessibleSanity() {
        assertEquals(Response.Status.OK.getStatusCode(), given().when().get("/api").statusCode(),
                "MicroProfile Starter REST API should be available");
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void thorntailEmpty(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "THORNTAIL_V2", "thorntail",
                SpecSelection.EMPTY, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void thorntailAll(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "THORNTAIL_V2", "thorntail",
                SpecSelection.ALL, new int[]{9990, 8180, 10090}, BuildTool.MAVEN);
    }

    @Test
    public void thorntailAllButJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "THORNTAIL_V2", "thorntail",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void thorntailJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "THORNTAIL_V2", "thorntail",
                SpecSelection.JWT_REST, new int[]{9990, 8180, 10090}, BuildTool.MAVEN);
    }

    @Test
    public void payaraEmpty(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "PAYARA_MICRO", "payara",
                SpecSelection.EMPTY, new int[]{6900}, BuildTool.MAVEN);
    }

    @Test
    public void payaraAll(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "PAYARA_MICRO", "payara",
                SpecSelection.ALL, new int[]{6900, 6901, 8180}, BuildTool.MAVEN);
    }

    @Test
    public void payaraAllGradle(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "PAYARA_MICRO", "payara",
                SpecSelection.ALL, new int[]{6900, 6901, 8180}, BuildTool.GRADLE);
    }

    @Test
    public void payaraAllButJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "PAYARA_MICRO", "payara",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{6900}, BuildTool.MAVEN);
    }

    @Test
    public void payaraJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "PAYARA_MICRO", "payara",
                SpecSelection.JWT_REST, new int[]{6900, 6901, 8180}, BuildTool.MAVEN);
    }

    @Test
    public void libertyEmpty(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "LIBERTY", "liberty",
                SpecSelection.EMPTY, new int[]{8181, 9080, 8543, 9443}, BuildTool.MAVEN);
    }

    @Test
    public void libertyGraphQL(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "LIBERTY", "liberty",
                SpecSelection.GRAPHQL, new int[]{8181, 9080, 8543, 9443}, BuildTool.MAVEN);
    }

    @Test
    public void libertyAll(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "LIBERTY", "liberty",
                SpecSelection.ALL, new int[]{8181, 9080, 8543, 9443, 9444, 8281, 9081}, BuildTool.MAVEN);
    }

    @Test
    // Liberty leaks fds, cannot stop gradle daemon, cannot be killed,
    // results in D:\a\_temp\liberty\service-a\.gradle\6.8.3\fileHashes\fileHashes.lock:
    // The process cannot access the file because it is being used by another process.
    // Hence, disabled on Windows.
    @DisabledOnOs(OS.WINDOWS)
    public void libertyAllGradle(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "LIBERTY", "liberty",
                SpecSelection.ALL, new int[]{8181, 9080, 8543, 9443, 9444, 8281, 9081}, BuildTool.GRADLE);
    }

    @Test
    public void libertyAllButJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "LIBERTY", "liberty",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{8181, 9080, 8543, 9443}, BuildTool.MAVEN);
    }

    @Test
    public void libertyJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "LIBERTY", "liberty",
                SpecSelection.JWT_REST, new int[]{8181, 9080, 8543, 9443, 9444, 8281, 9081}, BuildTool.MAVEN);
    }

    @Test
    public void helidonEmpty(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "HELIDON", "helidon",
                SpecSelection.EMPTY, new int[]{}, BuildTool.MAVEN);
    }

    @Test
    public void helidonAll(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "HELIDON", "helidon",
                SpecSelection.ALL, new int[]{8180}, BuildTool.MAVEN);
    }

    @Test
    public void helidonAllGradle(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "HELIDON", "helidon",
                SpecSelection.ALL, new int[]{8180}, BuildTool.GRADLE);
    }

    @Test
    public void helidonAllButJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "HELIDON", "helidon",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{}, BuildTool.MAVEN);
    }

    @Test
    public void helidonJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "HELIDON", "helidon",
                SpecSelection.JWT_REST, new int[]{8180}, BuildTool.MAVEN);
    }

    @Test
    public void kumuluzeeEmpty(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "KUMULUZEE", "kumuluzee",
                SpecSelection.EMPTY, new int[]{}, BuildTool.MAVEN);
    }

    @Test
    public void kumuluzeeAll(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "KUMULUZEE", "kumuluzee",
                SpecSelection.ALL, new int[]{8180}, BuildTool.MAVEN);
    }

    @Test
    public void kumuluzeeAllButJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "KUMULUZEE", "kumuluzee",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{}, BuildTool.MAVEN);
    }

    @Test
    public void kumuluzeeJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "KUMULUZEE", "kumuluzee",
                SpecSelection.JWT_REST, new int[]{8180}, BuildTool.MAVEN);
    }

    @Test
    public void tomeeEmpty(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "TOMEE", "tomee",
                SpecSelection.EMPTY, new int[]{8009, 8005}, BuildTool.MAVEN);
    }

    @Test
    public void tomeeAll(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "TOMEE", "tomee",
                SpecSelection.ALL, new int[]{8009, 8005, 8180, 8109, 8105}, BuildTool.MAVEN);
    }

    @Test
    public void tomeeAllButJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "TOMEE", "tomee",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{8009, 8005}, BuildTool.MAVEN);
    }

    @Test
    public void tomeeJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "TOMEE", "tomee",
                SpecSelection.JWT_REST, new int[]{8009, 8005, 8180, 8109, 8105}, BuildTool.MAVEN);
    }

    @Test
    public void quarkusEmpty(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "QUARKUS", "quarkus",
                SpecSelection.EMPTY, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void quarkusAllGradle(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "QUARKUS", "quarkus",
                SpecSelection.ALL, new int[]{9990, 8180, 10090}, BuildTool.GRADLE);
    }

    @Test
    public void quarkusAll(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "QUARKUS", "quarkus",
                SpecSelection.ALL, new int[]{9990, 8180, 10090}, BuildTool.MAVEN);
    }

    @Test
    public void quarkusAllButJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "QUARKUS", "quarkus",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void quarkusJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "QUARKUS", "quarkus",
                SpecSelection.JWT_REST, new int[]{9990, 8180, 10090}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyEmpty(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.EMPTY, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyAll(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.ALL, new int[]{9990, 8180, 10090}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyAllButJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyJWTRest(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.JWT_REST, new int[]{9990, 8180, 10090}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyConfig(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.CONFIG, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyFaultTolerance(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.FAULT_TOLERANCE, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyHealthchecks(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.HEALTH_CHECKS, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyJWTAuth(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.JWT_AUTH, new int[]{9990, 8180, 10090}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyMetrics(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.METRICS, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyOpenAPI(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.OPEN_API, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyOpenTracing(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.OPEN_TRACING, new int[]{9990}, BuildTool.MAVEN);
    }

    @Test
    public void wildflyRestClient(TestInfo testInfo) throws IOException, InterruptedException {
        testRuntime(testInfo, "WILDFLY", "wildfly",
                SpecSelection.REST_CLIENT, new int[]{9990, 8180, 10090}, BuildTool.MAVEN);
    }

}
