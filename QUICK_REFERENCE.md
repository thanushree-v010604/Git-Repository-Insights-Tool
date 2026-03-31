# Quick Reference Guide - User Data Implementation

## 🎯 What Was Changed?

The Settings page now shows **real user data** instead of hardcoded values.

### Before
```
Settings Page:
├─ Name: "Thanu" ← hardcoded
├─ Email: "thanu.dev@example.com" ← hardcoded
└─ Avatar: "T" ← from hardcoded name
```

### After
```
Settings Page:
├─ Name: "John Doe" ← from Firebase
├─ Email: "john@example.com" ← from Firebase
└─ Avatar: "J" ← from real name
```

---

## 📦 Three Components Working Together

### 1️⃣ **MainActivity** (Authentication Handler)
**What it does**: When user logs in/signs up, extract their data from Firebase and save it

```kotlin
// After login
val currentUser = firebaseAuth.currentUser
preferencesManager.setUserEmail(currentUser.email)
preferencesManager.setUserName(currentUser.displayName)
```

### 2️⃣ **PreferencesManager** (Data Storage)
**What it does**: Store and retrieve user data from SharedPreferences

```kotlin
// Save
preferencesManager.setUserName("John Doe")
preferencesManager.setUserEmail("john@example.com")

// Retrieve
val name = preferencesManager.getUserName()
val email = preferencesManager.getUserEmail()
```

### 3️⃣ **SettingsFragment** (UI Display)
**What it does**: Read saved data and display it on screen

```kotlin
private fun bindProfile(view: View) {
    val name = preferencesManager.getUserName()
    val email = preferencesManager.getUserEmail()
    view.findViewById<TextView>(R.id.tvUserName).text = name
    view.findViewById<TextView>(R.id.tvUserEmail).text = email
}
```

---

## 🔄 Complete Data Flow

```
User Input (Signup/Login)
         ↓
Firebase Authentication
         ↓
Firebase returns User object
         ↓
MainActivity extracts name & email
         ↓
PreferencesManager saves to SharedPreferences
         ↓
SettingsFragment retrieves data
         ↓
UI displays dynamic values
         ↓
✅ Real user data shown
```

---

## 📊 Implementation Summary

| Component | File | Change |
|-----------|------|--------|
| **Authentication** | MainActivity.kt | Added user data saving in 4 places |
| **Storage** | PreferencesManager.kt | Removed hardcoded defaults |
| **Display** | SettingsFragment.kt | Updated bindProfile() method |
| **Result** | Settings UI | Shows real user data |

---

## ✅ Features Implemented

✅ **Email/Password Signup** - Saves name and email  
✅ **Email/Password Login** - Saves email and derives name  
✅ **Google Sign-In** - Saves Google profile data  
✅ **App Restart** - Data persists across app restarts  
✅ **Logout** - Clears all user data  
✅ **Fallbacks** - Shows "User"/"Not provided" if no data  

---

## 🧪 Test It Now

1. **Sign up** with name "John Doe" and email "john@example.com"
2. Open **Settings** → Should show "John Doe" and "john@example.com"
3. **Logout** → Settings shows "User" and "Not provided"
4. **Login** again → Data reappears
5. **Close and reopen** app → Data persists (if still logged in)

---

## 📝 Key Takeaways

- **User data comes from Firebase**, not hardcoded
- **Data is saved to SharedPreferences** on every login/signup
- **Settings page displays from SharedPreferences**, not from UI constants
- **Data persists across app restarts** as long as user is logged in
- **Empty SharedPreferences means no user logged in**

---

**Status**: ✅ Complete  
**Date**: March 30, 2026

