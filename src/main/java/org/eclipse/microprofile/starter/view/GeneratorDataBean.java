/*
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
 * Contributors:
 *   2018-09-29 - Rudy De Busscher
 *      Initially authored in Atbash Jessie
 */
package org.eclipse.microprofile.starter.view;

import org.eclipse.microprofile.starter.Version;
import org.eclipse.microprofile.starter.ZipFileCreator;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.StandaloneMPSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.VersionSpecMatrix;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.ServerMPVersion;
import org.eclipse.microprofile.starter.core.artifacts.Creator;
import org.eclipse.microprofile.starter.core.exception.JessieUnexpectedException;
import org.eclipse.microprofile.starter.core.model.BeansXMLMode;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.JessieMaven;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.JessieSpecification;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;
import org.eclipse.microprofile.starter.core.model.ModelManager;
import org.eclipse.microprofile.starter.core.model.OptionValue;
import org.eclipse.microprofile.starter.log.ErrorLogger;
import org.eclipse.microprofile.starter.log.LoggingTask;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ViewScoped
@Named
public class GeneratorDataBean implements Serializable {

    @Inject
    private ModelManager modelManager;

    @Inject
    private Creator creator;

    @Inject
    private ZipFileCreator zipFileCreator;

    @Inject
    private Version version;

    @Inject
    private ErrorLogger errorLogger;

    @Resource
    private ManagedExecutorService managedExecutorService;

    private EngineData engineData;

    private MicroProfileVersion microProfileVersion;

    private List<SelectItem> supportedServerItems;
    private List<String> selectedSpecs = new ArrayList<>();
    private List<SelectItem> specs;
    private List<SelectItem> standaloneSpecs;
    private List<String> selectedStandaloneSpecs = new ArrayList<>();
    private boolean javaSEEnabled = false;

    @PostConstruct
    public void init() {
        engineData = new EngineData();
        defineSupportedServerItems(null);
    }

    public void onMPVersionSelected() {
        microProfileVersion = MicroProfileVersion.valueFor(engineData.getMpVersion());
        defineExampleSpecs(microProfileVersion);
        defineSupportedServerItems(microProfileVersion);
        defineStandaloneExampleSpecs();
        defineJavaSEVersionEnabled();
    }

    public void onMPRuntimeSelected() {
        if (engineData.getMpVersion() == null || engineData.getMpVersion().trim().isEmpty()) {
            defineMPVersionValue();
            onMPVersionSelected();  // So that example specs are filled and shown on screen.
            // This also limit the supportedServers as the MPVersion is now filled with a value.
        }
        defineStandaloneExampleSpecs();  // Make sure to update the enabled status of the standalone specs
        defineJavaSEVersionEnabled();
    }

    private void defineJavaSEVersionEnabled() {
        javaSEEnabled = false;
        if (engineData.getMpVersion() != null && engineData.getSupportedServer() != null) {
            SupportedServer supportedServer = SupportedServer.valueFor(engineData.getSupportedServer());
            MicroProfileVersion mpVersion = MicroProfileVersion.valueFor(engineData.getMpVersion());
            javaSEEnabled = isSameOrMoreRecentVersion(supportedServer.getFirstJava11SupportedVersion(), mpVersion);
        }
    }

    private boolean isSameOrMoreRecentVersion(MicroProfileVersion firstJava11SupportedVersion, MicroProfileVersion mpVersion) {
        if (firstJava11SupportedVersion == null) {
            return false;  // No Java 11 support for Implementation defined.
        }
        return firstJava11SupportedVersion.ordinal() >= mpVersion.ordinal();
    }

    private void defineMPVersionValue() {
        // Look for the latest MP version
        SupportedServer supportedServer = SupportedServer.valueFor(engineData.getSupportedServer());
        List<MicroProfileVersion> versions = supportedServer.getMpVersions();
        MicroProfileVersion microProfileVersion = versions.get(versions.size() - 1);
        engineData.setMpVersion(microProfileVersion.getCode());
    }

