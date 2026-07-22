# Sakreen Shot — Release Notes v1.0.0 (Closed Testing)

## Summary
Welcome to the initial closed-testing candidate of **Sakreen Shot** (v1.0.0, versionCode 1). Sakreen Shot is a 100% offline, privacy-first screenshot organizer designed for Pakistani retail, wholesale, supplier, payment, customer-support, and personal record-keeping.

## What's Included in v1.0.0
- **100% On-Device OCR**: Instant text recognition from receipts, bills, CNICs, chats, and notes powered locally by ML Kit.
- **Full-Text Search (FTS5)**: Search your entire screenshot gallery by transaction reference ID, mobile number, customer name, or keyword in milliseconds.
- **Smart Category Organization**: Automatic grouping into Payments, Chats, ID/Documents, Delivery/Orders, Work, and Unsorted.
- **User-Controlled Storage Cleanup**: Select and delete clutter safely using Android's official Scoped Storage deletion confirmation dialogs.
- **Pinning & Re-classification**: Pin vital payment receipts for fast access and reassign categories with a single tap.
- **Privacy Protection**: Zero internet permission, zero cloud uploads, zero ad SDKs, and zero telemetry. Auto-backup disabled to protect sensitive local records.

## System Notes & Known Behaviors
- **Background Sync Timing**: Background detection of newly taken screenshots uses Android WorkManager. OS battery optimizations or OEM background limits may delay background ingestion; opening the app reconciles all missed screenshots automatically.
- **First Launch Indexing**: On initial launch, Sakreen Shot scans existing device screenshots and indexes text locally. Indexing time depends on the total number of screenshots in your gallery.
