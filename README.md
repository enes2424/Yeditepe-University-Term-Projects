# CSE211 Term Project - Tetris Game

## Object-Oriented Programming with C++ (Nesne Yönelimli Programlama)

Bu proje, Yeditepe Üniversitesi CSE211 (Object Oriented Programming) dersi için geliştirilmiş C++ tabanlı klasik Tetris oyunudur. OOP prensipleri, dinamik bellek yönetimi, grafik programlama ve oyun döngüsü tasarımı konularında pratik deneyim kazandırmak için tasarlanmıştır.

## 📚 Modüller

### tetris.cpp - Ana Oyun Motoru

**Dosya:** `tetris/tetris.cpp`

Ana oyun döngüsü ve oyun mekaniğini içeren sınıf.

- **Fonksiyon:** `int main()`
- **Açıklama:** Raylib tabanlı grafik render, kullanıcı girişi ve oyun mantığı
- **Özellikler:**
  - 16x12 oyun tahtası sistemi
  - Gerçek zamanlı şekil düşürme ve rotasyon
  - Skor hesaplama ve seviye sistemi
  - Çoklu satır temizleme bonusu
  - Ses efektleri ve müzik entegrasyonu
  - Game over kontrolü
- **Ana Fonksiyonlar:**
  ```cpp
  void draw_board(ColorMatrix& color_matrix)
  bool is_collison(ColorMatrix &color_matrix, Shape &shape, int x, int y)
  void save(ColorMatrix& color_matrix, Shape& shape, int x, int y, int &score, int &level, int &z, bool &is_game_over)
  ```

### Shape.hpp/cpp - Şekil Yönetim Sistemi

**Dosya:** `tetris/Shape.hpp`, `tetris/Shape.cpp`

Tetris şekillerini temsil eden ve yöneten sınıf.

- **Sınıf:** `Shape`
- **Açıklama:** Şekil rotasyonu, çizimi ve kopyalama işlemleri
- **Özellikler:**
  - 4 rotasyon durumu (0°, 90°, 180°, 270°)
  - Dinamik bellek yönetimi
  - RGB renk desteği
  - Matris tabanlı şekil temsili
- **Yapı:**
  ```cpp
  class Shape {
  private:
      int   width, height;
      Color color;
      int** shape;              // Aktif şekil matrisi
      int   widths[4], heights[4];
      int** shapes[4];          // 4 rotasyon durumu
  public:
      Shape(int** shape, int height, int width, Color color);
      void rotate(int status);  // Şekil döndürme
      void draw(int x, int y);  // Ekrana çizim
      void copy(CopyShape *copy_shape);
  };
  ```

### ShapeList.hpp/cpp - Şekil Koleksiyonu

**Dosya:** `tetris/ShapeList.hpp`, `tetris/ShapeList.cpp`

Oyundaki tüm Tetris şekillerini yöneten liste sınıfı.

- **Sınıf:** `ShapeList`
- **Açıklama:** 10 temel Tetris şeklini içeren dinamik dizi
- **Şekiller:**
  - PlusSign (Artı İşareti)
  - SmallSquare (Küçük Kare)
  - CornerPiece (Köşe Parçası)
  - TallTower (Uzun Kule)
  - Zigzag (Zikzak)
  - UShape (U Şekli)
  - StepShape (Basamak)
  - ArrowPointingUp (Yukarı Ok)
  - DoubleZigzag (Çift Zikzak)
  - Pyramid (Piramit)
- **Özelleşirme:**

  ```cpp
  // Kullanıcıdan özel renk seçimi
  Color get_color(string* color_name_list, Color* color_list, string str)

  // Yeni şekil ekleme
  void add_shape_list(Shape*** list, int& size, ...)
  ```

### Color.hpp/cpp - Renk ve Tahta Yönetimi

**Dosya:** `tetris/Color.hpp`, `tetris/Color.cpp`

Oyun tahtası ve renk yönetim sistemi.

