$ErrorActionPreference = "Stop"
$adb = "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe"

# 1. Commit/stash current changes
git add .
git commit -m "WIP: Migration script"

# 2. Checkout v1
git checkout e8a4039
.\gradlew.bat clean assembleDebug
if ($LASTEXITCODE -ne 0) { throw "v1 build failed" }

# 3. Install v1
& $adb install -r app\build\outputs\apk\debug\app-debug.apk
if ($LASTEXITCODE -ne 0) { throw "v1 install failed" }

# 4. Launch v1
& $adb shell am start -n com.sakreenshot.app/.MainActivity
Start-Sleep -Seconds 5

# 5. Take some screenshots so v1 db has rows
& $adb shell screencap -p /sdcard/Pictures/Screenshots/test_v1.png
& $adb shell "am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/Screenshots/test_v1.png"
Start-Sleep -Seconds 15 # Wait for ingestion

# 6. Checkout main (v2)
git checkout main
.\gradlew.bat clean assembleDebug
if ($LASTEXITCODE -ne 0) { throw "v2 build failed" }

# 7. Install v2 over v1
& $adb install -r app\build\outputs\apk\debug\app-debug.apk
if ($LASTEXITCODE -ne 0) { throw "v2 install failed" }

# 8. Launch v2
& $adb shell am start -n com.sakreenshot.app/.MainActivity
Start-Sleep -Seconds 10 # Let migration run

Write-Host "Migration test complete. Now check DB."
