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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.eclipse.microprofile.starter.core.model.deserializer.OptionsDeserializer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class JessieModel {

    public static final String MAIN_INDICATOR = "service-a";
    public static final String SECONDARY_INDICATOR = "service-b";

    private String topLevelDirectory;
    private String mainDirectory;
    private String secondaryDirectory;

    @NotNull
    @Valid
    private JessieMaven maven;

    private JessieSpecification specification;

    private String template;

    private List<String> addons = new ArrayList<>();

    @JsonDeserialize(using = OptionsDeserializer.class)
    final private Map<String, OptionValue> options = new HashMap<>();

    @JsonIgnore
    final private Map<String, Object> parameters = new HashMap<>();

    @JsonIgnore
    final private Map<String, String> variables = new HashMap<>();

    /**
     * The root directory of the main application (containing the main/demo application)
     * or the secondary application.
     *
     * @return directory location for main or secondary application.
     */
    public String getDirectory(boolean mainProject) {
        return mainProject ? mainDirectory : secondaryDirectory;
    }

    public String getTopLevelDirectory() {
        return topLevelDirectory;
    }

    public void setDirectory(String directory) {
        topLevelDirectory = directory;
        mainDirectory = directory;
        secondaryDirectory = directory;
    }

    public void generateMainAndSecondaryProject() {
        mainDirectory = topLevelDirectory + "/" + MAIN_INDICATOR;
        secondaryDirectory = topLevelDirectory + "/" + SECONDARY_INDICATOR;
    }

    public boolean hasMainAndSecondaryProject() {
        return !mainDirectory.equals(secondaryDirectory);
    }

    public JessieMaven getMaven() {
        return maven;
    }

    public void setMaven(JessieMaven maven) {
        this.maven = maven;
    }

    public JessieSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(JessieSpecification specification) {
        this.specification = specification;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<String> getAddons() {
        if (addons == null) {
            addons = new ArrayList<>();
        }
        return addons;
    }

    public void setAddons(List<String> addons) {
        this.addons = addons;
    }

    public Map<String, OptionValue> getOptions() {
        return options;
    }

    public void addVariable(String name, String value) {
        variables.put(name, value);
    }

    public void addVariables(Map<String, String> variables) {
        variables.forEach(this::addVariable);
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void addParameter(Parameter parameter, Object value) {
        parameters.put(parameter.name(), value);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getParameter(Parameter parameter) {
        return (T) parameters.get(parameter.name());
    }

    public enum Parameter {
        FILENAME, ALTERNATIVES, ADDONS, MICROPROFILESPECS
    }

}
