package org.eclipse.microprofile.starter.core.model;

import org.junit.Assert;
import org.junit.Test;

public class JessieMavenTest {

    @Test
    public void shouldRelaceHypendsWithPeriodInPackageName() {
        JessieMaven maven = new JessieMaven();

        maven.setArtifactId("test-service");

        Assert.assertEquals("test.service", maven.getPackage());
    }
}