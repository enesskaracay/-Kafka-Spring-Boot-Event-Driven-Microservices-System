# Projeni hangi Java sürümüyle yazdıysan onu seç (17, 19, veya 21)
FROM eclipse-temurin:19-jdk-alpine

# İçeride geçici bir klasör oluşturur
VOLUME /tmp

# Hedef klasöründeki derlenmiş .jar dosyasını bulur
ARG JAR_FILE=target/*.jar

# O jar dosyasını konteynerin içine 'app.jar' adıyla kopyalar
COPY ${JAR_FILE} app.jar

# Konteyner çalışınca bu komutu tetikler: java -jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]