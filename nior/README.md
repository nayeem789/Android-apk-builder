# NIOR Music Player — Android Native Project

Complete native offline music player built with **Kotlin**, **Jetpack Compose**, **AndroidX Media3 (ExoPlayer)**, and **Room Database**.

## Features
- **Compatibility**: Android 7.0 (API 24) to Android 15/17 (API 35+)
- **100% Offline**: Zero internet permission, zero tracking, zero remote servers
- **Liquid Glass UI**: Translucent frosted panels, purple glowing accents, swipeable pill bottom navigation
- **Audio Engine**: AndroidX Media3 Foreground Service, MediaSession, AudioFocus handling, Equalizer & BassBoost
- **Device Music Scanner**: MediaStore integration for fast local audio indexing

---

## How to Build the APK & Install on Your Android Phone

### Step 1: Download or Unzip the Project
Extract this folder to your computer (e.g. `C:\Projects\nior-music-player` or `~/nior-music-player`).

### Step 2: Open in Android Studio
1. Open **Android Studio** (Koala, Ladybug, Iguana, or later).
2. Click **Open** and select this directory.
3. Allow Gradle to sync dependencies automatically (takes 1–2 minutes on first run).

### Step 3: Build Debug APK or Run on Device
#### Option A: Run directly on your phone
1. Enable **Developer Options** and **USB Debugging** on your Android phone:
   - Go to `Settings` -> `About phone` -> Tap `Build number` 7 times.
   - Go to `Settings` -> `System` -> `Developer options` -> Turn on `USB debugging`.
2. Connect your phone via USB cable and tap **Allow** on the phone prompt.
3. Select your phone in Android Studio's device dropdown and click the green **Play / Run (Shift+F10)** button.
4. NIOR Music Player installs and launches automatically!

#### Option B: Build APK without Android Studio (Command Line)
Open terminal in this project root:
```bash
# On Linux / macOS:
./gradlew assembleDebug

# On Windows:
gradlew.bat assembleDebug
```
The output APK file will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

Copy `app-debug.apk` to your phone via USB, Google Drive, or Bluetooth, and tap to install!
