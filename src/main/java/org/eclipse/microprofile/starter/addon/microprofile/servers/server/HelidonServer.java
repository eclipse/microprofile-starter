/*
 * Copyright (c) 2019-2021 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.addon.microprofile.servers.server;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.eclipse.microprofile.starter.addon.microprofile.servers.AbstractMicroprofileAddon;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.artifacts.BuildToolCreator;
import org.eclipse.microprofile.starter.core.artifacts.CDICreator;
import org.eclipse.microprofile.starter.core.artifacts.MavenCreator;
import org.eclipse.microprofile.starter.core.model.JessieModel;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class HelidonServer extends AbstractMicroprofileAddon {

    @Inject
    private CDICreator cdiCreator;

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public String addonName() {
        return SupportedServer.HELIDON.getCode();
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        // Helidon's html files reside in resources/WEB; we can delete webapp dir.
        String webHtmlDir = model.getDirectory(true) + "/" + MavenCreator.SRC_MAIN_RESOURCES + "/" + "WEB";
        directoryCreator.createDirectory(webHtmlDir);
        templateEngine.processTemplateFile(webHtmlDir, "index.html", alternatives, variables);
        directoryCreator.removeDirectory(model.getDirectory(true) + "/" + MavenCreator.SRC_MAIN_WEBAPP);
        if (model.hasMainAndSecondaryProject()) {
            directoryCreator.removeDirectory(model.getDirectory(false) + "/" + MavenCreator.SRC_MAIN_WEBAPP);
        }

        // Helidon is JAR based, so needs beans.xml within META-INF
        cdiCreator.createCDIFilesForJar(model, true);
        if (model.hasMainAndSecondaryProject()) {
            cdiCreator.createCDIFilesForJar(model, false);
        }

        // Remove WEB-INF containing the beans.xml
        String webDirectory = model.getDirectory(true) + "/" + MavenCreator.SRC_MAIN_WEBAPP + "/WEB-INF";
        directoryCreator.removeDirectory(webDirectory);

        String rootJava = BuildToolCreator.SRC_MAIN_JAVA + "/" + directoryCreator.createPathForGroupAndArtifact(model.getMaven());

        String resourcesDirectory = model.getDirectory(true) + "/" + MavenCreator.SRC_MAIN_RESOURCES;
        directoryCreator.createDirectory(resourcesDirectory);

        templateEngine.processTemplateFile(resourcesDirectory, "logging.properties", alternatives, variables);
        templateEngine.processTemplateFile(resourcesDirectory, "privateKey.pem", alternatives, variables);

        List<MicroprofileSpec> microprofileSpecs = model.getParameter(JessieModel.Parameter.MICROPROFILESPECS);

        if (model.hasMainAndSecondaryProject()) {
            String viewDirectory = model.getDirectory(false) + "/" + rootJava;
            directoryCreator.createDirectory(viewDirectory);
            Set<String> tempAlternative = new HashSet<>(alternatives);
            tempAlternative.add(JessieModel.SECONDARY_INDICATOR);
            templateEngine.processTemplateFile(viewDirectory,
                    "RestApplication.java", variables.get("application") + "RestApplication.java", tempAlternative, variables);

            String bResourcesDir = model.getDirectory(false) + "/" + MavenCreator.SRC_MAIN_RESOURCES;
            String bResourcesMETAINFDir = bResourcesDir + "/META-INF";
            directoryCreator.createDirectory(bResourcesMETAINFDir);

            if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
                templateEngine.processTemplateFile(bResourcesDir, "publicKey.pem", alternatives, variables);
            }

            templateEngine.processTemplateFile(bResourcesMETAINFDir, "microprofile-config.properties", tempAlternative, variables);
        }
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model, boolean mainProject) {
        // Helidon needs jar packaging
        pomFile.setPackaging("jar");

        String packageName = model.getMaven().getGroupId() + '.' + model.getMaven().getArtifactId();
        pomFile.addProperty("package", packageName);

        String helidonVersion = "";
        String mpVersion = "";
        boolean useApplicationsParentPom = true;
        switch (model.getSpecification().getMicroProfileVersion()) {
            case NONE:
                break;
            case MP61:
                break;
            case MP60:
                break;
            case MP50:
                helidonVersion = "3.1";
                break;
            case MP41:
                helidonVersion = "2.4.2";
                mpVersion = "4.1";
                break;
            default:
                break;
        }
        pomFile.addProperty("helidonVersion", helidonVersion);
        pomFile.addProperty("mpVersion", mpVersion);
        if (useApplicationsParentPom) {
            setApplicationsParentPom(pomFile, helidonVersion);
        }
    }

    private static void setApplicationsParentPom(Model pomFile, String helidonVersion) {
        Parent parent = new Parent();
        parent.setGroupId("io.helidon.applications");
        parent.setArtifactId("helidon-mp");
        parent.setVersion(helidonVersion);
        parent.setRelativePath("");
        pomFile.setParent(parent);
    }

    @Override
    public Map<String, String> defineAdditionalVariables(JessieModel model, boolean mainProject) {
        // For customization of the build.gradle file
        Map<String, String> result = new HashMap<>();

        if (mainProject) {
            result.put("port_service", SupportedServer.HELIDON.getPortServiceA());
        } else {
            result.put("port_service", SupportedServer.HELIDON.getPortServiceB());
        }

        return result;
    }

}
