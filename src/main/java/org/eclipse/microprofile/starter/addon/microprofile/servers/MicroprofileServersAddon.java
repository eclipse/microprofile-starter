/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.microprofile.starter.addon.microprofile.servers;

import org.apache.maven.model.Activation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.artifacts.CDICreator;
import org.eclipse.microprofile.starter.core.artifacts.MavenCreator;
import org.eclipse.microprofile.starter.core.exception.JessieConfigurationException;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.OptionValue;
import org.eclipse.microprofile.starter.spi.MavenHelper;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@ApplicationScoped
public class MicroprofileServersAddon extends AbstractMicroprofileAddon {

    @Inject
    private MavenHelper mavenHelper;

    @Inject
    private CDICreator cdiCreator;

    private Model serverPomModel;

    private List<MicroprofileSpec> microprofileSpecs;

    @PostConstruct
    public void init() {
        super.init();
        serverPomModel = mavenHelper.readModel("/pom-servers.xml");
    }

    @Override
    public String addonName() {
        return "mp";
    }

    protected void validateModel(JessieModel model) {
        checkServerValue(model);

        handleSpecOptions(model);
        // TODO Should we forsee a Map within JessieModel to stores these things?
        // So that it doesn't need to be be redefined?
    }

    private void handleSpecOptions(JessieModel model) {
        OptionValue specs = options.get("specs");

        microprofileSpecs = new ArrayList<>();
        List<String> invalidSpecs = new ArrayList<>();
        for (String spec : specs.getValues()) {
            MicroprofileSpec microprofileSpec = MicroprofileSpec.valueFor(spec);
            if (microprofileSpec == null) {
                invalidSpecs.add(spec);
            } else {
                model.addVariable("mp_" + microprofileSpec.getCode(), "true");
                microprofileSpecs.add(microprofileSpec);
            }
        }

        if (!invalidSpecs.isEmpty()) {
            throw new JessieConfigurationException(invalidSpecValue(invalidSpecs));
        }
    }

    private void checkServerValue(JessieModel model) {
        String serverName = options.get("server").getSingleValue();
        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

        if (supportedServer == null) {
            throw new JessieConfigurationException(invalidMPServerValue(serverName));
        }

        model.addVariable("mp_servername", supportedServer.getCode());
    }

    private String invalidMPServerValue(String serverName) {
        return "Unknown value for option 'mp.server' : " + serverName;
    }

    private String invalidSpecValue(List<String> invalidSpecs) {
        return "Unknown value for option 'mp.specs' : " + invalidSpecs.stream()
                .collect(Collectors.joining(", "));
    }

