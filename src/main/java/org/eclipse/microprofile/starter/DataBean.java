/*
 * Copyright (c) 2017 - 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
@Named
public class DataBean {

    private List<SelectItem> mpItems;

    @PostConstruct
    public void init() {
        defineMPVersions();
    }

    private void defineMPVersions() {
        mpItems = new ArrayList<>();
        for (MicroProfileVersion microProfileVersion : MicroProfileVersion.values()) {
            if (microProfileVersion == MicroProfileVersion.NONE || versionHasImplementations(microProfileVersion)) {
                mpItems.add(new SelectItem(microProfileVersion.getCode(), microProfileVersion.getLabel()));
            }
        }
    }

    private boolean versionHasImplementations(MicroProfileVersion microProfileVersion) {
        return Arrays.stream(SupportedServer.values())
                .anyMatch(server -> server.getMpVersions().contains(microProfileVersion));

    }

    public List<SelectItem> getMpItems() {
        return mpItems;
    }

    public String getHomePage(String value) {
        return SupportedServer.valueFor(value).getHomePage();
    }

    public String getRuntimeName(String value) {

        SupportedServer supportedServer = SupportedServer.valueFor(value);
        return supportedServer == null ? "" : supportedServer.getDisplayName();
    }

    public String getVersionReleasePage(String value) {
        return "https://github.com/eclipse/microprofile/releases/tag/" + value;
    }
}
