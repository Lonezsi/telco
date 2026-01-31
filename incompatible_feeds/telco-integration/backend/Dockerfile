FROM eclipse-temurin:17-jdk-alpine
COPY . .
RUN ./mvnw clean package -DskipTests
ENTRYPOINT ["java","-jar","target/*.jar"]