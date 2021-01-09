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

import java.util.Arrays;
import java.util.List;

public enum StandaloneMPSpec {
    // @formatter:off
    GRAPHQL("graphql", "GraphQL",
            "https://github.com/eclipse/microprofile-graphql/releases/tag/%s",
            "GraphQL - GraphQL is a query language for APIs and a library for fulfilling those queries with your existing data.",
            Arrays.asList(ServerMPVersion.of(SupportedServer.LIBERTY)),
            "org.eclipse.microprofile.graphql",
            "microprofile-graphql-api",
            "1.0.2")
    // @formatter:off
    ;

    private String code;
    private String label;
    private String tagURL;
    private String description;
    private List<ServerMPVersion> serverRestrictions;
    private String groupId;
    private String artifactId;
    private String version;

    StandaloneMPSpec(String code,
                     String label,
                     String tagURL,
                     String description,
                     List<ServerMPVersion> serverRestrictions,
                     String groupId,
                     String artifactId,
                     String version) {
        this.code = code;
        this.label = label;
        this.tagURL = tagURL;
        this.description = description;
        this.serverRestrictions = serverRestrictions;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getTagURL() {
        return tagURL;
    }

    public String getDescription() {
        return description;
    }

    public List<ServerMPVersion> getServerRestrictions() {
        return serverRestrictions;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public static StandaloneMPSpec valueFor(String data) {
        StandaloneMPSpec result = null;
        for (StandaloneMPSpec spec : StandaloneMPSpec.values()) {
            if (spec.code.equalsIgnoreCase(data)) {
                result = spec;
            }
        }
        return result;
    }

}
