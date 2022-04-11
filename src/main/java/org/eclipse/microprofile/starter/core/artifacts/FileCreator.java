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
package org.eclipse.microprofile.starter.core.artifacts;

/**
 *
 */

public abstract class FileCreator {

    public abstract void writeContents(String directory, String fileName, String contents);

    public abstract void writeContents(String directory, String fileName, byte[] contents);

    public abstract void writeContents(String directory, String fileName, String contents, Boolean executable);

    public abstract void writeContents(String directory, String fileName, byte[] contents, Boolean executable);

    public void createEmptyFile(String directory, String fileName) {
        writeContents(directory, fileName, "", false);
    }
}
