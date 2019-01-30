package [# th:text="${java_package}"/];
[# th:if="${mp_JWT_auth}"]
import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
[/]
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/data")
[# th:if="${mp_JWT_auth}"]
@LoginConfig(authMethod = "MP-JWT", realmName = "jwt-jaspi")
@DeclareRoles({"protected"})
[/]
public class [# th:text="${artifact}"/]RestApplication extends Application {
}
