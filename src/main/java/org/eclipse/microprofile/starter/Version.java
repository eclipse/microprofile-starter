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
package org.eclipse.microprofile.starter;

import org.eclipse.microprofile.starter.core.files.FilesLocator;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Scanner;


/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@ApplicationScoped
public class Version {
    private static final Logger LOG = Logger.getLogger(Version.class);

    private String git;

    @PostConstruct
    public void init() {
        try (Scanner s = new Scanner(FilesLocator.class.getClassLoader()
                .getResourceAsStream("/version.txt")).useDelimiter("\\A")) {
            git = s.hasNext() ? s.next() : "";
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public String getGit() {
        return git;
    }
}
    
