# Yeditepe Üniversitesi İlk Dönem Projesi: UNO Oyunu
## Basit UNO Kart Oyunu Simülasyonu

> **Önemli Not:** Bu proje, Yeditepe Üniversitesi'ndeki ilk dönem projem olup hayatımda yazdığım ilk kodlardan birisidir. Kod yapısının ve mantığının bu kadar basic ve acemice olmasının (örneğin spagetti kodlar, global değişkenler ve tam oturmamış sınıf yapıları) temel sebebi budur. :)

Bu proje, Python kullanılarak terminal üzerinden oynanabilen basit bir UNO kart oyunu simülasyonudur. İki oyunculu yapıya sahip olan oyunda, kart dağıtımı, eşleştirme (renk veya sayı) ve oyun döngüsü gibi temel mekanikler yer almaktadır.

## 📚 Proje Bileşenleri

### Enes_ATEŞ_uno.py - Ana Oyun Dosyası
**Dosya:** `Enes_ATEŞ_uno.py`

Python kullanılarak yazılmış, oyunun tüm mantığını ve sınıflarını (class) barındıran temel dosya.
- **UnoCard Sınıfı:** Kartların renk ve sayı özelliklerini tutar. Kartların eşleşip eşleşemeyeceğini ([canPlay](cci:1://file:///c:/Users/Enes%20Mahmut%20Ate%C5%9F/Desktop/Yeditepe-University-Term-Projects/Enes_ATE%C5%9E_uno.py:8:4-12:24)) belirler.
- **CollectionOfUnoCards Sınıfı:** Deste ve oyuncu ellerini yönetir. Kart ekleme, çekme ve karıştırma işlemleri yapılır.
- **Uno Sınıfı:** Oyun motorunu oluşturur. Oyunculara kartları dağıtır, turları ([playTurn](cci:1://file:///c:/Users/Enes%20Mahmut%20Ate%C5%9F/Desktop/Yeditepe-University-Term-Projects/Enes_ATE%C5%9E_uno.py:63:4-98:67)) yönetir ve oyunun sonucunu hesaplar.
- **Ana Oyun Döngüsü:** Kullanıcıdan tur sayısı alınarak oyun başlatılır, global fonksiyonlar ve recursive (özyinelemeli) çağrılar kullanılarak turlar sırayla oynatılır.

## 🔧 Kurulum ve Kullanım

### Gereksinimler
Proje herhangi bir harici kütüphane gerektirmez. Sadece temel Python kütüphanesi olan `random` kullanılmaktadır.
- Python 3.x sürümünün kurulu olması yeterlidir.

### Projeyi Çalıştırma
Projeyi çalıştırmak için dosyanın bulunduğu dizinde terminali açarak şu komutu girebilirsiniz:
```bash
python Enes_ATEŞ_uno.py
