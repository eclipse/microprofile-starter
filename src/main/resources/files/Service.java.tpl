package [# th:text="${java_package}"/].client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import [# th:text="${jakarta_ee_package}"/].enterprise.context.ApplicationScoped;
import [# th:text="${jakarta_ee_package}"/].ws.rs.GET;
import [# th:text="${jakarta_ee_package}"/].ws.rs.Path;
import [# th:text="${jakarta_ee_package}"/].ws.rs.PathParam;

@RegisterRestClient
@ApplicationScoped
public interface Service {

    @GET
    @Path("/{parameter}")
    String doSomething(@PathParam("parameter") String parameter);

}
