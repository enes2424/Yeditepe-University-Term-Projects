# CSE471 Term Project - P2P File Sharing Application
## Distributed Systems and Network Programming (Dağıtık Sistemler ve Ağ Programlama)

Bu proje, Yeditepe Üniversitesi CSE471 (Computer Networks) dersi için geliştirilmiş Java tabanlı Peer-to-Peer (P2P) dosya paylaşım uygulamasıdır. Socket programlama, multi-threading, GUI tasarımı ve dağıtık sistemler konularında pratik deneyim kazandırmak için tasarlanmıştır.

## 📚 Modüller

### P2P.java - Ana Uygulama Sınıfı
**Dosya:** `src/P2P.java`

Ana P2P uygulamasının GUI ve koordinasyon mantığını içeren sınıf.
- **Sınıf:** `P2P extends JFrame`
- **Açıklama:** Swing tabanlı GUI, kullanıcı etkileşimleri ve peer koordinasyonu
- **Özellikler:**
  - Paylaşılan klasör seçimi ve yönetimi
  - İndirme klasörü belirleme
  - Klasör ve dosya filtreleme sistemi
  - Gerçek zamanlı dosya keşfi ve indirme
  - Bağlantı durumu göstergesi
- **Örnek:**
  ```java
  // Uygulamayı başlatma
  java -cp lib/gson-2.8.9.jar:class P2P peer1
  ```

### SocketOperation.java - Ağ İletişim Modülü
**Dosya:** `src/SocketOperation.java`

UDP ve TCP socket operasyonlarını yöneten temel ağ sınıfı.
- **Sınıf:** `SocketOperation`
- **Açıklama:** P2P ağ protokolü ve dosya transfer işlemleri
- **Protokoller:**
  - UDP broadcast (port 9090) - peer keşfi
  - TCP unicast (port 8080) - dosya transferi
- **Özellikler:**
  ```java
  // Ana metodlar
  public void connect()           // Ağa bağlan
  public void disconnect()        // Ağdan ayrıl
  public void download()          // Dosya indir
  private void shareFolder()      // Klasör paylaş
  ```

### FolderOperation.java - Dosya Sistemi Yönetimi
**Dosya:** `src/FolderOperation.java`

Dosya sistemi operasyonları ve dosya filtreleme işlemleri.
- **Sınıf:** `FolderOperation`
- **Açıklama:** Recursive dosya tarama, hash hesaplama ve filtreleme
- **Algoritmalar:**
  - SHA-256 hash hesaplama
  - Wildcard pattern matching
  - Recursive klasör tarama
- **Örnek:**
  ```java
  // Dosya hash hesaplama
  private static String getFileHash(byte[] fileBytes)
  
  // Pattern matching
  public static boolean isMatchedFileAndMask(String fileName, String mask)
  ```

### FolderSelection.java - Klasör Seçim Arayüzü
**Dosya:** `src/FolderSelection.java`

Kullanıcının hariç tutulacak klasörleri seçmesi için dialog penceresi.
- **Sınıf:** `FolderSelection extends JFrame`
- **Açıklama:** Modal dialog ile klasör seçimi yapan yardımcı GUI
- **Özellikler:**
  - Hierarchical klasör görünümü
  - Çoklu seçim desteği
  - Dinamik liste güncelleme

## 🔧 Kurulum ve Çalıştırma

### Gereksinimler
```bash
# Java 17+ kurulumu
sudo apt update && sudo apt install -y openjdk-17-jdk

# Gson kütüphanesi (proje ile birlikte gelir)
lib/gson-2.8.9.jar
```

### Normal Çalıştırma
```bash
# Kaynak kodları derleme
make normal

# Manual derleme
javac -cp lib/gson-2.8.9.jar:src -d class src/P2P.java
java -cp lib/gson-2.8.9.jar:class -Djava.net.preferIPv4Stack=true P2P
```

