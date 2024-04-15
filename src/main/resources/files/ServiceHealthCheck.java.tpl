package [# th:text="${java_package}"/].health;

import org.eclipse.microprofile.health.[# th:text="${microprofile_health}"/];
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import [# th:text="${jakarta_ee_package}"/].enterprise.context.ApplicationScoped;



@[# th:text="${microprofile_health}"/]
@ApplicationScoped
public class ServiceHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {

        return HealthCheckResponse.named(ServiceHealthCheck.class.getSimpleName()).up().build();

    }
}
