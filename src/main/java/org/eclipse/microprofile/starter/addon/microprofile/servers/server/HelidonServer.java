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
package org.eclipse.microprofile.starter.addon.microprofile.servers.server;

import org.apache.maven.model.Model;
import org.eclipse.microprofile.starter.addon.microprofile.servers.AbstractMicroprofileAddon;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.artifacts.CDICreator;
import org.eclipse.microprofile.starter.core.artifacts.MavenCreator;
import org.eclipse.microprofile.starter.core.model.JessieModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

        // kumuluzEE is JAR based, so needs beans.xml within META-INF
        cdiCreator.createCDIFilesForJar(model, true);

        // Remove WEB-INF containing the beans.xml
        String webDirectory = model.getDirectory(true) + "/" + MavenCreator.SRC_MAIN_WEBAPP + "/WEB-INF";
        directoryCreator.removeDirectory(webDirectory);

        String rootJava = MavenCreator.SRC_MAIN_JAVA + "/" + directoryCreator.createPathForGroupAndArtifact(model.getMaven());
        String viewDirectory = model.getDirectory(true) + "/" + rootJava;

        String resourcesDirectory = model.getDirectory(true)  + "/" + MavenCreator.SRC_MAIN_RESOURCES;
        directoryCreator.createDirectory(resourcesDirectory);

        processTemplateFile(resourcesDirectory, "application.yaml", alternatives, variables);
        processTemplateFile(resourcesDirectory, "logging.properties", alternatives, variables);
        processTemplateFile(resourcesDirectory, "publicKey.pem", alternatives, variables);

        String artifactId = variables.get("artifact");

        String restAppFile = thymeleafEngine.processFile("RestApplication.java", alternatives, variables);
        fileCreator.writeContents(viewDirectory, artifactId + "RestApplication.java", restAppFile);


    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model, boolean mainProject) {
        // Helidon needs jar packaging
        pomFile.setPackaging("jar");

        String packageName = model.getMaven().getGroupId() + '.' + model.getMaven().getArtifactId();
        pomFile.addProperty("package", packageName);


    }
}
