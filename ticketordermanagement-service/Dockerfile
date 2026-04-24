FROM gradle:8.14-jdk17 AS builder
WORKDIR /workspace
COPY . .
RUN gradle clean bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
