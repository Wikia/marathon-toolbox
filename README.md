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


### Project properties that change behaviour

- `marathon.forceUpdate` - makes app update override any deployments currently running
- `marathon.chooseMaxResourceValue` - does not override marathon assigned resource quotas if those quotas are higher than ones defined in the deployment
