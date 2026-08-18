# Gün 3 — Liquibase Migration Dosyalarının Oluşturulması

**Tarih:** 2026-07-08
**Plan referansı:** [`staj_defteri_plani.md`](../../staj_defteri_plani.md) — Gün 3
**Detaylı plan:** [`docs/plans/2026-07-08-gun-3-liquibase.md`](../plans/2026-07-08-gun-3-liquibase.md)

## Yapılanlar

- **Liquibase entegrasyonu:** `pom.xml`'e `liquibase-core` ve
  `liquibase-maven-plugin` eklendi; CLI'dan `rollback`/`update` çalıştırabilmek
  için `liquibase.properties` (local DB bağlantı bilgisi) oluşturuldu.
- **Changelog yapısı:** `db/changelog/db.changelog-master.xml` sadece
  `<include>` listesi olacak şekilde kuruldu; gerçek değişiklikler
  `db/changelog/changes/` altında ayrı dosyalarda. Bu ayrım bilinçli: yeni bir
  şema değişikliği her zaman **yeni bir dosya** olarak eklenir, uygulanmış bir
  dosyaya asla dokunulmaz (Liquibase her changeset'in checksum'ını tutuyor;
  içerik değişirse uygulama açılışta hata verir).
- **001-initial-schema.xml:** `author`, `category`, `book`, `book_category`
  tabloları ve aralarındaki foreign key'ler — Hibernate'in `ddl-auto: update`
  ile önceden ürettiği şemayla birebir aynı isimlendirmeyle (`publication_year`
  gibi snake_case kolonlar dahil) — altı ayrı `changeSet` halinde tanımlandı.
  Her tablo/FK kendi changeset'inde; ileride biri tek başına geri alınmak
  istenirse diğerlerini etkilemesin diye.
- **002-book-isbn-unique.xml:** `book.isbn` üzerine `addUniqueConstraint` ile
  gerçek bir domain kuralı eklendi; rollback söz dizimini göstermek için elle
  bir `<rollback>` bloğu da yazıldı (bu changeset tipi aslında Liquibase'in
  **otomatik** ürettiği rollback'lerden biri, elle yazmak zorunlu değildi —
  ama söz dizimini görmek için bilerek elle yazıldı).
