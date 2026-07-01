# NIDS Monitor — Android App

Companion Android app for the NIDS backend. Built with Kotlin and Jetpack Compose (Material3), following MVVM architecture. Displays real-time alerts and receives push notifications via Firebase Cloud Messaging when the backend detects an attack.

## Requirements

- Android Studio (latest stable)
- Min SDK 24 · Target SDK 35
- A physical device or emulator on the **same local network** as the Flask backend
- A Firebase project with Cloud Messaging enabled

## Setup

1. **Clone and open** this folder (`mobile-app/NidsMonitor`) in Android Studio.

2. **Add Firebase config:**
   - Go to the [Firebase Console](https://console.firebase.google.com/) → your project → Project Settings → Add Android app
   - Use package name: `com.example.nidsmonitor` *(replace with your actual package name)*
   - Download `google-services.json` and place it in `app/`
   - This file is gitignored — never commit it

3. **Sync Gradle** and let Android Studio download dependencies.

4. **Build and run** on an emulator or device.

## Connecting to the backend

1. Make sure the Flask backend (`backend/app.py`) is running on your machine
2. Find your machine's local IP address (e.g. `192.168.1.104`)
3. In the app: **Settings → Server IP Address** → enter `http://<your-ip>:5000/`
4. Tap **Apply Target Configuration**

Login credentials (demo): `admin` / `admin`

## 📱 Android App Structure

📂 app/src/main/java/.../  
 ├── 📂 data/  
 │    └── 📄 Models.kt — Stats, Alert, Health data classes  
 ├── 📂 network/  
 │    ├── 📄 ApiService.kt — Retrofit interface  
 │    └── 📄 NetworkManager.kt — Retrofit client singleton  
 ├── 📂 ui/  
 │    ├── 📂 screens/ — Login, Dashboard, Alerts, AlertDetail, Health, Settings  
 │    └── 📄 NidsViewModel.kt — MVVM ViewModel, 5s polling loop  
 ├── 📂 messaging/  
 │    └── 📄 NidsMessagingService.kt — FCM push notification handler  
 └── 📂 navigation/  
      └── 📄 AppNavigation.kt — Jetpack Navigation Compose, deep links  

## Key Libraries

| Library | Purpose |
|---|---|
| Jetpack Compose (Material3) | Declarative UI |
| Jetpack Navigation Compose | Screen routing + deep links (`nidsapp://alert/{id}`) |
| Retrofit2 + Gson | REST API communication |
| Firebase Cloud Messaging | Real-time push alerts |
| ViewModel + StateFlow | Reactive state management |

## Notes

- Push notifications require `POST_NOTIFICATIONS` permission on Android 13+ (requested at runtime)
- The app subscribes to the `alerts` FCM topic on launch — all devices with the app installed receive every alert