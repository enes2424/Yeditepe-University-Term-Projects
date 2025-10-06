# Yeditepe Üniversitesi AI Dönem Projesi
## Strategic Board Game with AI Implementation (Yapay Zeka ile Strateji Oyunu)

Bu proje, Yeditepe Üniversitesi Bilgisayar Mühendisliği bölümü Yapay Zeka dersi dönem projesidir. 7x7 boyutundaki bir tahta üzerinde oynanan strateji oyunu, Minimax algoritması ile çalışan AI implementasyonu, C++ programlama dili ve Raylib grafik kütüphanesi kullanılarak geliştirilmiştir.

## 🎮 Oyun Mekanikleri

### Oyun Alanı
- **Tahta Boyutu:** 7x7 grid
- **Oyuncular:** İnsan (Human) vs AI
- **Başlangıç Durumu:** Her köşede 1 AI ve 1 İnsan parçası
- **Maksimum Hamle:** 50 tur
- **Oyun Modu:** Sıra tabanlı strateji oyunu

### Hareket Sistemi
- **İnsan Hamlesi:** 2 hareket hakkı (1 tur)
- **AI Hamlesi:** 2 hareket hakkı (1 tur)  
- **Hareket Kuralı:** Parçalar sadece komşu boş karelere hareket edebilir
- **Yakalama Sistemi:** Satır/sütun doldurma ile rakip parçaları yakalama

## 📁 Proje Dosya Yapısı

### include/ - Header Dosyaları
**Dosya:** `include/game.h`
- **Struct:** `Coordinates` - X,Y koordinat sistemi
- **Struct:** `AIPiece` - AI parçası bilgileri  
- **Fonksiyonlar:** Oyun kontrol, yakalama, texture yönetim fonksiyonları

**Dosya:** `include/ai.h`
- **Class:** `AI` - Yapay zeka sınıfı
- **Metodlar:** Hamle kontrolü, texture yönetimi, parça silme
- **Sabitler:** `AIPLAYER`, `HUMANPLAYER` tanımları

**Dosya:** `include/node.h`
- **Class:** `Node` - Minimax algoritması için düğüm sınıfı
- **Sabitler:** `MAXDEPTH 5` - Maksimum arama derinliği
- **Metodlar:** Dal çıkarma, hedef seçimi, harita kopyalama

### src/ - Kaynak Kod Dosyaları

**Dosya:** `src/main.cpp`
- **Fonksiyon:** `main()` - Ana program giriş noktası
- **Açıklama:** Oyun döngüsünü başlatır ve sonuç ekranını gösterir

**Dosya:** `src/game.cpp`
- **Fonksiyon:** `control()` - Oyun bitiş koşullarını kontrol eder
- **Fonksiyon:** `capture()` - Parça yakalama algoritması
- **Fonksiyon:** `gameLoop()` - Ana oyun döngüsü ve grafik renderı
- **Fonksiyon:** `gameEnd()` - Oyun sonu ekranı yönetimi
- **Özellik:** Raylib ile grafik arayüz, mouse input yönetimi

**Dosya:** `src/ai.cpp`
- **Fonksiyon:** `moveControl()` - AI hamle karar sistemi
- **Özellik:** Thread kullanımı ile gecikme efekti
- **Algoritma:** Minimax tree üzerinden optimal hamle seçimi

**Dosya:** `src/node.cpp`
- **Fonksiyon:** `branchOut()` - Minimax ağacı oluşturma
- **Fonksiyon:** `addLeaf()` - Yeni dal ekleme
- **Fonksiyon:** `getTarget()` - Rastgele hedef seçimi
- **Algoritma:** Minimax alpha-beta budama benzeri optimizasyon

### images/ - Grafik Dosyaları
- `Ai.png` - AI parçası görüntüsü
- `Background.png` - Oyun tahtası arkaplanı  
- `Draw.png` - Beraberlik ekranı
- `Human.png` - İnsan parçası görüntüsü
- `Player1Win.png` - AI kazanma ekranı
- `Player2Win.png` - İnsan kazanma ekranı
- `Target.png` - Hareket hedefi göstergesi

### lib/ - Kütüphane Dosyaları
- `libraylib.a` - Raylib statik kütüphanesi

## 🔧 Kullanım

### Gereksinimler
```bash
# Ubuntu/Debian
sudo apt-get install build-essential
sudo apt-get install libraylib-dev

# macOS (Homebrew) 
brew install raylib

# Windows (MinGW)
# Raylib'i manuel olarak kurun
```

### Derleme ve Çalıştırma
```bash
# Projeyi derle
make

# Oyunu çalıştır  
make run

# Valgrind ile memory leak kontrolü
make val

# Temizlik ve yeniden derleme
make re

# Temizlik
make clean
```

### Makefile Komutları
```makefile
# Derleme: g++ -Wall -Wextra -Werror -g ile strict compilation
# Bağlantı: Raylib statik kütüphanesi ile
# Çıktı: AITermProject executable
```

## 🤖 AI Algoritması Detayları

### Minimax Implementasyonu
1. **Arama Derinliği:** 5 seviye lookahead
2. **Evaluasyon:** Yakalanan parça sayısı bazlı puanlama
3. **Optimizasyon:** Rastgele seçim ile aynı puanlı hamleler arasında
4. **Zaman Yönetimi:** Thread sleep ile görsel gecikme efekti

### Karar Verme Süreci
```cpp
// AI için maksimum puan arar
if (who == AIPLAYER) {
    int max = -5;
    for (Node *neighbour : neighbours)
        if (neighbour->getPoint() > max)
            max = neighbour->getPoint();
    point = max;
}
```

### Yakalama Algoritması
- **Yatay Yakalama:** Satır tamamen doldurulduğunda
- **Dikey Yakalama:** Sütun tamamen doldurulduğunda  
- **Karışık Yakalama:** Kısmi doldurma durumlarında
- **Puan Sistemi:** +1 insan yakalama, -1 AI yakalama

## 🎯 Öğrenilen Kavramlar

1. **Oyun Teorisi:** Minimax algoritması ve sıra tabanlı strateji
2. **Grafik Programlama:** Raylib ile 2D grafik ve input yönetimi
3. **Veri Yapıları:** Tree struktur ve node-based algoritma
4. **Bellek Yönetimi:** Dynamic memory allocation ve proper cleanup
5. **Object Oriented Programming:** Class design ve encapsulation
6. **Algoritma Optimizasyonu:** Search pruning ve heuristic evaluation
7. **Game Development:** Game loop, state management, UI design

## 📋 Teknik Özellikler

- **Programlama Dili:** C++17 standart ile
- **Grafik Kütüphanesi:** Raylib 4.x
- **Derleme Bayrakları:** `-Wall -Wextra -Werror -g` (strict compilation)
- **Bellek Yönetimi:** RAII pattern ve manual memory management
- **Threading:** `std::this_thread::sleep_for` ile timing control
- **Random Generation:** `std::mt19937` ile high-quality randomness
- **Cross Platform:** Linux, macOS, Windows desteği

## 🏆 Oyun Kazanma Koşulları

1. **AI Kazanır:** Tüm insan parçaları yakalandığında veya 50 turda AI parçası sayısı fazlaysa
2. **İnsan Kazanır:** Tüm AI parçaları yakalandığında veya 50 turda insan parçası sayısı fazlaysa  
3. **Beraberlik:** 50 tur sonunda eşit parça sayısı veya hiç parça kalmaması
