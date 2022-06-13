/*
 * Copyright (c) 2017 - 2020 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.eclipse.microprofile.starter.utils.Commands.IS_THIS_WINDOWS;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class ReadmeParser {
    public static String[][] parseReadme(File readme, boolean isServiceA) throws FileNotFoundException {
        String[] buildCommand = null;
        String[] runCommand = null;
        String[] webAddress = null;
        try (Scanner sc = new Scanner(readme)) {
            while (sc.hasNextLine()) {
                if (buildCommand != null && runCommand != null && webAddress != null) {
                    break;
                }
                String line = sc.nextLine();
                if (buildCommand == null && (line.startsWith("    mvn") || line.startsWith("    ./gradlew"))) {
                    buildCommand = line.trim().split(" ");
                    if (IS_THIS_WINDOWS && buildCommand[0].contains("gradlew")) {
                        buildCommand[0] = "gradlew.bat";
                    }
                    continue;
                }
                if (runCommand == null && (line.startsWith("    java") || line.startsWith("    ./gradlew"))) {
                    runCommand = line.trim().split(" ");
                    if (IS_THIS_WINDOWS && runCommand[0].contains("gradlew")) {
                        runCommand[0] = "gradlew.bat";
                    }
                    continue;
                }
                if (webAddress == null && isServiceA && line.startsWith("    http://")) {
                    webAddress = line.trim().split(" ");
                }
            }
        }
        if (isServiceA) {
            return new String[][]{buildCommand, runCommand, webAddress};
        }
        return new String[][]{buildCommand, runCommand};
    }
}
