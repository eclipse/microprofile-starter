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
package org.eclipse.microprofile.starter.core;

import org.eclipse.microprofile.starter.core.model.JessieMaven;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.BuildTool;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.JessieSpecification;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TemplateVariableProviderTest {

    @Test
    public void shouldStoreValidApplicationName() {
        TemplateVariableProvider provider = new TemplateVariableProvider();
        JessieModel model = new JessieModel();
        JessieMaven maven = new JessieMaven();
        JessieSpecification specification = new JessieSpecification();
        specification.setMicroProfileVersion(MicroProfileVersion.MP22);
        specification.setBuildTool(BuildTool.MAVEN);
        specification.setJavaSEVersion(JavaSEVersion.SE8);
        maven.setArtifactId("demo-service");
        model.setMaven(maven);
        model.setSpecification(specification);
        model.generateMainAndSecondaryProject();

        Map<String, String> variables = provider.determineVariables(model);

        Assert.assertEquals("Demoservice", variables.get("application"));
    }
}