    @Override
    public List<String> getDependentAddons(JessieModel model) {
        List<String> result = new ArrayList<>();
        result.add(model.getOptions().get("mp.server").getSingleValue());  // Here we have the original option, not translated.

        return result;
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model) {

        String serverName = options.get("server").getSingleValue();
        String profileName = serverName + "-" + model.getSpecification().getMicroProfileVersion().getCode();

        Profile profile = findProfile(profileName);
        if (profile == null) {
            profile = findProfile(serverName);
        }
        if (profile == null) {
            // FIXME Throw exception
        }

        Profile selectedProfile = profile.clone();
        selectedProfile.setId(serverName);
        Activation activeByDefault = new Activation();
        activeByDefault.setActiveByDefault(true);
        selectedProfile.setActivation(activeByDefault);
        pomFile.getProfiles().add(selectedProfile);

        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            mavenHelper.addDependency(pomFile, "com.nimbusds", "nimbus-jose-jwt", "5.7", "test");
            if (supportedServer != SupportedServer.KUMULUZEE && supportedServer != SupportedServer.HELIDON) {
                TestDependenciesRestClient.JessieMavenWithVersion data = TestDependenciesRestClient.getInstance()
                        .getServerSpecificData(supportedServer);
                mavenHelper.addDependency(pomFile, data.getGroupId(), data.getArtifactId(), data.getVersion(), "test");
            }
            mavenHelper.addDependency(pomFile, "org.bouncycastle", "bcpkix-jdk15on", "1.53", "test");
        }

    }

    private Profile findProfile(String profileName) {
        Profile result = null;

        for (Profile profile : serverPomModel.getProfiles()) {
            if (profile.getId().equals(profileName)) {
                result = profile;
            }
        }
        return result;
    }

    @Override
    public Set<String> alternativesNames(JessieModel model) {
        String serverName = options.get("server").getSingleValue();
        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

        Set<String> alternatives = new HashSet<>();
        alternatives.add(supportedServer.getCode());
        return alternatives;
    }

    @Override
    public void createFiles(JessieModel model) {

        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        String serverName = model.getOptions().get("mp.server").getSingleValue();
        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

                String rootJava = getJavaApplicationRootPackage(model);

        if (microprofileSpecs.contains(MicroprofileSpec.HEALTH_CHECKS)) {
            String healthDirectory = model.getDirectory() + "/" + rootJava + "/health";
            directoryCreator.createDirectory(healthDirectory);

            processTemplateFile(healthDirectory, "ServiceHealthCheck.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.CONFIG)) {
            String configDirectory = model.getDirectory() + "/" + rootJava + "/config";
            directoryCreator.createDirectory(configDirectory);

            processTemplateFile(configDirectory, "ConfigTestController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.HEALTH_METRICS)) {
            String metricDirectory = model.getDirectory() + "/" + rootJava + "/metric";
            directoryCreator.createDirectory(metricDirectory);

            processTemplateFile(metricDirectory, "MetricController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.FAULT_TOLERANCE)) {
            String faultDirectory = model.getDirectory() + "/" + rootJava + "/resilient";
            directoryCreator.createDirectory(faultDirectory);

            processTemplateFile(faultDirectory, "ResilienceController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            String secureDirectory = model.getDirectory() + "/" + rootJava + "/secure";
            directoryCreator.createDirectory(secureDirectory);

            processTemplateFile(secureDirectory, "ProtectedController.java", alternatives, variables);
        }

        // TODO : Verify : This is for all specs?
        if (supportedServer != SupportedServer.KUMULUZEE) {
            // With kumuluzEE, it properties are integrated within config.yaml
            String metaInfDirectory = getResourceDirectory(model) + "/META-INF";

            directoryCreator.createDirectory(metaInfDirectory);
            processTemplateFile(metaInfDirectory, "microprofile-config.properties", alternatives, variables);
        }

        // Demo index file to all endpoints
        String webDirectory = model.getDirectory() + "/" + MavenCreator.SRC_MAIN_WEBAPP;
        directoryCreator.createDirectory(webDirectory);
        processTemplateFile(webDirectory, "index.html", alternatives, variables);

        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            addTestClient(model, alternatives, variables);
        }

        variables.put("jar_file", defineJarFileName(supportedServer, model.getMaven().getArtifactId()));
        variables.put("test_url", defineTestURL(supportedServer, model.getMaven().getArtifactId()));

        processTemplateFile(model.getDirectory(), "readme.md", alternatives, variables);
    }

    private String defineJarFileName(SupportedServer supportedServer, String artifactId) {
        String result;
        switch (supportedServer) {

            case WILDFLY_SWARM:
                result = String.format("%s-swarm.jar", artifactId);
                break;
            case THORNTAIL_V2:
                result = String.format("%s-thorntail.jar", artifactId);
                break;
            case LIBERTY:
                result = String.format("%s.jar", artifactId);
                break;
            case KUMULUZEE:
                result = String.format("%s.jar", artifactId);
                break;
            case PAYARA_MICRO:
                result = String.format("%s-microbundle.jar", artifactId);
                break;
            case TOMEE:
                result = String.format("%s-exec.jar", artifactId);
                break;
            case HELIDON:
                result = String.format("%s.jar", artifactId);
                break;
            default:
                throw new IllegalArgumentException(String.format("Value of supportedServer '%s' is not supported", supportedServer.getCode()));
        }
        return result;
    }

    private String defineTestURL(SupportedServer supportedServer, String artifactId) {
        String result;
        switch (supportedServer) {

            case WILDFLY_SWARM:
                result = "http://localhost:8080/index.html";
                break;
            case THORNTAIL_V2:
                result = "http://localhost:8080/index.html";
                break;
            case LIBERTY:
                result = String.format("http://localhost:8181/%s/index.html", artifactId);
                break;
            case KUMULUZEE:
                result = "http://localhost:8080/index.html";
                break;
            case PAYARA_MICRO:
                result = "http://localhost:8080/index.html";
                break;
            case TOMEE:
                result = "http://localhost:8080/index.html";
                break;
            case HELIDON:
                result = "http://localhost:8080/index.html";
                break;
            default:
                throw new IllegalArgumentException(String.format("Value of supportedServer '%s' is not supported", supportedServer.getCode()));
        }
        return result;
    }

    private void addTestClient(JessieModel model, Set<String> alternatives, Map<String, String> variables) {
        alternatives.add("test-client");

        String javaDirectory = model.getDirectory() + "/" + getJavaTestApplicationRootPackage(model);

        directoryCreator.createDirectory(javaDirectory);

        processTemplateFile(javaDirectory, "JWTClient.java", alternatives, variables);
        processTemplateFile(javaDirectory, "MPJWTToken.java", alternatives, variables);

        String resourceDirectory = getTestResourcesDirectory(model);
        directoryCreator.createDirectory(resourceDirectory);

        processTemplateFile(resourceDirectory, "privateKey.pem", alternatives, variables);

    }

}