Building an image and running a container
=========================================

The Dockerfile is a [multi-staged build](https://docs.docker.com/develop/develop-images/multistage-build).
The first stage downloads WildFly, Maven, installs OpenJDK devel, builds the application and trims down
unused jars in WildFly. The trimming could be switched off by providing ```--build-arg DO_TRIMMING=false```
to the Docker build command.

The second stage installs just OpenJDK headless JVM and adds the WildFly directory from the previous stage.

Due to the application's dependency on JSF, it pulls in Weld and the whole EE stack thus making the image very fat.
Refactoring the app so as it runs just with a plain servlet container is a possible improvement.

Building an image
-----------------
Note the tag ```karm/mp-starter:1.0-SNAPSHOT``` is an example and you should use your own namespace.

```docker build -t karm/mp-starter:1.0-SNAPSHOT .```

One can push the built image to a public DockerHub (one needs an account though):

```docker push karm/mp-starter:1.0-SNAPSHOT```

Running a container locally
---------------------------
The container accepts several environment properties one should use them to tweak the server. Note that if you run the container
on a 36 core host the WildFly server would erroneously assume it has 36 cores at its disposal disregarding
the fact that you might set a cgroup cap on cores to 2 or 4. ```MY_TASK_MAX_THREADS``` and ```MY_IO_THREADS``` default values below
correspond to 4 cores.

```docker run -e MY_LOGLEVEL=INFO \
              -e MY_IO_THREADS="8" \
              -e MY_TASK_MAX_THREADS="64" \
              -e MY_HTTP_PORT="8080" \
              -e MY_HTTPS_PORT="8443" \
              -e MY_MS_HEAP="64m" \
              -e MY_MX_HEAP="512m" \
              -e MY_META_SPACE="96M" \
              -e MY_MAX_META_SPACE="256m" \
   -p 127.0.0.1:8443:8443/tcp -p 127.0.0.1:8080:8080/tcp -d -i --name mp-starter karm/mp-starter:1.0-SNAPSHOT```

One can watch the logs:
```docker logs -f mp-starter```

You can stop and remove it as:
```docker stop -t 2 mp-starter && docker rm mp-starter```

Debugging the image build
-------------------------
One can skip the final stage of the multistage build and just do the first stage:
```docker build --target build-env -t karm/mp-starter:1.0-SNAPSHOT .```

You can start bash and look around without starting the WildFly server:
```docker run -i --entrypoint=/bin/bash --name mp-starter karm/mp-starter:1.0-SNAPSHOT```

If you have the container running already and you want to examine it, you can:
```docker exec -t -i mp-starter bash```


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
    image: "karm/mp-starter:1.0-SNAPSHOT"
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
