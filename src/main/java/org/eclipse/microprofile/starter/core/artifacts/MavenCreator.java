/*
 * Copyright (c) 2017 - 2022 Contributors to the Eclipse Foundation
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
 * Contributors:
 *   2018-09-29 - Rudy De Busscher
 *      Initially authored in Atbash Jessie
 */
package org.eclipse.microprofile.starter.core.artifacts;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.addon.AddonManager;
import org.eclipse.microprofile.starter.core.exception.TechnicalException;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.spi.JessieAddon;
import org.eclipse.microprofile.starter.spi.JessieMavenAdapter;
import org.eclipse.microprofile.starter.spi.MavenHelper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 */
@ApplicationScoped
public class MavenCreator extends BuildToolCreator {

    @Inject
    AddonManager addonManager;

    @Inject
    MavenHelper mavenHelper;

    public void createMavenFiles(JessieModel model) {
        Model pomFile = createSingleModule(model);

        applyMavenAdapters(model, pomFile, true);

        writePOMFile(pomFile, model, true);
        createDefaultDirectories(model, true);

        if (model.hasMainAndSecondaryProject()) {
            pomFile = createSingleModule(model);

            applyMavenAdapters(model, pomFile, false);

            writePOMFile(pomFile, model, false);
            createDefaultDirectories(model, false);
        }
    }

    private void writePOMFile(Model pomFile, JessieModel model, boolean mainProject) {
        String content;
        MavenXpp3Writer pomWriter = new MavenXpp3Writer();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pomWriter.write(out, pomFile);
            out.close();
            content = new String(out.toByteArray());
        } catch (IOException e) {
            throw new TechnicalException(e);
        }

        fileCreator.writeContents(model.getDirectory(mainProject), "pom.xml", content);

    }

    private void applyMavenAdapters(JessieModel model, Model pomFile, boolean mainProject) {
        List<JessieAddon> allAddons = model.getParameter(JessieModel.Parameter.ADDONS);

        for (JessieAddon addon : allAddons) {
            addon.adaptMavenModel(pomFile, model, mainProject);
        }

        for (JessieMavenAdapter mavenAdapter : addonManager.getMavenAdapters()) {
            mavenAdapter.adaptMavenModel(pomFile, model, mainProject);
        }
    }

    private Model createSingleModule(JessieModel model) {

        Model pomFile = new Model();
        pomFile.setModelVersion("4.0.0");

        pomFile.setGroupId(model.getMaven().getGroupId());
        pomFile.setArtifactId(model.getMaven().getArtifactId());
        pomFile.setVersion("1.0-SNAPSHOT");

        pomFile.setPackaging("war");

        addDependencies(pomFile, model);

        addJavaSEVersionProperties(pomFile, model);

        pomFile.addProperty("failOnMissingWebXml", "false");

        pomFile.addProperty("final.name", model.getMaven().getArtifactId());

        Build build = new Build();
        build.setFinalName(model.getMaven().getArtifactId());
        pomFile.setBuild(build);

        return pomFile;
    }

    private void addDependencies(Model pomFile, JessieModel model) {
        addJavaMPDependencies(pomFile, model);

    }

    private void addJavaMPDependencies(Model pomFile, JessieModel model) {
        /*
         * Quarkus should not have explicitly enforced MP version in its generated pom.xml
         */
        if (!SupportedServer.QUARKUS.getCode().equals(model.getOptions().get("mp.server").getSingleValue())) {
            mavenHelper.addDependency(pomFile, "org.eclipse.microprofile", "microprofile",
                    model.getSpecification().getMicroProfileVersion().getDepVersion(), "provided", "pom");
        }
    }

    private void addJavaSEVersionProperties(Model pomFile, JessieModel model) {

        JavaSEVersion seVersion = model.getSpecification().getJavaSEVersion();
        pomFile.addProperty("maven.compiler.source", seVersion.getCode());
        pomFile.addProperty("maven.compiler.target", seVersion.getCode());
    }

}
