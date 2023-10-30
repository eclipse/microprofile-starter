/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.eclipse.microprofile.starter.rest;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;
import org.eclipse.microprofile.starter.rest.model.Project;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Overwrites methods that are changed in subsequent APIs
 * to retain former behaviour.
 *
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@Path("/1")
public class APIEndpointV1 extends APIEndpointLatest {

    @Inject
    private APIService api;

    @Path("/supportMatrix")
    @GET
    @Produces({"application/json"})
    @Override
    public Response supportMatrix(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch) {
        return api.supportMatrixV1(ifNoneMatch);
    }

    @Path("/supportMatrix/servers")
    @GET
    @Produces({"application/json"})
    @Override
    public Response supportMatrixServers(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch) {
        return api.supportMatrixServersV1(ifNoneMatch);
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
                               @QueryParam("selectedSpecs") List<String> selectedSpecs) {
        return api.getProjectV1(ifNoneMatch, supportedServer, groupId, artifactId, mpVersion, javaSEVersion, selectedSpecs);
    }

    @Path("/project")
    @POST
    @Consumes({"application/json"})
    @Produces({"application/zip", "application/json"})
    @Override
    public Response projectPost(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch, @NotNull Project body) {
        return api.getProjectV1(ifNoneMatch, body);
    }
}
