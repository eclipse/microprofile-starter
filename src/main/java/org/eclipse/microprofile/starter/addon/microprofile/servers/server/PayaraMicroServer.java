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

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class PayaraMicroServer extends AbstractMicroprofileAddon {

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public String addonName() {
        return SupportedServer.PAYARA_MICRO.getCode();
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        List<MicroprofileSpec> microprofileSpecs = model.getParameter(JessieModel.Parameter.MICROPROFILESPECS);
        if (model.hasMainAndSecondaryProject() && microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {

            // Specific files for Auth-JWT
            String resourceDirectory = getResourceDirectory(model, false);
            directoryCreator.createDirectory(resourceDirectory);
            templateEngine.processTemplateFile(resourceDirectory, "publicKey.pem", alternatives, variables);
            templateEngine.processTemplateFile(resourceDirectory, "payara-mp-jwt.properties", alternatives, variables);
        }

        String metaInfDirectory = getResourceDirectory(model, true) + "/META-INF";

        directoryCreator.createDirectory(metaInfDirectory);

    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model, boolean mainProject) {
        String payaraVersion = definePayaraVersion(model);
        pomFile.addProperty("payaraVersion", payaraVersion);
    }

    private String definePayaraVersion(JessieModel model) {
        String payaraVersion = "";
        switch (model.getSpecification().getMicroProfileVersion()) {
            case NONE:
                break;
            case MP61:
                break;
            case MP60:
                payaraVersion = "6.0.0";
                break;
            case MP50:
                payaraVersion = "6.2022.1.Alpha2";
                break;
            case MP41:
                payaraVersion = "5.37.0";
                break;
            default:
                break;
        }
        return payaraVersion;
    }

    @Override
    public Map<String, String> defineAdditionalVariables(JessieModel model, boolean mainProject) {
        // For customization of the build.gradle file
        Map<String, String> result = new HashMap<>();
        result.put("payara_version", definePayaraVersion(model));
        if (mainProject) {
            result.put("port_service", SupportedServer.PAYARA_MICRO.getPortServiceA());
        } else {
            result.put("port_service", SupportedServer.PAYARA_MICRO.getPortServiceB());
        }

        return result;
    }
}
