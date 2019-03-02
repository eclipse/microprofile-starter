mp:
  jwt:
    verify:
      issuer: "https://server.example.com"

security:
  providers:
    - mp-jwt-auth:
        atn-token:
          # must use helidon specific configuration, as otherwise MP validation could fail if public key location is defined
          #   as well
          verify-key: "/publicKey.pem"
    - abac:
