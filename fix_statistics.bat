@echo off
echo Correction automatique de StatisticsActivity.kt...
cd /d "C:\Users\gauti\AndroidStudioProjects\MyApplication2\app\src\main\java\com\example\myapplication"

:: Créer une sauvegarde
copy StatisticsActivity.kt StatisticsActivity.kt.backup

:: Utiliser PowerShell pour les remplacements
powershell -Command "(Get-Content StatisticsActivity.kt) -replace 'Icons\.Filled\.Brush', 'Icons.Filled.Edit' | Set-Content StatisticsActivity.kt"
powershell -Command "(Get-Content StatisticsActivity.kt) -replace '\.background\(AppColors\.GradientPrimary\)', '.background(brush = AppColors.GradientPrimary)' | Set-Content StatisticsActivity.kt"

echo.
echo ✅ Corrections appliquées !
echo.
echo Changements effectués:
echo   - Icons.Filled.Brush → Icons.Filled.Edit
echo   - .background(AppColors.GradientPrimary) → .background(brush = AppColors.GradientPrimary)
echo.
echo Une sauvegarde a été créée: StatisticsActivity.kt.backup
echo.
pause
