/*
 * Copyright (c) 2017-2021 Contributors to the Eclipse Foundation
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

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.starter.core.artifacts.FileCreator;

import jakarta.enterprise.context.SessionScoped;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SessionScoped
public class ZipFileCreator extends FileCreator implements Serializable {

    // <Filename : isExecutable> : File contents
    private final Map<Pair<String, Boolean>, byte[]> archiveContent = new HashMap<>();

    @Override
    public void writeContents(String directory, String fileName, String contents, Boolean executable) {
        archiveContent.put(Pair.of(directory + File.separator + fileName, executable), contents.getBytes());
    }

    @Override
    public void writeContents(String directory, String fileName, byte[] contents, Boolean executable) {
        archiveContent.put(Pair.of(directory + File.separator + fileName, executable), contents);
    }

    @Override
    public void writeContents(String directory, String fileName, String contents) {
        archiveContent.put(Pair.of(directory + File.separator + fileName, false), contents.getBytes());
    }

    @Override
    public void writeContents(String directory, String fileName, byte[] contents) {
        archiveContent.put(Pair.of(directory + File.separator + fileName, false), contents);
    }

    public byte[] createArchive() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream archive = new ZipArchiveOutputStream(baos)) {
            for (Map.Entry<Pair<String, Boolean>, byte[]> entry : archiveContent.entrySet()) {
                ZipArchiveEntry zipEntry = new ZipArchiveEntry(entry.getKey().getLeft());
                if (entry.getKey().getRight()) {
                    zipEntry.setUnixMode(0755);
                }
                archive.putArchiveEntry(zipEntry);
                archive.write(entry.getValue());
                archive.closeArchiveEntry();
            }
            archive.finish();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            archiveContent.clear();
        }

        return baos.toByteArray();
    }

    public void removeFilesFrom(String directoryPath) {
        archiveContent.entrySet().removeIf(entry -> entry.getKey().getLeft().startsWith(directoryPath));
    }
}
