/*
 * Copyright (c) 2019 - 2022 Contributors to the Eclipse Foundation
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
import org.codehaus.plexus.util.xml.Xpp3Dom;
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

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@ApplicationScoped
public class WildFlyServer extends AbstractMicroprofileAddon {

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public String addonName() {
        return SupportedServer.WILDFLY.getCode();
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        List<MicroprofileSpec> microprofileSpecs = model.getParameter(JessieModel.Parameter.MICROPROFILESPECS);
        Set<String> bAlternatives = new HashSet<>(alternatives);

        if (model.hasMainAndSecondaryProject()) {
            bAlternatives.add(JessieModel.SECONDARY_INDICATOR);
            String metaInfDirectory = getResourceDirectory(model, false) + "/META-INF";
            directoryCreator.createDirectory(metaInfDirectory);
            templateEngine.processTemplateFile(metaInfDirectory, "microprofile-config.properties",
                    "microprofile-config.properties", bAlternatives, variables);
        }

        String metaInfDirectory = getResourceDirectory(model, true) + "/META-INF";
        directoryCreator.createDirectory(metaInfDirectory);
        templateEngine.processTemplateFile(metaInfDirectory, "microprofile-config.properties",
                "microprofile-config.properties", alternatives, variables);

        if (model.hasMainAndSecondaryProject() && microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            // Specific files for Auth-JWT
            String resourceDirectory = getResourceDirectory(model, false);
            directoryCreator.createDirectory(resourceDirectory);
            metaInfDirectory = getResourceDirectory(model, false) + "/META-INF";
            directoryCreator.createDirectory(metaInfDirectory);
            templateEngine.processTemplateFile(metaInfDirectory, "publicKey.pem", "publicKey.pem", bAlternatives, variables);
        }
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model, boolean mainProject) {
        pomFile.addProperty("version.wildfly", defineWildFlyVersion(model));
        List<MicroprofileSpec> microprofileSpecs = model.getParameter(JessieModel.Parameter.MICROPROFILESPECS);
        Xpp3Dom configuration = (Xpp3Dom) pomFile.getProfiles().get(0).getBuild().getPlugins()
                .get(0).getConfiguration();

        Xpp3Dom layers = new Xpp3Dom("layers");

        Xpp3Dom layer = new Xpp3Dom("layer");
        layer.setValue("jaxrs");
        layers.addChild(layer);

        if (microprofileSpecs.contains(MicroprofileSpec.CONFIG)) {
            layer = new Xpp3Dom("layer");
            layer.setValue("microprofile-config");
            layers.addChild(layer);
        }
        if (microprofileSpecs.contains(MicroprofileSpec.FAULT_TOLERANCE) && mainProject) {
            layer = new Xpp3Dom("layer");
            layer.setValue("microprofile-fault-tolerance");
            layers.addChild(layer);
        }
        if (microprofileSpecs.contains(MicroprofileSpec.HEALTH_CHECKS) && mainProject) {
            layer = new Xpp3Dom("layer");
            layer.setValue("microprofile-health");
            layers.addChild(layer);
        }
        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            layer = new Xpp3Dom("layer");
            layer.setValue("microprofile-jwt");
            layers.addChild(layer);
        }
        if (microprofileSpecs.contains(MicroprofileSpec.METRICS) && mainProject) {
            layer = new Xpp3Dom("layer");
            layer.setValue("microprofile-metrics");
            layers.addChild(layer);
        }
        if (microprofileSpecs.contains(MicroprofileSpec.OPEN_API) && mainProject) {
            layer = new Xpp3Dom("layer");
            layer.setValue("microprofile-openapi");
            layers.addChild(layer);
        }
        if (microprofileSpecs.contains(MicroprofileSpec.OPEN_TRACING)) {
            layer = new Xpp3Dom("layer");
            layer.setValue("open-tracing");
            layers.addChild(layer);
        }
        if ((microprofileSpecs.contains(MicroprofileSpec.REST_CLIENT) ||
                microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) &&
                mainProject && !microprofileSpecs.contains(MicroprofileSpec.CONFIG)) {
            layer = new Xpp3Dom("layer");
            layer.setValue("microprofile-config");
            layers.addChild(layer);
        }
        configuration.addChild(layers);
    }

    private String defineWildFlyVersion(JessieModel model) {
        switch (model.getSpecification().getMicroProfileVersion()) {
            case NONE:
                break;
            case MP41:
                return "25.0.0.Final";
            case MP40:
                return "23.0.2.Final";
            case MP33:
                return "20.0.0.Final";
            case MP32:
                return "20.0.0.Final";
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
