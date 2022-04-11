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
 */
package org.eclipse.microprofile.starter;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MicroProfile Starter runtimes API smoke tests.
 *
 * Some rudimentary tests to make sure we ain't breaking the API.
 *
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@QuarkusTest
public class APIITCase {

    @ParameterizedTest
    @ValueSource(strings = {
            "/6/supportMatrix          █/json_examples/v6/supportMatrix.json.segments",
            "/6/supportMatrix/servers  █/json_examples/v6/supportMatrix_servers.json.segments",
            "/5/supportMatrix          █/json_examples/v5/supportMatrix.json.segments",
            "/5/supportMatrix/servers  █/json_examples/v5/supportMatrix_servers.json.segments",
            "/4/supportMatrix          █/json_examples/v4/supportMatrix.json.segments",
            "/4/supportMatrix/servers  █/json_examples/v4/supportMatrix_servers.json.segments",
            "/3/supportMatrix          █/json_examples/v3/supportMatrix.json.segments",
            "/3/supportMatrix/servers  █/json_examples/v3/supportMatrix_servers.json.segments",
    })
    public void supportMatrixAPI(String testData) throws FileNotFoundException {
        final String[] apiURIjson = testData.split("█");
        final String url = apiURIjson[0].trim();
        final File file = new File(APIITCase.class.getResource(apiURIjson[1].trim()).getFile());
        final String response = given().get("/api" + url).asString();
        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\n");
            while (scanner.hasNext()) {
                String l = scanner.nextLine();
                assertTrue(response.contains(l), "Response of " + url + " \n" + response + "\n should have contained the string: " + l);
            }
        }
    }

    /**
     * It could happen that REST API works, but JSF doesn't even start.
     * This smoke test makes sure it fails TS.
     *
     * LambdaConversionException: Invalid caller: org.eclipse.microprofile.starter.Version_ClientProxy
     * LambdaConversionException: Invalid caller: org.eclipse.microprofile.starter.DataBean_ClientProxy
     * LambdaConversionException: Invalid caller: org.eclipse.microprofile.starter.core.model.JessieMaven
     * LambdaConversionException: Invalid caller: org.eclipse.microprofile.starter.view.EngineData
     * LambdaConversionException: Invalid caller: org.eclipse.microprofile.starter.view.GeneratorDataBean_ClientProxy
     *
     * The aforementioned exceptions are related to the fact that a plain "curl" like reader does not execute
     * dynamic elements and thus the JSF gets confused. It does not happen in production with a normal browser.
     *
     * @param testData
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "/            █Examples for specifications",
            "/index.xhtml █data-label=\"MP 4.0\"",
            "/            █data-label=\"MP 3.3\"",
            "/            █data-label=\"Payara Micro\""
    })
    public void webSmokeTest(String testData) throws IOException {
        final String[] urlContent = testData.split("█");
        final String url = urlContent[0].trim();
        final String expected = urlContent[1].trim();
        given()
                .when().get(url)
                .then()
                .statusCode(200)
                .body(containsString(expected));
    }
}
