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

import org.eclipse.microprofile.starter.core.files.FileCopyEngine;
import org.eclipse.microprofile.starter.core.files.ThymeleafEngine;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class TemplateEngine {

    @Inject
    protected ThymeleafEngine thymeleafEngine;

    @Inject
    protected FileCopyEngine fileCopyEngine;

    @Inject
    protected FileCreator fileCreator;

    public void processTemplateFile(String directory, String templateFileName, String fileName,
                                    Set<String> alternatives, Map<String, String> variables) {
        String javaFile = thymeleafEngine.processFile(templateFileName, alternatives, variables);
        fileCreator.writeContents(directory, fileName, javaFile, false);
    }

    public void processTemplateFile(String directory, String fileName, Set<String> alternatives, Map<String, String> variables) {
        String javaFile = thymeleafEngine.processFile(fileName, alternatives, variables);
        fileCreator.writeContents(directory, fileName, javaFile, false);
    }

    public void processFile(String directory, String fileName, Set<String> alternatives) {
        byte[] fileContent = fileCopyEngine.processFile(fileName, alternatives);
        fileCreator.writeContents(directory, fileName, fileContent, false);
    }

    public void processFile(String directory, String fileName, Set<String> alternatives, Boolean executable) {
        byte[] fileContent = fileCopyEngine.processFile(fileName, alternatives);
        fileCreator.writeContents(directory, fileName, fileContent, executable);
    }
}
