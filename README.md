# WasteCo

WasteCo adalah aplikasi Android untuk membantu rumah tangga memanfaatkan bahan pangan yang masih layak pakai. Pengguna bisa mencatat bahan di pantry, mencari ide masakan dari bahan yang tersedia, dan berbagi bahan dengan tetangga. Aplikasi ini dikembangkan sebagai salah satu upaya mengurangi tingkat pemborosan makanan di Indonesia yang semakin mengkhawatirkan.

## Fitur utama

| Fitur | Alur di aplikasi |
| --- | --- |
| Pantry dan deteksi foto | Tambahkan bahan secara manual atau ambil foto dari kamera maupun galeri. Periksa dan koreksi hasil deteksi sebelum menyimpannya ke pantry. |
| Rekomendasi resep | Cari resep dari bahan yang tersedia di pantry, lihat detail resep, lalu konfirmasi masakan agar backend mencatat pemakaian bahan. |
| Donasi bahan | Buat permintaan dengan titik temu dan jadwal penerimaan. Temukan permintaan di daftar atau peta dalam radius **3 km**. Pendonor memilih bahan dan berkoordinasi lewat WhatsApp. Peminta mengonfirmasi setelah bahan diterima. |
| Akun dan profil | Daftar, masuk, serta perbarui nama, nomor WhatsApp, dan area. Pengguna dapat memakai GPS untuk membantu mengisi area. |

## Menjalankan proyek

### Persyaratan

- Android Studio dan Android SDK Platform **API 37** untuk build.
- JDK **25** untuk Gradle daemon. Repo memakai Gradle Wrapper **9.5.0** dan dikonfigurasi untuk menyediakan JDK yang sesuai. [Konfigurasi JDK](gradle/gradle-daemon-jvm.properties) · [Gradle Wrapper](gradle/wrapper/gradle-wrapper.properties)
- Emulator atau perangkat fisik dengan **Android 13 (API 33)** atau lebih baru. [Konfigurasi modul Android](app/build.gradle.kts)
- Koneksi internet dan API key WasteCo untuk mengakses fitur yang terhubung ke backend.

### Konfigurasi lokal

1. Buka folder root repo di Android Studio. Pastikan Android SDK Platform API 37 terpasang melalui **SDK Manager**.
2. Tambahkan properti berikut ke file `local.properties` di root. Gunakan API key yang diberikan pengelola backend dan pertahankan baris `sdk.dir` jika Android Studio sudah mengisinya.

   ```properties
   wasteco.apiKey=GANTI_DENGAN_API_KEY_WASTECO
   ```

3. Jalankan **Sync Project with Gradle Files**. Pilih konfigurasi **app**, pilih emulator atau perangkat yang sudah terhubung, lalu klik **Run**.

`local.properties` diabaikan oleh Git. Simpan API key di file lokal tersebut. [Aturan Git](.gitignore)

### Build dan tes

Jalankan dari root repo dengan Gradle Wrapper.

```bash
./gradlew :app:compileDebugKotlin
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

Di Windows, gunakan `gradlew.bat`. APK debug berada di `app/build/outputs/apk/debug/app-debug.apk`.

Untuk memastikan integrasi berjalan di perangkat, coba simpan hasil deteksi foto dan buat rekomendasi dari pantry. Uji juga satu permintaan donasi menggunakan dua akun untuk memeriksa alur dari pembuatan permintaan sampai konfirmasi penerimaan.

## Integrasi backend

Base URL WasteCo adalah `https://wastecobe.mhqif.my.id/`. Aplikasi mengirim `x-api-key` dari konfigurasi lokal dan token `Authorization: Bearer …` untuk request yang memerlukan sesi. [Konfigurasi modul Android](app/build.gradle.kts) · [Dokumentasi API WasteCo di Apidog](https://udbgxui5c2.apidog.io/)

## Teknologi dan struktur kode

WasteCo memakai **Kotlin**, **Jetpack Compose** dan **Material 3**, serta **Navigation Compose** dengan rute bertipe. Arsitekturnya menggunakan **MVVM** dan aliran data searah. ViewModel mengelola state dengan **Coroutines** dan **StateFlow**, sedangkan repository mengakses backend melalui **Retrofit**, **OkHttp**, dan **Kotlinx Serialization**. **DataStore Preferences** menyimpan sesi, dan **AppContainer** menyediakan dependensi. [Version catalog](gradle/libs.versions.toml)

```text
app/src/main/java/com/example/wasteco/
├── data/     # API, DTO, mapper, repository, penyimpanan sesi
├── di/       # AppContainer
├── domain/   # Model aplikasi
└── ui/       # Layar Compose, ViewModel, state, navigasi, dan tema
app/src/test/ # Tes unit
```
