# TrueLoss — tm.true.loss
Kotlin + Jetpack Compose, Clean Architecture, disiplinli multi-module. Material 3 Expressive mavimsi tema.

## Modül Yapısı
```
app/                      -> MainActivity, NavHost, Hilt App (tek composition root)
core/
  common/                 -> Result<T>, DispatcherProvider, FlowExt
  domain/                 -> UseCase base, TraceModel/HopModel
  ui/                     -> BaseViewModel<State,Event,Effect>
  designsystem/           -> TrueLossTheme (Color.kt/Shape.kt/Type.kt - mavimsi M3)
  navigation/             -> Route (type-safe @Serializable)
  network/                -> Retrofit + OkHttp + Json
  database/               -> Room TrueLossDatabase
  datastore/              -> PreferencesManager (DataStore)
  data/                   -> shared data aggregator
feature/
  trace/{domain,data,ui}      -> ICMP/UDP/TCP, IPv4/IPv6, ASN, ülke/şehir, canlı Loss%, ping grafiği + gauge
  history/{domain,data,ui}    -> geçmiş testler
  result/{domain,data,ui}     -> görsel paylaşma
  settings/{domain,data,ui}   -> tema, protokol seçimi
build-logic/convention/   -> Convention Plugins (tm.trueloss.android.*)
```

## Özellikler
- Domain/IP giriş, gerçek ICMP/UDP/TCP traceroute, IPv4/IPv6
- Her hop loss yüzdesi, RTT sparkline, ASN/ülke/şehir
- Profesyonel mavimsi M3 Expressive tema, gauge ve grafik

## Bağımlılık Kuralları (derleyici zorlar)
- `feature:ui` -> `feature:domain` + `core:ui/designsystem/navigation` (asla data görmez)
- `feature:data` -> `feature:domain` + `core:data/network/database`
- `core:*` -> asla `feature:*` bağımlı olamaz
- `app` -> tüm `feature:ui` + `feature:data` yı birleştiren tek yer

## Build
```bash
./gradlew assembleDebug   # APK: app/build/outputs/apk/debug/app-debug.apk -> TrueLoss.apk (armv7)
```

Paket: `tm.true.loss` | App adı: `TrueLoss` | MinSdk 26 | armv7
