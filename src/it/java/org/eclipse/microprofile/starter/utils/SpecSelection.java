/*
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.utils;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public enum SpecSelection {
    EMPTY("", false),
    ALL("&selectAllSpecs=true", true),
    ALL_BUT_JWT_REST(
            "&selectedSpecs=CONFIG" +
                    "&selectedSpecs=FAULT_TOLERANCE" +
                    "&selectedSpecs=HEALTH_CHECKS" +
                    "&selectedSpecs=METRICS" +
                    "&selectedSpecs=OPEN_TRACING" +
                    "&selectedSpecs=OPEN_API", false),
    JWT_REST("&selectedSpecs=JWT_AUTH&selectedSpecs=REST_CLIENT", true);

    public final String queryParam;
    public final boolean hasServiceB;

    SpecSelection(String queryParam, boolean hasServiceB) {
        this.queryParam = queryParam;
        this.hasServiceB = hasServiceB;
    }
}
