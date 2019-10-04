FROM centos:7
LABEL Author="Michal Karm Babacek <karm@redhat.com>"
ENV JAVA_VERSION 11
RUN yum install java-${JAVA_VERSION}-openjdk -y && yum clean all && rm -rf /var/cache/yum /tmp/* && \
    useradd -s /sbin/nologin wildfly
EXPOSE 8080/tcp
USER wildfly
WORKDIR /opt/
COPY --chown=wildfly:wildfly target/mp-starter-hollow-thorntail /opt/mp-starter-hollow-thorntail
COPY --chown=wildfly:wildfly target/mp-starter.war Container/start.sh /opt/
CMD ["/opt/start.sh"]
