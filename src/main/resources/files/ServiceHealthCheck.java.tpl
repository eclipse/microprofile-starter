package [# th:text="${java_package}"/].health;

 import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import [# th:text="${jakarta_ee_package}"/].enterprise.context.ApplicationScoped;



@Liveness
@ApplicationScoped
public class ServiceHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {

        return HealthCheckResponse.named(ServiceHealthCheck.class.getSimpleName()).up().build();

    }
}
