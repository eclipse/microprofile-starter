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
import org.eclipse.microprofile.starter.core.model.JessieModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class LibertyServer extends AbstractMicroprofileAddon {

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public String addonName() {
        return SupportedServer.LIBERTY.getCode();
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        String resourceDirectory = model.getDirectory() + "/src/main/liberty/config";

        directoryCreator.createDirectory(resourceDirectory);

        processTemplateFile(resourceDirectory, "server.xml", alternatives, variables);

        resourceDirectory = model.getDirectory() + "/src/main/liberty/server/resources/security";

        directoryCreator.createDirectory(resourceDirectory);

        processFile(resourceDirectory, "public.jks", alternatives);

    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model) {
        String openLibertyVersion = "";
        String openLibertyMavenVersion = "";
        switch (model.getSpecification().getMicroProfileVersion()) {

            case NONE:
                break;
            case MP22:
                break;
            case MP21:
                openLibertyVersion = "RELEASE";
                openLibertyMavenVersion = "2.2";
                break;
            case MP20:
                openLibertyVersion = "RELEASE";
                openLibertyMavenVersion = "2.2";
                break;
            case MP14:
                openLibertyVersion = "18.0.0.3";
                openLibertyMavenVersion = "2.0";
                break;
            case MP13:
                openLibertyVersion = "18.0.0.1";
                openLibertyMavenVersion = "2.0";
                break;
            case MP12:
                openLibertyVersion = "17.0.0.3";
                openLibertyMavenVersion = "2.0";
                break;
        }
        pomFile.addProperty("openliberty.version", openLibertyVersion);
        pomFile.addProperty("openliberty.maven.version", openLibertyMavenVersion);

    }
}
