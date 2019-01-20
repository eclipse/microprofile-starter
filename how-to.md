# How to

## Add new template file

Before new template files can be used within the generated project, following steps need to be perfomed:

* Add the file, with an extension of `.tpl` into the directory _src\main\resources\files_.
* A custom version of the template for a certain implementation, can be put into a subdirectory with the name of the implementation.
* Add the file to the `files.lst`, you can use the command `find src/main/resources/files -type f -name "*.tpl" > src/main/resources/files.lst` to do this automatically.

Have a look at the method _org.eclipse.microprofile.starter.addon.microprofile.servers.MicroprofileServersAddon.createFiles()_ for example how template files can be used.

 