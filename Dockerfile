# BUILD IMAGE
FROM maven:3.9.6-amazoncorretto-21 AS build

WORKDIR /mapleglory

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package


# JRE IMAGE
FROM amazoncorretto:21

WORKDIR /mapleglory

COPY --from=build /mapleglory/target/server.jar ./server.jar

CMD ["java", "-jar", "server.jar"]
