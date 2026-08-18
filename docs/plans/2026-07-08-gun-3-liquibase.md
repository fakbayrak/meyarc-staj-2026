# Gün 3: Liquibase Migration Dosyalarının Oluşturulması

## Context

30 günlük staj planının 3. maddesi. Şu anda `library-service`'in veritabanı şeması
`spring.jpa.hibernate.ddl-auto: update` ile Hibernate tarafından **otomatik** yönetiliyor
(`application.yml:12`). Bu, Gün 1'de bilinçli olarak seçilmiş geçici bir çözümdü.

Sorun: `ddl-auto: update` production'da tehlikeli — şema değişikliği versiyonlanmıyor,
code review'dan geçmiyor, geri alınamıyor, ortamlar arası sessizce sapabiliyor ve
veri taşıma (data migration) ifade edilemiyor.

Hedef: Şemayı versiyonlanan, gözden geçirilebilir ve geri alınabilir Liquibase
migration dosyalarına taşımak; Hibernate'i şema **üreticisi** olmaktan çıkarıp
şema **doğrulayıcısı** (`validate`) konumuna almak.

**Onaylanan kararlar:** changelog formatı **XML**; mevcut tablolar düşürülüp şema
sıfırdan Liquibase ile kurulacak (**temiz başlangıç**); kurulumla yetinilmeyip
gerçek bir şema değişikliği + **rollback** de denenecek.

---

## Mevcut şema (birebir korunmalı)

`ddl-auto: validate` başarılı olsun diye changelog, Hibernate'in ürettiği isimlendirmeyle
tıpatıp eşleşmeli:

| Tablo | Kolonlar |
|---|---|
| `author` | `id` (PK, identity), `name` |
| `category` | `id` (PK, identity), `name` |
| `book` | `id` (PK, identity), `title`, `isbn`, `publication_year`, `author_id` (FK → author) |
| `book_category` | `book_id` (FK → book), `category_id` (FK → category), bileşik PK |

