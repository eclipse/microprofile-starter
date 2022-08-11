package [# th:text="${java_package}"/].health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import [# th:text="${jakarta_ee_package}"/].enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class ServiceLiveHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {

        return HealthCheckResponse.named(ServiceLiveHealthCheck.class.getSimpleName()).withData("live",true).up().build();

    }
}
