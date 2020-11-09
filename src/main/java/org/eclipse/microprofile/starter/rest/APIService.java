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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.starter.Version;
import org.eclipse.microprofile.starter.ZipFileCreator;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.JDKSelector;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.artifacts.Creator;
import org.eclipse.microprofile.starter.core.files.FilesLocator;
import org.eclipse.microprofile.starter.core.model.BeansXMLMode;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.JessieMaven;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.JessieSpecification;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;
import org.eclipse.microprofile.starter.core.model.ModelManager;
import org.eclipse.microprofile.starter.core.model.OptionValue;
import org.eclipse.microprofile.starter.core.validation.PackageNameValidator;
import org.eclipse.microprofile.starter.log.ErrorLogger;
import org.eclipse.microprofile.starter.log.LoggingTask;
import org.eclipse.microprofile.starter.rest.model.MPOptionsAvailable;
import org.eclipse.microprofile.starter.rest.model.Project;
import org.eclipse.microprofile.starter.rest.model.ServerOptions;
import org.eclipse.microprofile.starter.view.EngineData;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@ApplicationScoped
public class APIService {

    private static final Logger LOG = Logger.getLogger(APIService.class.getName());

    private Map<String, String> specsDescriptions;

    private TreeMap<MicroProfileVersion, MPOptionsAvailable> mpvToOptions;
    private EntityTag mpvToOptionsEtag;

    private Map<SupportedServer, List<ServerOptions>> serversToOptions;
    private EntityTag serversToOptionsEtag;

    private String readme;
    private EntityTag readmeEtag;

    @Inject
    private JDKSelector selector;

    @Inject
    private Version version;

    @Inject
    private PackageNameValidator packageNameValidator;

    @Inject
    private ErrorLogger errorLogger;

