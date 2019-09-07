/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.dynamologger;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.JessieMaven;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;
import org.eclipse.microprofile.starter.log.DynamoDBLogger;
import org.eclipse.microprofile.starter.view.EngineData;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class DynamoDBLoggerTest {

    @Test
    public void demoLoggingTest() {

        DynamoDBLogger dynamoDBLogger = new DynamoDBLogger();

        JessieMaven jessieMaven = new JessieMaven();
        jessieMaven.setArtifactId("something");
        jessieMaven.setGroupId("com.example.hahah");

        EngineData engineData = new EngineData();
        engineData.setMpVersion(MicroProfileVersion.MP20.getCode());
        engineData.setSupportedServer(SupportedServer.LIBERTY.getCode());
        engineData.setSelectedSpecs(
                Stream.of(MicroprofileSpec.values()).map(MicroprofileSpec::getCode).collect(Collectors.toList()));
        engineData.setMavenData(jessieMaven);

        dynamoDBLogger.log(engineData);
    }
}
