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
package org.eclipse.microprofile.starter.core.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.microprofile.starter.core.model.OptionValue;

import java.io.IOException;
import java.util.*;

public class OptionsDeserializer extends JsonDeserializer<Map<String, OptionValue>> {
    @Override
    public Map<String, OptionValue> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Map<String, OptionValue> result = new HashMap<>();
        ObjectNode node = jsonParser.readValueAsTree();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            OptionValue optionValue;
            if (entry.getValue().isArray()) {
                List<String> values = new ArrayList<>();
                for (JsonNode jsonNode : entry.getValue()) {
                    values.add(jsonNode.asText());
                }
                optionValue = new OptionValue(values);

            } else {
                optionValue = new OptionValue(entry.getValue().asText());
            }
            result.put(entry.getKey(), optionValue);

        }
        return result;
    }
}
