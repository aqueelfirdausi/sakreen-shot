# Sakreen Shot — Play Store Closed-Testing Release Checklist

This step-by-step checklist guides the app owner through publishing the first closed-testing release candidate on Google Play Console.

---

## 1. Play Console Setup & App Identity
- [ ] Log in to Google Play Console with owner account.
- [ ] Create a new app:
  - App Name: `Sakreen Shot`
  - Default Language: `English (US)`
  - App or Game: `App`
  - Free or Paid: `Free`
- [ ] Confirm package name matches repository: `com.sakreenshot.app`

## 2. Production Keystore & Local Signing
- [ ] Generate owner production keystore locally (refer to `README.md`).
- [ ] Securely back up `sakreen-release.jks` and passwords.
- [ ] Set signing environment variables (`SAKREEN_KEYSTORE_PATH`, `SAKREEN_KEYSTORE_PASSWORD`, `SAKREEN_KEY_ALIAS`, `SAKREEN_KEY_PASSWORD`).
- [ ] Build signed Android App Bundle (AAB):
  ```powershell
  .\gradlew.bat bundleRelease
  ```
- [ ] Verify signed AAB generated in `app/build/outputs/bundle/release/app-release.aab`.

## 3. Store Listing & Privacy Setup
- [ ] Fill Store Listing using `docs/play-store-listing-draft.md`:
  - Short Description (77 chars)
  - Full Description
  - Feature bullets
- [ ] Set Privacy Policy HTTPS URL pointing to hosted `docs/privacy-policy-public.html`.
- [ ] Upload Store Graphics:
  - App Icon (512 × 512 PNG, based on approved ivory/espresso/bronze design)
  - Feature Graphic (1024 × 500 PNG, per `docs/store-assets/feature-graphic-brief.md`)
  - Phone Screenshots (minimum 2 screenshots, stored in `docs/store-assets/screenshots/`)

## 4. Policy Declarations & Questionnaire
- [ ] Complete **Data Safety** form using `docs/play-store-data-safety-draft.md`.
- [ ] Complete **Content Rating** questionnaire (IARC).
- [ ] Complete **Target Audience and Content** declaration (Select 18+ / General Audience, Not designed for children).
- [ ] Declare **Financial Features** (None / Personal productivity management only).

## 5. Closed-Testing Upload & Track Management
- [ ] Navigate to **Testing > Closed testing** in Play Console.
- [ ] Create or select track (e.g. `Alpha` or `Internal testing`).
- [ ] Create new release and upload `app-release.aab`.
- [ ] Paste release notes from `docs/release-notes-v1.0.0.md`.
- [ ] Save and roll out release to closed-testing track.
- [ ] Add tester email list.

## 6. Testing & Post-Upload Verification
- [ ] Install Sakreen Shot from Google Play testing link on test device.
- [ ] Verify clean installation and migration from debug build if upgrading.
- [ ] Test screenshot ingestion, on-device OCR search, category assignment, and deletion confirmation.
- [ ] Check Play Console **Pre-launch report** for stability, accessibility, or device-specific launch issues.
