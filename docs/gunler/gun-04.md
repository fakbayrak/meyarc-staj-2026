# Gün 4 — Design Patterns Mini-Projesi

**Tarih:** 2026-07-09
**Plan referansı:** [`staj_defteri_plani.md`](../../staj_defteri_plani.md) — Gün 4
**Detaylı plan:** [`docs/plans/2026-07-09-gun-4-design-patterns.md`](../plans/2026-07-09-gun-4-design-patterns.md)

## Yapılanlar

- **Ayrı mini modül:** `library-service`'e dokunmadan, repo kökünde bağımsız bir
  Java/Maven modülü olarak `design-patterns-lab/` oluşturuldu (kendi `mvnw`
  wrapper'ı, Spring parent'ı yok — sade `maven-compiler-plugin` + çalıştırmak
  için `exec-maven-plugin`). Amaç: her kalıbı Spring/DB/HTTP gürültüsü olmadan
  izole görmek.
- **Singleton — `LibraryConfig`:** Klasik `private static instance` +
  `getInstance()` yerine enum tabanlı Singleton idiomu (`enum LibraryConfig {
  INSTANCE; ... }`) kullanıldı — JVM tarafından garanti tek örnek, thread-safe,
  serialization'a karşı da güvenli.
- **Factory — `NotificationFactory`:** `Notifier` arayüzü ve `EmailNotifier`/
  `SmsNotifier` implementasyonları; `Main`, hangi somut sınıfla konuştuğunu
  bilmiyor, sadece `Notifier` arayüzünü kullanıyor.
- **Observer — `Library`/`Member`:** `java.util.Observer` (Java 9'dan beri
  deprecated) kullanılmadı; kendi `BookAddedListener` arayüzümüz yazıldı.
  `Library.addBook()` çağrıldığında kayıtlı tüm `Member`'lara haber gidiyor.
- **Strategy — `LateFeeStrategy`:** `BookReturn` sınıfı, hangi ücret hesaplama
  algoritmasını (`FlatLateFeeStrategy` / `PerDayLateFeeStrategy`) kullanacağını
  constructor'dan alıyor; `if/else` yığını yerine dışarıdan enjekte edilen
  davranış.
- **`Main.java`:** Dördünü tek bir senaryoda birleştirdi — config okunuyor, iki
  üye (observer) farklı bildirim kanallarıyla (factory) kaydediliyor, kitap
  eklenince ikisi de haberdar oluyor, aynı gecikme için iki farklı strateji
  farklı ücret üretiyor (10.0 TL sabit vs 12.5 TL gün başı) — bu fark,
  Strategy'nin gerçekten "swap edilebilir davranış" olduğunu somutlaştırıyor.
- **Test:** `./mvnw compile exec:java` ile IntelliJ dışında bir kez, sonra
  IntelliJ'den `Main.java` doğrudan çalıştırılarak bir kez daha doğrulandı;
  çıktı beklenen dört bloğu (Singleton/Factory+Observer/Strategy) sorunsuz
  üretti.

## Karşılaşılan sorunlar ve çözümleri

1. **`exec-maven-plugin` versiyon uyuşmazlığı riski.** Plan sırasında güncel
   bir versiyon (3.5.0) yazılmıştı; kod yazımına başlamadan önce local Maven
   deposu kontrol edildi ve sadece `3.6.3`'ün cache'lendiği görüldü. İnternete
   çıkmadan derlemenin çalışması için `pom.xml` doğrudan `3.6.3` ile yazıldı —
   çalışma zamanında hataya dönüşmeden önce fark edilen küçük bir düzeltme.

## Öğrenilenler

- Enum tabanlı Singleton, çoklu thread'in aynı anda ilk çağrıyı yapması
  durumunda klasik yaklaşımda ortaya çıkabilecek "iki örnek oluşma" riskini
  (`double-checked locking` ile elle çözülen bir problem) JVM garantisiyle
  bedavaya kapatıyor.
- Factory deseni, yeni bir varyant eklemeyi (`PUSH` bildirimi gibi) sadece
  factory'ye bir `case` eklemekle sınırlıyor — `Main`'e veya `Notifier`'ı
  kullanan hiçbir yere dokunmadan.
- Kendi yazdığımız `BookAddedListener`, Spring'in `ApplicationEventPublisher`/
  `@EventListener` mekanizmasının temelde yaptığı şeyin çıplak hali — ileride
  Spring'de göreceğimiz bir soyutlamayı sıfırdan görmüş olduk.
- Strategy'nin asıl kanıtı, iki farklı implementasyonun **aynı girdiyle
  farklı çıktı** üretmesi (`10.0` vs `12.5`) — davranış gerçekten swap
  edilebiliyor, `BookReturn` hangi stratejiyle çalıştığını bilmiyor.
- Dört kalıbı da Spring/DB olmadan, sade Java ile yazmak, bir sonraki günlerde
  (Gün 5 Gateway, Gün 8 event-driven) bu kalıpların çerçeve/altyapı
  seviyesinde nasıl "büyütüldüğünü" ayırt etmeyi kolaylaştıracak bir referans
  noktası oluşturdu.

## Sırada ne var

Gün 5: Gateway entegrasyonu — mikroservis mimarisine bir API Gateway eklenerek
servisler arası yönlendirme ve merkezi giriş noktası sağlanacak.
