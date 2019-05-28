package org.eclipse.microprofile.starter;

import org.eclipse.microprofile.starter.core.files.FilesLocator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@ApplicationScoped
public class Version {
    private static final Logger LOG = Logger.getLogger(Version.class.getName());

    private String git;

    @PostConstruct
    public void init() {
        try (Scanner s = new Scanner(FilesLocator.class.getClassLoader()
                .getResourceAsStream("/version.txt")).useDelimiter("\\A")) {
            git = s.hasNext() ? s.next() : "";
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
    }

    public String getGit() {
        return git;
    }
}
    
