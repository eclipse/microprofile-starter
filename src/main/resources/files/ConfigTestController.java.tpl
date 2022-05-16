package [# th:text="${java_package}"/].config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import [# th:text="${jakarta_ee_package}"/].enterprise.context.RequestScoped;
import [# th:text="${jakarta_ee_package}"/].inject.Inject;
import [# th:text="${jakarta_ee_package}"/].ws.rs.GET;
import [# th:text="${jakarta_ee_package}"/].ws.rs.Path;

@Path("/config")
@RequestScoped
public class ConfigTestController {

    @Inject
    @ConfigProperty(name = "injected.value")
    private String injectedValue;

    @Path("/injected")
    @GET
    public String getInjectedConfigValue() {
        return "Config value as Injected by CDI " + injectedValue;
    }

    @Path("/lookup")
    @GET
    public String getLookupConfigValue() {
        Config config = ConfigProvider.getConfig();
        String value = config.getValue("value", String.class);
        return "Config value from ConfigProvider " + value;
    }
}
