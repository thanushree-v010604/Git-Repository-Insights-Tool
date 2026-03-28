# Firebase Authentication - Step-by-Step Setup Guide

Complete guide to set up Firebase and deploy your app.

---

## 📋 Prerequisites

Before starting, ensure you have:
- [ ] Android Studio installed
- [ ] Git_repo_4 project opened
- [ ] Google account (for Firebase Console)
- [ ] Internet connection

---

## 🚀 Step 1: Create Firebase Project

### 1.1 Open Firebase Console
- Go to https://console.firebase.google.com/
- Sign in with your Google account

### 1.2 Create New Project
- Click "Create a project"
- Project name: `RepoPulse` (or your choice)
- Click "Continue"
- Choose location (default is fine)
- Click "Create project"
- Wait for project creation (1-2 minutes)

### 1.3 Add Android App to Firebase
- Click "Get started" or the iOS/Android icon
- Select "Android"
- Fill in details:
  - **Package name**: `com.example.git_repo_4` (exact match!)
  - **App nickname**: `RepoPulse` (optional)
  - **SHA-1 fingerprint**: Leave blank for now (optional)
- Click "Register app"

---

## 📥 Step 2: Download google-services.json

### 2.1 Download the File
- Firebase Console will show a blue "Download google-services.json" button
- Click it
- Save the file (remember location)

### 2.2 Add to Your Project
- In Android Studio, go to `Project` view (left sidebar)
- Navigate to `app/` folder
- Right-click on `app/` folder
- Select "Show in Explorer" (Windows) or "Reveal in Finder" (Mac)
- Paste `google-services.json` into the `app/` directory

**File location should be**:
```
Git_repo_4/app/google-services.json
```

### 2.3 Verify in Android Studio
- In Android Studio, expand `app/` folder
- You should see `google-services.json` file
- If not, right-click `app/` → "Synchronize"

---

## 🔐 Step 3: Enable Email/Password Authentication

### 3.1 Go to Authentication
- In Firebase Console, click "Authentication" (left menu)
- Click "Get Started" or "Sign-in method"

