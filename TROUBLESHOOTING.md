# Troubleshooting Guide - User Data Implementation

## ❌ Problem: Settings page still shows hardcoded values

### Root Causes & Solutions

#### 1. **User Data Not Being Saved**
```kotlin
// Check: Is performLogin() saving data?
✓ CORRECT:
firebaseAuth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val currentUser = firebaseAuth.currentUser
            preferencesManager.setUserEmail(currentUser.email)
            preferencesManager.setUserName(currentUser.displayName)
        }
    }

✗ WRONG:
firebaseAuth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            navigateToHome()  // Missing: data not saved!
        }
    }
```

**Fix**: Ensure you're calling `preferencesManager.setUserName()` and `preferencesManager.setUserEmail()` before navigating.

---

#### 2. **PreferencesManager Returning Empty String**
```kotlin
// Check: getters in PreferencesManager
✓ CORRECT:
fun getUserName(): String {
    return sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
}

✗ WRONG:
fun getUserName(): String {
    return sharedPreferences.getString(KEY_USER_NAME, "DefaultName") ?: "DefaultName"
}
```

**Fix**: Use empty string as default, not a hardcoded name.

---

#### 3. **SettingsFragment Not Reading Saved Data**
```kotlin
// Check: bindProfile() method
✓ CORRECT:
private fun bindProfile(view: View) {
    val name = preferencesManager.getUserName()
    view.findViewById<TextView>(R.id.tvUserName).text = name
}

✗ WRONG:
private fun bindProfile(view: View) {
    view.findViewById<TextView>(R.id.tvUserName).text = "Thanu"  // Hardcoded!
}
```

**Fix**: Read from PreferencesManager, not hardcoded strings.

---

### Debug Steps

```kotlin
// In SettingsFragment.bindProfile(), add logging:
private fun bindProfile(view: View) {
    val name = preferencesManager.getUserName()
    val email = preferencesManager.getUserEmail()
    
    Log.d("Settings", "Username from prefs: '$name'")  // Check output
    Log.d("Settings", "Email from prefs: '$email'")
    
    view.findViewById<TextView>(R.id.tvUserName).text = name
    view.findViewById<TextView>(R.id.tvUserEmail).text = email
}
```

**Expected Output** (after login):
```
D/Settings: Username from prefs: 'John Doe'
D/Settings: Email from prefs: 'john@example.com'
```

**Problematic Output**:
```
D/Settings: Username from prefs: ''  ← Empty! Check login flow
D/Settings: Email from prefs: ''
```

---

## ❌ Problem: Settings shows "User" instead of real name

### Root Causes & Solutions

#### 1. **Firebase User Has No Display Name**
```kotlin
// The issue:
val displayName = currentUser.displayName  // Returns null if not set
preferencesManager.setUserName(displayName)  // Saves null → becomes empty

// The fix:
val displayName = currentUser.displayName ?: email.substringBefore("@")
preferencesManager.setUserName(displayName)  // Saves fallback
```

**Solution**: Always provide a fallback (email prefix).

#### 2. **Signup Form Name Not Being Used**
```kotlin
// In MainActivity.performSignUp():
✗ WRONG:
if (task.isSuccessful) {
    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
    navigateToLogin()  // Name from form is lost!
}

✓ CORRECT:
if (task.isSuccessful) {
    preferencesManager.setUserName(name)  // Save the form name
    preferencesManager.setUserEmail(email)
    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
    navigateToLogin()
}
```

**Solution**: Save the name from signup form immediately.

---

## ❌ Problem: Data disappears after app restart

### Root Causes & Solutions

#### 1. **Logout Clears Data**
```kotlin
// In SettingsFragment.setupSignOut():
preferencesManager.clearAllData()  // This removes everything!
```

**This is expected behavior**. After logout, data should be empty.

#### 2. **Data Not Being Synced on App Restart**
```kotlin
// In MainActivity.SplashScreen:
✗ WRONG:
SplashScreen(
    onTimeout = {
        val currentUser = auth?.currentUser
        if (currentUser != null) {
            sessionManager.saveLogin(currentUser.email)
            navigateToHome()  // Missing: data not synced!
        }
    }
)

✓ CORRECT:
SplashScreen(
    onTimeout = {
        val currentUser = auth?.currentUser
        if (currentUser != null) {
            preferencesManager.setUserEmail(currentUser.email)  // Sync!
            preferencesManager.setUserName(currentUser.displayName ?: ...)
            sessionManager.saveLogin(currentUser.email)
            navigateToHome()
        }
    }
)
```

**Solution**: Sync Firebase data to PreferencesManager on app startup.

#### 3. **Firebase Session Expired**
If Firebase session expired, `auth?.currentUser` returns null.
→ Settings shows "User"/"Not provided"
→ This is expected - user needs to log in again

---

## ❌ Problem: Different values in different screens

### Root Causes & Solutions

#### 1. **Reading from Different Sources**
```kotlin
// SettingsFragment reads from PreferencesManager:
val name = preferencesManager.getUserName()

// But somewhere else reads from Firebase:
val name = firebaseAuth.currentUser?.displayName

// These might be different!
```

**Solution**: Always read from PreferencesManager, not Firebase directly.

#### 2. **Data Not Updated After Profile Change**
```kotlin
// User changes their name in ProfileActivity
// But SettingsFragment still shows old name

// Fix: Refresh in onResume()
override fun onResume() {
    super.onResume()
    view?.let { bindProfile(it) }  // Re-read from storage
}
```

---

## ❌ Problem: Null Pointer Exception in Settings

