FROM openjdk:21-slim
WORKDIR /app
COPY target/blogApp-0.0.1-SNAPSHOT.jar /app/blogApp.jar
CMD ["java", "-jar", "blogApp.jar"]