<?xml version="1.0" encoding="UTF-8"?>
<server description="${project.name}">

    <featureManager>
        <feature>microProfile-[# th:text="${mp_version}"/]</feature>
    </featureManager>

    <httpEndpoint id="defaultHttpEndpoint"
                  httpPort="[# th:text="${port_service_a}"/]"
                  httpsPort="9443"/>

    <webApplication location="${project.name}.war" contextRoot="/">
        <classloader apiTypeVisibility="+third-party" />
    </webApplication>
    <mpMetrics authentication="false"/>

    <ssl id="defaultSSLConfig" trustDefaultCerts="true" />

    <!-- The MP JWT configuration that injects the caller's JWT into a ResourceScoped bean for inspection. -->
    <mpJwt id="jwtUserConsumer" keyName="theKeyId" audiences="targetService" issuer="${jwt.issuer}"/>

</server>
