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
import org.eclipse.microprofile.starter.core.artifacts.MavenCreator;
import org.eclipse.microprofile.starter.core.model.JessieModel;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@ApplicationScoped
public class QuarkusServer extends AbstractMicroprofileAddon {

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public String addonName() {
        return SupportedServer.QUARKUS.getCode();
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        directoryCreator.removeDirectory(model.getDirectory(true) + "/" + MavenCreator.SRC_MAIN_WEBAPP);
        if (model.hasMainAndSecondaryProject()) {
            directoryCreator.removeDirectory(model.getDirectory(false) + "/" + MavenCreator.SRC_MAIN_WEBAPP);
        }

        List<MicroprofileSpec> microprofileSpecs = model.getParameter(JessieModel.Parameter.MICROPROFILESPECS);

        Set<String> bAlternatives = new HashSet<>(alternatives);
        templateEngine.processTemplateFile(getResourceDirectory(model, true),
                "application.properties", alternatives, variables);

        if (model.hasMainAndSecondaryProject()) {
            bAlternatives.add(JessieModel.SECONDARY_INDICATOR);
            templateEngine.processTemplateFile(getResourceDirectory(model, false),
                    "application.properties", bAlternatives, variables);
        }

        String webDirectory = getResourceDirectory(model, true) + "/META-INF/resources";
        directoryCreator.createDirectory(webDirectory);
        templateEngine.processTemplateFile(webDirectory, "index.html", alternatives, variables);

        if (model.hasMainAndSecondaryProject() && microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            // Specific files for Auth-JWT
            String metaInfDirectory = getResourceDirectory(model, false) + "/META-INF/resources";
            directoryCreator.createDirectory(metaInfDirectory);
            templateEngine.processTemplateFile(metaInfDirectory, "publicKey.pem", alternatives, variables);
        }
    }

    @Override
    public Map<String, String> defineAdditionalVariables(JessieModel model, boolean mainProject) {
        // For customization of the gradle.properties file
        Map<String, String> result = new HashMap<>();
        result.put("quarkus_version", defineQuarkusVersion(model));
        return result;
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model, boolean mainProject) {
        pomFile.addProperty("version.quarkus", defineQuarkusVersion(model));
        pomFile.setPackaging("jar");
        List<MicroprofileSpec> microprofileSpecs = model.getParameter(JessieModel.Parameter.MICROPROFILESPECS);

        // We add Rest by default as all our examples use it anyway.
        mavenHelper.addDependency(pomFile, "io.quarkus", "quarkus-resteasy", "${version.quarkus}");

        if (microprofileSpecs.contains(MicroprofileSpec.FAULT_TOLERANCE) && mainProject) {
            mavenHelper.addDependency(pomFile, "io.quarkus", "quarkus-smallrye-fault-tolerance", "${version.quarkus}");
        }
        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            mavenHelper.addDependency(pomFile, "io.quarkus", "quarkus-smallrye-jwt", "${version.quarkus}");
        }
        if (microprofileSpecs.contains(MicroprofileSpec.METRICS) && mainProject) {
            mavenHelper.addDependency(pomFile, "io.quarkus", "quarkus-smallrye-metrics", "${version.quarkus}");
        }
        if (microprofileSpecs.contains(MicroprofileSpec.HEALTH_CHECKS) && mainProject) {
            mavenHelper.addDependency(pomFile, "io.quarkus", "quarkus-smallrye-health", "${version.quarkus}");
        }
        if (microprofileSpecs.contains(MicroprofileSpec.OPEN_API) && mainProject) {
            mavenHelper.addDependency(pomFile, "io.quarkus", "quarkus-smallrye-openapi", "${version.quarkus}");
        }
        if (microprofileSpecs.contains(MicroprofileSpec.OPEN_TRACING)) {
            mavenHelper.addDependency(pomFile, "io.quarkus", "quarkus-smallrye-opentracing", "${version.quarkus}");
        }
        if ((microprofileSpecs.contains(MicroprofileSpec.REST_CLIENT) || microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) && mainProject) {
            mavenHelper.addDependency(pomFile, "io.quarkus", "quarkus-rest-client", "${version.quarkus}");
        }
    }

    private String defineQuarkusVersion(JessieModel model) {
        switch (model.getSpecification().getMicroProfileVersion()) {
            case NONE:
                break;
            case MP41:
                return "2.2.3.Final";
            case MP40:
                break;
            case MP33:
                break;
            case MP32:
                return "1.7.6.Final";
            case MP22:
                break;
            case MP21:
                break;
            case MP20:
                break;
            case MP14:
                break;
            case MP13:
                break;
            case MP12:
                break;
            default:
        }
        return null;
    }
}
