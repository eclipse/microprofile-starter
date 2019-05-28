package org.eclipse.microprofile.starter.rest.model;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;

import java.util.Collections;
import java.util.List;

public class MPOptionsAvailable {

    private final List<SupportedServer> supportedServers;
    private final List<MicroprofileSpec> specs;

    public MPOptionsAvailable(List<SupportedServer> supportedServers, List<MicroprofileSpec> specs) {
        this.supportedServers = supportedServers;
        this.specs = specs;
    }

    public List<SupportedServer> getSupportedServers() {
        Collections.shuffle(supportedServers);
        return supportedServers;
    }

    public List<MicroprofileSpec> getSpecs() {
        return specs;
    }
}
