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

import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class JDKSelector {

    private Map<SupportedServer, Map<MicroProfileVersion, List<JavaSEVersion>>> data;

    @PostConstruct
    public void init() {
        data = new EnumMap<>(SupportedServer.class);

        fillJavaSEVersion(data, SupportedServer.WILDFLY_SWARM, JavaSEVersion.SE8, null, null);  // Supported for all MPVersions

        fillJavaSEVersion(data, SupportedServer.THORNTAIL_V2, JavaSEVersion.SE8, null, null);  // Supported for all MPVersions
        fillJavaSEVersion(data, SupportedServer.THORNTAIL_V2, JavaSEVersion.SE11, MicroProfileVersion.MP22, null);  // Supported from MP 2.2

        fillJavaSEVersion(data, SupportedServer.QUARKUS, JavaSEVersion.SE8, null, null);  // Supported for all MPVersions

        fillJavaSEVersion(data, SupportedServer.WILDFLY, JavaSEVersion.SE8, null, null);  // Supported for all MPVersions
        fillJavaSEVersion(data, SupportedServer.WILDFLY, JavaSEVersion.SE11, MicroProfileVersion.MP32, null);  // Supported from MP 3.2

        fillJavaSEVersion(data, SupportedServer.LIBERTY, JavaSEVersion.SE8, null, null);  // Supported for all MPVersions
        fillJavaSEVersion(data, SupportedServer.LIBERTY, JavaSEVersion.SE11, MicroProfileVersion.MP12, null);  // Supported from MP 1.2

        fillJavaSEVersion(data, SupportedServer.KUMULUZEE, JavaSEVersion.SE8, null, null);  // Supported for all MPVersions

        fillJavaSEVersion(data, SupportedServer.PAYARA_MICRO, JavaSEVersion.SE8, null, null);  // Supported for all MPVersions
        fillJavaSEVersion(data, SupportedServer.PAYARA_MICRO, JavaSEVersion.SE11, MicroProfileVersion.MP32, null);  // Supported from MP 3.2

        fillJavaSEVersion(data, SupportedServer.TOMEE, JavaSEVersion.SE8, null, null);  // Supported for all MPVersions

        fillJavaSEVersion(data, SupportedServer.HELIDON, JavaSEVersion.SE8, null, MicroProfileVersion.MP30);  // Supported until MP 3.2
        fillJavaSEVersion(data, SupportedServer.HELIDON, JavaSEVersion.SE11, MicroProfileVersion.MP32, null);  // Supported from MP 3.2

    }

    private void fillJavaSEVersion(Map<SupportedServer, Map<MicroProfileVersion, List<JavaSEVersion>>> data,
                                   SupportedServer supportedServer,
                                   JavaSEVersion seVersion,
                                   MicroProfileVersion minVersion,
                                   MicroProfileVersion maxVersion) {

        Map<MicroProfileVersion, List<JavaSEVersion>> specData;
        if (data.containsKey(supportedServer)) {
            specData = data.get(supportedServer);
        } else {
            specData = new EnumMap<>(MicroProfileVersion.class);
            for (MicroProfileVersion version : MicroProfileVersion.values()) {
                specData.put(version, new ArrayList<>());
            }
            data.put(supportedServer, specData);
        }

        for (MicroProfileVersion version : MicroProfileVersion.values()) {
            if (minVersion == null && maxVersion == null) {  // No restriction on MPVersion, add always
                specData.get(version).add(seVersion);
            } else {
                boolean addVersion = false;
                // the checks <= and => seems inverted but that is because the newest MicroProfile versions
                // are added first in the list of MicroProfileVersion.
                if (minVersion != null && version.ordinal() <= minVersion.ordinal()) {
                    addVersion = true;
                }
                if (maxVersion != null && version.ordinal() >= maxVersion.ordinal()) {
                    addVersion = true;
                }

                if (addVersion) {
                    specData.get(version).add(seVersion);
                }
            }
        }
    }

    public List<JavaSEVersion> getSupportedVersion(SupportedServer supportedServer, MicroProfileVersion version) {
        Map<MicroProfileVersion, List<JavaSEVersion>> versionListMap = data.get(supportedServer);

        return versionListMap.get(version);
    }
}
