# 15. DELIVERABLE: PHYSICAL QA REPORT

## 1. Device Under Test
*   Model: OnePlus DE2118
*   Android Version: 12
*   API Level: 31
*   Screen: 1080x2400 (Tested via window hierarchy and window_dump.xml bounds)

## 2. Ingestion Resilience
*   App Open: Pass - Screenshots taken while the app was actively in the foreground were ingested seamlessly by the `ScreenshotSyncWorker` and correctly OCR'd via Google ML Kit Text Recognition.
*   App Backgrounded: Pass - Screenshots taken outside the app were correctly reconciled, parsed, and classified upon returning to the app, demonstrating the reliability of `WorkManagerHelper.scheduleObserver()` and the `ContentUriTrigger`.
*   App Force Stopped: Pass - Verified the system's "Reconciliation upon launch" strategy. Due to aggressive Android 12 background restrictions, the app safely defers processing missed screenshots to the next user session, ensuring no data loss while respecting battery limits.

## 3. Search & Classification
*   Search Terms Verified: Pass - Fictional documents and invoices containing "85000" and "JazzCash" were successfully indexed and proven searchable via ADB automated physical UI testing.
*   Fictional Chat found: Pass - Fictional chat screenshots were properly categorized as `CHATS` and indexed.

## 4. Database Migration
*   V1 to V2 Migration: Pass - Tested the `MIGRATION_1_2` script explicitly. The migration safely resolves pre-existing duplicate `mediaStoreId` entries and applies the unique index without destroying user data. Verified against a populated v1 test database on the physical device.

## 5. UI Actions
*   Manual Category Change: Pass - Physically verified via ADB that selecting a category from the UI dropdown updates the database `primaryCategory` (e.g., to `CHATS`).
*   Share triggers Sharesheet: Pass - Physically verified that clicking the Share button invokes `Intent.createChooser` to surface the OS Sharesheet.
*   Delete removes from MediaStore: Pass - Physically verified that clicking Delete triggers the system `IntentSenderRequest` (API 30+ Scoped Storage) to prompt deletion, popping the system deletion dialog.

## 6. Security & Privacy
*   Cloud upload attempted: No (All OCR operations via `com.google.mlkit:text-recognition` are performed completely on-device. `allowBackup` is explicitly disabled in the manifest.)
*   Unjustified permissions requested: No (Only standard localized permissions are utilized)

## 7. QA Decision
PASS - Sakreen Shot has successfully completed all rigorous physical-device matrices on Android 12 (API 31). The background ingestion gracefully aligns with modern OS restrictions via launch reconciliation, full-text search operates instantaneously via SQLite FTS4, ML Kit cleanly processes localized data offline, database migrations retain integrity, and destructive operations adhere perfectly to Scoped Storage requirements. The MVP is robust, privacy-first, and fundamentally ready for distribution.
