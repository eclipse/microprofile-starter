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
package org.eclipse.microprofile.starter.addon.microprofile.servers;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.JessieMaven;

import java.util.HashMap;
import java.util.Map;

public class TestDependenciesRestClient {

    private static final TestDependenciesRestClient INSTANCE = new TestDependenciesRestClient();

    private Map<SupportedServer, JessieMavenWithVersion> serverSpecificData;

    private TestDependenciesRestClient() {
        serverSpecificData = new HashMap<>();
        serverSpecificData.put(SupportedServer.PAYARA_MICRO, new JessieMavenWithVersion("org.glassfish.jersey.core", "jersey-client", "2.25.1"));
        serverSpecificData.put(SupportedServer.KUMULUZEE, new JessieMavenWithVersion("org.glassfish.jersey.core", "jersey-client", "2.25.1"));
        serverSpecificData.put(SupportedServer.LIBERTY, new JessieMavenWithVersion("org.apache.cxf", "cxf-rt-rs-client", "3.0.4"));
        serverSpecificData.put(SupportedServer.TOMEE, new JessieMavenWithVersion("org.apache.cxf", "cxf-rt-rs-client", "3.0.4"));
        serverSpecificData.put(SupportedServer.THORNTAIL_V2, new JessieMavenWithVersion("org.jboss.resteasy", "resteasy-client", "3.6.3.Final"));
        serverSpecificData.put(SupportedServer.WILDFLY_SWARM, new JessieMavenWithVersion("org.jboss.resteasy", "resteasy-client", "3.6.3.Final"));
    }

    public JessieMavenWithVersion getServerSpecificData(SupportedServer supportedServer) {
        return serverSpecificData.get(supportedServer);
    }

    public static TestDependenciesRestClient getInstance() {
        return INSTANCE;
    }

    public static class JessieMavenWithVersion extends JessieMaven {
        private String version;

        private JessieMavenWithVersion(String groupId, String artifactId, String version) {
            setGroupId(groupId);
            setArtifactId(artifactId);
            this.version = version;
        }

        public String getVersion() {
            return version;
        }
    }
}
