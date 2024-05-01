FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} mosa-webapp.jar
CMD apt-get update-y
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/mosa-webapp.jar"]