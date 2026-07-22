# Sakreen Shot — Play Store Data Safety Questionnaire Draft

This document provides exact responses for the Google Play Console Data Safety form based on Sakreen Shot's local-first architecture.

---

## 1. Data Collection & Sharing Summary

- **Does your app collect or share any of the required user data types?**  
  👉 **No** (The app operates 100% offline; data is processed exclusively on-device and never leaves the phone).

- **Is all of the user data collected by your app encrypted in transit?**  
  👉 **N/A** (No data is transmitted over the network; `INTERNET` permission is absent).

- **Do you provide a way for users to request that their data be deleted?**  
  👉 **Yes** (Uninstalling the app removes all local database records and OCR indexes. Screenshots can also be deleted directly via Android MediaStore system confirmation dialogs).

---

## 2. Specific Data Type Declarations

### Photos and Videos (Photos)
- **Collected?** No (Photos are read locally from Scoped Storage for OCR indexing; they are not sent to developer or third-party servers).
- **Shared?** No.
- **Processed locally?** Yes (On-device screenshot text extraction & classification).
- **Ephemeral processing?** No (Local index stored in app sandbox database for search).

### Files and Docs (Files & Documents)
- **Collected?** No.
- **Shared?** No.
- **Processed locally?** Yes (OCR text metadata stored in local SQLite database).

### Personal Info, Financial Info, Location, Health, Messages, Contacts, Audio, App Activity, App Info & Performance, Device Identifiers
- **Collected?** No.
- **Shared?** No.

---

## 3. Security & Privacy Practices

- **Encryption in Transit**: N/A (No network transmission).
- **Data Deletion Request**: Yes (Direct local deletion within app via Android Scoped Storage system prompts, or complete deletion upon app uninstall).
- **Independent Security Review**: Not conducted.
- **Child Safety (Families Policy)**: App is not designed for children under 13.

---

## 4. Content Rating Questionnaire Answers

- **Violence / Sexual Content / Hate Speech / Drugs**: No.
- **Online Interaction / Social Features**: No (Offline app).
- **Shares Physical Location**: No.
- **Allows Purchasing Digital Goods**: No.
- **Unrestricted Internet Access / Web Browser**: No.
