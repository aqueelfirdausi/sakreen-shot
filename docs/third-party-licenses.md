# Sakreen Shot Third-Party Dependency & License Inventory

This document lists all third-party software dependencies compiled into or used by Sakreen Shot v1.0.0.

## Dependency Inventory

| Dependency Name | Group / Package | Version | Purpose | License | Attribution Required? |
|---|---|---|---|---|---|
| ML Kit Text Recognition | `com.google.mlkit:text-recognition` | 16.0.1 | On-device OCR text extraction from screenshots | Apache License 2.0 / Android SDK Terms | Yes (standard notice) |
| AndroidX Room | `androidx.room:room-runtime`, `room-ktx`, `room-compiler` | 2.7.0-rc03 | Local SQLite database persistence & FTS5 search | Apache License 2.0 | Yes (standard notice) |
| AndroidX WorkManager | `androidx.work:work-runtime-ktx` | 2.9.0 | Local background MediaStore sync & periodic indexing | Apache License 2.0 | Yes (standard notice) |
| Jetpack Compose Material3 | `androidx.compose.material3:*` | BOM 2026.03.01 | Modern declarative UI component system | Apache License 2.0 | Yes (standard notice) |
| Coil Compose | `io.coil-kt.coil3:coil-compose` | 3.0.4 | Image loading, thumbnail rendering & caching | Apache License 2.0 | Yes (standard notice) |
| AndroidX Navigation | `androidx.navigation:navigation-compose` | 2.8.5 | Declarative in-app Compose navigation | Apache License 2.0 | Yes (standard notice) |
| AndroidX DataStore | `androidx.datastore:datastore-preferences` | 1.1.1 | Key-value local preferences storage | Apache License 2.0 | Yes (standard notice) |
| AndroidX Core KTX | `androidx.core:core-ktx` | 1.18.0 | Kotlin extensions for core Android APIs | Apache License 2.0 | Yes (standard notice) |
| AndroidX Lifecycle | `androidx.lifecycle:lifecycle-runtime-compose` | 2.10.0 | Lifecycle management & ViewModel bindings | Apache License 2.0 | Yes (standard notice) |
| KotlinX Coroutines | `org.jetbrains.kotlinx:kotlinx-coroutines-play-services` | 1.8.0 | Asynchronous coroutine integration for Play Services Tasks | Apache License 2.0 | Yes (standard notice) |

## Summary & Compliance Status
- **Copyleft (GPL/AGPL)**: None.
- **Proprietary SDKs / Trackers**: None.
- **License Compatibility**: All libraries are licensed under open-source Apache License 2.0. No licensing blockers or restrictions exist for closed-testing or Play Store distribution.
