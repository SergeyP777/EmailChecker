FROM openjdk:21-jdk-slim
RUN apt-get update && \
    apt-get install -y whois && \
    echo "whois 43/tcp nicname # WHOIS service" >> /etc/services && \
    rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY /build/libs/email-0.0.1-SNAPSHOT.jar /app/email-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "email-0.0.1-SNAPSHOT.jar"]