# RepoPulse - Setup Guide (Start to End)

This guide helps anyone who downloads this project from GitHub and runs it successfully from scratch.

## Checklist

- [ ] Install required tools (JDK + Android Studio)
- [ ] Clone or download and extract the project
- [ ] Open the project in Android Studio
- [ ] Add Firebase config file (`app/google-services.json`)
- [ ] Set local config in `local.properties`
- [ ] Sync Gradle and build
- [ ] Run on emulator or physical Android device
- [ ] Verify login and main screens load

## 1) Prerequisites

Install these first:

- Android Studio (latest stable)
- Android SDK (API 36 recommended)
- JDK 11 (project uses Java 11 compatibility)
- Git (optional, but recommended)

## 2) Get the Project

### Option A: Clone with Git

```powershell
cd C:\Users\<your-user>\Desktop
git clone <your-repo-url> Git_repo_4
cd Git_repo_4
```

### Option B: Download ZIP

1. Download ZIP from GitHub.
2. Extract it to a simple path (example: `C:\Projects\Git_repo_4`).
3. Avoid very long folder paths.

## 3) Open in Android Studio

1. Open Android Studio.
2. Click **Open**.
3. Select the project root folder (the one containing `settings.gradle.kts`).
4. Let Gradle sync finish.

## 4) Firebase Setup (Required)

This project validates Firebase config during build.

1. Go to Firebase Console: https://console.firebase.google.com/
2. Create/select a Firebase project.
3. Add Android app with package name:
   - `com.example.git_repo_4`
4. Download `google-services.json`.
5. Put the file here:
   - `app/google-services.json`

Important:

- If `google-services.json` is missing, build will fail.
- If package name does not match `com.example.git_repo_4`, build will fail.

## 5) Configure `local.properties`

`local.properties` should stay local and should not be committed with secrets.

Make sure these entries are set:

```ini
sdk.dir=C\:\\Users\\<your-user>\\AppData\\Local\\Android\\Sdk
github.token=
```

Notes:

- `sdk.dir` must point to your local Android SDK path.
- `github.token` is optional but recommended for GitHub API reliability.

## 6) Build the App

From Android Studio:

- **Build > Make Project**

Or via terminal:

```powershell
cd C:\Users\<your-user>\Desktop\Git_repo_4
.\gradlew.bat :app:assembleDebug
```

APK output (debug):

- `app\build\outputs\apk\debug\app-debug.apk`

## 7) Run the App

1. Start an Android emulator (API 24+), or connect a real device with USB debugging enabled.
2. In Android Studio, select device.
3. Click **Run**.

App launch flow is from `MainActivity` (launcher activity in `app/src/main/AndroidManifest.xml`).

## 8) Common Issues and Fixes

### A) `Missing app/google-services.json`

Cause: Firebase file not present.
Fix: Add `google-services.json` to `app/`.

### B) `No client with package_name=com.example.git_repo_4`

Cause: Firebase Android app package mismatch.
Fix: Recreate/add Android app in Firebase with exact package name.

### C) Gradle sync/build issues after moving project

Try:

```powershell
cd C:\Users\<your-user>\Desktop\Git_repo_4
.\gradlew.bat --stop
.\gradlew.bat clean
.\gradlew.bat :app:assembleDebug
```

### D) SDK not found

Cause: wrong `sdk.dir` in `local.properties`.
Fix: update `sdk.dir` to your local SDK path.

## 9) Recommended Before Pushing to GitHub

- Keep `google-services.json` out of public repo unless intentional.
- Keep real tokens out of `local.properties`.
- Add/update `.gitignore` for local and secret files.

## 10) Project Quick References

- App module: `app/`
- Manifest: `app/src/main/AndroidManifest.xml`
- Build config: `app/build.gradle.kts`
- Local machine config: `local.properties`

---

If you share this project with others, ask them to follow this README step-by-step from Section 1 to Section 7.
