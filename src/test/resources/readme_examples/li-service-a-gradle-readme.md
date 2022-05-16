# MicroProfile generated Application

## Introduction

MicroProfile Starter has generated this MicroProfile application for you.

The generation of the executable jar file can be performed by issuing the following command





    ./gradlew libertyPackage



This will create an executable jar file **liber.jar** within the _build/libs_ gradle folder. This can be started by executing the following command



    ./gradlew libertyRun --no-daemon





### Liberty Dev Mode

During development, you can use Liberty's development mode (dev mode) to code while observing and testing your changes on the fly.
With the dev mode, you can code along and watch the change reflected in the running server right away; 
unit and integration tests are run on pressing Enter in the command terminal; you can attach a debugger to the running server at any time to step through your code.




    ./gradlew libertyDev



To launch the test page, open your browser at the following URL

    http://localhost:9080/index.html  


## Next Step

Leave the server-a server running and proceed with the instructions contained in `demo/service-b/readme.md`.


## Specification examples

By default, there is always the creation of a JAX-RS application class to define the path on which the JAX-RS endpoints are available.

Also, a simple Hello world endpoint is created, have a look at the class **HelloController**.

More information on MicroProfile can be found [here](https://microprofile.io/)


### Config

Configuration of your application parameters. Specification [here](https://microprofile.io/project/eclipse/microprofile-config)

The example class **ConfigTestController** shows you how to inject a configuration parameter and how you can retrieve it programmatically.



### Fault tolerance

Add resilient features to your applications like TimeOut, RetryPolicy, Fallback, bulkhead and circuit breaker. Specification [here](https://microprofile.io/project/eclipse/microprofile-fault-tolerance)

The example class **ResilienceController** has an example of a FallBack mechanism where an fallback result is returned when the execution takes too long.



### Health

The health status can be used to determine if the 'computing node' needs to be discarded/restarted or not. Specification [here](https://microprofile.io/project/eclipse/microprofile-health)

The class **ServiceHealthCheck** contains an example of a custom check which can be integrated to health status checks of the instance.  The index page contains a link to the status data.



### Metrics

The Metrics exports _Telemetric_ data in a uniform way of system and custom resources. Specification [here](https://microprofile.io/project/eclipse/microprofile-metrics)

The example class **MetricController** contains an example how you can measure the execution time of a request.  The index page also contains a link to the metric page (with all metric info)



### JWT Auth

Using the OpenId Connect JWT token to pass authentication and authorization information to the JAX-RS endpoint. Specification [here](https://microprofile.io/project/eclipse/microprofile-jwt-auth)

Have a look at the **TestSecureController** class which calls the protected endpoint on the secondary application.
The **ProtectedController** (secondary application) contains the protected endpoint since it contains the _@RolesAllowed_ annotation on the JAX-RS endpoint method.

The _TestSecureController_ code creates a JWT based on the private key found within the resource directory.
However, any method to send a REST request with an appropriate header will work of course. Please feel free to change this code to your needs.



### Open API

Exposes the information about your endpoints in the format of the OpenAPI v3 specification. Specification [here](https://microprofile.io/project/eclipse/microprofile-open-api)

The index page contains a link to the OpenAPI information of your endpoints.




### Open Tracing

Allow the participation in distributed tracing of your requests through various micro services. Specification [here](https://microprofile.io/project/eclipse/microprofile-opentracing)

To show this capability, you need to download [Jaeger](https://www.jaegertracing.io/download/#binaries) and run ```./jaeger-all-in-one```. 
Alternatively, you can download the docker image of `all-in-one` using ```docker pull jaegertracing/all-in-one:${version}```,
followed by running the docker image. Refer to [Jaeger doc](https://www.jaegertracing.io/docs/) for more info.

Open [http://localhost:16686/](http://localhost:16686/) to see the traces. You have to invoke your demo app endpoint for any traces to show on Jaeger UI.




### Rest Client

A type safe invocation of HTTP rest endpoints. Specification [here](https://microprofile.io/project/eclipse/microprofile-rest-client)

The example calls one endpoint from another JAX-RS resource where generated Rest Client is injected as CDI bean.



### GraphQL

GraphQL is a remote data query language initially invented by Facebook and now evolving under it's own specification and community. MicroProfile GraphQL provides annotation-based APIs for building GraphQL services in Java. The specification is available [here](https://microprofile.io/project/eclipse/microprofile-graphql).

The example contains a limited version of the SuperHero example from the TCK and shows the Schema file for it. You can think of it as something a comic-book government agency might use to keep track of Super Heroes.


To get started, run the sample, then browse to: http://localhost:9080/graphql-ui - you can then issue a query of all known super heroes like this:
```
query allHeroes {
    allHeroes {
        name
        primaryLocation
        superPowers
        realName
    }
}
```
