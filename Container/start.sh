#!/bin/sh
# @author Michal Karm Babacek

# Assigned by the container
export HOSTNAME=`hostname`

# Run
java -server \
 -Xms${MY_MS_HEAP:-64m} \
 -Xmx${MY_MX_HEAP:-512m} \
 -XX:MetaspaceSize=${MY_META_SPACE:-96M} \
 -XX:MaxMetaspaceSize=${MY_MAX_META_SPACE:-256m} \
 -XX:+UseG1GC \
 -XX:+HeapDumpOnOutOfMemoryError \
 -XX:HeapDumpPath=/opt/mp-starter-hollow-thorntail/ \
 -XX:+ExitOnOutOfMemoryError \
 -cp /opt/mp-starter-hollow-thorntail \
 org.wildfly.swarm.bootstrap.Main \
 /opt/mp-starter.war \
 -Dswarm.io.workers.default.io-threads=${MY_IO_THREADS:-8} \
 -Dswarm.io.workers.default.task-max-threads=${MY_TASK_MAX_THREADS:-64} \
 -Dswarm.transactions.node-identifier=666 \
 -Dswarm.logging.root-logger.level=${MY_LOGLEVEL:-INFO} \
 -Dswarm.http.port=${MY_HTTP_PORT:-8080} \
 -Dswarm.https.port=${MY_HTTPS_PORT:-8443} \
 -Dswarm.undertow.servers.default-server.hosts.default-host.alias="${HOSTNAME}" \
 -Dswarm.bind.address="${HOSTNAME}" \
 -DAWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID}" \
 -DAWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY}" \
 -DMP_STARTER_APP_ID="${MP_STARTER_APP_ID:-test-instance}" \
 -DAWS_REGION="${AWS_REGION:-eu-west-1}" \
 -DAWS_DYNAMODB_TABLE_NAME="${AWS_DYNAMODB_TABLE_NAME:-microprofile_starter_log}"
