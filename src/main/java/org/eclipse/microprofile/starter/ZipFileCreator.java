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
package org.eclipse.microprofile.starter;

import org.eclipse.microprofile.starter.core.artifacts.FileCreator;

import javax.enterprise.context.SessionScoped;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SessionScoped
public class ZipFileCreator extends FileCreator implements Serializable {

    private Map<String, byte[]> archiveContent = new HashMap<>();

    @Override
    public void writeContents(String directory, String fileName, String contents) {
        archiveContent.put(directory + File.separator + fileName, contents.getBytes());
    }

    @Override
    public void writeContents(String directory, String fileName, byte[] contents) {
        archiveContent.put(directory + File.separator + fileName, contents);
    }

    public byte[] createArchive() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Map.Entry<String, byte[]> entry : archiveContent.entrySet()) {

                ZipEntry zipEntry = new ZipEntry(entry.getKey());

                zos.putNextEntry(zipEntry);
                zos.write(entry.getValue());
                zos.closeEntry();

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        archiveContent.clear();
        return baos.toByteArray();
    }

    public void removeFilesFrom(String directoryPath) {
        archiveContent.entrySet().removeIf(entry -> entry.getKey().startsWith(directoryPath));
    }
}
