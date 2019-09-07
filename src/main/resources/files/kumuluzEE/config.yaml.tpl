kumuluzee:
  name: [# th:text="${maven_artifactid}"/]
  version: 1.0-SNAPSHOT
  env:
    name: dev
  metrics:
    web-instrumentation:
      - name: testEndpoint
        url-pattern: data/hello/*
  rest-client:
    registrations:
      - class: [# th:text="${java_package}"/].client.Service
        url: http://localhost:[# th:text="${port_service_b}"/]/data/client/service
  server:
    http:
      port: [# th:text="${port_service_a}"/]
injected:
  value: Injected value
value: lookup value
