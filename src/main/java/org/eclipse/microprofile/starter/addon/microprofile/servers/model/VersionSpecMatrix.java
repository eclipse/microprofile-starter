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
 */
package org.eclipse.microprofile.starter.addon.microprofile.servers.model;

import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;

import java.util.EnumMap;
import java.util.Map;

public final class VersionSpecMatrix {

    private static final VersionSpecMatrix MATRIX = new VersionSpecMatrix();

    private Map<MicroProfileVersion, Map<MicroprofileSpec, String>> data;

    private VersionSpecMatrix() {
        init();
    }

    private void init() {
        data = new EnumMap<>(MicroProfileVersion.class);

        data.put(MicroProfileVersion.MP12,
                new EnumMapBuilder()
                .add(MicroprofileSpec.CONFIG, "1.1")
                .add(MicroprofileSpec.FAULT_TOLERANCE, "1.0")
                .add(MicroprofileSpec.JWT_AUTH, "1.0")
                .add(MicroprofileSpec.HEALTH_CHECKS, "1.0")
                .add(MicroprofileSpec.METRICS, "1.0"));

        data.put(MicroProfileVersion.MP13,
                new EnumMapBuilder()
                .add(MicroprofileSpec.CONFIG, "1.2")
                .add(MicroprofileSpec.FAULT_TOLERANCE, "1.0")
                .add(MicroprofileSpec.JWT_AUTH, "1.0")
                .add(MicroprofileSpec.HEALTH_CHECKS, "1.0")
                .add(MicroprofileSpec.OPEN_API, "1.0")
                .add(MicroprofileSpec.REST_CLIENT, "1.0")
                .add(MicroprofileSpec.OPEN_TRACING, "1.0")
                .add(MicroprofileSpec.METRICS, "1.1"));

        data.put(MicroProfileVersion.MP14,
                new EnumMapBuilder()
                .add(MicroprofileSpec.CONFIG, "1.3")
                .add(MicroprofileSpec.FAULT_TOLERANCE, "1.1")
                .add(MicroprofileSpec.JWT_AUTH, "1.1")
                .add(MicroprofileSpec.HEALTH_CHECKS, "1.0")
                .add(MicroprofileSpec.OPEN_API, "1.0")
                .add(MicroprofileSpec.REST_CLIENT, "1.1")
                .add(MicroprofileSpec.OPEN_TRACING, "1.1")
                .add(MicroprofileSpec.METRICS, "1.1"));

        data.put(MicroProfileVersion.MP20,
                new EnumMapBuilder()
                .add(MicroprofileSpec.CONFIG, "1.3")
                .add(MicroprofileSpec.FAULT_TOLERANCE, "1.1")
                .add(MicroprofileSpec.JWT_AUTH, "1.1")
                .add(MicroprofileSpec.HEALTH_CHECKS, "1.0")
                .add(MicroprofileSpec.OPEN_API, "1.0")
                .add(MicroprofileSpec.REST_CLIENT, "1.1")
                .add(MicroprofileSpec.OPEN_TRACING, "1.1")
                .add(MicroprofileSpec.METRICS, "1.1"));

        data.put(MicroProfileVersion.MP21,
                new EnumMapBuilder()
                .add(MicroprofileSpec.CONFIG, "1.3")
                .add(MicroprofileSpec.FAULT_TOLERANCE, "1.1")
                .add(MicroprofileSpec.JWT_AUTH, "1.1")
                .add(MicroprofileSpec.HEALTH_CHECKS, "1.0")
                .add(MicroprofileSpec.OPEN_API, "1.0")
                .add(MicroprofileSpec.REST_CLIENT, "1.1")
                .add(MicroprofileSpec.OPEN_TRACING, "1.2")
                .add(MicroprofileSpec.METRICS, "1.1"));

        data.put(MicroProfileVersion.MP22,
                new EnumMapBuilder()
                .add(MicroprofileSpec.CONFIG, "1.3")
                .add(MicroprofileSpec.FAULT_TOLERANCE, "2.0")
                .add(MicroprofileSpec.JWT_AUTH, "1.1")
                .add(MicroprofileSpec.HEALTH_CHECKS, "1.0")
                .add(MicroprofileSpec.OPEN_API, "1.1")
                .add(MicroprofileSpec.REST_CLIENT, "1.2")
                .add(MicroprofileSpec.OPEN_TRACING, "1.3")
                .add(MicroprofileSpec.METRICS, "1.1"));

        data.put(MicroProfileVersion.MP30,
                new EnumMapBuilder()
                .add(MicroprofileSpec.CONFIG, "1.3")
                .add(MicroprofileSpec.FAULT_TOLERANCE, "2.0")
                .add(MicroprofileSpec.JWT_AUTH, "1.1")
                .add(MicroprofileSpec.HEALTH_CHECKS, "2.0")
                .add(MicroprofileSpec.OPEN_API, "1.1")
                .add(MicroprofileSpec.REST_CLIENT, "1.3")
                .add(MicroprofileSpec.OPEN_TRACING, "1.3")
                .add(MicroprofileSpec.METRICS, "2.0"));

        data.put(MicroProfileVersion.MP32,
                new EnumMapBuilder()
                .add(MicroprofileSpec.CONFIG, "1.3")
                .add(MicroprofileSpec.FAULT_TOLERANCE, "2.0")
                .add(MicroprofileSpec.JWT_AUTH, "1.1")
                .add(MicroprofileSpec.HEALTH_CHECKS, "2.1")
                .add(MicroprofileSpec.OPEN_API, "1.1")
                .add(MicroprofileSpec.REST_CLIENT, "1.3")
                .add(MicroprofileSpec.OPEN_TRACING, "1.3")
                .add(MicroprofileSpec.METRICS, "2.2"));

        data.put(MicroProfileVersion.MP33,
                new EnumMapBuilder()
                .add(MicroprofileSpec.CONFIG, "1.4")
                .add(MicroprofileSpec.FAULT_TOLERANCE, "2.1")
                .add(MicroprofileSpec.JWT_AUTH, "1.1")
                .add(MicroprofileSpec.HEALTH_CHECKS, "2.2")
                .add(MicroprofileSpec.OPEN_API, "1.1")
                .add(MicroprofileSpec.REST_CLIENT, "1.4.0")
                .add(MicroprofileSpec.OPEN_TRACING, "1.3")
                .add(MicroprofileSpec.METRICS, "2.3"));

        data.put(MicroProfileVersion.MP40,
                new EnumMapBuilder()
                .add(MicroprofileSpec.CONFIG, "2.0")
                .add(MicroprofileSpec.FAULT_TOLERANCE, "3.0")
                .add(MicroprofileSpec.JWT_AUTH, "1.2")
                .add(MicroprofileSpec.HEALTH_CHECKS, "3.0")
                .add(MicroprofileSpec.OPEN_API, "2.0")
                .add(MicroprofileSpec.REST_CLIENT, "2.0.0")
                .add(MicroprofileSpec.OPEN_TRACING, "2.0")
                .add(MicroprofileSpec.METRICS, "3.0"));

        data.put(MicroProfileVersion.MP41,
                new EnumMapBuilder()
                        .add(MicroprofileSpec.CONFIG, "2.0")
                        .add(MicroprofileSpec.FAULT_TOLERANCE, "3.0")
                        .add(MicroprofileSpec.JWT_AUTH, "1.2")
                        .add(MicroprofileSpec.HEALTH_CHECKS, "3.1")
                        .add(MicroprofileSpec.OPEN_API, "2.0")
                        .add(MicroprofileSpec.REST_CLIENT, "2.0.0")
                        .add(MicroprofileSpec.OPEN_TRACING, "2.0")
                        .add(MicroprofileSpec.METRICS, "3.0"));
        
        data.put(MicroProfileVersion.MP50,
                new EnumMapBuilder()
                        .add(MicroprofileSpec.CONFIG, "3.0")
                        .add(MicroprofileSpec.FAULT_TOLERANCE, "4.0")
                        .add(MicroprofileSpec.JWT_AUTH, "2.0")
                        .add(MicroprofileSpec.HEALTH_CHECKS, "4.0")
                        .add(MicroprofileSpec.OPEN_API, "3.0")
                        .add(MicroprofileSpec.REST_CLIENT, "3.0.0")
                        .add(MicroprofileSpec.OPEN_TRACING, "3.0")
                        .add(MicroprofileSpec.METRICS, "4.0"));
        data.put(MicroProfileVersion.MP60,
                new EnumMapBuilder()
                        .add(MicroprofileSpec.CONFIG, "3.0")
                        .add(MicroprofileSpec.FAULT_TOLERANCE, "4.0")
                        .add(MicroprofileSpec.JWT_AUTH, "2.1")
                        .add(MicroprofileSpec.HEALTH_CHECKS, "4.0")
                        .add(MicroprofileSpec.OPEN_API, "3.1")
                        .add(MicroprofileSpec.REST_CLIENT, "3.0.0")
                        .add(MicroprofileSpec.METRICS, "5.0")
                        .add(MicroprofileSpec.TELEMETRY,"1.0"));
    }

    public Map<MicroprofileSpec, String> getSpecData(MicroProfileVersion version) {
        return data.get(version);
    }

    public static VersionSpecMatrix getInstance() {
        return MATRIX;
    }

    private static class EnumMapBuilder extends EnumMap<MicroprofileSpec, String> {

        public EnumMapBuilder() {
            super(MicroprofileSpec.class);
        }

        public EnumMapBuilder add(MicroprofileSpec spec, String version) {
            put(spec, version);
            return this;
        }
    }
}
