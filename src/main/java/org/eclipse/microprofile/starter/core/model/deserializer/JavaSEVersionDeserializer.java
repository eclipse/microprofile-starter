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
package org.eclipse.microprofile.starter.core.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;

import java.io.IOException;

/**
 *
 */
public class JavaSEVersionDeserializer extends JsonDeserializer<JavaSEVersion> {
    @Override
    public JavaSEVersion deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String propertyValue = jsonParser.getText();
        JavaSEVersion result = JavaSEVersion.valueFor(propertyValue);
        if (result == null) {
            throw new PropertyValueNotSupportedException("javaSE", propertyValue);
        }
        return result;
    }
}
