#
# Build stage
#
FROM eclipse-temurin:17-jdk-focal as builder

# Set the initial working directory in the container
WORKDIR /app

# Ensure mvnw and .mvn are executable, and copy pom.xml for dependency caching
# Copy source code (this comes after copying pom.xml to leverage Docker caching for dependencies)
COPY backend/banking/mvnw /app/backend/banking/mvnw
COPY backend/banking/.mvn /app/backend/banking/.mvn
COPY backend/banking/pom.xml /app/backend/banking/pom.xml
COPY backend/banking/src /app/backend/banking/src

# --- CRITICAL DEBUG STEP 1: Show what Docker sees in the build context root (/) ---
# This command tries to list the 'frontend' directory specifically and show its existence.
# If it doesn't exist *in the build context*, this will echo an error.
RUN echo "--- START DEBUG: Checking for 'frontend' at build context root (/) ---" && \
    ls -d ./frontend || echo "ERROR: 'frontend' directory NOT FOUND in Docker build context root! Ensure it's there." && \
    echo "--- END DEBUG: Checking for 'frontend' at build context root (/) ---"

# --- CRITICAL DEBUG STEP 1.5: List contents of the build context *before* copying frontend ---
# This will show what Docker *sees* at the root of its temporary build context before the COPY command.
# If 'frontend' is not listed here, then Render is not providing it to Docker as expected for some reason.
RUN echo "--- START DEBUG: ls -la / (Build Context Root BEFORE frontend COPY) ---" && \
    ls -la / && \
    echo "--- END DEBUG: ls -la / (Build Context Root BEFORE frontend COPY) ---"

# Copy the frontend application from the build context root to /app/frontend inside the container.
# This assumes your project structure has 'frontend' at the same level as 'backend'.
COPY frontend /app/frontend

# --- CRITICAL DEBUG STEP 2: Verify /app/frontend directory existence and contents after copy ---
# This will try to list the contents of /app/frontend and signal if it fails.
RUN echo "--- START DEBUG: Verifying /app/frontend after copy ---" && \
    if [ -d "/app/frontend" ]; then \
        echo "SUCCESS: /app/frontend directory exists. Contents:" && ls -la /app/frontend; \
    else \
        echo "ERROR: /app/frontend directory DOES NOT EXIST after COPY! Frontend was not copied correctly."; \
    fi && \
    echo "--- END DEBUG: Verifying /app/frontend ---"

# --- CRITICAL DEBUG STEP 3: Check if package.json exists within /app/frontend ---
# This is what the frontend-maven-plugin would typically look for to run npm install.
RUN echo "--- START DEBUG: Checking for package.json in /app/frontend ---" && \
    if [ -f "/app/frontend/package.json" ]; then \
        echo "SUCCESS: /app/frontend/package.json exists. Content head:" && head -n 5 /app/frontend/package.json; \
    else \
        echo "ERROR: /app/frontend/package.json DOES NOT EXIST! Frontend project likely incomplete or not copied."; \
    fi && \
    echo "--- END DEBUG: Checking for package.json ---"

# Set working directory to the banking backend for Maven build
WORKDIR /app/backend/banking

# Build the Spring Boot application, which will trigger the frontend-maven-plugin
RUN ./mvnw clean package -DskipTests

#
# Package stage
#
FROM eclipse-temurin:17-jre-focal as stage-1
WORKDIR /app
# Copy the built JAR from the builder stage
COPY --from=builder /app/backend/banking/target/*.jar /app/backend/banking/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/backend/banking/app.jar"]
