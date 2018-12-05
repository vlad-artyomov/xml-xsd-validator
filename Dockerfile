FROM openjdk:8-jdk-alpine

LABEL maintainer="artyomov.dev@gmail.com"

EXPOSE 8080

EXPOSE 8000

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} xml-xsd-validator.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n","-jar","/xml-xsd-validator.jar"]