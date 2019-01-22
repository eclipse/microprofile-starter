/*
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

import org.eclipse.microprofile.starter.ZipFileCreator;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.artifacts.Creator;
import org.eclipse.microprofile.starter.core.model.*;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ViewScoped
@Named
public class GeneratorDataBean implements Serializable {

    @Inject
    private ModelManager modelManager;

    @Inject
    private Creator creator;

    @Inject
    private ZipFileCreator zipFileCreator;

    private JessieMaven mavenData;
    private String javaSEVersion = "1.8";

    private String mpVersion;
    private String supportedServer;
    private String beansxmlMode;

    private List<SelectItem> supportedServerItems;
    private List<String> selectedSpecs = new ArrayList<>();
    private List<SelectItem> specs;
    private String selectedSpecDescription;

    @PostConstruct
    public void init() {
        mavenData = new JessieMaven();
    }

    public void onMPVersionSelected() {
        MicroProfileVersion version = MicroProfileVersion.valueFor(mpVersion);
        defineExampleSpecs(version);
        defineSupportedServerItems(version);
    }

    private void defineExampleSpecs(MicroProfileVersion version) {
        specs = new ArrayList<>();

        for (MicroprofileSpec microprofileSpec : MicroprofileSpec.values()) {
            if (microprofileSpec.getMpVersions().contains(version)) {
                specs.add(new SelectItem(microprofileSpec.getCode(), microprofileSpec.getLabel()));
                selectedSpecs.add(microprofileSpec.getCode());
            }
        }
    }

    private void defineSupportedServerItems(MicroProfileVersion version) {

        supportedServerItems = new ArrayList<>();
        for (SupportedServer supportedServer : SupportedServer.values()) {
            if (supportedServer.getMpVersions().contains(version)) {
                supportedServerItems.add(new SelectItem(supportedServer.getName(), supportedServer.getName()));
            }
        }
        randomizeSupportedServers();
    }

    private void randomizeSupportedServers() {
        Random rnd = new Random();
        Map<Integer, SelectItem> data = supportedServerItems
                .stream().collect(Collectors.toMap(s -> rnd.nextInt(500),
                        Function.identity()));

        supportedServerItems = new ArrayList<>(data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new))
                .values());

    }

    public void generateProject() {

        JessieModel model = new JessieModel();
        model.setDirectory(mavenData.getArtifactId());
        JessieMaven mavenModel = new JessieMaven();
        mavenModel.setGroupId(mavenData.getGroupId());
        mavenModel.setArtifactId(mavenData.getArtifactId());
        model.setMaven(mavenModel);

        JessieSpecification specifications = new JessieSpecification();

        specifications.setJavaSEVersion(JavaSEVersion.valueFor(javaSEVersion));
        specifications.setModuleStructure(ModuleStructure.SINGLE);

        specifications.setMicroProfileVersion(MicroProfileVersion.valueFor(mpVersion));

        model.getOptions().put("mp.server", new OptionValue(supportedServer));
        model.getOptions().put("mp.specs", new OptionValue(selectedSpecs));


        model.setSpecification(specifications);

        model.getOptions().put(BeansXMLMode.OptionName.name, new OptionValue(BeansXMLMode.getValue(beansxmlMode).getMode()));

        modelManager.prepareModel(model, false);
        creator.createArtifacts(model);

        download(zipFileCreator.createArchive());

    }

    private void download(byte[] archive) {
        String fileName = mavenData.getArtifactId() + ".zip";
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
            e.printStackTrace(); // FIXME
        }

        fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
    }

    public JessieMaven getMavenData() {
        return mavenData;
    }

    public String getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(String javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
    }

    public String getMpVersion() {
        return mpVersion;
    }

    public void setMpVersion(String mpVersion) {
        this.mpVersion = mpVersion;
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

        selectedSpecDescription = selectedSpecs.stream()
                .map(s -> MicroprofileSpec.valueFor(s).getLabel())
                .collect(Collectors.joining(", "));

    }

    public String getSelectedSpecDescription() {
        return selectedSpecDescription;
    }

    public String getSupportedServer() {
        return supportedServer;
    }

    public void setSupportedServer(String supportedServer) {
        this.supportedServer = supportedServer;
    }

    public String getBeansxmlMode() {
        return beansxmlMode;
    }

    public void setBeansxmlMode(String beansxmlMode) {
        this.beansxmlMode = beansxmlMode;
    }

    public String getBeansxmlModelDescription() {
        String result;
        switch (BeansXMLMode.getValue(beansxmlMode)) {

            case IMPLICIT:
                result = "No beans.xml file generated (implicit)";
                break;
            case ANNOTATED:
                result = "beans.xml file generated with discovery mode 'annotated'";
                break;
            case ALL:
                result = "beans.xml file generated with discovery mode 'all'";
                break;
            default:
                throw new IllegalArgumentException(String.format("BeansXMLMode '%s' not supported", beansxmlMode));
        }
        return result;
    }
}
