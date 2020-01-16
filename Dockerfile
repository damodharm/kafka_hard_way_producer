FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY build/libs/kafka_hard_way_producer-1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
