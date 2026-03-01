# Transportation Service

Lokasyon ve ulaşım verileri üzerinden **iki nokta arasında rota arama** yapan REST API servisi. Kullanıcılar lokasyon ekler, lokasyonlar arasında ulaşım (uçuş, otobüs, metro, vb.) tanımlar; ardından bir lokasyondan diğerine gidebilecekleri rotaları **en fazla 1 uçuş** ve **en fazla 3 segment (node)** kısıtlarıyla sorgulayabilir.

---

## Özellikler

- **Lokasyon yönetimi**: Lokasyon ekleme, listeleme, id ile getirme, silme (IATA benzeri kod, şehir, ülke, tip: AIRPORT / OTHER).
- **Ulaşım yönetimi**: İki lokasyon arasında ulaşım tanımlama (FLIGHT, BUS, SUBWAY, UBER) ve **çalışma günleri** (1–7, Pazartesi–Pazar) ile filtreleme.
- **Rota arama**: Origin + destination + tarih ile rotaları getirme; sadece seçilen tarihin gününde çalışan ulaşımlar kullanılır.
- **Güvenlik**: JWT tabanlı kimlik doğrulama; login ile token alınıp API isteklerinde Bearer token kullanılır.
- **API dokümantasyonu**: SpringDoc OpenAPI ile Swagger UI (`/swagger-ui.html`).

---

## Kullanılan Teknolojiler

| Alan | Teknoloji |
|------|-----------|
| **Runtime** | Java 17 |
| **Framework** | Spring Boot 4.x |
| **Web & API** | Spring Web MVC, Spring Validation |
| **Veritabanı** | Spring Data JPA, PostgreSQL (prod/stb), H2 (dev/test) |
| **Migrasyon** | Liquibase (YAML changelog) |
| **Güvenlik** | Spring Security, JWT (Nimbus JOSE) |
| **Dokümantasyon** | SpringDoc OpenAPI (Swagger UI) |
| **Cache** | Spring Cache (rota sonuçları için `@Cacheable`) |
| **Araçlar** | Lombok, Gradle |
| **Container** | Docker, Docker Compose |

---

## Algoritmalar ve Rota Bulma Yaklaşımı

Rota arama, **graf (graph)** yapısı üzerinde çalışır: **lokasyonlar düğüm (node)**, **ulaşımlar kenar (edge)** olarak modellenir.

### Kısıtlar

- **En fazla 3 segment**: Rota 1, 2 veya 3 ulaşım segmentinden oluşabilir.
- **Tam 1 uçuş**: Rota içinde tam olarak **1 adet** `FLIGHT` segmenti bulunmalı (daha fazla uçuş veya hiç uçuş yok kabul edilmez).

### 1. DFS tabanlı rota bulucu (`RouteFinderDfs` – varsayılan)

- **Algoritma**: **Depth-First Search (DFS)**.
- **Mantık**: Graf, “her lokasyondan çıkan ulaşımlar” şeklinde adjacency list olarak tutulur. Başlangıç lokasyonundan DFS ile tüm yollar denenir; yol uzunluğu 3’ü geçmeden ve uçuş sayısı 1’i geçmeden hedefe ulaşılan yollar toplanır. Sadece **tam 1 uçuş** içeren rotalar sonuç listesine eklenir.
- **Tekrarsızlık**: Segment id’leriyle üretilen bir “signature” ile aynı rota tekrar eklenmez.

### 2. Pattern tabanlı rota bulucu (`ManuelRouteFinder`)

- **Yaklaşım**: Kısıtları sağlayan **4 sabit pattern** açıkça kodlanır:
  1. **Direct flight**: Tek segment, origin → destination uçuş.
  2. **Ground → Flight**: Önce kara/ray ulaşımı, sonra uçuş.
  3. **Flight → Ground**: Önce uçuş, sonra kara/ray ulaşımı.
  4. **Ground → Flight → Ground**: Kara – uçuş – kara (3 segment).
- Uçuşlar ve uçuş-dışı ulaşımlar ayrı indekslerde (lokasyon kodu → liste) tutulur; bu pattern’lere göre tüm geçerli kombinasyonlar taranır ve signature ile tekrarsız şekilde sonuçlara eklenir.

