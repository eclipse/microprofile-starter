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

import org.eclipse.microprofile.starter.core.model.BuildTool;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.spi.JessieAddon;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 *
 */
@ApplicationScoped
public class Creator {

    @Inject
    MavenCreator mavenCreator;

    @Inject
    GradleCreator gradleCreator;

    @Inject
    CDICreator cdiCreator;

    @Inject
    JavaCreator javaCreator;

    public void createArtifacts(JessieModel model) {

        if (model.getSpecification().getBuildTool() == BuildTool.MAVEN) {
            mavenCreator.createMavenFiles(model);
        } else {
            gradleCreator.createGradleFiles(model);
        }

        cdiCreator.createCDIFilesForWeb(model);

        javaCreator.createJavaFiles(model);

        List<JessieAddon> addons = model.getParameter(JessieModel.Parameter.ADDONS);
        for (JessieAddon addon : addons) {
            addon.createFiles(model);
        }
    }
}
