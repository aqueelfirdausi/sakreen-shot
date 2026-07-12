# Sakreen Shot — Google Antigravity Build Prompt

You are working inside this exact local project folder:

`C:\Users\Administrator\Desktop\Antigravity Projects\Sakreen-Shot`

## Mission

Build a working Android MVP of **Sakreen Shot** as quickly and cleanly as possible.

Sakreen Shot is an offline-first Android utility that detects screenshots, extracts text locally, categorizes them, makes them searchable, and lets the user manually bulk-delete unwanted screenshots with a single Android confirmation.

Do not over-document. Do not create unnecessary architecture layers. Do not spend the session writing planning files instead of building. Implement the actual app.

## Existing visual references

Use these local images as the visual direction:

- `00-original-concept.png`
- `01-refinement-round-01.png`

The approved visual language is:

- premium beige and parchment theme
- warm ivory surfaces
- deep espresso text
- restrained bronze accents
- subtle paper-inspired texture
- elegant serif only for major headings
- clean sans-serif for functional UI
- calm, luxurious, private, tactile, editorial
- no neon
- no cyberpunk
- no bright gradients
- no excessive glassmorphism
- no generic Material defaults without customization

Do not redesign the app into a dark neon dashboard.

---

# Product requirements

## Core user experience

The user should be able to:

1. Open the app manually.
2. Grant required media permissions.
3. Import existing screenshots from MediaStore.
4. Detect and process newly added screenshots.
5. Extract visible text locally on-device.
6. Automatically classify screenshots into:
   - `PAYMENTS`
   - `CHATS`
   - `DOCUMENTS`
   - `UNSORTED`
7. Search screenshots by extracted text.
8. Browse category folders with live counts.
9. Open a screenshot detail screen.
10. Pin or unpin a screenshot.
11. Manually change its category.
12. Copy extracted text.
13. Share the screenshot.
14. Review cleanup candidates.
15. Select multiple screenshots.
16. Delete them using one `MediaStore.createDeleteRequest()` confirmation.
17. Reconcile deleted items with the local database.

## Silent behavior

The app must remain quiet.

Do not add:

- screenshot-capture notifications
- daily summaries
- persistent status-bar icons
- promotional alerts
- automatic deletion
- background uploads
- cloud sync
- accounts
- analytics
- advertising SDKs

Use short-lived background work only when necessary.

---

# Technical stack

Use:

- Kotlin
- Jetpack Compose
- Material 3 as the base, heavily customized
- Room
- Room FTS
- WorkManager
- MediaStore
- `ContentObserver`
- Google ML Kit Text Recognition
- Coroutines and Flow
- Navigation Compose
- Coil for thumbnails
- DataStore only if needed for small local preferences

Use a single app module unless a second module is genuinely required.

Do not create a large multi-module clean-architecture framework.

Preferred package name:

`com.sakreenshot.app`

Use the latest stable Android configuration already supported by the installed local toolchain. Do not upgrade build tools recklessly if the environment already has a working stable configuration.

Minimum SDK target:

- Prefer Android 10 / API 29 unless a concrete dependency conflict requires a different minimum.

Target current stable SDK available locally.

---

# Local project structure

Create the Android project directly inside:

`C:\Users\Administrator\Desktop\Antigravity Projects\Sakreen-Shot`

Preserve the two existing PNG visual references.

A practical structure is enough:

```text
app/src/main/java/com/sakreenshot/app/
├── MainActivity.kt
├── SakreenShotApp.kt
├── data/
│   ├── db/
│   ├── media/
│   ├── ocr/
│   ├── classification/
│   └── repository/
├── worker/
├── ui/
│   ├── navigation/
│   ├── theme/
│   ├── components/
│   └── screens/
│       ├── home/
│       ├── search/
│       ├── detail/
│       ├── cleanup/
│       └── settings/
└── util/
```

Do not create dozens of empty directories or placeholder classes.

---

# Database design

Use a Room entity named `ScreenshotEntity`.

Recommended fields:

