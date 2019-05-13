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
public class TomeeServer extends AbstractMicroprofileAddon {

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public String addonName() {
        return SupportedServer.TOMEE.getCode();
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        String resourceDirectory = getResourceDirectory(model);
        directoryCreator.createDirectory(resourceDirectory);
        processTemplateFile(resourceDirectory, "publicKey.pem", alternatives, variables);


    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model) {
        String tomeeVersion = "";
        switch (model.getSpecification().getMicroProfileVersion()) {

            case NONE:
                break;
            case MP22:
                break;
            case MP21:
                break;
            case MP20:
                tomeeVersion = "8.0.0-M2";
                break;
            case MP14:
                tomeeVersion = "8.0.0-M2";
                break;
            case MP13:
                tomeeVersion = "8.0.0-M2";
                break;
            case MP12:
                tomeeVersion = "8.0.0-M2";
                break;
            default:
        }
        pomFile.addProperty("tomee.version", tomeeVersion);

    }
}
