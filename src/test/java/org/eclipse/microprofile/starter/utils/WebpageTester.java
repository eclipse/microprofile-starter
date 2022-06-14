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

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class WebpageTester {
    private static final Logger LOGGER = Logger.getLogger(WebpageTester.class);

    /**
     * Patiently try to wait for a web page and examine it
     *
     * @param url             address
     * @param timeoutS        in seconds
     * @param stringToLookFor string must be present on the page
     */
    public static void testWeb(String url, long timeoutS, String stringToLookFor) throws InterruptedException, IOException {
        testWeb(url, timeoutS, stringToLookFor, null);
    }

    /**
     * Patiently try to wait for a web page and examine it
     *
     * @param url             address
     * @param timeoutS        in seconds
     * @param stringToLookFor string must be present on the page
     * @patam postPaylaod     when used, POST this payload
     */
    public static void testWeb(String url, long timeoutS, String stringToLookFor, String postPayload) throws InterruptedException, IOException {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url must not be empty");
        }
        if (timeoutS < 0) {
            throw new IllegalArgumentException("timeoutS must be positive");
        }
        if (StringUtils.isBlank(stringToLookFor)) {
            throw new IllegalArgumentException("stringToLookFor must contain a non-empty string");
        }

        String webPage = "";
        long now = System.currentTimeMillis();
        long startTime = now;
        boolean found = false;
        // Some runtimes give you HTTP 404 or even HTTP 200 with empty response
        // the very moment the web app is being deployed. We patiently wait for the correct result here.
        while (now - startTime < 1000 * timeoutS) {
            HttpURLConnection c = (HttpURLConnection) (new URL(url).openConnection());
            // Server returned HTTP response code: 406 for URL: http://localhost:8080/metrics
            c.setRequestProperty("Accept", "*/*");
            c.setConnectTimeout(500);
            // POST Payload...
            if (postPayload != null) {
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                try (OutputStreamWriter w = new OutputStreamWriter(c.getOutputStream())) {
                    w.write(postPayload);
                    w.flush();
                }
            } else {
                c.setRequestMethod("GET");
            }
            try (Scanner scanner = new Scanner(c.getInputStream(), StandardCharsets.UTF_8.toString())) {
                scanner.useDelimiter("\\A");
                webPage = scanner.hasNext() ? scanner.next() : "";
            } catch (Exception e) {
                LOGGER.info("Waiting `" + stringToLookFor + "' to appear on " + url);
            }
            if (webPage.contains(stringToLookFor)) {
                found = true;
                break;
            }
            Thread.sleep(1000);
            now = System.currentTimeMillis();
        }
        // Test landing page
        assertTrue(found, webPage + " must contain string: `" + stringToLookFor + "'");
    }
}
