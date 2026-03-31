# Donut Chart Visibility Fix - Testing & Verification Guide

## Quick Start Testing

### Pre-Test Checklist
- [ ] Android device or emulator running (API 21+)
- [ ] App built successfully with latest changes
- [ ] Test data available with different language distributions

---

## Test Cases

### Test Case 1: Extreme Imbalance (One Category Dominates)
**Scenario:** Repository with 99% Core code, 0% UI/UX, API, Docs

**Steps:**
1. Load a repository with heavily skewed language distribution
2. Navigate to Insights fragment
3. Observe donut chart in "Impact Distribution" card

**Expected Results:**
✅ All 4 colors visible in chart (Green, Yellow, Orange, Red)  
✅ Core shows as large slice (97% of chart after normalization)  
✅ UI/UX, API, Docs show as tiny slices with visible lines  
✅ Text labels show honest percentages:
   - Core - 99%
   - UI/UX - 0%
   - API - 0%
   - Docs - 0%

✅ Chart labels (outside slices) show normalized percentages
✅ No slices missing or hidden

**Validation Points:**
- Count visible colors: Should be 4
- Check if smallest slices have connecting lines to labels
- Verify labels are readable and don't overlap

---

### Test Case 2: Balanced Distribution
**Scenario:** Repository with ~25% each category

**Steps:**
1. Load a repository with balanced language distribution
2. Navigate to Insights fragment
3. Observe donut chart

**Expected Results:**
✅ 4 equal or near-equal slices (25% ± 2% each)  
✅ All colors clearly visible and distinguishable  
✅ Labels readable without overlap  
✅ Text labels show approximate percentages  

**Validation Points:**
- Visual check: All 4 slices visible
- Color check: Each slice has distinct color
- Label check: All 4 category names visible

---

### Test Case 3: Mixed Distribution with Zero Values
**Scenario:** Repository with Core=50%, UI/UX=30%, API=20%, Docs=0%

**Steps:**
1. Load appropriate repository
2. Navigate to Insights fragment
3. Observe donut chart

**Expected Results:**
✅ Docs category still visible (adjusted to ~1%)  
✅ Text label: "Docs - 0%"  
✅ Chart label: "Docs - 0.99%" (normalized)  
✅ Visible line connecting tiny Docs slice to label  

**Validation Points:**
- Zero-value category is NOT missing
- Zero-value category has visible slice and label
- Other categories show correct percentages

---

### Test Case 4: Visual Label Clarity
**Scenario:** Any repository with diverse language distribution

**Steps:**
1. Load repository
2. Navigate to Insights fragment
3. Examine chart labels carefully

**Expected Results:**
✅ Labels positioned OUTSIDE slices (not overlapping)  
✅ White lines connecting labels to slices  
✅ Format: "Category - XX.X%"  
✅ Text readable on dark background  
✅ Labels don't overlap each other  

**Validation Points:**
- Check label positioning (should be outside circle)
- Check for text overlaps
- Check readability of smallest labels
- Check line visibility

---

### Test Case 5: Color Specification Verification
**Scenario:** Any repository

**Steps:**
1. Load repository
2. Navigate to Insights fragment
3. Identify each slice and note its color

**Expected Results:**
| Category | Expected Color | Hex Code | Visual Name |
|----------|---|---|---|
| Core | Green | #4CAF50 | Bright green |
| UI/UX | Yellow | #FFC107 | Bright yellow |
| API | Orange | #FF9800 | Bright orange |
| Docs | Red | #F44336 | Red |

✅ Each category has correct color  
✅ Colors are vibrant and high-contrast  
✅ Colors easily distinguishable from each other  

**Validation Points:**
- Color accuracy (compare against hex values)
- Color distinctness (can each be identified separately)

---

### Test Case 6: Animation & Interaction
**Scenario:** Any repository

**Steps:**
1. Load repository
2. Watch chart load and animate
3. Attempt to interact with chart (if applicable)

