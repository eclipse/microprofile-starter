kumuluzee:
  name: [# th:text="${maven_artifactid}"/]
  version: 1.0-SNAPSHOT
  env:
    name: dev
  jwt-auth:
    public-key: MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtJt3yCtGjZOpFo/YEJTs3sF9VuaZfvh7cigTg/CBbkcEGvRinJVgOBhI/otnQAnTxeGyViCPVr99Bt6ANf56LyZBNpEjs1hmQ5ZKIj0acAV00I5Mn5orzRVmcomiI2978r+D5s2h1HV37ZiBdD5aRxnW+VXRswRgnvvF6/8r4bu+wJHqiStzeZOZqVzs9mxqS20eKxG93C2h972Gz/RvJR9BP7nvduDq2JVSkoukYyhtgZ5q50E42KLgzyAecj6T36qcgciIcYuJTJLZn3paXF16VzRmeuqNKprZYEA4VADTOC2JtozJIc3Q+y4Kf9zT1hYJzYZxk69Hux/OdPBIKwIDAQAB
    issuer: https://server.example.com
  server:
    http:
      port: [# th:text="${port_service_b}"/]

