/*
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

/**
 *
 */
public enum JavaEEVersion implements ComboBoxItem {
    NONE(null, ""), EE6("6", "Java EE 6"), EE7("7", "Java EE 7"), EE8("8", "Java EE 8");

    private String code;
    private String label;
    private Set<String> alternatives;

    JavaEEVersion(String code, String label) {
        this.code = code;
        this.label = label;
        alternatives = new HashSet<>();
        if (code != null) {
            switch (code) {
                case "6":
                    alternatives.add("ee6");
                    break;
                case "7":
                    alternatives.add("ee7");
                    break;
                case "8":
                    alternatives.add("ee8");
                    break;
            }
        }
    }

    @Override
    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    public Set<String> getAlternatives() {
        return alternatives;
    }

    public static JavaEEVersion valueFor(String code) {
        JavaEEVersion result = null;
        for (JavaEEVersion javaEEVersion : JavaEEVersion.values()) {
            if (javaEEVersion.code != null && javaEEVersion.code.equals(code)) {
                result = javaEEVersion;
            }
        }
        return result;
    }

}
