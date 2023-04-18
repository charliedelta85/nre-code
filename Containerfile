FROM docker.io/openjdk:9
COPY . /app/
WORKDIR /app/target
ENTRYPOINT ["java", "-cp","/app/lib/*.jar", "-jar","nreclient-0.0.1-SNAPSHOT.jar"]
