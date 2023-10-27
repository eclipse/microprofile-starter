/*
 * Copyright (c) 2019-2022 Contributors to the Eclipse Foundation
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
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.StandaloneMPSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.VersionStandaloneMatrix;
import org.eclipse.microprofile.starter.core.artifacts.MavenCreator;
import org.eclipse.microprofile.starter.core.exception.JessieConfigurationException;
import org.eclipse.microprofile.starter.core.exception.JessieUnexpectedException;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;
import org.eclipse.microprofile.starter.core.model.OptionValue;
import org.eclipse.microprofile.starter.spi.MavenHelper;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private List<StandaloneMPSpec> microprofileStandaloneSpecs;

    public static final String VERTX_JWT_VERSION = "3.9.5";

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
        handleStandaloneSpecOptions(model);
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

    private void handleStandaloneSpecOptions(JessieModel model) {
        OptionValue specs = options.get("standaloneSpecs");

        microprofileStandaloneSpecs = new ArrayList<>();
        List<String> invalidSpecs = new ArrayList<>();
        for (String spec : specs.getValues()) {
            StandaloneMPSpec standaloneMPSpec = StandaloneMPSpec.valueFor(spec);
            if (standaloneMPSpec == null) {
                invalidSpecs.add(spec);
            } else {
                model.addVariable("mp_" + standaloneMPSpec.getCode(), "true");
                // For OpenLiberty feature we need the version
                Map<StandaloneMPSpec, String> specData = VersionStandaloneMatrix.getInstance().
                    getSpecData(model.getSpecification().getMicroProfileVersion());
                model.addVariable("mp_" + standaloneMPSpec.getCode() + "_version", majorMinorVersion(specData.get(standaloneMPSpec)));

                microprofileStandaloneSpecs.add(standaloneMPSpec);
            }
        }

        if (!invalidSpecs.isEmpty()) {
            throw new JessieConfigurationException(invalidStandaloneSpecValue(invalidSpecs));
        }

        model.addParameter(MICROPROFILESPECS, microprofileSpecs);
    }

    private String majorMinorVersion(String version) {
        String[] parts = version.split("\\.");
        int neededParts = Math.min(parts.length, 2);  //We only need Major minor or just the version.
        return Arrays.stream(parts).limit(neededParts).collect(Collectors.joining("."));
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

    private String invalidStandaloneSpecValue(List<String> invalidSpecs) {
        return "Unknown value for option 'mp.standaloneSpecs' : " + String.join(", ", invalidSpecs);
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
        // From MP 3.2 on with Helidon, one of the Maven deps changed
        // So I created a new profile with correct naming.  The solution with the MP version in the profile name is not really useful in this case
        // since it will be for several MP releases.
        if ("helidon".equals(serverName) && model.getSpecification().getMicroProfileVersion().ordinal() <= MicroProfileVersion.MP32.ordinal()) {
            serverName = "helidon2";
        }
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
            mavenHelper.addDependency(pomFile, "io.vertx", "vertx-auth-jwt", VERTX_JWT_VERSION);
        }

        if (microprofileStandaloneSpecs.contains(StandaloneMPSpec.GRAPHQL) && mainProject) {

            Map<StandaloneMPSpec, String> specData = VersionStandaloneMatrix.getInstance()
                    .getSpecData(model.getSpecification().getMicroProfileVersion());

            mavenHelper.addDependency(pomFile,
                    StandaloneMPSpec.GRAPHQL.getGroupId(),
                    StandaloneMPSpec.GRAPHQL.getArtifactId(),
                    specData.get(StandaloneMPSpec.GRAPHQL),
                    "provided");
        }

        if (model.hasMainAndSecondaryProject()) {
            if (mainProject) {
                pomFile.setArtifactId(model.getMaven().getArtifactId() + "-" + JessieModel.MAIN_INDICATOR);
            } else {
                pomFile.setArtifactId(model.getMaven().getArtifactId() + "-" + JessieModel.SECONDARY_INDICATOR);
            }
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
        Set<String> bAlternatives = new HashSet<>(alternatives);
        bAlternatives.add(JessieModel.SECONDARY_INDICATOR);
        Map<String, String> variables = model.getVariables();

        String serverName = model.getOptions().get("mp.server").getSingleValue();
        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

        String artifactId = model.getMaven().getArtifactId();
        variables.put("jar_file", defineJarFileName(supportedServer, artifactId));
        variables.put("jar_file_no_suffix", variables.get("jar_file").split("\\.jar")[0]);
        variables.put("jar_parameters", defineJarParameters(supportedServer));
        variables.put("port_service_a", supportedServer.getPortServiceA());
        variables.put("port_service_b", supportedServer.getPortServiceB());
        variables.put("artifact_id", artifactId);
        variables.put("wf_main_url", "http://localhost:" + SupportedServer.WILDFLY.getPortServiceA());
        variables.put("wf_manage_url", "http://localhost:9990");
        variables.put("mp_servername", serverName);

        String rootJava = getJavaApplicationRootPackage(model);

        if (microprofileSpecs.contains(MicroprofileSpec.HEALTH_CHECKS)) {
            String healthDirectory = model.getDirectory(true) + "/" + rootJava + "/health";
            directoryCreator.createDirectory(healthDirectory);

            if (alternatives.contains(MicroProfileVersion.Constants.MP41_ALTERNATIVE)
                    || alternatives.contains(MicroProfileVersion.Constants.MP3X_ALTERNATIVE) || 
                    alternatives.contains(MicroProfileVersion.Constants.MP5X_ALTERNATIVE)) {
                templateEngine.processTemplateFile(healthDirectory, "ServiceLiveHealthCheck.java", alternatives, variables);
                templateEngine.processTemplateFile(healthDirectory, "ServiceReadyHealthCheck.java", alternatives, variables);
                if (alternatives.contains(MicroProfileVersion.Constants.MP41_ALTERNATIVE) || 
                    alternatives.contains(MicroProfileVersion.Constants.MP5X_ALTERNATIVE)) {
                    templateEngine.processTemplateFile(healthDirectory, "ServiceStartupHealthCheck.java", alternatives, variables);
                }
            } else {
                templateEngine.processTemplateFile(healthDirectory, "ServiceHealthCheck.java", alternatives, variables);
            }
        }

        if (microprofileSpecs.contains(MicroprofileSpec.CONFIG)) {
            String configDirectory = model.getDirectory(true) + "/" + rootJava + "/config";
            directoryCreator.createDirectory(configDirectory);

            templateEngine.processTemplateFile(configDirectory, "ConfigTestController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.METRICS)) {
            String metricDirectory = model.getDirectory(true) + "/" + rootJava + "/metric";
            directoryCreator.createDirectory(metricDirectory);

            templateEngine.processTemplateFile(metricDirectory, "MetricController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.OPEN_API)) {
            String openapiDirectory = model.getDirectory(true) + "/" + rootJava + "/openapi";
            directoryCreator.createDirectory(openapiDirectory);

            templateEngine.processTemplateFile(openapiDirectory, "BookingController.java", alternatives, variables);
            templateEngine.processTemplateFile(openapiDirectory, "Booking.java", alternatives, variables);
            templateEngine.processTemplateFile(openapiDirectory, "Destination.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.FAULT_TOLERANCE)) {
            String faultDirectory = model.getDirectory(true) + "/" + rootJava + "/resilient";
            directoryCreator.createDirectory(faultDirectory);

            templateEngine.processTemplateFile(faultDirectory, "ResilienceController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.REST_CLIENT)) {
            String clientMainDirectory = model.getDirectory(true) + "/" + rootJava + "/client";
            directoryCreator.createDirectory(clientMainDirectory);

            String clientSecondaryDirectory = model.getDirectory(false) + "/" + rootJava + "/client";
            directoryCreator.createDirectory(clientSecondaryDirectory);

            templateEngine.processTemplateFile(clientSecondaryDirectory, "ServiceController.java", bAlternatives, variables);
            templateEngine.processTemplateFile(clientMainDirectory, "Service.java", alternatives, variables);
            templateEngine.processTemplateFile(clientMainDirectory, "ClientController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            if (model.hasMainAndSecondaryProject()) {
                String bSecureDirectory = model.getDirectory(false) + "/" + rootJava + "/secure";
                directoryCreator.createDirectory(bSecureDirectory);
                templateEngine.processTemplateFile(bSecureDirectory, "ProtectedController.java", bAlternatives, variables);
            }

            String aSecureDirectory = model.getDirectory(true) + "/" + rootJava + "/secure";

            templateEngine.processTemplateFile(aSecureDirectory, "TestSecureController.java", alternatives, variables);
            templateEngine.processTemplateFile(aSecureDirectory, "MPJWTToken.java", alternatives, variables);

            String resourceDirectory = getResourceDirectory(model, true);

            templateEngine.processTemplateFile(resourceDirectory, "privateKey.pem", alternatives, variables);
        }

        if (microprofileStandaloneSpecs.contains(StandaloneMPSpec.GRAPHQL)) {
            String graphqlDirectory = model.getDirectory(true) + "/" + rootJava + "/graphql";
            String graphqlModelDirectory = model.getDirectory(true) + "/" + rootJava + "/graphql/model";
            String graphqlDbDirectory = model.getDirectory(true) + "/" + rootJava + "/graphql/db";

            directoryCreator.createDirectory(graphqlDirectory);
            directoryCreator.createDirectory(graphqlModelDirectory);
            directoryCreator.createDirectory(graphqlDbDirectory);

            templateEngine.processTemplateFile(graphqlDirectory, "HeroFinder.java", alternatives, variables);
            templateEngine.processTemplateFile(graphqlModelDirectory, "SuperHero.java", alternatives, variables);
            templateEngine.processTemplateFile(graphqlModelDirectory, "Team.java", alternatives, variables);
            templateEngine.processTemplateFile(graphqlModelDirectory, "UnknownCharacterException.java", alternatives, variables);
            templateEngine.processTemplateFile(graphqlDbDirectory, "HeroDatabase.java", alternatives, variables);
            templateEngine.processTemplateFile(graphqlDbDirectory, "DuplicateSuperHeroException.java", alternatives, variables);
            templateEngine.processTemplateFile(graphqlDbDirectory, "UnknownHeroException.java", alternatives, variables);
            templateEngine.processTemplateFile(graphqlDbDirectory, "UnknownTeamException.java", alternatives, variables);
        }

        // With KumuluzEE, it properties are integrated within config.yaml
        // With Quarkus, its properties use application.properties
        if (supportedServer != SupportedServer.KUMULUZEE &&
                supportedServer != SupportedServer.QUARKUS &&
                supportedServer != SupportedServer.WILDFLY) {
            String metaInfDirectory = getResourceDirectory(model, true) + "/META-INF";
            directoryCreator.createDirectory(metaInfDirectory);
            templateEngine.processTemplateFile(metaInfDirectory, "microprofile-config.properties", alternatives, variables);
        }

        // Helidon should have it in src/main/resources/WEB
        // Quarkus should have it in src/main/resources/META-INF/resources
        if (supportedServer != SupportedServer.HELIDON & supportedServer != SupportedServer.QUARKUS) {
            // Demo index file to all endpoints
            String webDirectory = model.getDirectory(true) + "/" + MavenCreator.SRC_MAIN_WEBAPP;
            directoryCreator.createDirectory(webDirectory);
            templateEngine.processTemplateFile(webDirectory, "index.html", alternatives, variables);
        }

        templateEngine.processTemplateFile(model.getDirectory(true), "readme.md", alternatives, variables);
        if (model.hasMainAndSecondaryProject()) {
            templateEngine.processTemplateFile(model.getTopLevelDirectory(), "readme.md.top", "readme.md", alternatives, variables);
            templateEngine.processTemplateFile(model.getDirectory(false), "readme.md.secondary", "readme.md", alternatives, variables);
        }
    }

    private String defineJarFileName(SupportedServer supportedServer, String artifactId) {
        return String.format(supportedServer.getJarFileName(), artifactId);
    }

    private String defineJarParameters(SupportedServer supportedServer) {
        return supportedServer.getJarParameters();
    }

}
