# Dynamic User Data Implementation - Settings Page

## 📋 Overview
This implementation replaces hardcoded user data in the Settings page with actual user information from Firebase Authentication. User name and email are now dynamically fetched and displayed after login/signup.

---

## 🔧 Changes Made

### 1. **MainActivity.kt** - Authentication & Data Persistence

#### Added Import
```kotlin
import com.example.git_repo_4.utils.PreferencesManager
```

#### Added Class Property
```kotlin
private lateinit var preferencesManager: PreferencesManager
```

#### Initialize in onCreate()
```kotlin
sessionManager = SessionManager(this)
preferencesManager = PreferencesManager(this)  // NEW
```

#### ✅ **Feature 1: Sync User Data on App Startup**
**Location**: `MainActivity.kt` - SplashScreen onTimeout callback

When the app starts and detects a logged-in user, it automatically syncs their data from Firebase to SharedPreferences:

```kotlin
SplashScreen(
    onTimeout = {
        val currentUser = auth?.currentUser
        if (currentUser != null) {
            // Sync user data from Firebase to PreferencesManager
            preferencesManager.setUserEmail(currentUser.email ?: "")
            val displayName = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "User"
            preferencesManager.setUserName(displayName)
            
            sessionManager.saveLogin(currentUser.email)
            navigateToHome()
        } else {
            sessionManager.logout()
            currentScreen = AppScreen.LOGIN
        }
    }
)
```

**Benefits**:
- User data persists after app restart
- Fallback to email prefix if display name is not set
- Handles null cases gracefully

---

#### ✅ **Feature 2: Save User Data During Login**
**Location**: `MainActivity.kt` - performLogin() method

When user logs in with email/password:

```kotlin
firebaseAuth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            // Get the current user
            val currentUser = firebaseAuth.currentUser
            
            // Save user data to PreferencesManager
            if (currentUser != null) {
                preferencesManager.setUserEmail(currentUser.email ?: email)
                // Use display name if available, otherwise use email prefix
                val displayName = currentUser.displayName ?: email.substringBefore("@")
                preferencesManager.setUserName(displayName)
            }
            
            sessionManager.saveLogin(email)
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }
    }
```

**Features**:
- Extracts email and display name from Firebase user object
- Falls back to email prefix if display name unavailable
- Stores data in SharedPreferences via PreferencesManager

---

#### ✅ **Feature 3: Save User Data During Signup**
**Location**: `MainActivity.kt` - performSignUp() method

When user creates a new account:

```kotlin
firebaseAuth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            // Signup success
            val currentUser = firebaseAuth.currentUser
            
            // Save user data to PreferencesManager
            if (currentUser != null) {
                preferencesManager.setUserEmail(email)
                preferencesManager.setUserName(name)  // From signup form
            }
            
            Toast.makeText(
                this,
                "Account created successfully. Please log in.",
                Toast.LENGTH_SHORT
            ).show()
            navigateToLogin()
        }
    }
```

**Features**:
- Saves the name entered during signup
- Saves the email used for registration
- Data available immediately upon successful signup

---

#### ✅ **Feature 4: Save User Data During Google Sign-In**
**Location**: `MainActivity.kt` - firebaseAuthWithGoogle() method

When user signs in with Google:

```kotlin
val credential = GoogleAuthProvider.getCredential(idToken, null)
firebaseAuth.signInWithCredential(credential)
    .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            val currentUser = firebaseAuth.currentUser
            val email = currentUser?.email ?: ""
            val displayName = currentUser?.displayName ?: email.substringBefore("@")
            
            // Save user data to PreferencesManager
            preferencesManager.setUserEmail(email)
            preferencesManager.setUserName(displayName)
            
            sessionManager.saveLogin(email)
            Toast.makeText(this, "Google sign-in successful", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }
    }
```

**Features**:
- Fetches Google profile information
- Stores Google-provided name and email
- Handles missing display name gracefully

---

### 2. **PreferencesManager.kt** - Data Storage

