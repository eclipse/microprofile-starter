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

import java.util.regex.Pattern;

/**
 * Whitelists errors in log files.
 *
 * Ideally should be empty :)
 *
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public enum Whitelist {
    ALL("ALL", new Pattern[]{
            Pattern.compile(".*maven-error-diagnostics.*"),
            Pattern.compile(".*error_prone_annotations.*"),
            Pattern.compile(".*error_prone_parent.*"),
    }),
    THORNTAIL_V2("thorntail", new Pattern[]{
            Pattern.compile(".*wildfly-domain-http-error-context.*"),
    }),
    PAYARA_MICRO("payara", new Pattern[]{
            Pattern.compile(".*com.hazelcast.nio.tcp.TcpIpConnectionErrorHandler.*"),
            Pattern.compile(".*com.hazelcast.internal.server.tcp.TcpServerConnectionErrorHandler.*"),
    }),
    LIBERTY("liberty", new Pattern[]{
            Pattern.compile(".*FrameworkEvent ERROR.*"),
            Pattern.compile(".*CWWKE0701E.*"),
            // An exception occurred while stopping the application liberty. The exception message was:
            // java.lang.NoClassDefFoundError: com/ibm/ws/threading/internal/ImmediateFutureImpl
            Pattern.compile(".*CWWKZ0010E:.*"),
            // [ERROR   ] CWWKL0002E: The system could not read class [io.jaegertracing.internal.reporters.RemoteReporter$Command]
            // as resource [io/jaegertracing/internal/reporters/RemoteReporter$Command.class].
            Pattern.compile(".*CWWKL0002E:.*"),
            Pattern.compile(".*Could not load service class com.ibm.ws.io.smallrye.graphql.component.GraphQLExtension.*"),
    }),
    HELIDON("helidon", new Pattern[]{
            // Windows specific. Something with a buffer?
            Pattern.compile(".*Error intercepted before request context established.*"),
    }),
    KUMULUZEE("kumuluzee", new Pattern[]{
            Pattern.compile(".*underlying class loading error: Type Failure to load: com.mongodb.MongoClient not found.*"),
    }),
    TOMEE("tomee", new Pattern[]{}),
    QUARKUS("quarkus", new Pattern[]{
            Pattern.compile(".*\\[org.jboss.threads.errors] Thread Thread\\[build.*"),
            Pattern.compile(".*org/jboss/threads/EnhancedQueueExecutor.*"),
            // If there is nobody to receive traces, there is a log about it.
            Pattern.compile(".*io.jaegertracing.internal.exceptions.SenderException.*"),
    }),
    WILDFLY("wildfly", new Pattern[]{
            Pattern.compile(".*wildfly-domain-http-error-context.*"),
            // Known warning, needs WF update
            Pattern.compile(".*io/netty/util/internal/logging/Log4J2Logger.*"),
            Pattern.compile(".*io.undertow.servlet.handlers.SendErrorPageHandler.*"),
    });

    public final String name;
    public final Pattern[] errs;

    Whitelist(String name, Pattern[] errs) {
        this.name = name;
        this.errs = errs;
    }
}
