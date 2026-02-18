@echo off
REM Script de génération d'APK pour Focus Guard
REM Version 1.0 - 2025-01-12

echo ========================================
echo    FOCUS GUARD - GENERATION APK
echo ========================================
echo.

REM Vérifier que nous sommes dans le bon répertoire
if not exist "gradlew.bat" (
    echo ERREUR: gradlew.bat non trouve!
    echo Assurez-vous d'executer ce script depuis le repertoire racine du projet.
    pause
    exit /b 1
)

REM Créer le dossier releases s'il n'existe pas
if not exist "releases" mkdir releases

echo [1/4] Nettoyage du projet...
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR lors du nettoyage!
    pause
    exit /b 1
)

echo.
echo [2/4] Generation de l'APK DEBUG...
call gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR lors de la generation de l'APK debug!
    pause
    exit /b 1
)

echo.
echo [3/4] Copie de l'APK debug dans releases/...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    copy "app\build\outputs\apk\debug\app-debug.apk" "releases\FocusGuard-debug.apk" >nul
    echo ✓ APK debug copie avec succes!
    echo    Location: releases\FocusGuard-debug.apk
) else (
    echo ERREUR: APK debug non trouve!
)

echo.
echo [4/4] Tentative de generation de l'APK RELEASE...
echo (Necessite une cle de signature configuree)
echo.

call gradlew.bat assembleRelease 2>nul
if %ERRORLEVEL% EQU 0 (
    if exist "app\build\outputs\apk\release\app-release.apk" (
        copy "app\build\outputs\apk\release\app-release.apk" "releases\FocusGuard-release.apk" >nul
        echo ✓ APK release genere et copie avec succes!
        echo    Location: releases\FocusGuard-release.apk
    )
) else (
    echo ⚠ APK release non genere (cle de signature manquante)
    echo    Voir GUIDE_ICONE_ET_APK.md pour creer une cle
)

echo.
echo ========================================
echo    GENERATION TERMINEE!
echo ========================================
echo.
echo APK disponibles dans le dossier "releases\":
echo.

if exist "releases\FocusGuard-debug.apk" (
    echo ✓ FocusGuard-debug.apk
    for %%I in ("releases\FocusGuard-debug.apk") do echo    Taille: %%~zI octets
)

if exist "releases\FocusGuard-release.apk" (
    echo ✓ FocusGuard-release.apk
    for %%I in ("releases\FocusGuard-release.apk") do echo    Taille: %%~zI octets
)

echo.
echo ========================================
echo.
echo Pour installer l'APK :
echo   1. Copier FocusGuard-debug.apk sur votre telephone
echo   2. Ou utiliser : adb install releases\FocusGuard-debug.apk
echo.
echo Pour creer une version RELEASE signee :
echo   Voir : GUIDE_ICONE_ET_APK.md
echo.
pause
