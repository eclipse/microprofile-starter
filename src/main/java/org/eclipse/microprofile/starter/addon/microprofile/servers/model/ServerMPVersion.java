/*
 * Copyright (c) 2020 - 2022 Contributors to the Eclipse Foundation
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

public class ServerMPVersion {

    private final SupportedServer supportedServer;
    private final MicroProfileVersion minimalMPVersion;  // minimal MP Version required to show checkbox.
    // Can be null meaning that there is no restriction on the MP Version, only on the Runtime.


    private ServerMPVersion(SupportedServer supportedServer, MicroProfileVersion minimalMPVersion) {
        this.supportedServer = supportedServer;
        this.minimalMPVersion = minimalMPVersion;
    }

    public SupportedServer getSupportedServer() {
        return supportedServer;
    }

    public MicroProfileVersion getMinimalMPVersion() {
        return minimalMPVersion;
    }

    public static ServerMPVersion of(SupportedServer supportedServer) {
        return new ServerMPVersion(supportedServer, null);
    }

    public static ServerMPVersion of(SupportedServer supportedServer, MicroProfileVersion microProfileVersion) {
        return new ServerMPVersion(supportedServer, microProfileVersion);
    }

    public static boolean isEnabled(StandaloneMPSpec spec, String supportedServerCode, MicroProfileVersion microProfileVersion) {

        if (supportedServerCode == null) {
            // If no runtime specified, standalone spec can never be enabled as we can't determine if runtime has support for it.
            return false;
        }
        boolean result = false;
        for (ServerMPVersion serverRestriction : spec.getServerRestrictions()) {
            if (serverRestriction.getSupportedServer().getCode().equals(supportedServerCode)) {
                // This restriction is for the selected runtime
                if (serverRestriction.getMinimalMPVersion() == null) {
                    // No restriction on MP version -> enabled
                    result = true;
                } else {
                    // Current selected version more recenter as MP version defined in restriction.
                    result = serverRestriction.getMinimalMPVersion().ordinal() >= microProfileVersion.ordinal();
                }
            }
        }
        return result;
    }

}
