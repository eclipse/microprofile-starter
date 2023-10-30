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
package org.eclipse.microprofile.starter.core.validation;

import org.eclipse.microprofile.starter.core.exception.JessieException;

import jakarta.validation.ConstraintViolation;
import java.util.Set;

/**
 *
 */
public class ModelValidationException extends JessieException {
    public ModelValidationException(Set<ConstraintViolation<Object>> violations) {
        super(defineMessages(violations));
    }

    private static String defineMessages(Set<ConstraintViolation<Object>> violations) {
        StringBuilder result = new StringBuilder();
        for (ConstraintViolation<Object> violation : violations) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(violation.getPropertyPath()).append(' ').append(violation.getMessage());
        }
        return result.toString();
    }
}
