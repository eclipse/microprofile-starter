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

import org.eclipse.microprofile.starter.core.addon.AddonManager;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.spi.JessieAddon;
import org.eclipse.microprofile.starter.spi.JessieGradleAdapter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.eclipse.microprofile.starter.core.model.JessieModel.MAIN_INDICATOR;

/**
 *
 */
@ApplicationScoped
public class GradleCreator extends BuildToolCreator {

    @Inject
    private AddonManager addonManager;

    @Inject
    private TemplateEngine templateEngine;

    public void createGradleFiles(JessieModel model) {
        createDefaultDirectories(model, true);

        if (model.hasMainAndSecondaryProject()) {
            createDefaultDirectories(model, false);
        }
    }

    @Override
    protected void createDefaultDirectories(JessieModel model, boolean mainProject) {
        super.createDefaultDirectories(model, mainProject);

        Set<String> alternatives = new HashSet<>(model.getParameter(JessieModel.Parameter.ALTERNATIVES));
        alternatives.add("gradle");  // So that files can be placed in a separate directory

        if (!mainProject) {

            alternatives.add(JessieModel.SECONDARY_INDICATOR);
        }

        String rootDirectory = model.getDirectory(mainProject);
        templateEngine.processFile(rootDirectory, "gradlew", alternatives, true);
        templateEngine.processFile(rootDirectory, "gradlew.bat", alternatives, true);

        String gradleWrapperDirectory = rootDirectory + "/gradle/wrapper";
        directoryCreator.createDirectory(gradleWrapperDirectory);

        templateEngine.processFile(gradleWrapperDirectory, "gradle-wrapper.jar", alternatives);
        templateEngine.processFile(gradleWrapperDirectory, "gradle-wrapper.properties", alternatives);

        Map<String, String> variables = model.getVariables();
        variables.putAll(defineVariablesFromAdapters(model, mainProject));

        if (rootDirectory.contains(MAIN_INDICATOR)) {
            variables.put("mainProject", "true");
        } else {
            variables.put("mainProject", "false");
        }
        templateEngine.processTemplateFile(rootDirectory, "build.gradle", alternatives, variables);
        templateEngine.processTemplateFile(rootDirectory, "settings.gradle", alternatives, variables);
    }

    private Map<String, String> defineVariablesFromAdapters(JessieModel model, boolean mainProject) {
        List<JessieAddon> allAddons = model.getParameter(JessieModel.Parameter.ADDONS);

        Map<String, String> result = new HashMap<>();

        for (JessieAddon addon : allAddons) {
            result.putAll(addon.defineAdditionalVariables(model, mainProject));
        }

        for (JessieGradleAdapter gradleAdapter : addonManager.getGradleAdapters()) {
            result.putAll(gradleAdapter.defineAdditionalVariables(model, mainProject));
        }
        return result;
    }
}
