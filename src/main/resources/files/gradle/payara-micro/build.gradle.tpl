plugins {
  id "fish.payara.micro-gradle-plugin" version "1.0.4"
}

group = '[# th:text="${maven_groupid}"/]'
version = '1.0-SNAPSHOT'

description = "MicroProfile Starter example"

sourceCompatibility = [# th:text="${se_version}"/]
targetCompatibility = [# th:text="${se_version}"/]

payaraMicro {
    payaraVersion = '[# th:text="${payara_version}"/]'
    deployWar = true
    useUberJar = true
    daemon = false
    commandLineOptions = [port: [# th:text="${port_service}"/], contextroot : '/']
}

dependencies {
    providedCompile 'org.eclipse.microprofile:microprofile:[# th:text="${mp_version}"/]'
    [# th:if="${mainProject and mp_JWT_auth}"]
    implementation 'io.vertx:vertx-auth-jwt:3.9.2'
    [/]
}

repositories {
    mavenCentral()
}