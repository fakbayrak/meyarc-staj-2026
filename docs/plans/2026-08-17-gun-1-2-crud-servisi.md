# Gün 1-2: Katmanlı Mimari ile CRUD Mikroservisi

## Context

30 günlük staj planının ilk maddesi: Java ile Entity-Service-Repository-Controller
katmanlı mimarisine sahip, temel CRUD işlemlerini yapabilen bir mikroservis geliştirmek.

Bu servis sadece bir "gün 1 ödevi" değil — **30 gün boyunca üzerine inşa edeceğimiz ana
proje**. Gün 3'te Liquibase, Gün 5'te Gateway, Gün 9'da Redis, Gün 10'da Elasticsearch
hep bu kod tabanına eklenecek. Bu yüzden temel yapıyı baştan doğru kurmak önemli.

**Domain:** Kütüphane / kitap yönetimi (Book, Author, Category)
**Stack:** Java 21 + Spring Boot 3.5.x + Maven + PostgreSQL (Docker)
**IDE:** IntelliJ IDEA
**Çalışma tarzı:** Kodu ben yazarım, her adımda *neden öyle yaptığımı* açıklarım; sen
okur, soru sorarsın, IntelliJ'de çalıştırıp doğrularsın.

---

## Mimari: Neden 4 katman?

```
HTTP isteği
    ↓
[Controller]  → HTTP'yi bilir. JSON alır/döner. İş mantığı YOK.
    ↓
[Service]     → İş mantığı burada. Validasyon, kurallar, transaction sınırı.
    ↓
[Repository]  → Veritabanını bilir. SQL/JPA. İş mantığı YOK.
    ↓
[Entity]      → Veritabanı tablosunun Java karşılığı.
    ↓
PostgreSQL
```

Her katman sadece bir alttakini tanır. Bunun kazancı: Controller'ı değiştirmeden
(örn. REST → GraphQL, Gün 11) iş mantığı aynı kalır; veritabanını değiştirsek
Service'e dokunmayız.

---

## Gün 1 — İskelet ve tek entity üzerinde CRUD

### 1.1 Proje iskeleti
- `library-service/` klasörü altında Spring Boot projesi (Maven, `mvnw` wrapper dahil —
  makinede Maven CLI kurulu değil, wrapper bunu çözer).
