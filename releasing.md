# Procedures around PRs and releases

## General

- Each PR should have a corresponding issue.
- Each issue should have a correct milestone (semantic versioning rules) attached to it.
- PRs are merged into master after 2 acks (creator PR and acks should be from at minimum 2 companies/community users)
- When releasing, a branch (using the same name as milestone) is created and tagged with that name.
- This branch is put as the production version on the website.
- After releasing to production, create a [release in GitHub repo](https://github.com/eclipse/microprofile-starter/releases) describing the release.

## HotFixing

For a hotfix, the rules are a bit relaxed as it deals with fixing some important issues in production.

- Create an issue describing the problem and the reason for the hotfix.
- Create PR and merge into master (we can accept that there are no acks required to do this since it is a hotfix)
- Start from the current branch released in production (since master can contain already more stuff)
- Create another branch with the correct branch name using semantic versioning rules and release.

## Releases on test site

Releases on the [test site](https://test-start.microprofile.io/) are performed automatically each time a PR is merged into the master branch.

- Changes are pulled regularly into [repo](https://github.com/Karm/microprofile-starter/).
- Circle CI performs build and push to the server.
- Message is pushed to the [gitter chat](https://gitter.im/eclipse/microprofile-starter) when updates are live.

## Releases on production

Releasing on production requires a manual step and can only be performed by those who have the SSH key for the production machine.

- mvn package -Pthorntail
- unzip target/mp-starter-hollow-thorntail.jar -d target/mp-starter-hollow-thorntail
- docker build -f Container/Dockerfile -t microprofile/start.microprofile.io:_1.0_ .
- docker login (with an account which can push to https://hub.docker.com/r/microprofile/start.microprofile.io)
- docker push microprofile/start.microprofile.io:_1.0_
- Edit image version within _docker-compose.yml_ file at aws-microstarter server (match with the one you have pushed)
- Restart Docker container ( ssh -i <<rsa-key-location>> ec2-user@aws-microstarter "sudo systemctl restart docker-compose@start.microprofile.io")

 
