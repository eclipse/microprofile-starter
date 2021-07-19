injected.value=Injected value
value=lookup value
quarkus.package.output-name=[# th:text="${maven_artifactid}"/]
[# th:if="${mp_rest_client}"]
[# th:text="${java_package}"/].client.Service/mp-rest/url=http://localhost:[# th:text="${port_service_b}"/]/data/client/service
[/]
quarkus.ssl.native=true
[# th:if="${mp_JWT_auth}"]
quarkus.smallrye-jwt.enabled=false
[/]
[# th:if="${mp_open_tracing}"]
quarkus.jaeger.service-name=Demo-Service-A
quarkus.jaeger.sampler-type=const
quarkus.jaeger.sampler-param=1
quarkus.jaeger.endpoint=http://localhost:14268/api/traces
[/]
[# th:if="${mp_JWT_auth}"]
# These options are needed only if you build your project into a native executable.
quarkus.native.additional-build-args=-H:Log=registerResource:,-H:IncludeResources=privateKey.pem,--initialize-at-run-time=io.vertx.ext.auth.impl.jose.JWT
[/]