- **`ddl-auto: update` → `validate`:** Hibernate artık şema **üretmiyor**,
  sadece entity'lerle veritabanının uyuştuğunu doğruluyor. `spring.liquibase.
  change-log` ile changelog yolu açıkça belirtildi (varsayılan yol `.yaml`
  uzantısı arıyor, biz XML kullandığımız için gerekliydi).
- **Temiz başlangıç:** `docker compose down -v && docker compose up -d` ile
  Postgres volume'u silinip sıfırdan ayağa kaldırıldı; şema tamamen Liquibase
  üzerinden kuruldu (canlı bir veritabanında izlenen `generateChangeLog` +
  `changelogSync` yolu bilinçli olarak tercih edilmedi — sıfırdan kurmak,
  changeset yazma pratiği için daha öğreticiydi).
- **Hata dönüşü iyileştirmesi:** Test sırasında ortaya çıkan bir eksik
  (aşağıda) üzerine `GlobalExceptionHandler`'a `DataIntegrityViolationException`
  handler'ı eklendi → veritabanı kısıtı ihlalleri artık 500 yerine 409
  Conflict, aynı `ErrorResponse` formatında dönüyor.
- **Uçtan uca test:** `psql` ile tabloların ve `databasechangelog`'daki 7
  changeset'in `EXECUTED` olduğu doğrulandı; Postman'den aynı `isbn` ile iki
  `POST` denenip constraint'in gerçekten çalıştığı görüldü; `liquibase:rollback`
  ile constraint kaldırılıp `liquibase:update` ile geri getirildi, her adımda
  `\d book` ile kontrol edildi; son olarak `GET /api/books` ile Gün 1-2'nin
  CRUD/sayfalama işlevinin bozulmadığı (regresyon yok) doğrulandı.

## Karşılaşılan sorunlar ve çözümleri

1. **`liquibase-core` tek başına yetmedi — Liquibase hiç çalışmadı.**
   Uygulama `Schema validation: missing table [author]` hatasıyla açılışta
   çöktü; loglarda Liquibase'e dair tek satır bile yoktu. Kök neden: bu
   projenin kullandığı Spring Boot 4.1.0'da Liquibase autoconfiguration'ı,
   önceki sürümlerdeki gibi `spring-boot-autoconfigure` içinde değil, ayrı bir
   modülde (`org.springframework.boot:spring-boot-liquibase`) — Boot 4'ün her
   teknolojiyi kendi autoconfigure modülüne ayırdığı yeni yapının bir parçası
   (`spring-boot-starter-webmvc` isimlendirmesiyle aynı desen). `liquibase-core`
   sadece kütüphanenin kendisiydi, onu tetikleyen Spring entegrasyonu değildi.
   Çözüm: `pom.xml`'e `spring-boot-liquibase` bağımlılığı eklendi.
2. **Unique constraint ihlali 500 dönüyordu.** `GlobalExceptionHandler` sadece
   `ResourceNotFoundException`/`MethodArgumentNotValidException`'ı yakalıyordu;
   veritabanının attığı `DataIntegrityViolationException` yakalanmadan Spring'in
   generic 500'üne düşüyordu. Çözüm: bu exception için 409 Conflict dönen bir
   handler eklendi — mesaj bilinçli olarak genel tutuldu, ham SQL/constraint
   adı client'a sızdırılmadı.
3. **PowerShell'de `-D` argümanı bölünüyordu.** `./mvnw liquibase:rollback
   -Dliquibase.rollbackCount=1` çalıştırıldığında Maven `Unknown lifecycle
   phase ".rollbackCount=1"` hatası verdi — PowerShell argümanı ikiye bölmüştü.
   Çözüm: tüm `-D` ifadesini tırnak içine almak
   (`"-Dliquibase.rollbackCount=1"`).

## Öğrenilenler

- `ddl-auto: validate`, entity ile şema arasındaki sapmayı **uygulama
  açılışında** yakalıyor — bir migration yazmayı unutursak uygulama hiç
  ayağa kalkmıyor; bu, `update`'in "sessizce kendi kendine düzeltir" davranışına
  göre çok daha güvenli bir varsayılan.
- Bir changeset'in `id` + `author` + checksum üçlüsü, uygulanmış bir dosyanın
  neden asla düzenlenmemesi gerektiğini açıklıyor: içerik değişirse checksum
  uyuşmaz ve uygulama migration'ı reddeder. Doğrusu her zaman **yeni** bir
  changeset eklemek.
- Liquibase, `createTable`, `addForeignKeyConstraint`, `addUniqueConstraint`
  gibi çoğu yapısal değişiklik için rollback'i **otomatik** üretebiliyor; elle
  `<rollback>` yazmak zorunlu değil ama söz dizimini bilmek, otomatik geri
  alınamayan tiplerde (örn. veri taşıyan `sql`/`update` changeset'leri) işe
  yarıyor.
- Spring Boot 4.x, autoconfiguration'ı teknoloji başına ayrı modüllere
  bölmüş durumda (`spring-boot-liquibase`, `spring-boot-hibernate-autoconfigure`
  gibi) — bir kütüphaneyi classpath'e eklemek, onun Spring entegrasyonunun da
  otomatik geleceği anlamına gelmiyor; ikisi ayrı bağımlılık olabiliyor.
- `DataIntegrityViolationException`, Spring'in veritabanı sürücüsünden
  bağımsız çalışan hata soyutlaması — unique, not-null, FK ihlali fark
  etmeksizin tek bir exception tipine çevriliyor, driver'a özel `SQLException`
  yakalamaktan daha taşınabilir.
- Windows PowerShell'de Maven'a `-D` ile sistem özelliği geçerken tüm ifadeyi
  tırnak içine almak, argümanın shell tarafından bölünmesini engelliyor.

## Sırada ne var

Gün 4: Design Patterns mini-projesi — Singleton, Factory, Observer ve Strategy
kalıplarının küçük, canlı örneklerle uygulanması.
