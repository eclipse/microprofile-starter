package [# th:text="${java_package}"/];

import [# th:text="${jakarta_ee_package}"/].ws.rs.ApplicationPath;
import [# th:text="${jakarta_ee_package}"/].ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/data")
public class [# th:text="${application}"/]RestApplication extends Application {
}
