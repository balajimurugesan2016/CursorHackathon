# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8085

HEALTHCHECK --interval=5s --timeout=3s --start-period=30s --retries=10 \
  CMD curl -f http://localhost:8085/api/v1/plants || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
