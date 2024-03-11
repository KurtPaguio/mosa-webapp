FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
COPY target/mosa-webapp.jar mosa-webapp.jar
ENTRYPOINT ["java", "-jar", "/mosa-webapp.jar"]