### Root Causes & Solutions

#### 1. **Null Email/Name**
```kotlin
✗ WRONG:
val name = preferencesManager.getUserName()
view.findViewById<TextView>(R.id.tvUserName).text = name  // NPE if name is null

✓ CORRECT:
val name = preferencesManager.getUserName().takeIf { it.isNotBlank() } ?: "User"
view.findViewById<TextView>(R.id.tvUserName).text = name  // Always has value
```

**Solution**: Use `takeIf` and provide fallback value.

#### 2. **Null View Reference**
```kotlin
✗ WRONG:
val view = view  // Can be null!
view.findViewById<TextView>(R.id.tvUserName).text = name  // NPE!

✓ CORRECT:
view?.let { v ->
    v.findViewById<TextView>(R.id.tvUserName).text = name
}
```

**Solution**: Use safe call operator `?.` or let binding.

---

## 🔍 Verification Checklist

Run through these steps to verify the implementation is correct:

### Step 1: Check MainActivity Imports
```kotlin
✓ import com.example.git_repo_4.utils.PreferencesManager
```

### Step 2: Check MainActivity Properties
```kotlin
✓ private lateinit var preferencesManager: PreferencesManager
```

### Step 3: Check MainActivity Initialization
```kotlin
✓ preferencesManager = PreferencesManager(this)  // In onCreate()
```

### Step 4: Check performLogin()
```kotlin
✓ preferencesManager.setUserEmail(currentUser.email)
✓ preferencesManager.setUserName(displayName)
```

### Step 5: Check performSignUp()
```kotlin
✓ preferencesManager.setUserEmail(email)
✓ preferencesManager.setUserName(name)
```

### Step 6: Check firebaseAuthWithGoogle()
```kotlin
✓ preferencesManager.setUserEmail(email)
✓ preferencesManager.setUserName(displayName)
```

### Step 7: Check SplashScreen
```kotlin
✓ preferencesManager.setUserEmail(...)
✓ preferencesManager.setUserName(...)
```

### Step 8: Check PreferencesManager
```kotlin
✓ getUserName() returns ""  // Not "Thanu"
✓ getUserEmail() returns ""  // Not "thanu@..."
```

### Step 9: Check SettingsFragment
```kotlin
✓ val name = preferencesManager.getUserName()
✓ val email = preferencesManager.getUserEmail()
✓ view.findViewById<TextView>(R.id.tvUserName).text = name
✓ view.findViewById<TextView>(R.id.tvUserEmail).text = email
```

### Step 10: Run Test
```
1. Sign up with name "Test User" and email "test@example.com"
2. Open Settings
3. Verify shows: "Test User" and "test@example.com"
4. Logout
5. Verify shows: "User" and "Not provided"
✓ ALL TESTS PASS
```

---

## 🛠️ Common Fixes

### Fix 1: Hardcoded Value Still Showing
**Find**: `text = "Thanu"` or `text = "thanu@..."`  
**Replace With**: `text = preferencesManager.getUserName()`  
**Files to Check**: 
- SettingsFragment.kt
- fragment_settings.xml (remove hardcoded android:text)

### Fix 2: Empty Preferences Manager
**Problem**: `getUserName()` returns empty string  
**Check**: Is `setUserName()` being called during login?  
**Add Logging**:
```kotlin
fun setUserName(name: String) {
    Log.d("PreferencesManager", "Saving name: '$name'")
    sharedPreferences.edit { putString(KEY_USER_NAME, name) }
}

fun getUserName(): String {
    val result = sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
    Log.d("PreferencesManager", "Retrieved name: '$result'")
    return result
}
```

### Fix 3: Data Lost After App Restart
**Check**: Is SplashScreen syncing data?  
**Add Code**:
```kotlin
if (currentUser != null) {
    // MUST sync data:
    preferencesManager.setUserEmail(currentUser.email ?: "")
    preferencesManager.setUserName(currentUser.displayName ?: "User")
}
```

### Fix 4: Wrong Display Name
**Check**: `displayName` can be null  
**Use Fallback**:
```kotlin
val displayName = currentUser.displayName ?: email.substringBefore("@") ?: "User"
```

---

## 📞 Getting Help

### If implementation still doesn't work:

1. **Run adb logcat** to see logs:
   ```bash
   adb logcat | grep -E "PreferencesManager|Settings"
   ```

2. **Add debug logging** in PreferencesManager and SettingsFragment

3. **Check SharedPreferences** content:
   ```kotlin
   // In Android Studio: Device File Explorer
   // Navigate to: data/data/com.example.git_repo_4/shared_prefs/
   // Look for: repo_pulse_prefs.xml
   ```

4. **Verify Firebase is working**:
   ```kotlin
   Log.d("Firebase", "Current user: ${firebaseAuth.currentUser?.email}")
   ```

5. **Check all 4 authentication points**:
   - ✓ performLogin()
   - ✓ performSignUp()  
   - ✓ firebaseAuthWithGoogle()
   - ✓ SplashScreen.onTimeout()

---

## ✅ Final Validation

After making fixes, verify:

| Test | Expected | Status |
|------|----------|--------|
| Sign up | Settings shows form name/email | ✓ |
| Email login | Settings shows saved data | ✓ |
| Google login | Settings shows Google profile | ✓ |
| App restart | Data persists | ✓ |
| Logout | Shows "User"/"Not provided" | ✓ |
| Rapid login/logout | No crashes | ✓ |

---

**Last Updated**: March 30, 2026  
**Version**: 1.0  
**Status**: Troubleshooting Guide Complete

