package [# th:text="${java_package}"/].client;

import [# th:text="${jakarta_ee_package}"/].ws.rs.GET;
import [# th:text="${jakarta_ee_package}"/].ws.rs.Path;
import [# th:text="${jakarta_ee_package}"/].ws.rs.PathParam;

@Path("/client/service")
public class ServiceController {

    @GET
    @Path("/{parameter}")
    public String doSomething(@PathParam("parameter") String parameter) {
        return String.format("Processed parameter value '%s'", parameter);
    }
}
