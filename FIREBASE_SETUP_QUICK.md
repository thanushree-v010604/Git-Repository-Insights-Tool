# 🚀 Firebase Setup - 5 Minute Quick Start

Your app is running but showing **"Firebase is not configured"** because the Firebase credentials are empty in `BuildConfig`. Follow these steps to fix it.

---

## Step 1: Create Firebase Project (2 minutes)

1. Go to **https://console.firebase.google.com/**
2. Click **"Create a project"** or use an existing one
3. Give it a name (e.g., "RepoPulse")
4. Click **Create project**
5. Wait for it to finish loading

---

## Step 2: Register Your Android App (2 minutes)

1. In Firebase Console, click **"Add app"** → select **Android**
2. Fill in these details:
   - **Android package name**: `com.example.git_repo_4`
   - **App nickname** (optional): RepoPulse
   - **Debug signing certificate SHA-1** (leave blank for now, optional)
3. Click **"Register app"**

---

## Step 3: Get Your Firebase Credentials (1 minute)

### Option A: From Firebase Console (Recommended)

1. In Firebase Console, go to **⚙️ Project Settings** (gear icon, top left)
2. Click the **Android** tab
3. Look for this section and copy the values:
   ```
   apiKey: "AIzaSy..." → firebase.apiKey
   applicationId: "1:123456:android:abc..." → firebase.appId
   projectId: "yourproject-12345" → firebase.projectId
   storageBucket: "yourproject.appspot.com" → firebase.storageBucket
   messagingSenderId: "123456789" → firebase.gcmSenderId
   ```

### Option B: From google-services.json (Alternative)

1. Download `google-services.json` from Firebase Console
2. Place it in: `app/src/main/google-services.json`
3. The values are in the JSON file

---

## Step 4: Add Credentials to Your Project

Edit: `C:\Users\thanu\OneDrive\Desktop\Git_repo_4\local.properties`

Replace the placeholders with your actual Firebase values:

```ini
firebase.apiKey=AIzaSyD...
firebase.appId=1:234567:android:abc123def456
firebase.projectId=my-project-12345
firebase.storageBucket=my-project-12345.appspot.com
firebase.gcmSenderId=123456789
```

**⚠️ Important**: 
- Never commit `local.properties` to Git
- It's already in `.gitignore` (check: `cat .gitignore | grep local.properties`)
- Only store this file locally

---

## Step 5: Rebuild and Test (Final Step)

1. **Android Studio**: Click **Build** → **Rebuild Project**
   OR in terminal:
   ```powershell
   cd C:\Users\thanu\OneDrive\Desktop\Git_repo_4
   .\gradlew.bat clean build
   ```

2. **Uninstall old app** from device/emulator:
   ```powershell
   adb uninstall com.example.git_repo_4
   ```

3. **Run app again**:
   - Click **Run** in Android Studio or use:
   ```powershell
   .\gradlew.bat installDebug
   adb shell am start -n com.example.git_repo_4/.MainActivity
   ```

4. **Test signup**: The "Firebase is not configured" toast should disappear!

---

## ✅ Verify It's Working

When you tap **Sign Up** with valid credentials:
- ✅ No "Firebase is not configured" message
- ✅ Account is created (if email is new)
- ✅ Redirected back to login screen
- ✅ Can now log in with that email/password

---

## 🆘 Troubleshooting

| Problem | Solution |
|---------|----------|
| Still shows "Firebase is not configured" | Rebuild the app after adding credentials to `local.properties` |
| "Email already registered" | Use a different email address for testing |
| Emulator/device not found | Run `adb devices` to see connected devices |
| BuildConfig fields still empty | Close Android Studio and reopen the project |

---

## 📝 Where Are These Values Used?

Your app's `build.gradle.kts` reads from `local.properties`:

```kotlin
val firebaseApiKey = (project.findProperty("firebase.apiKey") as? String).orEmpty()
buildConfigField("String", "FIREBASE_API_KEY", "\"${escapeForBuildConfig(firebaseApiKey)}\"")
```

When you rebuild, these values become available in `BuildConfig.FIREBASE_API_KEY`, etc., and your app can initialize Firebase successfully.

---

## 🔗 Helpful Links

- **Firebase Console**: https://console.firebase.google.com/
- **Firebase Auth Docs**: https://firebase.google.com/docs/auth
- **Android Setup Guide**: https://firebase.google.com/docs/android/setup
- **Troubleshooting**: https://firebase.google.com/docs/auth/troubleshoot

---

**You're almost done!** Just 5 minutes and your Firebase auth will be live. 🎉