İki implementasyon da `RouteFinder` arayüzü ile soyutlanmıştır; varsayılan (primary) implementasyon DFS tabanlıdır.

---

## Proje Yapısı ve Mimari

- **Katmanlar**: Controller → Service → Repository / RouteFinder; DTO’lar ile giriş/çıkış modelleri ayrılmış.
- **Rota servisi**: `RouteService`, verilen tarihin haftanın gününe göre `TransportationRepository.findDistinctByOperatingDaysContains(dayOfWeek)` ile o gün çalışan kenarları çeker; tüm rotayı `RouteFinder`’a bırakır. Sonuçlar `@Cacheable(cacheNames = "routes", key = "origin|destination|date")` ile önbelleğe alınır.
- **Hata yönetimi**: `GlobalExceptionHandler`, `RouteNotFoundException`, `LocationNotFoundException`, validasyon ve IATA/operating days ile ilgili özel hatalar için tutarlı `ErrorResponse` döner.
- **Çoklu dil**: `messages.properties` / `messages_tr.properties` ile doğrulama ve hata mesajları i18n desteklidir.

---

## API Özeti

| Metot | Endpoint | Açıklama |
|-------|----------|----------|
| POST | `/api/auth/login` | Kullanıcı girişi, JWT token döner |
| GET/POST | `/api/locations` | Lokasyon listesi / yeni lokasyon |
| GET/DELETE | `/api/locations/{id}` | Lokasyon getir / sil |
| GET/POST | `/api/transportations` | Ulaşım listesi / yeni ulaşım |
| GET/DELETE | `/api/transportations/{id}` | Ulaşım getir / sil |
| GET | `/api/routes?originCode=&destinationCode=&date=` | Rota ara (origin, destination, tarih) |

Tüm listeleme ve rota cevapları `BaseResponse<T>` wrapper’ı içinde döner. Detaylı API için Swagger UI kullanılabilir.

---

## Çalıştırma

### Gereksinimler

- Java 17+
- (Opsiyonel) Docker & Docker Compose (PostgreSQL ve uygulama için)

### Yerel (Geliştirme – H2)

```bash
./gradlew bootRun
# Profil: dev (varsayılan), H2 in-memory, Liquibase kapalı
# H2 Console: http://localhost:8080/h2-console
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Docker Compose (PostgreSQL + uygulama)

```bash
docker compose up -d
# Uygulama: http://localhost:8080
# PostgreSQL: 5432, pgAdmin: http://localhost:5050 (admin@local.dev / admin)
```

### Test

```bash
./gradlew test
```

---

## Konfigürasyon

- **Profiller**: `dev` (H2, in-memory), `stb` (PostgreSQL, Liquibase, JWT vb.).
- **Veritabanı**: `application-dev.yaml` (H2), `application-stb.yaml` ve `docker-compose` env’leri (PostgreSQL).
- **JWT**: `security.jwt.issuer`, `security.jwt.secret`, `security.jwt.access-token-ttl-minutes` (stb/prod ortamında mutlaka güçlü secret kullanılmalı).

---

## Veritabanı Şeması (Özet)

- **locations**: `id`, `location_code` (unique), `name`, `city`, `country`, `type` (AIRPORT/OTHER).
- **transportations**: `id`, `origin_location_id`, `destination_location_id`, `transportation_type` (FLIGHT/BUS/SUBWAY/UBER); ilişkili **transportation_operating_days** tablosunda `day_of_week` (1–7).
- **users**: Spring Security kullanıcıları (Liquibase ile oluşturulur / seed’lenir).

---

## Özet

Bu proje, lokasyon ve ulaşım verilerini graf olarak kullanıp **en fazla 1 uçuş** ve **en fazla 3 segment** kısıtıyla rota üreten bir servistir. Rota bulma hem **DFS** hem de **pattern tabanlı** iki strateji ile implemente edilmiş olup, gün bazlı çalışma takvimi, cache ve JWT ile production’a yakın bir API sunar.
