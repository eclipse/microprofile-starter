plugins {
    id 'java'
    id 'io.quarkus'
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}")
    implementation 'io.quarkus:quarkus-arc'
    implementation 'io.quarkus:quarkus-resteasy'
    [# th:if="${mainProject and mp_fault_tolerance}"]
    implementation 'io.quarkus:quarkus-smallrye-fault-tolerance'[/]
    [# th:if="${mp_JWT_auth}"]
    implementation 'io.quarkus:quarkus-smallrye-jwt'[/]
    [# th:if="${mainProject and mp_metrics}"]
    implementation 'io.quarkus:quarkus-smallrye-metrics'[/]
    [# th:if="${mainProject and mp_health_checks}"]
    implementation 'io.quarkus:quarkus-smallrye-health'[/]
    [# th:if="${mainProject and mp_open_API}"]
    implementation 'io.quarkus:quarkus-smallrye-openapi'[/]
    [# th:if="${mp_open_tracing}"]
    implementation 'io.quarkus:quarkus-smallrye-opentracing'[/]
    [# th:if="${mainProject and (mp_rest_client or mp_JWT_auth)}"]
    implementation 'io.quarkus:quarkus-rest-client'[/]
    [# th:if="${mainProject and mp_JWT_auth}"]
    implementation 'io.vertx:vertx-auth-jwt:[# th:text="${vertx_auth_jwt_version}"/]'[/]
}

group '[# th:text="${maven_groupid}"/]'
description = "MicroProfile Starter example"

java {
    sourceCompatibility = JavaVersion.VERSION_[# th:text="${se_version}"/]
    targetCompatibility = JavaVersion.VERSION_[# th:text="${se_version}"/]
}

compileJava {
    options.encoding = 'UTF-8'
    options.compilerArgs << '-parameters'
}

compileTestJava {
    options.encoding = 'UTF-8'
}
