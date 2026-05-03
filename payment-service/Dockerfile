# --- 1. AŞAMA: Build ---
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# --- 2. AŞAMA: Runtime ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
VOLUME /tmp
EXPOSE 8082
# Ödeme servisi kritik olduğu için bellek sınırını koruyoruz
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]