**Expected Results:**
✅ Chart animates smoothly for ~1 second on load  
✅ Chart is not interactive (touch disabled)  
✅ Center text displays: "All\nCategories"  
✅ No errors during animation  

**Validation Points:**
- Smooth animation (no jank or stuttering)
- Correct center text display
- No touch response (as expected)

---

### Test Case 7: Responsive Design
**Scenario:** Different screen sizes

**Steps:**
1. Test on small phone (5")
2. Test on medium phone (6")
3. Test on large tablet (10"+)
4. Test in both portrait and landscape

**Expected Results:**
✅ Chart scales properly on all screen sizes  
✅ Labels remain readable  
✅ No text clipping  
✅ No overlap issues  
✅ Chart fills available space appropriately  

**Validation Points:**
- Check label visibility on small screens
- Check space efficiency on large screens
- Check orientation changes
- Verify chart doesn't expand beyond container

---

### Test Case 8: Theme Compatibility
**Scenario:** Light and dark themes

**Steps:**
1. Switch app to light theme
2. Observe chart appearance
3. Switch app to dark theme
4. Observe chart appearance

**Expected Results:**
✅ Chart visible and readable in both themes  
✅ White text labels clearly visible  
✅ Transparent background matches theme  
✅ No color issues or poor contrast  

**Validation Points:**
- Text contrast in light theme
- Text contrast in dark theme
- Color distinctness in both themes
- Overall visual appeal

---

## Manual Verification Steps

### Step 1: Verify File Changes
```bash
# Check the modified file
cat "C:\Users\thanu\OneDrive\Desktop\Git_repo_4\app\src\main\java\com\example\git_repo_4\InsightsFragment.kt"

# Key sections to verify:
# 1. Lines 143-163: Minimum value handling (should be 1f, not 0.5f)
# 2. Lines 160-163: PieEntry labels (should be "Core", "UI/UX", "API", "Docs")
# 3. Lines 223-228: Color definitions (should have 4 distinct hex colors)
# 4. Lines 251-261: ValueFormatter implementation (should show percentages)
# 5. Lines 265-305: Chart configuration (should have updated settings)
```

### Step 2: Verify Compilation
```bash
cd "C:\Users\thanu\OneDrive\Desktop\Git_repo_4"

# Clean and build
./gradlew.bat clean build

# Expected output:
# BUILD SUCCESSFUL in X seconds
```

### Step 3: Verify Data Flow
Add temporary logging to trace data:

```kotlin
// In languageStats observer:
Log.d("DonutChart", "corePercent=$corePercent, uiPercent=$uiPercent, apiPercent=$apiPercent, docsPercent=$docsPercent")
Log.d("DonutChart", "coreNormalized=$coreNormalized, uiNormalized=$uiNormalized, apiNormalized=$apiNormalized, docsNormalized=$docsNormalized")
Log.d("DonutChart", "entries.size=${entries.size}, entries=$entries")
```

Expected log output:
```
D/DonutChart: corePercent=99, uiPercent=0, apiPercent=0, docsPercent=0
D/DonutChart: coreNormalized=97.05, uiNormalized=0.98, apiNormalized=0.98, docsNormalized=0.98
D/DonutChart: entries.size=4, entries=[PieEntry(97.05, Core), PieEntry(0.98, UI/UX), PieEntry(0.98, API), PieEntry(0.98, Docs)]
```

---

## Visual Inspection Checklist

### Chart Appearance
- [ ] Chart displays 4 colored slices
- [ ] Largest slice takes appropriate proportion
- [ ] Smallest slices are still visible as thin lines
- [ ] No slices are completely hidden
- [ ] Slice colors match specification (Green, Yellow, Orange, Red)

### Labels
- [ ] Category names visible outside slices
- [ ] Percentages displayed with labels
- [ ] Labels have connecting lines to slices
- [ ] No label overlap (except possibly for closely positioned slices)
- [ ] Text is white and readable on dark background

### Center Content
- [ ] Center shows "All\nCategories"
- [ ] Center text is white and readable
- [ ] Center styling matches overall chart design

### Text Statistics (Below Chart)
- [ ] Core percentage shown (e.g., "Core - 99%")
- [ ] UI/UX percentage shown (e.g., "UI/UX - 0%")
- [ ] API percentage shown (e.g., "API - 0%")
- [ ] Docs percentage shown (e.g., "Docs - 0%")
- [ ] All percentages are readable

---

## Common Issues & Solutions

### Issue 1: Labels Overlapping
**Symptom:** Chart labels are overlapping or hard to read

**Solution:**
- Check `setExtraOffsets()` value (should be 30f for all sides)
- Verify `valueTextSize` is 10f (not too large)
- Ensure labels are positioned OUTSIDE_SLICE

### Issue 2: Small Slices Not Visible
**Symptom:** Some colored slices are missing

**Solution:**
- Check minimum value adjustment (should be 1f, not 0.5f)
- Verify normalization is working correctly
- Check `setSliceSpace()` is 3f
- Verify colors are set correctly

### Issue 3: Wrong Colors Displayed
**Symptom:** Colors don't match specification

**Solution:**
- Verify hex color codes in `colors` list
- Ensure colors are in correct order (Core, UI/UX, API, Docs)
- Check `setColors(colors)` is being called
- Verify no color override elsewhere in code

### Issue 4: Chart Not Animating
**Symptom:** Chart appears instantly without animation

**Solution:**
- Check `animateY(1000)` is called
- Verify chart is not already invalidated before animation
- Check device animation settings are enabled

### Issue 5: Labels Not Showing
**Symptom:** No text visible on chart slices

**Solution:**
- Check `setDrawValues(true)` is set
- Verify `setDrawEntryLabels(true)` is enabled
- Check `valueTextColor` is WHITE
- Ensure ValueFormatter is not returning empty strings

---

## Performance Metrics

### Expected Performance
- Chart rendering time: < 500ms
- Animation duration: 1000ms (smooth)
- Memory usage: < 5MB
- No frame drops during animation
- No ANR (Application Not Responding) errors

### Monitoring
Use Android Profiler to check:
- CPU usage during chart animation
- Memory allocation
- Frame rate (should maintain 60fps)
- GC (garbage collection) events

---

## Regression Testing

After deployment, verify these still work:
- [ ] Contributor list displays correctly
- [ ] Commit count shows correct number
- [ ] Weekly growth metric displays
- [ ] Review speed metric displays
- [ ] Health status displays
- [ ] Risks section displays
- [ ] Suggestions section displays
- [ ] Navigation to other tabs works
- [ ] Data refresh works correctly
- [ ] No crashes on data update

---

## Browser/Emulator Testing

### Recommended Test Environments
1. **Android 5.1 (API 22)** - Minimum supported
2. **Android 7.0 (API 24)** - Common version
3. **Android 11 (API 30)** - Modern version
4. **Android 13 (API 33)** - Latest version

### Test Devices
- Small phone (5")
- Medium phone (6")
- Large phone (6.5"+)
- Tablet (10")

---

## Sign-Off Checklist

### Development
- [ ] Code changes completed
- [ ] No compilation errors
- [ ] All imports correct
- [ ] Code follows style guidelines

### Testing
- [ ] All 8 test cases passed
- [ ] Visual inspection passed
- [ ] No regressions detected
- [ ] Performance acceptable

### Documentation
- [ ] Code comments added
- [ ] Technical guide created
- [ ] Testing guide created
- [ ] README updated

### Deployment
- [ ] Build successful
- [ ] APK/AAB generated
- [ ] No warnings or errors
- [ ] Ready for release

---

## Post-Deployment Monitoring

### Metrics to Track
- Crash rate (should be 0 for this change)
- User feedback (chart visibility improvements)
- Chart interaction metrics
- Performance metrics

### Alert Conditions
- Crash rate > 0.01%
- ANR (Application Not Responding) events
- Frame rate < 45fps
- Memory usage > 50MB

---

**Version:** 1.0  
**Created:** March 31, 2026  
**Status:** ✅ Ready for Testing

