# Sakreen Shot Privacy Model

Sakreen Shot is built on a strictly local-first privacy model designed for Pakistani retail, wholesale, and small-business workflows.

## Core Principles
1. **Fully Local-First**: All data, including screenshots, extracted OCR text, and classification results, never leaves the device.
2. **No Cloud Backend**: The application does not connect to any cloud server for processing.
3. **No Analytics or Tracking**: We do not use tracking SDKs, advertising SDKs, or crash reporting tools.
4. **Android-Confirmed Deletion**: The application only deletes media using the explicit Android MediaStore deletion request, ensuring users are always in control of what gets deleted.

## Data Handling
- **OCR Data**: Extracted text from screenshots is stored in a local SQLite (Room) database.
- **Backup Configuration**: Android Auto-Backup is explicitly disabled (`allowBackup="false"`) to prevent sensitive OCR data (such as CNICs, account numbers, and chat messages) from being silently synced to Google Drive.
- **Background Ingestion**: WorkManager jobs run locally. No remote triggers are used.

## Permissions
- **Storage**: The app requests `READ_EXTERNAL_STORAGE` (Android 12) or `READ_MEDIA_IMAGES` (Android 13+) solely to detect and process screenshots.
- **Internet**: The `INTERNET` permission is NOT requested and is not present in the manifest.
