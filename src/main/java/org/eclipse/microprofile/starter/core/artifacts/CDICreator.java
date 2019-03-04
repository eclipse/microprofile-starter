/*
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.core.artifacts;

import org.eclipse.microprofile.starter.core.model.BeansXMLMode;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.OptionValue;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@ApplicationScoped
public class CDICreator extends AbstractCreator {

    public void createCDIFilesForWeb(JessieModel model) {
        BeansXMLMode mode = getMode(model);
        if (mode == BeansXMLMode.IMPLICIT) {
            // implicit means no beans.xml
            return;
        }
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();
        variables.put("beans_xml_mode", mode.getMode());

        String webInfDirectory = model.getDirectory() + "/" + MavenCreator.SRC_MAIN_WEBAPP + "/WEB-INF";
        directoryCreator.createDirectory(webInfDirectory);

        String beansXMLContents = thymeleafEngine.processFile("beans.xml", alternatives, variables);
        fileCreator.writeContents(webInfDirectory, "beans.xml", beansXMLContents);

    }

    private BeansXMLMode getMode(JessieModel model) {
        OptionValue optionValue = model.getOptions().get(BeansXMLMode.OptionName.NAME);
        BeansXMLMode mode = BeansXMLMode.IMPLICIT;
        if (optionValue != null) {
            mode = BeansXMLMode.getValue(optionValue.getSingleValue());
        }
        return mode;
    }

    public void createCDIFilesForJar(JessieModel model) {
        BeansXMLMode mode = getMode(model);
        if (mode == BeansXMLMode.IMPLICIT) {
            // implicit means no beans.xml
            return;
        }
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();
        variables.put("beans_xml_mode", mode.getMode());

        String metaInfDirectory = model.getDirectory() + "/" + MavenCreator.SRC_MAIN_RESOURCES + "/META-INF";
        directoryCreator.createDirectory(metaInfDirectory);

        String beansXMLContents = thymeleafEngine.processFile("beans.xml", alternatives, variables);
        fileCreator.writeContents(metaInfDirectory, "beans.xml", beansXMLContents);

    }

}