- Bağımlılıklar: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`,
  `spring-boot-starter-validation`, `postgresql`, `lombok`.
- Paket kökü: `com.meyarc.library`

### 1.2 PostgreSQL'i ayağa kaldırma
- Kök dizinde `docker-compose.yml` → tek servis: `postgres:16` + named volume.
- `application.yml` içinde datasource ayarları; `ddl-auto: update` (Gün 3'te Liquibase
  gelince bu `validate` olacak — o geçişi bilinçli yapacağız).

### 1.3 İlk dikey dilim: Book CRUD
Aşağıdaki dosyaları **bu sırayla** yazacağız; her biri bir öğretim adımı:

| Dosya | Öğrenilen kavram |
|---|---|
| `entity/Book.java` | `@Entity`, `@Id`, `@GeneratedValue`, alan eşleme |
| `repository/BookRepository.java` | `JpaRepository` — neden hiç kod yazmadan CRUD gelir |
| `dto/BookRequest.java`, `dto/BookResponse.java` | Entity'yi neden API'ye açmıyoruz |
| `service/BookService.java` + `impl/BookServiceImpl.java` | Interface + impl ayrımı, `@Transactional` |
| `controller/BookController.java` | `@RestController`, HTTP metod → CRUD eşlemesi, doğru status kodları |

### 1.4 Elle test
- Uygulamayı IntelliJ'den çalıştırma (Run configuration).
- `curl` / IntelliJ HTTP Client ile 5 endpoint'i tek tek deneme:
  `POST /api/books`, `GET /api/books`, `GET /api/books/{id}`,
  `PUT /api/books/{id}`, `DELETE /api/books/{id}`.

---

## Gün 2 — Olgunlaştırma: ilişkiler, validasyon, hata yönetimi

### 2.1 İlişkili entity'ler
- `Author` ve `Category` entity'leri.
- `Book` → `Author`: `@ManyToOne` (bir yazarın çok kitabı olur).
- `Book` ↔ `Category`: `@ManyToMany`.
- **Konu:** lazy vs eager loading, N+1 sorgu problemi (ileride Gün 9 cache ve
  veritabanı optimizasyonu konularında geri döneceğimiz nokta).

### 2.2 Girdi doğrulama — ✅ Gün 1'de tamamlandı
`BookRequest` üzerinde `@NotBlank`/`@Min` ve Controller'da `@Valid` Gün 1'de,
`BookRequest`'i yazarken zaten eklendi ve boş `title` senaryosu Gün 1'de test edilip
`gun-01.md`'ye işlendi. Gün 2'de tekrar "yeni iş" olarak yazılmayacak; sadece Author/
Category DTO'larına aynı desen uygulanacak (bkz. 2.5).

### 2.3 Merkezi hata yönetimi
- `exception/ResourceNotFoundException.java`
- `exception/GlobalExceptionHandler.java` → `@RestControllerAdvice`
- **Konu:** Neden her controller'da try-catch yazmıyoruz; tutarlı hata JSON formatı
  (RFC 7807 tarzı basit bir `ErrorResponse`).

### 2.4 Sayfalama ve sıralama
- `GET /api/books?page=0&size=10&sort=title,asc` → Spring Data `Pageable`.
- **Konu:** Neden "tüm kayıtları dön" production'da tehlikeli.

### 2.5 Author & Category için CRUD
- Aynı deseni tekrar ederek pekiştirme. Bu sefer bazı parçaları sana bırakıp
  birlikte gözden geçirebiliriz.

---

## Oluşacak dosya yapısı

```
StajRaporu-2026/
├── docker-compose.yml
├── docs/gunler/
│   ├── gun-01.md
│   └── gun-02.md
└── library-service/
    ├── pom.xml
    ├── mvnw, mvnw.cmd
    └── src/main/
        ├── java/com/meyarc/library/
        │   ├── LibraryApplication.java
        │   ├── entity/       Book, Author, Category
        │   ├── repository/   BookRepository, AuthorRepository, CategoryRepository
        │   ├── dto/          BookRequest, BookResponse, ...
        │   ├── mapper/       BookMapper
        │   ├── service/      BookService (+ impl/)
        │   ├── controller/   BookController, ...
        │   └── exception/    ResourceNotFoundException, GlobalExceptionHandler
        └── resources/application.yml
```

---

## Doğrulama (nasıl "çalışıyor" diyeceğiz)

1. `docker compose up -d` → `docker ps` ile postgres ayakta mı.
2. `./mvnw spring-boot:run` → uygulama 8080'de açılıyor mu, hata logu var mı.
3. HTTP Client ile senaryo: Author oluştur → Category oluştur → o ikisine bağlı Book
   oluştur → listede gör → güncelle → sil → tekrar GET'te 404 al.
4. Hata senaryoları: boş `title` ile POST → 400 + anlamlı mesaj; olmayan id ile GET → 404.
5. Postgres'e bağlanıp (`docker exec ... psql`) tabloların gerçekten oluştuğunu görme.

> Not: Otomatik testler (JUnit/Mockito) planda **Gün 14**'te. Gün 1-2'de elle
> doğrulama yapacağız; bu bilinçli bir sıralama, plana sadık kalıyoruz.

---

## Gün sonu

Her günün sonunda `docs/gunler/gun-01.md` ve `gun-02.md` dosyalarını birlikte
dolduracağız: ne yaptık, hangi sorunla karşılaştık, nasıl çözdük, ne öğrendik.
Ardından anlamlı bir commit mesajıyla GitHub'a push.
