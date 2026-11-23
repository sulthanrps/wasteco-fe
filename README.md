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

## CI/CD dan rilis APK

Dua workflow GitHub Actions ada di `.github/workflows/`:

| Workflow | Pemicu | Yang dilakukan |
| --- | --- | --- |
| [CI](.github/workflows/ci.yml) | Push ke `main` dan setiap pull request | Kompilasi, tes unit, build APK debug, lalu unggah APK sebagai artifact selama 14 hari. |
| [Release APK](.github/workflows/release.yml) | Push tag `vMAJOR.MINOR.PATCH`, atau dijalankan manual dengan tag yang sudah ada | Build APK rilis bertanda tangan, verifikasi tanda tangan, lalu terbitkan GitHub Release berisi `wasteco.apk` dan `wasteco.apk.sha256`. |

Nama aset selalu `wasteco.apk`, sehingga tautan berikut selalu mengarah ke versi terbaru dan dipakai oleh landing page:

```text
https://github.com/sulthanrps/wasteco-fe/releases/latest/download/wasteco.apk
```

### Menyiapkan secret (sekali saja)

1. Buat keystore rilis. Simpan file dan password-nya di tempat aman. Jika keystore hilang, pengguna harus uninstall aplikasi sebelum bisa memasang versi berikutnya.

   ```bash
   keytool -genkeypair -v -keystore wasteco-release.jks -alias wasteco \
     -keyalg RSA -keysize 4096 -validity 10000
   base64 -w0 wasteco-release.jks > wasteco-release.jks.b64
   ```

2. Di **Settings › Secrets and variables › Actions**, tambahkan secret berikut:

   | Secret | Isi |
   | --- | --- |
   | `WASTECO_API_KEY` | API key backend WasteCo |
   | `WASTECO_KEYSTORE_BASE64` | Isi `wasteco-release.jks.b64` |
   | `WASTECO_KEYSTORE_PASSWORD` | Password keystore |
   | `WASTECO_KEY_ALIAS` | Alias key, mis. `wasteco` |
   | `WASTECO_KEY_PASSWORD` | Password key |

3. Opsional: agar landing page langsung build ulang setelah rilis, isi variable `LANDING_REPO` (mis. `sulthanrps/wasteco-landing`) dan secret `LANDING_DISPATCH_TOKEN` berisi fine-grained token dengan izin **Contents: Read and write** di repo landing page.

### Membuat rilis

```bash
git tag v1.0.0
git push origin v1.0.0
```

`versionName` diambil dari tag (`1.0.0`) dan `versionCode` dihitung sebagai `major × 10000 + minor × 100 + patch` (`10000`). Minor dan patch harus di bawah 100. Catatan rilis dibuat otomatis dari commit dan pull request sejak tag sebelumnya.

Untuk build rilis bertanda tangan di komputer sendiri, isi properti berikut di `local.properties` (path relatif terhadap root repo):

```properties
wasteco.signing.storeFile=wasteco-release.jks
wasteco.signing.storePassword=...
wasteco.signing.keyAlias=wasteco
wasteco.signing.keyPassword=...
```

Tanpa properti tersebut, `assembleRelease` tetap berjalan tetapi menghasilkan APK yang belum ditandatangani.

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
