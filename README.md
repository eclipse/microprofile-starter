# microprofile-starter

[![Gitter](https://badges.gitter.im/eclipse/microprofile-starter.svg)](https://gitter.im/eclipse/microprofile-starter?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Web

Live tool at [MicroProfile starter - Generate MicroProfile Maven Project](https://start.microprofile.io/index.xhtml)

## REST API

See [documentation](./src/main/resources/REST-README.md).

## Procedures around PRs and releases

See [documentation](./releasing.md).

## When can an implementation be added to the MicroProfile Tool

Pre-requisites to be added as an implementation:

- Ensure that your implementation has passed the MicroProfile TCKs for an umbrella release.
- Add your implementation under the right MicroProfile version at page [MicroProfile implementation](https://wiki.eclipse.org/MicroProfile/Implementation)
- Create a GitHub issue for adding your implementation to the MicroProfile Starter project at [Issues page](https://github.com/eclipse/microprofile-starter/issues)
- Do a Pull Request on the MicroProfile Starter project (this project) to add your new integration.

## Technical documentation

Information about adding
* a new MicroProfile Implementation
* an additional MicroProfile Version for an Implementation
* a new MicroProfile Specification

is found in the [how to document](https://github.com/eclipse/microprofile-starter/blob/master/how-to.md).

It is also possible to build and deploy to Open Liberty using the Liberty profile. To build the MP Starter app, use: `mvn package -Pliberty liberty:run` and then browse to http://localhost:9080/mp-starter/ to test the app.