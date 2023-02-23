FROM openjdk:11
COPY target/*.jar /app/application.jar
WORKDIR /app
CMD ["/usr/bin/java", "-jar", "/app/application.jar"]