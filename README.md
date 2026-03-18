# DELLIE'S Kargo ve Teslimat Yönetim Sistemi
## Client-Server Mimari ve Genetik Algoritma Destekli Rota Optimizasyonu

Bu proje, Python kullanılarak geliştirilmiş kapsamlı bir kargo/teslimat yönetim sistemidir. İstemci-Sunucu (Client-Server) mimarisi üzerinde TCP soketleri ile haberleşen sistem; veritabanı yönetimi, grafiksel kullanıcı arayüzü (GUI) ve teslimat rotalarının optimizasyonu için Genetik Algoritma (Genetic Algorithm - TSP) çözümleri içerir.

## 📚 Proje Bileşenleri

### İstemci (Client) Modülü
**Dizin:** `Client/`
- **`client.py`:** Tkinter kullanılarak geliştirilmiş masaüstü uygulamasıdır. Kullanıcıların (Müşteri, Sürücü, Operatör) sisteme giriş ve kayıt işlemlerini, sipariş yönetimini ve arayüz etkileşimlerini yönetir.
- **`geneticAlgorithm/`:** Siparişlerin teslimat noktalarına göre en kısa ve maliyetsiz rotayı hesaplamak için kullanılan genetik algoritma motorunu barındırır. Sürücülerin "Find Route" (Rota Bul) isteklerini işler.

### Sunucu (Server) Modülü
**Dizin:** `Server/`
- **`server.py`:** Çoklu istemci (multithreaded) destekli TCP sunucu yazılımıdır. Soket bağlantılarını dinler, istemciden gelen istekleri (giriş, durum güncelleme, siparişler) SQLite veritabanı ile eşzamanlı olarak işler ve sonuçları döndürür.
- **Veritabanları (`orders.db` & `sign.db`):** Kullanıcı hesap bilgilerinin ve kargo sipariş durumlarının (Queued, Dispatched, Arrived vb.) güvenli bir şekilde saklandığı yerel veritabanlarıdır.

## 🔧 Kurulum ve Kullanım

### Gereksinimler
Proje temel Python (built-in) modülleri (socket, threading, sqlite3, tkinter) üzerine inşa edilmiştir. Standart bir Python 3.x kurulumu yeterlidir.

### Projeyi Çalıştırma
Sistemin çalışabilmesi için önce sunucunun, ardından istemcinin başlatılması gerekmektedir:

```bash
# 1. Sunucuyu Başlatın
cd Server
python server.py

# 2. İstemciyi Başlatın (Farklı bir terminal penceresinde)
cd Client
python client.py
```

## 🎯 Proje Özellikleri

1. **Çoklu Kullanıcı Rolleri**:
   - **Müşteriler:** Kargo kodu ile paket durumu sorgulama, aktif ve geçmiş siparişleri listeleme, puan sistemi takibi.
   - **Sürücüler (Drivers):** Atanan siparişleri görme, sipariş durumunu güncelleme, rotasyon/takvim değiştirme (Swap Schedule) ve teslimat rotası optimizasyonu (Find Route).
   - **Operatörler:** İdari yetkiler, yorum ve sohbet kontrolleri.

2. **Kargo Durum Takibi**:
   - `Queued_for_Delivery`, `Dispatched`, `Arrived`, `Stored`, `Delivered` durumları üzerinden anlık kargo takip imkanı.

3. **Genetik Algoritma Rota Motoru**:
   - Gezgin Satıcı Problemi (TSP) mantığıyla dağıtım noktaları arasındaki en kısa mesafeyi genetik operasyonlar (mutasyon, çaprazlama) ile hesaplayarak sürücülere zaman ve yakıt tasarrufu sağlar.

4. **Gerçek Zamanlı İstemci-Sunucu İletişimi**:
   - Her istemci Thread'ler ile sunucuya asenkron bağlanır. İşlem sırasında "Race Condition" oluşmaması için Mutex (Lock) yapıları kullanılmıştır.

## 🔬 Teknik Detaylar

- **Haberleşme İskeleti:** `AF_INET`, `SOCK_STREAM` (TCP) Soket tabanlı networking.
- **Veritabanı Yönetimi:** `sqlite3` ile ilişkisel veritabanı. İletişim sırasında eşzamanlı veri yazılmasını önlemek için Threading Lock senkronizasyonu.
- **Arayüz Geliştirme:** Modüler ve nesne tabanlı `Tkinter` tasarımı (User, Customer ve Driver sınıflarının kalıtımları).
- **Yapay Zeka / Optimizasyon:** Genetik algoritmalarla rota hesabı.

## 📚 Öğrenilen Kavramlar

1. **Client-Server Architecture:** İstemci ve sunucu haberleşmesinin soket programlama ile sağlanması.
2. **Concurrency & Threading:** Çoklu işlemlerin aynı anda kilitlenme olmadan yönetilmesi.
3. **Database Interactions:** SQL sorguları ile veri yönetimi.
4. **Genetic Algorithms:** Sezgisel algoritmalar ile NP-Hard optimizasyon problemlerinin çözümü.

## 🚀 Geliştirilecek Özellikler

- [ ] Verilerin şifrelenmesi (Password hashing vb.).
- [ ] Daha gelişmiş masaüstü frameworkleri (PyQt vb.) ile arayüzün iyileştirilmesi.
- [ ] Harita üzerinde gerçek zamanlı GPS tabanlı rota koordinatlarının gösterilmesi.

## 📞 İletişim
Bu proje Yeditepe Üniversitesi kapsamında geliştirilmiş kapsamlı bir sunucu-istemci yazılım çalışmasıdır.
