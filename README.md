# Sakreen Shot

Sakreen Shot is a private, offline-first Android screenshot organizer designed for Pakistani retail, wholesale, supplier, payment, customer-support, and small-business workflows.

## Features
- **Local-First Privacy**: Detects and processes screenshots entirely on-device. No cloud uploads, no internet permission required.
- **Smart Classification**: Automatically groups screenshots into functional categories like Payments, Chats, Documents, and Unsorted.
- **On-Device OCR**: Extracts text from receipts, CNICs, and chats using Google ML Kit.
- **Full-Text Search**: Instantly find screenshots using SQLite FTS (Full-Text Search).
- **Secure Deletion**: Uses the official Android MediaStore deletion requests to permanently remove screenshots securely and safely.

## Android Requirements
- Minimum SDK: API 26 (Android 8.0)
- Target SDK: API 34 (Android 14)
- Permissions: Storage access (`READ_EXTERNAL_STORAGE` on API 31 and below, `READ_MEDIA_IMAGES` on API 33+)

## Development Setup
This project uses standard Android build tools and Jetpack Compose.
```bash
# Clean the project
./gradlew clean

# Run Android Lint
./gradlew lint

# Run Unit Tests
./gradlew test

# Build Debug APK
./gradlew assembleDebug
```

## Known Limitations & Ingestion Behavior
- **Background Ingestion**: Screenshot ingestion operates locally via Android WorkManager. When the app is swiped away or force-stopped, Android OS restrictions may delay background work. Sakreen Shot compensates by reconciling missing screenshots immediately upon the next app launch.
- **OCR Accuracy**: ML Kit text recognition depends on image quality and font size. Very small text on receipts may occasionally be misread.

## QA Rule
**CRITICAL**: When testing or developing, use FICTIONAL test data only. Do not use or capture real personal, banking, supplier, customer, CNIC, or chat data in test screenshots.
