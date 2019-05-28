package org.eclipse.microprofile.starter.rest;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;
import org.eclipse.microprofile.starter.rest.model.Project;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@Path("/1.0.0")
public class APIEndpoint {

    @Inject
    private APIService api;

    @Path("/")
    @GET
    @Produces({"text/x-markdown"})
    public Response readme(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch) {
        return api.readme(ifNoneMatch);
    }

    @Path("/mpVersion")
    @GET
    @Produces({"application/json"})
    public Response listMPVersions() {
        return api.listMPVersions();
    }

    @Path("/mpVersion/{mpVersion}")
    @GET
    @Produces({"application/json"})
    public Response listOptions(@NotNull @PathParam("mpVersion") MicroProfileVersion mpVersion) {
        return api.listOptions(mpVersion);
    }


    @Path("/project")
    @GET
    @Produces({"application/zip", "application/json"})
    public Response getProject(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                               @QueryParam("supportedServer") SupportedServer supportedServer,
                               @QueryParam("groupId") String groupId,
                               @QueryParam("artifactId") String artifactId,
                               @QueryParam("mpVersion") MicroProfileVersion mpVersion,
                               @QueryParam("javaSEVersion") JavaSEVersion javaSEVersion,
                               @QueryParam("selectedSpecs") List<MicroprofileSpec> selectedSpecs) {
        return api.getProject(ifNoneMatch, supportedServer, groupId, artifactId, mpVersion, javaSEVersion, selectedSpecs);
    }

    @Path("/project")
    @POST
    @Consumes({"application/json"})
    @Produces({"application/zip", "application/json"})
    public Response projectPost(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch, @NotNull Project body) {
        return api.getProject(ifNoneMatch, body);
    }
}
