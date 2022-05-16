/*
 * Copyright (c) 2020-2022 Contributors to the Eclipse Foundation
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

import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;

import java.util.EnumMap;
import java.util.Map;

public class VersionStandaloneMatrix {

    private static final VersionStandaloneMatrix MATRIX = new VersionStandaloneMatrix();

    private Map<MicroProfileVersion, Map<StandaloneMPSpec, String>> data;

    private VersionStandaloneMatrix() {
        init();
    }

    private void init() {
        data = new EnumMap<>(MicroProfileVersion.class);

        data.put(MicroProfileVersion.MP12,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP13,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP14,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP20,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP21,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP22,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP30,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP32,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP33,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP40,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP41,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "1.0.2"));
        data.put(MicroProfileVersion.MP50,
                new VersionStandaloneMatrix.EnumMapBuilder()
                        .add(StandaloneMPSpec.GRAPHQL, "2.0"));
    }

    public Map<StandaloneMPSpec, String> getSpecData(MicroProfileVersion version) {
        return data.get(version);
    }

    public static VersionStandaloneMatrix getInstance() {
        return MATRIX;
    }

    private static class EnumMapBuilder extends EnumMap<StandaloneMPSpec, String> {

        public EnumMapBuilder() {
            super(StandaloneMPSpec.class);
        }

        public VersionStandaloneMatrix.EnumMapBuilder add(StandaloneMPSpec spec, String version) {
            put(spec, version);
            return this;
        }
    }
}
