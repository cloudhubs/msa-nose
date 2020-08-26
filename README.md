# MSANose

This library is built using Spring Boot to detect 11 code smells in microservice applications.

## Getting Started

These instructions will get you a copy of the project up and running.

### Prerequisites

There are a couple prerequisites to cover before running.

#### Installing ws4j

1. [Download the Jar](https://code.google.com/archive/p/ws4j/downloads)
2. Add the jar to your local mvn repository

```
 mvn install:install-file -Dfile=<path-to-jar> -DgroupId=com.sciss -DartifactId=ws4j -Dversion=1.0.1 -Dpackaging=jar 
```

## Endpoints

In this section, there are brief overviews of each endpoint.

### /api/v1/report 

This endpoint will run all of the other endpoints and aggregate them into a single report object. Additionally, it will time each code-smell detection and report their times.

### /api/v1/apis 

This endpoint will find all of the unversioned APIs and report them as a list.

### /api/v1/apis 

This endpoint will find all of the shared libraries and report them as a list.

### /api/v1/wrongCuts 

This endpoint will detect any microservice that is cut wrongly. It will also find the number of entities in each microservice.

### /api/v1/cyclicDependency 

This endpoint will give a boolean that is true if any cycles are detected in the microservice dependency graph.

### /api/v1/sharedPersistency 

This endpoint will find any shared persistencies and report a list of the offending microservices and their shared persistency.

### /api/v1/esbUsage 

This endpoint will return a list of potential ESB microservices.

### /api/v1/noAPIGateway 

This endpoint will return true if an API gateway should be used (microservice count > 50).

### /api/v1/inappropriateServiceIntimacy 

This endpoint will return a list of inappropriately similar microservices along with their similarity scores.

### /api/v1/tooManyStandards 

This endpoint will return the standards used by the application for the presentation, business and data layers.

### /api/v1/microservicesGreedy 

This endpoint will return a list of greedy microservices.

## Using the Endpoints

Each of the endpoints is called using a POST operation with the body as follows:

```
{
    "pathToCompiledMicroservices": "/<path-to-microservices>/",
    "organizationPath": "",
    "outputPath": ""
}
```

## Authors

* [**Andrew Walker**](https://github.com/walker76)

## Acknowledgments

This material is based upon work supported by the National Science Foundation under Grant No. 1854049 and a grant from [Red Hat Research](https://research.redhat.com).