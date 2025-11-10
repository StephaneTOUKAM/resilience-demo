FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/api-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
