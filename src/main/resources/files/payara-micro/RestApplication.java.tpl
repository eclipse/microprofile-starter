package [# th:text="${java_package}"/];

import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/data")
@LoginConfig(authMethod = "MP-JWT")
@ApplicationScoped
@DeclareRoles({"protected"})
public class [# th:text="${artifact}"/]RestApplication extends Application {
}
