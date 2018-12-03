#!/bin/bash

# @author Michal Karm Babacek

# Replace Logging level
sed -i "s/@MY_LOGLEVEL@/${MY_LOGLEVEL:-INFO}/g" /opt/wildfly/standalone/configuration/standalone.xml

# Assigned by the container
export HOSTNAME=`hostname`

# Run
/opt/wildfly/bin/standalone.sh \
 -c standalone.xml \
 -Djboss.modules.system.pkgs=org.jboss.byteman \
 -Djava.awt.headless=true \
 -Dmy.io.threads=${MY_IO_THREADS:-8} \
 -Dmy.task.max.threads=${MY_TASK_MAX_THREADS:-64} \
 -Djboss.http.port=${MY_HTTP_PORT:-8080} \
 -Djboss.https.port=${MY_HTTPS_PORT:-8443} \
 -Djboss.node.name="${HOSTNAME}" \
 -Djboss.host.name="${HOSTNAME}" \
 -Djboss.qualified.host.name="${HOSTNAME}" \
 -Djboss.bind.address="${HOSTNAME}"
