package org.eclipse.microprofile.starter.log;

import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ManagedExecutorProducer {

    @Produces
    @AppContext
    ManagedExecutor createExecutor() {
        return ManagedExecutor.builder().build();
    }

    void disposeExecutor(@Disposes @AppContext ManagedExecutor exec) {
        exec.shutdownNow();
    }
}
