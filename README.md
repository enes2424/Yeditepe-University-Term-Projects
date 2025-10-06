# Yeditepe Üniversitesi CSE212 Dönem Projesi
## Space Invaders Oyunu (Java Swing ile 2D Oyun Geliştirme)

Bu proje, Yeditepe Üniversitesi Bilgisayar Mühendisliği Bölümü CSE212 ders## 👨‍💻 Geliştirici Bilgileri

**Ad:** Enes Mahmut ATEŞ  
**Numara:** 20200702008  
**E-posta:** enesmahmut.ates@std.yeditepe.edu.tr  
**Ders:** CSE212 - Nesne Yönelimli Programlama  
**Üniversite:** Yeditepe Üniversitesi Bilgisayar Mühendisliği

## 📝 Notlarrojesidir. Java Swing kütüphanesi kullanılarak geliştirilen klasik Space Invaders oyununun modern bir uyarlamasıdır. Nesne yönelimli programlama, GUI tasarımı, oyun döngüsü ve çarpışma algılama gibi temel oyun geliştirme konseptlerini öğretmek için tasarlanmıştır.

## 📚 Sınıflar ve Dosyalar

### SpaceInvadersTest.java
**Ana Sınıf:** `SpaceInvadersTest extends JFrame`

Oyunun ana penceresi ve menü sistemi.
- **Ana Fonksiyon:** `main(String[] args)`
- **Özellikler:** JFrame tabanlı ana pencere, menü çubuğu, oyun paneli yönetimi
- **Menü Öğeleri:**
  - Register: Yeni hesap oluşturma
  - Play Game: Oyunu başlatma  
  - High Score: En yüksek skorları görüntüleme
  - About: Geliştirici bilgileri
- **Boyut:** 1338x1000 piksel sabit boyut

### Panel.java
**Oyun Motoru:** `Panel extends JPanel implements KeyListener`

Oyunun ana mantığını ve render döngüsünü yöneten sınıf.
- **Ana Sınıflar:** Player, Enemy, Laser, GameObject, BoxCollider, EnemySpawner
- **Özellikler:** 
  - Delta time tabanlı hareket sistemi
  - Çarpışma algılama (Collision Detection)
  - Sprite yönetimi ve animasyon
  - Klavye input yönetimi (Arrow keys + Space)
- **Oyun Mekanikleri:**
  ```java
  // Oyuncu hareketi: Arrow keys (↑↓←→)
  // Ateş etme: Space tuşu (cooldown ile sınırlı)
  // Düşman spawn sistemi: 1-3 saniye random aralıklar
  // Level progression: Tüm düşmanları temizleme sistemi
  ```

### Account.java
**Kullanıcı Yönetimi:** `Account extends JFrame`

Oyuncu hesap oluşturma ve yönetim sistemi.
- **Özellikler:** Kullanıcı kaydı, doğrulama, oyuncu durumu
- **Doğrulama Kuralları:**
  - Minimum 8 karakter kullanıcı adı/şifre
  - Boşluk karakteri yasak
  - Benzersiz kullanıcı adı zorunlu
- **Oyuncu Durumu:**
  ```java
  private int health = 3;    // Oyuncu canı
  private int score = 0;     // Mevcut skor  
  private int level = 1;     // Mevcut level
  ```

### PlayGame.java
**Giriş Sistemi:** `PlayGame extends JFrame`

Oyuna giriş yapmak için kullanılan arayüz.
- **Özellikler:** Kullanıcı adı/şifre doğrulama, güvenli giriş
- **Giriş Başarılı:** Ana oyun ekranına geçiş ve oyuncu bilgilerini güncelleme
- **Hata Yönetimi:** Yanlış kimlik bilgileri için uyarı mesajı

### Score.java
**Skor Sistemi:** `Score implements Comparable<Score>`

Yüksek skor tablosu için skor nesnesi.
- **Özellikler:** Kullanıcı adı ve skor eşleştirmesi
- **Sıralama:** CompareTo methodu ile büyükten küçüğe sıralama
- **Format:** 7 haneli sağa hizalı skor görüntüleme

### Makefile
**Derleme Sistemi:** Otomatik derleme ve çalıştırma

Java projesini derlemek ve çalıştırmak için basit build sistemi.
- **Komutlar:**
  ```bash
  make all    # Projeyi derle
  make run    # Oyunu çalıştır  
  make clean  # .class dosyalarını temizle
  ```

## 🎮 Oyun Mekanikleri

