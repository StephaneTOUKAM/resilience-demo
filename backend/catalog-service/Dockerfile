FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/catalog-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