```text
id: Long
contentUri: String
mediaStoreId: Long
displayName: String
relativePath: String?
extractedText: String
normalizedText: String
primaryCategory: String
classificationScore: Int
capturedAt: Long
indexedAt: Long
modifiedAt: Long
width: Int?
height: Int?
fileSize: Long?
isPinned: Boolean
estimatedExpiry: Long?
processingStatus: String
contentHash: String?
```

Use `contentUri` and `mediaStoreId` as the real media references.

Do not depend only on raw filesystem paths.

Use a Room FTS table linked to the screenshot rows for fast text search.

Required DAO operations:

- insert or update screenshot
- observe all screenshots
- observe category counts
- search extracted text
- filter by category
- observe pinned screenshots
- update pin state
- update category
- delete records
- mark missing media
- fetch cleanup candidates
- find by MediaStore ID
- prevent duplicate indexing

Use migrations properly if the schema changes during the build.

---

# Classification rules

Implement deterministic weighted local rules.

## Payments and Receipts

Keywords and patterns:

- Sent
- Received
- Transaction ID
- Transaction
- TRX
- TRX ID
- Rs
- Rs.
- PKR
- Successful
- Payment Successful
- Remittance
- Ref No
- Reference
- EasyPaisa
- JazzCash
- NayaPay
- SadaPay
- HBL
- Meezan
- UBL
- MCB
- Bank Alfalah
- Allied Bank
- account number patterns
- IBAN patterns
- currency amount patterns

## Work and Chats

Keywords:

- WhatsApp
- Chat
- Today
- Yesterday
- Online
- Type a message
- typing
- message
- sent
- delivered
- forwarded

Do not classify as chat based only on weak words like “Today” or “Sent” if strong payment evidence exists.

## Documents and Details

Keywords and signals:

- CNIC
- Identity
- National Identity
- Name
- Date of Birth
- Address
- Invoice
- Bill
- Statement
- Customer
- Supplier
- Quantity
- Total
- high text density
- CNIC-like patterns
- invoice-number patterns

## Unsorted

Use as the safe fallback.

## Scoring behavior

Use weighted scoring instead of first-match logic.

Example:

- strong exact phrase: +5 or +6
- medium keyword: +3
- weak keyword: +1
- structured numeric pattern: +2 or +3
- high text density: +2 for documents

If the best category score is below a sensible threshold, return `UNSORTED`.

In ties, prefer stronger structured evidence. If still tied, use `UNSORTED`.

Add focused unit tests for classification.

---

# Screenshot discovery

Implement both:

1. Historical screenshot import
2. New screenshot detection

## Historical import

Query MediaStore images and include likely screenshots using:

- `RELATIVE_PATH`
- `DISPLAY_NAME`
- MIME type
- common screenshot folder names
- common OEM naming patterns

Support names and paths such as:

- Screenshots
- Screenshot
- DCIM/Screenshots
- Pictures/Screenshots
- screenshot_
- Screenshot_
- Screenshot-
- SmartCapture
- ScreenCapture

Do not scan unrelated media unnecessarily.

The first-run import should be manually started from the app.

Process in small batches.

Show progress only inside the app.

## New screenshot detection

Use a `ContentObserver` attached to:

`MediaStore.Images.Media.EXTERNAL_CONTENT_URI`

When a new image is detected:

- debounce repeated observer events
- query the candidate
- confirm it is likely a screenshot
- ensure it is not already indexed
- enqueue unique WorkManager work based on MediaStore ID
- process it
- shut down

Do not use a permanent foreground service merely to watch screenshots.

Be honest about Android lifecycle limits. The app should reconcile missed screenshots when opened again.

---

# OCR

Use Google ML Kit Text Recognition locally.

Requirements:

- load from `contentUri`
- downsample only when needed
- preserve enough resolution for text accuracy
- handle rotated images
- return empty text safely
- normalize whitespace
- lowercase a normalized copy for classification
- store raw extracted text separately
- mark failures without crashing
- allow retry

Do not upload images or OCR text anywhere.

---

# Permissions

