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
package org.eclipse.microprofile.starter.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.eclipse.microprofile.starter.core.model.deserializer.*;

import java.util.List;

/**
 *
 */
public class JessieSpecification {

    @JsonProperty(value = "MP")
    @JsonDeserialize(using = MicroProfileVersionDeserializer.class)
    private MicroProfileVersion microProfileVersion;

    @JsonProperty(value = "javaSE")
    @JsonDeserialize(using = JavaSEVersionDeserializer.class)
    private JavaSEVersion javaSEVersion;

    @JsonProperty(value = "structure")
    @JsonDeserialize(using = ModuleDeserializer.class)
    private ModuleStructure moduleStructure;

    @JsonDeserialize(using = ViewTypeDeserializer.class)
    private List<ViewType> views;

    public MicroProfileVersion getMicroProfileVersion() {
        return microProfileVersion;
    }

    public void setMicroProfileVersion(MicroProfileVersion microProfileVersion) {
        this.microProfileVersion = microProfileVersion;
    }

    public JavaSEVersion getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(JavaSEVersion javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
    }

    public ModuleStructure getModuleStructure() {
        return moduleStructure;
    }

    public void setModuleStructure(ModuleStructure moduleStructure) {
        this.moduleStructure = moduleStructure;
    }

    public List<ViewType> getViews() {
        return views;
    }

    public void setViews(List<ViewType> views) {
        this.views = views;
    }

}