### Docker ile Çalıştırma
```bash
# Tek peer çalıştırma
make docker

# Çoklu peer çalıştırma
make docker-compose

# Temizlik
make clean-docker
make clean-all-docker
```

## 🐳 Docker Yapılandırması

### Dockerfile
```dockerfile
FROM ubuntu:22.04
RUN apt update && apt install -y openjdk-17-jdk
WORKDIR /CSE471TermProject
COPY src src
COPY images images
COPY lib lib
ENTRYPOINT ["./entrypoint.sh"]
```

### Docker Compose
```yaml
# 3 peer'li test ortamı
services:
  peer1, peer2, peer3:
    image: p2p
    environment:
      - DISPLAY=:0
      - APPNAME=peer1
    networks:
      - cse471
```

## 🌐 P2P Protokol Detayları

### UDP Broadcast Mesajları
- **FOUND**: Paylaşılan dosyaları duyur
- **CONTROL**: Dosya parça kontrolü
- **DISCONNECT**: Ağdan ayrılma bildirimi

### TCP Dosya Transferi
- **REQUEST**: Dosya parçası istemi
- **Chunk-based transfer**: 256KB parçalar halinde
- **Integrity check**: SHA-256 hash doğrulama

### Mesaj Formatları
```java
// Dosya duyuru mesajı
"FOUND " + dosyaListesi + ";" + boyutListesi + ";" + jsonMetadata

// Dosya parça istemi
"REQUEST " + offset + ";" + chunkID

// Durum kontrolü
"CONTROL " + indirilenByte + ";" + dosyaID + ";" + fileInfo
```

## 🎯 Teknik Özellikler

1. **Multi-threading**: ExecutorService ile eşzamanlı işlemler
2. **Network Discovery**: UDP broadcast ile otomatik peer keşfi
3. **File Integrity**: SHA-256 hash ile dosya bütünlüğü kontrolü
4. **Chunk-based Transfer**: Büyük dosyalar için parçalı transfer
5. **GUI Programming**: Swing ile responsive kullanıcı arayüzü
6. **JSON Serialization**: Gson ile structured data exchange
7. **Docker Containerization**: Isolated test environment
8. **Wildcard Filtering**: Flexible file/folder exclusion system

## � Performans ve Sınırlamalar

### Optimizasyonlar
- **ReentrantLock**: Thread-safe operations
- **Byte Array Caching**: Memory efficient file handling
- **Sorted Hash Lists**: Faster file comparison
- **Connection Pooling**: Efficient resource management

### Sınırlamalar
- **Network Scope**: Aynı broadcast domain'i gereksinimi
- **Memory Usage**: Büyük dosyalar için RAM kullanımı
- **Concurrent Downloads**: Aynı anda maksimum transfer sayısı
- **Error Recovery**: Network kesintilerinde retry mekanizması

## 🔒 Güvenlik Özellikleri

- **File Validation**: SHA-256 hash verification
- **Network Isolation**: Docker network segmentation
- **Input Sanitization**: Path traversal protection
- **Resource Limits**: Memory ve connection limiting

## 📋 Proje Yapısı

```
CSE471-Term_Project/
├── src/                    # Java kaynak kodları
│   ├── P2P.java           # Ana uygulama
│   ├── SocketOperation.java # Ağ katmanı
│   ├── FolderOperation.java # Dosya yönetimi
│   └── FolderSelection.java # GUI yardımcısı
├── images/                 # GUI görselleri
├── lib/                    # External kütüphaneler
├── bin/                    # Derlenmiş sınıflar
├── Dockerfile             # Container tanımı
├── docker-compose.yml     # Multi-peer setup
├── Makefile              # Build automation
└── entrypoint.sh         # Container başlatma
```

## 📝 Öğrenci Bilgileri

**Geliştirici:** Enes Mahmut ATEŞ  
**Okul Numarası:** 20200702008  
**E-posta:** enesmahmut.ates@std.yeditepe.edu.tr  
**Ders:** CSE471 - Computer Networks  
**Üniversite:** Yeditepe Üniversitesi
