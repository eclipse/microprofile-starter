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
package org.eclipse.microprofile.starter.log;

import org.eclipse.microprofile.starter.view.EngineData;

import javax.enterprise.inject.spi.CDI;

public class LoggingTask implements Runnable {

    private final EngineData engineData;

    public LoggingTask(EngineData engineData) {
        this.engineData = engineData;
    }

    @Override
    public void run() {
        DynamoDBLogger logger = CDI.current().select(DynamoDBLogger.class).get();
        logger.log(engineData);
    }
}
