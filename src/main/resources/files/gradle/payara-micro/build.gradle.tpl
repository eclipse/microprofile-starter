plugins {
  id "fish.payara.micro-gradle-plugin" version "1.0.4"
}

group = '[# th:text="${maven_groupid}"/]'
version = '1.0-SNAPSHOT'

description = "MicroProfile Starter example"

sourceCompatibility = JavaVersion.VERSION_[# th:text="${se_version}"/]
targetCompatibility = JavaVersion.VERSION_[# th:text="${se_version}"/]

payaraMicro {
    payaraVersion = '[# th:text="${payara_version}"/]'
    deployWar = true
    useUberJar = true
    daemon = false
    commandLineOptions = [port: [# th:text="${port_service}"/], contextroot : '/']
}

dependencies {
    providedCompile 'org.eclipse.microprofile:microprofile:[# th:text="${mp_depversion}"/]'
    [# th:if="${mainProject and mp_JWT_auth}"]
    implementation 'io.vertx:vertx-auth-jwt:[# th:text="${vertx_auth_jwt_version}"/]'[/]
    [# th:if="${mainProject and mp_graphql}"]
    providedCompile 'org.eclipse.microprofile.graphql:microprofile-graphql-api:1.0.2'[/]
}

repositories {
    mavenCentral()
}
