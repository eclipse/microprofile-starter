package org.eclipse.microprofile.starter;

import org.eclipse.microprofile.starter.core.model.JessieMaven;
import org.eclipse.microprofile.starter.log.DynamoDBLogger;
import org.eclipse.microprofile.starter.view.EngineData;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class DynamoDBLoggerTest {

    @Test
    public void demoLoggingTest() {

        DynamoDBLogger dynamoDBLogger = new DynamoDBLogger();

        JessieMaven jessieMaven = new JessieMaven();
        jessieMaven.setArtifactId("something");
        jessieMaven.setGroupId("com.example.hahah");

        EngineData engineData = new EngineData();
        engineData.setMpVersion("2.0");
        engineData.setSupportedServer("liberty");
        engineData.setBeansxmlMode("ANNOTATED");
        engineData.setSelectedSpecs(Stream.of("HEALTH_METRICS", "HEALTH_CHECKS", "REST_CLIENT").collect(Collectors.toList()));
        engineData.setMavenData(jessieMaven);

        dynamoDBLogger.log(engineData);
    }
}
