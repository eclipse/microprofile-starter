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
package org.eclipse.microprofile.starter.addon.microprofile.servers.model;

import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;

import java.util.Arrays;
import java.util.List;

public enum MicroprofileSpec {
    // @formatter:off
    CONFIG("config", "Config",
            "Configuration - externalize and manage your configuration parameters outside your microservices",
            "https://github.com/eclipse/microprofile-config/releases/tag/%s",
            Arrays.asList(MicroProfileVersion.MP41, MicroProfileVersion.MP50,MicroProfileVersion.MP60,MicroProfileVersion.MP61))
    , FAULT_TOLERANCE("fault_tolerance", "Fault Tolerance",
            "Fault Tolerance - all about bulkheads, timeouts, circuit breakers, retries, etc. for your microservices",
            "https://github.com/eclipse/microprofile-fault-tolerance/releases/tag/%s",
            Arrays.asList( MicroProfileVersion.MP41, MicroProfileVersion.MP50,MicroProfileVersion.MP60,MicroProfileVersion.MP61))
    , JWT_AUTH("JWT_auth", "JWT Auth",
            "JWT Propagation - propagate security across your microservices",
            "https://github.com/eclipse/microprofile-jwt-auth/releases/tag/%s",
            Arrays.asList( MicroProfileVersion.MP41, MicroProfileVersion.MP50,MicroProfileVersion.MP60,MicroProfileVersion.MP61))
    , METRICS("metrics", "Metrics",
            "Metrics - Gather and create operational and business measurements for your microservices",
            "https://github.com/eclipse/microprofile-metrics/releases/tag/%s",
            Arrays.asList( MicroProfileVersion.MP41, MicroProfileVersion.MP50,MicroProfileVersion.MP60,MicroProfileVersion.MP61))
    , HEALTH_CHECKS("health_checks", "Health",
            "Health - Verify the health of your microservices with custom verifications",
            "https://github.com/eclipse/microprofile-metrics/releases/tag/%s",
            Arrays.asList(MicroProfileVersion.MP41, MicroProfileVersion.MP50,MicroProfileVersion.MP60,MicroProfileVersion.MP61))
    , OPEN_API("open_API", "OpenAPI",
            "Open API - Generate OpenAPI-compliant API documentation for your microservices",
            "https://github.com/eclipse/microprofile-open-api/releases/tag/%s",
            Arrays.asList( MicroProfileVersion.MP41, MicroProfileVersion.MP50,MicroProfileVersion.MP60, MicroProfileVersion.MP61))
    , OPEN_TRACING("open_tracing", "OpenTracing",
            "Open Tracing - trace the flow of requests as they traverse your microservices",
            "https://github.com/eclipse/microprofile-opentracing/releases/tag/%s",
            Arrays.asList( MicroProfileVersion.MP41, MicroProfileVersion.MP50))
    , REST_CLIENT("rest_client", "Rest Client",
            "Rest Client - Invoke RESTful services in a type-safe manner",
            "https://github.com/eclipse/microprofile-rest-client/releases/tag/%s",
            Arrays.asList( MicroProfileVersion.MP41, MicroProfileVersion.MP50,MicroProfileVersion.MP60, MicroProfileVersion.MP61))
    ,OPEN_TELEMETRY("open_telemetry","Open Telemetry"
            ,"OpenTelemetry consists of Tracing, Logging, Metrics and Baggage support",
            "https://github.com/eclipse/microprofile-telemetry/releases/tag/$s", Arrays.asList(  MicroProfileVersion.MP60, MicroProfileVersion.MP61))
    ,REACTIVE_MESSAGING(
            "reactive_messaging","Reactive Messaging for MicroProfile",
            "This specification provides asynchronous messaging support based on Reactive Streams for MicroProfile.",
            "https://github.com/eclipse/microprofile-reactive-messaging/releases/tag/%s", Arrays.asList( MicroProfileVersion.MP60, MicroProfileVersion.MP61))
    ,REACTIVE_STREAMS(
            "reactive_streams","MicroProfile Reactive Streams Operators",
            "Reactive Streams is an integration SPI - it allows two different libraries that provide asynchronous streaming to be able to stream data to and from each other",
            "https://github.com/eclipse/microprofile-reactive-messaging/releases/tag/%s", Arrays.asList(  MicroProfileVersion.MP60, MicroProfileVersion.MP61))

    ;
    // @formatter:on

    private String code;
    private String label;
    private String tagURL;
    private String description;
    private List<MicroProfileVersion> mpVersions;

    MicroprofileSpec(String code, String label, String description, String tagURL, List<MicroProfileVersion> mpVersions) {
        this.code = code;
        this.label = label;
        this.tagURL = tagURL;
        this.description = description;

        this.mpVersions = mpVersions;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getTagURL() {
        return tagURL;
    }

    public List<MicroProfileVersion> getMpVersions() {
        return mpVersions;
    }

    public String getLabel() {
        return label;
    }

    public static MicroprofileSpec valueFor(String data) {
        MicroprofileSpec result = null;
        for (MicroprofileSpec spec : MicroprofileSpec.values()) {
            if (spec.code.equalsIgnoreCase(data)) {
                result = spec;
            }
        }
        return result;
    }
}
