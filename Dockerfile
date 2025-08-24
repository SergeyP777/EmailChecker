FROM openjdk:21-jdk-slim
WORKDIR /app
COPY /build/libs/email-0.0.1-SNAPSHOT.jar /app/email-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "email-0.0.1-SNAPSHOT.jar"]