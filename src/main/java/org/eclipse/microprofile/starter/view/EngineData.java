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
package org.eclipse.microprofile.starter.view;

import org.eclipse.microprofile.starter.core.model.JessieMaven;

import java.util.List;

public class EngineData {

    private JessieMaven mavenData;
    private String javaSEVersion = "1.8";

    private String mpVersion;
    private String supportedServer;
    private String beansxmlMode;
    private List<String> selectedSpecs;

    public EngineData() {
        mavenData = new JessieMaven();
        mavenData.setGroupId("com.example");
        mavenData.setArtifactId("demo");
    }

    public JessieMaven getMavenData() {
        return mavenData;
    }

    public void setMavenData(JessieMaven mavenData) {
        this.mavenData = mavenData;
    }

    public String getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(String javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
    }

    public String getMpVersion() {
        return mpVersion;
    }

    public void setMpVersion(String mpVersion) {
        this.mpVersion = mpVersion;
    }

    public String getSupportedServer() {
        return supportedServer;
    }

    public void setSupportedServer(String supportedServer) {
        this.supportedServer = supportedServer;
    }

    public String getBeansxmlMode() {
        return beansxmlMode;
    }

    public void setBeansxmlMode(String beansxmlMode) {
        this.beansxmlMode = beansxmlMode;
    }

    public List<String> getSelectedSpecs() {
        return selectedSpecs;
    }

    public void setSelectedSpecs(List<String> selectedSpecs) {
        this.selectedSpecs = selectedSpecs;
    }
}
