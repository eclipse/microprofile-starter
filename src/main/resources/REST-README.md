```
  __  __ _                _____            __ _ _      
 |  \/  (_)              |  __ \          / _(_) |     
 | \  / |_  ___ _ __ ___ | |__) | __ ___ | |_ _| | ___ 
 | |\/| | |/ __| '__/ _ \|  ___/ '__/ _ \|  _| | |/ _ \
 | |  | | | (__| | | (_) | |   | | | (_) | | | | |  __/
 |_|__|_|_|\___|_|  \___/|_|   |_|  \___/|_| |_|_|\___|
  / ____| |           | |                              
 | (___ | |_ __ _ _ __| |_ ___ _ __                    
  \___ \| __/ _` | '__| __/ _ \ '__|                   
  ____) | || (_| | |  | ||  __/ |                      
 |_____/ \__\__,_|_|   \__\___|_|                      
```

# Project generator REST API

This document summarizes examples of MicroProfile Starter web application REST API
[microprofile/starter/1](https://app.swaggerhub.com/apis/microprofile/starter/1)

Each MicroProfile version (mpVersion) offers a suite of specifications (specs) and is implemented
by a set of application servers (supportedServers).

In order to get a project zip generated one has to select at least the desired server (supportedServer).
The latest mpVersion with no specs but the basic hello world JAX-RS endpoint is used by default then.

# Versions

There are currently 6 versions of the API to retain backward compatibility. ```/api/``` is the latest;
use e.g. ```/api/1/``` to get the legacy behaviour.

# Get available MicroProfile versions

```
$ curl https://start.microprofile.io/api/mpVersion

["MP22","MP21","MP20","MP14","MP13","MP12"]
```

# Get available supportedServers and specs

Note the order of supportedServers values is pseudorandom each call.
Note the JSON formatting was altered for this document.

```
$ curl https://start.microprofile.io/api/mpVersion/MP22
{
    "supportedServers": [
        "PAYARA_MICRO",
        "LIBERTY",
        "KUMULUZEE",
        "THORNTAIL_V2",
        "HELIDON"
    ],
    "specs": [
        "CONFIG",
        "FAULT_TOLERANCE",
        "JWT_AUTH",
        "METRICS",
        "HEALTH_CHECKS",
        "OPEN_API",
        "OPEN_TRACING",
        "REST_CLIENT"
    ]
}
```

# Getting project

Curl: ```-O -J``` makes curl to save the zip file in the current directory. ```-L``` makes curl to follow redirects.

## Minimal example

Note that latest available MP version for the supportedServer and **No** available Specs were selected by default.

```
$ curl -O -J 'https://start.microprofile.io/api/project?supportedServer=THORNTAIL_V2'

curl: Saved to filename 'demo.zip'

```

On the contrary, when API version 1 or 2 is used, **All** available Specs are selected by default.

```
$ curl -O -J 'https://start.microprofile.io/api/2/project?supportedServer=THORNTAIL_V2'

curl: Saved to filename 'demo.zip'

```

To get **All** available specs with the current latest API, use ```selectAllSpecs=true``` query param in the URL, e.g.:

```
$ curl -O -J 'https://start.microprofile.io/api/project?supportedServer=THORNTAIL_V2&selectAllSpecs=true'

curl: Saved to filename 'demo.zip'

```

## Examples

The only mandatory attribute is ```supportedServer```. One can omit ```-v``` curl flag.
Note the optional usage of [Etag](https://tools.ietf.org/html/rfc7232#section-2.3) and [If-None-Match](https://tools.ietf.org/html/rfc7232#section-3.2).

```
$ curl -v -O -J -L 'https://start.microprofile.io/api/project?supportedServer=PAYARA_MICRO&artifactId=XXXXX&mpVersion=MP12&selectedSpecs=FAULT_TOLERANCE&selectedSpecs=JWT_AUTH'

< ETag: "dd085c81"

curl: Saved to filename 'XXXXX.zip'
```

Note we can use ETag dd085c81 from the previous response:

```
curl -v -O -J -L -H 'If-None-Match: "dd085c81"' 'https://start.microprofile.io/api/project?supportedServer=PAYARA_MICRO&artifactId=XXXXX&mpVersion=MP12&selectedSpecs=FAULT_TOLERANCE&selectedSpecs=JWT_AUTH'

< HTTP/1.1 304 Not Modified
```

## Example with all attributes

```
$ curl -O -J -L 'https://start.microprofile.io/api/project?supportedServer=LIBERTY&groupId=com.example&artifactId=myapp&mpVersion=MP22&javaSEVersion=SE8&buildTool=MAVEN&selectedSpecs=CONFIG&selectedSpecs=FAULT_TOLERANCE&selectedSpecs=JWT_AUTH&selectedSpecs=METRICS&selectedSpecs=HEALTH_CHECKS&selectedSpecs=OPEN_API&selectedSpecs=OPEN_TRACING&selectedSpecs=REST_CLIENT'

curl: Saved to filename 'myapp.zip'
```

## Use JSON for get the project

By POSTing a JSON string one can get the same result as with using query parameters above.

```
$ curl -O -J -L -H "Content-Type: application/json" -d '{"mpVersion":"MP14","supportedServer":"TOMEE","selectedSpecs":["REST_CLIENT","CONFIG"]}' 'https://start.microprofile.io/api/project'

curl: Saved to filename 'demo.zip'
```

Note that if you omit ```"selectedSpecs":["REST_CLIENT","CONFIG"]```, nothing but a single Hello World JAX-RS resource is generated.
On the other hand, if you add ```"selectAllSpecs":"true"```, all available specs within given ```mpVersion``` are generated. The legacy ```/api/2/``` and prior
behaviour was to give you all available specs when none was selected.

This is how one can have the JSON stored in a file:

```
$ curl -O -J -L -H "Content-Type: application/json" -d @my.json 'https://start.microprofile.io/api/project'

curl: Saved to filename 'demo.zip'
```

## JSON All attributes used

```
$ cat all.json
{
  "groupId": "com.example",
  "artifactId": "myapp",
  "mpVersion": "MP22",
  "javaSEVersion": "SE8",
  "supportedServer": "THORNTAIL_V2",
  "buildTool": "MAVEN",
  "selectedSpecs": [
    "CONFIG",
    "FAULT_TOLERANCE",
    "JWT_AUTH",
    "METRICS",
    "HEALTH_CHECKS",
    "OPEN_API",
    "OPEN_TRACING",
    "REST_CLIENT"
  ]
}

$ curl -O -J -L -H "Content-Type: application/json" -d @all.json 'https://start.microprofile.io/api/project'

curl: Saved to filename 'myapp.zip'
```

## JSON Minimal example

Add ```"selectAllSpecs":"true"``` to get all specs examples.

```
$ curl -O -J -L -H "Content-Type: application/json" -d '{"supportedServer":"KUMULUZEE"}' 'https://start.microprofile.io/api/project'

curl: Saved to filename 'demo.zip'
```

# Errors

Note that if you select an invalid combination of selectedSpecs and supportedServer and mpVersion you get an error, e.g.:

```
$ curl -v -O -J -L 'https://start.microprofile.io/api/project?supportedServer=HELIDON&selectedSpecs=REST_CLIENT'

curl: Saved to filename 'error.json'

$ cat error.json 
{"error":"One or more selectedSpecs is not available for given mpVersion","code":"ERROR003"}
```

What happened: We did not specify mpVersion, so the latest for the given server at the time was selected by default, it means MP12 at the time of writing of this example. MP12 does not offer spec REST_CLIENT as we can see:

```
$ curl https://start.microprofile.io/api/mpVersion/MP12
{
  "supportedServers": [
    "PAYARA_MICRO",
    "THORNTAIL_V2",
    "KUMULUZEE",
    "TOMEE",
    "HELIDON",
    "WILDFLY_SWARM",
    "LIBERTY"
  ],
  "specs": [
    "CONFIG",
    "FAULT_TOLERANCE",
    "JWT_AUTH",
    "METRICS",
    "HEALTH_CHECKS"
  ]
}
```

## Windows users

### curl.exe examples

Curl is widely available on Windows too, e.g. [https://curl.haxx.se/windows/](https://curl.haxx.se/windows/)

```
C:\curl\bin>curl -O -J https://start.microprofile.io/api/project?supportedServer=THORNTAIL_V2&selectAllSpecs=true

curl: Saved to filename 'demo.zip'
```

```
C:\curl\bin>curl -O -J "https://start.microprofile.io/api/project?supportedServer=TOMEE&selectedSpecs=JWT_AUTH&selectedSpecs=CONFIG"

curl: Saved to filename 'demo.zip'
```

### Powershell examples

#### Minimal

```
PS C:\> Invoke-WebRequest -OutFile project.zip -Uri https://start.microprofile.io/api/project?supportedServer=LIBERTY
```

#### Using JSON

```
PS C:\> type .\all.json
{
  "groupId": "com.example",
  "artifactId": "myapp",
  "mpVersion": "MP22",
  "javaSEVersion": "SE8",
  "supportedServer": "THORNTAIL_V2",
  "selectedSpecs": [
    "CONFIG",
    "FAULT_TOLERANCE",
    "JWT_AUTH",
    "METRICS",
    "HEALTH_CHECKS",
    "OPEN_API",
    "OPEN_TRACING",
    "REST_CLIENT"
  ]
}
PS C:\> $headers = @{
>>   "Content-Type" = "application/json"
>> }
PS C:\> Invoke-WebRequest -InFile ./all.json -OutFile project.zip -Method Post -Uri "https://start.microprofile.io/api/project" -Headers $headers
```

# Integration with IDEs

If it is preferred to acquire all valid options in a one request the ```supportMatrix``` call is recommended.
The integration code [SHOULD](https://tools.ietf.org/html/rfc2119) use [Etag](https://tools.ietf.org/html/rfc7232#section-2.3) response header
and [If-None-Match](https://tools.ietf.org/html/rfc7232#section-3.2) request header so as to avoid unnecessary deserialization of identical responses.

The ```supportMatrix/servers``` endpoint returns the support for a Build Tool (Maven and or Gradle) for each MP version and runtime combination.

## MicroProfile versions as keys

```
$ curl -i https://start.microprofile.io/api/supportMatrix
...
ETag: "44730639"
...
{
    "configs": {
        "MP30": {
            "supportedServers": [
                "LIBERTY",
                "THORNTAIL_V2",
                "HELIDON"
            ],
            "specs": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ]
        },
        "MP22": {
            "supportedServers": [
                "THORNTAIL_V2",
                "LIBERTY",
                "KUMULUZEE",
                "PAYARA_MICRO",
                "HELIDON"
            ],
            "specs": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ]
        },
...
...other MP versions removed for brevity...
...
    },
    "descriptions": {
        "CONFIG": "Configuration - externalize and manage your configuration parameters outside your microservices",
        "OPEN_API": "Open API - Generate OpenAPI-compliant API documentation for your microservices",
        "HEALTH_CHECKS": "Health - Verify the health of your microservices with custom verifications",
        "REST_CLIENT": "Rest Client - Invoke RESTful services in a type-safe manner",
        "FAULT_TOLERANCE": "Fault Tolerance - all about bulkheads, timeouts, circuit breakers, retries, etc. for your microservices",
        "JWT_AUTH": "JWT Propagation - propagate security across your microservices",
        "OPEN_TRACING": "Open Tracing - trace the flow of requests as they traverse your microservices",
        "METRICS": "Metrics - Gather and create operational and business measurements for your microservices"
    }
}
```

Using value from ETag in If-None-Match:

```
$ curl '-HIf-None-Match: "44730639"' -i https://start.microprofile.io/api/supportMatrix
...
HTTP/1.1 304 Not Modified
```

## Server implementations as keys

```
$ curl -i https://start.microprofile.io/api/supportMatrix/servers
...
ETag: "7b99230f"
...
{
    "configs": {
        "LIBERTY": {
            "MP14": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP22": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP30": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP20": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP21": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP13": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP12": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS"
            ]
        },
        "PAYARA_MICRO": {
            "MP14": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP22": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP20": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP21": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP12": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS"
            ],
            "MP13": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ]
        },
        "THORNTAIL_V2": {
            "MP22": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP30": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP21": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ],
            "MP12": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS"
            ],
            "MP13": [
                "CONFIG",
                "FAULT_TOLERANCE",
                "JWT_AUTH",
                "METRICS",
                "HEALTH_CHECKS",
                "OPEN_API",
                "OPEN_TRACING",
                "REST_CLIENT"
            ]
        }
...
...other servers removed for brevity...
...
    },
    "descriptions": {
        "CONFIG": "Configuration - externalize and manage your configuration parameters outside your microservices",
        "OPEN_API": "Open API - Generate OpenAPI-compliant API documentation for your microservices",
        "HEALTH_CHECKS": "Health - Verify the health of your microservices with custom verifications",
        "REST_CLIENT": "Rest Client - Invoke RESTful services in a type-safe manner",
        "FAULT_TOLERANCE": "Fault Tolerance - all about bulkheads, timeouts, circuit breakers, retries, etc. for your microservices",
        "JWT_AUTH": "JWT Propagation - propagate security across your microservices",
        "OPEN_TRACING": "Open Tracing - trace the flow of requests as they traverse your microservices",
        "METRICS": "Metrics - Gather and create operational and business measurements for your microservices"
    }
}
```

Using value from ETag in If-None-Match:

```
$ curl '-HIf-None-Match: "7b99230f"' -i https://start.microprofile.io/api/supportMatrix/servers
...
HTTP/1.1 304 Not Modified
```
