FROM bellsoft/liberica-openjdk-alpine:latest-aarch64
ARG JAR_FILE=build/libs/ortem-bot-0.0.1.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]