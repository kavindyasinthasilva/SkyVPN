# TOTO VPN - Android VPN App

TOTO VPN is a comprehensive Android application that combines a robust VPN service with utility features like Phone Booster, CPU Cooler, and Battery Saver. This project is built for performance and modern Android standards.

## 🚀 Key Features

*   **Fast VPN Service**: Secure and high-speed OpenVPN-based connections.
*   **Phone Booster**: Optimizes memory and kills background processes to speed up the device.
*   **CPU Cooler**: Monitors device temperature and cools down the CPU by closing heat-producing apps.
*   **Battery Saver**: Extended battery life through different power-saving modes.
*   **Subscription Plan**: Integrated Google Play Billing for Premium/VIP server access.
*   **AdMob & Facebook Ads**: Optimized ad integration for monetization.

## 🛠 Tech Stack

*   **Language**: Java & Kotlin
*   **VPN Engine**: OneConnectLib (OpenVPN API)
*   **Network**: Volley, Glide
*   **Utilities**: Conscrypt (Modern SSL/TLS), OneSignal (Notifications)
*   **Build System**: Gradle 8.9

## ⚡ Recent Optimizations & Fixes

We have recently performed major architectural improvements to ensure a smooth user experience:

### 1. Build & Dependency Fixes
*   **Migrated to OneConnectLib**: Fixed the critical "401 Unauthorized" error from JitPack by migrating from the deprecated `thor77:OpenVPN-Api` to the stable and publicly maintained `OneConnectLib`.

### 2. Performance Enhancements
*   **In-Memory Caching**: Implemented a static cache in `ActiveServer.java` to reduce disk I/O and speed up server state checks.
*   **Staggered Initialization**: Optimized `App.java` to initialize heavy SDKs (Ads, OneSignal, Conscrypt) in a background thread with staggered delays. This resolved the "No response to onStartJob" ANR issue.
*   **Build Speed**: Increased Gradle heap size to 4GB and enabled parallel execution and configuration caching.

### 3. User Experience (UX) Improvements
*   **Connection Watchdog**: Added a 30-second timeout mechanism to prevent the app from getting stuck in a "Connecting" state indefinitely.
*   **Smarter IP Fetching**: Optimized IP address discovery to reduce redundant network calls.
*   **UI Synchronization**: Improved state management in `MainActivity` to ensure the UI always reflects the actual VPN connection status.

## ⚙️ Setup Instructions

1.  **Clone the project** into Android Studio.
2.  **Gradle Sync**: Ensure you have an active internet connection to download the `OneConnectLib` and other dependencies from JitPack.
3.  **Firebase**: Replace `google-services.json` with your own Firebase configuration file.
4.  **OneSignal**: Update the App ID in `App.java`.
5.  **Ad IDs**: Configure your AdMob and Facebook Audience Network IDs in `MainActivity.java`.

## 📄 License

This project is based on the TOTO VPN codecanyon template. Please refer to your purchase license for usage rights.
