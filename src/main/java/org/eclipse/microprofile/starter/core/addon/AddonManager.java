/*
 * Copyright (c) 2017-2021 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.core.addon;

import org.eclipse.microprofile.starter.spi.JessieAddon;
import org.eclipse.microprofile.starter.spi.JessieAlternativesProvider;
import org.eclipse.microprofile.starter.spi.JessieGradleAdapter;
import org.eclipse.microprofile.starter.spi.JessieMavenAdapter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
@ApplicationScoped
public class AddonManager {

    @Inject
    private Instance<JessieAddon> addons;

    @Inject
    private Instance<JessieAlternativesProvider> alternativeProviders;

    @Inject
    private Instance<JessieMavenAdapter> mavenAdapters;

    @Inject
    private Instance<JessieGradleAdapter> gradleAdapters;

    public List<JessieAddon> getAddons(String addonName) {
        List<JessieAddon> result = new ArrayList<>();

        for (JessieAddon addon : addons) {
            if (addonName.equalsIgnoreCase(addon.addonName())) {
                // There are not that many addons that a break (or convertion to while) improves performance.
                result.add(addon);
            }
        }

        return result;
    }

    public List<JessieAlternativesProvider> getAlternativeProviders() {

        Iterator<JessieAlternativesProvider> alternativesIterator = alternativeProviders.iterator();
        return getProviders(alternativesIterator);
    }

    public List<JessieMavenAdapter> getMavenAdapters() {

        Iterator<JessieMavenAdapter> mavenAdapterIterator = mavenAdapters.iterator();
        return getProviders(mavenAdapterIterator);
    }

    public List<JessieGradleAdapter> getGradleAdapters() {

        Iterator<JessieGradleAdapter> mavenAdapterIterator = gradleAdapters.iterator();
        return getProviders(mavenAdapterIterator);
    }

    private <T> List<T> getProviders(Iterator<T> alternativesIterator) {
        List<T> result = new ArrayList<>();
        while (alternativesIterator.hasNext()) {
            T provider = alternativesIterator.next();

            //exclude the addons, they are already selected by the addon SPI feature.
            if (!JessieAddon.class.isAssignableFrom(provider.getClass())) {
                result.add(provider);
            }
        }
        return result;
    }
}