#### Removed Hardcoded Defaults
**Before**:
```kotlin
fun getUserName(): String {
    return sharedPreferences.getString(KEY_USER_NAME, "Thanu") ?: "Thanu"
}

fun getUserEmail(): String {
    return sharedPreferences.getString(KEY_USER_EMAIL, "thanu.dev@example.com")
        ?: "thanu.dev@example.com"
}
```

**After**:
```kotlin
fun getUserName(): String {
    return sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
}

fun getUserEmail(): String {
    return sharedPreferences.getString(KEY_USER_EMAIL, "")
        ?: ""
}
```

**Reason**: 
- Empty strings allow SettingsFragment to detect when user data is not set
- Enables proper fallback UI handling in settings page

---

### 3. **SettingsFragment.kt** - UI Display

#### Updated bindProfile() Method
```kotlin
private fun bindProfile(view: View) {
    val name = preferencesManager.getUserName().takeIf { it.isNotBlank() } ?: "User"
    val email = preferencesManager.getUserEmail().takeIf { it.isNotBlank() } ?: "Not provided"
    
    view.findViewById<TextView>(R.id.tvUserName)?.text = name
    view.findViewById<TextView>(R.id.tvUserEmail)?.text = email

    val githubUsername = preferencesManager.getGithubUsername()
    view.findViewById<TextView>(R.id.tvGithubSubtitle)?.text = "Connected: @$githubUsername"

    applyAvatar(view, name)
}
```

**Features**:
- Displays actual user name and email from SharedPreferences
- Falls back to "User" / "Not provided" if data is empty
- Still calls `applyAvatar()` to generate initial letter from name
- Automatically refreshed on fragment resume (via `onResume()`)

**Layout IDs Used**:
- `R.id.tvUserName` - Displays user's full name
- `R.id.tvUserEmail` - Displays user's email address

---

## 🔄 Data Flow Diagram

```
User Signs Up/Logs In
         ↓
Firebase Authenticates User
         ↓
MainActivity captures Firebase User object
         ↓
Extract: name, email, displayName
         ↓
PreferencesManager saves to SharedPreferences
         ↓
SettingsFragment reads from SharedPreferences
         ↓
UI displays dynamic values
```

---

## 💾 Data Storage Structure

### SharedPreferences Keys (PreferencesManager)
```
PREFS_NAME = "repo_pulse_prefs"

KEY_USER_NAME = "user_name"              // User's full name
KEY_USER_EMAIL = "user_email"            // User's email address
KEY_GITHUB_USERNAME = "github_username"  // (existing)
KEY_PROFILE_IMAGE_URI = "profile_image_uri" // (existing)
...other keys
```

### Example Data After Login
```
SharedPreferences ("repo_pulse_prefs"):
{
  "user_name": "John Doe",
  "user_email": "john.doe@example.com",
  "github_username": "john-dev",
  "isLoggedIn": true,
  ...
}
```

---

## 🎯 User Scenarios

### Scenario 1: New User Signs Up
1. User enters Name, Email, Password in SignUpScreen
2. Clicks "Sign Up"
3. MainActivity.performSignUp() is called
4. Firebase creates account → Returns user object
5. **PreferencesManager saves**: name, email
6. User redirected to login screen
7. After login, Settings page shows: John Doe | john@example.com

### Scenario 2: Existing User Logs In
1. User enters Email, Password in LoginScreen
2. Clicks "Login"
3. MainActivity.performLogin() is called
4. Firebase authenticates → Returns user object
5. **PreferencesManager saves**: email, display name (or email prefix)
6. User navigated to HomeActivity
7. Settings page displays saved user data

### Scenario 3: User Signs In with Google
1. User taps "Sign in with Google"
2. Google OAuth flow completes
3. Firebase receives Google token → Authenticates
4. **PreferencesManager saves**: Google email, Google display name
5. User navigated to HomeActivity
6. Settings page shows: Google Profile Name | Google Email

### Scenario 4: User Restarts App (Still Logged In)
1. User closes and reopens the app
2. SplashScreen checks: `auth?.currentUser`
3. Finds logged-in Firebase user
4. **PreferencesManager syncs** user data from Firebase
5. Settings page displays saved data without re-login

