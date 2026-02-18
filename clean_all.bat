@echo off
echo =========================================
echo NETTOYAGE COMPLET DU PROJET
echo =========================================
echo.

cd /d "C:\Users\gauti\AndroidStudioProjects\MyApplication2"

echo Suppression de .gradle...
if exist ".gradle" rd /s /q ".gradle"

echo Suppression de build (racine)...
if exist "build" rd /s /q "build"

echo Suppression de app\build...
if exist "app\build" rd /s /q "app\build"

echo Suppression de .idea\caches...
if exist ".idea\caches" rd /s /q ".idea\caches"

echo.
echo ✅ Nettoyage terminé !
echo.
echo Maintenant dans Android Studio:
echo 1. Build -^> Clean Project
echo 2. Build -^> Rebuild Project
echo.
pause
