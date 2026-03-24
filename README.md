# CSE AI Term Project - Strategic Board Game (Move & Remove)
## AI Implementation with Minimax Algorithm (Minimax Algoritması ile Strateji Oyunu)

Bu proje, Yeditepe Üniversitesi Bilgisayar Mühendisliği bölümü Yapay Zeka dersi için geliştirilmiş, C++ ve Raylib tabanlı bir strateji oyunudur. Oyun, "Move and Remove" (Hareket Et ve Kaldır) mekaniğine dayanır ve derinliği artırılmış bir Minimax yapay zekası ile çalışır.

## 🎮 Oyun Kuralları ve Mekanikler

Oyun, 7x7 boyutunda bir tahta üzerinde iki oyuncu (İnsan vs AI) arasında sıra tabanlı olarak oynanır.

### Hamle Aşamaları
Her oyuncunun sırası iki alt aşamadan oluşur:
1. **Move (Hareket Et):** Oyuncu, mevcut parçasını komşu (yatay, dikey veya çapraz) boş bir kareye (`'E'`) taşır.
2. **Remove (Kaldır):** Hareket tamamlandıktan sonra, oyuncu tahta üzerindeki herhangi bir boş kareyi seçerek kalıcı olarak "engelli" (`'R' - Removed`) hale getirir. Bu kareye artık kimse hareket edemez.

### Kazanma Koşulu
- Kendi sırası geldiğinde **hiçbir geçerli hamlesi kalmayan** (tüm çevresi engellenmiş veya tahta kenarına sıkışmış) oyuncu oyunu **kaybeder**.

## 📁 Proje Yapısı ve Modüller

### include/ - Başlık Dosyaları
- **`game.h`:** Ana oyun döngüsü, texture yükleme ve ekran yönetimi fonksiyonları.
- **`node.h`:** Minimax ağacı için düğüm yapısını ve derinlik bazlı arama parametrelerini (`MAXDEPTH 7`) içerir.
- **`ai.h`:** Yapay zeka orkestrasyonu için `move()` ve `removeCell()` metodlarını tanımlar.

### src/ - Kaynak Kodlar
- **`main.cpp`:** Uygulama giriş noktası; `gameLoop` ve `gameEnd` süreçlerini yönetir.
- **`game.cpp`:** Raylib grafik motoru entegrasyonu, mouse girişi ve Move/Remove görselleştirmesi.
- **`node.cpp`:** **Minimax** algoritmasının kalbidir. Her düğümde tüm ihtimalleri değerlendirir ve terminal düğümlere puan atar.
- **`ai.cpp`:** Belirlenen en iyi hamle ve kaldırma koordinatlarını sırayla uygulayan AI arayüzü.

## 🤖 AI Algoritması Detayları

### Minimax ve Derinlik
- **Arama Derinliği:** Bu versiyonda arama derinliği **7 katmana** çıkarılmıştır. Bu, AI'nın hamle yaparken çok daha ilerisini görmesini sağlar.
- **Sıralı Operasyon:** Yapay zeka, önce en iyi hareketini belirler, ardından rakibini en çok kısıtlayacak kaldırma işlemini gerçekleştirir.

### Heuristic (Puanlama) Sistemi
AI, tahtadaki pozisyonları "mobilite" (hareket kabiliyeti) üzerinden değerlendirir:
- Eğer bir konumda AI daha fazla boş komşu kareye sahipse puan artar.
- Eğer rakibin hareket alanı kısıtlanıyorsa puan artar.
- Hamlesi kalmayan oyuncuya en düşük puan (`-8` veya `-9`) atanarak bu durumdan kaçınılır.

## 🔧 Kurulum ve Derleme

### Bağımlılıklar
- **C++17 Standart:** Derleme için C++17 destekleyen bir derleyici gereklidir.
- **Raylib 4.x:** Grafik arayüzü için Raylib kütüphanesinin sistemde kurulu olması gerekir.

### Derleme Komutları
```bash
# Projeyi derle
make

# Oyunu çalıştır
make run

# Bellek kontrolü (Valgrind)
make val

# Temizle
make clean
```

## 📋 Teknik Özellikler

- **Geliştirme Dili:** C++
- **Grafik Kütüphanesi:** Raylib (Statik olarak linklenmiştir)
- **Hata Yönetimi:** `-Wall -Wextra -Werror` bayrakları ile güvenli derleme.
- **Rastgelelik:** `std::mt19937` ile yüksek kaliteli rastgele hamle çeşitliliği.
- **Görselleştirme:**
    - `'A'`: AI Parçası
    - `'H'`: İnsan Parçası
    - `'P'`: Olası Hareketler (Possible Moves)
    - `'R'`: Kaldırılmış Hücre (Removed Cell)
