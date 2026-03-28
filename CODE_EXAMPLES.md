# 📖 Code Examples & Reference Guide

## Quick Reference for Developers

### Using PreferencesManager

#### Initialize
```kotlin
val preferencesManager = PreferencesManager(context)
```

#### Get User Data
```kotlin
val userName = preferencesManager.getUserName()        // "Thanu"
val userEmail = preferencesManager.getUserEmail()      // "thanu.dev@example.com"
val githubUser = preferencesManager.getGithubUsername() // "thanu-dev"
```

#### Set User Data
```kotlin
preferencesManager.setUserName("New Name")
preferencesManager.setUserEmail("new@email.com")
preferencesManager.setGithubUsername("new-github-user")
```

#### Check Settings
```kotlin
val isNotifEnabled = preferencesManager.isPushNotificationsEnabled()
val is2FAEnabled = preferencesManager.isTwoFAEnabled()
val theme = preferencesManager.getThemeMode() // "light" or "dark"
```

#### Save Preferences
```kotlin
preferencesManager.setPushNotificationsEnabled(true)
preferencesManager.setTwoFAEnabled(false)
preferencesManager.setThemeMode("dark")
```

#### Clear All Data (Logout)
```kotlin
preferencesManager.clearAllData()
```

---

## Navigation Examples

### Open Profile Activity
```kotlin
val intent = Intent(requireContext(), ProfileActivity::class.java)
startActivity(intent)
```

### Open Security Activity
```kotlin
val intent = Intent(requireContext(), SecurityActivity::class.java)
startActivity(intent)
```

### Open GitHub Profile
```kotlin
val githubUsername = preferencesManager.getGithubUsername()
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/$githubUsername"))
startActivity(intent)
```

### Sign Out
```kotlin
preferencesManager.clearAllData()
val intent = Intent(requireContext(), LoginActivity::class.java)
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
startActivity(intent)
requireActivity().finish()
```

---

## UI Component Examples

### Find TextViews Dynamically
```kotlin
val allViews = mutableListOf<View>()
fun collectAllViews(v: View) {
    allViews.add(v)
    if (v is ViewGroup) {
        for (i in 0 until v.childCount) {
            collectAllViews(v.getChildAt(i))
        }
    }
}
collectAllViews(view)

for (v in allViews) {
    if (v is TextView && v.text == "Target Text") {
        v.text = "New Text"
    }
}
```

### Toggle Switch Listener
```kotlin
val switchNotif: SwitchMaterial = view.findViewById(R.id.switchPushNotifications)
switchNotif.setOnCheckedChangeListener { _, isChecked ->
    preferencesManager.setPushNotificationsEnabled(isChecked)
    Snackbar.make(view, "Notifications ${if (isChecked) "enabled" else "disabled"}", 
                  Snackbar.LENGTH_SHORT).show()
}
```

### Material Card Click Listener
```kotlin
val card: MaterialCardView = view.findViewById(R.id.cardProfile)
card.setOnClickListener {
    val intent = Intent(requireContext(), ProfileActivity::class.java)
    startActivity(intent)
}
```

---

## Validation Examples

### Email Validation
```kotlin
fun isValidEmail(email: String): Boolean {
    return email.contains("@") && email.contains(".")
}

// Usage
val email = etEmail.text.toString().trim()
if (!isValidEmail(email)) {
    Snackbar.make(view, "Invalid email format", Snackbar.LENGTH_SHORT).show()
    return
}
```

### Password Validation
```kotlin
fun isValidPassword(password: String, confirm: String): Boolean {
    return password.length >= 6 && password == confirm
}

// Usage
val password = etPassword.text.toString().trim()
val confirm = etConfirm.text.toString().trim()
if (!isValidPassword(password, confirm)) {
    Snackbar.make(view, "Password must be 6+ chars and match", Snackbar.LENGTH_SHORT).show()
    return
}
```

### Non-Empty Validation
```kotlin
fun isEmpty(text: String): Boolean {
    return text.trim().isEmpty()
}

// Usage
val name = etName.text.toString().trim()
if (isEmpty(name)) {
    Snackbar.make(view, "Name cannot be empty", Snackbar.LENGTH_SHORT).show()
    return
}
```

---

## Snackbar Examples

### Success Message
```kotlin
Snackbar.make(view, "Profile updated successfully", Snackbar.LENGTH_SHORT).show()
```

### Error Message
```kotlin
Snackbar.make(view, "Please enter valid email", Snackbar.LENGTH_SHORT).show()
```

### Action Message
```kotlin
Snackbar.make(view, "Notifications enabled", Snackbar.LENGTH_SHORT)
    .setAction("UNDO") {
        preferencesManager.setPushNotificationsEnabled(false)
    }
    .show()
```

---

## Common Patterns

