FROM openjdk:8-jdk-alpine

LABEL maintainer="artyomov.dev@gmail.com"

EXPOSE 8080

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} xml-xsd-validator.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/xml-xsd-validator.jar"]