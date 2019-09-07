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
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.microprofile.starter.addon.microprofile.servers.AbstractMicroprofileAddon;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.JessieModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;
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

        List<MicroprofileSpec> microprofileSpecs = model.getParameter(JessieModel.Parameter.MICROPROFILESPECS);
        if (model.hasMainAndSecondaryProject() && microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {

            String resourceDirectory = getResourceDirectory(model, false);
            directoryCreator.createDirectory(resourceDirectory);
            processTemplateFile(resourceDirectory, "publicKey.pem", alternatives, variables);
        }
    }

    @Override
    public void adaptMavenModel(Model pomFile, JessieModel model, boolean mainProject) {
        String tomeeVersion = "";
        switch (model.getSpecification().getMicroProfileVersion()) {

            case NONE:
                break;
            case MP22:
                break;
            case MP21:
                tomeeVersion = "8.0.0-M3";
                break;
            case MP20:
                tomeeVersion = "8.0.0-M3";
                break;
            case MP14:
                tomeeVersion = "8.0.0-M3";
                break;
            case MP13:
                tomeeVersion = "8.0.0-M3";
                break;
            case MP12:
                tomeeVersion = "8.0.0-M3";
                break;
            default:
        }
        pomFile.addProperty("tomee.version", tomeeVersion);

        adjustPOM(pomFile, model, mainProject);

    }

    private void adjustPOM(Model pomFile, JessieModel model, boolean mainProject) {
        Profile profile = pomFile.getProfiles().get(0);// We assume there is only 1 profile.
        Plugin mavenPlugin = findMavenPlugin(profile.getBuild().getPlugins());
        Xpp3Dom configuration = (Xpp3Dom) mavenPlugin.getConfiguration();

        Xpp3Dom httpPort = new Xpp3Dom("tomeeHttpPort");
        httpPort.setValue(
                mainProject ? SupportedServer.TOMEE.getPortServiceA() : SupportedServer.TOMEE.getPortServiceB()
        );
        configuration.addChild(httpPort);

        Xpp3Dom shutdownPort = new Xpp3Dom("tomeeShutdownPort");
        shutdownPort.setValue(
                mainProject ? "8005" : "8105"
        );
        configuration.addChild(shutdownPort);

        Xpp3Dom ajpPort = new Xpp3Dom("tomeeAjpPort");
        ajpPort.setValue(
                mainProject ? "8009" : "8109"
        );
        configuration.addChild(ajpPort);

        List<MicroprofileSpec> microprofileSpecs = model.getParameter(JessieModel.Parameter.MICROPROFILESPECS);
        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH) && !mainProject) {
            Xpp3Dom systemVariables = new Xpp3Dom("systemVariables");
            Xpp3Dom publicKeyLocation = new Xpp3Dom("mp.jwt.verify.publickey.location");
            publicKeyLocation.setValue("/publicKey.pem");
            systemVariables.addChild(publicKeyLocation);
            configuration.addChild(systemVariables);
        }

    }

    private Plugin findMavenPlugin(List<Plugin> plugins) {
        Plugin result = null;
        for (Plugin plugin : plugins) {
            if ("tomee-maven-plugin".equals(plugin.getArtifactId())) {
                result = plugin;
            }
        }
        return result;
    }
}
