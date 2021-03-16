# MicroProfile generated Application

## Introduction

MicroProfile Starter has generated this MicroProfile application for you containing some endpoints which are called from the main application (see the `service-a` directory)

The generation of the executable jar file can be performed by issuing the following command

[# th:if="${build_tool} == 'MAVEN'"]

    mvn clean package

This will create an executable jar file **[# th:text="${jar_file}"/]** within the _target_ maven folder. This can be started by executing the following command

    java -jar target/[# th:text="${jar_file}"/] [# th:text="${jar_parameters}"/]

[/]
[# th:if="${build_tool} == 'GRADLE'"]
[# th:if="${mp_servername} == 'payara-micro'"]
    ./gradlew microBundle
[/]
[# th:if="${mp_servername} == 'liberty'"]
    ./gradlew libertyPackage
[/]
[# th:if="${mp_servername} == 'helidon'"]
    ./gradlew assemble
[/]

This will create an executable jar file **[# th:text="${jar_file}"/]** within the _build/libs_ gradle folder. This can be started by executing the following command

[# th:if="${mp_servername} == 'payara-micro'"]
    ./gradlew microStart
[/]
[# th:if="${mp_servername} == 'liberty'"]
    ./gradlew libertyRun  --no-daemon
[/]
[# th:if="${mp_servername} == 'helidon'"]
    java -jar build/libs/[# th:text="${jar_file}"/]
[/]

[/]

[# th:if="${mp_servername} == 'liberty'"]
### Liberty's Dev Mode

During development, you can use Liberty's development mode (dev mode) to code while observing and testing your changes on the fly.
With the dev mode, you can code along and watch the change reflected in the running server right away; 
unit and integration tests are run on pressing Enter in the command terminal; you can attach a debugger to the running server at any time to step through your code.

[# th:if="${build_tool} == 'MAVEN'"]
    mvn liberty:dev
[/]

[# th:if="${build_tool} == 'GRADLE'"]
   ./gradlew libertyDev
[/]
[/]
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