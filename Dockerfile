FROM openjdk:21-jdk-slim

ENV JAVA_OPTIONS="-Xms150m -Xmx1500m"

COPY build/libs/circle-on-0.0.1-SNAPSHOT.jar /app/circle-on.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTIONS -jar /app/circle-on.jar"]