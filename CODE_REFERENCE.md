# Firebase Authentication - Code Reference

This document shows the exact code implemented in each file.

---

## 1. MainActivity.kt (UPDATED)

**Location**: `app/src/main/java/com/example/git_repo_4/MainActivity.kt`

**Full Implementation**:

```kotlin
package com.example.git_repo_4

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.git_repo_4.ui.theme.Git_repo_4Theme
import com.example.git_repo_4.utils.ValidationUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private enum class AppScreen {
    SPLASH,
    LOGIN,
    SIGN_UP
}

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase Auth
        auth = Firebase.auth

        setContent {
            Git_repo_4Theme {
                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

                when (currentScreen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(onTimeout = { currentScreen = AppScreen.LOGIN })
                    }

                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onLoginClick = { email, password ->
                                performLogin(email, password)
                            },
                            onSignUpClick = { currentScreen = AppScreen.SIGN_UP },
                            onForgotPasswordClick = {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Forgot Password clicked",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    AppScreen.SIGN_UP -> {
                        SignUpScreen(
                            onSignUpClick = { name, email, password ->
                                performSignUp(name, email, password, currentScreen) { currentScreen = AppScreen.LOGIN }
                            },
                            onLoginClick = { currentScreen = AppScreen.LOGIN }
                        )
                    }
                }
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        // Validate inputs
        val emailError = ValidationUtils.validateEmail(email)
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show()
            return
        }

        val passwordError = ValidationUtils.validatePassword(password)
        if (passwordError != null) {
            Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show()
            return
        }

        // Authenticate with Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login success
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed
                    val errorMessage = when {
                        task.exception?.message?.contains("user not found", ignoreCase = true) == true -> {
                            "User not found. Please sign up first."
                        }
                        task.exception?.message?.contains("password is invalid", ignoreCase = true) == true -> {
                            "Invalid password"
                        }
                        else -> {
                            task.exception?.message ?: "Authentication failed"
                        }
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun performSignUp(
        name: String,
        email: String,
        password: String,
        currentScreen: AppScreen,
        navigateToLogin: () -> Unit
    ) {
        // Validate inputs
        val nameError = ValidationUtils.validateName(name)
        if (nameError != null) {
            Toast.makeText(this, nameError, Toast.LENGTH_SHORT).show()
            return
        }

        val emailError = ValidationUtils.validateEmail(email)
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show()
            return
        }

        val passwordError = ValidationUtils.validatePassword(password)
        if (passwordError != null) {
            Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show()
            return
        }

        // Create user with Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Signup success
                    Toast.makeText(
                        this,
                        "Account created successfully. Please log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToLogin()
                } else {
                    // Signup failed
                    val errorMessage = when {
                        task.exception?.message?.contains("email already", ignoreCase = true) == true -> {
                            "Email is already registered"
                        }
                        else -> {
                            task.exception?.message ?: "Registration failed"
                        }
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
```

**Key Points**:
- Initializes Firebase Auth: `auth = Firebase.auth`
- Validates before making API calls
- Specific error handling with user-friendly messages
- Navigates to HomeActivity on success
- Uses Toast for feedback

---

## 2. ValidationUtils.kt (NEW)

**Location**: `app/src/main/java/com/example/git_repo_4/utils/ValidationUtils.kt`

**Full Implementation**:

```kotlin
package com.example.git_repo_4.utils

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"))
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !isValidEmail(email) -> "Please enter a valid email address"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password cannot be empty"
            !isValidPassword(password) -> "Password must be at least 6 characters"
            else -> null
        }
    }

    fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name cannot be empty"
            name.length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }
}
```

**Key Points**:
- Email regex pattern for format validation
- Password minimum 6 characters
- Name minimum 2 characters
- Returns null if valid, error message if invalid
- Case-insensitive comparison

---

## 3. app/build.gradle.kts (UPDATED)

**Location**: `app/build.gradle.kts`

**Changes Made**:

```gradle
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")  // ← ADDED
}

// ... existing android configuration ...

dependencies {
    // ... existing dependencies ...
    
    // Firebase dependencies  ← ADDED
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    
    testImplementation(libs.junit)
    // ... rest of dependencies ...
}
```

**What Changed**:
- Added Google Services plugin: `id("com.google.gms.google-services")`
- Added Firebase BOM: `implementation(platform(libs.firebase.bom))`
- Added Firebase Auth: `implementation(libs.firebase.auth.ktx)`

---

## 4. gradle/libs.versions.toml (UPDATED)

**Location**: `gradle/libs.versions.toml`

**Changes Made**:

```toml
[versions]
# ... existing versions ...
glide = "5.0.5"
firebaseBom = "33.5.0"  # ← ADDED

[libraries]
# ... existing libraries ...
glide = { group = "com.github.bumptech.glide", name = "glide", version.ref = "glide" }
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }  # ← ADDED
firebase-auth-ktx = { group = "com.google.firebase", name = "firebase-auth-ktx" }  # ← ADDED

[plugins]
# ... existing plugins ...
```

**What Changed**:
- Added Firebase BOM version: `firebaseBom = "33.5.0"`
- Added Firebase BOM library definition
- Added Firebase Auth library definition

---

## 5. build.gradle.kts - Root (UPDATED)

**Location**: `build.gradle.kts` (Root directory)

**Changes Made**:

```gradle
// Root build.gradle.kts intentionally left minimal; all configuration is done via settings.gradle.kts and module build files.

plugins {
    id("com.google.gms.google-services") version "4.4.1" apply false  # <- ADDED
}
```

**What Changed**:
- Added Google Services plugin definition
- Version: 4.4.1

---

## Firebase Implementation Flow

