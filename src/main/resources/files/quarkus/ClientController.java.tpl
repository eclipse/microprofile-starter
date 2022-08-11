package [# th:text="${java_package}"/].client;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import [# th:text="${jakarta_ee_package}"/].enterprise.context.ApplicationScoped;
import [# th:text="${jakarta_ee_package}"/].inject.Inject;
import [# th:text="${jakarta_ee_package}"/].ws.rs.GET;
import [# th:text="${jakarta_ee_package}"/].ws.rs.Path;
import [# th:text="${jakarta_ee_package}"/].ws.rs.PathParam;

@Path("/client")
@ApplicationScoped
public class ClientController {

    @Inject
    @RestClient
    // https://quarkus.io/guides/cdi-reference#private-members
    // - private Service service;
    Service service;

    @GET
    @Path("/test/{parameter}")
    public String onClientSide(@PathParam("parameter") String parameter) {
        return service.doSomething(parameter);
    }
}
