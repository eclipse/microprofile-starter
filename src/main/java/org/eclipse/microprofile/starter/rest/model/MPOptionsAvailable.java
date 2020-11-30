/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.rest.model;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.StandaloneMPSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MPOptionsAvailable {

    private final List<SupportedServer> supportedServers;
    private final List<MicroprofileSpec> specs;

    private final List<String> specCodes;

    public MPOptionsAvailable(List<SupportedServer> supportedServers, List<MicroprofileSpec> specs) {
        this.supportedServers = supportedServers;
        this.specs = specs;
        this.specCodes = specs.stream().map(ms-> ms.getCode().toUpperCase()).collect(Collectors.toList());
    }

    public List<SupportedServer> getSupportedServers() {
        Collections.shuffle(supportedServers);
        return supportedServers;
    }

    @JsonbTransient
    public List<MicroprofileSpec> getSpecs() {
        return specs;
    }

    @JsonbProperty("specs")
    public List<String> getSpecCodes() {
        return specCodes;
    }

    public void setStandaloneSpecs(List<StandaloneMPSpec> standaloneSpecs) {
        // We do not have a need to retrieve the list of StandaloneMPSpec yet.
        this.specCodes.addAll(standaloneSpecs.stream().map(ss -> ss.getCode().toUpperCase()).collect(Collectors.toList()));
    }
}