### Login Flow
```
User inputs email/password
           ↓
validateEmail() → check format and empty
           ↓
validatePassword() → check length and empty
           ↓
auth.signInWithEmailAndPassword()
           ↓
On Success: Navigate to HomeActivity
On Failure: Show specific error message
```

### Sign Up Flow
```
User inputs name/email/password
           ↓
validateName() → check length and empty
           ↓
validateEmail() → check format and empty
           ↓
validatePassword() → check length and empty
           ↓
auth.createUserWithEmailAndPassword()
           ↓
On Success: Show confirmation + navigate to Login
On Failure: Show specific error message
```

---

## Error Messages Mapping

### Validation Errors
```
Email validation:
  - Empty → "Email cannot be empty"
  - Invalid format → "Please enter a valid email address"

Password validation:
  - Empty → "Password cannot be empty"
  - Too short (< 6 chars) → "Password must be at least 6 characters"

Name validation:
  - Empty → "Name cannot be empty"
  - Too short (< 2 chars) → "Name must be at least 2 characters"
```

### Firebase Auth Errors
```
Login:
  - User not found → "User not found. Please sign up first."
  - Invalid password → "Invalid password"
  - Other → Firebase error message

Sign Up:
  - Email already registered → "Email is already registered"
  - Other → Firebase error message
```

---

## Key Implementation Details

### Firebase Auth Instance
```kotlin
// In MainActivity.kt
private lateinit var auth: FirebaseAuth

override fun onCreate(savedInstanceState: Bundle?) {
    // ...
    auth = Firebase.auth  // Initialize here
}
```

### Login Call
```kotlin
auth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            // User is logged in
        } else {
            // Show error
        }
    }
```

### Sign Up Call
```kotlin
auth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            // User account created
        } else {
            // Show error
        }
    }
```

### Get Current User
```kotlin
val currentUser = Firebase.auth.currentUser
if (currentUser != null) {
    val userEmail = currentUser.email
    val userId = currentUser.uid
}
```

### Sign Out
```kotlin
Firebase.auth.signOut()
```

---

## Testing Code Snippets

### Test Login Success
```text
Manually enter:
Email: test@example.com
Password: password123

Expected result:
Toast: "Login successful"
Navigation: To HomeActivity
```

### Test Login Failure - Wrong Password
```text
Email exists but password is wrong:
Email: test@example.com
Password: wrongpassword

Expected result:
Toast: "Invalid password"
Stay on: LoginScreen
```

### Test Login Failure - User Not Found
```text
Email does not exist:
Email: nonexistent@example.com
Password: password123

Expected result:
Toast: "User not found. Please sign up first."
Stay on: LoginScreen
```

### Test Sign Up Success
```text
Name: John Doe
Email: john.doe@example.com
Password: password123

Expected result:
Toast: "Account created successfully. Please log in."
Navigation: To LoginScreen
You can now log in with the same credentials.
```

### Test Sign Up Failure - Duplicate Email
```text
Email already registered:
Name: Jane Doe
Email: test@example.com (already exists)
Password: password123

Expected result:
Toast: "Email is already registered"
Stay on: SignUpScreen
```

### Test Validation - Empty Email
```text
Email: (empty)
Password: password123

Expected result:
Toast: "Email cannot be empty"
Stay on: LoginScreen
No API call made
```

### Test Validation - Invalid Email Format
```text
Email: notanemail
Password: password123

Expected result:
Toast: "Please enter a valid email address"
Stay on: LoginScreen
No API call made
```

### Test Validation - Short Password
```text
Email: test@example.com
Password: pass (only 4 characters)

Expected result:
Toast: "Password must be at least 6 characters"
Stay on: LoginScreen
No API call made
```

---

## Security Features

✅ **Password Security**
- Minimum 6 characters enforced
- Never logged or displayed
- Sent encrypted to Firebase
- Firebase stores securely (hashed)

✅ **Email Validation**
- Regex pattern matching
- Valid format required
- Duplicate detection

✅ **No Hardcoding**
- No credentials in code
- All handled by Firebase
- Secure by design

✅ **Session Management**
- Firebase handles tokens
- Automatic session management
- Can check current user anytime

---

## Dependencies Summary

- Firebase BOM: 33.5.0 - Version management for Firebase libraries
- Firebase Auth: Latest (via BOM) - Email/password authentication
- Google Services Plugin: 4.4.1 - Build integration with Firebase

## File Sizes (Approximate)

- `MainActivity.kt`: 166 lines - Main activity with auth logic
- `ValidationUtils.kt`: 25 lines - Validation helper
- `app/build.gradle.kts`: 98 lines - App build configuration
- `gradle/libs.versions.toml`: 59 lines - Dependency management
- `build.gradle.kts`: 7 lines - Root configuration

---

## Integration Checklist

- [x] Firebase BOM added to dependencies
- [x] Firebase Auth library added
- [x] Google Services plugin configured
- [x] ValidationUtils created
- [x] MainActivity updated with auth logic
- [x] performLogin() function implemented
- [x] performSignUp() function implemented
- [x] Error handling implemented
- [x] Navigation implemented
- [x] UI unchanged
- [ ] google-services.json added (manual step)
- [ ] Firebase project created (manual step)
- [ ] Email/Password auth enabled (manual step)
- [ ] App tested with real Firebase

---

## Next Steps

1. Download google-services.json from Firebase Console
2. Place in app/ directory
3. Sync Gradle
4. Build and test
5. Deploy!

---

**Complete Reference**: See `FIREBASE_AUTH_IMPLEMENTATION.md` for detailed guide.
**Quick Start**: See `FIREBASE_QUICK_START.md` for rapid setup.
