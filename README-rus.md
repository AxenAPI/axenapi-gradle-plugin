# axenapi-generator-plugin

axenapi-generator-plugin предназначен для генерации кода по спецификации асинхронного API в формате OpenAPI 3.*. 

## Подключение плагина 
1. скачайте, соберите и установите в свой локальный mvn repository (или удаленный) нужную вам версию проекта http://gitlab.ru-central1.internal/internal/swagger4kafka-generator/-/tree/develop (версия должна совпадать с версией плагина).
2. скачайте, соберите и установите в свой локальный mvn repository (или удаленный) нужную вам версию плагина. 
3. подключете плагин к вашему проекту:
```
plugins {
//...
    id 'axenapi-generator-plugin' version '<current version>'
//...
}
```
4. добавьте зависимоть компиляции от генерации кода:
```
compileJava {
    dependsOn "generateKafka"
}
```
5. опишите параметры генерации кода в build.gradle:
    * Генерация кода сервера:
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
    * Генерация кода клиента:
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
5. рекомендуется добавить сгенерированные файлы в src проекта:
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
6. По указанному в параметрах пути $outDir/$srcDir у вас будут сгенерирован код. 

## Описание параметров 

| Наименование | Тип | Обязательное | Значение по умолчанию | Описание 
| ------ | ------ | ------ | ------ | ------ |
| openApiPath | String | Да | Нет значения по умолчанию | Путь к спецификации в формате OpenAPI 3.*
| outDir | String | Да | Нет значения по умолчанию | Каталог, куда будет сложен сгенерированный код
| srcDir | String | Да | Нет значения по умолчанию | Путь к src каталогу. Рекомендуемое значение  `"src/main/java"`
| listenerPackage | String | Да | Нет значения по умолчанию | package, в который попадут сгеренированные client/listeners
| modelPackage | String | Да | Нет значения по умолчанию | package, к который попадут сгеренированные модели (Data Transfer Object)
| useSpring3 | Boolean | Нет | `false` | Если true, то генерация будет происзодить для springboot 3.1. Если false, то для spring boot 2.7
| kafkaClient | Boolean | Нет | `false` | Если true, будет генерироваться клиент (producer сообщений), false - интерфейсы сервера (consumer)
| interfaceOnly | Boolean | Нет | `true` | Влияет только на генерацию клинета. Если true - то будут сгенерированы классы реализации отправки сообщений в kafka. Если false - только интерфейсы.
| resultWrapper | String | Нет | `""` | Класс, в который будет обернуто возвращаемое значение. Необходимо описать полный путь к классу. 
| securityAnnotation | cell | Нет | `""` | Класс аннотации, который выставляется при генерации сервера при использовании в consumer авторизации. Если ничего не указано, то security-аннотации не будут ставится. 
| sendBytes | cell | Нет | `true` | Если стоит `true`, то не будет отправлять header с маппингом типов на наименование headers. Если false - то будет.
| useAutoconfig | cell | Нет | `true` | Если `true`, то при генерации клиента будет сгенерированы файлы для автоконфигурации.
| generateMessageId | cell | Нет | `false` | Если `true`, то сгенерированный клиент будет автоматически проставлять header `kafka_messageId` (или другое наименование из параметра `messageIdName`). Значение - случайный UUID.
| generateCorrelationId | cell | Нет | `false` | Если `true`, то сгенерированный клиент будет автоматически проставлять header `kafka_correlationId` (или другое наименование из параметра `correlationIdName`). Значение - случайный UUID.
| messageIdName | cell | Нет | "kafka_messageId" | Наименование header, в который положится значение messageId (если `generateMessageId = true`)
| correlationIdName | cell | Нет | "kafka_correlationId" |  Наименование header, в который положится значение correlationId (если `generateCorrelationId = true`)

## Описание формата файла спецификации
Для каждого Listener формируется свой контроллер с именем <ListenerClassName>Controller.
Пример сгенерированного http метода:

Метод Post
Url: "/kafka/group-2/multiType/Subordinate"
Возвращает: Subordinate

Каждый consumer представляет из себя post метод с определенным форматом:

* все сгенерированные http интерфейсы - post методы
* все сгенерированные интерфейсы имеют url состоящий из 3-4 частей:
    * первая часть: всегда начинаются с kafka/ - позволяет отделить сгенерированные url от уже имеющихся в приложении http интерфейсов
    * вторая часть: группа - необязательная часть. Если url состоит из 3 частей, то считается, что группа не указана.
    * третья часть: наименование топика
    * четвертая часть: наименование считываемой из топика модели данных (DTO)

> 💡 Остальная логика не нуждается в дополнительном описании. По сгенерированным контроллерам теперь есть возможность создать спецификацию в OpenAPI формате. Описание формата: https://spec.openapis.org/oas/latest.html. Авторизация и счема авторизации указывается по правилам OpenAPI 3.*

Работа с хедерами:

Для обозначения хедеров к concumer используются query params - можно указать тип и обязательность. Каждые query param - это отдельный header сообщения. 

> :bulb: Query params указываются по правилам формата OpenAPI 3.*

> :warning: `__TypeId__` не нужно указывать в хедерах. Сгенерированный клиент будет отправлять `__TypeId__` автоматически.

## Особенности генерации клиента

Сгенерированный клиент использует сгенерированный класс `KafkaSenderServiceImpl` реализующий интерфейс `KafkaSenderService` для отправки сообщений в топик. Если вы не хотите использовать сгенерированную реализацию, то вы можете создать свой Bean реализующий интерфейс `KafkaSenderService`.
