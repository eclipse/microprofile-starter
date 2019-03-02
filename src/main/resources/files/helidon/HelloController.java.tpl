package [# th:text="${java_package}"/];

import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/hello")
@Singleton
@PermitAll
public class HelloController {

    @GET
    public String sayHello() {
        return "Hello World";
    }
}
