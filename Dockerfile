# Stage 1: Build the application using Maven
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /backEnd
COPY . .
RUN mvn clean package -DskipTests

# Print the contents of the /app/target directory to debug
RUN ls -l /backEnd/target

# Stage 2: Run the application using OpenJDK
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /backEnd
COPY --from=build /backEnd/target/*.jar /backEnd/bookKeeper-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/backEnd/bookKeeper-0.0.1-SNAPSHOT.jar"]
