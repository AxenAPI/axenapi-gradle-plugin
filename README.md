# axenapi-generator-plugin

axenapi-generator-plugin is a plugin for generating code from asynchronous OpenAPI 3.* specification.
## Installing plugin
1. Download, build and install to your local(or remote) mvn repository required version of a project http://gitlab.ru-central1.internal/internal/swagger4kafka-generator/-/tree/develop (project version must be same as the plugin version).
2. Download, build and install to your local(or remote) mvn repository required plugin version.
3. Add a plugin dependency to your project:
```
plugins {
//...
    id 'axenapi-generator-plugin' version '<current version>'
//...
}
```
4. Add a compilation dependency on code generation:
```
compileJava {
    dependsOn "generateKafka"
}
```
5. Add code generation parameters to your build.gradle:
- Server code generation:
```
codegenData {
    openApiPath =  getProjectDir().getAbsolutePath() + '/src/main/resources/json.json'
    outDir = getProjectDir().getAbsolutePath() + '/build'
    srcDir = 'src/main/java'
    listenerPackage = 'swagger4kafka.listener'
    modelPackage = 'swagger4kafka.model'
    kafkaClient = false
    interfaceOnly = false
    resultWrapper = 'java.util.concurrent.CompletableFuture'
}
```
- Client code generation:

```
    codegenData {
        openApiPath =  getProjectDir().getAbsolutePath() + '/src/main/resources/test.json'
        outDir = getProjectDir().getAbsolutePath() + '/build'
        srcDir = 'src/main/java'
        listenerPackage = 'swagger4kafka.client'
        modelPackage = 'swagger4kafka.model'
        kafkaClient = true
        interfaceOnly = false
        useSpring3 = true
        resultWrapper
    }
```
6. It is recommended to add generated files to src directory of your project:
```
sourceSets {
    main {
        java {
            srcDirs '/build/src/main/java'
        }
        resources {
            srcDir '/build/src/main/resources'
        }
    }
}
```
7. The code will be generated to a directory specified in $outDir/$srcDir parameters.

## Parameters description:

| Name| Type| Required| Default value | Description
| ------ | ------ | ------ | ------ | ------ |
| openApiPath | String | Yes | No default value | Path to OpenAPI 3.* specification.
| outDir | String | Yes | No default value | Directory, where generated code will be stored.
| srcDir | String | Yes | No default value | Path to src directory. Recommended value is `"src/main/java"`.
| listenerPackage | String | Yes | No default value | Package, in which client/listeners will be generated.
| modelPackage | String | Yes | No default value | Package, in wich models will be generated (Data Transfer Object).
| useSpring3 | Boolean | No | `false` | If `true`, then code will be generated for springboot 3.1. If `false`, then code will be generated for spring boot 2.7.
| kafkaClient | Boolean | No | `false` | If `true`, client code(producer) will be generated, if `false` - server code(consumer).
| interfaceOnly | Boolean | No | `true` | Affects only client generation. If `true` - Kafka consumer implemenation classes will be generated, if `false` - only iterfaces.
| resultWrapper | String | No | `""` | Class, in which return value will be wrapped. Full path to that class must be specified.
| securityAnnotation | cell | No | `""` | Annotation class which will be used in consumer code generation if consumer authorization is implemented. If this parameter is not specified, security annotations will not be generated.
| sendBytes | cell | Нет | No | If `true`, then headers with types mapped by header names will not be used. If `false`, then types will be mapped.
| useAutoconfig | cell | No | `true` | If `true`, then autoconfiguation files will be generated alongside clients.
| generateMessageId | cell | No | `false` | If `true`, then generated clients will use header `kafka_messageId`(or other name specified in `messageIdName` parameter) by default. Header value will be random UUID.
| generateCorrelationId | cell | No | `false` | If `true`, then generated clients will use header `kafka_correlationId` (or other name specified in `correlationIdName` parameter) by default. Header value will be random UUID.
| messageIdName | cell | No | "kafka_messageId" | Name of the header, in which `messageId` value will be stored(If `generateMessageId = true`)
| correlationIdName | cell | No | "kafka_correlationId" | Name of the header header, in which `correlationId` value will be stored(If `generateCorrelationId = true`)

## Specification format:
For every `Listener` controller <ListenerClassName>Controller will be generated.
Example of generated http method:

* POST method:
* Url: "/kafka/group-2/multiType/Subordinate"
* Returns: Subordinate

Every `consumer` consists of POST method of format:

* All generated http interfaces - are POST methods
* All generated interfaces have url, consisting of 3-4 parts:
    * first part: always starts with kafka/ - which allows to separate generated url from already existing in applicatio http interfaces.
    * second part: group - is optional. If url consists of 3 parts - that means that group is not specified.
    * third part: topic name.
    * fourth part: Name of data models being read from topic(DTO).

> 💡 Other logic does not require additional description. You can create OpenAPI specification from generated controller. Format descriptipn: https://spec.openapis.org/oas/latest.html. Authorization should be described by OpenAPI 3.* specification.

Using headers:

You can use `query params` to describe headers for consumers  - type and optionality can be specified. Every query param is a separate message header.

> :bulb: Query params is described by OpenAPI 3.* specification.

> :warning: `__TypeId__` should not be specified in headers. Generated clients will send `__TypeId__` automatically.

## Client generation features

Generated clients use generated `KafkaSenderServiceImpl` implementations of `KafkaSenderService` inerface to send messages to topics. If you do not want to use generated implementation, you can create custom Bean, implementing `KafkaSenderService` interface.