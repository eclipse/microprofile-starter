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
package org.eclipse.microprofile.starter.rest.model;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.StandaloneMPSpec;
import org.eclipse.microprofile.starter.core.model.BuildTool;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class ServerOptions {

    public final MicroProfileVersion mpVersion;
    public final List<String> mpSpecs;
    public final List<JavaSEVersion> javaSEVersions;
    public final List<String> buildTools;

    public ServerOptions(MicroProfileVersion mpVersion,
                         List<MicroprofileSpec> mpSpecs,
                         List<BuildTool> buildTools,
                         List<StandaloneMPSpec> mpStandaloneSpecs,
                         List<JavaSEVersion> javaSEVersions) {
        this.mpVersion = mpVersion;
        this.mpSpecs =  mpSpecs.stream().map(spec -> spec.getCode().toUpperCase()).collect(Collectors.toList());
        this.buildTools = buildTools.stream().map(Enum::name).collect(Collectors.toList());
        this.mpSpecs.addAll(mpStandaloneSpecs.stream().map(spec -> spec.getCode().toUpperCase()).collect(Collectors.toList()));
        this.javaSEVersions = javaSEVersions;
    }

    public ServerOptions(MicroProfileVersion mpVersion,
                         List<String> mpSpecs,
                         List<JavaSEVersion> javaSEVersions) {
        this.mpVersion = mpVersion;
        this.mpSpecs =  mpSpecs;
        this.buildTools = null;
        this.javaSEVersions = javaSEVersions;
    }
}
