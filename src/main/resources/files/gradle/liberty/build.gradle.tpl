apply plugin: 'war'
apply plugin: 'liberty'

group = '[# th:text="${maven_groupid}"/]'
version = '1.0-SNAPSHOT'

description = "MicroProfile Starter example"

sourceCompatibility = [# th:text="${se_version}"/]
targetCompatibility = [# th:text="${se_version}"/]

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

// configure liberty-gradle-plugin
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'io.openliberty.tools:liberty-gradle-plugin:3.0'
    }
}

repositories {
    mavenCentral()
}

dependencies {
    providedCompile 'org.eclipse.microprofile:microprofile:[# th:text="${mp_version}"/]'
}

ext  {
    liberty.server.var.'default.http.port' = '9080'
    liberty.server.var.'default.https.port' = '9443'
    liberty.server.var.'app.context.root' = /
}

