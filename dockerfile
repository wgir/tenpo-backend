# Use official JDK 21 image
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /app

# Copy maven executable and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Resolve dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src
# Build the application
RUN ./mvnw package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
