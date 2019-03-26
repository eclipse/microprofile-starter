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
package org.eclipse.microprofile.starter.core.model;

import java.util.HashSet;
import java.util.Set;

public enum MicroProfileVersion {

    // @formatter:off
    NONE(null, "")
    , MP21("2.1", "MP 2.1")
    , MP20("2.0", "MP 2.0", "2.0.1")
    , MP14("1.4", "MP 1.4")
    , MP13("1.3", "MP 1.3")
    , MP12("1.2", "MP 1.2");
    // @formatter:on

    private String code;
    private String mavenVersion;
    private String label;
    private Set<String> alternatives;

    MicroProfileVersion(String code, String label) {
        this.code = code;
        this.label = label;
        alternatives = new HashSet<>();
        mavenVersion = code;
    }

    MicroProfileVersion(String code, String label, String mavenVersion) {
        this(code, label);
        this.mavenVersion = mavenVersion;

    }

    public String getCode() {
        return code;
    }

    public String getMavenVersion() {
        return mavenVersion;
    }

    public String getLabel() {
        return label;
    }

    public Set<String> getAlternatives() {
        return alternatives;
    }

    public static MicroProfileVersion valueFor(String code) {
        MicroProfileVersion result = null;
        for (MicroProfileVersion microProfileVersion : MicroProfileVersion.values()) {
            if (microProfileVersion.code != null && microProfileVersion.code.equals(code)) {
                result = microProfileVersion;
            }
        }
        return result;
    }
}
