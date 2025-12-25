## build
FROM maven:3.9-eclipse-temurin-17 as build
WORKDIR /app

COPY .mvn mvnw mvnw.cmd pom.xml ./
COPY src ./src
RUN mvn package -Dmaven.test.skip

## deploy
FROM maven:3.9-eclipse-temurin-17 as deploy

WORKDIR /app

COPY --from=build /app/target/eureka-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
