# Microprofile server properties
server.port=[# th:text="${port_service_b}"/]
server.host=0.0.0.0

# src/main/resources/WEB in your source tree
server.static.classpath.location=/WEB
# default is index.html
#server.static.classpath.welcome=index.html

[# th:if="${mp_JWT_auth}"]
# configure JWT handling
mp.jwt.verify.issuer=https://server.example.com
mp.jwt.verify.publickey.location=publicKey.pem
# enable the security provider
security.providers.0.mp-jwt-auth
[/]