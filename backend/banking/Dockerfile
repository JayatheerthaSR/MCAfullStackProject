# Use a multi-stage build for a smaller, more secure image
FROM openjdk:17-jdk-slim AS builder

# Set the working directory inside the container.
WORKDIR /app/backend/banking

# Copy the mvnw, mvn directory, pom.xml, and src directory
COPY backend/banking/mvnw /app/backend/banking/.
COPY backend/banking/.mvn/ /app/backend/banking/.mvn/
COPY backend/banking/pom.xml /app/backend/banking/
COPY backend/banking/src/ /app/backend/banking/src/

# Now, copy the frontend directory from its location relative to the repository root
# to a specific location in the container.
COPY frontend/ /app/frontend/

# Change WORKDIR to the backend/banking directory where the pom.xml is located
WORKDIR /app/backend/banking

# --- DEBUG STEP 1: Verify frontend directory copied correctly ---
RUN ls -R /app/frontend

# Download dependencies (to leverage Docker layer caching)
RUN --mount=type=cache,target=/root/.m2 \
    .mvnw clean package -DskipTests

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
