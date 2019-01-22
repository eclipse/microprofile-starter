/*
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
 * Contributors:
 *   2018-09-29 - Rudy De Busscher
 *      Initially authored in Atbash Jessie
 */
package org.eclipse.microprofile.starter.addon.microprofile.servers;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.artifacts.CDICreator;
import org.eclipse.microprofile.starter.core.artifacts.MavenCreator;
import org.eclipse.microprofile.starter.core.exception.JessieConfigurationException;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.OptionValue;
import org.eclipse.microprofile.starter.spi.AbstractAddon;
import org.eclipse.microprofile.starter.spi.JessieAddon;
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
public class MicroprofileServersAddon extends AbstractAddon {

    @Inject
    private MavenHelper mavenHelper;

    @Inject
    private CDICreator cdiCreator;

    private Model serverPomModel;

    private List<MicroprofileSpec> microprofileSpecs;

    @PostConstruct
    public void init() {
        defaultOptions = new HashMap<>();
        serverPomModel = mavenHelper.readModel("/pom-servers.xml");
    }

    @Override
    public String addonName() {
        return "mp";
    }

    @Override
    public int priority() {
        return 50;
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

        model.addVariable("mp_servername", supportedServer.getName());
    }

    private String invalidMPServerValue(String serverName) {
        return "Unknown value for option 'mp.server' : " + serverName;
    }

    private String invalidSpecValue(List<String> invalidSpecs) {
        return "Unknown value for option 'mp.specs' : " + invalidSpecs.stream()
                .collect(Collectors.joining(", "));
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model) {

        String serverName = options.get("server").getSingleValue();
        String profileName = serverName + "-" + model.getSpecification().getMicroProfileVersion().getCode();
        for (Profile profile : serverPomModel.getProfiles()) {
            if (profile.getId().equals(profileName)) {
                Profile selectedProfile = profile.clone();
                selectedProfile.setId(serverName);
                pomFile.getProfiles().add(selectedProfile);
            }
        }

        SupportedServer supportedServer = SupportedServer.valueFor(serverName);
        if (supportedServer == SupportedServer.KUMULUZEE) {
            // KumuluzEE needs jar packaging
            pomFile.setPackaging("jar");
        }

        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            mavenHelper.addDependency(pomFile, "com.nimbusds", "nimbus-jose-jwt", "5.7", "test");
            mavenHelper.addDependency(pomFile, "org.glassfish.jersey.core", "jersey-client", "2.25.1", "test");

            mavenHelper.addDependency(pomFile, "org.bouncycastle", "bcpkix-jdk15on", "1.53", "test");

        }

    }

    @Override
    public Set<String> alternativesNames(JessieModel model) {
        String serverName = options.get("server").getSingleValue();
        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

        Set<String> alternatives = new HashSet<>();
        alternatives.add(supportedServer.getName());
        return alternatives;
    }

    @Override
    public List<String> getDependentAddons() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getConditionalConfiguration(JessieModel jessieModel, List<JessieAddon> addons) {

        return defaultOptions;
    }

    @Override
    public void createFiles(JessieModel model) {

        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        String serverName = model.getOptions().get("mp.server").getSingleValue();
        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

        if (supportedServer == SupportedServer.LIBERTY) {
            String resourceDirectory = model.getDirectory() + "/src/main/liberty/config";

            directoryCreator.createDirectory(resourceDirectory);

            processTemplateFile(resourceDirectory, "server.xml", alternatives, variables);

            resourceDirectory = model.getDirectory() + "/src/main/liberty/server/resources/security";

            directoryCreator.createDirectory(resourceDirectory);

            processFile(resourceDirectory, "public.jks", alternatives);
        }

        if (supportedServer == SupportedServer.KUMULUZEE) {
            // kumuluzEE js JAR based, so needs beans.xml within META-INF

            cdiCreator.createCDIFilesForJar(model);

            String webDirectory = model.getDirectory() + "/" + MavenCreator.SRC_MAIN_WEBAPP;
            directoryCreator.removeDirectory(webDirectory);
            // TODO But further on the directory is created again (for index.html) so it it then only to get rid of beans.xml in WEB-INF?

            String resourceDirectory = getResourceDirectory(model);
            processTemplateFile(resourceDirectory, "config.yaml", alternatives, variables);

            // Override RestApplication to add specific classes for

            String rootJava = MavenCreator.SRC_MAIN_JAVA + "/" + directoryCreator.createPathForGroupAndArtifact(model.getMaven());
            String viewDirectory = model.getDirectory() + "/" + rootJava;

            String artifactId = variables.get("artifact");

            String javaFile = thymeleafEngine.processFile("RestApplication.java", alternatives, variables);
            fileCreator.writeContents(viewDirectory, artifactId + "RestApplication.java", javaFile);

        }

        if (supportedServer == SupportedServer.WILDFLY_SWARM || supportedServer == SupportedServer.THORNTAIL_V2) {
            // Specific files for Auth-JWT
            String resourceDirectory = getResourceDirectory(model);
            directoryCreator.createDirectory(resourceDirectory);
            processTemplateFile(resourceDirectory, "project-defaults.yml", alternatives, variables);
            processTemplateFile(resourceDirectory, "jwt-roles.properties", alternatives, variables);

            String metaInfDirectory = getResourceDirectory(model) + "/META-INF";

            directoryCreator.createDirectory(metaInfDirectory);
            processTemplateFile(metaInfDirectory, "publicKey.pem", "MP-JWT-SIGNER", alternatives, variables);

            /// web.xml required for WildFly swarm
            String webInfDirectory = model.getDirectory() + "/" + MavenCreator.SRC_MAIN_WEBAPP + "/WEB-INF";
            directoryCreator.createDirectory(webInfDirectory);

            String webXMLContents = thymeleafEngine.processFile("web.xml", alternatives, variables);
            fileCreator.writeContents(webInfDirectory, "web.xml", webXMLContents);

        }

        if (supportedServer == SupportedServer.PAYARA_MICRO) {
            // Specific files for Auth-JWT
            String resourceDirectory = getResourceDirectory(model);
            directoryCreator.createDirectory(resourceDirectory);
            processTemplateFile(resourceDirectory, "publicKey.pem", alternatives, variables);
            processTemplateFile(resourceDirectory, "payara-mp-jwt.properties", alternatives, variables);

            String metaInfDirectory = getResourceDirectory(model) + "/META-INF";

            directoryCreator.createDirectory(metaInfDirectory);
        }

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

        processTemplateFile(model.getDirectory(), "readme.md", alternatives, variables);
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
