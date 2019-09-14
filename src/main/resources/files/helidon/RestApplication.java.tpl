package [# th:text="${java_package}"/];

[# th:if="${mp_config}"]
import [# th:text="${java_package}"/].config.ConfigTestController;
[/]
[# th:if="${mp_fault_tolerance}"]
import [# th:text="${java_package}"/].resilient.ResilienceController;
[/]
[# th:if="${mp_metrics}"]
import [# th:text="${java_package}"/].metric.MetricController;
[/]
[# th:if="${mp_JWT_auth}"]
import [# th:text="${java_package}"/].secure.TestSecureController;
[/]
[# th:if="${mp_rest_client}"]
import [# th:text="${java_package}"/].client.ClientController;
[/]
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@ApplicationPath("/data")
@ApplicationScoped
public class [# th:text="${application}"/]RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {

        Set<Class<?>> classes = new HashSet<>();

        // resources
        classes.add(HelloController.class);
        [# th:if="${mp_config}"]
        classes.add(ConfigTestController.class);
        [/]
        [# th:if="${mp_fault_tolerance}"]
        classes.add(ResilienceController.class);
        [/]
        [# th:if="${mp_metrics}"]
        classes.add(MetricController.class);
        [/]
        [# th:if="${mp_rest_client}"]
        classes.add(ClientController.class);
        [/]
        [# th:if="${mp_JWT_auth}"]
        classes.add(TestSecureController.class);
        [/]
        return classes;
    }

}
