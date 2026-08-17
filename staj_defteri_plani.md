# 30 Günlük Staj Defteri Planı

## Bölüm 1 — Kendi CRUD Sistemim ve Bağlı Bileşenler (Gün 1-27)

### Gün 1-2: Entity-Service-Repository-Controller Mimarisiyle CRUD Servisi Geliştirme
Java ile Entity-Service-Repository-Controller katmanlı mimarisine sahip, temel CRUD işlemlerini gerçekleştirebilen bir mikroservis geliştirildi. Katmanlar arası sorumluluk ayrımı ve REST API tasarımı üzerine çalışıldı.

### Gün 3: Liquibase Migration Dosyalarının Oluşturulması
Veritabanı şema değişikliklerini versiyonlayarak yönetebilmek için projeye Liquibase entegre edildi ve migration dosyaları oluşturuldu.

### Gün 4: Design Patterns Mini-Projesi
Singleton, Factory, Observer ve Strategy gibi yaygın tasarım kalıpları küçük bir Java uygulamasında canlı örneklerle uygulandı.

### Gün 5: Gateway Entegrasyonu
Mikroservis mimarisine bir API Gateway eklenerek servisler arası yönlendirme ve merkezi giriş noktası sağlandı.

### Gün 6: Authorizer Entegrasyonu
Mimariye kimlik doğrulama ve yetkilendirme katmanı eklendi, servis erişimleri güvenlik altına alındı.

### Gün 7: Mikroservisler Arası Senkron İletişim
Feign Client, RestTemplate ve WebClient yöntemleri karşılaştırılarak servisler arası senkron iletişim örnekleri geliştirildi.

### Gün 8: Kafka/RabbitMQ ile Event-Driven Mini Proje
İki servis arasında asenkron mesajlaşma sağlamak için Kafka veya RabbitMQ kullanılarak event-driven bir iletişim örneği kuruldu.

### Gün 9: Redis Cache Entegrasyonu
Bir endpoint için Redis tabanlı cache mekanizması eklendi ve cache'li/cache'siz performans karşılaştırması yapıldı.

### Gün 10: Elasticsearch ile Arama Özelliği
Projeye Elasticsearch entegre edilerek basit bir full-text arama özelliği geliştirildi.

### Gün 11: GraphQL ile API Denemesi
REST API'ye alternatif olarak GraphQL kullanılarak küçük bir API denemesi yapıldı.

### Gün 12: Spring Boot Actuator ile Health-Check ve Monitoring
Servise Actuator entegrasyonu yapılarak özel health indicator'lar, güvenli monitoring endpoint'leri ve metrik takibi sağlandı.

### Gün 13: API Dokümantasyonu
Springdoc/Swagger ile projenin API dokümantasyonu hazırlandı, endpoint açıklamaları ve örnek istekler eklenerek bir Postman koleksiyonu oluşturuldu.

### Gün 14: Test Yazımı
Geliştirilen servisler için JUnit ve Mockito kullanılarak unit ve entegrasyon testleri yazıldı.

### Gün 15-17: Frontend Tasarımı
Mikroservis mimarisine uygun bir frontend arayüzü tasarlandı ve geliştirildi; responsive tasarım ve state yönetimi gibi konular ele alındı.

### Gün 18: Frontend Skillerinin Araştırılması
UI Max, Impeccable, Stitch gibi çeşitli frontend tasarım araçları/skilleri denenip karşılaştırıldı.

### Gün 19: Docker Containerization
Geliştirilen servisler Docker ile containerize edildi; multi-stage build ve docker-compose ile local ortam kurulumu yapıldı.

### Gün 20: GitHub Actions ile CI/CD Pipeline
Projenin GitHub Actions üzerinden otomatik build, test ve deploy süreçlerini yöneten bir CI/CD pipeline'ı kuruldu.

### Gün 21: Kubernetes (k8s) Kurulumu
Servislerin k8s üzerinde çalıştırılması için Deployment, Service, Ingress ve ConfigMap/Secret yapılandırmaları oluşturuldu.

### Gün 22: VPS'e Deploy
Geliştirilen sistem bir VPS ortamına deploy edilerek canlıya alındı.

### Gün 23: Veritabanı Yedekleme Stratejisi
Düzenli, otomatik veritabanı yedekleme süreci kuruldu; saklama politikası belirlendi ve geri yükleme testi yapıldı.

### Gün 24-25: n8n ile Slack Bot Tasarımı
n8n üzerinden, yapay zeka alanındaki güncel gelişmeleri günlük olarak şirket Slack kanalına ileten bir otomasyon botu geliştirildi.

### Gün 26: AI Destekli Geliştirme Araçları Araştırması
Geliştirme sürecinin verimliliğini artırmaya yönelik çeşitli agent skill ve plugin'ler denenip değerlendirildi.

### Gün 27: Multi-Agent Mimari Araştırması
Multi-agent sistemler için kullanılabilecek farklı yöntem ve teknolojiler araştırıldı, örnek çözümler karşılaştırıldı.

---

## Bölüm 2 — Şirket İçi Ürün Geliştirme Sürecinde Yaptığım İşler (Gün 28-30)

### Gün 28: Gözlemlenebilirlik (Observability) Aracı Entegrasyonu
LLM tabanlı sohbet geçmişinin, kullanıcı/session bazlı trace ve maliyet takibinin yapılabildiği bir gözlemlenebilirlik aracı entegrasyonu üzerinde çalışıldı.

### Gün 29: Log Gözlem ve Sistem İzleme Entegrasyonu
Sistem loglarının görüntülenip filtrelenebildiği bir log gözlem platformu entegre edildi; ayrıca sistemin ayakta kalıp kalmadığını kontrol eden bir izleme/uyarı aracı bağlandı.

### Gün 30: Self-Hosted CI/CD Runner Kurulumu
CI/CD süreçlerinin dış kaynak kısıtlarına bağlı kalmadan çalışabilmesi için kendi sunucumuzda self-hosted bir runner kurulumu yapıldı.