### 3.2 Enable Email/Password
- Click "Email/Password" from the list
- Toggle "Enable" to ON
- Toggle "Email link (passwordless sign-in)" to OFF (we don't need this)
- Click "Save"

### 3.3 Verify
- You should see "Email/Password" listed as enabled
- Status should show "Enabled"

---

## 🔄 Step 4: Sync Gradle in Android Studio

### 4.1 Sync Project
- In Android Studio, click `File` menu
- Select `Sync Now`
- Wait for sync to complete (shows "Sync successful" message)

### 4.2 Alternative Method
- Press `Ctrl + Alt + S` (Windows/Linux) or `Cmd + Shift + ;` (Mac)
- Or: `Tools` → `Android` → `Sync Now`

### 4.3 Check for Errors
- Look at the "Sync" tab at bottom
- Should see: "Sync successful"
- If errors, see troubleshooting section below

---

## 🔨 Step 5: Build the Project

### 5.1 Clean Build
- Click `Build` menu
- Select `Clean Project`
- Wait for completion

### 5.2 Build App
- Click `Build` menu
- Select `Build Bundle(s) / APK(s)` → `Build APK(s)`
- Wait for build to complete
- Should see "Build complete" message

### 5.3 Check Build Status
- Look at "Build" tab at bottom
- Should show "BUILD SUCCESSFUL" in green

---

## 🧪 Step 6: Test the App

### 6.1 Run on Emulator
- Click the Run button (green play icon)
- Select Android Virtual Device (or create one)
- Click OK

### 6.2 Wait for App to Load
- App should start and show Splash screen
- After 5 seconds, should navigate to Login screen

### 6.3 Test Sign Up
**Scenario 1: Successful Sign Up**
```
1. Click "Sign Up" button
2. Enter:
   - Name: "John Doe"
   - Email: "john@example.com"
   - Password: "password123"
3. Click "Sign Up" button
4. Should see: "Account created successfully. Please login."
5. Should navigate to Login screen
```

**Scenario 2: Validation Error - Empty Email**
```
1. Click "Sign Up" button
2. Leave Name and Password empty
3. Click "Sign Up" button
4. Should see: "Name cannot be empty"
5. Try again with Name filled but Email empty
6. Should see: "Email cannot be empty"
```

**Scenario 3: Validation Error - Invalid Email**
```
1. Click "Sign Up" button
2. Enter:
   - Name: "Jane Doe"
   - Email: "notanemail" (no @ symbol)
   - Password: "password123"
3. Click "Sign Up" button
4. Should see: "Please enter a valid email address"
```

**Scenario 4: Validation Error - Short Password**
```
1. Click "Sign Up" button
2. Enter:
   - Name: "Bob Smith"
   - Email: "bob@example.com"
   - Password: "pass" (only 4 characters)
3. Click "Sign Up" button
4. Should see: "Password must be at least 6 characters"
```

**Scenario 5: Duplicate Email**
```
1. Click "Sign Up" button
2. Enter:
   - Name: "Another Name"
   - Email: "john@example.com" (same as before)
   - Password: "password123"
3. Click "Sign Up" button
4. Should see: "Email is already registered"
5. Should stay on Sign Up screen
```

### 6.4 Test Login
**Scenario 1: Successful Login**
```
1. On Login screen
2. Enter:
   - Email: "john@example.com"
   - Password: "password123"
3. Click "Login" button
4. Should see: "Login successful"
5. Should navigate to HomeActivity (app home screen)
```

**Scenario 2: Wrong Password**
```
1. On Login screen
2. Enter:
   - Email: "john@example.com"
   - Password: "wrongpassword"
3. Click "Login" button
4. Should see: "Invalid password"
5. Should stay on Login screen
```

**Scenario 3: User Not Found**
```
1. On Login screen
2. Enter:
   - Email: "nonexistent@example.com"
   - Password: "password123"
3. Click "Login" button
4. Should see: "User not found. Please sign up first."
5. Should stay on Login screen
```

**Scenario 4: Empty Email**
```
1. On Login screen
2. Leave Email empty
3. Enter Password: "password123"
4. Click "Login" button
5. Should see: "Email cannot be empty"
6. No API call made
```

**Scenario 5: Empty Password**
```
1. On Login screen
2. Enter Email: "john@example.com"
3. Leave Password empty
4. Click "Login" button
5. Should see: "Password cannot be empty"
6. No API call made
```

### 6.5 Test Navigation
- After successful login, verify HomeActivity opens
- Check that all other pages (Home, Insights, Activity, Settings) work
- Verify UI looks the same (no design changes)

---

## ✅ Verification Checklist

After setup, verify:

**Configuration Files**:
- [ ] google-services.json exists in `app/` directory
- [ ] app/build.gradle.kts has Google Services plugin
- [ ] app/build.gradle.kts has Firebase dependencies
- [ ] gradle/libs.versions.toml has Firebase versions
- [ ] build.gradle.kts (root) has Google Services plugin

**Code Files**:
- [ ] MainActivity.kt has Firebase imports
- [ ] MainActivity.kt initializes Firebase Auth
- [ ] ValidationUtils.kt exists with validation functions
- [ ] No errors in code editor

**Build Status**:
- [ ] Gradle synced successfully
- [ ] Build completed without errors
- [ ] No warnings about missing google-services.json

**Firebase Console**:
- [ ] Project created
- [ ] Android app added
- [ ] Email/Password authentication enabled
- [ ] Authentication rules set to "Allow read/write if request.auth != null"

**App Functionality**:
- [ ] Sign up creates new accounts
- [ ] Login with created accounts works
- [ ] Validation errors show correctly
- [ ] Duplicate email detected
- [ ] Wrong password detected
- [ ] Navigation to HomeActivity works
- [ ] Forgot Password shows toast

---

## 🐛 Troubleshooting

### Issue: "google-services.json not found"
**Error Message**: `Execution failed for task ':app:processDebugGoogleServices'.`

**Solution**:
1. Verify `google-services.json` is in `app/` directory
2. Filename must be exactly `google-services.json` (case-sensitive)
3. File location: `Git_repo_4/app/google-services.json` ✓
4. Not in `Git_repo_4/google-services.json` ✗

### Issue: Gradle Sync Failed
**Error Message**: `Failed to sync Gradle project`

**Solution**:
1. Click `File` → `Invalidate Caches`
2. Select "Invalidate and Restart"
3. Wait for Android Studio to restart
4. Try sync again

### Issue: Firebase Module Not Found
**Error Message**: `Unresolved reference: Firebase` or `Unresolved reference: FirebaseAuth`

**Solution**:
1. Verify gradle dependencies are correct
2. Sync Gradle: `Ctrl + Alt + S`
3. Clean project: `Build` → `Clean Project`
4. Rebuild: `Build` → `Build APK(s)`

### Issue: App Crashes on Login
**Error Message**: Shows Firebase error in Logcat

**Solution**:
1. Check Logcat for exact error message
2. Verify Firebase project settings
3. Ensure Email/Password auth is enabled in Firebase Console
4. Check that package name matches: `com.example.git_repo_4`

### Issue: Email Already Registered Error on New Email
**Cause**: Email might exist in Firebase

**Solution**:
1. Use different email address
2. Or delete user from Firebase Console → Authentication → Users
3. Try sign up again

### Issue: Google Services Plugin Error
**Error Message**: `Plugin with id 'com.google.gms.google-services' not found`

**Solution**:
1. Check `build.gradle.kts` (root) has:
   ```gradle
   plugins {
       id("com.google.gms.google-services") version "4.4.2" apply false
   }
   ```
2. Check `app/build.gradle.kts` has:
   ```gradle
   plugins {
       id("com.google.gms.google-services")
   }
   ```
3. Sync Gradle again

### Issue: Firebase Authentication Not Working
**Cause**: Email/Password auth might not be enabled

**Solution**:
1. Open Firebase Console
2. Go to Authentication → Sign-in method
3. Check Email/Password is "Enabled" (should be green)
4. If not, click and enable it
5. Click Save
6. Rebuild app

### Issue: Validation Not Showing
**Cause**: Code might not be loaded

**Solution**:
1. Clean and rebuild: `Build` → `Clean Project`
2. Rebuild: `Build` → `Build APK(s)`
3. Run again

### Issue: "Invalid password" When Email/Password Are Correct
**Cause**: Firebase might be case-sensitive

**Solution**:
1. During sign up, note exact email (case matters)
2. Use same email during login
3. Or reset password via Firebase Console

---

## 📱 Testing on Real Device

### 1. Connect Android Device
- Connect phone via USB cable
- Enable Developer Mode on phone
- Allow USB debugging when prompted

### 2. Select Device in Android Studio
- Run button → Select device
- Choose your connected phone
- Click OK

### 3. Install and Test
- App will install on device
- All tests work the same as emulator
- Internet connection required

---

## 🚀 Deployment

### Before Publishing

**Checklist**:
- [ ] All tests pass
- [ ] google-services.json in place
- [ ] Firebase authentication enabled
- [ ] No validation errors
- [ ] Sign up creates accounts
- [ ] Login works with created accounts
- [ ] All error messages show correctly
- [ ] No hardcoded credentials
- [ ] UI/UX unchanged
- [ ] App name and icon correct

### Prepare for Release

**Build Release APK**:
1. `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
2. Choose "release" build type (if asked)
3. Select signing key
4. Wait for build to complete

**Sign the APK**:
1. `Build` → `Generate Signed Bundle / APK`
2. Select "APK"
3. Click "Next"
4. Create or select signing key
5. Continue through wizard
6. Select "release" build variant
7. Click "Finish"

---

## 📚 Next Steps (Optional)

### Feature Ideas

- [ ] Password reset functionality
- [ ] Email verification
- [ ] Profile picture upload
- [ ] User profile page
- [ ] Persistent login (auto-login)
- [ ] Remember me checkbox
- [ ] Google/GitHub OAuth
- [ ] Phone number authentication
- [ ] Two-factor authentication

### Security Enhancements

- [ ] Add rate limiting
- [ ] Add failed login tracking
- [ ] Add device verification
- [ ] Add location tracking
- [ ] Add security questions

---

## 📞 Support

### Common Questions

**Q: How do I reset a user's password?**
A: Use `FirebaseAuth.sendPasswordResetEmail(email)`

**Q: How do I delete a user account?**
A: Go to Firebase Console → Authentication → Users → Delete

**Q: Can I use phone authentication?**
A: Yes, use `PhoneAuthProvider` (requires phone verification)

**Q: How do I add social login?**
A: Use `GoogleSignInClient` or similar for OAuth providers

### Documentation Links

- Firebase Auth: https://firebase.google.com/docs/auth
- Android Setup: https://firebase.google.com/docs/android/setup
- Firebase Console: https://console.firebase.google.com/
- Android Studio Docs: https://developer.android.com/studio

---

## ✨ Success!

Once you complete these steps:

✅ Real Firebase Authentication working
✅ Email/Password registration functional
✅ Login with validation
✅ User-friendly error messages
✅ Secure credential storage
✅ Production-ready app
✅ Zero UI changes
✅ Ready to deploy

---

## Quick Troubleshooting Reference

| Error | Solution |
|-------|----------|
| google-services.json not found | Place in app/ directory |
| Gradle sync failed | Invalidate caches and restart |
| Firebase module not found | Ensure google-services.json is present |
| Authentication fails | Check Email/Password enabled in Firebase |
| Validation not working | Clean and rebuild project |
| App crashes | Check Logcat for error details |
| Build fails | Check all dependencies installed |

---

**Status**: Ready to deploy after completing these steps! 🎉

For detailed reference, see:
- `FIREBASE_AUTH_IMPLEMENTATION.md` - Complete technical guide
- `CODE_REFERENCE.md` - Code snippets
- `FIREBASE_QUICK_START.md` - Quick reference