    private void defineExampleSpecs(MicroProfileVersion version) {
        specs = new ArrayList<>();
        List<String> currentSelected = new ArrayList<>(selectedSpecs);
        selectedSpecs.clear();

        for (MicroprofileSpec microprofileSpec : MicroprofileSpec.values()) {
            if (microprofileSpec.getMpVersions().contains(version)) {
                specs.add(new SelectItem(microprofileSpec.getCode(), microprofileSpec.getLabel()));
                if (currentSelected.contains(microprofileSpec.getCode())) {
                    // If the spec is currently selected, keep it selected.
                    // But if it is not listed anymore in the MP version, it has to go.
                    selectedSpecs.add(microprofileSpec.getCode());
                }
            }
        }

    }

    private void defineStandaloneExampleSpecs() {
        standaloneSpecs = new ArrayList<>();
        List<String> currentSelected = new ArrayList<>(selectedStandaloneSpecs);
        selectedStandaloneSpecs.clear();

        for (StandaloneMPSpec standaloneMPSpec : StandaloneMPSpec.values()) {
            if (standaloneSpecForVersion(standaloneMPSpec.getServerRestrictions())) {
                boolean standaloneSpecEnabled = isStandaloneSpecEnabled(standaloneMPSpec);
                if (standaloneSpecEnabled) {
                    standaloneSpecs.add(new SelectItem(standaloneMPSpec.getCode(), standaloneMPSpec.getLabel()));
                    if (currentSelected.contains(standaloneMPSpec.getCode())) {
                        selectedStandaloneSpecs.add(standaloneMPSpec.getCode());
                    }
                }
            }
        }
    }

    private boolean standaloneSpecForVersion(List<ServerMPVersion> serverRestrictions) {
        boolean result = false;
        for (ServerMPVersion serverRestriction : serverRestrictions) {
            if (serverRestriction.getMinimalMPVersion() == null) {
                result = true;  // When runtime has no MP version restriction, show it always.
            } else {
                if (microProfileVersion != null && serverRestriction.getMinimalMPVersion().ordinal() > microProfileVersion.ordinal()) {
                    result = true;
                }
            }
        }
        return result;
    }

    public String getSpecificationLink(MicroprofileSpec spec) {
        Map<MicroprofileSpec, String> specData = VersionSpecMatrix.getInstance().getSpecData(microProfileVersion);
        return String.format(spec.getTagURL(), specData.get(spec) );
    }

    public String getSpecificationStandaloneLink(StandaloneMPSpec spec) {

        return spec.getTagURL();
    }

    public boolean isStandaloneSpecEnabled(StandaloneMPSpec spec) {
        if (engineData.getSupportedServer() == null) {
            // If no runtime specified, standalone spec can never be enabled as we can't determine if runtime has support for it.
            return false;
        }
        boolean result = false;
        for (ServerMPVersion serverRestriction : spec.getServerRestrictions()) {
            if (serverRestriction.getSupportedServer().getCode().equals(engineData.getSupportedServer())) {
                // This restriction is for the selected runtime
                if (serverRestriction.getMinimalMPVersion() == null) {
                    // No restriction on MP version -> enabled
                    result = true;
                } else {
                    // Current selected version more recenter as MP version defined in restriction.
                    result = serverRestriction.getMinimalMPVersion().ordinal() >= microProfileVersion.ordinal();
                }
            }
        }
        return result;
    }

    private void defineSupportedServerItems(MicroProfileVersion version) {

        supportedServerItems = new ArrayList<>();
        for (SupportedServer supportedServer : SupportedServer.values()) {
            if (version == null || supportedServer.getMpVersions().contains(version)) {
                supportedServerItems.add(new SelectItem(supportedServer.getCode(), supportedServer.getDisplayName()));
            }
        }
        if (!supportedServerListContainsCurrentSelection()) {
            engineData.setSupportedServer(null);
        }
        randomizeSupportedServers();
    }

    private boolean supportedServerListContainsCurrentSelection() {
        return supportedServerItems.stream()
                .map(SelectItem::getValue)
                .anyMatch(s -> s.equals(engineData.getSupportedServer()));
    }

