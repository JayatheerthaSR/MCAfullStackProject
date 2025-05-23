# Use a multi-stage build for a smaller, more secure image
FROM openjdk:17-jdk-slim AS builder

# Set the working directory inside the container.
# IMPORTANT: Perform copies from the build context (repository root) first,
# then change WORKDIR.
WORKDIR /app

# Copy the backend/banking directory first to maintain context.
# This assumes your project structure is:
# /MCAPIFullStackProject
#   /backend/banking/
#     mvnw
#     .mvn/
#     pom.xml
#     src/
#   /frontend/
#     ...
COPY backend/banking /app/backend/banking
COPY frontend /app/frontend

# Now set the WORKDIR to the banking application directory
WORKDIR /app/backend/banking

# Ensure mvnw is executable
RUN chmod +x mvnw

# --- DEBUG STEP 1: Verify frontend directory copied correctly ---
RUN ls -R /app/frontend

# Download dependencies and build the application
# Use the full path to mvnw or ensure it's in the PATH.
# Using ./mvnw is usually fine if permissions are correct.
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests

# --- DEBUG STEP 2: Verify frontend build output exists ---
RUN ls -R /app/frontend/build

# --- DEBUG STEP 3: Verify resources copied into target/classes/static ---
RUN ls -R target/classes/static

# Stage 2: Create the final lean image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/backend/banking/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
