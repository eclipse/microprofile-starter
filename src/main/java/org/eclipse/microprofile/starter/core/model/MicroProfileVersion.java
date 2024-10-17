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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum MicroProfileVersion {

    // Order is important as it determines the order in the Dropdown on the screen.
    // @formatter:off
    NONE(null, "")
    , MP60("6.0", "MP 6.0", Collections.singletonList(Constants.MP6X_ALTERNATIVE))
    , MP50("5.0", "MP 5.0", Collections.singletonList(Constants.MP5X_ALTERNATIVE))
    , MP41("4.1", "MP 4.1", Collections.singletonList(Constants.MP41_ALTERNATIVE))
    , MP40("4.0", "MP 4.0", "4.0.1", Collections.singletonList(Constants.MP3X_ALTERNATIVE))
    , MP33("3.3", "MP 3.3", Collections.singletonList(Constants.MP3X_ALTERNATIVE))
    , MP32("3.2", "MP 3.2", Collections.singletonList(Constants.MP3X_ALTERNATIVE))
    , MP30("3.0", "MP 3.0", Collections.singletonList(Constants.MP3X_ALTERNATIVE))
    , MP22("2.2", "MP 2.2")
    , MP21("2.1", "MP 2.1")
    , MP20("2.0", "MP 2.0", "2.0.1")
    , MP14("1.4", "MP 1.4")
    , MP13("1.3", "MP 1.3")
    , MP12("1.2", "MP 1.2");
    // @formatter:on

    private String code;
    private String depVersion;
    private String label;
    private Set<String> alternatives;

    MicroProfileVersion(String code, String label) {
        this(code, label, code, new HashSet<>());
    }

    MicroProfileVersion(String code, String label, Collection<String> alternatives) {
        this(code, label, code, alternatives);
    }

    MicroProfileVersion(String code, String label, String mavenVersion) {
        this(code, label, mavenVersion, new HashSet<>());

    }

    MicroProfileVersion(String code, String label, String mavenVersion, Collection<String> alternatives) {
        this.code = code;
        this.label = label;
        this.alternatives = new HashSet<>(alternatives);
        this.depVersion = mavenVersion;
    }

    public String getCode() {
        return code;
    }

    public String getDepVersion() {
        return depVersion;
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

    public static class Constants {
        public static final String MP3X_ALTERNATIVE = "mp3_x";
        public static final String MP41_ALTERNATIVE = "mp4_1";
        public static final String MP5X_ALTERNATIVE = "mp5_x";
        public static final String MP6X_ALTERNATIVE = "mp6_x";
        public static final String MP61_ALTERNATIVE = "mp6_1";
    }
}
