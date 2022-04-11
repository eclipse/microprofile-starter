/*
 * Copyright (c) 2019 - 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.readmeparser;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.microprofile.starter.utils.Commands.IS_THIS_WINDOWS;
import static org.eclipse.microprofile.starter.utils.ReadmeParser.parseReadme;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ReadmeParserTest {

    @Test
    public void parsingTest() {

        Map<String, String[][]> conf = new HashMap<>();
        conf.put("he-service-a-gradle-readme.md",
                new String[][]{
                        new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "assemble"},
                        new String[]{"java", "-jar", "build/libs/helidon.jar"},
                        new String[]{"http://localhost:8080/index.html"}
                });
        conf.put("he-service-b-gradle-readme.md",
                new String[][]{
                        new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "assemble"},
                        new String[]{"java", "-jar", "build/libs/helidon.jar"},
                });
        conf.put("li-service-a-gradle-readme.md",
                new String[][]{
                        new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "libertyPackage"},
                        new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "libertyRun", "--no-daemon"},
                        new String[]{"http://localhost:9080/index.html"}
                });
        conf.put("li-service-b-gradle-readme.md",
                new String[][]{
                        new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "libertyPackage"},
                        new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "libertyRun", "--no-daemon"},
                });
        conf.put("li-service-a-readme.md",
                new String[][]{
                        new String[]{"mvn", "clean", "package"},
                        new String[]{"java", "-jar", "target/liberty.jar"},
                        new String[]{"http://localhost:8181/index.html"}
                });
        conf.put("li-service-b-readme.md",
                new String[][]{
                        new String[]{"mvn", "clean", "package"},
                        new String[]{"java", "-jar", "target/liberty.jar"}
                });
        conf.put("py-service-a-gradle-readme.md",
                new String[][]{
                        new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "microBundle"},
                        new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "microStart"},
                        new String[]{"http://localhost:8080/index.html"}
                });
        conf.put("py-service-b-gradle-readme.md",
                new String[][]{
                        new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "microBundle"},
                        new String[]{IS_THIS_WINDOWS ? "gradlew.bat" : "./gradlew", "microStart"},
                });
        conf.put("py-service-a-readme.md",
                new String[][]{
                        new String[]{"mvn", "clean", "package"},
                        new String[]{"java", "-jar", "target/payara-microbundle.jar"},
                        new String[]{"http://localhost:8080/index.html"}
                });
        conf.put("py-service-b-readme.md",
                new String[][]{
                        new String[]{"mvn", "clean", "package"},
                        new String[]{"java", "-jar", "target/payara-microbundle.jar", "--port", "8180"}
                });
        conf.put("q-service-a-readme.md",
                new String[][]{
                        new String[]{"mvn", "clean", "compile", "quarkus:build"},
                        new String[]{"java", "-jar", "target/quarkus-runner.jar"},
                        new String[]{"http://localhost:8080/index.html"}
                });
        conf.put("q-service-b-readme.md",
                new String[][]{
                        new String[]{"mvn", "clean", "compile", "quarkus:build"},
                        new String[]{"java", "-Dquarkus.http.port=8180", "-jar", "target/quarkus-runner.jar"}
                });
        conf.put("th-service-a-readme.md",
                new String[][]{
                        new String[]{"mvn", "clean", "package"},
                        new String[]{"java", "-jar", "target/thorntail-thorntail.jar"},
                        new String[]{"http://localhost:8080/index.html"}
                });
        conf.put("th-service-b-readme.md",
                new String[][]{
                        new String[]{"mvn", "clean", "package"},
                        new String[]{"java", "-jar", "target/thorntail-thorntail.jar", "-Dswarm.port.offset=100"}
                });

        conf.forEach((f, c) -> {
            File readme = new File(
                    getClass().getClassLoader().getResource("readme_examples/" + f).getFile()
            );

            boolean isServiceA = f.contains("-a-");
            String[][] buildCmdRunCmdWebAddr = new String[0][];
            try {
                buildCmdRunCmdWebAddr = parseReadme(readme, isServiceA);
            } catch (FileNotFoundException e) {
                fail(e.getMessage());
            }

            assertArrayEquals(c[0], buildCmdRunCmdWebAddr[0], "Build command parsing failed. Expected: " +
                    Arrays.toString(c[0]) + ", Was:" + Arrays.toString(buildCmdRunCmdWebAddr[0]));
            assertArrayEquals(c[1], buildCmdRunCmdWebAddr[1], "Run command failed. Expected: " +
                    Arrays.toString(c[1]) + ", Was:" + Arrays.toString(buildCmdRunCmdWebAddr[1]));
            if (isServiceA) {
                assertArrayEquals(c[2], buildCmdRunCmdWebAddr[2], "Web address parsing failed Expected: " +
                        Arrays.toString(c[2]) + ", Was:" + Arrays.toString(buildCmdRunCmdWebAddr[2]));
            }
        });
    }
}
