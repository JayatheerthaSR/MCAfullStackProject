#
# Build stage
#
FROM eclipse-temurin:17-jdk-focal as builder

# Set the working directory in the container
WORKDIR /app

# Ensure mvnw and .mvn are executable
COPY backend/banking/mvnw /app/backend/banking/mvnw
COPY backend/banking/.mvn /app/backend/banking/.mvn
#
# Build stage
#
FROM eclipse-temurin:17-jdk-focal as builder

# Set the working directory in the container
WORKDIR /app

# Ensure mvnw and .mvn are executable
COPY backend/banking/mvnw /app/backend/banking/mvnw
COPY backend/banking/.mvn /app/backend/banking/.mvn

# Copy the pom.xml for dependency caching
COPY backend/banking/pom.xml /app/backend/banking/pom.xml

# Copy the source code (this comes after copying pom.xml to leverage Docker caching for dependencies)
COPY backend/banking/src /app/backend/banking/src

# --- CRITICAL DEBUG STEP 1: Show what Docker sees in the build context root ---
# This command tries to list the 'frontend' directory specifically and show its existence.
# If it doesn't exist, this will error.
RUN echo "--- START DEBUG: Checking for 'frontend' at build context root (/) ---" && \
    ls -d /frontend || echo "ERROR: /frontend directory NOT FOUND in build context root!" && \
    echo "--- END DEBUG: Checking for 'frontend' at build context root (/) ---"

# Copy the frontend application
# Ensure the 'frontend' directory exists at the root of your repository (same level as 'backend')
COPY frontend /app/frontend

# --- CRITICAL DEBUG STEP 2: Verify frontend directory existence and contents after copy ---
# This will try to list the contents of /app/frontend and signal if it fails.
RUN echo "--- START DEBUG: Verifying /app/frontend after copy ---" && \
    if [ -d "/app/frontend" ]; then \
        echo "SUCCESS: /app/frontend directory exists. Contents:" && ls -la /app/frontend; \
    else \
        echo "ERROR: /app/frontend directory DOES NOT EXIST after COPY!"; \
    fi && \
    echo "--- END DEBUG: Verifying /app/frontend ---"

# --- CRITICAL DEBUG STEP 3: Check if package.json exists within frontend ---
# This is what the frontend-maven-plugin would typically look for.
RUN echo "--- START DEBUG: Checking for package.json in /app/frontend ---" && \
    if [ -f "/app/frontend/package.json" ]; then \
        echo "SUCCESS: /app/frontend/package.json exists. Content head:" && head /app/frontend/package.json; \
    else \
        echo "ERROR: /app/frontend/package.json DOES NOT EXIST!"; \
    fi && \
    echo "--- END DEBUG: Checking for package.json ---"


# Set working directory to the banking backend for Maven build
WORKDIR /app/backend/banking

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

#
# Package stage
#
FROM eclipse-temurin:17-jre-focal as stage-1
WORKDIR /app
COPY --from=builder /app/backend/banking/target/*.jar /app/backend/banking/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/backend/banking/app.jar"]
# Copy the pom.xml for dependency caching
COPY backend/banking/pom.xml /app/backend/banking/pom.xml

# Copy the source code (this comes after copying pom.xml to leverage Docker caching for dependencies)
COPY backend/banking/src /app/backend/banking/src

# --- CRITICAL DEBUG STEP: List contents of the build context before copying frontend ---
# This will list contents of the directory where Docker BUILD command is run.
# Render's build context is set to '.', so this should list the repo root.
RUN echo "--- START DEBUG: ls -la / (Build Context Root) ---" && ls -la / && echo "--- END DEBUG: ls -la / ---"

# Copy the frontend application
# Ensure the 'frontend' directory exists at the root of your repository (same level as 'backend')
COPY frontend /app/frontend

# Set working directory to the banking backend for Maven build
WORKDIR /app/backend/banking

# --- DEBUG STEP 1: List contents of /app ---
RUN echo "--- START DEBUG: ls -la /app ---" && ls -la /app && echo "--- END DEBUG: ls -la /app ---"

# --- DEBUG STEP 2: List contents of /app/backend ---
RUN echo "--- START DEBUG: ls -la /app/backend ---" && ls -la /app/backend && echo "--- END DEBUG: ls -la /app/backend ---"

# --- DEBUG STEP 3: List contents of /app/frontend ---
RUN echo "--- START DEBUG: ls -la /app/frontend ---" && ls -la /app/frontend && echo "--- END DEBUG: ls -la /app/frontend ---"

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

#
# Package stage
#
FROM eclipse-temurin:17-jre-focal as stage-1
WORKDIR /app
COPY --from=builder /app/backend/banking/target/*.jar /app/backend/banking/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/backend/banking/app.jar"]
