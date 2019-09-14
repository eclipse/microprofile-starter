# MicroProfile generated Application

## Introduction

MicroProfile Starter has generated this MicroProfile application for you.

The generation of the executable jar file can be performed by issuing the following command

    mvn clean package

This will create an executable jar file **[# th:text="${jar_file}"/]** within the _target_ maven folder. This can be started by executing the following command

    java -jar target/[# th:text="${jar_file}"/]

To launch the test page, open your browser at the following URL

    http://localhost:[# th:text="${port_service_a}"/]/index.html

## Specification examples

By default, there is always the creation of a JAX-RS application class to define the path on which the JAX-RS endpoints are available.

Also, a simple Hello world endpoint is created, have a look at the class **HelloController**.

More information on MicroProfile can be found [here](https://microprofile.io/)

[# th:if="${mp_config}"]
### Config

Configuration of your application parameters. Specification [here](https://microprofile.io/project/eclipse/microprofile-config)

The example class **ConfigTestController** shows you how to inject a configuration parameter and how you can retrieve it programmatically.
[/]

[# th:if="${mp_fault_tolerance}"]
### Fault tolerance

Add resilient features to your applications like TimeOut, RetryPolicy, Fallback, bulkhead and circuit breaker. Specification [here](https://microprofile.io/project/eclipse/microprofile-fault-tolerance)

The example class **ResilienceController** has an example of a FallBack mechanism where an fallback result is returned when the execution takes too long.
[/]

[# th:if="${mp_health_checks}"]
### Health

The health status can be used to determine if the 'computing node' needs to be discarded/restarted or not. Specification [here](https://microprofile.io/project/eclipse/microprofile-health)

The class **ServiceHealthCheck** contains an example of a custom check which can be integrated to health status checks of the instance.  The index page contains a link to the status data.
[/]

[# th:if="${mp_metrics}"]
### Metrics

The Metrics exports _Telemetric_ data in a uniform way of system and custom resources. Specification [here](https://microprofile.io/project/eclipse/microprofile-metrics)

The example class **MetricController** contains an example how you can measure the execution time of a request.  The index page also contains a link to the metric page (with all metric info)
[/]

[# th:if="${mp_JWT_auth}"]
### JWT Auth

Using the OpenId Connect JWT token to pass authentication and authorization information to the JAX-RS endpoint. Specification [here](https://microprofile.io/project/eclipse/microprofile-rest-client)

Have a look at the **TestSecureController** class which calls the protected endpoint on the secondary application.
The **ProtectedController** (secondary application) contains the protected endpoint since it contains the _@RolesAllowed_ annotation on the JAX-RS endpoint method.

The _TestSecureController_ code creates a JWT based on the private key found within the resource directory.
However, any method to send a REST request with an appropriate header will work of course. Please feel free to change this code to your needs.
[/]

[# th:if="${mp_open_API}"]
### Open API

Exposes the information about your endpoints in the format of the OpenAPI v3 specification. Specification [here](https://microprofile.io/project/eclipse/microprofile-open-api)

The index page contains a link to the OpenAPI information of your endpoints.

[/]

[# th:if="${mp_open_tracing}"]
### Open Tracing

Allow the participation in distributed tracing of your requests through various micro services. Specification [here](https://microprofile.io/project/eclipse/microprofile-opentracing)

Example needs to be created.
[/]

[# th:if="${mp_rest_client}"]
### Rest Client

A type safe invocation of HTTP rest endpoints. Specification [here](https://microprofile.io/project/eclipse/microprofile-rest-client)

The example calls one endpoint from another JAX-RS resource where generated Rest Client is injected as CDI bean.
[/]