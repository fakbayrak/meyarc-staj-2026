# Gün 2 — İlişkiler, Merkezi Hata Yönetimi, Sayfalama, Author/Category CRUD

**Tarih:** 2026-08-17
**Plan referansı:** [`staj_defteri_plani.md`](../../staj_defteri_plani.md) — Gün 1-2
**Detaylı plan:** [`docs/plans/2026-08-17-gun-1-2-crud-servisi.md`](../plans/2026-08-17-gun-1-2-crud-servisi.md)

## Yapılanlar

- **İlişkili entity'ler:** `Author` ve `Category` entity'leri ve `JpaRepository`
  tabanlı repository'leri eklendi. `Book` entity'sine `Author`'a `@ManyToOne`,
  `Category`'lere `@ManyToMany` ilişkileri kuruldu (`book_category` ara tablosu
  üzerinden).
- **Merkezi hata yönetimi:** `exception/ResourceNotFoundException.java` ve
  `@RestControllerAdvice` ile `exception/GlobalExceptionHandler.java` eklendi;
  ortak bir `dto/ErrorResponse.java` (`status`, `message`, `timestamp`) formatı
  kullanılıyor. Gün 1'de bilinçli olarak ham bırakılan `RuntimeException`,
  `BookServiceImpl`'de gerçek `ResourceNotFoundException`'a (→ 404) çevrildi;
  `@Valid` doğrulama hataları (`MethodArgumentNotValidException`) da aynı
  formatta 400 dönecek şekilde yakalandı.
- **Sayfalama ve sıralama:** `BookService.getAll()` düz `List` yerine
  `Page<BookResponse> getAll(Pageable pageable)` dönecek şekilde değiştirildi;
  `GET /api/books?page=&size=&sort=` artık Spring Data `Pageable` ile çalışıyor.
- **Author & Category için uçtan uca CRUD:** DTO (`*Request`/`*Response`),
  mapper, service (+impl), controller katmanları — Book'taki güncel desenle
  (sayfalama dahil) birebir aynı yapıda yazıldı.
- **Book ↔ Author/Category bağlantısının API'ye açılması:** `BookRequest`'e
  `authorId` ve `categoryIds` eklendi; `BookServiceImpl` bu id'leri
  `AuthorRepository`/`CategoryRepository` üzerinden gerçek entity'lere
  çeviriyor; `BookResponse`'a `authorName`/`categoryNames` eklendi ki bağlantı
  sonucu doğrudan yanıtta görülebilsin.
- **Uçtan uca manuel test:** Postman ve PowerShell (`Invoke-RestMethod`) ile
  Author CRUD, Category CRUD, Book↔Author/Category bağlantısı, sayfalama/
  sıralama ve hata senaryoları test edildi; sonuçlar `psql` ile veritabanı
  seviyesinde de (tablolar, foreign key'ler, `book_category` satırları) çapraz
  doğrulandı.

## Karşılaşılan sorunlar ve çözümleri

1. **Postman'de body yerine URL'ye JSON yazılması** — bir `PUT` isteğinde JSON
   gövde yanlışlıkla URL çubuğunun devamına yazıldı
   (`.../authors/1 {"name": "..."}`). Sonuç: `id` path variable'ı bozuk bir
   string oldu, Spring bunu sayıya çeviremeyip 400 döndü; hata gövdesindeki
   `path` alanı (URL-encode edilmiş hali) kök nedeni gösterdi. Çözüm: JSON'u
   Body sekmesine (raw + JSON) taşımak. Gerçek bir uygulama hatası değildi,
   client tarafı bir kullanım hatasıydı.

## Öğrenilenler

- `@ManyToOne`'ın JPA varsayılanı **EAGER**, `@ManyToMany`'nin ise **LAZY** —
  ikisini de elle `FetchType.LAZY` yaparak N+1 sorgu riskini baştan kapattık;
  bu, Gün 9'da cache konusunda tekrar döneceğimiz bir karar.
- Many-to-many koleksiyonlarda `List` yerine `Set` kullanmak, Hibernate'in
  "bag semantics" tuzağını (silme/güncellemede tüm ilişki satırlarını silip
  yeniden ekleme davranışı) önlüyor.
- `@RestControllerAdvice`, her controller'da tekrar eden try-catch'i ortadan
  kaldırıp tek bir yerden tutarlı bir hata JSON formatı sağlıyor.
- `Page<T>`'in kendi `map()` metodu var (Java `Stream`'den farklı) — sayfalama
  meta verisini (toplam eleman/sayfa) koruyarak içeriği dönüştürmeye izin
  veriyor.
- Katman sorumluluk sınırı: id'den gerçek entity'ye çözümleme (`authorId` →
  `Author`) mapper'ın değil, veri erişimine ihtiyaç duyduğu için service
  katmanının işi — mapper sadece saf DTO↔entity alan eşlemesi yapmalı.
- `PUT` tam-değiştirme (full replace) semantiğinde, istekte gönderilmeyen bir
  alan "değiştirme" değil "temizle" anlamına geliyor — bunu `authorId`'yi
  boş bırakan bir `PUT` ile bilerek test edip doğruladık.
- Postman'de bir isteğin dönen `id`'sini **Tests** sekmesinde
  `pm.collectionVariables.set(...)` ile saklayıp sonraki isteklerde
  `{{değişken}}` olarak kullanmak, çok adımlı zincirleme testlerde elle id
  kopyalamayı ortadan kaldırıyor.

## Sırada ne var

Gün 3: Liquibase migration dosyalarının oluşturulması — şu ana kadar şema
`ddl-auto: update` ile Hibernate tarafından otomatik yönetiliyordu; bunu
versiyonlanan, geri alınabilir migration dosyalarına taşıyacağız.
