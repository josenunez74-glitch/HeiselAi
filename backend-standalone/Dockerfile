FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

COPY build.gradle.kts .
COPY src ./src

RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV PORT=8080
ENV OLLAMA_URL=${OLLAMA_URL:-http://ollama:11434}
ENV SD_URL=${SD_URL:-http://stablediffusion:7860}

ENTRYPOINT ["java", "-jar", "app.jar"]
