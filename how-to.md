# How to

## Add new MicroProfile Implementation

* Add the implementation to the enum **org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer**   
    
    |Parameter|Description|
    |---------|-----------|
    |Code     | Identification of the MP Implementation.|
    |Dropdown Label | Name of the MP Implementation in the UI dropdown.|
    |Supported MP Versions | List of MicroProfile versions supported by the Implementation.|
    |JAR Filename | File name or pattern of the generated jar by the MP implementation (Maven plugin). When the implementation generates a name based on the _artifact_, a placeholder can be used. For example `%s-microbundle.jar`. |
    |Program options | Command line options to start the _service-b_ generated artifact on a different HTTP port. For example, `--port 8180` to set the port explicitly. If not set by command line option, this parameter can be an empty string. |
    |Test URL     | URL of the test page provided by _service_a_ to easily access the generated examples of the Specifications|
    |Secondary URL   | URL of the _service_b_ deployment|
    
    Example
    `IMPL_X("implementationx"
    , "Name of Implementation"
    , Arrays.asList(MicroProfileVersion.MP14, MicroProfileVersion.MP20, MicroProfileVersion.MP21))
    , "%s-microbundle.jar" 
    , "--port 8180" 
    , "http://localhost:8080" 
    , "http://localhost:8180" `
* Add a 'profile' to the file `src/main/resources/pom-servers.xml`.   
   The id of the profile must be the _code_ you specified in the previous bullet. See also the section _ Use a specific 'profile' for a server - MicroProfile version combination_ for an alternative.
   This profile should generate an executable jar file.  
   `<profile>
      <id>implementationx</id>
         <build>
            <plugins>
            ...
   `
* Create an add-on for the server, for examples, see the package`org.eclipse.microprofile.starter.addon.microprofile.servers.server`
* The method `addonName()` must return the code used in the `SupportedServer enum and the _pom.xml_ generation and which files are created, can be customized through the methods **adaptMavenModel()** and **createFiles()**.  
* Test out the generated project with this new MicroProfile implementation.
    Start the Starter application (see **/Container/README.md in this repository** on how to start locally), select MP version and your new implementation and click on download. Test out the Maven project in the downloaded ZIP file.
* In case the implementation requires adjusted example files (which is, however, an indication of some problem following the specification) you can create a customized template within the directory `src/main/resources/files/<code>`.  
   This adjusted template file will be picked up automatically when the rules described in the section **Add a new template file** are followed. 

## Add a new Supported MicroProfile Version for an Implementation

* Add the version to the list of supported versions of the implementation (see enum **org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer**)   
* Adapt the method **adaptMavenModel()** of the specific server add-on.  
* Test out the generated project with this new version.
   Start the Starter application (see **/Container/README.md in this repository** on how to start starter locally), select MP version and your new implementation and click on download. Test out the Maven project in the downloaded ZIP file.
* In case the implementation requires adjusted example files (which is, however, an indication of some problem following the specification) you can create a customized template within the directory `src/main/resources/files/<code>`
   This adjusted template file will be picked up automatically when the rules described in the section **Add a new template file** are followed.

## Use a specific 'profile' for an Implementation - MicroProfile version combination

* The selection of the 'profile' within the `src/main/resources/pom-servers.xml` file is:
   1. Based on the implementation and MP version -> `<code>-<version>`
   2. If not found, based on the server -> `<code>`
* In both cases, customizations, like version numbers can be performed by adding properties in the **adaptMavenModel()** of the add-on.

Example: 
When user selects Thorntail - MP Version 2.2 as combination within the UI, the following profile ids are search in the `pom-servers.xml` file: 
- thorntail-2.2
- thorntail

If a specific one for the MP version exists, the `thorntail-2.2` profile is used, otherwise the more 'generic' one `thorntail` will be used.
 
## Adding new MicroProfile Specification

* Add a new value to the enum **org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec**

    |Parameter|Description|
    |---------|-----------|
    |Code     | Identification of the MP Specification.|
    |Dropdown Label | Name of the MP Specification in the UI.|
    |Description | Longer description used by the REST API.|
    |Supported MP Versions | List of MicroProfile versions containing the specification.|

* Add an `if-structure` to the method **createFiles()** of **org.eclipse.microprofile.starter.addon.microprofile.servers.MicroprofileServersAddon** so that files are included in the demo application when the specification is selected.
* Update the **readme.md.tpl** to include some basic information about this new specification. Make this addition conditional (see other specification on how to do this)

## Adding a new MicroProfile Version

* Add the enum value for the new version to the **org.eclipse.microprofile.starter.core.model.MicroProfileVersion**. The third parameter is only required when the maven MicroProfile version is not the same as _version value_ (as for example with version 2.0)
* Add to each MicroProfile specification this new enum value in the class **org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec**. (since existing specifications will also be part of the new MicroProfile version)

As long as there are no MicroProfile implementations assigned to this new MicroProfile version, the version isn't shown in the dropdown.

## Add new template file

The template files contains the file contents (java source, resources, ...) which will be placed in the generated project. For the following cases you need to add new template files

- You want a slightly changed file for a specific implementation
- You need additional files for a new specification which will be supported by MicroProfile Starter.

The templates are processed by _ThymeLeaf_ templating engine and thus templating logic should follow those rules (like [# th:text="${xyz}"/]`, `[# th:if="${rst}"]Conditional shown[/]`)

Before the new template files can be used, the following steps need to be performed:

* Add the file, with an extension of `.tpl` into the directory _src\main\resources\files_.
* A custom version of the template for a certain implementation, can be put into a subdirectory with the name of the implementation.
* When there is a specific version required for inclusion within `service-b`, use that as the name of the subdirectory.
* Add the file to the `files.lst`, you can use the command `find src/main/resources/files -type f -name "*.tpl" > src/main/resources/files.lst` to do this automatically.

Have a look at the method _org.eclipse.microprofile.starter.addon.microprofile.servers.MicroprofileServersAddon.createFiles()_ for examples how the template files can be used.

As an example, this is how we could create a custom _index.html_ file for _implementationx_

Create a file in the directory _src/main/resources/files/implementationx/index.html.tpl_  
The contents is then (as example, not realistic as the index.html file would become empty)  
```
 <!DOCTYPE html>
 <html lang="en">
 <head>
     <meta charset="UTF-8">
     <title>Eclipse MicroProfile demo</title>
 </head>
 <body>
 
 <h2>MicroProfile</h2>
 </body>
 </html> 
```

 