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

import org.eclipse.microprofile.starter.core.exception.TechnicalException;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;

/**
 *
 */
@ApplicationScoped
public class JessieModelInitializer {

    public void defineDefaults(JessieModel model, boolean localExecution) {
        checkDirectory(model, localExecution);

        defineTemplate(model);

        if (!model.getOptions().containsKey(BeansXMLMode.OptionName.NAME)) {
            model.getOptions().put(BeansXMLMode.OptionName.NAME, new OptionValue(BeansXMLMode.ANNOTATED.getMode()));
        }
    }

    private void checkDirectory(JessieModel model, boolean localExecution) {
        if (model.getDirectory(true) == null) {
            String modelFileName = model.getParameter(JessieModel.Parameter.FILENAME);
            model.setDirectory(getDirectoryFromModelFileName(modelFileName));
        }

        if (localExecution) {
            File file = new File(model.getDirectory(true));
            try {
                model.setDirectory(file.getCanonicalPath());
            } catch (IOException e) {
                throw new TechnicalException(e);
            }
        }
    }

    private String getDirectoryFromModelFileName(String modelFileName) {
        String result = modelFileName;
        int length = modelFileName.length();
        if (modelFileName.endsWith(".yaml") && length > 5) {
            result = "./" + modelFileName.substring(0, length - 5);
        }
        return result;
    }

    private void defineTemplate(JessieModel model) {

        model.setTemplate("defaultMP");
    }
}
