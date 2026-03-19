# Sınav Görevlendirme ve Planlama Sistemi (Exam Planning System)
## Roller Bazlı Akademik Sınav Yönetimi

Bu proje, üniversite departmanlarının sınav planlamalarını ve asistan görevlendirmelerini yönetmek için geliştirilmiş rol tabanlı (role-based) bir PHP web otomasyonudur. 

## 📚 Proje Bileşenleri

### Temel Modüller ve Sayfalar
- **`Start Screen.php`:** Tüm rollerin (Asistan, Sekreter, Bölüm Başkanı vb.) sisteme giriş yaptığı ana kimlik doğrulama ve oturum başlatma ekranı.
- **`Assistant.php`:** Araştırma görevlilerinin kendilerine atanan sınav gözetmenliklerini, tarih ve saatleri ile birlikte görüntüledikleri arayüz.
- **`Secretary.php`:** Bölüm sekreterlerinin sınav ve ders programını sisteme eklediği, ihtiyaç duyulan gözetmen sayısını sisteme girdiği ve sistemin asistanları puan sıralamasına göre otomatik görevlendirdiği temel operasyon modülü.
- **`Head of Department.php` / `Head of Secretary.php` / `Dean.php`:** Bölüm başkanı, sekreter şefi ve dekan gibi yetkililere özel görüntüleme, yönetim ve denetim alanları.
- **`Forgot Password.php`:** Kullanıcıların şifrelerini yenilemesi / görüntülemesi için kullanılan modül.

### Veritabanı ve Şema
- **`ER Diagram.svg`:** Sistemin karmaşık yapıdaki ilişkisel veritabanı altyapısını (`employee`, `faculty`, `department`, `courses`, `exam`) gösteren görsel varlık-ilişki diyagramı.

## 🔧 Kurulum ve Kullanım

### Gereksinimler
- PHP destekli herhangi bir yerel sunucu ortamı (XAMPP, MAMP, WAMP vb.)
- MySQL Veritabanı servisi.

### Projeyi Çalıştırma
1. Cihazınızda XAMPP/MAMP üzerinden MySQL ve Apache (veya Nginx) sunucularını başlatın.
2. MySQL ortamınızda `exam_planning` adında bir veritabanı oluşturup tabloları (`employee`, `faculty`, `department` vs.) içe aktarın. (*Not: Kodlar içindeki `$password` gibi bağlantı bilgileri varsayılan olarak `mysql` yapılmıştır, yerel ayarınıza göre güncelleyebilirsiniz.*)
3. Proje dizinini XAMPP için `htdocs`, WAMP/MAMP için de `www` klasörü içine taşıyın.
4. Tarayıcınızda `localhost/Start Screen.php` adresine girerek sistemi başlatın.

## 🎯 Proje Özellikleri

1. **Çoklu İzin Seviyesi (Role-Based Access):**
   - Sisteme farklı yetkilerle giriş yapılmasını sağlayan, her rol için farklı verilerin getirildiği gelişmiş `$_SESSION` mimarisi.
2. **Akıllı Gözetmen Atama Algoritması:**
   - Sekreter modülünden yeni bir sınav tanımlanırken talep edilen gözetmen sayısına göre; sistem **en az görev verilen (puanı en düşük)** asistanları tespit eder ve gözetmenlik atamalarını eşit iş yükü prensibine dayanarak otonom olarak gerçekleştirir.
3. **Çakışma (Collision) Kontrolü:**
   - Asistan atamaları sırasında asistanın aynı tarih ve saatte önceden planlanmış başka bir sınav görevi olup olmadığı otonom olarak denetlenir.

## 🔬 Teknik Detaylar

- **Backend:** Saf PHP (Procedural yaklaşım).
- **Frontend:** Standart HTML Form/Tablo Mimarisi.
- **Veritabanı Entegrasyonu:** `mysqli` kullanılarak ilişkisel veritabanına sorgular atılması (Bölüm, Fakülte, Ders ve Çalışanlar arası çok yönlü `JOIN` gerektiren yapılar).
- **Oturum ve Bellek Yönetimi:** Karmaşık veri kümelerinin anlık ve güvenli takibi için çok boyutlu `$_SESSION` dizilerinin etkin kullanımı.

## 📚 Öğrenilen Kavramlar

1. **Role & Session Management:** PHP ile dinamik kullanıcı oturumu yönetimi ve doğrulama süreçleri.
2. **Relational Database Design:** ER diyagramından gerçek koda geçiş, tablolar arası foreign-key bağları üzerine kurulu SQL algoritmaları.
3. **Algorithmic Logic:** Adil atama, çakışma tespiti ve çok boyutlu dizilerin (array) sıralanması gibi konsept algoritmaların web ortamına entegre edilmesi.

## 🚀 Geliştirilecek Özellikler

- [ ] Güvenlik amacıyla SQL Injection zaaflarının giderilmesi için **PDO / Prepared Statements** altyapısına geçiş yapılması.
- [ ] Parolaların düz metin olarak değil şifreli (hashing) olarak kaydedilmesi.
- [ ] Gelişmiş bir arayüz ve mobil uyumluluk için modern CSS (Bootstrap/Tailwind) implementasyonu.
- [ ] Geleneksel procedural yapıdan MVC (Model-View-Controller) mimarisine modüler geçiş.
