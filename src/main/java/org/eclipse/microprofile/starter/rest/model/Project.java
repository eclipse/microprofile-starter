package org.eclipse.microprofile.starter.rest.model;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.JavaSEVersion;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;


public class Project {
    private String groupId = null;
    private String artifactId = null;
    private MicroProfileVersion mpVersion = null;
    private JavaSEVersion javaSEVersion = null;
    private SupportedServer supportedServer = null;
    private List<MicroprofileSpec> selectedSpecs = null;

    @Pattern(regexp = "^(\\w+|\\w+\\.\\w+)+$")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Pattern(regexp = "^(\\w+|\\w+\\.\\w+)+$")
    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public MicroProfileVersion getMpVersion() {
        return mpVersion;
    }

    public void setMpVersion(MicroProfileVersion mpVersion) {
        this.mpVersion = mpVersion;
    }

    public JavaSEVersion getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(JavaSEVersion javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
    }

    @NotNull
    public SupportedServer getSupportedServer() {
        return supportedServer;
    }

    public void setSupportedServer(SupportedServer supportedServer) {
        this.supportedServer = supportedServer;
    }

    public List<MicroprofileSpec> getSelectedSpecs() {
        return selectedSpecs;
    }

    public void setSelectedSpecs(List<MicroprofileSpec> selectedSpecs) {
        this.selectedSpecs = selectedSpecs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Project project = (Project) o;
        return Objects.equals(groupId, project.groupId) &&
                Objects.equals(artifactId, project.artifactId) &&
                mpVersion == project.mpVersion &&
                javaSEVersion == project.javaSEVersion &&
                supportedServer == project.supportedServer &&
                Objects.equals(selectedSpecs, project.selectedSpecs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, mpVersion, javaSEVersion, supportedServer, selectedSpecs);
    }
}