    private void randomizeSupportedServers() {
        List<Integer> rnd = generateUniqueRandomNumbers(supportedServerItems.size());

        Iterator<Integer> keyIterator = rnd.iterator();
        Iterator<SelectItem> valueIterator = supportedServerItems.iterator();
        Map<Integer, SelectItem> data = IntStream.range(0, rnd.size()).boxed()
                .collect(Collectors.toMap(i -> keyIterator.next(), i -> valueIterator.next()));

        supportedServerItems = new ArrayList<>(data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new))
                .values());

    }

    private List<Integer> generateUniqueRandomNumbers(int randomNumberSize) {
        Random rnd = new Random();
        List<Integer> result = new ArrayList<>();
        while (result.size() < randomNumberSize) {
            int value = rnd.nextInt(500);
            if (!result.contains(value)) {
                result.add(value);
            }
        }
        return result;
    }

    public void generateProject() {

        JessieModel model = new JessieModel();
        model.setDirectory(engineData.getMavenData().getArtifactId());
        JessieMaven mavenModel = new JessieMaven();
        mavenModel.setGroupId(engineData.getMavenData().getGroupId());
        mavenModel.setArtifactId(engineData.getMavenData().getArtifactId());
        model.setMaven(mavenModel);

        JessieSpecification specifications = new JessieSpecification();

        specifications.setJavaSEVersion(JavaSEVersion.valueFor(engineData.getJavaSEVersion()));

        specifications.setMicroProfileVersion(MicroProfileVersion.valueFor(engineData.getMpVersion()));

        model.getOptions().put("mp.server", new OptionValue(engineData.getSupportedServer()));
        model.getOptions().put("mp.specs", new OptionValue(selectedSpecs));
        model.getOptions().put("mp.standaloneSpecs", new OptionValue(selectedStandaloneSpecs));

        List<String> allSpecs = new ArrayList<>();
        allSpecs.addAll(selectedSpecs);
        allSpecs.addAll(selectedStandaloneSpecs);
        engineData.setSelectedSpecs(allSpecs);

        model.setSpecification(specifications);

        model.getOptions().put(BeansXMLMode.OptionName.NAME, new OptionValue(BeansXMLMode.getValue(engineData.getBeansxmlMode()).getMode()));

        try {
            modelManager.prepareModel(model, false);
            creator.createArtifacts(model);

            managedExecutorService.submit(new LoggingTask(engineData));

            download(zipFileCreator.createArchive());
        } catch (Throwable e) {
            errorLogger.logError(e, model);
            String messageText = "Unexpected error occurred; Please file a GitHub issue if the problem persists. Error : " + e.getMessage();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, messageText);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    private void download(byte[] archive) {
        String fileName = engineData.getMavenData().getArtifactId() + ".zip";
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset();
        ec.setResponseContentType("application/zip");
        ec.setResponseContentLength(archive.length);
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try {
            OutputStream outputStream = ec.getResponseOutputStream();

            outputStream.write(archive);
            outputStream.close();
        } catch (IOException e) {
            throw new JessieUnexpectedException("IO Error during download of ZIP");
        }

        // Important! Otherwise JSF will attempt to render the response which obviously will fail
        // since it's already written with a file and closed.
        fc.responseComplete();
    }

    public Version getVersion() {
        return version;
    }

    public EngineData getEngineData() {
        return engineData;
    }

    public List<SelectItem> getSupportedServerItems() {
        return supportedServerItems;
    }

    public List<SelectItem> getSpecs() {
        return specs;
    }

    public List<String> getSelectedSpecs() {
        return selectedSpecs;
    }

    public void setSelectedSpecs(List<String> selectedSpecs) {
        this.selectedSpecs = selectedSpecs;
    }

    public void selectAll() {
        selectedSpecs = specs.stream().map(si -> si.getValue().toString()).collect(Collectors.toList());
        selectedStandaloneSpecs = standaloneSpecs.stream().map(si -> si.getValue().toString()).collect(Collectors.toList());
    }

    public void unselectAll() {
        selectedSpecs.clear();
        selectedStandaloneSpecs.clear();
    }

    public boolean getJavaSEEnabled() {
        return javaSEEnabled;
    }

    public List<SelectItem> getStandaloneSpecs() {
        return standaloneSpecs;
    }

    public List<String> getSelectedStandaloneSpecs() {
        return selectedStandaloneSpecs;
    }

    public void setSelectedStandaloneSpecs(List<String> selectedStandaloneSpecs) {
        this.selectedStandaloneSpecs = selectedStandaloneSpecs;
    }
}
