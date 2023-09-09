## build
FROM maven:3.8.4-openjdk-17-slim as build
WORKDIR /app

COPY .mvn mvnw mvnw.cmd pom.xml ./
COPY src ./src
RUN mvn package -Dmaven.test.skip

## deploy
FROM openjdk:17-jdk-slim as deploy

WORKDIR /app

COPY --from=build /app/target/transactionservice-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
