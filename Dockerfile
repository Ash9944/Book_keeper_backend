# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /backEnd

# Copy the project’s jar file into the container
COPY target/bookKeeper-0.0.1-SNAPSHOT.jar /backEnd/bookKeeper-0.0.1-SNAPSHOT.jar

# Expose the application port (default Spring Boot port is 8080)
EXPOSE 8080

# Command to run the app
ENTRYPOINT ["java", "-jar", "bookKeeper-0.0.1-SNAPSHOT.jar"]
