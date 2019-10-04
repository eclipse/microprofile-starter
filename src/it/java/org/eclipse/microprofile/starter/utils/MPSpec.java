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

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public enum MPSpec {
    DEFAULT(new String[][]{
            new String[]{"data/hello", "Hello World"}
    }),
    CONFIG(new String[][]{
            new String[]{"data/config/injected", "Config value as Injected by CDI Injected value"},
            new String[]{"data/config/lookup", "Config value from ConfigProvider lookup value"}
    }),
    FAULT_TOLERANCE(new String[][]{
            new String[]{"data/resilience", "Fallback answer due to timeout"}
    }),
    HEALTH_CHECKS(new String[][]{
            new String[]{"health", "\"UP\""}
    }),
    METRICS(new String[][]{
            new String[]{"data/metric/timed", "Request is used in statistics, check with the Metrics call."},
            // Tomee has "metric_metric_controller", others have "MetricController". This will do.
            new String[]{"metrics", "ontroller_timed_request_seconds_count"}
    }),
    JWT_AUTH(new String[][]{
            // Helidon puts double quotes around the custom claim i.e. Custom value : "Jessie specific value",
            // while other runtimes don't. Check relaxed just to the claim's value.
            new String[]{"data/secured/test", "Jessie specific value"},
    }),
    OPEN_API(new String[][]{
            // Tomee shows only /resilience:, all others show "/data/resilience".
            new String[]{"openapi", "/resilience"}
    }),
    // TODO Install Jaeger?
    //OPEN_TRACING(new String[][]{
    //        new String[]{"URL", "expected string"},
    //        new String[]{"URL 1", "expected string 1"}
    //}),
    REST_CLIENT(new String[][]{
            new String[]{"data/client/test/parameterValue=xxx", "Processed parameter value 'parameterValue=xxx'"}
    });

    public final String[][] urlContent;

    MPSpec(String[][] urlContent) {
        this.urlContent = urlContent;
    }
}
