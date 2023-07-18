apply plugin: 'war'
apply plugin: 'liberty'

def projectName = '[# th:text="${maven_artifactid}"/]'
group = '[# th:text="${maven_groupid}"/]'
version = '1.0-SNAPSHOT'

description = "MicroProfile Starter example"

sourceCompatibility = JavaVersion.VERSION_[# th:text="${se_version}"/]
targetCompatibility = JavaVersion.VERSION_[# th:text="${se_version}"/]

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

war {
    archiveName = projectName + '.war'
}

// configure liberty-gradle-plugin
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'io.openliberty.tools:liberty-gradle-plugin:3.5.2'
    }
}

repositories {
    mavenCentral()
}

dependencies {
    providedCompile 'org.eclipse.microprofile:microprofile:[# th:text="${mp_depversion}"/]'
    [# th:if="${mainProject and mp_JWT_auth}"]
    implementation 'io.vertx:vertx-auth-jwt:[# th:text="${vertx_auth_jwt_version}"/]'[/]
    [# th:if="${mainProject and mp_graphql}"]
    providedCompile 'org.eclipse.microprofile.graphql:microprofile-graphql-api:[# th:text="${mp_graphql_version}"/]'[/]
   
}

ext  {
    liberty.server.var.'default.http.port' = '[# th:text="${port_service}"/]'
    liberty.server.var.'project.name' = projectName
    liberty.server.var.'app.context.root' = '/'
    [# th:if="${secondary_project and mp_JWT_auth}"]
    liberty.server.var.'jwt.issuer' = 'https://server.example.com'
    [/]
}

