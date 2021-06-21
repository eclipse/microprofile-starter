package org.eclipse.microprofile.starter.readmeparser;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.microprofile.starter.utils.ReadmeParser.parseReadme;
import static org.junit.Assert.assertArrayEquals;

public class ReadmeParserTest {

    @Test
    public void parsingTest() {

        Map<String, String[][]> conf = new HashMap<>();
        conf.put("he-service-a-gradle-readme.md",
                new String[][]{
                        new String[]{"./gradlew", "assemble"},
                        new String[]{"java", "-jar", "build/libs/helidon.jar"},
                        new String[]{"http://localhost:8080/index.html"}
                });
        conf.put("he-service-b-gradle-readme.md",
                new String[][]{
                        new String[]{"./gradlew", "assemble"},
                        new String[]{"java", "-jar", "build/libs/helidon.jar"},
                });
        conf.put("li-service-a-gradle-readme.md",
                new String[][]{
                        new String[]{"./gradlew", "libertyPackage"},
                        new String[]{"./gradlew", "libertyRun", "--no-daemon"},
                        new String[]{"http://localhost:9080/index.html"}
                });
        conf.put("li-service-b-gradle-readme.md",
                new String[][]{
                        new String[]{"./gradlew", "libertyPackage"},
                        new String[]{"./gradlew", "libertyRun", "--no-daemon"},
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
                        new String[]{"./gradlew", "microBundle"},
                        new String[]{"./gradlew", "microStart"},
                        new String[]{"http://localhost:8080/index.html"}
                });
        conf.put("py-service-b-gradle-readme.md",
                new String[][]{
                        new String[]{"./gradlew", "microBundle"},
                        new String[]{"./gradlew", "microStart"},
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
                Assert.fail(e.getMessage());
            }

            assertArrayEquals("Build command parsing failed. Expected: " +
                    Arrays.toString(c[0]) + ", Was:" + Arrays.toString(buildCmdRunCmdWebAddr[0]), c[0], buildCmdRunCmdWebAddr[0]);
            assertArrayEquals("Run command failed. Expected: " +
                    Arrays.toString(c[1]) + ", Was:" + Arrays.toString(buildCmdRunCmdWebAddr[1]), c[1], buildCmdRunCmdWebAddr[1]);
            if (isServiceA) {
                assertArrayEquals("Web address parsing failed Expected: " +
                        Arrays.toString(c[2]) + ", Was:" + Arrays.toString(buildCmdRunCmdWebAddr[2]), c[2], buildCmdRunCmdWebAddr[2]);
            }

        });
    }
}