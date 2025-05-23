# Use a multi-stage build for a smaller, more secure image

# Stage 1: Build the application
FROM openjdk:17-jdk-slim AS builder

# Set the working directory inside the container.
# This will be the base for copying all backend files.
WORKDIR /app/backend/banking

# Copy the mvnw, .mvn directory, pom.xml, and src directory
# from the host's backend/banking directory into the container.
# The source paths are relative to the *root of the repository* (MCAfullStackProject/)
# because the build log indicates the build context is the repository root.
COPY backend/banking/mvnw /app/backend/banking/
COPY backend/banking/.mvn/ /app/backend/banking/.mvn/
COPY backend/banking/pom.xml /app/backend/banking/
COPY backend/banking/src/ /app/backend/banking/src/

# Now, copy the frontend directory from its location relative to the repository root
# to a specific location in the container.
# This assumes it's needed for the frontend-maven-plugin.
COPY frontend/ /app/backend/frontend/

# Change WORKDIR to the backend/banking directory where the pom.xml is located
WORKDIR /app/backend/banking

# Download dependencies (to leverage Docker layer caching)
# Use --mount=type=cache for BuildKit for faster builds.
# If BuildKit is not enabled, you can remove this part and rely on standard caching.
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final lean image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Expose the port your Spring Boot app listens on (default is 8080)
EXPOSE 8080

# Copy the built JAR from the builder stage
# The JAR will be at /app/backend/banking/target/*.jar because that was the WORKDIR for the build.
COPY --from=builder /app/backend/banking/target/*.jar app.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
