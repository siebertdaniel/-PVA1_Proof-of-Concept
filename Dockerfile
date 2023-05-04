FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY config/config.properties config/config.properties
COPY target/pva1.jar pva1/app.jar
RUN apt update && apt install argon2
EXPOSE 8080
ENTRYPOINT ["java","-jar","pva1/app.jar"]