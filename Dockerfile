# STAGE 1: BUILDER (Compiles the Java code using Maven)
FROM maven:3-openjdk-17 AS builder

WORKDIR /app

# Copy the Maven project files and source code
COPY pom.xml .
COPY src ./src

# Build the Spring Boot application (creates the JAR)
RUN mvn clean package -DskipTests

# STAGE 2: RUNNER (Uses a minimal JRE image to run the JAR)
FROM openjdk:17-jre-slim

WORKDIR /app

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Copy the final JAR from the builder stage
COPY --from=builder /app/target/stringAPI-0.0.1-SNAPSHOT.jar /app/stringAPI.jar

# Set the entrypoint to run the JAR
ENTRYPOINT ["java", "-jar", "stringAPI.jar"]
