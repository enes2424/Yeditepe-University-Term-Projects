# CSE471 Term Project - P2P Video Streaming Application
## Distributed Systems and Network Programming (Dağıtık Sistemler ve Ağ Programlama)

Bu proje, Yeditepe Üniversitesi CSE471 (Computer Networks) dersi için geliştirilmiş Java tabanlı, **Peer-to-Peer (P2P) Video Streaming** uygulamasıdır. Swing tabanlı bir arayüz ve VLC medya oynatıcı entegrasyonu (vlcj) kullanılarak, ağ üzerindeki diğer peer'lar ile gerçek zamanlı video keşfi ve akışı (streaming) yapılmasına olanak tanır.

## 🎥 Temel Özellikler

- **P2P Video Discovery**: UDP broadcast ile ağdaki aktif video dosyalarını otomatik keşfetme.
- **Real-time Streaming**: Chunk-based transfer (256KB parçalar) ile indirme devam ederken video oynatma.
- **VLC Integration**: `vlcj` kütüphanesi ile yüksek performanslı video oynatma desteği.
- **Dynamic Buffering**: Ağ gecikmesi ve paket kaybına göre dinamik olarak ayarlanan buffer mekanizması.
- **Advanced Filtering**: Klasör bazlı hariç tutma ve dosya maskeleme (wildcard) sistemleri.
- **Search System**: Ağ genelinde video dosyalarını ada göre filtreleme ve arama.
- **Event Logging**: Bağlantı ve transfer süreçlerini anlık takip eden olay günlüğü.

## 📚 Modüler Mimari

### P2P.java - Ana Uygulama Sınıfı
Uygulamanın ana giriş noktası ve GUI koordinatörüdür.
- Root Video Folder ve Buffer Folder yönetimi.
- Arama ve "Active Streams" tablosu üzerinden indirme/oynatma kontrolü.
- `FoundFilesManager` ile ağdaki dosyaların senkronizasyonu.

### VideoPlayer.java - Medya Oynatıcı
VLC medya oynatıcısını uygulama içine gömen ve streaming akışını yöneten sınıftır.
- `EmbeddedMediaPlayerComponent` kullanımı.
- Yeni chunk geldikçe oynatmayı sürdürme (`onNewDataAvailable`).
- Dosya buffering durumuna göre otomatik duraklatma ve devam etme.

### SocketOperation.java - Ağ Orkestrasyonu
UDP ve TCP operasyonlarını koordine eden merkezi sınıftır.
- `NetworkCommunicator`: UDP broadcast ve TCP unicast soket işlemleri.
- `FileDownloader`: Chunk-based paralel indirme yönetimi.
- `FileUploader`: Diğer peer'lara veri gönderimi.

### DynamicBuffer.java - Akıllı Buffer Yönetimi
Ağ koşullarına göre buffer boyutunu optimize eden sınıftır.
- Paket kaybı ve gecikme (latency) takibi.
- Koşullara göre 4KB ile 128KB arasında dinamik buffer optimizasyonu.

## 🔧 Kurulum ve Çalıştırma

### Gereksinimler
- **Java 17+**: Uygulamanın çalışması için gereklidir.
- **VLC Media Player**: `vlcj` entegrasyonu için sisteminizde VLC kurulu olmalıdır.
- **Kütüphaneler**: `lib/gson-2.8.9.jar` ve VLC kütüphaneleri.

### Normal Çalıştırma
```bash
# Kaynak kodları derleme
make normal

# Manual derleme ve çalıştırma
javac -cp lib/gson-2.8.9.jar:src -d class src/P2P.java
java -cp lib/gson-2.8.9.jar:class -Djava.net.preferIPv4Stack=true P2P
```

### Docker ile Çalıştırma
Uygulama, X11 yönlendirmesi ile izole container ortamında çalıştırılabilir:
```bash
# Single peer test
make docker

# Multi-peer simulation (3 peer)
make docker-compose
```

## 🌐 P2P Protokol Detayları

### UDP Broadcast (Port 9090)
Uygulama, peer keşfi ve durum kontrolü için aşağıdaki mesajları kullanır:
- `FOUND`: Paylaşılan video listesini duyurur.
- `CONTROL`: Dosya parça ve hash kontrolü yapar.
- `CONFIRM`: Transfer onay mekanizması.
- `REQUEST`: Chunk isteği bildirimi.

### TCP Unicast (Port 8080)
Gerçek veri transferi TCP üzerinden 256KB'lık parçalar (chunks) halinde gerçekleşir.

## 📋 Proje Yapısı

```
CSE471-Term_Project/
├── src/                    # Java kaynak kodları
│   ├── P2P.java           # GUI Koordinatörü
│   ├── VideoPlayer.java   # VLC Player Entegrasyonu
│   ├── SocketOperation.java # Network Yönetimi
│   ├── FileDownloader.java # İndirme Mantığı
│   ├── FileUploader.java   # Gönderim Mantığı
│   ├── DynamicBuffer.java  # Buffer Optimizasyonu
│   └── ... (Diğer yardımcı sınıflar)
├── videos/                 # Örnek video dosyaları (isteğe bağlı)
├── lib/                    # Gson ve bağımlılıklar
├── Dockerfile             # Konteyner tanımı
├── docker-compose.yml     # Çoklu peer simülasyonu
└── Makefile              # Build otomasyonu
```

## 📝 Geliştirici Bilgileri

**Geliştirici:** Oguzhan CAKAN  
**Okul Numarası:** 20210702128  
**E-posta:** oguzhan.cakan@std.yeditepe.edu.tr  
**Ders:** CSE471 - Computer Networks  
**Üniversite:** Yeditepe Üniversitesi
