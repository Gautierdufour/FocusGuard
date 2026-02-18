# 🚀 Guide Rapide - Focus Guard

## Compilation Rapide

### Option 1 : Utiliser le script automatique (RECOMMANDÉ)
```bash
# Double-cliquer sur :
compile.bat
```

### Option 2 : Ligne de commande
```bash
# Nettoyer et compiler
.\gradlew clean assembleDebug

# Installer directement sur un appareil
.\gradlew installDebug
```

### Option 3 : Android Studio
1. Ouvrir le projet dans Android Studio
2. `Build > Make Project` (Ctrl+F9)
3. `Run > Run 'app'` (Shift+F10)

---

## Installation sur Appareil

### 1. APK Généré
L'APK se trouve dans :
```
app\build\outputs\apk\debug\app-debug.apk
```

### 2. Installer manuellement
- Copier l'APK sur le téléphone
- Activer "Sources inconnues" dans les paramètres
- Ouvrir l'APK pour installer

### 3. Installer via ADB
```bash
adb install app\build\outputs\apk\debug\app-debug.apk
```

---

## Configuration Initiale de l'App

### 1. Premières Permissions Nécessaires
Au premier lancement, l'application demandera :

1. **Usage des données d'utilisation** ⚠️ OBLIGATOIRE
   - Paramètres > Applications spéciales > Accès aux données d'utilisation
   - Activer Focus Guard

2. **Affichage par-dessus d'autres applications** ⚠️ OBLIGATOIRE
   - Permet d'afficher l'écran de blocage
   - Paramètres > Applications spéciales > Afficher par-dessus
   - Activer Focus Guard

3. **Notifications** 📢 RECOMMANDÉ
   - Pour le service en arrière-plan

4. **Localisation** 📍 OPTIONNEL
   - Uniquement si vous utilisez la planification intelligente

### 2. Scanner les Applications
1. Ouvrir Focus Guard
2. Appuyer sur "Sélectionner les applications"
3. Choisir les apps à bloquer
4. Activer le blocage

### 3. Choisir le Type de Défi
- **Pompes** : Détection par capteur de proximité
- **Respiration** : Exercices de cohérence cardiaque
- **Cognitif** : Calculs mentaux
- **Attente** : Timer de réflexion
- **Manuel** : Bouton de déblocage

---

## Test Rapide de l'Application

### Test 1 : Scanner des Apps
```
1. Lancer Focus Guard
2. Cliquer "Sélectionner les applications"
3. Vérifier que la liste s'affiche
```
✅ Si la liste est vide → Vérifier permission "Usage des données"

### Test 2 : Activer le Blocage
```
1. Sélectionner une app de test
2. Activer le blocage (switch en haut)
3. Minimiser Focus Guard
4. Lancer l'app sélectionnée
```
✅ L'écran de blocage devrait apparaître

### Test 3 : Défi de Pompes
```
1. Dans l'écran de blocage, choisir "Pompes"
2. Poser le téléphone au sol, écran vers le haut
3. Faire des pompes au-dessus
```
✅ Le compteur devrait augmenter quand vous vous approchez

### Test 4 : Statistiques
```
1. Retourner sur Focus Guard
2. Cliquer sur "Statistiques"
3. Vérifier les données
```
✅ Devrait afficher les blocages effectués

---

## Résolution des Problèmes Courants

### ❌ Le blocage ne fonctionne pas
**Cause :** Permissions manquantes  
**Solution :**
1. Paramètres > Applications > Focus Guard > Permissions
2. Vérifier "Usage des données" et "Affichage par-dessus"

### ❌ L'app ne redémarre pas après reboot
**Cause :** Optimisation de batterie  
**Solution :**
1. Paramètres > Batterie > Optimisation de la batterie
2. Désactiver l'optimisation pour Focus Guard

### ❌ Le détecteur de pompes ne répond pas
**Cause :** Capteur de proximité bouché ou inexistant  
**Solutions :**
1. Nettoyer le capteur (en haut de l'écran)
2. Essayer le mode HYBRID
3. Vérifier dans les logs :
   ```bash
   adb logcat | findstr "PushupDetector"
   ```

### ❌ Erreur de compilation "SDK not found"
**Solution :**
1. Android Studio > File > Project Structure
2. SDK Location > Pointer vers le SDK Android
3. Sync Gradle

### ❌ Erreur "Duplicate class"
**Solution :**
```bash
.\gradlew clean
File > Invalidate Caches > Invalidate and Restart
```

---

## Logs en Temps Réel

### Voir les logs de l'application
```bash
# Tous les logs Focus Guard
adb logcat | findstr "myapplication"

# Logs détecteur de pompes
adb logcat | findstr "PushupDetector"

# Logs service de monitoring
adb logcat | findstr "MonitorService"

# Logs écran de blocage
adb logcat | findstr "LockActivity"
```

---

## Désinstallation

### Via l'appareil
```
Paramètres > Applications > Focus Guard > Désinstaller
```

### Via ADB
```bash
adb uninstall com.example.myapplication
```

---

## Support & Documentation

### Fichiers de référence dans le projet :
- `ETAT_PROJET.md` - État complet du projet
- `DIAGNOSTIC_ET_CORRECTION.md` - Détails sur les corrections
- `FIXES.md` - Liste des correctifs
- `STRUCTURE_COMPLETE_APP.md` - Architecture de l'application

### En cas de problème :
1. Vérifier `ETAT_PROJET.md` pour l'état actuel
2. Consulter les logs avec `adb logcat`
3. Vérifier les permissions dans les paramètres de l'appareil
4. Redémarrer l'appareil si nécessaire

---

## Méthodes de Détection des Pompes

### AUTO (Proximité) ⭐ RECOMMANDÉ
- **Comment :** Capteur de proximité
- **Position :** Téléphone au sol, écran vers le haut
- **Détection :** Votre corps s'approche/s'éloigne

### HYBRID (Mixte)
- **Comment :** Accéléromètre + Proximité
- **Avantage :** Plus fiable, double vérification

### SHAKE (Secousse)
- **Comment :** Accéléromètre uniquement
- **Position :** Téléphone dans la main ou poche
- **Détection :** Mouvements brusques

### MANUAL (Manuel)
- **Comment :** Bouton à presser
- **Usage :** Test ou si capteurs indisponibles

---

## Version & Mise à Jour

**Version actuelle :** 1.0  
**Date :** 2025-01-12  
**Status :** ✅ Stable et fonctionnel

Pour compiler une nouvelle version :
1. Modifier `versionCode` et `versionName` dans `app/build.gradle.kts`
2. Recompiler avec `.\gradlew assembleRelease`
3. Signer l'APK pour la distribution

---

**Bon développement ! 🚀**
