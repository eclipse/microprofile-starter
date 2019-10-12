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
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.JessieModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.List;
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

        String resourceDirectory = model.getDirectory(true)  + "/src/main/liberty/config";
        directoryCreator.createDirectory(resourceDirectory);

        processTemplateFile(resourceDirectory, "server.xml", alternatives, variables);

        List<MicroprofileSpec> microprofileSpecs = model.getParameter(JessieModel.Parameter.MICROPROFILESPECS);
        if (model.hasMainAndSecondaryProject()) {
            Set<String> tempAlternative = new HashSet<>(alternatives);
            tempAlternative.add(JessieModel.SECONDARY_INDICATOR);

            resourceDirectory = model.getDirectory(false)  + "/src/main/liberty/config";
            directoryCreator.createDirectory(resourceDirectory);

            processTemplateFile(resourceDirectory, "server.xml", tempAlternative, variables);
            if  (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {

                resourceDirectory = resourceDirectory + "/resources/security";
                directoryCreator.createDirectory(resourceDirectory);
    
                processFile(resourceDirectory, "public.jks", alternatives);
            }
        }

        
        if (model.hasMainAndSecondaryProject() && microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {

            resourceDirectory = model.getDirectory(false) + "/src/main/liberty/server/resources/security";

            directoryCreator.createDirectory(resourceDirectory);

            processFile(resourceDirectory, "public.jks", alternatives);
        }

    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model, boolean mainProject) {
        String openLibertyVersion = "[19.0.0.9,)";
        String openLibertyMavenVersion = "3.1";
        pomFile.addProperty("openliberty.version", openLibertyVersion);
        pomFile.addProperty("openliberty.maven.version", openLibertyMavenVersion);

    }
}
