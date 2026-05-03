# --- 1. AŞAMA: Build (İnşa) ---
# Java 17 ve Maven içeren imajı kullanıyoruz.
# Bu aşama sadece JAR üretmek için var.
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Sadece pom.xml'i kopyalayıp kütüphaneleri önden indiriyoruz.
# Bu, kod değişikliğinde kütüphaneleri tekrar indirmemeyi (Caching) sağlar.
COPY pom.xml .
RUN mvn dependency:go-offline

# Kodun tamamını kopyalayıp JAR dosyasını üretiyoruz.
COPY src ./src
RUN mvn clean package -DskipTests

# --- 2. AŞAMA: Runtime (Çalışma) ---
# Sadece hafif bir Java 17 JRE kullanıyoruz (Boyutu 3 kat küçültür).
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# İlk aşamada üretilen o ağır JAR dosyasını buraya "app.jar" olarak kopyalıyoruz.
COPY --from=build /app/target/*.jar app.jar

# Uygulama hangi porttan konuşacak?
EXPOSE 8081

# Çalıştırma komutu
ENTRYPOINT ["java", "-jar", "app.jar"]