@echo off
REM Script de compilation automatique pour Focus Guard
REM Version 1.0 - 2025-01-12

echo ========================================
echo    FOCUS GUARD - COMPILATION
echo ========================================
echo.

REM Vérifier que nous sommes dans le bon répertoire
if not exist "gradlew.bat" (
    echo ERREUR: gradlew.bat non trouve!
    echo Assurez-vous d'executer ce script depuis le repertoire racine du projet.
    pause
    exit /b 1
)

echo [1/5] Nettoyage du projet...
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR lors du nettoyage!
    pause
    exit /b 1
)

echo.
echo [2/5] Synchronisation Gradle...
REM Gradle sync se fait automatiquement

echo.
echo [3/5] Compilation du projet...
call gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR lors de la compilation!
    pause
    exit /b 1
)

echo.
echo [4/5] Verification de l'APK...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✓ APK genere avec succes!
    echo Location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo ERREUR: APK non trouve!
    pause
    exit /b 1
)

echo.
echo [5/5] Installation sur appareil (optionnel)...
echo.
echo Voulez-vous installer l'APK sur un appareil connecte? (O/N)
set /p install_choice="> "

if /i "%install_choice%"=="O" (
    echo Installation en cours...
    call gradlew.bat installDebug
    if %ERRORLEVEL% EQU 0 (
        echo ✓ Application installee avec succes!
    ) else (
        echo ERREUR lors de l'installation!
        echo Assurez-vous qu'un appareil est connecte en mode debug USB.
    )
)

echo.
echo ========================================
echo    COMPILATION TERMINEE!
echo ========================================
echo.
echo L'APK est disponible dans:
echo app\build\outputs\apk\debug\app-debug.apk
echo.
pause
