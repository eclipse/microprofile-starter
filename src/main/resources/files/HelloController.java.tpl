package [# th:text="${java_package}"/];

import [# th:text="${jakarta_ee_package}"/].inject.Singleton;
import [# th:text="${jakarta_ee_package}"/].ws.rs.GET;
import [# th:text="${jakarta_ee_package}"/].ws.rs.Path;

/**
 *
 */
@Path("/hello")
@Singleton
public class HelloController {

    @GET
    public String sayHello() {
        return "Hello World";
    }
}
