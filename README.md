# Usage #
## Including plugin in the project

In the build.gradle add following lines
```
buildscript {
    dependencies {
        classpath "com.wikia.gradle:marathon-gradle-plugin:0.5.40"
    }
}
```

## Example of using plugin for deployments using maven repository

The application needs to launch on specific port taken from environment variable `PORT0`. 
The following snippets will allow deployment of JVM based apps. 
 

```
apply plugin: 'application'
apply plugin: 'com.wikia.gradle.marathon'
apply plugin: 'maven-publish'

## define artifact publishing using gradle maven-publish
publishing {
    publications {
        applicationZip(MavenPublication) {
            artifact distZip # in this case we use application zip as main means of deployments
        }
    }
    repositories {
        maven {
            credentials {
                username "username"
                password "password"
            }
            url "http://some.maven.repository.com/deployments/"
        }
    }
}

## define deployment stage
deployments {
    globalDefaults { # 'globalDefaults' is a special stage that will be applied to first to all other stages
        app {
            mavenSource( # defines source of deployment 
                    "http://some.maven.repository.com/deployments/", 
                    "publishApplicationZipPublicationToMavenRepository" # publishing task name
            )
        }
        healthchecks {
            healthcheck {
                portIndex = 1
                path = "/healthcheck"
                maxConsecutiveFailures = 20
                intervalSeconds = 10
            }
        }        
    }
    development { # 'development' can be almost any arbitrary string        
        marathon {
            marathonUrl = "http://marathon.development.url:8080"
        }
        resources {
            cpus = 1
            mem = 200
            disk = 10
            instances = 10
            useRandomPorts(1)
        }
    }
}
```

## deploying application

```gradle deployDevelopment```

# Configuration options

## Stage definition

```
deployments {
    stageName {
    }
}
```

Where stage name can be any arbitrary string e.g. `dev`, `prod`
 
### Stages and deployment 

`stageName` will be used to create deployment tasks `deployStageName` and `setupStageName` notice upcased StageName


#### `setupStageName` task

Only sends processed configuration to marathon


#### `deployStageName` task

First deploys artifact using what was configured e.g. `com.wikia.gradle.marathon.common.App.mavenSource` method 

Then it launches `setupStageName` task.

###  Default stage `globalDefaults`

`globalDefaults` is special stage that is preppended to any other stage this it can be used to provide
global defaults for all stages so that there is less configuration duplications

### Stage overriding and stacking

`deployments { stageName { } }` can be defined multiple times it all subsequent definitions of `stageName` 
closure will be stacked together, it also mean that `stageName` once defined cannot be overriden all 
options inside it can only be individually replaced so.

```
deployments {
  stageName {
    resources {
      mem = 100
      cpu = 1
    }
  }
  stageName {
    resources {
      cpu = 10
    }
    resources {
      instances = 4
    }
  }
}
```

Above definitions will result in following configuration being included in app definition sent to marathon

```
"resources": {
  "cpu": 10,
  "mem": 100,
  "instances": 4
}
```

# In stage options

## `resources`

Settable properties:
- `cpu` - floating point number of CPU shares assigned to the service.
Where 1.0 CPU means that service is *guaranteed* to have *minimum* computing power of 1 CPU 
- `mem` - number in MB of allocated memory.
This is strict quota if service goes over this limit it will be OOM killed by the system.
- `disk` - number in MB of maximum disk space available to the service. If exceeded task will be killed by the system.
- `instances` - number of instances that will be launched valid values are 0 to 9001+
- `ports` - array of port numbers that application wants to acquire.
-  `requirePorts` - when used with `ports` it will allow service to get exactly the specified ports in `ports`

Helper methods:
- `useRandomPorts(n)` - will cause mesos to assign *n* random ports to the service 

### Example
```
deployments {
  stage {
    resources {
      cpu = 1
      instances = 3
      mem = 100
    }
  }
}
```

## `environment`

Allows setting environment variables. It supports both direct values and closures.

### Example
 
```
deployments {
  stage {
    environment {
        SOME_VAR value
        SOME_OTHER_VAR { value in closure }
    }
  }
}
```

## `marathon`

Allows configuring various marathon behaviors.

Settable properties:
- `marathonUrl` - points to marathon endpoint e.g. 'http://some.marathon.host:8080'
- `backoffFactor` - multiplies that us used to extend time taken between two consecutive unsuccessful deployments.
In other words - each next unsuccessful deployment will be delayed by multiplying current deploy by `backoffFactor`
- `backoffSeconds` - initial number of seconds between each attempted deployments
- `maxLaunchDelaySeconds` - maximum delay between two consecutive attempts
- `id` - this sets Marathon application id. By default id taken from environment, project and group name

Inner configuration closures:
- `upgradeStrategy { }`
- `labels { }`
- `constraints { }`

### `marathon { upgradeStrategy { } }`

Allows configuring upgrade strategy

Settable properties:
- `minimumHealthCapacity` - number between 0 and 1. Minimum capacity maintained during application restarts/deployments.
Consult marathon documentation for more in depth information
- `maximumOverCapacity` - number between 0 and 1. Maximum additional capacity spawned during restart/deployment

### `marathon { labels {} }`

Marathon supports additional metadata that can be assigned to application in the form of labels in `key` = `value` format.

### `marathon { constraints {} }`

Allows setting deployment [constraints](https://mesosphere.github.io/marathon/docs/constraints.html)

Helper methods:
- `constraints(attribute, operator)`
- `constraints(attribute, operator, value)

Example:

```
marathon {
  id = "/service-name"
  marathonUrl = "http://some.marathon.host:8080"
  labels {
    key = value
    other = value
  }
  constraints {
    constraint("hostname", "GROUP_BY")
  }
}
```

## `app`

Allows setting various data needed to download, and execute the application.

Settable properties:
- `mavenPublishTaskName` - task name used to handle artifact upload in `mavenSource` source 
- `cacheFetching` - controls caching of artifacts by mesos
- `extractFetched` - controls should artifact be extracted or not

Helper methods:
- `dropwizardCommand(command[, configName])` - used to configure dropwizard application that has some dw specific arguments 
- `executeApplicationZip(args...)` - can be used with any application packed using zip and whose executable is `bin/<applicationName>`
 
Note some preliminary support for docker images is also added, but this is not yet documented and is not used in production anywhere yet.

## `healthchecks`

Allows configuring healtchecks for application. Configured by adding one or more inner `healthcheck` closures.

### `healthchecks { healthcheck { } }`

Settable properties:
- `path` - absolute URI path to healthcheck endpoint 
- `protocol` - tcp or http, by default its http
- `portIndex` - among ports assigned by marathon this fields selects one of the ports for querying the healtcheck
- `gracePeriodSeconds`- health check failures are ignored within this number of seconds of the task being started or until the task becomes healthy for the first time.
- `intervalSeconds` - number of seconds to wait between health checks
- `timeoutSeconds` - number of seconds after which a health check is considered a failure regardless of the response
- `maxConsecutiveFailures` - number of consecutive health check failures after which the unhealthy task should be killed


## Gradle settable properties (-P or via property.ext)

- `marathon.forceUpdate` - makes app update override any deployments currently running
- `marathon.chooseMaxResourceValue` - does not override marathon assigned resource quotas if those quotas are higher than ones defined in the deployment
- `marathon.isConfirmationNotNeeded` - disables the confirmation dialog
