<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Eclipse MicroProfile demo</title>
</head>
<body>

<h2>MicroProfile</h2>

<a href="data/hello" target="_blank" >Hello JAX-RS endpoint</a> <br/>

[# th:if="${mp_config}"]
<h3>Config</h3>
<a href="data/config/injected" target="_blank" >Injected config values</a> <br/>
<a href="data/config/lookup" target="_blank" >Config values by lookup</a> <br/>
[/]

[# th:if="${mp_fault_tolerance}"]
<h3>Fault tolerance</h3>
<a href="data/resilience" target="_blank" >Fallback after timeout</a> <br/>
[/]

[# th:if="${mp_health_checks}"]
<h3>Health</h3>
<a href="health/live" target="_blank" >Health (Live) status (with custom status)</a> <br/>
<a href="health/ready" target="_blank" >Health (Ready) status (with custom status)</a> <br/>
[/]

[# th:if="${mp_metrics}"]
<h3>Metrics</h3>
<a href="data/metric/timed" target="_blank" >Timed endpoint</a> <br/>
<a href="metrics" target="_blank" >Metrics page</a> <br/>
[/]

[# th:if="${mp_JWT_auth}"]
<h3>JWT Auth</h3>
<a href="data/secured/test" target="_blank" >Call Secured endpoint with JWT in Authorization Header</a> <br/>
[/]

[# th:if="${mp_open_API}"]
<h3>Open API</h3>
<a href="openapi" target="_blank" >Open API Documentation</a> <br/>
[/]

[# th:if="${mp_open_tracing}"]
<h3>Open Tracing</h3>
If you have <pre>./jaeger-all-in-one</pre> running, open <a href="http://localhost:16686/">http://localhost:16686</a>
[/]

[# th:if="${mp_rest_client}"]
<h3>Rest Client</h3>
<a href="data/client/test/parameterValue" target="_blank" >Call REST endpoint using generated client based on interface</a> <br/>
[/]

</body>
</html>