Implement correct behavior by Android version.

Use the narrowest permissions possible.

Handle:

- granted
- denied
- permanently denied
- partial access where relevant
- no screenshots found

Explain permissions clearly in the UI.

Do not pretend the app can silently bypass Android restrictions.

---

# Screens and UX

Build these screens:

## 1. Archive Home

Use the approved reference image.

Include:

- Sakreen Shot title
- subtitle: `Your private screenshot archive`
- sticky search entry
- four category cards
- live item counts
- pinned records section
- Storage Sanity card
- bottom navigation

Category cards:

- Payments & Receipts
- Work & Chats
- Documents & Details
- Unsorted

Storage Sanity subtitle:

`Review and remove old screenshots`

## 2. Intelligent Search

Include:

- sticky search input
- instant debounced search
- category filters
- pinned filter
- recent filter
- compact result cards
- screenshot thumbnail
- title or strongest OCR line
- highlighted matching text where practical
- category
- date
- pin state

Use dense, readable cards.

## 3. Private Record View

Include:

- screenshot preview
- extracted text section
- category
- captured date
- processing status
- expiry suggestion
- pin
- copy text
- share
- change category
- delete

Keep:

`Processed privately on this device`

Use one-item delete confirmation through Android’s supported MediaStore mechanism.

## 4. Storage Sanity

Include cleanup groups:

- Old Screenshots
- OTPs & Codes
- Low Information
- Temporary Confirmations
- Unsorted
- Large Files

Show:

- item count
- estimated recoverable storage
- oldest item
- dense selectable screenshot grid
- selection count
- Keep
- Delete Selected

Keep:

`Android will ask once before deleting this batch.`

Never delete automatically.

## 5. Settings

Keep it minimal.

Include:

- permission status
- run screenshot import
- re-index failed items
- classification explanation
- privacy statement
- app version
- optional cleanup age threshold

Do not turn Settings into a large control panel.

---

# Visual system

Use the approved mockup as the source of truth.

Suggested palette:

```text
Background        #E8DECF
Surface           #F4EBDD
Surface Elevated  #FBF6ED
Primary Text      #29231E
Secondary Text    #665B51
Accent Bronze     #9A7247
Border            #C7B8A5
Delete            #A45F4A
Success Olive     #7B8065
```

Requirements:

- very subtle texture only
- no heavy paper grain behind dense text
- 12–18 dp corner radius range
- restrained shadows
- visible but soft borders
- high enough contrast for real devices
- accessible tap targets
- consistent icon stroke style
- consistent spacing
- serif used sparingly
- no ornamental vintage treatment

Use Android system fonts if licensing or bundled-font uncertainty would slow progress. A refined serif may be used for headings only if it is safely available.

---

# Cleanup logic

For MVP, cleanup candidates can be generated through local rules.

## Old screenshots

Default: older than 30 days.

Allow user threshold later.

## OTPs and Codes

Look for:

- OTP
- verification code
- one-time password
- code expires
- valid for
- numeric short-code patterns

Use an estimated expiry when confidence is reasonable.

## Low Information

Examples:

- empty OCR
- very short OCR
- no meaningful words
- accidental captures

Do not assume empty OCR always means junk. Only surface for review.

## Temporary Confirmations

Examples:

- payment successful
- order placed
- copied
- saved
- download complete

Only surface for review after an age threshold.

## Large Files

Sort by file size.

Never auto-delete.

---

# Bulk deletion

Use:

`MediaStore.createDeleteRequest()`

Bundle selected content URIs into one request.

After user confirmation:

- re-query MediaStore
- remove records for successfully deleted items
- preserve records for cancelled or failed items
- update UI immediately
- handle partial failure safely

Do not show one confirmation per file.

---

# Performance

The app should remain responsive with several thousand screenshots.

Use:

- Room Flow
- pagination or efficient limits where needed
- thumbnail loading through Coil
- stable keys
- background dispatchers
- unique WorkManager jobs
- database indexes
- FTS
- batched import
- no full-size bitmap loading in grids

