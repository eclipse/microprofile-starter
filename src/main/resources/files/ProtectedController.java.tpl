package [# th:text="${java_package}"/].secure;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;

import [# th:text="${jakarta_ee_package}"/].annotation.security.RolesAllowed;
import [# th:text="${jakarta_ee_package}"/].enterprise.context.RequestScoped;
import [# th:text="${jakarta_ee_package}"/].inject.Inject;
import [# th:text="${jakarta_ee_package}"/].ws.rs.GET;
import [# th:text="${jakarta_ee_package}"/].ws.rs.Path;

/**
 *
 */
@Path("/protected")
@RequestScoped
public class ProtectedController {

    @Inject
    @Claim("custom-value")
    private ClaimValue<String> custom;

    @GET
    @RolesAllowed("protected")
    public String getJWTBasedValue() {
        return "Protected Resource; Custom value : " + custom.getValue();
    }
}
