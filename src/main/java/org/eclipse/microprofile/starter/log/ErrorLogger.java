/*
 * Copyright (c) 2019 - 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.PrintWriter;
import java.io.StringWriter;

@ApplicationScoped
public class ErrorLogger {

    private static final Logger LOG = Logger.getLogger(ErrorLogger.class);

    public void logError(Throwable e, JessieModel model) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(model);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        LOG.errorf("Error during generation of project: \n Model : %s \n Stacktrace %s%n", json, stacktrace(e));
    }

    private String stacktrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
