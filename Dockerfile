# Stage 1: Build the application with Maven
# Use a full JDK image to compile the Java code and build the JAR file.
FROM eclipse-temurin:17-jdk-alpine as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml first to leverage Docker's layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the Maven wrapper executable inside the Linux container (fixes permission issues from Windows)
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the application's source code
COPY src ./src

# Package the application into a JAR file, skipping tests for a faster build
RUN ./mvnw package -DskipTests


# Stage 2: Create the final, lightweight runtime image
# Use a smaller JRE image which is all that's needed to run the application.
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Define arguments that can be passed during the Docker build.
# These will be set by your deployment service (e.g., Render).
ARG MONGODB_URI
ARG JWT_SECRET
ARG SERVER_PORT=8080

# Set the arguments as environment variables for the application to use at runtime.
ENV SPRING_DATA_MONGODB_URI=$MONGODB_URI
ENV JWT_SECRET=$JWT_SECRET
ENV SERVER_PORT=$SERVER_PORT

# Copy the executable JAR file from the 'builder' stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# The command to run the application when the container starts.
# Spring Boot will automatically read the environment variables defined above.
ENTRYPOINT ["java", "-jar", "app.jar"]