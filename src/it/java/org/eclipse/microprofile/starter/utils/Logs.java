/*
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertTrue;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class Logs {
    private static final Logger LOGGER = Logger.getLogger(Logs.class.getName());

    private static final Pattern ERROR_DETECTION_PATTERN = Pattern.compile("(?i:.*ERROR.*)");

    public static void checkLog(String testClass, String testMethod, String logname, File log) throws IOException {
        Pattern[] whiteList;
        if (testMethod.contains(Whitelist.THORNTAIL_V2.name)) {
            whiteList = Whitelist.THORNTAIL_V2.errs;
        } else if (testMethod.contains(Whitelist.PAYARA_MICRO.name)) {
            whiteList = Whitelist.PAYARA_MICRO.errs;
        } else if (testMethod.contains(Whitelist.LIBERTY.name)) {
            whiteList = Whitelist.LIBERTY.errs;
        } else if (testMethod.contains(Whitelist.HELIDON.name)) {
            whiteList = Whitelist.HELIDON.errs;
        } else if (testMethod.contains(Whitelist.KUMULUZEE.name)) {
            whiteList = Whitelist.KUMULUZEE.errs;
        } else if (testMethod.contains(Whitelist.TOMEE.name)) {
            whiteList = Whitelist.TOMEE.errs;
        } else if (testMethod.contains(Whitelist.QUARKUS.name)) {
            whiteList = Whitelist.QUARKUS.errs;
        } else if (testMethod.contains(Whitelist.WILDFLY.name)) {
            whiteList = Whitelist.WILDFLY.errs;
        } else {
            throw new IllegalArgumentException(
                    "testMethod as matter of convention should always contain lower-case server name, e.g. thorntail");
        }
        try (Scanner sc = new Scanner(log, UTF_8)) {
            Set<String> offendingLines = new HashSet<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                boolean error = ERROR_DETECTION_PATTERN.matcher(line).matches();
                boolean whiteListed = false;
                if (error) {
                    for (Pattern p : whiteList) {
                        if (p.matcher(line).matches()) {
                            whiteListed = true;
                            LOGGER.info(logname + " " + log.getName() + " log for " + testMethod + " contains whitelisted error: `" + line + "'");
                            break;
                        }
                    }
                    if (!whiteListed) {
                        offendingLines.add(line);
                    }
                }
            }
            assertTrue(logname + " " + log.getName() + " log should not contain error or warning lines that are not whitelisted. " +
                    "See target" + File.separator + "archived-logs" +
                    File.separator + testClass + File.separator + testMethod + File.separator + log.getName() +
                    " and check these offending lines: \n" + String.join("\n", offendingLines), offendingLines.isEmpty());
        }
    }

    public static void archiveLog(String testClass, String testMethod, File log) throws IOException {
        if (log == null || !log.exists()) {
            LOGGER.severe("log must be a valid, existing file. Skipping operation.");
            return;
        }
        if (StringUtils.isBlank(testClass)) {
            throw new IllegalArgumentException("testClass must not be blank");
        }
        if (StringUtils.isBlank(testMethod)) {
            throw new IllegalArgumentException("testMethod must not be blank");
        }
        Path destDir = new File(System.getProperties().get("basedir") + File.separator + "target" + File.separator + "archived-logs" + File.separator + testClass + File.separator + testMethod).toPath();
        Files.createDirectories(destDir);
        String filename = log.getName();
        Files.copy(log.toPath(), Paths.get(destDir.toString(), filename));
    }

}