### Scenario 5: User Never Logged In
1. App starts
2. No current Firebase user found
3. PreferencesManager returns empty strings
4. Settings page displays: "User" | "Not provided"
5. User can navigate to login

---

## ✨ Edge Cases Handled

| Case | Behavior |
|------|----------|
| User has no display name | Uses email prefix (part before @) |
| Email is null from Firebase | Falls back to provided email parameter |
| SharedPreferences is empty | Returns empty string → UI shows "User" / "Not provided" |
| User logs out | `preferencesManager.clearAllData()` clears all stored data |
| App crashes and restarts | Data persists in SharedPreferences; re-synced from Firebase |
| User deleted Firebase account | Empty SharedPreferences after logout |

---

## 🔐 Security Considerations

1. **No hardcoded credentials**: All user data comes from Firebase Authentication
2. **Local storage only**: Data stored in app's private SharedPreferences
3. **Session-based**: SessionManager tracks login state
4. **Secure logout**: `clearAllData()` removes all sensitive information on sign out
5. **Firebase validation**: All authentication handled by Firebase (passwords never stored locally)

---

## 📱 UI Impact

### Before Implementation
```
Settings Page:
├─ Name: "Thanu" (hardcoded)
├─ Email: "thanu.dev@example.com" (hardcoded)
└─ GitHub: "@thanu-dev" (hardcoded)
```

### After Implementation
```
Settings Page:
├─ Name: "John Doe" (from Firebase)
├─ Email: "john.doe@example.com" (from Firebase)
└─ GitHub: "@john-dev" (user can set)
```

---

## ✅ Testing Checklist

- [ ] **Signup Flow**: Create account → Check Settings page shows correct name/email
- [ ] **Login Flow**: Log in with email/password → Check Settings displays correct data
- [ ] **Google Sign-In**: Sign in with Google → Check Settings shows Google profile
- [ ] **App Restart**: Log in → Close app → Reopen → Verify data persists
- [ ] **Logout**: Sign out → Verify Settings shows "User" / "Not provided"
- [ ] **Name with Special Characters**: Sign up with special characters → Verify displays correctly
- [ ] **Long Email Address**: Test with long email → Verify truncation/display
- [ ] **Profile Photo**: Upload photo → Verify initial letter still shows with avatar

---

## 🚀 Future Enhancements

1. **Profile Editing**: Allow users to update name/email in Settings
2. **Firebase Profile Updates**: Sync changes to Firebase (updateProfile())
3. **User Preferences**: Store additional profile info (phone, bio, etc.)
4. **Multiple Accounts**: Support switching between accounts
5. **Offline Support**: Cache user data for offline viewing

---

## 📚 Related Files

| File | Purpose |
|------|---------|
| MainActivity.kt | Authentication & data persistence |
| PreferencesManager.kt | SharedPreferences wrapper for user data |
| SettingsFragment.kt | Display user data in settings UI |
| LoginScreen.kt | Compose UI for email/password login |
| SignUpScreen.kt | Compose UI for account creation |
| SessionManager.kt | Manages login session state |

---

## 🔗 Key Methods Reference

### MainActivity.kt
- `performLogin()` - Email/password authentication + data save
- `performSignUp()` - Account creation + data save
- `firebaseAuthWithGoogle()` - Google sign-in + data save
- `SplashScreen.onTimeout` - App startup sync

### PreferencesManager.kt
- `setUserName(name: String)` - Save username
- `getUserName()` - Retrieve username
- `setUserEmail(email: String)` - Save email
- `getUserEmail()` - Retrieve email
- `clearAllData()` - Clear all stored data on logout

### SettingsFragment.kt
- `bindProfile(view: View)` - Display user data on screen
- `onResume()` - Refresh data when settings page is shown

---

## 📝 Notes

- Data is **NOT** encrypted (consider for sensitive data)
- SharedPreferences is **app-private** (not accessible by other apps)
- For **sensitive data**, consider Android Keystore
- Regular backups recommended for user data
- GDPR compliance: Ensure user can delete their data

---

**Implementation Date**: March 30, 2026  
**Status**: ✅ Complete  
**Testing**: Ready for QA

