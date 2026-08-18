# Gün 1 — Proje İskeleti ve Tek Entity Üzerinde CRUD

**Tarih:** 2026-07-06
**Plan referansı:** [`staj_defteri_plani.md`](../../staj_defteri_plani.md) — Gün 1-2
**Detaylı plan:** [`docs/plans/2026-08-17-gun-1-2-crud-servisi.md`](../plans/2026-08-17-gun-1-2-crud-servisi.md)

## Yapılanlar

- Repo (`meyarc-staj-2026`) için proje iskeleti hazırlandı: `.gitignore`, `docs/gunler/`
  klasör yapısı, ilk commit GitHub'a push edildi.
- Spring Initializr üzerinden `library-service` adında bir Maven/Java 21 Spring Boot
  projesi oluşturuldu (Spring Boot 4.1.0, Web/MVC, Data JPA, Validation, PostgreSQL
  driver, Lombok bağımlılıkları ile).
- PostgreSQL veritabanı `docker-compose.yml` ile container olarak ayağa kaldırıldı.
- Entity-Service-Repository-Controller katmanlı mimarisiyle **Book** kaynağı için uçtan
  uca CRUD akışı yazıldı:
  - `entity/Book.java` — JPA entity (`@Entity`, `@Id`, `@GeneratedValue`)
  - `repository/BookRepository.java` — `JpaRepository` ile hazır CRUD metodları
  - `dto/BookRequest.java`, `dto/BookResponse.java` — entity'yi API'ye doğrudan
    açmamak için ayrı istek/yanıt modelleri, `@NotBlank`/`@Min` validasyonları
  - `mapper/BookMapper.java` — entity ↔ DTO dönüşümü
  - `service/BookService.java` (+ `impl/BookServiceImpl.java`) — iş mantığı,
    `@Transactional`, interface/implementasyon ayrımı
  - `controller/BookController.java` — 5 REST endpoint (`POST/GET/GET-by-id/PUT/DELETE
    /api/books`), doğru HTTP status kodları (201, 200, 204)
- Uygulama `curl` ile uçtan uca test edildi: create → get → list → update → delete →
  tekrar get (bulunamama durumu), ayrıca boş `title` ile validasyon hatası (400)
  senaryosu doğrulandı.
- PostgreSQL içinde `book` tablosunun doğru sütun/tip eşlemesiyle oluştuğu `psql` ile
  kontrol edildi.

## Karşılaşılan sorunlar ve çözümleri

1. **Spring Initializr sürüm hatası** — `bootVersion=4.1.0.RELEASE` ile proje
   üretildi ama Maven Central'da bu artefakt yoktu (`Non-resolvable parent POM`).
   Sebep: Spring Boot artık sürüm adlarına `.RELEASE` eki eklemiyor. Maven Central
   metadata'sından gerçek sürümün `4.1.0` olduğu doğrulanıp `pom.xml` düzeltildi.

2. **Port 5432 çakışması** — `docker-compose.yml`'de PostgreSQL container'ı
   `5432:5432` ile yayınlandı ama uygulama "password authentication failed"
   hatasıyla başlamadı. Kök neden: makinede zaten **native (Windows'a kurulu) bir
   PostgreSQL servisi** 5432 portunu dinliyormuş; host'tan gelen bağlantılar Docker
   container'ına değil o servise gidiyordu. Container portu `5434:5432`'ye taşınarak
   çözüldü.

3. **Port 8080 çakışması** — uygulama `Port 8080 was already in use` hatası verdi.
   Sebep: bu makinede zaten **meyarc şirket altyapısı** (`meyarc-gateway`,
   `meyarc-postgres`, `meyarc-keycloak`, `meyarc-langfuse`, `meyarc-redis` vb.) Docker
   container'ları çalışıyor ve 8080'i `meyarc-gateway` kullanıyor. `library-service`
   portu `8090`'a taşındı. **Not:** bu container'lar muhtemelen Gün 28-30'da
   entegre olacağımız gerçek şirket sistemi — ileride bu bilgiyi kullanacağız.

4. **Türkçe karakterlerle test isteği** — `curl` ile "Suç ve Ceza" gibi Türkçe
   içerikli bir JSON gönderildiğinde Git Bash terminali UTF-8 baytlarını bozdu
   (`Invalid UTF-8 middle byte`). Uygulama tarafında bir hata değildi; ASCII içerikle
   tekrar denenince istek sorunsuz çalıştı. İleride Postman/IntelliJ HTTP Client gibi
   bir araçla test edilirse bu sorun yaşanmaz.

## Öğrenilenler

- Katmanlı mimaride her katmanın tek sorumluluğu olması (Controller → HTTP,
  Service → iş mantığı, Repository → veri erişimi, Entity → tablo) değişikliklerin
  etkisini tek katmanda tutuyor.
- `JpaRepository`'den kalıtım almak, hiç SQL/implementasyon yazmadan temel CRUD
  metodlarını (`save`, `findById`, `findAll`, `delete`) hazır getiriyor.
- Entity'yi API'ye doğrudan açmamak (DTO kullanmak) hem güvenlik hem API/veritabanı
  şemasını birbirinden bağımsızlaştırma açısından önemli.
- `@Transactional` altında yönetilen (managed) bir entity'nin alanlarını değiştirmek
  yeterli — Hibernate "dirty checking" ile otomatik `UPDATE` üretiyor, elle `save()`
  çağırmaya gerek yok.
- Lokal geliştirme ortamında port çakışmalarını erken fark edip (`Get-NetTCPConnection`,
  `docker ps`) doğru teşhis etmek önemli; "zaten çalışan bir şey var mı" kontrolü
  standart bir ilk adım olmalı.

## Sırada ne var

Gün 2: `Author`/`Category` ilişkili entity'ler, girdi validasyonunun uçtan uca
davranışı, `GlobalExceptionHandler` ile merkezi hata yönetimi (404 dahil), sayfalama.
