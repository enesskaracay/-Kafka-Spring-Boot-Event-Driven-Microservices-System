# 🚀 Kafka & Spring Boot: Event-Driven Microservices System

Bu proje, modern backend sistemlerde kullanılan **Event-Driven Architecture**, **Saga Pattern (Choreography)** ve **Transactional Outbox Pattern** yaklaşımlarını temel alarak geliştirilmiş, yüksek ölçeklenebilir bir e-ticaret sipariş yönetim sistemi simülasyonudur.

Sistem; sipariş oluşturma, ödeme işlemleri ve bildirim süreçlerini **Apache Kafka** üzerinden tamamen asenkron ve gevşek bağlı (**loosely coupled**) bir şekilde yönetir.

---

## 🧠 Proje Amacı

Bu proje aşağıdaki konuları pratik etmek amacıyla geliştirilmiştir:

- Microservice Architecture  
- Event-Driven Systems  
- Apache Kafka ile asenkron iletişim  
- Saga Pattern (Choreography)  
- Transactional Outbox Pattern  
- Eventual Consistency yaklaşımı  

---

## 🏗️ Mimari Genel Bakış

Sistem, birbirinden bağımsız çalışan mikroservislerden oluşur:

- Order Service  
- Payment Service  
- Notification Service  
- Eureka Server (Service Discovery)  

Servisler birbirleriyle doğrudan haberleşmez. Tüm iletişim **Kafka eventleri** üzerinden gerçekleşir.

---

## 🔄 Event Flow (Saga Choreography)

Sistemde merkezi bir orchestrator yoktur. Servisler event’leri dinleyerek süreci ilerletir.


OrderCreated → PaymentProcessed → OrderUpdated → NotificationSent


### Detaylı Akış

**Order Service**
- Sipariş oluşturur (`PENDING`)
- `OrderCreated` event’i üretir (Outbox üzerinden)

**Payment Service**
- Event’i consume eder
- Ödeme işlemini gerçekleştirir
- `PaymentProcessed` event’i üretir

**Order Service**
- Payment sonucunu dinler
- Order status günceller:
  - `COMPLETED` (başarılı)
  - `CANCELLED` (başarısız)

**Notification Service**
- Event’leri dinler
- Kullanıcıya bildirim gönderir

---

## 🧩 Mimari Desenler

### 1️⃣ Saga Pattern (Choreography)

- Merkezi bir orchestrator yoktur  
- Servisler event’ler üzerinden birbirini tetikler  
- Daha gevşek bağlı ve ölçeklenebilir yapı sağlar  

---

### 2️⃣ Transactional Outbox Pattern

**Problem:**
Database işlemi ile Kafka event’i arasında tutarsızlık olabilir.

**Çözüm:**

- Event önce veritabanındaki **outbox** tablosuna yazılır  
- Background process ile Kafka’ya gönderilir  

✔ Böylece:
- Mesaj kaybı engellenir  
- Sistem daha güvenilir hale gelir  

---

### 3️⃣ Database per Service

Her mikroservisin kendine ait izole veritabanı vardır:

- `order_db` → Sipariş + Outbox  
- `payment_db` → Ödeme kayıtları  
- `notification_db` → Bildirim geçmişi  

✔ Servis izolasyonu sağlar  
✔ Bağımsız ölçeklenebilirlik sunar  

---

## 🛠️ Teknoloji Yığını

- **Backend:** Java, Spring Boot 3+  
- **Persistence:** Spring Data JPA, Hibernate  
- **Message Broker:** Apache Kafka (Zookeeper ile)  
- **Database:** PostgreSQL  
- **Service Discovery:** Netflix Eureka  
- **Containerization:** Docker, Docker Compose  
- **Build Tool:** Maven  

---

## 📦 Mikroservis Detayları

| Servis | Port | Açıklama |
|------|------|--------|
| Eureka Server | 8761 | Servis kayıt ve keşif |
| Order Service | 8081 | Sipariş oluşturma, state yönetimi, outbox |
| Payment Service | 8082 | Ödeme işlemleri ve event üretimi |
| Notification Service | 8083 | Bildirim gönderimi |

---

## 🧠 Order Lifecycle

Mevcut yapı:


PENDING → COMPLETED / CANCELLED


Bu yapı **eventual consistency** prensibine göre çalışır.

> Order, ödeme sonucu geldikten sonra güncellenir.

---

## ⚙️ Outbox Mekanizması

**Order oluşturulurken:**
- Order kaydı DB’ye yazılır  
- Aynı transaction içinde Outbox kaydı oluşturulur  

**Scheduler:**
- Outbox’tan event’leri okur  
- Kafka’ya publish eder  

---

## 🧪 Test Senaryoları

### ✔ Temel Senaryo

- Order oluştur  
- Payment çalışır  
- Order güncellenir  
- Notification gönderilir  

---

### 🔥 Kritik Senaryo (Fault Tolerance)


Payment service kapalı
↓
Order oluştur
↓
Payment aç


**Beklenen:**

- Event kaybolmaz  
- Payment sonradan işlenir  

---

## 🚀 Nasıl Çalıştırılır

### 1. Projeyi Klonla

```bash
git clone https://github.com/enesskaracay/-Kafka-Spring-Boot-Event-Driven-Microservices-System.git
cd -Kafka-Spring-Boot-Event-Driven-Microservices-System

2. Build Al
mvn clean package -DskipTests

3. Docker ile Ayağa Kaldır
docker-compose up -d --build

🔍 Sistemi Gözlemleme
Order Oluştur
POST http://localhost:8081/api/orders

Logları İzle
docker-compose logs -f

👉 Burada servisler arası event akışını canlı olarak gözlemleyebilirsin.

⚠️ Geliştirme Alanları

Bu proje production-grade değildir. Geliştirilebilecek noktalar:

❗ Idempotency
Aynı event birden fazla işlenebilir

Çözüm:

eventId kullanımı
unique constraint

❗ State Machine
Önerilen yapı:

PENDING → PROCESSING → COMPLETED
                ↘ FAILED
❗ Retry & DLQ
Outbox retry mekanizması geliştirilebilir
Dead Letter Queue (DLQ) eklenebilir

❗ Observability
Distributed tracing (OpenTelemetry, Zipkin)
Merkezi loglama

❗ Notification Service
Gerçek email / SMS entegrasyonu eklenebilir

💥 Öne Çıkan Özellikler
Event-driven microservice mimarisi
Saga choreography implementasyonu
Transactional Outbox Pattern kullanımı
Servisler arası tam asenkron iletişim
Database isolation (per service)

🏁 Sonuç

Bu proje, modern backend sistemlerde kullanılan kritik mimari yaklaşımları uygulamalı olarak göstermektedir:

Mikroservis mimarisi
Event-driven sistemler
Asenkron iletişim
Eventual consistency

Mevcut haliyle güçlü bir temel sunmakta olup, yapılacak geliştirmelerle production seviyesine taşınabilir.

👤 Yazar

Enes Karaçay
