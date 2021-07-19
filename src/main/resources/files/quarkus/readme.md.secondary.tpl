# MicroProfile generated Application

## Introduction

MicroProfile Starter has generated this MicroProfile application for you containing some endpoints which are called from the main application (see the `service-a` directory)
[# th:if="${build_tool} == 'GRADLE'"]
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Packaging and running the application

If you want to build an _über-jar_, execute the following command:

    ./gradlew build -Dquarkus.package.type=uber-jar

To run the application:

    java [# th:text="${jar_parameters}"/] -jar build/[# th:text="${jar_file}"/]

The application can be also packaged using simple:

    ./gradlew build

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it is not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

## Creating a native executable

Mind having GRAALVM_HOME set to your Mandrel or GraalVM installation.

You can create a native executable using:

    ./gradlew build -Dquarkus.package.type=native

Or, if you don't have [Mandrel](https://github.com/graalvm/mandrel/releases/) or
[GraalVM](https://github.com/graalvm/graalvm-ce-builds/releases) installed, you can run the native executable
build in a container using:

    ./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true

Or to use Mandrel distribution:

    ./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-mandrel:20.3-java11

You can then execute your native executable with:

    ./build/[# th:text="${jar_file_no_suffix}"/] [# th:text="${jar_parameters}"/]

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.
[/][# th:if="${build_tool} == 'MAVEN'"]
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Packaging and running the application

If you want to build an _über-jar_, execute the following command:

    mvn package -Dquarkus.package.type=uber-jar

To run the application:

    java [# th:text="${jar_parameters}"/] -jar target/[# th:text="${jar_file}"/]

The application can be also packaged using simple:

    mvn package

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it is not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

## Creating a native executable

Mind having GRAALVM_HOME set to your Mandrel or GraalVM installation.

You can create a native executable using:

    mvn package -Dquarkus.package.type=native

Or, if you don't have [Mandrel](https://github.com/graalvm/mandrel/releases/) or
[GraalVM](https://github.com/graalvm/graalvm-ce-builds/releases) installed, you can run the native executable
build in a container using:

    mvn package -Dquarkus.package.type=native -Dquarkus.native.container-build=true

Or to use Mandrel distribution:

    mvn package -Dquarkus.package.type=native -Dquarkus.native.container-build=true -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-mandrel:20.3-java11

You can then execute your native executable with:

    ./target/[# th:text="${jar_file_no_suffix}"/] [# th:text="${jar_parameters}"/]

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.
[/]
## Specification examples

[# th:if="${mp_JWT_auth}"]
### JWT Auth

Have a look at the **TestSecureController** class (main application) which calls the protected endpoint on the secondary application.
The **ProtectedController** contains the protected endpoint since it contains the _@RolesAllowed_ annotation on the JAX-RS endpoint method.

The _TestSecureController_ code creates a JWT based on the private key found within the resource directory.
However, any method to send a REST request with an appropriate header will work of course. Please feel free to change this code to your needs.
[/][# th:if="${mp_rest_client}"]
### Rest Client
A type safe invocation of HTTP rest endpoints. Specification [here](https://microprofile.io/project/eclipse/microprofile-rest-client)
The example calls one endpoint from another JAX-RS resource where generated Rest Client is injected as CDI bean.
[/]