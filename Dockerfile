#
# Build Stage: Compiles the application using Maven and Java 17
#
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

#
# Package Stage: Creates the final, lightweight image to run the application
#
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Define and set the environment variables your application needs
ARG MONGODB_URI
ARG JWT_SECRET
ARG JWT_EXPIRATION_MS
ENV SPRING_DATA_MONGODB_URI=$MONGODB_URI
ENV JWT_SECRET=$JWT_SECRET
ENV JWT_EXPIRATION_MS=$JWT_EXPIRATION_MS

# Copy the compiled JAR from the build stage
# Note: The JAR name is taken from your pom.xml file
COPY --from=build /app/target/chronoblog-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java","-jar","app.jar"]