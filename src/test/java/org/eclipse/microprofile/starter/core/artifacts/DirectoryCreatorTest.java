package org.eclipse.microprofile.starter.core.artifacts;

import org.eclipse.microprofile.starter.FakeDirectoryCreator;
import org.eclipse.microprofile.starter.core.model.JessieMaven;
import org.junit.Assert;
import org.junit.Test;

public class DirectoryCreatorTest {

    @Test
    public void shouldCreateUsingValidPackage() {
        FakeDirectoryCreator creator = new FakeDirectoryCreator();

        JessieMaven maven = new JessieMaven();
        maven.setGroupId("com.test");
        maven.setArtifactId(("test-service"));
        String path = creator.createPathForGroupAndArtifact(maven);

        Assert.assertEquals("com/test/test/service", path);
    }
}