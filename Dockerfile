FROM openjdk:17
COPY target/*.jar mosa-webapp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "mosa-webapp.jar"]