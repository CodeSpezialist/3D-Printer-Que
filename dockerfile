FROM maven:3.8-openjdk-18 as build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -D spring.profiles.active=prod -Dmaven.test.skip=true
FROM openjdk:18

WORKDIR /app

COPY --from=build /app/target/fileQue-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]