### Initialize View & Preferences
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_profile)
    
    preferencesManager = PreferencesManager(this)
    etName = findViewById(R.id.etProfileName)
    etEmail = findViewById(R.id.etProfileEmail)
    btnSave = findViewById(R.id.btnSaveProfile)
    
    loadUserData()
    setupClickListeners()
}

private fun loadUserData() {
    etName.setText(preferencesManager.getUserName())
    etEmail.setText(preferencesManager.getUserEmail())
}
```

### Save & Provide Feedback
```kotlin
private fun saveData() {
    val name = etName.text.toString().trim()
    
    // Validate
    if (name.isEmpty()) {
        showError("Name cannot be empty")
        return
    }
    
    // Save
    preferencesManager.setUserName(name)
    
    // Feedback
    Snackbar.make(view, "Saved successfully", Snackbar.LENGTH_SHORT).show()
    
    // Return
    finish()
}
```

### Handle On Resume
```kotlin
override fun onResume() {
    super.onResume()
    // Refresh user data in case it was updated in another activity
    view?.let { updateUserProfile(it) }
}
```

---

## SharedPreferences Direct Access

### Alternative to PreferencesManager
```kotlin
val sharedPref = context.getSharedPreferences("repo_pulse_prefs", Context.MODE_PRIVATE)

// Read
val name = sharedPref.getString("user_name", "Thanu") ?: "Thanu"

// Write
with(sharedPref.edit()) {
    putString("user_name", "New Name")
    apply()
}

// Clear
sharedPref.edit().clear().apply()
```

---

## Best Practices

### ✅ DO
```kotlin
// Use PreferencesManager for consistency
val manager = PreferencesManager(context)
val name = manager.getUserName()

// Validate input before saving
if (input.isNotEmpty()) {
    manager.setUserName(input)
}

// Provide user feedback
Snackbar.make(view, "Saved", Snackbar.LENGTH_SHORT).show()

// Use proper flag combinations for navigation
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
```

### ❌ DON'T
```kotlin
// Don't access SharedPreferences directly everywhere
val prefs = context.getSharedPreferences("any_name", Context.MODE_PRIVATE)

// Don't save without validation
prefs.edit().putString("key", input).apply() // Input not checked!

// Don't silently fail
try {
    // operation
} catch(e: Exception) {
    // Silent fail - bad!
}

// Don't use wrong flags
intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
```

---

## Testing Code

### Test User Data Persistence
```kotlin
fun testDataPersistence() {
    val manager = PreferencesManager(context)
    
    // Save data
    manager.setUserName("Test User")
    manager.setUserEmail("test@test.com")
    
    // Create new instance (simulates app restart)
    val manager2 = PreferencesManager(context)
    
    // Verify data persists
    assert(manager2.getUserName() == "Test User")
    assert(manager2.getUserEmail() == "test@test.com")
}
```

### Test Data Clear
```kotlin
fun testDataClear() {
    val manager = PreferencesManager(context)
    
    // Set data
    manager.setUserName("Test")
    manager.setPushNotificationsEnabled(true)
    
    // Clear
    manager.clearAllData()
    
    // Verify cleared (returns defaults)
    assert(manager.getUserName() == "Thanu")
    assert(manager.isPushNotificationsEnabled() == true) // true is default
}
```

---

## Error Handling

### Try-Catch Pattern
```kotlin
private fun saveProfile() {
    try {
        val name = etName.text.toString().trim()
        if (name.isEmpty()) throw IllegalArgumentException("Name required")
        
        preferencesManager.setUserName(name)
        Snackbar.make(view, "Saved", Snackbar.LENGTH_SHORT).show()
    } catch (e: IllegalArgumentException) {
        Snackbar.make(view, e.message ?: "Error", Snackbar.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Snackbar.make(view, "Unexpected error", Snackbar.LENGTH_LONG).show()
    }
}
```

### Null Safety
```kotlin
val userName = preferencesManager.getUserName() ?: "Unknown"
val userEmail = preferencesManager.getUserEmail() ?: "N/A"

// Or with let
preferencesManager.getUserName().let { name ->
    textView.text = name
}
```

---

## File Structure Reference

```
app/
├── src/main/
│   ├── java/com/example/git_repo_4/
│   │   ├── SettingsFragment.kt ✅ (Updated)
│   │   ├── ProfileActivity.kt ✅ (NEW)
│   │   ├── SecurityActivity.kt ✅ (NEW)
│   │   ├── LoginActivity.kt
│   │   ├── HomeActivity.kt
│   │   └── utils/
│   │       ├── PreferencesManager.kt ✅ (NEW)
│   │       └── ... (other utilities)
│   └── res/
│       └── layout/
│           ├── fragment_settings.xml (NO CHANGES)
│           ├── activity_profile.xml ✅ (NEW)
│           ├── activity_security.xml ✅ (NEW)
│           └── ... (other layouts)
└── AndroidManifest.xml ✅ (Updated)
```

---

**Last Updated**: March 17, 2026  
**Status**: Complete & Ready for Development

