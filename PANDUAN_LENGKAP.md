# 📱 PANDUAN LENGKAP: Aplikasi Rekod Tugasan Juruteknik Android

## 🎯 APA YANG ANDA DAPAT

Aplikasi Android untuk merekod tugasan juruteknik komputer dengan:
- ✅ Rekod maklumat pelanggan & lokasi
- ✅ Ambil gambar dokumentasi guna kamera telefon
- ✅ Simpan dalam database SQLite (offline)
- ✅ Senarai semua tugasan
- ✅ Lihat butiran lengkap setiap tugasan
- ✅ Padam tugasan yang tidak diperlukan

---

## 📋 CARA BINA APLIKASI INI

### Pilihan 1: GUNA ANDROID STUDIO (Disyorkan)

#### 1️⃣ MUAT TURUN ANDROID STUDIO
- Pergi ke: https://developer.android.com/studio
- Muat turun dan install Android Studio
- Buka Android Studio

#### 2️⃣ BUAT PROJECT BARU
1. Klik "New Project"
2. Pilih "Empty Activity"
3. Klik "Next"
4. Isi maklumat:
   - **Name**: Rekod Tugasan Juruteknik
   - **Package name**: com.teknik.rekodtugasan
   - **Save location**: Pilih folder anda
   - **Language**: Java
   - **Minimum SDK**: API 21 (Android 5.0)
5. Klik "Finish"

#### 3️⃣ COPY SEMUA FILE
Sekarang copy file-file yang telah saya buat:

**📁 Java Files** (dalam folder `app/src/main/java/com/teknik/rekodtugasan/`):
- `MainActivity.java`
- `Task.java`
- `DatabaseHelper.java`

**📁 Layout Files** (dalam folder `app/src/main/res/layout/`):
- `activity_main.xml`
- `dialog_add_task.xml`
- `dialog_task_details.xml`
- `item_task.xml`
- `item_photo.xml`

**📁 Drawable Files** (dalam folder `app/src/main/res/drawable/`):
- `button_primary.xml`
- `button_secondary.xml`
- `button_outline.xml`
- `button_outline_red.xml`
- `edittext_background.xml`
- `spinner_background.xml`
- `gradient_header.xml`

**📁 Values Files** (dalam folder `app/src/main/res/values/`):
- `strings.xml`

**📁 Root Files**:
- `AndroidManifest.xml` (dalam folder `app/src/main/`)
- `build.gradle` (dalam folder `app/`)

#### 4️⃣ BUAT ICON TAMBAHAN

Dalam folder `app/src/main/res/drawable/`, buat file `ic_add.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M19,13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
</vector>
```

#### 5️⃣ UPDATE build.gradle (Project Level)

Buka `build.gradle` (Project level) dan pastikan ada:

```gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.4.2'
    }
}
```

#### 6️⃣ SYNC PROJECT
1. Klik "Sync Now" di bahagian atas (jika muncul)
2. Atau klik: File → Sync Project with Gradle Files

#### 7️⃣ RUN APLIKASI
1. Sambung telefon Android anda dengan USB (enable USB Debugging)
2. Atau guna Android Emulator
3. Klik butang hijau ▶️ "Run"
4. Pilih device anda
5. Aplikasi akan install dan buka automatik!

---

### Pilihan 2: GUNA SKETCHWARE (Untuk Pemula)

Sketchware adalah app untuk buat Android app terus dari telefon, TAPI ia agak terhad. Untuk app kompleks macam ni, Android Studio lebih sesuai.

---

## 🔧 CARA GUNA APLIKASI

### 1️⃣ TAMBAH TUGASAN BARU
1. Tekan butang ➕ (biru) di bawah kanan
2. Isi maklumat:
   - Nama pelanggan
   - Lokasi
   - Jenis tugasan (pilih dari dropdown)
   - Keterangan
   - Status
3. Tekan "📸 Ambil Gambar" untuk dokumentasi
4. Kamera akan buka - ambil gambar
5. Boleh ambil banyak gambar
6. Tambah nota jika perlu
7. Tekan "💾 Simpan"

### 2️⃣ LIHAT SENARAI TUGASAN
- Semua tugasan akan dipaparkan dalam senarai
- Setiap kad tugasan tunjuk:
  - Nama pelanggan
  - Jenis kerja
  - Lokasi
  - Tarikh & masa
  - Status (dengan warna berbeza)
  - Bilangan gambar

### 3️⃣ LIHAT BUTIRAN PENUH
1. Tekan butang "Lihat" pada mana-mana tugasan
2. Akan tunjuk semua maklumat lengkap
3. Boleh lihat semua gambar yang diambil

### 4️⃣ PADAM TUGASAN
1. Tekan butang "🗑️ Padam"
2. Confirm padam
3. Tugasan akan dibuang dari database

---

## 🔐 PERMISSIONS YANG DIPERLUKAN

Aplikasi ini perlukan permission:
- **CAMERA** - untuk ambil gambar
- **STORAGE** - untuk simpan gambar

Telefon akan auto tanya permission bila pertama kali guna.

---

## 📊 STRUKTUR DATABASE

```
Table: tasks
- id (Primary Key)
- customer_name
- location
- task_type
- description
- notes
- status
- date
- time
- photos (JSON array of Base64 images)
```

---

## 🎨 WARNA TEMA

- **Primary**: #667EEA (Ungu/Biru)
- **Gradient**: #667EEA → #764BA2
- **Selesai**: Hijau
- **Dalam Proses**: Oren
- **Pending**: Merah

---

## ⚠️ TROUBLESHOOTING

### Error: "SDK not found"
- Install Android SDK melalui Android Studio
- Tools → SDK Manager

### Error: "Permission denied"
- Pastikan USB Debugging enabled di telefon
- Developer Options → USB Debugging

### App crash bila ambil gambar
- Check AndroidManifest.xml ada permissions
- Test pada device sebenar (bukan emulator tanpa kamera)

### Gambar tak keluar
- Pastikan permission CAMERA dan STORAGE diberi
- Settings → Apps → [Your App] → Permissions

---

## 📱 REQUIREMENT SISTEM

**Untuk Telefon:**
- Android 5.0 (Lollipop) atau lebih tinggi
- Kamera (depan atau belakang)
- Minimum 50MB storage

**Untuk Development:**
- Windows/Mac/Linux
- Android Studio Arctic Fox atau lebih baru
- Minimum 8GB RAM
- 4GB free disk space

---

## 🚀 CARA EXPORT APK

1. Dalam Android Studio: Build → Build Bundle(s)/APK(s) → Build APK(s)
2. Tunggu build selesai
3. Klik "locate" bila siap
4. Copy APK file
5. Transfer ke telefon dan install!

---

## 📞 SOKONGAN

Kalau ada masalah:
1. Check semua file di tempat yang betul
2. Sync Gradle
3. Clean & Rebuild project
4. Restart Android Studio

---

## ✨ FUTURE IMPROVEMENTS (Jika nak tambah)

- Export report ke PDF
- Share report via WhatsApp/Email
- Search & filter tugasan
- Edit tugasan
- Backup & restore database
- Dark mode
- Multi-bahasa (English/Malay)

---

**🎉 SELAMAT MENCUBA! 🎉**

Aplikasi ini dibina untuk memudahkan kerja juruteknik komputer merekod tugasan harian dengan dokumentasi lengkap!
