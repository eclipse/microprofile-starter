#!/bin/sh
echo "Trimming jars..."
rm -rf ${WF_HOME}/welcome-content/* ${WF_HOME}/docs ${WF_HOME}/domain ${WF_HOME}/.galleon ${WF_HOME}/.installation ${WF_HOME}/.well-known \
${WF_HOME}/appclient ${WF_HOME}/bin/domain* ${WF_HOME}/bin/common.bat \
${WF_HOME}/bin/common.ps1 ${WF_HOME}/bin/add-user.* ${WF_HOME}/bin/appclient.* ${WF_HOME}/bin/client \
${WF_HOME}/bin/domain.* ${WF_HOME}/bin/elytron-tool.* ${WF_HOME}/bin/jboss-cli* ${WF_HOME}/bin/.jbossclirc \
${WF_HOME}/bin/jconsole.* ${WF_HOME}/bin/jdr.* ${WF_HOME}/bin/vault.* ${WF_HOME}/bin/wildfly-elytron-tool.jar ${WF_HOME}/bin/ws* \
${MODULES}/nu \
${MODULES}/gnu \
${MODULES}/org/antlr \
${MODULES}/org/jboss/ejb \
${MODULES}/org/jboss/remoting3 \
${MODULES}/org/jboss/as/vault-tool \
${MODULES}/org/jboss/as/domain-add-user \
${MODULES}/org/jboss/as/pojo \
${MODULES}/org/jboss/as/configadmin \
${MODULES}/org/jboss/as/xts \
${MODULES}/org/jboss/as/sar \
${MODULES}/org/jboss/as/host-controller \
${MODULES}/org/jboss/as/modcluster \
${MODULES}/org/jboss/as/appclient \
${MODULES}/org/jboss/as/management-client-content \
${MODULES}/org/jboss/as/cmp \
${MODULES}/org/jboss/as/messaging \
${MODULES}/org/jboss/as/mail \
${MODULES}/org/jboss/resteasy/resteasy-multipart-provider \
${MODULES}/org/jboss/resteasy/resteasy-yaml-provider \
${MODULES}/org/jboss/resteasy/resteasy-json-binding-provider \
${MODULES}/org/jboss/resteasy/resteasy-jackson2-provider \
${MODULES}/org/jboss/resteasy/resteasy-crypto \
${MODULES}/org/jboss/resteasy/resteasy-json-p-provider \
${MODULES}/org/jboss/resteasy/resteasy-jaxb-provider \
${MODULES}/org/jboss/resteasy/jose-jwt \
${MODULES}/org/jboss/resteasy/resteasy-jackson-provider \
${MODULES}/org/jboss/resteasy/resteasy-rxjava2 \
${MODULES}/org/jboss/resteasy/resteasy-cdi \
${MODULES}/org/jboss/resteasy/resteasy-jettison-provider \
${MODULES}/org/jboss/resteasy/resteasy-atom-provider \
${MODULES}/org/jboss/resteasy/resteasy-spring \
${MODULES}/org/jboss/resteasy/resteasy-jsapi \
${MODULES}/org/jboss/genericjms \
${MODULES}/org/jboss/mod_cluster \
${MODULES}/org/jboss/remoting-jmx \
${MODULES}/org/fusesource \
${MODULES}/org/glassfish/soteria \
${MODULES}/org/jaxen \
${MODULES}/org/hornetq \
${MODULES}/org/apache/lucene \
${MODULES}/org/apache/avro \
${MODULES}/org/apache/velocity \
${MODULES}/org/apache/santuario \
${MODULES}/org/apache/httpcomponents \
${MODULES}/org/apache/qpid \
${MODULES}/org/apache/james \
${MODULES}/org/apache/openjpa \
${MODULES}/org/apache/neethi \
${MODULES}/org/apache/commons/codec \
${MODULES}/org/apache/commons/io \
${MODULES}/org/apache/commons/lang3 \
${MODULES}/org/apache/commons/beanutils \
${MODULES}/org/apache/commons/collections \
${MODULES}/org/apache/commons/lang \
${MODULES}/org/apache/thrift \
${MODULES}/org/jdom \
${MODULES}/org/hibernate/jipijapa-hibernate5-3 \
${MODULES}/org/hibernate/search \
${MODULES}/org/hibernate/jipijapa-hibernate4-3 \
${MODULES}/org/hibernate/4.1 \
${MODULES}/org/hibernate/5.0 \
${MODULES}/org/hibernate/jipijapa-hibernate5 \
${MODULES}/org/hibernate/4.3 \
${MODULES}/org/hibernate/commons-annotations \
${MODULES}/org/hibernate/infinispan \
${MODULES}/org/hibernate/5.3 \
${MODULES}/org/hibernate/envers \
${MODULES}/org/wildfly/embedded \
${MODULES}/org/wildfly/mod_cluster \
${MODULES}/org/wildfly/microprofile \
${MODULES}/org/infinispan/cachestore \
${MODULES}/org/infinispan/hibernate-cache \
${MODULES}/org/eclipse/persistence \
${MODULES}/org/eclipse/microprofile \
${MODULES}/org/opensaml \
${MODULES}/org/picketlink \
${MODULES}/org/yaml \
${MODULES}/org/jboss/as/console
echo "Silence." > ${WF_HOME}/welcome-content/index.html