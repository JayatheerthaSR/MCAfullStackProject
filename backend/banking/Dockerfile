# Use a multi-stage build for a smaller, more secure image

# Stage 1: Build the application
FROM openjdk:17-jdk-slim AS builder

# Set the working directory inside the container.
# This WORKDIR will be the base for all subsequent COPY and RUN commands.
# Since Render's "Root Directory" is backend/banking,
# the Docker build context implicitly starts there.
WORKDIR /app

# Copy the mvnw, .mvn directory, pom.xml, and src directory
# from the *current build context* (which Render has set to backend/banking/)
# into the /app directory in the container.
# Now, /app/pom.xml will be your backend/banking/pom.xml
COPY mvnw .mvn/ ./
COPY pom.xml ./
COPY src/ ./src/

# IMPORTANT: You still need to copy the frontend directory!
# Since the Render root is backend/banking/, the frontend is at ../frontend/
# So, copy it to /app/frontend in the Docker image for the frontend-maven-plugin.
COPY ../../frontend/ ./frontend/

# At this point, the backend banking project is at /app,
# and the frontend project is at /app/frontend.
# This means your pom.xml's relative path logic (../../frontend)
# will correctly resolve to /app/frontend from /app.

# Download dependencies (to leverage Docker layer caching)
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
# The JAR will be at /app/target/*.jar because /app was the WORKDIR for the build.
COPY --from=builder /app/target/*.jar app.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
