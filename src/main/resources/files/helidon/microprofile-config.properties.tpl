[# th:if="${mp_config}"]
injected.value=Injected value
value=lookup value
[/]
# Microprofile server properties
server.port=[# th:text="${port_service_a}"/]
server.host=0.0.0.0

# src/main/resources/WEB in your source tree
server.static.classpath.location=/WEB
# default is index.html
#server.static.classpath.welcome=index.html

[# th:if="${mp_rest_client}"]
[# th:text="${java_package}"/].client.Service/mp-rest/url=http://localhost:[# th:text="${port_service_b}"/]/data/client/service
[/]
[# th:if="${mp_JWT_auth}"]
serviceb.url=http://localhost:[# th:text="${port_service_b}"/]/data/protected
[/]