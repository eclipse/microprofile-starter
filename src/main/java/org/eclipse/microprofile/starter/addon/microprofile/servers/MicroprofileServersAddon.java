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
import org.eclipse.microprofile.starter.core.artifacts.MavenCreator;
import org.eclipse.microprofile.starter.core.exception.JessieConfigurationException;
import org.eclipse.microprofile.starter.core.exception.JessieUnexpectedException;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.OptionValue;
import org.eclipse.microprofile.starter.spi.MavenHelper;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

import static org.eclipse.microprofile.starter.core.model.JessieModel.Parameter.MICROPROFILESPECS;

/**
 *
 */
@ApplicationScoped
public class MicroprofileServersAddon extends AbstractMicroprofileAddon {

    @Inject
    private MavenHelper mavenHelper;

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

        model.addParameter(MICROPROFILESPECS, microprofileSpecs);
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
        return "Unknown value for option 'mp.specs' : " + String.join(", ", invalidSpecs);
    }

    @Override
    public List<String> getDependentAddons(JessieModel model) {
        List<String> result = new ArrayList<>();
        result.add(model.getOptions().get("mp.server").getSingleValue());  // Here we have the original option, not translated.

        return result;
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model, boolean mainProject) {

        String serverName = options.get("server").getSingleValue();
        String profileName = serverName + "-" + model.getSpecification().getMicroProfileVersion().getCode();

        Profile profile = findProfile(profileName);
        if (profile == null) {
            profile = findProfile(serverName);
        }

        if (profile == null) {
            throw new JessieUnexpectedException("Profile not found " + profileName);
        }

        Profile selectedProfile = profile.clone();
        selectedProfile.setId(serverName);
        Activation activeByDefault = new Activation();
        activeByDefault.setActiveByDefault(true);
        selectedProfile.setActivation(activeByDefault);
        pomFile.getProfiles().add(selectedProfile);

        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH) && mainProject) {
            mavenHelper.addDependency(pomFile, "com.nimbusds", "nimbus-jose-jwt", "5.7");
            mavenHelper.addDependency(pomFile, "org.bouncycastle", "bcpkix-jdk15on", "1.53");
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

        String artifactId = model.getMaven().getArtifactId();
        variables.put("jar_file", defineJarFileName(supportedServer, artifactId));
        variables.put("jar_parameters", defineJarParameters(supportedServer));
        variables.put("test_url", defineTestURL(supportedServer, artifactId));
        variables.put("secondary_url", defineSecondaryURL(supportedServer, artifactId));
        variables.put("artifact_id", artifactId);

        String rootJava = getJavaApplicationRootPackage(model);

        if (microprofileSpecs.contains(MicroprofileSpec.HEALTH_CHECKS)) {
            String healthDirectory = model.getDirectory(true) + "/" + rootJava + "/health";
            directoryCreator.createDirectory(healthDirectory);

            processTemplateFile(healthDirectory, "ServiceHealthCheck.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.CONFIG)) {
            String configDirectory = model.getDirectory(true) + "/" + rootJava + "/config";
            directoryCreator.createDirectory(configDirectory);

            processTemplateFile(configDirectory, "ConfigTestController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.METRICS)) {
            String metricDirectory = model.getDirectory(true) + "/" + rootJava + "/metric";
            directoryCreator.createDirectory(metricDirectory);

            processTemplateFile(metricDirectory, "MetricController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.FAULT_TOLERANCE)) {
            String faultDirectory = model.getDirectory(true) + "/" + rootJava + "/resilient";
            directoryCreator.createDirectory(faultDirectory);

            processTemplateFile(faultDirectory, "ResilienceController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            String secureDirectory;

            if (model.hasMainAndSecondaryProject()) {
                secureDirectory = model.getDirectory(false) + "/" + rootJava + "/secure";
                directoryCreator.createDirectory(secureDirectory);

                processTemplateFile(secureDirectory, "ProtectedController.java", alternatives, variables);

            }
        }


        if (microprofileSpecs.contains(MicroprofileSpec.REST_CLIENT)) {
            String clientMainDirectory = model.getDirectory(true) + "/" + rootJava + "/client";
            directoryCreator.createDirectory(clientMainDirectory);

            String clientSecondaryDirectory = model.getDirectory(false) + "/" + rootJava + "/client";
            directoryCreator.createDirectory(clientSecondaryDirectory);

            processTemplateFile(clientSecondaryDirectory, "ServiceController.java", alternatives, variables);
            processTemplateFile(clientMainDirectory, "Service.java", alternatives, variables);
            processTemplateFile(clientMainDirectory, "ClientController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            String javaDirectory = model.getDirectory(true) + "/" + rootJava + "/secure";

            processTemplateFile(javaDirectory, "TestSecureController.java", alternatives, variables);
            processTemplateFile(javaDirectory, "MPJWTToken.java", alternatives, variables);

            String resourceDirectory = getResourceDirectory(model, true);

            processTemplateFile(resourceDirectory, "privateKey.pem", alternatives, variables);

        }

        // TODO : Verify : This is for all specs?
        if (supportedServer != SupportedServer.KUMULUZEE) {
            // With kumuluzEE, it properties are integrated within config.yaml
            String metaInfDirectory = getResourceDirectory(model, true) + "/META-INF";

            directoryCreator.createDirectory(metaInfDirectory);
            processTemplateFile(metaInfDirectory, "microprofile-config.properties", alternatives, variables);
        }

        // Demo index file to all endpoints
        String webDirectory = model.getDirectory(true) + "/" + MavenCreator.SRC_MAIN_WEBAPP;
        directoryCreator.createDirectory(webDirectory);
        processTemplateFile(webDirectory, "index.html", alternatives, variables);

        processTemplateFile(model.getDirectory(true), "readme.md", alternatives, variables);
        if (model.hasMainAndSecondaryProject()) {
            processTemplateFile(model.getTopLevelDirectory(), "readme.md.top", "readme.md", alternatives, variables);
            processTemplateFile(model.getDirectory(false), "readme.md.secondary", "readme.md", alternatives, variables);
        }
    }

    private String defineJarFileName(SupportedServer supportedServer, String artifactId) {
        return String.format(supportedServer.getJarFileName(), artifactId);
    }

    private String defineJarParameters(SupportedServer supportedServer) {
        return supportedServer.getJarParameters();
    }

    private String defineTestURL(SupportedServer supportedServer, String artifactId) {
        return String.format(supportedServer.getTestURL(), artifactId);
    }

    private String defineSecondaryURL(SupportedServer supportedServer, String artifactId) {
        return String.format(supportedServer.getSecondaryURL(), artifactId);
    }

}