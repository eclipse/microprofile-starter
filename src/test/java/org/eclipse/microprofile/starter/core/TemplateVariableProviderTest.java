package org.eclipse.microprofile.starter.core;

import org.eclipse.microprofile.starter.core.model.JessieMaven;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.JessieSpecification;
import org.eclipse.microprofile.starter.core.model.MicroProfileVersion;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TemplateVariableProviderTest {

    @Test
    public void shouldStoreValidApplicationName() {
        TemplateVariableProvider provider = new TemplateVariableProvider();
        JessieModel model = new JessieModel();
        JessieMaven maven = new JessieMaven();
        JessieSpecification specification = new JessieSpecification();
        specification.setMicroProfileVersion(MicroProfileVersion.MP22);
        maven.setArtifactId("demo-service");
        model.setMaven(maven);
        model.setSpecification(specification);

        Map<String, String> variables = provider.determineVariables(model);

        Assert.assertEquals("Demoservice", variables.get("application"));
    }
}