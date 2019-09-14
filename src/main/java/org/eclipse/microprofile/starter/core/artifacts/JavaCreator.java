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

import org.eclipse.microprofile.starter.core.model.JessieModel;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@ApplicationScoped
public class JavaCreator extends AbstractCreator {

    public void createJavaFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        String rootJava = MavenCreator.SRC_MAIN_JAVA + "/" + directoryCreator.createPathForGroupAndArtifact(model.getMaven());
        String viewDirectory = model.getDirectory(true) + "/" + rootJava;
        directoryCreator.createDirectory(viewDirectory);

        String application = variables.get("application");
        String javaFile = thymeleafEngine.processFile("RestApplication.java", alternatives, variables);
        fileCreator.writeContents(viewDirectory, application + "RestApplication.java", javaFile);

        javaFile = thymeleafEngine.processFile("HelloController.java", alternatives, variables);
        fileCreator.writeContents(viewDirectory, "HelloController.java", javaFile);

        if (model.hasMainAndSecondaryProject()) {
            viewDirectory = model.getDirectory(false) + "/" + rootJava;
            directoryCreator.createDirectory(viewDirectory);

            Set<String> tempAlternative = new HashSet<>(alternatives);
            tempAlternative.add(JessieModel.SECONDARY_INDICATOR);
            application = variables.get("application");
            javaFile = thymeleafEngine.processFile("RestApplication.java", tempAlternative, variables);
            fileCreator.writeContents(viewDirectory, application + "RestApplication.java", javaFile);
        }
    }
}
