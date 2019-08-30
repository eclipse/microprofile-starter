kumuluzee:
  name: [# th:text="${maven_artifactid}"/]
  version: 1.0-SNAPSHOT
  env:
    name: dev
  jwt-auth:
    public-key: MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzwDtyf6ePOjf/oELJcX7AuFXLe3AFTIu79yOpk1w4s8Pia6TE0TVVHJ1X9MpiiMRh5LW56rRSQX5H5SHSyMTOT8DwdtydHoZJTjTavMxqoxWUCPU1NR93tdibewFJ+EykGj17QUGpcm2+msk+0YYjTa1Bhjcx/sC9WpPI1QBBaS7hXH6IycLIRd5sjX1yJwKkSBghHkARcma4fESvj8aBjomJlySYMQ8bX69HDFWu8tIHT7kqhA0DQ/r7fJFUJOO3CvHhh9cqaD35HpnjTqY4w0WiSLqA4FxDgkMR1U9ajeDo1jnwGEWV7X78usaz/q0uaNIKoP9bWWxe11Jq5uOrwIDAQAB
    issuer: https://server.example.com
  server:
    http:
      port: [# th:text="${port_service_b}"/]

