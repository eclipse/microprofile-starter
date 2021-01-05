apply plugin: 'war'

group = '[# th:text="${maven_groupid}"/]'
version = '1.0-SNAPSHOT'

sourceCompatibility = [# th:text="${se_version}"/]
targetCompatibility = [# th:text="${se_version}"/]

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

repositories {
    mavenCentral()
}

dependencies {
    providedCompile 'org.eclipse.microprofile:microprofile:[# th:text="${mp_version}"/]'
}

