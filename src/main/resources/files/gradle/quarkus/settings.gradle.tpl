pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id "${quarkusPluginId}" version "${quarkusPluginVersion}"
    }
}
rootProject.name = '[# th:text="${maven_artifactid}"/]'