Kaynak: `entity/Book.java`, `entity/Author.java`, `entity/Category.java`.
Dikkat: `publicationYear` → `publication_year` (Spring Boot'un snake_case stratejisi),
`GenerationType.IDENTITY` → Liquibase'de `autoIncrement="true"`.

---

## Adımlar

### 3.1 Bağımlılık ve Maven plugin

`library-service/pom.xml`:
- `org.liquibase:liquibase-core` (versiyon Spring Boot parent'tan gelir, elle yazma).
- `org.liquibase:liquibase-maven-plugin` — `rollback`, `status`, `generateChangeLog`
  komutlarını CLI'dan çalıştırabilmek için. Bağlantı ayarları
  `library-service/liquibase.properties` dosyasından okunacak (local geliştirme
  kimlik bilgileri, `application.yml`'dakiyle aynı).

### 3.2 Changelog yapısı

```
library-service/src/main/resources/db/changelog/
├── db.changelog-master.xml          # sadece <include> listesi
└── changes/
    ├── 001-initial-schema.xml
    └── 002-book-isbn-unique.xml
```

Master dosyanın sadece include içermesi bilinçli: her yeni migration yeni bir dosya
olarak eklenir, uygulanmış dosyalara **asla dokunulmaz** (checksum kırılır).

### 3.3 001-initial-schema.xml

Ayrı ayrı changeset'ler halinde (her biri kendi `id`/`author` ile):
1. `createTable` author
2. `createTable` category
3. `createTable` book (+ `author_id` kolonu)
4. `addForeignKeyConstraint` book.author_id → author.id
5. `createTable` book_category + bileşik PK
6. `addForeignKeyConstraint` × 2 (book_category → book, category)

### 3.4 002-book-isbn-unique.xml

`addUniqueConstraint` ile `book.isbn` benzersiz yapılacak — hem gerçek bir domain
kuralı, hem de rollback denemesi için hedef. Liquibase bu değişikliği otomatik geri
alabiliyor; yine de `<rollback>` etiketini elle yazıp söz dizimini göstereceğiz
(her değişiklik tipi auto-rollback edilemiyor — bu ayrım öğretilecek).

### 3.5 application.yml

- `spring.jpa.hibernate.ddl-auto`: `update` → **`validate`**
  (`none` değil: `validate`, entity ile şema arasındaki sapmayı uygulama açılışında
  yakalar — migration yazmayı unutursak uygulama ayağa kalkmaz.)
- `spring.liquibase.change-log: classpath:db/changelog/db.changelog-master.xml`
  (varsayılan yol `.yaml` uzantısını arar, XML için açıkça belirtmek gerekiyor.)

### 3.6 Temiz başlangıç

`docker compose down -v && docker compose up -d` ile named volume silinip Postgres
sıfırdan ayağa kaldırılacak. **Bu mevcut test verisini kalıcı olarak siler** — Gün 1-2'de
elle girilen birkaç kayıt dışında değerli veri yok, bilinçli tercih.

### 3.7 Dökümantasyon

- `docs/gunler/gun-03.md` — taslak yazılıp onayına sunulacak, sonra commit+push.
- Staj defteri: `staj-defteri/Gun-03.docx`, **Tarih 08.07.2026, Sayfa No 4**
  (`RULES.md`'deki defter kurallarına göre).
- Plan dosyası `docs/plans/2026-07-08-gun-3-liquibase.md` olarak repoya kaydedilecek —
  Gün 1-2 planı gerçek takvim tarihiyle (`2026-08-17-...`) adlandırılmıştı; artık staj
  takvimine geçtiğimiz için yeni planlarda staj tarihi kullanılacak, eski dosya
  bağlantıları kırılmasın diye yeniden adlandırılmayacak.

---

## Öğretilecek kavramlar (yazarken açıklanacak)

- `ddl-auto: update` neden production'da tehlikeli (versiyonsuz, review'suz, geri alınamaz).
- `databasechangelog` ve `databasechangeloglock` tablolarının işlevi.
- changeset `id` + `author` + **checksum**: uygulanmış bir changeset'i düzenlemek neden
  uygulamayı patlatır, doğrusu neden "yeni changeset eklemek"tir.
- `validate` ile `none` farkı ve entity/şema sapmasının erken yakalanması.
- `preConditions` (kısaca değinilecek, kullanılmayacak).
- Gerçek hayatta canlı bir veritabanı varken izlenen yol: `generateChangeLog` +
  `changelogSync` (bilinçli olarak seçmedik; neden temiz başlangıcın daha öğretici
  olduğu anlatılacak).

---

## Doğrulama

1. `docker compose down -v && docker compose up -d` → `docker ps` ile postgres ayakta.
2. `./mvnw spring-boot:run` → başlangıç loglarında Liquibase'in changeset'leri
   uyguladığı görülmeli, hata olmamalı.
3. `psql` ile: `\dt` → `author`, `book`, `book_category`, `category` +
   `databasechangelog`, `databasechangeloglock`.
4. `SELECT id, author, filename, exectype FROM databasechangelog;` → tüm changeset'ler
   `EXECUTED` durumunda.
5. `\d book` → `isbn` üzerinde unique constraint görünüyor.
6. Fonksiyonel test: aynı `isbn` ile iki kez `POST /api/books` → ikincisi hata almalı.
7. Rollback: `./mvnw liquibase:rollback -Dliquibase.rollbackCount=1` →
   unique constraint düşer, `databasechangelog`'dan ilgili satır silinir.
8. `./mvnw liquibase:update` ile geri alınan değişiklik tekrar uygulanır.
9. Uygulamayı yeniden başlat → `validate` sapma bulmadan geçmeli.
10. Gün 1-2 endpoint'lerinden birkaçı (Book/Author CRUD) hâlâ çalışıyor mu — regresyon kontrolü.
