# Use OpenJDK 17 as the base image
#FROM eclipse-temurin:17-jdk-jammy
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/Presentsir.jar /app/Presentsir.jar

# Expose the application port
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "Presentsir.jar"]
