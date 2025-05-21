# backend/backend/Dockerfile
# Use a multi-stage build for a smaller, more secure image

# Stage 1: Build the application
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app

# Copy Maven/Gradle wrapper and build files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# If using Gradle:
# COPY gradlew build.gradle settings.gradle ./

# Download dependencies (to leverage Docker layer caching)
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline
# If using Gradle:
# RUN --mount=type=cache,target=/root/.gradle ./gradlew dependencies

# Copy the rest of the application code
COPY src/ ./src/

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests
# If using Gradle:
# RUN ./gradlew bootJar

# Stage 2: Create the final lean image
FROM openjdk:17-jre-slim
WORKDIR /app

# Expose the port your Spring Boot app listens on (default is 8080)
EXPOSE 8080

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar
# If using Gradle:
# COPY --from=builder /app/build/libs/*.jar app.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]