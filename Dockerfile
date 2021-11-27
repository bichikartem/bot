FROM bellsoft/liberica-openjdk-alpine:latest-aarch64
ARG JAR_FILE=build/libs/ContoraTelegram-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=ortem", "-jar", "/app.jar"]