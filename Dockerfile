FROM openjdk:17.0.2-jdk

WORKDIR /app

COPY build/libs/axenapi-gradle-plugin-1.0.0.jar ./app.jar

EXPOSE 8080

ENTRYPOINT exec java -jar ./app.jar
