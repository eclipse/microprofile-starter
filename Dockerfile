# @author Michal Karm Babacek

# Multi-stage build: The final stage adds "just" JVM and Wildfly with a war file, but it is still very heavy:
# centos           7             75835a67d134  200MB
# karm/mp-starter  1.0-SNAPSHOT  f29fc6748793  501MB

# build stage
#############
FROM centos:7 AS build-env
LABEL Author="Michal Karm Babacek <karm@redhat.com>"
WORKDIR /opt
ENV MVN_VERSION  3.6.0
ENV WF_VERSION   14.0.1.Final
ENV JAVA_VERSION 1.8.0
ENV M2_HOME /opt/apache-maven-${MVN_VERSION}
ENV WF_HOME /opt/wildfly-${WF_VERSION}
ENV MODULES ${WF_HOME}/modules/system/layers/base
ENV JAVA_HOME /usr/lib/jvm/jre-${JAVA_VERSION}-openjdk
RUN yum install java-${JAVA_VERSION}-openjdk-devel unzip -y
RUN curl -L -O http://download.jboss.org/wildfly/${WF_VERSION}/wildfly-${WF_VERSION}.zip && \
    unzip wildfly-${WF_VERSION}.zip
RUN curl -L -O https://www-eu.apache.org/dist/maven/maven-3/${MVN_VERSION}/binaries/apache-maven-${MVN_VERSION}-bin.zip && \
    unzip apache-maven-${MVN_VERSION}-bin.zip
ADD Docker/standalone.xml ${WF_HOME}/standalone/configuration/
# Trim down unused jars. Skip this step if you don't want that.
ADD Docker/trim-jars-${WF_VERSION}.sh .
ARG DO_TRIMMING=true
RUN if [ "${DO_TRIMMING,,}" = "true" ]; then ./trim-jars-${WF_VERSION}.sh; fi
RUN echo 'JAVA_OPTS="\
 -server \
 -Xms${MY_MS_HEAP:-64m} \
 -Xmx${MY_MX_HEAP:-512m} \
 -XX:MetaspaceSize=${MY_META_SPACE:-96M} \
 -XX:MaxMetaspaceSize=${MY_MAX_META_SPACE:-256m} \
 -XX:+UseG1GC \
 -XX:+HeapDumpOnOutOfMemoryError \
 -XX:HeapDumpPath=/opt/wildfly \
"' >> ${WF_HOME}/bin/standalone.conf
ADD ["src", "pom.xml", "./"]
RUN mkdir src && mv main src/ && ./apache-maven-${MVN_VERSION}/bin/mvn package && \
    cp target/mp-starter.war ${WF_HOME}/standalone/deployments/ROOT.war
# Final package
RUN mv ${WF_HOME} /opt/wildfly


# final stage
#############
FROM centos:7
LABEL Author="Michal Karm Babacek <karm@redhat.com>"
ENV JAVA_VERSION 1.8.0
RUN yum install java-${JAVA_VERSION}-openjdk-headless -y && yum clean all && rm -rf /var/cache/yum /tmp/* && \
    useradd -s /sbin/nologin wildfly
EXPOSE 8443/tcp
EXPOSE 8080/tcp
USER wildfly
COPY --from=build-env --chown=wildfly:wildfly /opt/wildfly /opt/wildfly
ADD Docker/start.sh /opt/wildfly/bin/
WORKDIR /opt/wildfly/
CMD ["/opt/wildfly/bin/start.sh"]
