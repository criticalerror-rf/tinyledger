FROM gradle:jdk19 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle

RUN gradle dependencies --no-daemon

COPY src src

RUN gradle build --no-daemon

FROM openjdk:19-slim

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "--enable-preview", "-jar", "app.jar"]