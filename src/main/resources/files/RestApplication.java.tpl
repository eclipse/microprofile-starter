package [# th:text="${java_package}"/];

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/data")
public class [# th:text="${application}"/]RestApplication extends Application {
}
