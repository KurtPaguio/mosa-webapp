FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar mosa-webapp.jar
ENTRYPOINT ["java","-jar","/app.jar"]