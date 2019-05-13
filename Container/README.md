Building an image and running a container
=========================================

Development workflow
====================

Locally without Docker
----------------------

```
mvn thorntail:run -Pthorntail

```

Navigate to 127.0.0.1:8080 or http://127.0.0.1:8080/index.xhtml to see the app.

Docker build
------------
With at least Docker 17.05, one can build an image and either run it locally or push it to a registry.
Please note that the first build takes time, but subsequent runs add merely ```~12M``` of the war file.

```
mvn package -Pthorntail && unzip target/mp-starter-hollow-thorntail.jar -d target/mp-starter-hollow-thorntail
docker build -f Container/Dockerfile -t microprofile/start.microprofile.io:0.9.2 .
```

Run locally
-----------

```
docker run -p 127.0.0.1:8080:8080/tcp -d -i --name mp-starter microprofile/start.microprofile.io:0.9.2
docker stop -t 2 mp-starter && docker rm mp-starter
```

Push image to registry and restart service
------------------------------------------

```
docker push microprofile/start.microprofile.io:0.9.2
ssh ec2-user@aws-microstarter "sudo systemctl restart docker-compose@start.microprofile.io"
```

Subsequent pushes just upload ```~12M``` of the war file.

Example flow
------------

```
mvn package -Pthorntail && unzip target/mp-starter-hollow-thorntail.jar -d target/mp-starter-hollow-thorntail
docker build -f Container/Dockerfile -t microprofile/start.microprofile.io:0.9.2 .
docker push microprofile/start.microprofile.io:0.9.2
ssh ec2-user@aws-microstarter "sudo systemctl restart docker-compose@start.microprofile.io"
```

Recorded
--------
[![asciicast](https://asciinema.org/a/217550.svg)](https://asciinema.org/a/217550)


Production and CI
=================

The Dockerfile is a [multi-staged build](https://docs.docker.com/develop/develop-images/multistage-build).
The first stage downloads Maven, installs OpenJDK Devel, builds the application.

The second stage installs just OpenJDK Headless JVM and adds the uberjar contents from the previous stage.

Due to the application's dependency on JSF the image is very fat.
Refactoring the app so as it runs just with a plain servlet container is a possible improvement to be made.

Building an image
-----------------
Note the tag ```microprofile/start.microprofile.io:0.9.2``` is an example and you should use your own namespace.

```
docker build -f Container/Dockerfile.CI -t microprofile/start.microprofile.io:0.9.2 .
```

One can push the built image to a public DockerHub (one needs an account though):

```
docker push microprofile/start.microprofile.io:0.9.2
```

Running a container locally
---------------------------
The container accepts several environment properties one should use them to tweak the server.

```
docker run -e MY_LOGLEVEL=INFO \
              -e MY_IO_THREADS="8" \
              -e MY_TASK_MAX_THREADS="64" \
              -e MY_HTTP_PORT="8080" \
              -e MY_HTTPS_PORT="8443" \
              -e MY_MS_HEAP="64m" \
              -e MY_MX_HEAP="512m" \
              -e MY_META_SPACE="96M" \
              -e MY_MAX_META_SPACE="256m" \
   -p 127.0.0.1:8080:8080/tcp -d -i --name mp-starter microprofile/start.microprofile.io:0.9.2
```

One can watch the logs:

```
docker logs -f mp-starter
```

You can stop and remove it as:

```
docker stop -t 2 mp-starter && docker rm mp-starter
```

Debugging the image build
-------------------------
One can skip the final stage of the multistage build and just do the first stage:

```
docker build --target build-env -f Container/Dockerfile.CI -t microprofile/start.microprofile.io:0.9.2 .
```

You can start bash and look around without starting the application:

```
docker run -i --entrypoint=/bin/bash --name mp-starter microprofile/start.microprofile.io:0.9.2
```

If you have the container running already and you want to examine it, you can:

```
docker exec -t -i mp-starter bash
```


Using Systemd and Docker Compose
================================

If you don't need any orchestration and you would just like to play with the container on a single host,
the following might come handy.

Get a VM on the Internet
------------------------

After you are done installing Docker (use your package manager of choice) and Docker compose, e.g.

```
curl -L https://github.com/docker/compose/releases/download/1.23.2/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

Compose
-------

Have a Docker compose file created with env variables for the container. This example uses a small 1 vCPU 1G RAM VM.
Mind the name "mp-starter" in the path to the file.

```
cat /etc/docker/compose/mp-starter/docker-compose.yml
version: '3'
services:
  mp-starter:
    image: "microprofile/start.microprofile.io:0.9.2"
    user: wildfly
    ports:
      - 443:8443
      - 80:8080
    restart: always
    environment:
      MY_LOGLEVEL:         "INFO"
      MY_IO_THREADS:       "2"
      MY_TASK_MAX_THREADS: "32"
      MY_MS_HEAP:          "512m"
      MY_MX_HEAP:          "512m"
      MY_META_SPACE:       "96M"
      MY_MAX_META_SPACE:   "256m"
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

Although one probably cannot access it from the outside due to a firewall your IaaS provider has in place, so let's configure the Security Group/Firewall/Network Security, depends on your provider.

We haven't setup our own TLS infrastructure nor we have Elytron in Wildfly configured with Let's Encrypt, so let's hide
the system behind Cloudflare's balancer. This is the list of Cloudflare's IP ranges we are going to allow inbound traffic from: https://www.cloudflare.com/ips/

Now if you configure your domain and enforce HTTPS with Cloudflare you get your application running and accessible from
the Internet on HTTPS only.
