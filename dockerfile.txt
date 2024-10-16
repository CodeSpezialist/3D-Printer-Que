FROM maven:3.8-openjdk-18 as build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -D spring.profiles.active=prod -Dmaven.test.skip=true

# Nutze Ubuntu 22.04 als Basis-Image für ARM-Architektur
FROM arm64v8/ubuntu:22.04

# Setze Umgebungsvariablen, damit Installationen ohne Benutzerinteraktion laufen
ENV DEBIAN_FRONTEND=noninteractive

# Aktualisiere das System und installiere OpenJDK 18
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y wget openjdk-18-jdk

# Bestätige die Installation von Java 18
RUN java -version

# Festlegen des Workdir
WORKDIR /app

COPY --from=build /app/target/fileQue-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
