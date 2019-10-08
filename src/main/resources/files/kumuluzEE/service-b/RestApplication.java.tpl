package [# th:text="${java_package}"/];

[# th:if="${mp_JWT_auth}"]
import [# th:text="${java_package}"/].secure.ProtectedController;
import com.kumuluz.ee.jwt.auth.feature.JWTRolesAllowedDynamicFeature;
import com.kumuluz.ee.jwt.auth.filter.JWTAuthorizationFilter;
import org.eclipse.microprofile.auth.LoginConfig;
[/]
[# th:if="${mp_rest_client}"]
import [# th:text="${java_package}"/].client.ServiceController;
[/]

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
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
public class [# th:text="${application}"/]RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {

        Set<Class<?>> classes = new HashSet<>();

        [# th:if="${mp_JWT_auth}"]
        // microprofile jwt auth filters
        classes.add(JWTAuthorizationFilter.class);
        classes.add(JWTRolesAllowedDynamicFeature.class);
        [/]

        // resources

        [# th:if="${mp_JWT_auth}"]
        classes.add(ProtectedController.class);
        [/]
        [# th:if="${mp_rest_client}"]
        classes.add(ServiceController.class);
        [/]

        return classes;
    }
}
