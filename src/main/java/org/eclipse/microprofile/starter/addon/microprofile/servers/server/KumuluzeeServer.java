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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class KumuluzeeServer extends AbstractMicroprofileAddon {

    @Inject
    private CDICreator cdiCreator;

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public String addonName() {
        return SupportedServer.KUMULUZEE.getCode();
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();
        createFiles(model, alternatives, variables, true);
        if (model.hasMainAndSecondaryProject()) {
            Set<String> tempAlternatives = new HashSet<>(alternatives);
            tempAlternatives.add(JessieModel.SECONDARY_INDICATOR);
            createFiles(model, tempAlternatives, variables, false);

        }
    }

    private void createFiles(JessieModel model, Set<String> alternatives, Map<String, String> variables, boolean mainProject) {
        // kumuluzEE is JAR based, so needs beans.xml within META-INF
        cdiCreator.createCDIFilesForJar(model, mainProject);

        // Remove WEB-INF containing the beans.xml
        String webDirectory = model.getDirectory(mainProject) + "/" + MavenCreator.SRC_MAIN_WEBAPP + "/WEB-INF";
        directoryCreator.removeDirectory(webDirectory);

        String resourceDirectory = getResourceDirectory(model, mainProject);
        processTemplateFile(resourceDirectory, "config.yaml", alternatives, variables);
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model, boolean mainProject) {
        // KumuluzEE needs jar packaging
        pomFile.setPackaging("jar");


        String kumuluzVersion = "";
        String kumuluzeeConfigVersion = "";
        String artifactId = "";
        switch (model.getSpecification().getMicroProfileVersion()) {

            case NONE:
                break;
            case MP30:
                kumuluzVersion = "3.6.0";
                kumuluzeeConfigVersion = "1.3.0";
                artifactId = "kumuluzee-microProfile-3.0";
                break;
            case MP22:
                kumuluzVersion = "3.5.0";
                kumuluzeeConfigVersion = "1.3.0";
                artifactId = "kumuluzee-microProfile-2.2";
                break;
            case MP21:
                kumuluzVersion = "3.2.0";
                kumuluzeeConfigVersion = "1.3.0";
                artifactId = "kumuluzee-microProfile-2.1";
                break;
            case MP20:
                kumuluzVersion = "3.2.0";
                kumuluzeeConfigVersion = "1.3.0";
                artifactId = "kumuluzee-microProfile-2.0";
                break;
            case MP14:
                kumuluzVersion = "2.6.0";
                kumuluzeeConfigVersion = "1.3.0";
                artifactId = "kumuluzee-microProfile-1.4";
                break;
            case MP13:
                kumuluzVersion = "2.6.0";
                kumuluzeeConfigVersion = "1.3.0";
                artifactId = "kumuluzee-microProfile-1.3";
                break;
            case MP12:
                kumuluzVersion = "2.5.2";
                kumuluzeeConfigVersion = "1.1.1";
                artifactId = "kumuluzee-microProfile-1.2";
                break;
            default:
        }
        pomFile.addProperty("kumuluz.version", kumuluzVersion);
        pomFile.addProperty("kumuluzee-config-mp.version", kumuluzeeConfigVersion);
        pomFile.addProperty("kumuluzee-microprofile.version", artifactId);

    }
}