    @PostConstruct
    public void init() {
        specsDescriptions = Stream.of(MicroprofileSpec.values())
                .collect(Collectors.toMap(MicroprofileSpec::toString, MicroprofileSpec::getDescription));
        // Keys are MP versions and values are servers and specs
        mpvToOptions = new TreeMap<>();
        Stream.of(MicroProfileVersion.values()).filter(mpv -> mpv != MicroProfileVersion.NONE).sorted().forEach(mpv -> {
            List<SupportedServer> supportedServers = Stream.of(SupportedServer.values())
                    .filter(v -> v.getMpVersions().contains(mpv)).collect(Collectors.toList());
            List<MicroprofileSpec> specs = Stream.of(MicroprofileSpec.values())
                    .filter(v -> v.getMpVersions().contains(mpv)).collect(Collectors.toList());
            mpvToOptions.put(mpv, new MPOptionsAvailable(supportedServers, specs));
        });
        mpvToOptionsEtag = new EntityTag(Integer.toHexString(
                31 * version.getGit().hashCode() + mpvToOptions.hashCode() + specsDescriptions.hashCode()));
        // Keys are servers and values are MP versions and specs
        serversToOptions = new HashMap<>(SupportedServer.values().length);
        for (SupportedServer s : SupportedServer.values()) {
            List<ServerOptions> serverOptions = new ArrayList<>();
            s.getMpVersions().forEach(mpv -> {
                List<MicroprofileSpec> mpSpec = Stream.of(MicroprofileSpec.values())
                        .filter(v -> v.getMpVersions().contains(mpv)).collect(Collectors.toList());

                List<JavaSEVersion> supportedJavaVersions = selector.getSupportedVersion(s, mpv);

                serverOptions.add(new ServerOptions(mpv, mpSpec, supportedJavaVersions));
            });
            serversToOptions.put(s, serverOptions);
        }
        serversToOptionsEtag = new EntityTag(Integer.toHexString(
                31 * version.getGit().hashCode() + serversToOptions.hashCode() + specsDescriptions.hashCode()));
        try (Scanner s = new Scanner(FilesLocator.class.getClassLoader()
                .getResourceAsStream("/REST-README.md")).useDelimiter("\\A")) {
            readme = (s.hasNext() ? s.next() : "") + "\n" + version.getGit() + "\n";
            readmeEtag = new EntityTag(Integer.toHexString(readme.hashCode()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
    }

    public static final String ERROR001 =
            "{\"error\":\"supportedServer query parameter is mandatory\",\"code\":\"ERROR001\"}";
    public static final String ERROR002 =
            "{\"error\":\"Selected supportedServer is not available for the given mpVersion\",\"code\":\"ERROR002\"}";
    public static final String ERROR003 =
            "{\"error\":\"One or more selectedSpecs is not available for the given mpVersion\",\"code\":\"ERROR003\"}";
    public static final String ERROR004 =
            "{\"error\":\"groupId contains illegal characters, does not start with a word or is longer than " +
                    PackageNameValidator.MAX_LENGTH + "\",\"code\":\"ERROR004\"}";
    public static final String ERROR005 =
            "{\"error\":\"artifactId contains illegal characters, does not start with a word or is longer than " +
                    PackageNameValidator.MAX_LENGTH + "\",\"code\":\"ERROR005\"}";

    @Inject
    private ModelManager modelManager;

    @Inject
    private Creator creator;

    @Inject
    private ZipFileCreator zipFileCreator;

    @Resource
    private ManagedExecutorService managedExecutorService;

    public Response readme(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (readmeEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        return Response.ok(readme).tag(readmeEtag).build();
    }

    public Response listMPVersions() {
        return Response.ok(
                // We don't want to return NONE as a viable option
                Stream.of(MicroProfileVersion.values())
                        .filter(mpv -> mpv != MicroProfileVersion.NONE)
                        .collect(Collectors.toList()), MediaType.APPLICATION_JSON_TYPE
        ).build();
    }

    public Response supportMatrixV1(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (mpvToOptionsEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        return Response.ok(mpvToOptions, MediaType.APPLICATION_JSON_TYPE).tag(mpvToOptionsEtag).build();
    }

    public Response supportMatrix(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (mpvToOptionsEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        Map<String, Map> mpvToOptionsAndSpecsDescriptions = new HashMap<>(2);
        mpvToOptionsAndSpecsDescriptions.put("configs", mpvToOptions);
        mpvToOptionsAndSpecsDescriptions.put("descriptions", specsDescriptions);
        return Response.ok(mpvToOptionsAndSpecsDescriptions, MediaType.APPLICATION_JSON_TYPE).tag(mpvToOptionsEtag).build();
    }

    public Map<SupportedServer, Map<MicroProfileVersion, List<MicroprofileSpec>>> transformToLegacy() {
        List<SupportedServer> servers = new ArrayList<>(serversToOptions.keySet());
        Collections.shuffle(servers);
        Map<SupportedServer, Map<MicroProfileVersion, List<MicroprofileSpec>>> rndServersToOptions = new LinkedHashMap<>(servers.size());
        for (SupportedServer s : servers) {
            List<ServerOptions> so = serversToOptions.get(s);
            Map<MicroProfileVersion, List<MicroprofileSpec>> mpvSpecs = new HashMap<>(so.size());
            so.forEach(soo -> mpvSpecs.put(soo.mpVersion, soo.mpSpecs));
            rndServersToOptions.put(s, mpvSpecs);
        }
        return rndServersToOptions;
    }

    public Response supportMatrixServersV1(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (serversToOptionsEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        return Response.ok(transformToLegacy(), MediaType.APPLICATION_JSON_TYPE).tag(serversToOptionsEtag).build();
    }

    public Response supportMatrixServersV3(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (serversToOptionsEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        Map<String, Map> serversAndSpecsDescriptions = new HashMap<>(2);
        serversAndSpecsDescriptions.put("configs", transformToLegacy());
        serversAndSpecsDescriptions.put("descriptions", specsDescriptions);

        return Response.ok(serversAndSpecsDescriptions, MediaType.APPLICATION_JSON_TYPE).tag(serversToOptionsEtag).build();
    }

    public Response supportMatrixServers(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (serversToOptionsEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        List<SupportedServer> servers = new ArrayList<>(serversToOptions.keySet());
        Collections.shuffle(servers);
        Map<SupportedServer, List<ServerOptions>> rndServersToOptions = new LinkedHashMap<>(servers.size());
        for (SupportedServer s : servers) {
            rndServersToOptions.put(s, serversToOptions.get(s));
        }
        Map<String, Map> serversAndSpecsDescriptions = new HashMap<>(2);
        serversAndSpecsDescriptions.put("configs", rndServersToOptions);
        serversAndSpecsDescriptions.put("descriptions", specsDescriptions);

        return Response.ok(serversAndSpecsDescriptions, MediaType.APPLICATION_JSON_TYPE).tag(serversToOptionsEtag).build();
    }

    public Response listOptions(MicroProfileVersion mpVersion) {
        return Response.ok(mpvToOptions.get(mpVersion)).build();
    }

    public Response getProjectV1(String ifNoneMatch,
                                 SupportedServer supportedServer,
                                 String groupId, String artifactId,
                                 MicroProfileVersion mpVersion,
                                 JavaSEVersion javaSEVersion,
                                 List<MicroprofileSpec> selectedSpecs) {
        Project project = new Project();
        project.setSupportedServer(supportedServer);
        project.setGroupId(groupId);
        project.setArtifactId(artifactId);
        project.setMpVersion(mpVersion);
        project.setJavaSEVersion(javaSEVersion);
        project.setSelectedSpecs(selectedSpecs);

        setDefaultsV1(project);

        return processProject(ifNoneMatch, project);
    }

    public Response getProjectV1(String ifNoneMatch, Project body) {
        setDefaultsV1(body);
        return processProject(ifNoneMatch, body);
    }

    public Response getProject(String ifNoneMatch,
                               SupportedServer supportedServer,
                               String groupId, String artifactId,
                               MicroProfileVersion mpVersion,
                               JavaSEVersion javaSEVersion,
                               List<MicroprofileSpec> selectedSpecs,
                               boolean selectAllSpecs) {
        Project project = new Project();
        project.setSupportedServer(supportedServer);
        project.setGroupId(groupId);
        project.setArtifactId(artifactId);
        project.setMpVersion(mpVersion);
        project.setJavaSEVersion(javaSEVersion);
        project.setSelectedSpecs(selectedSpecs);
        project.setSelectAllSpecs(selectAllSpecs);

        setDefaults(project);

        return processProject(ifNoneMatch, project);
    }

    public Response getProject(String ifNoneMatch, Project body) {
        setDefaults(body);
        return processProject(ifNoneMatch, body);
    }

    private void setDefaultsV1(Project p) {
        if (StringUtils.isBlank(p.getGroupId())) {
            p.setGroupId(EngineData.DEFAULT_GROUP_ID);
        }
        if (StringUtils.isBlank(p.getArtifactId())) {
            p.setArtifactId(EngineData.DEFAULT_ARTIFACT_ID);
        }
        if (p.getMpVersion() == null || p.getMpVersion() == MicroProfileVersion.NONE) {
            p.setMpVersion(
                    p.getSupportedServer().getMpVersions().get(p.getSupportedServer().getMpVersions().size() - 1));
        }
        if (p.getJavaSEVersion() == null || p.getJavaSEVersion() == JavaSEVersion.NONE) {
            p.setJavaSEVersion(EngineData.DEFAULT_JAVA_SE_VERSION);
        }
        if (p.getSelectedSpecs() == null || p.getSelectedSpecs().isEmpty()) {
            p.setSelectedSpecs(mpvToOptions.get(p.getMpVersion()).getSpecs());
        }
    }

    private void setDefaults(Project p) {
        if (StringUtils.isBlank(p.getGroupId())) {
            p.setGroupId(EngineData.DEFAULT_GROUP_ID);
        }
        if (StringUtils.isBlank(p.getArtifactId())) {
            p.setArtifactId(EngineData.DEFAULT_ARTIFACT_ID);
        }
        if (p.getMpVersion() == null || p.getMpVersion() == MicroProfileVersion.NONE) {
            p.setMpVersion(
                    p.getSupportedServer().getMpVersions().get(p.getSupportedServer().getMpVersions().size() - 1));
        }
        if (p.getJavaSEVersion() == null || p.getJavaSEVersion() == JavaSEVersion.NONE) {
            p.setJavaSEVersion(EngineData.DEFAULT_JAVA_SE_VERSION);
        }
        if (p.getSelectedSpecs() == null) {
            p.setSelectedSpecs(Collections.emptyList());
        }
        if (p.getSelectedSpecs().isEmpty() && p.isSelectAllSpecs()) {
            p.setSelectedSpecs(mpvToOptions.get(p.getMpVersion()).getSpecs());
        }
    }

    private Response validate(Project p) {
        if (p.getSupportedServer() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR001)
                    .type("application/json")
                    .header("Content-Length", ERROR001.length())
                    .header("Content-Disposition", "attachment; filename=\"error.json\"")
                    .build();
        }
        if (!packageNameValidator.isValidPackageName(p.getGroupId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR004)
                    .type("application/json")
                    .header("Content-Length", ERROR004.length())
                    .header("Content-Disposition", "attachment; filename=\"error.json\"")
                    .build();
        }
        if (!packageNameValidator.isValidPackageName(p.getArtifactId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR005)
                    .type("application/json")
                    .header("Content-Length", ERROR005.length())
                    .header("Content-Disposition", "attachment; filename=\"error.json\"")
                    .build();
        }
        if (!mpvToOptions.get(p.getMpVersion()).getSupportedServers().contains(p.getSupportedServer())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type("application/json")
                    .header("Content-Length", ERROR002.length())
                    .header("Content-Disposition", "attachment; filename=\"error.json\"")
                    .entity(ERROR002)
                    .build();
        }
        if (!mpvToOptions.get(p.getMpVersion()).getSpecs().containsAll(p.getSelectedSpecs())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type("application/json")
                    .header("Content-Length", ERROR003.length())
                    .header("Content-Disposition", "attachment; filename=\"error.json\"")
                    .entity(ERROR003)
                    .build();
        }

        return Response.ok().build();
    }

    private EngineData getEngineData(Project p) {
        List<String> selectedSpecs = p.getSelectedSpecs().stream()
                .map(MicroprofileSpec::getCode).collect(Collectors.toList());
        EngineData engineData = new EngineData();
        engineData.getMavenData().setGroupId(p.getGroupId());
        engineData.getMavenData().setArtifactId(p.getArtifactId());
        engineData.setTrafficSource(EngineData.TrafficSource.REST);
        engineData.setSelectedSpecs(selectedSpecs);
        engineData.setSupportedServer(p.getSupportedServer().getCode());
        engineData.setMpVersion(p.getMpVersion().getCode());
        return engineData;
    }

    private Response processProject(String ifNoneMatch, Project p) {
        Response validatorResponse = validate(p);
        if (validatorResponse.getStatusInfo() != Response.Status.OK) {
            return validatorResponse;
        }

        EngineData ed = getEngineData(p);

        managedExecutorService.submit(new LoggingTask(ed));

        EntityTag etag = new EntityTag(Integer.toHexString(31 * version.getGit().hashCode() + p.hashCode()));

        if (ifNoneMatch != null) {
            if (etag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }

        JessieModel model = new JessieModel();
        model.setDirectory(ed.getMavenData().getArtifactId());

        JessieMaven mavenModel = new JessieMaven();
        mavenModel.setGroupId(ed.getMavenData().getGroupId());
        mavenModel.setArtifactId(ed.getMavenData().getArtifactId());
        model.setMaven(mavenModel);

        JessieSpecification specifications = new JessieSpecification();
        specifications.setJavaSEVersion(p.getJavaSEVersion());
        specifications.setMicroProfileVersion(p.getMpVersion());

        model.getOptions().put("mp.server", new OptionValue(ed.getSupportedServer()));
        model.getOptions().put("mp.specs", new OptionValue(ed.getSelectedSpecs()));

        model.setSpecification(specifications);

        model.getOptions().put(BeansXMLMode.OptionName.NAME,
                new OptionValue(BeansXMLMode.getValue(ed.getBeansxmlMode()).getMode()));

        try {
            modelManager.prepareModel(model, false);
            creator.createArtifacts(model);

            byte[] archive = zipFileCreator.createArchive();

            String fileName = ed.getMavenData().getArtifactId() + ".zip";

            return Response
                    .ok()
                    .tag(etag)
                    .header("Content-Length", archive.length)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .type("application/zip")
                    .entity(archive)
                    .build();
        } catch (Throwable e) {
            errorLogger.logError(e, model);
            return Response
                    .serverError()
                    .type("")
                    .entity("Unexpected error occurred; Please file a GitHub issue if the problem persists. Error : " + e.getMessage())
                    .build();
        }
    }
}
