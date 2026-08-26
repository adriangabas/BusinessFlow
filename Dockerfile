FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
RUN mvn -B package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 businessflow
COPY --from=build --chown=businessflow:businessflow /workspace/target/businessflow-*.jar app.jar
USER businessflow
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
