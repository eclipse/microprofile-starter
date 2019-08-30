# MicroProfile generated Application

## Introduction

MicroProfile Starter has generated this MicroProfile application for you containing some endpoints which are called from the main application (see the `service-a` directory)

The generation of the executable jar file can be performed by issuing the following command

    mvn clean compile quarkus:build

This will create a jar file **[# th:text="${jar_file}"/]** within the _target_ maven folder. This can be started by executing the following command

    java [# th:text="${jar_parameters}"/] -jar target/[# th:text="${jar_file}"/]

You can also start the project in development mode where it automatically updates code on the fly as you save your files:

    mvn [# th:text="${jar_parameters}"/] clean compile quarkus:dev

Last but not least, you can build the whole application into a one statically linked executable that does not require JVM:

    mvn clean compile quarkus:native-image -Pnative

Native executable build might take a minute. Then you can execute it on a compatible architecture without JVM:

    ./target/[# th:text="${jar_file_no_suffix}"/] [# th:text="${jar_parameters}"/]

## Note on Native image

 * You need GraalVM installed from the GraalVM web site. Using the community edition is enough. Version 19.1.1+ is required.
 * The GRAALVM_HOME environment variable configured appropriately
 * The native-image tool must be installed; this can be done by running ```gu install native-image``` from your GraalVM directory

## Specification examples

[# th:if="${mp_JWT_auth}"]
### JWT Auth

Have a look at the **TestSecureController** class (main application) which calls the protected endpoint on the secondary application.
The **ProtectedController** contains the protected endpoint since it contains the _@RolesAllowed_ annotation on the JAX-RS endpoint method.

The _TestSecureController_ code creates a JWT based on the private key found within the resource directory.
However, any method to send a REST request with an appropriate header will work of course. Please feel free to change this code to your needs.

[/]

[# th:if="${mp_rest_client}"]
### Rest Client

A type safe invocation of HTTP rest endpoints. Specification [here](https://microprofile.io/project/eclipse/microprofile-rest-client)

The example calls one endpoint from another JAX-RS resource where generated Rest Client is injected as CDI bean.

[/]