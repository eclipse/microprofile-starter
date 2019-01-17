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

    public static final String SRC_TEST_JAVA = "src/test/java";
    public static final String SRC_TEST_RESOURCES = "src/test/resources";

    @Inject
    private DirectoryCreator directoryCreator;

    @Inject
    private FileCreator fileCreator;

    @Inject
    private AddonManager addonManager;

    @Inject
    private MavenHelper mavenHelper;

    public void createMavenFiles(JessieModel model) {
        Model pomFile = null;
        switch (model.getSpecification().getModuleStructure()) {

            case SINGLE:
                pomFile = createSingleModule(model);
                break;
            case MULTI:
                throw new IllegalArgumentException("Maven multi module needs to be supported");
                //break;
        }

        applyMavenAdapters(model, pomFile);

        writePOMFile(pomFile, model);

        createDefaultDirectories(model);
    }

    private void writePOMFile(Model pomFile, JessieModel model) {
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

        fileCreator.writeContents(model.getDirectory(), "pom.xml", content);
    }

    private void applyMavenAdapters(JessieModel model, Model pomFile) {
        List<JessieAddon> allAddons = model.getParameter(JessieModel.Parameter.ADDONS);

        for (JessieAddon addon : allAddons) {
            addon.adaptMavenModel(pomFile, model);
        }

        for (JessieMavenAdapter mavenAdapter : addonManager.getMavenAdapters()) {
            mavenAdapter.adaptMavenModel(pomFile, model);
        }
    }

    private void createDefaultDirectories(JessieModel model) {
        String javaDirectory = model.getDirectory() + "/" + SRC_MAIN_JAVA;
        directoryCreator.createDirectory(javaDirectory);
        javaDirectory = model.getDirectory() + "/src/test/java";
        directoryCreator.createDirectory(javaDirectory);
        fileCreator.createEmptyFile(javaDirectory, ".gitkeep");

        String resourcesDirectory = model.getDirectory() + "/src/main/resources";
        directoryCreator.createDirectory(resourcesDirectory);
        fileCreator.createEmptyFile(resourcesDirectory, ".gitkeep");

        String webappDirectory = model.getDirectory() + "/" + SRC_MAIN_WEBAPP;
        directoryCreator.createDirectory(webappDirectory);
        fileCreator.createEmptyFile(webappDirectory, ".gitkeep");
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
