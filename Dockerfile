# Use Eclipse Temurin as the base image for Java 21
FROM eclipse-temurin:21-jdk AS builder

# Set working directory
WORKDIR /app

# Copy maven wrapper files first
COPY mvnw .
COPY .mvn .mvn

# Make the maven wrapper executable
RUN chmod +x mvnw


# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src


# Build the application
RUN ./mvnw clean package -DskipTests


# Create the runtime image
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/notificationService-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]