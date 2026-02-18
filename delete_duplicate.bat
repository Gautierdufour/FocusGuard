@echo off
echo Suppression du fichier dupliqué...
cd /d "C:\Users\gauti\AndroidStudioProjects\MyApplication2\app\src\main\java\com\example\myapplication"

if exist "StatisticsActivity_NEW.kt" (
    del "StatisticsActivity_NEW.kt"
    echo ✅ StatisticsActivity_NEW.kt supprimé !
) else (
    echo ℹ️  Fichier déjà supprimé
)

echo.
echo Maintenant dans Android Studio:
echo Build -^> Clean Project
echo Build -^> Rebuild Project
echo.
pause
