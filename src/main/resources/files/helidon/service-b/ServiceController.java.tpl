package [# th:text="${java_package}"/].client;
[# th:if="${mp_JWT_auth}"]
import [# th:text="${jakarta_ee_package}"/].annotation.security.PermitAll;
[/]
import [# th:text="${jakarta_ee_package}"/].ws.rs.GET;
import [# th:text="${jakarta_ee_package}"/].ws.rs.Path;
import [# th:text="${jakarta_ee_package}"/].ws.rs.PathParam;

@Path("/client/service")
public class ServiceController {

    @GET
    @Path("/{parameter}")
    [# th:if="${mp_JWT_auth}"]
    @PermitAll
    [/]
    public String doSomething(@PathParam("parameter") String parameter) {
        return String.format("Processed parameter value '%s'", parameter);
    }
}
