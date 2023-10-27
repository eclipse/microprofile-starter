/*
 * Copyright (c) 2017-2021 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.core;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.starter.core.model.BuildTool;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@ApplicationScoped
public class TemplateVariableProvider {

    public Map<String, String> determineVariables(JessieModel model) {
        Map<String, String> result = new HashMap<>();

        result.put("java_package", model.getMaven().getGroupId() + '.' + model.getMaven().getPackage());
        result.put("build_tool", model.getSpecification().getBuildTool().name());
        result.put("maven_groupid", model.getMaven().getGroupId());
        result.put("maven_artifactid", model.getMaven().getArtifactId());

        String artifactId = model.getMaven().getArtifactId().replaceAll("\\.", "");
        result.put("artifact", StringUtils.capitalize(artifactId));
        result.put("application", StringUtils.capitalize(artifactId.replaceAll("-", "")));

        result.put("mp_version", model.getSpecification().getMicroProfileVersion().getCode());
        result.put("mp_depversion", model.getSpecification().getMicroProfileVersion().getDepVersion());
        result.put("secondary_project", model.hasMainAndSecondaryProject() ? "true" : "false");

        JavaSEVersion seVersion = model.getSpecification().getJavaSEVersion();
        if (model.getSpecification().getBuildTool() == BuildTool.GRADLE) {
            // Gradle:
            // Good: sourceCompatibility = JavaVersion.VERSION_1_8
            // No-good: sourceCompatibility = JavaVersion.VERSION_1.8
            result.put("se_version", seVersion.getCode().replace(".", "_"));
        } else {
            result.put("se_version", seVersion.getCode());
        }

        if (model.getSpecification().getMicroProfileVersion() == MicroProfileVersion.MP50) {
            result.put("jakarta_ee_package", "jakarta");

        } else {
            result.put("jakarta_ee_package", "javax");

        }
        return result;
    }
}
