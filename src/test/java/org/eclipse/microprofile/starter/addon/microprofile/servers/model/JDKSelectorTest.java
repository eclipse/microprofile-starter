/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.addon.microprofile.servers.model;

import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JDKSelectorTest {

    private JDKSelector selector;

    @Before
    public void setup() {
        selector = new JDKSelector();
        selector.init();
    }

    @Test
    public void testNoRange() {
        List<JavaSEVersion> supportedVersions = selector.getSupportedVersion(SupportedServer.WILDFLY_SWARM, MicroProfileVersion.MP14);
        Assert.assertEquals(1, supportedVersions.size());
        Assert.assertEquals(JavaSEVersion.SE8, supportedVersions.get(0));
    }

    @Test
    public void testMinRange() {
        List<JavaSEVersion> supportedVersions = selector.getSupportedVersion(SupportedServer.THORNTAIL_V2, MicroProfileVersion.MP22);
        Assert.assertEquals(2, supportedVersions.size());
        Set<JavaSEVersion> expected = new HashSet<>();
        expected.add(JavaSEVersion.SE8);
        expected.add(JavaSEVersion.SE11);
        for (JavaSEVersion version : supportedVersions) {
            boolean removed = expected.remove(version);
            if (!removed) {
                Assert.fail("The element " + version + " was not expected to be in the list");
            }
        }

    }

    @Test
    public void testMaxRange() {
        List<JavaSEVersion> supportedVersions = selector.getSupportedVersion(SupportedServer.HELIDON, MicroProfileVersion.MP30);
        Assert.assertEquals(1, supportedVersions.size());
        Assert.assertEquals(JavaSEVersion.SE8, supportedVersions.get(0));


        supportedVersions = selector.getSupportedVersion(SupportedServer.HELIDON, MicroProfileVersion.MP32);
        Assert.assertEquals(1, supportedVersions.size());
        Assert.assertEquals(JavaSEVersion.SE11, supportedVersions.get(0));


    }
}
