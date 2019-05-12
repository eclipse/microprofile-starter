package org.eclipse.microprofile.starter.addon.microprofile.servers;

import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.spi.AbstractAddon;
import org.eclipse.microprofile.starter.spi.JessieAddon;
import org.eclipse.microprofile.starter.spi.MavenHelper;

import javax.inject.Inject;
import java.util.*;

public abstract class AbstractMicroprofileAddon extends AbstractAddon {

    @Inject
    protected MavenHelper mavenHelper;

    public void init() {
        defaultOptions = new HashMap<>();
    }

    @Override
    public int priority() {
        return 70;
    }

    @Override
    public Map<String, String> getConditionalConfiguration(JessieModel jessieModel, List<JessieAddon> addons) {

        return defaultOptions;
    }

    @Override
    public List<String> getDependentAddons(JessieModel model) {
        return Collections.emptyList();
    }

    @Override
    public Set<String> alternativesNames(JessieModel model) {
        return Collections.emptySet();
    }


}
