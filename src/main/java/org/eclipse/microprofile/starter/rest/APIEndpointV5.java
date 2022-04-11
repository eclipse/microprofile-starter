/*
 * Copyright (c) 2019 - 2022 Contributors to the Eclipse Foundation
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

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *
 */
@Path("/5")
public class APIEndpointV5 extends APIEndpointLatest {

    @Inject
    APIService api;

    @Path("/project")
    @GET
    @Produces({"application/zip", "application/json"})
    public Response getProject(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                               @QueryParam("supportedServer") SupportedServer supportedServer,
                               @QueryParam("groupId") String groupId,
                               @QueryParam("artifactId") String artifactId,
                               @QueryParam("mpVersion") MicroProfileVersion mpVersion,
                               @QueryParam("javaSEVersion") JavaSEVersion javaSEVersion,
                               @QueryParam("selectedSpecs") List<String> selectedSpecCodes,
                               @QueryParam("selectAllSpecs") boolean selectAllSpecs) {
        return api.getProjectV5(ifNoneMatch,
                supportedServer,
                groupId,
                artifactId,
                mpVersion,
                javaSEVersion,
                selectedSpecCodes,
                selectAllSpecs);
    }

    @Path("/supportMatrix/servers")
    @GET
    @Produces({"application/json"})
    public Response supportMatrixServers(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch) {
        return api.supportMatrixServersV5(ifNoneMatch);
    }
}
