FROM openjdk:8
COPY target/gs-spring-boot-docker-0.1.1.jar /app.jar
EXPOSE 8080/tcp
ENTRYPOINT ["java", "-jar", "/app.jar"]