- **Sınıflar:** `ColorClass`, `ColorMatrix`
- **Açıklama:** 16x12 matris yapısı ile tahta durumu takibi
- **Özellikler:**
  - Dinamik bellek tahsisi
  - Null-safe pointer yönetimi
  - RGB renk wrapper sınıfı
- **Yapı:**
  ```cpp
  class ColorMatrix {
      ColorClass*** colors;    // 16x12 3D pointer array
  public:
      ColorClass *get(int index1, int index2);
      void set(int index1, int index2, Color &color, int status);
  };
  ```

### Piece.hpp - Şekil Tanımlamaları

**Dosya:** `tetris/Piece.hpp`

Oyundaki şekillerin enum tanımları.

- **Enum:** `Piece`
- **Açıklama:** Şekil tipi tanımlamaları için sayısal ID'ler
- **Değerler:**
  ```cpp
  enum Piece {
      PlusSign, SmallSquare, CornerPiece, TallTower, Zigzag,
      UShape, StepShape, ArrowPointingUp, DoubleZigzag, Pyramid
  };
  ```

## 🔧 Kurulum ve Çalıştırma

### Gereksinimler

```bash
# Ubuntu/Debian için Raylib kurulumu
sudo apt update && sudo apt install -y build-essential git
sudo apt install -y libraylib-dev

# Manuel Raylib kurulumu
git clone https://github.com/raysan5/raylib.git
cd raylib/src
make PLATFORM=PLATFORM_DESKTOP
sudo make install
```

### Derleme ve Çalıştırma

```bash
# G++ ile derleme
g++ -o tetris tetris/tetris.cpp tetris/Shape.cpp tetris/ShapeList.cpp tetris/Color.cpp \
    -lraylib -lGL -lm -lpthread -ldl -lrt -lX11

# Visual Studio 2022 ile derleme
# tetris.sln dosyasını Visual Studio ile açın
# Build -> Build Solution (Ctrl+Shift+B)

# Çalıştırma
./tetris

# Gerekli dosyalar (aynı dizinde bulunmalı)
# - background.png
# - gameover.png
# - tetris.wav
# - game-over.wav
# - trung.wav
```

### Visual Studio Proje Yapılandırması

```xml
<!-- tetris.vcxproj içinde -->
<ItemDefinitionGroup>
  <ClCompile>
    <AdditionalIncludeDirectories>raylib/include</AdditionalIncludeDirectories>
  </ClCompile>
  <Link>
    <AdditionalLibraryDirectories>raylib/lib</AdditionalLibraryDirectories>
    <AdditionalDependencies>raylib.lib;opengl32.lib;winmm.lib</AdditionalDependencies>
  </Link>
</ItemDefinitionGroup>
```

## 🎮 Oyun Kontrolleri

### Tuş Atamaları

- **↑ / W**: Şekli döndür (4 rotasyon durumu)
- **↓ / S**: Hızlı düşür
- **← / A**: Sola hareket ettir
- **→ / D**: Sağa hareket ettir
- **SPACE**: Sonraki şekille değiştir (tek kullanımlık)
- **ESC**: Oyundan çık

### Oyun Kuralları

1. Şekiller üstten rastgele düşer
2. Tam satır oluşturulduğunda temizlenir (+100 puan)
3. Çoklu satır bonusu: her ekstra satır +100 puan
4. Seviye ilerledikçe düşüş hızı artar
5. Şekiller üst satıra ulaşırsa oyun biter

## 🎯 Teknik Özellikler

### 1. Object-Oriented Design

- **Encapsulation**: Private data members ile veri gizleme
- **Abstraction**: Shape ve ColorMatrix sınıfları
- **Dynamic Polymorphism**: Virtual functions ve operator overloading
- **Constructor/Destructor**: RAII prensibi ile kaynak yönetimi

### 2. Memory Management

