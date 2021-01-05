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

import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.JessieMaven;

import java.util.List;

public class EngineData {

    public static final String DEFAULT_GROUP_ID = "com.example";
    public static final String DEFAULT_ARTIFACT_ID = "demo";
    public static final JavaSEVersion DEFAULT_JAVA_SE_VERSION = JavaSEVersion.SE8;

    private JessieMaven mavenData;
    private String javaSEVersion = DEFAULT_JAVA_SE_VERSION.getCode();

    private String mpVersion;
    private String supportedServer;
    private String beansxmlMode = "all";
    private List<String> selectedSpecs;

    private String buildTool = "Maven";

    private TrafficSource trafficSource = TrafficSource.WEB;

    public enum TrafficSource {
        WEB,
        REST
    }

    public EngineData() {
        mavenData = new JessieMaven();
        mavenData.setGroupId(DEFAULT_GROUP_ID);
        mavenData.setArtifactId(DEFAULT_ARTIFACT_ID);
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

    public String getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(String buildTool) {
        this.buildTool = buildTool;
    }

    public TrafficSource getTrafficSource() {
        return trafficSource;
    }

    public void setTrafficSource(TrafficSource trafficSource) {
        this.trafficSource = trafficSource;
    }
}
