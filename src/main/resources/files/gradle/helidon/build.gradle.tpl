plugins {
    id 'java'
    id 'org.kordamp.gradle.jandex' version '0.6.0'
}

group = '[# th:text="${maven_groupid}"/]'
version = '1.0-SNAPSHOT'

description = "MicroProfile Starter example"

sourceCompatibility = [# th:text="${se_version}"/]
targetCompatibility = [# th:text="${se_version}"/]

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

ext {
    helidonversion = '2.2.0'
}

repositories {
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    // import Helidon BOM
    implementation enforcedPlatform("io.helidon:helidon-dependencies:${project.helidonversion}")
    implementation 'io.helidon.microprofile.bundles:helidon-microprofile'
    implementation 'org.glassfish.jersey.media:jersey-media-json-binding'

    runtimeOnly 'org.jboss:jandex'
    runtimeOnly 'jakarta.activation:jakarta.activation-api'
    [# th:if="${mainProject and mp_JWT_auth}"]
    implementation 'io.vertx:vertx-auth-jwt:3.9.2'
    [/]
}

// define a custom task to copy all dependencies in the runtime classpath
// into build/libs/libs
// uses built-in Copy
task copyLibs(type: Copy) {
  from configurations.runtimeClasspath
  into 'build/libs/libs'
}

// add it as a dependency of built-in task 'assemble'
copyLibs.dependsOn jar
assemble.dependsOn copyLibs

// default jar configuration
// set the main classpath
// add each jar under build/libs/libs into the classpath
jar {
  archiveFileName = "${project.name}.jar"
  manifest {
    attributes ('Main-Class': 'io.helidon.microprofile.cdi.Main',
                'Class-Path': configurations.runtimeClasspath.files.collect { "libs/$it.name" }.join(' ')
               )
  }
}