### Oyuncu Kontrolü
- **Hareket:** ↑↓←→ ok tuşları ile 8 yönlü hareket
- **Ateş:** Space tuşu ile lazer ateşleme
- **Can Sistemi:** 3 can, her çarpışmada 1 can kaybı
- **Sınırlar:** Oyuncu ekran sınırlarında kalır

### Düşman Sistemi  
- **İki Tip Düşman:**
  - Alien1: 1000 HP, 0.1 hız multiplier, 10 puan
  - Alien2: 1500 HP, 0.15 hız multiplier, 20 puan
- **Spawn Oranı:** İlk levellerde %90 Alien1, %10 Alien2 (level ilerledikçe değişir)
- **Otomatik Ateş:** 1-5 saniye random aralıklarla lazer ateşi

### Level Progression
- **Level 1:** Normal hız ve HP (15 düşman)
- **Level 2:** 1.25x hız ve HP multiplier (25 düşman)
- **Level 3:** 1.5x hız ve HP multiplier (35 düşman)  
- **Level 4:** Oyun tamamlandı (otomatik yeniden başlama)
- **Level Geçiş:** Mevcut leveldeki tüm düşmanları öldürme
- **Düşman Artışı:** Her level +10 düşman, spawn oranı da değişir

### Grafik Varlıklar
```
background.png    # Scrolling arkaplan
player.png        # Oyuncu uzay gemisi
alien1.png        # Tip 1 düşman
alien2.png        # Tip 2 düşman  
laser1.png        # Oyuncu lazeri
laser2.png        # Düşman lazeri
bug1.png          # Ana menü ekranı
bug2.png          # Oyun menüsü
bug3.png          # Can ikonu
bug4.png          # Game Over ekranı
```

## 🔧 Kurulum ve Çalıştırma

### Gereksinimler
```bash
# Java Development Kit 8 veya üzeri
java -version

# Make (isteğe bağlı, manuel derleme mümkün)
make --version
```

### Kurulum Adımları
```bash
# Projeyi klonlayın veya indirin
git clone <repository-url>
cd Yeditepe-University-Term-Projects

# Grafik dosyalarının doğru konumda olduğundan emin olun
ls *.png

# Projeyi derleyin
make all
# veya manuel derleme:
javac SpaceInvadersTest.java
```

### Oyunu Çalıştırma
```bash
# Makefile ile
make run

# Manuel çalıştırma
java SpaceInvadersTest

# Temizlik
make clean
```

## 🎯 Öğrenilen Konseptler

1. **Nesne Yönelimli Programlama**: Class inheritance, encapsulation, polymorphism
2. **Java Swing GUI**: JFrame, JPanel, Event handling, Custom painting
3. **Oyun Geliştirme Temel Kavramları**:
   - Game loop ve delta time
   - Sprite rendering ve animation
   - Collision detection (AABB)
   - Input handling
4. **Veri Yapıları**: ArrayList kullanımı, Comparable interface
5. **File I/O**: Image loading ve BufferedImage kullanımı
6. **Event-Driven Programming**: ActionListener ve KeyListener implementasyonu
7. **State Management**: Oyun durumları ve kullanıcı oturumu yönetimi

## 📋 Teknik Detaylar

- **Programlama Dili:** Java 8+
- **GUI Framework:** Java Swing
- **Render Engine:** Graphics2D custom rendering
- **Image Format:** PNG sprites
- **Build System:** Makefile
- **Design Pattern:** Observer pattern (Event listeners)
- **Collision System:** Axis-Aligned Bounding Box (AABB)
- **Performance:** Delta time based movement, efficient ArrayList management

## 👨‍💻 Geliştirici Bilgileri

**Ad:** Enes Mahmut ATEŞ  
**Numara:** 20200702008  
**E-posta:** enesmahmut.ates@std.yeditepe.edu.tr  
**Ders:** CSE212 - Nesne Yönelimli Programlama  
**Üniversite:** Yeditepe Üniversitesi Bilgisayar Mühendisliği

## � Notlar

- Tüm sınıflar nesne yönelimli programlama prensipleri ile tasarlanmıştır
- Oyun Swing'in custom painting özelliği kullanılarak geliştirilmiştir  
- Collision detection basit AABB algoritması ile implemented edilmiştir
- Sprite-based animasyon sistemi delta time kullanarak smooth hareket sağlar
- High score sistemi local olarak ArrayList'te saklanır (persistence yok)
