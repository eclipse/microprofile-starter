[# th:if="${mp_config}"]
injected.value=Injected value
value=lookup value
[/]
[# th:if="${mp_rest_client}"]
[# th:text="${java_package}"/].client.Service/mp-rest/url=http://localhost:[# th:text="${port_service_b}"/]/data/client/service
[/]
[# th:if="${mp_JWT_auth}"]
serviceb.url=http://localhost:[# th:text="${port_service_b}"/]/data/protected
[/]
