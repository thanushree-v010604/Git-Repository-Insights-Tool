# Quick Debugging Reference Card

## 🚀 Quick Setup

1. **Create local.properties** in project root:
   ```properties
   github.token=ghp_YOUR_TOKEN_HERE
   ```

2. **Sync Gradle:** File → Sync Now

3. **Rebuild:** Build → Rebuild Project

4. **Run:** Build → Run 'app'

---

## 📊 Logcat Filter Quick Guide

### Check Token Loading
```
Filter: TOKEN_DEBUG
Expected: "Token loaded: YES (40 chars)"
Problem? Check local.properties
```

### Check API Calls
```
Filter: API_RESPONSE
Expected: "← HTTP 200"
403? Check rate limit headers below
```

### Check Rate Limits
```
Filter: API_RESPONSE (search for "403")
Shows: X-RateLimit-Remaining, X-RateLimit-Limit, X-RateLimit-Reset
0 remaining? Wait until reset time
```

### Check Secondary APIs
```
Filters: API_CONTRIBUTORS, API_LANGUAGES, API_COMMITS, API_README
Each shows: success count or error reason
```

---

## 🔍 Common Issues & Fixes

| Issue | Log Tag | Fix |
|-------|---------|-----|
| Token Empty | TOKEN_DEBUG | Update local.properties, sync gradle, rebuild |
| HTTP 401 | API_RESPONSE | Generate new token, update local.properties |
| HTTP 403 | API_RESPONSE | Check remaining requests, wait for reset |
| API Fails | API_CONTRIBUTORS, etc. | Check secondary logs, cached data will show |
| Button Not Disabled | No specific log | Code working, app design issue |

---

## 📝 Example Logcat Output

### Successful Request
```
D/TOKEN_DEBUG: Token loaded: YES (40 chars)
D/TOKEN_DEBUG: Authorization header added
D/API_URL: Fetching: /repos/octocat/Hello-World
D/API_RESPONSE: → GET https://api.github.com/repos/octocat/Hello-World
D/API_RESPONSE: ← HTTP 200
D/API_CONTRIBUTORS: Loaded 50 contributors
D/API_LANGUAGES: Loaded 3 languages
```

### Rate Limited Request
```
D/API_RESPONSE: → GET https://api.github.com/repos/octocat/Hello-World
W/API_RESPONSE: HTTP 403 Rate Limit - Remaining: 0, Limit: 5000, Reset: 1710768000
D/API_RESPONSE: Retrying 403... (attempt 1/2)
D/API_RESPONSE: → GET https://api.github.com/repos/octocat/Hello-World
D/API_RESPONSE: ← HTTP 200
```

---

## ✅ Verification Steps

1. Open Logcat
2. Run app, click "Analyze Repository"
3. Enter: `https://github.com/octocat/Hello-World`
4. Click Analyze
5. Check Logcat:
   - See TOKEN_DEBUG: YES? ✅
   - See HTTP 200? ✅
   - See contributors loaded? ✅
   - Button disabled then enabled? ✅

---

## 🔗 Token Generation

1. https://github.com/settings/tokens
2. Generate new token (classic)
3. Name: `RepoPulse-Dev`
4. Scope: `repo`
5. Copy token → Update `local.properties`
6. Sync → Rebuild → Run

---

**All 10 debugging steps implemented successfully! ✅**

