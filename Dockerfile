FROM maven:3-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/target/stringAPI-0.0.1-SNAPSHOT.jar /app/stringAPI.jar
ENTRYPOINT ["java", "-jar", "stringAPI.jar"]
