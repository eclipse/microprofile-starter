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
package org.eclipse.microprofile.starter;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

/**
 * MicroProfile Starter runtimes API smoke tests.
 *
 * Some rudimentary tests to make sure we ain't breaking the API.
 *
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.WAR)
    public class APITest {

    public static final String API_URL = "http://127.0.0.1:9090/api";
    final Client client = ClientBuilder.newBuilder().build();

    private WebTarget target;
    private File v7Matrix;
    private File v7MatrixServers;
    private File v6Matrix;
    private File v6MatrixServers;
    private File v5Matrix;
    private File v5MatrixServers;
    private File v4Matrix;
    private File v4MatrixServers;
    private File v3Matrix;
    private File v3MatrixServers;

    @Before
    public void before() {
        target = client.target(API_URL);
        v7Matrix = new File(getClass().getClassLoader().getResource("json_examples/v7/supportMatrix.json.segments").getFile());
        v7MatrixServers = new File(getClass().getClassLoader().getResource("json_examples/v7/supportMatrix_servers.json.segments").getFile());
        v6Matrix = new File(getClass().getClassLoader().getResource("json_examples/v6/supportMatrix.json.segments").getFile());
        v6MatrixServers = new File(getClass().getClassLoader().getResource("json_examples/v6/supportMatrix_servers.json.segments").getFile());
        v5Matrix = new File(getClass().getClassLoader().getResource("json_examples/v5/supportMatrix.json.segments").getFile());
        v5MatrixServers = new File(getClass().getClassLoader().getResource("json_examples/v5/supportMatrix_servers.json.segments").getFile());
        v4Matrix = new File(getClass().getClassLoader().getResource("json_examples/v4/supportMatrix.json.segments").getFile());
        v4MatrixServers = new File(getClass().getClassLoader().getResource("json_examples/v4/supportMatrix_servers.json.segments").getFile());
        v3Matrix = new File(getClass().getClassLoader().getResource("json_examples/v3/supportMatrix.json.segments").getFile());
        v3MatrixServers = new File(getClass().getClassLoader().getResource("json_examples/v3/supportMatrix_servers.json.segments").getFile());
    }

    public void test(File segments, String uri) throws FileNotFoundException {
        String response = client.target(API_URL + uri).request().get(String.class);
        try (Scanner scanner = new Scanner(segments, StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\n");
            while (scanner.hasNext()) {
                String l = scanner.nextLine();
                assertTrue("Response of " + uri + " \n" + response + "\n should have contained the string: " + l, response.contains(l));
            }
        }
    }

    @Test
    @RunAsClient
    public void supportMatrix() throws FileNotFoundException {
        test(v6Matrix, "/6/supportMatrix");
        test(v6MatrixServers, "/6/supportMatrix/servers");
        test(v5Matrix, "/5/supportMatrix");
        test(v5MatrixServers, "/5/supportMatrix/servers");
        test(v4Matrix, "/4/supportMatrix");
        test(v4MatrixServers, "/4/supportMatrix/servers");
        test(v3Matrix, "/3/supportMatrix");
        test(v3MatrixServers, "/3/supportMatrix/servers");
    }
}