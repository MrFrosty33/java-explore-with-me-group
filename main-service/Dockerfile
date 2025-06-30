# build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -DskipTests -pl main-service -am package

# run
FROM eclipse-temurin:21-jre-jammy
ARG JAR_FILE=main-service/target/*.jar
COPY --from=build /app/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
