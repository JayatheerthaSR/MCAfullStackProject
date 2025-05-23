# Use a multi-stage build for a smaller, more secure image
FROM openjdk:17-jdk-slim AS builder

# Set the working directory inside the container.
# This will be the base for copying all backend files.
# The source paths are relative to the "root of the repository" (MCAPIFullStackProject/)
# because the build log indicates the build context is the repository root.
WORKDIR /app

# Copy the backend/banking directory contents specifically
# Assuming your Dockerfile is in backend/banking, these paths are relative to the build context (repository root)
COPY backend/banking/mvnw /app/backend/banking/mvnw
COPY backend/banking/.mvn /app/backend/banking/.mvn
COPY backend/banking/pom.xml /app/backend/banking/pom.xml
COPY backend/banking/src /app/backend/banking/src

# Now, copy the frontend directory from its location relative to the repository root
# to a specific location in the container.
# This assumes it's needed for the frontend-maven-plugin.
# THE CRUCIAL CHANGE IS HERE: Copy 'frontend' to '/app/frontend' directly.
COPY frontend /app/frontend

# Change WORKDIR to the backend/banking directory where the pom.xml is located
WORKDIR /app/backend/banking

# Ensure mvnw is executable
RUN chmod +x mvnw

# --- DEBUG STEP 1: List contents of /app ---
# This will show you what's directly under /app. It should show 'backend' and 'frontend'.
RUN ls -la /app

# --- DEBUG STEP 2: List contents of /app/backend ---
# This will show you what's directly under /app/backend. It should show 'banking'.
RUN ls -la /app/backend

# --- DEBUG STEP 3: List contents of /app/frontend ---
# This is crucial to confirm 'frontend' was copied correctly.
# It should show 'public', 'src', 'package.json', etc.
RUN ls -la /app/frontend

# Download dependencies (to leverage Docker layer caching)
# Use --mount=type=cache for buildkit for faster builds.
# If buildkit is not enabled, you can remove this part and rely on standard caching.
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# --- DEBUG STEP 4: Verify frontend build output exists (if the build even reaches this far) ---
# This step might not be reached if the previous RUN fails
RUN ls -R /app/frontend/build

# --- DEBUG STEP 5: Verify resources copied into target/classes/static (if the build even reaches this far) ---
# This step might not be reached if the previous RUN fails
RUN ls -R target/classes/static

# Stage 2: Create the final lean image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8080
# Copy the built JAR from the builder stage
# The JAR will be at /app/backend/banking/target/*.jar because that was the WORKDIR for the Maven build.
COPY --from=builder /app/backend/banking/target/*.jar app.jar
# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
