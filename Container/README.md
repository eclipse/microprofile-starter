Building an image and running a container
=========================================

Development workflow
====================

Locally without Docker, with Liberty
------------------------------------

```
./mvn clean package liberty:run -Pliberty
```

Open: http://localhost:9080/starter/index.xhtml

Note that the test suite requires Quarkus runtime, so it is
not supported to run tests with Liberty.

Locally without Docker, with Quarkus
------------------------------------

```
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```
Note the tests might take an hour as they download artifacts for all tested servers.
Use ` -Dtest=APIITCase` for just a quick API check.

Navigate to http://127.0.0.1:8080 to see the app.

Live coding available with:
```
 ./mvnw quarkus:dev
```

Docker build
------------

```
./mvnw clean package
docker build -f ./Container/Dockerfile.jvm -t microprofile/start.microprofile.io:master .
```

Run locally
-----------
e.g.
```
docker run --network=host -it -d -it --name starter -e AWS_ACCESS_KEY_ID=changeit \
     -e AWS_SECRET_ACCESS_KEY=changeit -e MP_STARTER_APP_ID=local-test -e AWS_REGION=changeit \
     -e AWS_DYNAMODB_TABLE_NAME=microprofile_test_starter_log \
     -e JAVA_OPTS="-Xms64m -Xmx128m" microprofile/start.microprofile.io:master
```

Push image to registry and restart service
------------------------------------------

```
docker push microprofile/start.microprofile.io:master
ssh ec2-user@aws-microstarter "sudo systemctl restart docker-compose@test-start.microprofile.io"
```

Using Systemd and Docker Compose
================================

If you don't need any orchestration, and you would just like to play with the container on a single host,
the following might come handy.

Get a VM on the Internet
------------------------

After you are done installing Docker (use your package manager of choice) and Docker compose:

Compose
-------

Have a Docker compose file created with env variables for the container. This example uses a small 1 vCPU 1G RAM VM.
Mind the name "mp-starter" in the path to the file.

```
cat /etc/docker/compose/mp-starter/docker-compose.yml
version: '3'
services:
  mp-starter:
    image: "microprofile/start.microprofile.io:master"
    ports:
      - 80:8080
    restart: always
    environment:
      AWS_ACCESS_KEY_ID:       "changeit"
      AWS_SECRET_ACCESS_KEY:   "changeit"
      MP_STARTER_APP_ID:       "changeit"
      AWS_REGION:              "changeit"
      AWS_DYNAMODB_TABLE_NAME: "microprofile_test_starter_log"
      JAVA_OPTS:               "-Xms64m -Xmx128m"
```

Systemd unit
------------

Next, let's create a Systemd unit that would control Docker compose:

```
cat /etc/systemd/system/docker-compose@.service
[Unit]
Description=%i service with docker compose
Requires=docker.service
After=docker.service
[Service]
Restart=always
WorkingDirectory=/etc/docker/compose/%i
# Remove old containers, images and volumes
ExecStartPre=/usr/local/bin/docker-compose down -v
ExecStartPre=/usr/local/bin/docker-compose rm -fv
ExecStartPre=-/bin/bash -c 'docker volume ls -qf "name=%i_" | xargs docker volume rm'
ExecStartPre=-/bin/bash -c 'docker network ls -qf "name=%i_" | xargs docker network rm'
ExecStartPre=-/bin/bash -c 'docker ps -aqf "name=%i_*" | xargs docker rm'
# Pull updated layers
ExecStartPre=-/usr/local/bin/docker-compose pull
# Compose up
ExecStart=/usr/local/bin/docker-compose up
# Compose down, remove containers and volumes
ExecStop=/usr/local/bin/docker-compose down -v
[Install]
WantedBy=multi-user.target
```

We can enable the unit now to have it running on VM startup:

```
systemctl enable docker-compose@mp-starter
```

And let's start it and give it a try:

```
systemctl start docker-compose@mp-starter
```

Verify it is running:

```
docker ps -a
curl localhost
```
