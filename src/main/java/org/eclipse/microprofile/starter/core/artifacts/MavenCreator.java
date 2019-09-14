/*
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
public class MavenCreator {

    public static final String SRC_MAIN_JAVA = "src/main/java";
    public static final String SRC_MAIN_RESOURCES = "src/main/resources";
    public static final String SRC_MAIN_WEBAPP = "src/main/webapp";

    @Inject
    private DirectoryCreator directoryCreator;

    @Inject
    private FileCreator fileCreator;

    @Inject
    private AddonManager addonManager;

    @Inject
    private MavenHelper mavenHelper;

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

    private void createDefaultDirectories(JessieModel model, boolean mainProject) {

        String directory = model.getDirectory(mainProject);

        String javaDirectory = directory + "/" + SRC_MAIN_JAVA;
        directoryCreator.createDirectory(javaDirectory);

        String resourcesDirectory = directory + "/" + SRC_MAIN_RESOURCES;
        directoryCreator.createDirectory(resourcesDirectory);
        fileCreator.createEmptyFile(resourcesDirectory, ".gitkeep");

        if (mainProject) {
            String webappDirectory = directory + "/" + SRC_MAIN_WEBAPP;
            directoryCreator.createDirectory(webappDirectory);
            fileCreator.createEmptyFile(webappDirectory, ".gitkeep");
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
        mavenHelper.addDependency(pomFile, "org.eclipse.microprofile", "microprofile",
                model.getSpecification().getMicroProfileVersion().getMavenVersion(), "provided", "pom");
    }

    private void addJavaSEVersionProperties(Model pomFile, JessieModel model) {

        JavaSEVersion seVersion = model.getSpecification().getJavaSEVersion();
        pomFile.addProperty("maven.compiler.source", seVersion.getCode());
        pomFile.addProperty("maven.compiler.target", seVersion.getCode());
    }

}
