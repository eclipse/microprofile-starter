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
 * Contributors:
 *   2018-09-29 - Rudy De Busscher
 *      Initially authored in Atbash Jessie
 */
package org.eclipse.microprofile.starter.spi;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.microprofile.starter.StarterUnexpectedException;

import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 *
 */
@ApplicationScoped
public class MavenHelper {

    public void addDependency(Model pomFile, String groupId, String artifactId, String version) {
        addDependency(pomFile, groupId, artifactId, version, null);
    }

    public void addDependency(Model pomFile, String groupId, String artifactId, String version, String scope) {
        addDependency(pomFile, groupId, artifactId, version, scope, null);
    }

    public void addDependency(Model pomFile, String groupId, String artifactId, String version, String scope, String type) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        if (scope != null) {

            dependency.setScope(scope);
        }
        if (type != null) {
            dependency.setType(type);
        }
        pomFile.addDependency(dependency);

    }

    public Model readModel(String pathToPom) {
        if (StringUtils.isBlank(pathToPom)) {
            throw new IllegalArgumentException("pathToPom must not be blank.");
        }
        Model model;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(MavenHelper.class.getResourceAsStream(pathToPom))))) {
            model = new MavenXpp3Reader().read(in);
        } catch (IOException | XmlPullParserException e) {
            throw new StarterUnexpectedException(e);
        }
        return model;
    }

}
