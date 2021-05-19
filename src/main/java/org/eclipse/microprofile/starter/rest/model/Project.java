/*
 * Copyright (c) 2019-2021 Contributors to the Eclipse Foundation
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
import org.eclipse.microprofile.starter.core.model.BuildTool;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class Project {
    private String groupId = null;
    private String artifactId = null;
    private MicroProfileVersion mpVersion = null;
    private JavaSEVersion javaSEVersion = null;
    private SupportedServer supportedServer = null;
    private List<String> selectedSpecs = null;
    private List<MicroprofileSpec> selectedSpecEnums;
    private List<StandaloneMPSpec> selectedStandaloneSpecs = null;
    private BuildTool buildTool = BuildTool.MAVEN;
    private boolean selectAllSpecs = false;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public MicroProfileVersion getMpVersion() {
        return mpVersion;
    }

    public void setMpVersion(MicroProfileVersion mpVersion) {
        this.mpVersion = mpVersion;
    }

    public JavaSEVersion getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(JavaSEVersion javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
    }

    public SupportedServer getSupportedServer() {
        return supportedServer;
    }

    public void setSupportedServer(SupportedServer supportedServer) {
        this.supportedServer = supportedServer;
    }

    public List<String> getSelectedSpecs() {
        return selectedSpecs;
    }

    public List<MicroprofileSpec> getSelectedSpecEnums() {
        return selectedSpecEnums;
    }

    public void setSelectedSpecs(List<String> selectedSpecs) {
        this.selectedSpecs = selectedSpecs;
    }

    public void setSelectedSpecEnums(List<MicroprofileSpec> selectedSpecs) {
        this.selectedSpecEnums = selectedSpecs;
        this.selectedSpecs = selectedSpecs.stream().map(MicroprofileSpec::getCode).collect(Collectors.toList());
    }

    public List<StandaloneMPSpec> getSelectedStandaloneSpecs() {
        return selectedStandaloneSpecs;
    }

    public void setSelectedStandaloneSpecs(List<StandaloneMPSpec> selectedStandaloneSpecs) {
        this.selectedStandaloneSpecs = selectedStandaloneSpecs;
    }

    public boolean isSelectAllSpecs() {
        return selectAllSpecs;
    }

    public void setSelectAllSpecs(boolean selectAllSpecs) {
        this.selectAllSpecs = selectAllSpecs;
    }

    public BuildTool getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(BuildTool buildTool) {
        this.buildTool = buildTool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Project project = (Project) o;
        return Objects.equals(groupId, project.groupId) &&
                Objects.equals(artifactId, project.artifactId) &&
                mpVersion == project.mpVersion &&
                javaSEVersion == project.javaSEVersion &&
                supportedServer == project.supportedServer &&
                buildTool == project.buildTool &&
                selectAllSpecs == project.selectAllSpecs &&
                Objects.equals(selectedSpecs, project.selectedSpecs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, mpVersion, javaSEVersion, supportedServer, buildTool, selectedSpecs, selectAllSpecs);
    }
}
