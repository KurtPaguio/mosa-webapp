FROM maven:3.6.3-openjdk-17 as build
RUN mvn clean install

FROM openjdk:17
COPY ./target/*.jar mosa-webapp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "mosa-webapp.jar"]