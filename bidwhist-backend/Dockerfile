# Use official Java 17 base image
FROM eclipse-temurin:17-jdk

# Set working directory inside container
WORKDIR /app

# Copy your backend code
COPY . .

# Make the Maven wrapper executable (just in case)
RUN chmod +x ./mvnw

# Build the app
RUN ./mvnw clean package -DskipTests

# Run the built JAR file
CMD ["java", "-jar", "target/bidwhist-backend-0.0.1-SNAPSHOT.jar"]
