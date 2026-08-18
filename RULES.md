# RULES.md — StajRaporu-2026

> Proje kuralları. Global `~/.claude/RULES.md` ile çelişirse bu dosya öne geçer.
> Kapsam: `staj-defteri/` klasöründeki resmi staj defteri sayfalarını (docx) doldururken.

## Staj Defteri Sayfaları (staj-defteri/*.docx)

1. **Önce mevcut günün örneğine bak.** Format konusunda tahmin yürütme —
   `staj-defteri/Gun-01.docx` (ve varsa devam sayfaları) güncel referanstır.
   Yeni bir gün doldurulacaksa önce o dosyaları python-docx ile açıp paragraf/run
   yapısını incele, sonra aynısını uygula.

2. **Düzyazı biçimi:** Arial 12pt, iki yana yaslı (`justify`), ilk satır
   girintisiz, tek satır aralığı.

3. **Kod biçimi:** Consolas 9pt, sol hizalı — kod bloğunu asla justify yapma.
   Kod her zaman gerçek kaynak dosyadan alınır, uydurulmaz.

4. **Kod satır sonu — bilinen hata kaynağı:** Her kod satırı için yeni bir run
   oluştur; `run.add_break()` çağrısı ve ardından `run.add_text(line)` AYNI run
   üzerinde yapılmalı. Break'i başka/eski bir run'a eklemek satırların üst üste
   yığılıp birbirine yapışmasına sebep olur — bu hata bir kez yaşandı ve fark
   edilmesi zor oldu, her yeni kod bloğunda bu deseni tekrar doğrula.

5. **Boş satır** için gerçek boş paragraf değil, ` ` (nbsp) içeren bir satır
   kullan — mevcut referans dosyalardaki kalıp bu.

6. **Sayfa sayısını tahmin etme, Word COM ile doğrula.** Her fiziksel sayfa
   dosyasını `ComputeStatistics(2)` ile aç ve gerçekten 1 sayfa olduğunu
   kontrol et. Sığmıyorsa içerik/kod bloklarını sayfalar arasında yeniden dağıt,
   tahminle bırakma.

7. **Uzun sınıfların tamamını değil, günün yeni öğrettiği kısmını göster.**
   Önceki günlerde zaten gösterilmiş boilerplate'i (ör. tekrarlayan CRUD
   metodları) atla; sadece o günün konusuna özgü satırları al.

8. **Import satırlarını kod bloklarından çıkar**, ardışık boş satırları
   sadeleştir, baştaki/sondaki boş satırları kırp.

9. **Bir fiziksel sayfa = bir docx dosyası** (`Gun-NN.docx`,
   `Gun-NN-Sayfa2.docx`, ...). Header tablosunda Tarih sabit kalır, Sayfa No
   artan olmalı; footer'daki "STAJ SORUMLUSU" onay/imza bloğuna asla dokunma.
   Tarih ve Sayfa No değer hücrelerini doldururken hem paragrafı
   (`WD_ALIGN_PARAGRAPH.CENTER`) hem hücreyi (`WD_ALIGN_VERTICAL.CENTER`)
   ortala — yazı hücrenin sol/üst köşesine değil tam ortasına gelmeli.

10. **Kod eklenip eklenmeyeceğini veya hangi bloğun ekleneceğini varsayma.**
    Bazı günler sadece düzyazı, bazı günler düzyazı+kod isteniyor — bu tercih
    oturumdan oturuma değişebilir; başlamadan önce sor ya da öneri sun, önceki
    günün tercihini sabit kabul etme.

11. **Word açıksa dokunma.** Herhangi bir docx'i düzenlemeden/üzerine
    yazmadan önce `Get-Process WINWORD` ile kontrol et; kullanıcının kendi
    açtığı bir pencereyi force-kill etme, kapatmasını iste ve sonra devam et.

12. **Doldurma script'i tek seferliktir.** İş bitince (`staj-defteri/_fill_*.py`
    gibi) sil — kalıcı bir araç olarak tutma, her seferinde o günün içeriğine
    göre yeniden yaz.

13. **`staj-defteri/` klasörü gitignore'dadır** — resmi imzalı belgeler repo'ya
    girmez, bu klasördeki dosyaları commit etme.

14. **Türkçe karakter bozukluğu bash `cat`/print çıktısında normaldir**, gerçek
    dosya bozukluğu değildir. Gerçek içeriği doğrulamak için Read tool veya
    Word/python-docx (UTF-8) kullan, terminal çıktısına güvenme.
