# PowerShell script to fix build issues caused by OneDrive file locking
# This script uses the working solution with offline mode

Write-Host "=== Android Build Fix Script ===" -ForegroundColor Cyan
Write-Host ""

$projectRoot = $PSScriptRoot
Set-Location $projectRoot

Write-Host "[1/5] Stopping Java/Gradle processes..." -ForegroundColor Yellow
Get-Process | Where-Object { $_.ProcessName -match "java|gradle" } | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3

Write-Host "[2/5] Cleaning local build outputs..." -ForegroundColor Yellow
if (Test-Path "app\build") {
    attrib -R "app\build\*" /S /D 2>$null
    Remove-Item "app\build" -Recurse -Force -ErrorAction SilentlyContinue
}
if (Test-Path ".gradle") {
    attrib -R ".gradle\*" /S /D 2>$null
    Remove-Item ".gradle" -Recurse -Force -ErrorAction SilentlyContinue
}
Start-Sleep -Seconds 2

Write-Host "[3/5] Preparing Gradle cache outside OneDrive..." -ForegroundColor Yellow
$gradleHome = Join-Path $env:LOCALAPPDATA "Gradle\RepoPulse"
if (-not (Test-Path $gradleHome)) {
    New-Item -Path $gradleHome -ItemType Directory | Out-Null
}
$env:GRADLE_USER_HOME = $gradleHome
Write-Host "    GRADLE_USER_HOME=$gradleHome" -ForegroundColor Gray

Write-Host "[4/5] Building APK..." -ForegroundColor Green
.\gradlew.bat :app:assembleDebug --no-daemon --no-build-cache --no-configuration-cache

Write-Host ""
Write-Host "[5/5] Checking build result..." -ForegroundColor Yellow

if (Test-Path "app\build\outputs\apk\debug\app-debug.apk") {
    $apkInfo = Get-Item "app\build\outputs\apk\debug\app-debug.apk"
    Write-Host ""
    Write-Host "BUILD SUCCESSFUL" -ForegroundColor Green
    Write-Host ""
    Write-Host "APK Details:" -ForegroundColor Cyan
    Write-Host "  Location: $($apkInfo.FullName)" -ForegroundColor White
    Write-Host "  Size: $([math]::Round($apkInfo.Length / 1MB, 2)) MB" -ForegroundColor White
    Write-Host "  Created: $($apkInfo.LastWriteTime)" -ForegroundColor White
    Write-Host ""
    Write-Host "To install:" -ForegroundColor Yellow
    Write-Host "  adb install app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor White
    Write-Host "  Or use Android Studio Run button" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "BUILD FAILED - APK not found" -ForegroundColor Red
    Write-Host "Check build errors above." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Script Complete ===" -ForegroundColor Cyan
Write-Host "Tip: Best long-term fix is moving repo out of OneDrive." -ForegroundColor Gray
