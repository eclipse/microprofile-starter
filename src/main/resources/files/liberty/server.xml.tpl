<?xml version="1.0" encoding="UTF-8"?>
<server description="${project.artifactId}">

    <featureManager>
        <feature>microProfile-[# th:text="${mp_version}"/]</feature>
    </featureManager>

    <httpEndpoint id="defaultHttpEndpoint"
                  httpPort="${httpPort}"
                  httpsPort="${httpsPort}"/>

    <application location="${project.build.directory}/${project.build.finalName}.war"/>

    <logging traceSpecification="${log.name}.*=${log.level}"/>

    <!-- This is the keystore that will be used by SSL and by JWT. -->
    <keyStore id="defaultKeyStore" location="public.jks" type="JKS" password="atbash" />


    <!-- The MP JWT configuration that injects the caller's JWT into a ResourceScoped bean for inspection. -->
    <mpJwt id="jwtUserConsumer" keyName="theKeyId" audiences="targetService" issuer="${jwt.issuer}"/>

</server>
