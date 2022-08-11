package [# th:text="${java_package}"/].health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Startup;

import [# th:text="${jakarta_ee_package}"/].enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class ServiceStartupHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {

        return HealthCheckResponse.named(ServiceStartupHealthCheck.class.getSimpleName()).withData("startup",true).up().build();

    }
}
