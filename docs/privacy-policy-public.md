# Privacy Policy for Sakreen Shot

**Effective Date:** July 23, 2026  
**Last Updated:** July 23, 2026  

Sakreen Shot ("we", "our", or "us") is dedicated to protecting your privacy. Sakreen Shot is designed from the ground up as a **100% local-first, offline screenshot organization tool**. 

This Privacy Policy explains how Sakreen Shot handles device data, storage permissions, and local processing.

---

## 1. 100% Local-First Architecture

- **Screenshots Remain On Your Device**: All screenshots imported into or managed by Sakreen Shot stay exclusively on your local device storage.
- **On-Device OCR Processing**: Optical Character Recognition (OCR) is performed entirely on your device using Google ML Kit on-device text recognition. Text from receipts, bills, CNICs, chats, or notes is never transmitted to any external server.
- **No Cloud Uploads**: Sakreen Shot has no cloud servers, no web API endpoints, and no remote data sync mechanism.
- **No Internet Permission**: The application does not request or possess the Android `INTERNET` permission (`android.permission.INTERNET`). The app cannot physically communicate with the internet or external servers.

---

## 2. Data Collection and Usage

- **No User Accounts**: You do not need to register, create an account, log in, or provide any personal information to use Sakreen Shot.
- **No Data Collection**: We do not collect, harvest, aggregate, or process any personal data, usage data, or metadata.
- **No Data Sale or Commercial Sharing**: We do not sell, rent, trade, or share any user data with third parties or advertising networks.
- **No Analytics or Telemetry**: Sakreen Shot contains zero analytics SDKs, zero telemetry tools, and zero tracking pixels.
- **No Advertising**: Sakreen Shot is 100% ad-free and contains no advertising SDKs.

---

## 3. Storage & Permissions

- **Image Access Permission**: Sakreen Shot requests standard Android storage read access (`READ_EXTERNAL_STORAGE` on Android 12 and below, or `READ_MEDIA_IMAGES` on Android 13+) solely to scan and index local screenshot media files.
- **Local Database & Metadata**: OCR text indexes, tags, category assignments, and pinned items are stored locally in an encrypted/isolated SQLite database within the app's internal sandbox directory.
- **Auto-Backup Disabled**: Android Cloud Auto-Backup is explicitly disabled (`allowBackup="false"`) so that your local OCR database and index are never uploaded to Google Drive or cloud backups.
- **App Uninstall Effect**: Uninstalling Sakreen Shot completely deletes the app's internal database and local OCR index. Original screenshot media files in your device gallery remain untouched unless explicitly deleted by you.

---

## 4. User-Controlled Deletion

- **No Automatic Media Deletion**: Sakreen Shot never deletes your screenshots automatically.
- **Android System Confirmation Required**: All screenshot deletions initiated within Sakreen Shot require your explicit confirmation through Android's native Scoped Storage dialog (`MediaStore.createDeleteRequest()`).
- **User Ownership**: You retain total authority over which screenshots are retained or deleted.

---

## 5. Background Ingestion & System Behavior

- **Local Background Sync**: Sakreen Shot uses Android WorkManager to periodically detect newly taken screenshots locally on your device.
- **OS Restrictions**: Background sync operates entirely within standard Android system constraints. System power-saving settings or OEM background optimizations may affect sync timing; missed screenshots are automatically reconciled whenever you open the app.

---

## 6. Contact Information

If you have questions or inquiries regarding this Privacy Policy or Sakreen Shot's privacy architecture, please contact:

**Contact Email / Address:** `[Insert Official Contact Address / Email]`  
**App Developer:** `Sakreen Shot Team`

---

*This privacy policy is provided for public reference and static HTTPS hosting in compliance with Google Play Store Developer Policies.*
