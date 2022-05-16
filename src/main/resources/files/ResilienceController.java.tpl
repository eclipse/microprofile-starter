package [# th:text="${java_package}"/].resilient;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;

import [# th:text="${jakarta_ee_package}"/].enterprise.context.ApplicationScoped;
import [# th:text="${jakarta_ee_package}"/].ws.rs.GET;
import [# th:text="${jakarta_ee_package}"/].ws.rs.Path;

@Path("/resilience")
@ApplicationScoped
public class ResilienceController {

    @Fallback(fallbackMethod = "fallback") // better use FallbackHandler
    @Timeout(500)
    @GET
    public String checkTimeout() {
        try {
            Thread.sleep(700L);
        } catch (InterruptedException e) {
            //
        }
        return "Never from normal processing";
    }

    public String fallback() {
        return "Fallback answer due to timeout";
    }
}
