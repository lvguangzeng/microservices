FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/gateway-server.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]