```cpp
// Manuel bellek yönetimi
Shape::Shape(int** shape, int height, int width, Color color) {
    // 4 rotasyon için bellek tahsisi
    for (int i = 1; i < 4; i++) {
        shapes[i] = new int*[...];
        for (int j = 0; j < ...; j++)
            shapes[i][j] = new int[...];
    }
}

Shape::~Shape(void) {
    // Bellek sızıntısını önleme
    for (int i = 0; i < 1; i++) {
        for (int j = 0; j < heights[i]; j++)
            delete[] shapes[i][j];
        delete[] shapes[i];
    }
}
```

### 3. Collision Detection System

```cpp
bool is_collison(ColorMatrix &color_matrix, Shape &shape, int x, int y) {
    // Sınır kontrolü
    if (y + shape.getHeight() > 16 || shape.getWidth() + x > 12 || x < 0)
        return true;

    // Piksel bazlı çarpışma tespiti
    for (int i = 0; i < shape.getHeight(); i++)
        for (int j = 0; j < shape.getWidth(); j++)
            if (shape.get(i, j) && color_matrix.get(y + i, x + j))
                return true;
    return false;
}
```

### 4. Rotation Algorithm

```cpp
// 90° saat yönünde döndürme: matrix[i][j] → matrix[j][height-1-i]
for (int i = 0; i < width; i++)
    for (int j = 0; j < height; j++)
        shapes[1][i][j] = shape[height - 1 - j][i];

// 180° döndürme: matrix[i][j] → matrix[height-1-i][width-1-j]
for (int i = 0; i < height; i++)
    for (int j = 0; j < width; j++)
        shapes[2][i][j] = shape[height - 1 - i][width - 1 - j];

// 270° döndürme: matrix[i][j] → matrix[width-1-j][i]
for (int i = 0; i < width; i++)
    for (int j = 0; j < height; j++)
        shapes[3][i][j] = shape[j][width - 1 - i];
```

### 5. Scoring System

```cpp
void save(..., int &score, int &level, int &z, ...) {
    int count = 0;  // Temizlenen satır sayısı
    for (int i = 0; i < 16; i++) {
        if (/* satır dolu */) {
            count++;
            score += 100;
            level++;
            z -= 50;  // Hız artışı (düşüş süresi azalır)
        }
    }
    // Combo bonus
    if (count > 1)
        score += (count - 1) * 100;
}
```

### 6. Random Shape Generation

```cpp
// C++ random library ile modern rastgele sayı üretimi
random_device rd;                              // Seed
mt19937 gen(rd());                            // Mersenne Twister
uniform_int_distribution<int> distribution(0, shape_list.size - 1);
int random_shape = distribution(gen);
```

## 🎨 Grafik ve Ses Sistemi

### Raylib Entegrasyonu

```cpp
// Pencere başlatma
InitWindow(900, 800, "Mikail 211 Project");

// Texture loading
Image image = LoadImage("background.png");
Texture2D texture = LoadTextureFromImage(image);

// Audio system
InitAudioDevice();
Music music = LoadMusicStream("tetris.wav");
Sound blockSound = LoadSound("trung.wav");
PlayMusicStream(music);

// Drawing
BeginDrawing();
ClearBackground(RAYWHITE);
DrawTexture(texture, 0, 0, WHITE);
DrawRectangle(x, y, 50, 50, color);
DrawText("SCORE", 675, 478, 30, BLACK);
EndDrawing();
```

### Renk Paleti

26 farklı Raylib rengi desteklenir:

- MAROON, BLUE, GOLD, ORANGE, PURPLE
- DARKBLUE, PINK, GREEN, BROWN, LIME
- LIGHTGRAY, GRAY, DARKGRAY, YELLOW, RED
- DARKGREEN, SKYBLUE, VIOLET, DARKPURPLE, BEIGE
- DARKBROWN, WHITE, BLACK, MAGENTA, RAYWHITE, BLANK

## 📊 Performans ve Optimizasyonlar

### Frame Rate Management