Avoid premature enterprise optimization.

---

# Testing

Do not create a massive test suite, but cover the risky parts.

Required tests:

- classification scoring
- FTS search
- Room DAO basics
- cleanup candidate rules
- duplicate prevention
- category counts

Manual verification:

- app launch
- permission flow
- historical import
- search
- pinning
- category change
- detail view
- multi-select
- deletion request
- database reconciliation
- screen rotation or configuration change
- denied permission
- no screenshots state
- failed OCR state

---

# Build workflow

Work in practical phases, but continue automatically unless blocked.

## Phase A — Foundation

- inspect current folder
- preserve PNG references
- initialize Git only if no repository exists
- create Android project
- establish theme
- create navigation shell
- build successfully

## Phase B — Local data and mock UI

- Room entities
- FTS
- DAO
- repository
- screens using temporary local mock records
- verify visual direction

## Phase C — MediaStore and permissions

- permissions
- screenshot query
- historical import
- thumbnails

## Phase D — OCR and classification

- ML Kit
- worker
- classification
- indexing

## Phase E — Search and detail actions

- full-text search
- pin
- copy
- share
- category change

## Phase F — Cleanup and deletion

- cleanup queries
- selection
- bulk delete request
- reconciliation

## Phase G — QA and polish

- fix crashes
- improve visual consistency
- verify on emulator or connected device
- build release APK

Do not pause after every tiny phase asking for permission.

Continue until:

- the MVP builds
- core flows work
- there are no known blocking crashes
- a release APK is produced

Stop only for:

- a required external credential
- an unavoidable local toolchain failure
- a permission or Android platform limitation that cannot be resolved safely
- a destructive operation outside the project folder
- a decision that materially changes the product

---

# Git discipline

Before changing anything:

- run `git status`
- inspect existing files
- confirm the exact project root

If no Git repository exists, initialize one.

Use small meaningful commits, but do not create a commit for every trivial edit.

Suggested milestones:

1. `Initialize Sakreen Shot Android app`
2. `Add local screenshot database and search`
3. `Add MediaStore import and OCR processing`
4. `Add classification and screenshot explorer`
5. `Add cleanup and bulk deletion`
6. `Polish MVP and document verification`

Do not rewrite Git history.

Do not delete or move the reference images.

---

# Minimal documentation only

Create only:

- `README.md`
- `docs/PROJECT-BRIEF.md`
- `docs/BUILD-STATUS.md`

Do not create dozens of planning documents.

`README.md` should contain:

- what the app does
- requirements
- how to build
- privacy statement
- current limitations

`BUILD-STATUS.md` should contain:

- completed features
- known issues
- exact APK path
- last verification commands

Keep documentation short and factual.

---

# Verification commands

Use the project’s actual Gradle wrapper.

At minimum run:

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

Also run connected or emulator tests when available.

Build a release APK if signing requirements allow an unsigned or debug-compatible release artifact.

Report exact artifact paths.

---

# Final deliverables

At completion, provide:

1. Current Git branch and commit.
2. Files and major features added.
3. Exact build commands run.
4. Test and lint results.
5. Exact APK path.
6. Screenshots of:
   - Home
   - Search
   - Detail
   - Storage Sanity
7. Known limitations.
8. Anything requiring real-device verification.
9. Confirmation that the two original UX reference images were preserved.
10. Confirmation that no cloud backend, analytics, ads, or uploads were added.

---

# Non-negotiable rules

- Work only inside the approved Sakreen Shot folder.
- Preserve existing reference images.
- Build the app instead of producing excessive plans.
- Do not create duplicate project folders.
- Do not create a second Android project elsewhere.
- Do not add cloud infrastructure.
- Do not add authentication.
- Do not add analytics or ads.
- Do not add automatic deletion.
- Do not add persistent notifications.
- Do not claim something works unless it was verified.
- Mark uncertain items as `Needs verification`.
- Prefer a working, polished MVP over over-engineered architecture.
- Keep momentum and finish the utility app as far as the local environment safely allows.
