# Build stage: compile, test, and package runnable JAR
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package

# Runtime: JRE only — small image, no Maven
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/software-engineer-challenge-1.0-SNAPSHOT.jar /app/aggregator.jar

# Mount CSV at /data/ad_data.csv and collect output from /out (see README or run --help via overriding CMD)
ENTRYPOINT ["java", "-jar", "/app/aggregator.jar"]
CMD ["--input", "/data/ad_data.csv", "--output", "/out"]
