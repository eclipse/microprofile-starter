package org.eclipse.microprofile.starter.core.artifacts;

import org.eclipse.microprofile.starter.core.model.JessieModel;

import javax.inject.Inject;

public class BuildToolCreator {

    public static final String SRC_MAIN_JAVA = "src/main/java";
    public static final String SRC_MAIN_RESOURCES = "src/main/resources";
    public static final String SRC_MAIN_WEBAPP = "src/main/webapp";

    @Inject
    protected FileCreator fileCreator;
    @Inject
    protected DirectoryCreator directoryCreator;

    protected void createDefaultDirectories(JessieModel model, boolean mainProject) {

        String directory = model.getDirectory(mainProject);

        String javaDirectory = directory + "/" + BuildToolCreator.SRC_MAIN_JAVA;
        directoryCreator.createDirectory(javaDirectory);

        String resourcesDirectory = directory + "/" + BuildToolCreator.SRC_MAIN_RESOURCES;
        directoryCreator.createDirectory(resourcesDirectory);
        fileCreator.createEmptyFile(resourcesDirectory, ".gitkeep");

        if (mainProject) {
            String webappDirectory = directory + "/" + BuildToolCreator.SRC_MAIN_WEBAPP;
            directoryCreator.createDirectory(webappDirectory);
            fileCreator.createEmptyFile(webappDirectory, ".gitkeep");
        }
    }
}
