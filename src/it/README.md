# Test suite

All tests are contained in a one class called TestMatrixTest, each server runtime
has 4 identical test cases (with special tweaks only for Tomee), e.g. with Thorntail it is:

    public void thorntailEmpty() {
        testRuntime("THORNTAIL_V2", "thorntail",
                SpecSelection.EMPTY, new int[]{9990});
    }

    public void thorntailAll() {
        testRuntime("THORNTAIL_V2", "thorntail",
                SpecSelection.ALL, new int[]{9990, 8180, 10090});
    }

    public void thorntailAllButJWTRest() {
        testRuntime("THORNTAIL_V2", "thorntail",
                SpecSelection.ALL_BUT_JWT_REST, new int[]{9990});
    }

    public void thorntailJWTRest() {
        testRuntime("THORNTAIL_V2", "thorntail",
                SpecSelection.JWT_REST, new int[]{9990, 8180, 10090});
    }

 * Empty test case does not select any examples, so only the default HelloWorld endpoint is tested.
 * All selects all examples and tests them all (expect for Open Tracing as Jaeger is on TODO list).
 * AllButJWTRest selects and tests all but JWT and REST; the main purpose is to ensure that no service-a/service-b division takes place.
 * JWTRest selects and tests only JWT and REST.
 
 Those additional ports are for the final cleanup after test, for a loop that awaits socket closure.
 The main landing application port is parsed from README file, but there are other auxiliary ports that had to be hardcoded here.
 
# How does it work
REST API is used to download a zip. The zip file is unzipped.
README file(s) are parsed to get the first mvn command,
which is assumed to be the build one. The first java command from README is assumed to be the one to run the server.
Multiple flavours of builds, i.e. multiple ways to build the application is an unsupported feature in the TS at the moment.
Landing page address is also parsed from the README file as the first one beginning with http://.


Build commands are used to build the generated project(s). If you have never run the TS, this might take a long time. Mind
you are downloading all dependencies for THORNTAIL_V2, PAYARA_MICRO, LIBERTY, HELIDON, KUMULUZEE and TOMEE.

Run commands are then used to run the application(s).

A loop awaits them to be available and then each spec is tested, see MPSpec enum for definitions of expected content.

Last but not least, build and runtime logs are checked for errors. See Whitelist enum for whitelisting known and expected error messages.

At the very end of the execution, application(s) are terminated. There is also a loop that waits for a TCP socket to die.
# Platforms
The TS was tested on OpenJDK Java 11 J9 and HotSpot on Linux and with OpenJDK 11 HotSpot on Windows. With Windows,  ```powershell``` is used
instead of ```unzip``` and ```taskkill``` instead of ```kill```. There is also some ```wmic``` heuristics to clean hanging processes.
If you are running the TS on Mac, make sure your ```unzip``` can work with the arguments TS supplies or open a PR.

# Logging and logs
All logs are archived in ```target/archived-logs```. If a build or a server runtime fails, 
this is the first place to look for clues. Both maven build logs and runtime logs are archived. Unzip log is archived too.

## Whitelisting errors
See Whitelist enum for currently whitelisted errors per each server runtime.
Examples from the TS execution:

Kumuluzee:
```
Jan 17, 2020 4:14:44 PM org.eclipse.microprofile.starter.utils.Logs checkLog
INFO: Build log for kumuluzeeAllButJWTRest contains whitelisted error: 
  `[INFO] Copying error_prone_annotations-2.2.0.jar to /dev/shm/kumuluzee/target/classes/lib/error_prone_annotations-2.2.0.jar'
```
Open Liberty:
```
Jan 17, 2020 11:21:21 AM org.eclipse.microprofile.starter.utils.Logs checkLog
INFO: Runtime log for libertyEmpty contains whitelisted error: 
  `[ERROR   ] CWMOT0008E: OpenTracing cannot track JAX-RS requests because an OpentracingTracerFactory class was not provided.'
```

# Running the TS

Run some tests for a specific runtime only:

    mvn clean verify -Pthorntail -Dtest=TestMatrixTest#tomee* -Dskip.integration.tests=false -DSTARTER_TS_WORKSPACE=/dev/shm/
    
Debug a single test:

    mvn -DfoforkCount=0 -Dmaven.surefire.debug clean verify -Pthorntail -Dtest=TestMatrixTest#tomeeAll -Dskip.integration.tests=false -DSTARTER_TS_WORKSPACE=/dev/shm/
    
Starting on Windows, e.g.:

    mvn clean verify -Pthorntail -Dskip.integration.tests=false -DSTARTER_TS_WORKSPACE=C:\tmp\

Note that if you don't specify STARTER_TS_WORKSPACE, the fallback is whatever ```java.io.tmpdir``` returns on your system.

Happy testing.