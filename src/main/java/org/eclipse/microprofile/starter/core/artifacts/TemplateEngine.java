package org.eclipse.microprofile.starter.core.artifacts;

import org.eclipse.microprofile.starter.core.files.FileCopyEngine;
import org.eclipse.microprofile.starter.core.files.ThymeleafEngine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class TemplateEngine {

    @Inject
    protected ThymeleafEngine thymeleafEngine;

    @Inject
    protected FileCopyEngine fileCopyEngine;

    @Inject
    protected FileCreator fileCreator;

    public void processTemplateFile(String directory, String templateFileName, String fileName,
                                    Set<String> alternatives, Map<String, String> variables) {
        String javaFile = thymeleafEngine.processFile(templateFileName, alternatives, variables);
        fileCreator.writeContents(directory, fileName, javaFile, false);
    }

    public void processTemplateFile(String directory, String fileName, Set<String> alternatives, Map<String, String> variables) {
        String javaFile = thymeleafEngine.processFile(fileName, alternatives, variables);
        fileCreator.writeContents(directory, fileName, javaFile, false);
    }

    public void processFile(String directory, String fileName, Set<String> alternatives) {
        byte[] fileContent = fileCopyEngine.processFile(fileName, alternatives);
        fileCreator.writeContents(directory, fileName, fileContent, false);
    }

    public void processFile(String directory, String fileName, Set<String> alternatives, Boolean executable) {
        byte[] fileContent = fileCopyEngine.processFile(fileName, alternatives);
        fileCreator.writeContents(directory, fileName, fileContent, executable);
    }
}
