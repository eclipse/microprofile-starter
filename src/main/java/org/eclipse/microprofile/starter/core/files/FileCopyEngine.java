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
package org.eclipse.microprofile.starter.core.files;

import com.google.common.io.ByteStreams;
import org.eclipse.microprofile.starter.core.exception.TechnicalException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 *
 */
@ApplicationScoped
public class FileCopyEngine {

    @Inject
    private FilesLocator filesLocator;

    public byte[] processFile(String file, Set<String> alternatives) {

        String fileIndication = filesLocator.findFile(file, alternatives);

        if ("-1".equals(fileIndication)) {
            throw new FileResolutionException(file, alternatives);
        }

        String sourceFile = "/" + filesLocator.getTemplateFile(fileIndication);

        InputStream resource = FileCopyEngine.class.getResourceAsStream(sourceFile);
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        try {
            ByteStreams.copy(resource, result);
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
        return result.toByteArray();
    }

}
