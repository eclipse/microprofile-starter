package [# th:text="${java_package}"/].secure;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/protected")
@RequestScoped
public class ProtectedController {

    @GET
    @RolesAllowed("protected")
    public String getJWTBasedValue() {
        return "Protected Resource";
    }
}
