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
import java.util.Collections;
import java.util.List;

public enum SupportedServer {
    // @formatter:off
    WILDFLY_SWARM("wildfly-swarm", "WildFly Swarm",
            Collections.singletonList(MicroProfileVersion.MP12)
            , "%s-swarm.jar" //jarFileName
            , "-Dswarm.port.offset=100" //jarParameters
            , "8080" //portServiceA
            , "8180" //portServiceB
            )
    , THORNTAIL_V2("thorntail-v2", "Thorntail V2",
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13, MicroProfileVersion.MP21,
                    MicroProfileVersion.MP22, MicroProfileVersion.MP30)
            , "%s-thorntail.jar" //jarFileName
            , "-Dswarm.port.offset=100" //jarParameters
            , "8080" //portServiceA
            , "8180" //portServiceB
            )
    , LIBERTY("liberty", "Open Liberty",
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13, MicroProfileVersion.MP14,
                    MicroProfileVersion.MP20, MicroProfileVersion.MP21, MicroProfileVersion.MP22,
                    MicroProfileVersion.MP30)
            , "%s.jar" //jarFileName
            , "" //jarParameters // Hard coded in server.xml since no way of overriding a default.
            , "8181" //portServiceA
            , "8281" //portServiceB
            )
    , KUMULUZEE("kumuluzEE", "KumuluzEE",
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13, MicroProfileVersion.MP14,
                    MicroProfileVersion.MP20, MicroProfileVersion.MP21, MicroProfileVersion.MP22)
            , "%s.jar" //jarFileName
            , "" //jarParameters // Hard coded in config.xml since we needed a specific version for secondary app.
            , "8080" //portServiceA
            , "8180" //portServiceB // This need to match with port value from secondary/config.yaml
            )
    , PAYARA_MICRO("payara-micro", "Payara Micro",
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13, MicroProfileVersion.MP14,
                    MicroProfileVersion.MP20, MicroProfileVersion.MP21, MicroProfileVersion.MP22)
            , "%s-microbundle.jar" //jarFileName
            , "--port 8180" //jarParameters
            , "8080" //portServiceA
            , "8180" //portServiceB // This need to match with port value from defineJarParameters()
            )
    , TOMEE("tomee", "Apache TomEE 8.0.0-M3",
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP13, MicroProfileVersion.MP14,
                    MicroProfileVersion.MP20, MicroProfileVersion.MP21)
            , "%s-exec.jar" //jarFileName
            , "" //jarParameters // Done by TomeeServer.adaptMavenModel
            , "8080" // portServiceA
            , "8180" //portServiceB // This need to match with Port value from TomeeServer.adjustPOM
            )
    , HELIDON("helidon", "Helidon",
            Arrays.asList(MicroProfileVersion.MP12, MicroProfileVersion.MP22)
            , "%s.jar" //jarFileName
            , "" //jarParameters // Done by secondary/helidon/microprofile-config.properties
            , "8080" //portServiceA
            , "8180" //portServiceB  // This need to match Port vcalue from secondary/microprofile-config.proeprties
            );
    // @formatter:on

    private String code;
    private String displayName;
    private List<MicroProfileVersion> mpVersions;
    private String jarFileName;
    private String jarParameters;
    private String portServiceA;
    private String portServiceB;

    SupportedServer(String code, String displayName, List<MicroProfileVersion> mpVersions, String jarFileName
            , String jarParameters, String portServiceA, String portServiceB) {
        this.code = code;
        this.displayName = displayName;
        this.mpVersions = mpVersions;
        this.jarFileName = jarFileName;
        this.jarParameters = jarParameters;
        this.portServiceA = portServiceA;
        this.portServiceB = portServiceB;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<MicroProfileVersion> getMpVersions() {
        return mpVersions;
    }

    public String getJarFileName() {
        return jarFileName;
    }

    public String getJarParameters() {
        return jarParameters;
    }

    public String getPortServiceA() {
        return portServiceA;
    }

    public String getPortServiceB() {
        return portServiceB;
    }

    public static SupportedServer valueFor(String data) {
        SupportedServer result = null;
        for (SupportedServer supportedServer : SupportedServer.values()) {
            if (supportedServer.code.equals(data)) {
                result = supportedServer;
            }
        }
        return result;
    }
}
