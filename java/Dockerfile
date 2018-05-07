FROM openjdk:8-jdk-slim AS build-env
WORKDIR /app
ADD app/hexbid-1.0.0-SNAPSHOT-fat.jar /app/
CMD ["java", "-jar", "hexbid-1.0.0-SNAPSHOT-all.jar"]
