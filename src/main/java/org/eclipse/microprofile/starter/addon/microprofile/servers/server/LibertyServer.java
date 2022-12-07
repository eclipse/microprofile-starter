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
import org.eclipse.microprofile.starter.addon.microprofile.servers.AbstractMicroprofileAddon;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.JessieModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

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

        String resourceDirectory = model.getDirectory(true)  + "/src/main/liberty/config";
        directoryCreator.createDirectory(resourceDirectory);

        templateEngine.processTemplateFile(resourceDirectory, "server.xml", alternatives, variables);

        List<MicroprofileSpec> microprofileSpecs = model.getParameter(JessieModel.Parameter.MICROPROFILESPECS);
        if (model.hasMainAndSecondaryProject()) {
            Set<String> tempAlternative = new HashSet<>(alternatives);
            tempAlternative.add(JessieModel.SECONDARY_INDICATOR);

            resourceDirectory = model.getDirectory(false)  + "/src/main/liberty/config";
            directoryCreator.createDirectory(resourceDirectory);

            templateEngine.processTemplateFile(resourceDirectory, "server.xml", tempAlternative, variables);
            if  (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {

                resourceDirectory = resourceDirectory + "/resources/security";
                directoryCreator.createDirectory(resourceDirectory);
    
                templateEngine.processFile(resourceDirectory, "public.jks", alternatives);
            }
        }

        
        if (model.hasMainAndSecondaryProject() && microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {

            resourceDirectory = model.getDirectory(false) + "/src/main/liberty/server/resources/security";

            directoryCreator.createDirectory(resourceDirectory);

            templateEngine.processFile(resourceDirectory, "public.jks", alternatives);
        }

    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model, boolean mainProject) {
        String openLibertyMavenVersion = "3.7.1";
        pomFile.addProperty("openliberty.maven.version", openLibertyMavenVersion);
        String jaegerClientVersion="0.34.0";
        String slf4jApiVersion="1.7.25";
        String slf4jJdkVersion="1.7.25";
        switch (model.getSpecification().getMicroProfileVersion()) {

            case NONE:
                break;
            case MP40: case MP41: case MP50:
                jaegerClientVersion="1.5.0";
                slf4jApiVersion="1.7.30";
                slf4jJdkVersion="1.7.30";
                break;
            case MP33: case MP30: 
            case MP22: case MP21: case MP20:
            case MP14: case MP13: case MP12:
            default:
                break;
        }
        pomFile.addProperty("jaeger.client.version", jaegerClientVersion);
        pomFile.addProperty("slf4j.api.version", slf4jApiVersion);
        pomFile.addProperty("slf4j.jdk.version", slf4jJdkVersion);
    }

    @Override
    public Map<String, String> defineAdditionalVariables(JessieModel model, boolean mainProject) {
        // For customization of the build.gradle file
        Map<String, String> result = new HashMap<>();

        if (mainProject) {
            result.put("port_service", SupportedServer.LIBERTY.getPortServiceA());
        } else {
            result.put("port_service", SupportedServer.LIBERTY.getPortServiceB());
        }

        return result;
    }

}
