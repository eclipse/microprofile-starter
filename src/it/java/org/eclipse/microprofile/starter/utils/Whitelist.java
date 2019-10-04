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
 * Whitelists errors in log files.
 *
 * Ideally should be empty :)
 *
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public enum Whitelist {
    THORNTAIL_V2("thorntail", new String[]{}),
    PAYARA_MICRO("payara", new String[]{}),
    LIBERTY("liberty", new String[]{
            "OpenTracing cannot track JAX-RS requests because an OpentracingTracerFactory class was not provided."
    }),
    HELIDON("helidon", new String[]{}),
    KUMULUZEE("kumuluzee", new String[]{"Copying error_prone_annotations"}),
    TOMEE("tomee", new String[]{});

    public final String name;
    public final String[] errs;

    Whitelist(String name, String[] errs) {
        this.name = name;
        this.errs = errs;
    }
}
