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
package org.eclipse.microprofile.starter.validation;

import org.eclipse.microprofile.starter.core.validation.PackageNameValidator;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.CDI;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("packageNameValidator")
public class PackageValidator implements Validator {

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {

        PackageNameValidator validator = retrieveInstance(PackageNameValidator.class);
        if (!validator.isValidPackageName(value.toString())) {
            FacesMessage msg =
                    new FacesMessage(((HtmlInputText) uiComponent).getLabel() + " field validation failed.",
                            "Please provide a valid package name");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(msg);
        }
    }

    /**
     * Retrieve the single CDI instance which has the classType in the bean definition. It throws the standard CDI exceptions
     * in case when there are no or multiple beans which are a candidate for the type.
     *
     * @param classType a {@link java.lang.Class} representing the required type
     * @param <T>       Generic Type argument
     * @return CDI instance matching the class type and qualifiers (if specified).
     * @throws javax.enterprise.inject.AmbiguousResolutionException When more then 1 bean is found in the match
     * @throws UnsatisfiedResolutionException                       When no bean is found in the match.
     */
    public static <T> T retrieveInstance(Class<T> classType) {
        Instance<T> instance = CDI.current().select(classType);
        if (instance.isUnsatisfied()) {
            throw new UnsatisfiedResolutionException(String.format("No bean found for class %s", classType.getName()));
        }
        return instance.get();
    }
}
