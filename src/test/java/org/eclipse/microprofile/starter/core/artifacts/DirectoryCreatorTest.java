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
package org.eclipse.microprofile.starter.core.artifacts;

import org.eclipse.microprofile.starter.FakeDirectoryCreator;
import org.eclipse.microprofile.starter.core.model.JessieMaven;
import org.junit.Assert;
import org.junit.Test;

public class DirectoryCreatorTest {

    @Test
    public void shouldCreateUsingValidPackage() {
        FakeDirectoryCreator creator = new FakeDirectoryCreator();

        JessieMaven maven = new JessieMaven();
        maven.setGroupId("com.test");
        maven.setArtifactId(("test-service"));
        String path = creator.createPathForGroupAndArtifact(maven);

        Assert.assertEquals("com/test/test/service", path);
    }
}