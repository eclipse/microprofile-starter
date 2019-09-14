package [# th:text="${java_package}"/].client;
[# th:if="${mp_JWT_auth}"]
import javax.annotation.security.PermitAll;
[/]
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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
