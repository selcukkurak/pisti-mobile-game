# Pişti - Geleneksel Türk Kart Oyunu

Modern Android cihazlar için geliştirilmiş profesyonel Pişti oyunu. Jetpack Compose ve Material Design 3 ile tasarlanmış, otantik Türk kart oyunu deneyimi sunar.

## 🎮 Oyun Özellikleri

### Geleneksel Pişti Kuralları ✅
- **4 Oyuncu Desteği**: 1 İnsan + 3 AI oyuncu
- **Kart Dağıtımı**: Her oyuncuya 4 kart, masaya 4 kart
- **Yakalama Sistemi**: Aynı değerdeki kartları eşleştir, Valeler her kartı yakalar
- **Pişti Bonusu**: Vale ile tek kart yakalamada +10 puan
- **Geleneksel Puanlama**: As=1, Sinek Valesi=2, Kupa Valesi=2, Karo 10=3, Trefel 2=2

### Yapay Zeka Seviyeleri 🤖
- **Kolay**: Temel kart eşleştirme
- **Orta**: Stratejik kart saklama
- **Zor**: Gelişmiş tahmin ve engelleme

### Modern Android Özellikleri 📱
- **Jetpack Compose**: Modern bildirimsel UI
- **Material Design 3**: Google'ın en son tasarım sistemi
- **MVVM Mimarisi**: Temiz, test edilebilir kod yapısı
- **Turkish Localization**: Tam Türkçe arayüz
- **Çevrimdışı Oyun**: İnternet bağlantısı gerektirmez

## 🏗️ Teknik Detaylar

### Proje Yapısı
```
app/src/main/java/com/pisti/game/
├── data/models/          # Veri modelleri (Card, Player, GameState)
├── game/engine/          # Oyun motoru ve kurallar  
├── game/ai/              # AI oyuncu stratejileri
├── ui/game/              # Oyun ekranı ve bileşenleri
├── ui/menu/              # Ana menü
└── ui/theme/             # Material Design 3 teması
```

### Gereksinimler
- **Platform**: Android
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34
- **Dil**: Kotlin
- **UI Framework**: Jetpack Compose

## 🧪 Test Edilen Özellikler

### Oyun Motoru Testleri ✅
```bash
# Temel testleri çalıştır
kotlinc -cp . -d /tmp/build app/src/main/java/com/pisti/game/data/models/*.kt \
  app/src/main/java/com/pisti/game/game/engine/*.kt \
  app/src/test/kotlin/PistiGameTest.kt

kotlin -cp /tmp/build com.pisti.game.test.PistiGameTestKt
```

**Test Sonuçları**:
- ✅ 52 kartlık deste oluşturma
- ✅ Oyun başlatma (4 kart/oyuncu, 4 kart masa)
- ✅ Kart yakalama mekaniği
- ✅ Pişti tespiti (Vale + tek kart)
- ✅ Puanlama sistemi

### AI Oyun Simülasyonu ✅
```bash
# Tam oyun simülasyonu
kotlinc -cp . -d /tmp/build app/src/main/java/com/pisti/game/data/models/*.kt \
  app/src/main/java/com/pisti/game/game/engine/*.kt \
  app/src/main/java/com/pisti/game/game/ai/*.kt \
  app/src/test/kotlin/PistiGameSimulation.kt

kotlin -cp /tmp/build com.pisti.game.test.PistiGameSimulationKt
```

**Simülasyon Özellikleri**:
- 4 oyunculu tam oyun (~48 hamle)
- Farklı AI zorluk seviyeleri
- Gerçek zamanlı skor takibi
- Detaylı oyun istatistikleri

## 🎯 Oyun Stratejileri

### Beginner AI (Kolay)
- Yakalama fırsatı varsa yakala
- Yoksa en düşük kartı oyna

### Intermediate AI (Orta)  
- Pişti fırsatlarını öncelikle
- Değerli kartları yakalamaya odaklan
- Güvenli kartları tercih et

### Expert AI (Zor)
- Oynan kartları hatırla
- Rakip Pişti'sini engelle
- Stratejik kart yerleştirme

## 📊 Puanlama Sistemi

| Kart | Puan |
|------|------|
| Her yakalanan kart | +1 |
| As | +1 (total +2) |
| Sinek Valesi | +2 (total +3) |
| Kupa Valesi | +2 (total +3) |
| Karo 10 | +3 (total +4) |
| Trefel 2 | +2 (total +3) |
| En çok kart | +3 bonus |
| Pişti (Vale yakalama) | +10 bonus |

## 🚀 Gelecek Özellikler

- **Çok Oyunculu Mod**: Firebase ile online oyun
- **İstatistikler**: Kazanma oranları ve başarılar
- **Ses Efektleri**: Geleneksel Türk oyun sesleri
- **Temalar**: Farklı kart arka tasarımları
- **AdMob Entegrasyonu**: Banner ve interstitial reklamlar

## 📝 Lisans

Bu proje MIT lisansı altında geliştirilmiştir. Kişisel ve ticari kullanım için serbesttir.

---

*Otantik Türk kart oyunu deneyimi için geliştirildi. 🇹🇷*