/*
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.core.artifacts;

import org.eclipse.microprofile.starter.core.model.JessieModel;

import javax.inject.Inject;

public class BuildToolCreator {

    public static final String SRC_MAIN_JAVA = "src/main/java";
    public static final String SRC_MAIN_RESOURCES = "src/main/resources";
    public static final String SRC_MAIN_WEBAPP = "src/main/webapp";

    @Inject
    protected FileCreator fileCreator;
    @Inject
    protected DirectoryCreator directoryCreator;

    protected void createDefaultDirectories(JessieModel model, boolean mainProject) {

        String directory = model.getDirectory(mainProject);

        String javaDirectory = directory + "/" + BuildToolCreator.SRC_MAIN_JAVA;
        directoryCreator.createDirectory(javaDirectory);

        String resourcesDirectory = directory + "/" + BuildToolCreator.SRC_MAIN_RESOURCES;
        directoryCreator.createDirectory(resourcesDirectory);
        fileCreator.createEmptyFile(resourcesDirectory, ".gitkeep");

        if (mainProject) {
            String webappDirectory = directory + "/" + BuildToolCreator.SRC_MAIN_WEBAPP;
            directoryCreator.createDirectory(webappDirectory);
            fileCreator.createEmptyFile(webappDirectory, ".gitkeep");
        }
    }
}
