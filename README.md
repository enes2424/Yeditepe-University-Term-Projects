# Yeditepe Üniversitesi Derleyici Tasarımı Projesi
## Python-Like Language Parser & Compiler (Python Benzeri Dil Ayrıştırıcı ve Derleyici)

Bu proje, Yeditepe Üniversitesi De### invalid4.txt - invalid8.txt
**Dosyalar:** `invalid6.txt` - `invalid8.txt`

Çeşitli hata senaryolarını test eden ek geçersiz girdi dosyaları.ici Tasarımı dersi kapsamında geliştirilmiş olan Lex/Yacc tabanlı bir parser ve derleyici projesidir. Python benzeri söz dizimi kullanan basit bir programlama dili için lexical analyzer (sözcüksel çözümleyici) ve syntax analyzer (söz dizimi çözümleyici) içerir.

## 📚 Proje Bileşenleri

### project.l - Lexical Analyzer (Sözcüksel Çözümleyici)
**Dosya:** `project.l`

Lex kullanılarak yazılmış lexical analyzer dosyası.
- **Token Tanımları:** Değişkenler, sayılar, operatörler, string literalleri
- **Desteklenen Tokenlar:**
  - `VAR`: Değişken isimleri (harf ile başlar, harf/rakam/underscore içerebilir)
  - `INT`: Tam sayı literalleri
  - `FLOAT`: Ondalık sayı literalleri  
  - `STRING`: String literalleri (çift tırnak içinde)
  - Operatörler: `+`, `-`, `*`, `/`, `=`
  - Karşılaştırma: `==`, `!=`, `<=`, `<`, `>=`, `>`
  - Kontrol yapıları: `if`, `elif`, `else:`
  - Whitespace: TAB, newline

### project.y - Syntax Analyzer (Söz Dizimi Çözümleyici)
**Dosya:** `project.y`

Yacc kullanılarak yazılmış syntax analyzer dosyası.
- **Grammar Kuralları:** Python benzeri söz dizimi kuralları
- **Özellikler:**
  - Indentation (girinti) kontrolü
  - Type checking (tip kontrolü)
  - Variable scope yönetimi
  - C++ kod üretimi
  - Error handling (hata yakalama)
- **Desteklenen Yapılar:**
  - Değişken atamaları
  - Aritmetik ifadeler
  - Koşullu ifadeler (if/elif/else)
  - Nested (iç içe) yapılar

### input.py - Örnek Test Dosyası
**Dosya:** `input.py`

Python benzeri söz dizimi kullanan örnek kod.
```python
a = 3
if a < 5:
    a = 3
    if a < 2:
        a = a * 2
        b = 4
    elif b > 4:
        if "enes" > "mikail":
            b = "eren"
a = 5
```

### valid1.txt - Geçerli Test Girdi 1
**Dosya:** `valid1.txt`

Basit değişken atama ve aritmetik işlem örneği.
```
x = "5"
y = 7
z = 3.14
x = y*z+10
```

### valid2.txt - Geçerli Test Girdi 2
**Dosya:** `valid2.txt`

Karmaşık if/elif/else yapısı ve nested koşullar içeren örnek.
```
x=5
y=7
z=3.14
if x<z:
    if y<z:
        result=z*x-y
        result=result/2
    else:
        result=z*x+y
        result=result/2
        if result>y:
            result=result/x
    y=x*2
elif y<x:
    result=z
else:
    result=z*x*x*y
x=y
```

### valid3.txt - Geçerli Test Girdi 3
**Dosya:** `valid3.txt`

Koşullu ifadeler ve negatif sayı işlemleri içeren örnek.
```
x=0
y=-1
if x>y:
    y=-1*y
elif y>x:
    x=-1*x
else:
    x=y
y=x
```

### invalid1.txt - Geçersiz Test Girdi 1
**Dosya:** `invalid1.txt`

Tab tutarsızlığı hatası örneği.
```
x=3
    y=5
```
**Hata:** Beklenmedik girinti seviyesi

### invalid2.txt - Geçersiz Test Girdi 2
**Dosya:** `invalid2.txt`

If bloğunda eksik girinti hatası.
```
x=0
if x==0:
y=x
```
**Hata:** If bloğu içinde gerekli girinti eksik

### invalid3.txt - Geçersiz Test Girdi 3
**Dosya:** `invalid3.txt`

Else bloğunda girinti tutarsızlığı.
```
x=1
if x!=0:
    y=x
else:
y=-1
```
**Hata:** Else bloğunda girinti tutarsızlığı

### invalid4.txt - Geçersiz Test Girdi 4
**Dosya:** `invalid4.txt`

Elif'in else'den sonra gelmesi hatası.
```
x=1
if x>=0:
    y=x
else:
    y=0
elif x<0:
    y=-1
x=y
```
**Hata:** Elif else'den sonra gelemez

### invalid5.txt - Geçersiz Test Girdi 5
**Dosya:** `invalid5.txt`

İç içe if yapısında girinti hatası.
```
x=1
y=0
if x<0:
if y>0:
    x=y
else:
    x=-1
y=2*x
```
**Hata:** İç içe if'te girinti eksik

### invalid6.txt - invalid8.txt
**Dosyalar:** `invalid6.txt` - `invalid8.txt`

Çeşitli hata senaryolarını test eden ek geçersiz girdi dosyaları.

### Makefile - Derleme Dosyası
**Dosya:** `Makefile`

