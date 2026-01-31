FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
ENTRYPOINT ["java","-jar","target/demo-0.0.1-SNAPSHOT.jar"]