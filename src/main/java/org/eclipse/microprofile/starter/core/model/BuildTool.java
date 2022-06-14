/*
 * Copyright (c) 2021 - 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.core.model;

import java.util.Arrays;

public enum BuildTool {

    MAVEN, GRADLE;

    public static BuildTool forValue(String data) {
        return Arrays.stream(BuildTool.values())
                .filter(v -> v.name().equalsIgnoreCase(data))
                .findAny().orElse(null);

    }
}