Proje derleme otomasyonu.
```makefile
all: lex yacc
    @g++ lex.yy.c y.tab.c -ll -o project

yacc: project.y
    @yacc -d -v project.y

lex: project.l
    @lex project.l

clean: lex.yy.c y.tab.c project y.tab.h
    @rm lex.yy.c y.tab.c project y.tab.h y.output
```

## 🔧 Kurulum ve Kullanım

### Gereksinimler
```bash
# Ubuntu/Debian
sudo apt-get install flex bison build-essential

# CentOS/RHEL
sudo yum install flex bison gcc-c++

# macOS (Homebrew)
brew install flex bison
```

### Projeyi Derleme
```bash
# Tüm projeyi derle
make

# Sadece lexical analyzer
make lex

# Sadece parser
make yacc

# Temizlik
make clean
```

### Kullanım
```bash
# Geçerli test dosyalarını çalıştır
./project valid1.txt
./project valid2.txt
./project valid3.txt

# Hata kontrolü için geçersiz dosyaları test et
./project invalid1.txt
./project invalid2.txt
./project invalid3.txt

# Kendi test dosyanızı çalıştır
./project your_test_file.txt
```

### Örnek Çıktı
```bash
# valid1.txt çalıştırıldığında:
./project valid1.txt
```
```cpp
void main()
{
    string x_str;
    int y_int;
    float z_flt;
    
    x_str = "5";
    y_int = 7;
    z_flt = 3.14;
    x_str = y_int * z_flt + 10;
}
```

## 🎯 Proje Özellikleri

### Desteklenen Dil Yapıları

1. **Değişken Atamaları**: 
   - Integer: `x = 5`
   - Float: `y = 3.14`
   - String: `z = "hello"`

2. **Aritmetik İşlemler**:
   - Toplama: `+`
   - Çıkarma: `-`
   - Çarpma: `*`
   - Bölme: `/`

3. **Karşılaştırma Operatörleri**:
   - Eşitlik: `==`, `!=`
   - Büyüklük: `<`, `<=`, `>`, `>=`

4. **Kontrol Yapıları**:
   - Koşullu ifadeler: `if`, `elif`, `else:`
   - İç içe yapılar (nested structures)

5. **Girinti (Indentation) Kontrolü**:
   - Python benzeri tab/space kontrolü
   - Girinti tutarlılığı zorunluluğu

### Hata Kontrolü

1. **Syntax Hatları**: Geçersiz söz dizimi
2. **Type Mismatch**: Tip uyumsuzluğu hatları
3. **Indentation Errors**: Girinti hatları
4. **Undefined Variables**: Tanımlanmamış değişken kullanımı
5. **If/Else Consistency**: Koşullu yapı tutarlılığı

### Kod Üretimi

- **Target Language**: C++
- **Variable Naming**: Tip soneki ekleme (x_int, y_flt, z_str)
- **Scope Management**: Blok bazlı kapsam yönetimi
- **Memory Management**: Otomatik bellek yönetimi

## 📋 Test Senaryoları

### Geçerli Test Dosyaları
- `valid1.txt`: Basit değişken atamaları ve aritmetik
- `valid2.txt`: Karmaşık if/elif/else yapıları
- `valid3.txt`: Koşullu ifadeler ve negatif sayı işlemleri

### Geçersiz Test Dosyaları
- `invalid1.txt`: Tab tutarsızlığı
- `invalid2.txt`: If bloğu girinti hatası
- `invalid3.txt`: Else bloğu girinti hatası
- `invalid4.txt`: Elif'in else'den sonra gelmesi hatası
- `invalid5.txt`: İç içe if yapısında girinti hatası
- `invalid6.txt` - `invalid8.txt`: Çeşitli hata durumları

## 🔬 Teknik Detaylar

### Lexical Analysis (Sözcüksel Analiz)
- **Tool**: Flex/Lex
- **Pattern Matching**: Regular expressions
- **Token Generation**: Symbol table oluşturma

### Syntax Analysis (Söz Dizimi Analizi)
- **Tool**: Bison/Yacc
- **Grammar Type**: Context-free grammar
- **Parsing Method**: LALR(1) parser

### Error Recovery
- **Error Detection**: Çoklu hata kontrolü
- **Error Messages**: Satır numarası ile detaylı hata mesajları
- **Graceful Exit**: Hata durumunda güvenli çıkış

### Memory Management
- **String Handling**: Dynamic memory allocation
- **Variable Scope**: Stack-based scope management
- **Cleanup**: Otomatik bellek temizleme

## 📚 Öğrenilen Kavramlar

1. **Compiler Theory**: Derleyici tasarım prensipleri
2. **Lexical Analysis**: Token tanıma ve üretme
3. **Syntax Analysis**: Grammar kuralları ve parsing
4. **Error Handling**: Hata yakalama ve raporlama
5. **Code Generation**: Hedef dil kod üretimi
6. **Type Checking**: Tip kontrolü algoritmaları
7. **Symbol Table**: Sembol tablosu yönetimi
8. **Scope Management**: Kapsam yönetimi

## 🚀 Geliştirilecek Özellikler

- [ ] For/while döngü desteği
- [ ] Fonksiyon tanımlama ve çağırma
- [ ] Liste/array desteği
- [ ] Daha gelişmiş hata mesajları
- [ ] Optimizasyon seçenekleri
- [ ] Debugging desteği

## 📞 İletişim

Bu proje Yeditepe Üniversitesi Derleyici Tasarımı dersi kapsamında geliştirilmiştir.

---

**Not**: Bu README, proje dosyalarının analizi sonucu otomatik olarak oluşturulmuştur ve projenin mevcut durumunu yansıtmaktadır.
