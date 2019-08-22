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
        url: [# th:text="${secondary_url}"/]/data/client/service
injected:
  value: Injected value
value: lookup value
