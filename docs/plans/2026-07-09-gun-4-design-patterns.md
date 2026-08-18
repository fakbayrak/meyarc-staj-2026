# Gün 4: Design Patterns Mini-Projesi

## Context

30 günlük staj planının 4. maddesi: "Singleton, Factory, Observer ve Strategy gibi
yaygın tasarım kalıpları küçük bir Java uygulamasında canlı örneklerle uygulandı."

Gün 1-3'ten farklı olarak bu gün `library-service`'in üzerine inşa etmiyor — plan
metni bilinçli olarak "küçük bir Java uygulaması" diyor. Onaylanan karar: **ayrı,
bağımsız bir mini modül** (`design-patterns-lab/`), `library-service`'e dokunulmadan,
kendi başına çalıştırılabilen sade bir Java programı. Amaç: her kalıbı izole, gürültüsüz
bir örnekte görmek — Spring/DB/HTTP katmanı olmadan, sadece kalıbın kendisine odaklanmak.

Tema tutarlılığı için (ama kod bağımlılığı YOK) örnekler yine kütüphane/kitap
dünyasından seçildi — dört kalıp birbirinden bağımsız ama tek bir küçük senaryoda
(`Main.java`) bir araya geliyor, böylece "neden bu kalıp burada" sorusu somut kalıyor.

---

## Modül yapısı

```
design-patterns-lab/
├── pom.xml                 # plain Java 21, Spring yok, sadece maven-compiler-plugin
├── mvnw, mvnw.cmd          # library-service'teki gibi wrapper (global Maven CLI yok)
└── src/main/java/com/meyarc/patterns/
    ├── Main.java
    ├── singleton/
    │   └── LibraryConfig.java
    ├── factory/
    │   ├── Notifier.java
    │   ├── EmailNotifier.java
    │   ├── SmsNotifier.java
    │   └── NotificationFactory.java
    ├── observer/
    │   ├── BookAddedListener.java
    │   ├── Member.java
    │   └── Library.java
    └── strategy/
        ├── LateFeeStrategy.java
        ├── FlatLateFeeStrategy.java
        └── PerDayLateFeeStrategy.java
```

`pom.xml` bağımsız (parent yok, `library-service`'in Spring Boot parent'ından ayrı) —
`<properties><maven.compiler.release>21</maven.compiler.release></properties>` yeterli.

---

## Kalıp başına senaryo

### Singleton — `LibraryConfig`
Kütüphanenin tek, global ayarı (ör. `maxBorrowDays`). Enum tabanlı Singleton idiomu
kullanılacak (`enum LibraryConfig { INSTANCE; ... }`) — Effective Java'nın önerdiği,
thread-safe ve serialization-güvenli yol. Klasik `private static instance` +
`getInstance()` yaklaşımının çoklu thread'de neden kırılgan olduğu (double-checked
locking ihtiyacı) yorum olarak anlatılacak, ama kod enum idiomunu kullanacak.

### Factory — `NotificationFactory`
Üyeye haber verme şekli (`EMAIL` / `SMS`) çalışma zamanında seçiliyor;
`NotificationFactory.create(type)` uygun `Notifier` implementasyonunu dönüyor.
Basit (statik) Factory — Factory Method'un `abstract class`'lı tam hali değil,
kalıbın en sık görülen pratik biçimi.

### Observer — `Library` / `Member`
`Library`, `BookAddedListener` arayüzünü uygulayan üyeleri (`Member`) tutuyor;
yeni bir kitap eklendiğinde (`addBook(...)`) tüm dinleyicilere haber veriyor.
`java.util.Observer`/`Observable` **kullanılmayacak** — bu API Java 9'dan beri
deprecated; kendi küçük listener arayüzümüzü yazmak hem güncel pratik hem de
Spring'in `ApplicationEventPublisher`'ının temelde ne yaptığını önden gösteriyor.

### Strategy — `LateFeeStrategy`
Gecikmiş kitap iade cezası iki farklı algoritmayla hesaplanabiliyor:
`FlatLateFeeStrategy` (sabit ücret) ve `PerDayLateFeeStrategy` (gün başı ücret).
Hangi stratejinin kullanılacağı çalışma zamanında (constructor injection ile)
seçiliyor — `if/else` yığını yerine swap edilebilir davranış.

### `Main.java`
Dördünü tek bir okunabilir senaryoda birleştiriyor: `LibraryConfig.INSTANCE`'tan
ayar okunuyor → bir `Library` oluşturulup `Member`'lar (observer) ekleniyor →
`NotificationFactory` ile üyenin tercih ettiği bildirim kanalı üretiliyor →
kitap eklenince observer'lar tetikleniyor → örnek bir geç iade için iki farklı
`LateFeeStrategy` ile ücret hesaplanıp konsola yazdırılıyor.

---

## Dokümantasyon

- `docs/gunler/gun-04.md` — taslak yazılıp onayına sunulacak, sonra commit+push
  (onaylanan steady-state akış: içerik onaylanınca otomatik commit+push).
- Staj defteri: `staj-defteri/Gun-04.docx`, Tarih **09.07.2026**, Sayfa No **5**.
- Plan dosyası: `docs/plans/2026-07-09-gun-4-design-patterns.md`.

---

## Doğrulama

Otomatik test yok (Gün 14'e bırakıldı, önceki günlerle tutarlı). Elle doğrulama:

1. `cd design-patterns-lab && ./mvnw compile exec:java -Dexec.mainClass=com.meyarc.patterns.Main`
   (ya da IntelliJ'den `Main.java`'yı doğrudan çalıştır).
2. Konsol çıktısında sırayla: config değeri, observer bildirimleri (her üye için),
   factory'nin ürettiği bildirim kanalı, ve iki farklı strateji için **farklı**
   iki ücret değeri görülmeli — dördü de görünür şekilde çalıştığını kanıtlıyor.
3. Kod okunarak: her kalıbın arayüz/implementasyon ayrımı ve `Main`'in bu
   soyutlamalara nasıl bağlandığı gözden geçirilecek (yazarken adım adım
   açıklanacak zaten — teaching style).
