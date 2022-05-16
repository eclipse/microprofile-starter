package [# th:text="${java_package}"/];

[# th:if="${mp_JWT_auth}"]
import [# th:text="${java_package}"/].secure.ProtectedController;
import org.eclipse.microprofile.auth.LoginConfig;
import [# th:text="${jakarta_ee_package}"/].annotation.security.DeclareRoles;
[/]
[# th:if="${mp_rest_client}"]
import [# th:text="${java_package}"/].client.ServiceController;
[/]
import [# th:text="${jakarta_ee_package}"/].enterprise.context.ApplicationScoped;
import [# th:text="${jakarta_ee_package}"/].ws.rs.ApplicationPath;
import [# th:text="${jakarta_ee_package}"/].ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@ApplicationPath("/data")
[# th:if="${mp_JWT_auth}"]
@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({"protected"})
[/]
@ApplicationScoped
public class [# th:text="${application}"/]RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {

        Set<Class<?>> classes = new HashSet<>();

        // resources
        [# th:if="${mp_rest_client}"]
        classes.add(ServiceController.class);
        [/]
        [# th:if="${mp_JWT_auth}"]
        classes.add(ProtectedController.class);
        [/]

        return classes;
    }
}
