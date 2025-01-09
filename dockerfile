# Step 1: Use Maven to build the application
FROM maven:3.8.8-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy project files into the container
COPY paper_system/pom.xml .
COPY paper_system/src ./src

# Run Maven to package the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Step 2: Use a lightweight JDK image to run the application
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
