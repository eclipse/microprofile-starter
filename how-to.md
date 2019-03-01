# How to

## Add new MicroProfile Implementation

* Add the implementation to the enum **org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer**   
    The first parameter is the 'code' for the new implementation.  
    The second parameter is the value for the dropdown in the UI.  
    The third parameter is the list of supported MicroProfile Versions  
    `IMPL_X("implementationx", "Name of Implementation", Arrays.asList(MicroProfileVersion.MP14, MicroProfileVersion.MP20, MicroProfileVersion.MP21))`
* Add a 'profile' to the file `src/main/resources/pom)servers.xml` for each Version specified for this new implementation in the previous step.   
   The id of the profile must have the structure `<code>-<version>` with the code the value you specified in the previous bullet and version the MicroProfile version this _profile_ must be used for.  
   This profile should generate an executable jar file.  
   `<profile>
      <id>implementationx-2.0</id>
         <build>
            <plugins>
            ...
   `
* Add the generated executable jar file in the method **defineJarFileName()** and the URL of the root page of the _index.xhtml_ page within the method **defineTestURL** of **org.eclipse.microprofile.starter.addon.microprofile.servers.MicroprofileServersAddon**
* Test out the generated project with this new MicroProfile implementation.
    Start the Starter application (see **/Container/README.md in this repository** on how to start starter locally), select MP version and your new implementation and click on download. Test out the Maven project in the downloaded ZIP file) 
* In case the implementation requires adjusted example files (which is, however, an indication of some problem following the specification) you can create a customized template within the directory `src/main/resources/files/<code>`.  
   This adjusted template file will be picked up automatically.
* If needed, the _pom.xml_ generation and which files are created can be customized through the methods **adaptMavenModel()** and **createFiles()** of **org.eclipse.microprofile.starter.addon.microprofile.servers.MicroprofileServersAddon**

## Add a new MicroProfile Version to an Implementation

* Add the version to the list of supported version of the implementation (see enum **org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer**)   
* Add a 'profile' to the file `src/main/resources/pom)servers.xml`.  
   The id of the profile must have the structure `<code>-<version>` with the code the value you specified in the previous bullet and version the MicroProfile version this profile must be used for.  
   This profile should generate an executable jar file.  
   `<profile>
      <id>implementationx-2.0</id>
         <build>
            <plugins>
            ...
   `
* Test out the generated project with this new version.
   Start the Starter application (see **/Container/README.md in this repository** on how to start starter locally), select MP version and your new implementation and click on download. Test out the Maven project in the downloaded ZIP file)
* In case the implementation requires adjusted example files (which is, however, an indication of some problem following the specification) you can create a customized template within the directory `src/main/resources/files/<code>`
   This adjusted template file will be picked up automatically.

## Adding new Specification

* Add a new value to the enum **org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec**    
     The first parameter is the 'code' for the new specification.  
     The second parameter is the value for the selection of the specification in the UI.  
     The third parameter is the list of MicroProfile Version the specification is part of.
* Add an `if-structure` to the method **createFiles()** of **org.eclipse.microprofile.starter.addon.microprofile.servers.MicroprofileServersAddon** so that files are included in the demo application when the specification is selected.
* Update the **readme.md.tpl** to include some basic information about this new specification. Make this addition conditional (see other specification on how to do this)

## Add new template file

The template files contains the file contents (java source, resources, ...) which will be placed in the generated project. For the following cases you need to add new template files

- You want a slightly changed file for a specific implementation
- You need additional files for a new specification which will be supported by MicroProfile Starter.

The templates are processed by _ThymeLeaf_ templating engine and thus templating logic should follow those rules (like [# th:text="${xyz}"/]`, `[# th:if="${rst}"]Conditional shown[/]`)

Before new template files can be used, following steps need to be performed:

* Add the file, with an extension of `.tpl` into the directory _src\main\resources\files_.
* A custom version of the template for a certain implementation, can be put into a subdirectory with the name of the implementation.
* Add the file to the `files.lst`, you can use the command `find src/main/resources/files -type f -name "*.tpl" > src/main/resources/files.lst` to do this automatically.

Have a look at the method _org.eclipse.microprofile.starter.addon.microprofile.servers.MicroprofileServersAddon.createFiles()_ for example how template files can be used.

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

 