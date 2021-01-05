package org.eclipse.microprofile.starter.core.model;

import java.util.Arrays;

public enum BuildTool {

    MAVEN, GRADLE;

    public static BuildTool forValue(String data) {
        return Arrays.stream(BuildTool.values())
                .filter(v -> v.name().equalsIgnoreCase(data))
                .findAny().orElse(null);

    }
}
