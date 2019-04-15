package [# th:text="${java_package}"/];

[# th:if="${mp_JWT_auth}"]
import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
[/]
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/data")
@ApplicationScoped
[# th:if="${mp_JWT_auth}"]
@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({"protected"})
[/]
public class [# th:text="${application}"/]RestApplication extends Application {
}
