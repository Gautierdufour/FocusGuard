# FocusGuard

> Take back control of your screen time — reprenez le contrôle de votre temps d'écran.

[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Version](https://img.shields.io/badge/Version-1.0-brightgreen.svg)](#)

---

## What is FocusGuard?

FocusGuard is an Android app that helps you break free from distracting apps. When you try to open a blocked app, FocusGuard intercepts it and asks you to complete a challenge first — turning every distraction attempt into a moment of effort or reflection.

**Available on [Google Play](#)** *(coming soon)*

---

## Features

### App Blocking
- Real-time foreground app detection
- Customizable list of apps to block
- Persistent background service with automatic restart after reboot

### Challenges
Complete one of these to unlock a blocked app:

| Challenge | Description |
|-----------|-------------|
| **Pushups** | Automatic detection via proximity sensor |
| **Breathing** | Guided cardiac coherence exercise |
| **Cognitive** | Mental math, quiz, logic puzzles |
| **Waiting** | Mandatory reflection timer |

### Smart Planning
- **Schedules** — block apps automatically on specific days/times
- **Focus sessions** — timed deep-work sessions
- **Location-based blocking** — block apps when you arrive at a specific place (work, school, etc.)

### Statistics & Gamification
- Blocks count and time saved per app
- Addiction score per application
- XP system, levels (1–50), 15+ badges, daily streaks
- Home screen widget

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 1.9 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + StateFlow |
| Database | Room |
| Background | Foreground Service + WorkManager |
| Sensors | Proximity sensor (pushup detection) |

---

## Build

### Requirements
- Android Studio Hedgehog or later
- Android SDK 26+ (Android 8.0+)

### Steps

```bash
git clone https://github.com/Gautierdufour/FocusGuard.git
cd FocusGuard
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

### Release build

Configure `local.properties` with your keystore credentials:

```properties
KEYSTORE_PATH=/path/to/your.jks
KEYSTORE_PASS=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASS=your_key_password
```

Then: `./gradlew bundleRelease`

---

## Permissions

| Permission | Purpose |
|-----------|---------|
| `PACKAGE_USAGE_STATS` | Detect which app is in the foreground |
| `QUERY_ALL_PACKAGES` | List installed apps to block |
| `SYSTEM_ALERT_WINDOW` | Display blocking overlay above other apps |
| `ACCESS_FINE_LOCATION` | Location-based blocking (optional feature) |

All data is processed **on-device only**. Nothing is transmitted externally.

---

## Privacy

FocusGuard collects no personal data and uses no third-party analytics or crash-reporting SDK.

Full privacy policy: [gautierdufour.github.io/FocusGuard/privacy-policy.html](https://gautierdufour.github.io/FocusGuard/privacy-policy.html)

---

## License

This project is licensed under the MIT License.

---

## Contact

Questions or feedback: [contact@focusguard.app](mailto:contact@focusguard.app)
