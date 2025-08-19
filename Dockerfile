# Stage 2: Create the final, lightweight runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Define arguments that can be passed during the Docker build.
ARG MONGODB_URI
ARG JWT_SECRET
ARG JWT_EXPIRATION_MS  # <-- ADD THIS LINE
ARG SERVER_PORT=8080

# Set the arguments as environment variables for the application to use at runtime.
ENV SPRING_DATA_MONGODB_URI=$MONGODB_URI
ENV JWT_SECRET=$JWT_SECRET
ENV JWT_EXPIRATION_MS=$JWT_EXPIRATION_MS 
ENV SERVER_PORT=$SERVER_port

# Copy the executable JAR file from the 'builder' stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]