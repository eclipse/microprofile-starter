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
package org.eclipse.microprofile.starter.spi;

import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.OptionValue;

import java.util.List;
import java.util.Map;

/**
 *
 */

public interface JessieAddon extends JessieAlternativesProvider, JessieMavenAdapter, JessieGradleAdapter {

    String addonName();

    int priority();  // Whe can't use @Priority as the annotation is lost when CDI proxies are created.

    void addonOptions(Map<String, OptionValue> options);

    /**
     * Return the addons on which this addon is dependent. Conditionally when based on the JessieModel
     *
     * @return List of add-on names.
     */
    List<String> getDependentAddons(JessieModel model);

    Map<String, String> getConditionalConfiguration(JessieModel jessieModel, List<JessieAddon> addons);

    void createFiles(JessieModel model);

    void validate(JessieModel model);
}