```cpp
int a = 0;                    // Input throttling counter
int moving_loop = 0;          // Drop speed controller
while (WindowShouldClose() == false) {
    if (a == 80) {            // ~50ms input delay
        // Process input
        a = 0;
    }
    if (moving_loop >= z) {   // Dynamic drop speed
        // Drop shape
        moving_loop = 0;
    }
    a++;
    moving_loop++;
}
```

### Memory Efficiency

- **Pointer-based matrix**: Heap üzerinde esnek bellek kullanımı
- **Rotation caching**: 4 rotasyon pre-computed
- **Null checking**: Boş hücreler için minimal memory footprint

### Optimizasyon Teknikleri

1. **Spatial partitioning**: Sadece aktif bölge kontrol edilir
2. **Early termination**: Collision detection'da ilk çarpışmada dön
3. **Object pooling**: Shape nesneleri tekrar kullanılır
4. **Minimal redraws**: Sadece değişen bölgeler yeniden çizilir

## 🔒 Hata Yönetimi ve Güvenlik

### Boundary Checking

```cpp
// Array bounds validation
if (y + shape.getHeight() > 16 || shape.getWidth() + x > 12 || x < 0)
    return true;

// Null pointer checks
if (color_matrix.get(i, j))
    delete color_matrix.get(i, j);
```

### Memory Leak Prevention

```cpp
// Her allocation için matching deallocation
ColorMatrix::~ColorMatrix(void) {
    for (int i = 0; i < 16; i++) {
        for (int j = 0; j < 12; j++)
            if (colors[i][j])
                delete colors[i][j];  // Nested cleanup
        delete[] colors[i];
    }
    delete[] colors;
}
```

### Input Validation

```cpp
// Kullanıcı girişi doğrulama
while (true) {
    cin >> str;
    if (str == "yes" || str == "no")
        break;
    cout << "wrong input!!" << endl;
}
```

## 📋 Proje Yapısı

```
Yeditepe-University-Term-Projects/
├── tetris/
│   ├── tetris.cpp              # Ana oyun motoru (228 satır)
│   ├── Shape.hpp               # Shape sınıfı interface
│   ├── Shape.cpp               # Shape implementasyonu
│   ├── ShapeList.hpp           # ShapeList interface
│   ├── ShapeList.cpp           # 10 şekil tanımı ve yönetimi
│   ├── Color.hpp               # ColorMatrix interface
│   ├── Color.cpp               # Tahta yönetimi
│   ├── Piece.hpp               # Enum tanımları
│   ├── tetris.vcxproj          # Visual Studio proje dosyası
│   ├── tetris.vcxproj.filters  # VS dosya filtreleri
│   └── x64/Debug/              # Debug build çıktıları
├── tetris.sln                  # Visual Studio solution
├── background.png              # Oyun arkaplanı
├── gameover.png                # Game over ekranı
├── tetris.wav                  # Oyun müziği
├── game-over.wav               # Game over müziği
├── trung.wav                   # Blok ses efekti
└── README.md                   # Proje dokümantasyonu
```

## 🎓 Öğrenme Hedefleri

Bu proje şu konularda deneyim kazandırır:

1. **OOP Prensipleri**: Class design, encapsulation, inheritance
2. **Dynamic Memory**: Pointer arithmetic, new/delete operations
3. **Data Structures**: 2D/3D arrays, matrix operations
4. **Game Development**: Game loop, input handling, rendering
5. **Graphics Programming**: Raylib API, texture management
6. **Audio Integration**: Music streaming, sound effects
7. **Algorithm Design**: Collision detection, rotation algorithms
8. **Resource Management**: RAII, destructor chaining
9. **User Interaction**: Console input, keyboard handling
10. **Project Organization**: Multi-file C++ project structure

## 📝 Öğrenci Bilgileri

**Geliştirici:** Enes Mahmut ATEŞ
**Okul Numarası:** 20200702008
**E-posta:** enesmahmut.ates@std.yeditepe.edu.tr
**Ders:** CSE211 - Object Oriented Programming
**Üniversite:** Yeditepe Üniversitesi
**Proje Adı:** Mikail 211 Project (Tetris)
