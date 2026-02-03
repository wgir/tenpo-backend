# Use official Maven image for building
FROM maven:3.9.6-eclipse-temurin-21-alpine as build
WORKDIR /app

# Copy pom.xml and resolve dependencies separately (for caching)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn package -DskipTests -B

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
