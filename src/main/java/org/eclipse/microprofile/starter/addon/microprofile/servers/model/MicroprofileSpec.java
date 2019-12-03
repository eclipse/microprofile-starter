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
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13, MicroProfileVersion.MP14,
                    MicroProfileVersion.MP20, MicroProfileVersion.MP21, MicroProfileVersion.MP22,
                    MicroProfileVersion.MP30, MicroProfileVersion.MP32))
    , FAULT_TOLERANCE("fault_tolerance", "Fault Tolerance",
            "Fault Tolerance - all about bulkheads, timeouts, circuit breakers, retries, etc. for your microservices",
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13, MicroProfileVersion.MP14,
                    MicroProfileVersion.MP20, MicroProfileVersion.MP21, MicroProfileVersion.MP22,
                    MicroProfileVersion.MP30, MicroProfileVersion.MP32))
    , JWT_AUTH("JWT_auth", "JWT Auth",
            "JWT Propagation - propagate security across your microservices",
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13, MicroProfileVersion.MP14,
                    MicroProfileVersion.MP20, MicroProfileVersion.MP21, MicroProfileVersion.MP22,
                    MicroProfileVersion.MP30, MicroProfileVersion.MP32))
    , METRICS("metrics", "Metrics",
            "Metrics - Gather and create operational and business measurements for your microservices",
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13, MicroProfileVersion.MP14,
                    MicroProfileVersion.MP20, MicroProfileVersion.MP21, MicroProfileVersion.MP22,
                    MicroProfileVersion.MP30, MicroProfileVersion.MP32))
    , HEALTH_CHECKS("health_checks", "Health Checks",
            "Health Checks - Verify the health of your microservices with custom verifications",
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13, MicroProfileVersion.MP14,
                    MicroProfileVersion.MP20, MicroProfileVersion.MP21, MicroProfileVersion.MP22,
                    MicroProfileVersion.MP30, MicroProfileVersion.MP32))
    , OPEN_API("open_API", "OpenAPI",
            "Open API - Generate OpenAPI-compliant API documentation for your microservices",
            Arrays.asList(MicroProfileVersion.MP13, MicroProfileVersion.MP14, MicroProfileVersion.MP20,
                    MicroProfileVersion.MP21, MicroProfileVersion.MP22,
                    MicroProfileVersion.MP30, MicroProfileVersion.MP32))
    , OPEN_TRACING("open_tracing", "OpenTracing",
            "Open Tracing - trace the flow of requests as they traverse your microservices",
            Arrays.asList(MicroProfileVersion.MP13, MicroProfileVersion.MP14, MicroProfileVersion.MP20,
                    MicroProfileVersion.MP21, MicroProfileVersion.MP22,
                    MicroProfileVersion.MP30, MicroProfileVersion.MP32))
    , REST_CLIENT("rest_client", "TypeSafe Rest Client",
            "Rest Client - Invoke RESTful services in a type-safe manner",
            Arrays.asList(MicroProfileVersion.MP13, MicroProfileVersion.MP14, MicroProfileVersion.MP20,
                    MicroProfileVersion.MP21, MicroProfileVersion.MP22,
                    MicroProfileVersion.MP30, MicroProfileVersion.MP32))
    ;
    // @formatter:on

    private String code;
    private String label;
    private String description;
    private List<MicroProfileVersion> mpVersions;

    MicroprofileSpec(String code, String label, String description, List<MicroProfileVersion> mpVersions) {
        this.code = code;
        this.label = label;
        this.description = description;

        this.mpVersions = mpVersions;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
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
            if (spec.code.equals(data)) {
                result = spec;
            }
        }
        return result;
    }
}
