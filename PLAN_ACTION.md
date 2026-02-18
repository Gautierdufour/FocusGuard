# 🎯 Plan d'Action - Focus Guard

## ✅ ÉTAPE 1 : VÉRIFICATION (5 min)

### À vérifier maintenant :
- [x] Tous les fichiers source sont présents
- [x] build.gradle.kts est configuré
- [x] AndroidManifest.xml est complet
- [x] PushupDetector.kt utilise le capteur de proximité
- [x] StatisticsActivity.kt est corrigé (pas d'Icons.Filled.Brush)

**Résultat :** ✅ TOUT EST PRÊT !

---

## 🔨 ÉTAPE 2 : COMPILATION (10 min)

### Méthode Recommandée :

#### Dans Android Studio :
1. **Ouvrir le projet**
   ```
   File > Open
   Sélectionner : C:\Users\gauti\AndroidStudioProjects\MyApplication2
   ```

2. **Attendre la synchronisation Gradle**
   - En bas à droite : "Gradle sync in progress..."
   - Attendre que ça finisse (1-2 min)

3. **Compiler**
   ```
   Build > Make Project (Ctrl+F9)
   ```

4. **Vérifier qu'il n'y a pas d'erreurs**
   - Tab "Build" en bas
   - Si tout est vert : ✅ SUCCESS
   - Si erreurs rouges : Noter les erreurs et les corriger

#### Alternative : Ligne de commande
```bash
cd C:\Users\gauti\AndroidStudioProjects\MyApplication2
.\gradlew clean assembleDebug
```

---

## 📱 ÉTAPE 3 : INSTALLATION (5 min)

### Option A : Via Android Studio (RECOMMANDÉ)
1. Connecter un appareil Android en USB
2. Activer le débogage USB sur l'appareil
3. `Run > Run 'app'` (Shift+F10)

### Option B : APK Manuel
1. Localiser l'APK :
   ```
   app\build\outputs\apk\debug\app-debug.apk
   ```
2. Copier sur le téléphone
3. Installer (activer "Sources inconnues" si nécessaire)

---

## 🧪 ÉTAPE 4 : TESTS ESSENTIELS (15 min)

### Test 1 : Permissions (CRITIQUE)
```
1. Lancer Focus Guard
2. Accorder TOUTES les permissions demandées :
   ✓ Usage des données d'utilisation (obligatoire)
   ✓ Affichage par-dessus d'autres apps (obligatoire)
   ✓ Notifications (recommandé)
   ✓ Localisation (si planification activée)
```

### Test 2 : Scanner d'Applications
```
1. Cliquer "Sélectionner les applications"
2. Vérifier que la liste des apps installées s'affiche
3. Sélectionner 2-3 apps de test (ex: Instagram, YouTube)
4. Retour à l'écran principal
```
**Attendu :** ✅ Les apps sélectionnées apparaissent dans la liste

### Test 3 : Activation du Blocage
```
1. Activer le switch "Blocage actif" en haut
2. Vérifier que le service démarre (notification visible)
3. Minimiser Focus Guard (bouton Home)
4. Lancer une des apps bloquées
```
**Attendu :** ✅ L'écran de blocage apparaît instantanément

### Test 4 : Défi de Pompes
```
1. Dans l'écran de blocage, choisir "Faire des pompes"
2. Sélectionner "AUTO (Proximité)" comme méthode
3. Poser le téléphone au sol, écran vers le haut
4. Faire 3-5 pompes au-dessus du téléphone
```
**Attendu :** 
- ✅ Le compteur augmente quand vous vous approchez
- ✅ Un son "beep" se déclenche à chaque pompe validée
- ✅ L'app se débloque après le nombre requis

### Test 5 : Statistiques
```
1. Retourner sur Focus Guard
2. Cliquer sur "Statistiques"
3. Vérifier les données
```
**Attendu :** ✅ Affiche le nombre de blocages et le temps économisé

### Test 6 : Redémarrage
```
1. Redémarrer le téléphone
2. Attendre 1-2 minutes
3. Vérifier que le service est actif (notification présente)
4. Tester de lancer une app bloquée
```
**Attendu :** ✅ Le blocage fonctionne toujours après reboot

---

## 🐛 SI PROBLÈMES DÉTECTÉS

### Problème : "Permission Usage des données manquante"
**Solution :**
```
Paramètres > Applications spéciales > 
Accès aux données d'utilisation > 
Focus Guard > ACTIVER
```

### Problème : "Le blocage ne fonctionne pas"
**Diagnostic :**
1. Vérifier que le service est actif (notification)
2. Vérifier les logs :
   ```bash
   adb logcat | findstr "MonitorService"
   ```
3. Vérifier la permission "Affichage par-dessus"

### Problème : "Le détecteur de pompes ne répond pas"
**Solutions possibles :**
1. Nettoyer le capteur de proximité (en haut de l'écran)
2. Vérifier que le téléphone a un capteur de proximité :
   ```bash
   adb logcat | findstr "PushupDetector"
   # Chercher "Capteur proximité OK" dans les logs
   ```
3. Essayer le mode HYBRID à la place de AUTO

### Problème : "L'app crash au lancement"
**Diagnostic :**
```bash
adb logcat | findstr "AndroidRuntime"
```
**Note les erreurs** et consulte les fichiers de correction

### Problème : "Erreur de compilation"
**Solutions :**
1. **Nettoyer le cache :**
   ```
   Build > Clean Project
   File > Invalidate Caches > Invalidate and Restart
   ```
2. **Vérifier Gradle :**
   ```
   File > Project Structure > Project
   Gradle version: 8.4
   AGP version: 8.2.2
   ```
3. **Resynchroniser :**
   ```
   File > Sync Project with Gradle Files
   ```

---

## 📊 APRÈS LES TESTS

### Si tout fonctionne ✅
**Bravo ! L'application est prête.**

Prochaines étapes possibles :
- [ ] Ajuster les seuils de détection des pompes si nécessaire
- [ ] Personnaliser les couleurs/thème
- [ ] Ajouter plus d'applications à bloquer
- [ ] Tester sur différents appareils
- [ ] Compiler une version Release signée

### Si problèmes persistent ❌
**Collecter les informations :**
1. Capturer les logs :
   ```bash
   adb logcat > logs_focus_guard.txt
   ```
2. Noter précisément :
   - Quel test échoue
   - Message d'erreur exact
   - Modèle de téléphone
   - Version d'Android

3. Consulter les fichiers de documentation :
   - `DIAGNOSTIC_ET_CORRECTION.md`
   - `FIXES.md`
   - `GUIDE_RAPIDE.md`

---

## 🎨 PERSONNALISATION (OPTIONNEL)

### Modifier les couleurs
Fichier : `AppColors.kt`
```kotlin
val Primary = Color(0xFF6C63FF)     // Violet principal
val Secondary = Color(0xFF00D4FF)   // Cyan
val Success = Color(0xFF00E676)     // Vert
```

### Ajuster la détection des pompes
Fichier : `PushupDetector.kt` (ligne ~83-84)
```kotlin
nearThreshold = maxRange * 0.3f  // Plus sensible : 0.25f
farThreshold = maxRange * 0.7f   // Moins sensible : 0.75f
```

### Modifier le nombre de pompes requis
Fichier : `LockActivity.kt`
```kotlin
// Chercher "required push-ups" et modifier le nombre
```

---

## 📝 CHECKLIST FINALE

Avant de considérer le projet terminé :

- [ ] L'application compile sans erreurs
- [ ] L'APK s'installe sur un appareil
- [ ] Toutes les permissions sont accordées
- [ ] Le scanner d'apps fonctionne
- [ ] Le blocage s'active correctement
- [ ] Au moins un type de défi fonctionne (pompes/respiration/cognitif)
- [ ] Les statistiques s'affichent
- [ ] Le service redémarre après reboot
- [ ] Pas de crash pendant 5 minutes d'utilisation

**Si tous les points sont cochés : 🎉 PROJET TERMINÉ !**

---

## 🚀 DISTRIBUTION

### Pour usage personnel :
✅ L'APK debug suffit

### Pour distribution publique :
1. **Créer une clé de signature :**
   ```
   Build > Generate Signed Bundle / APK
   ```
2. **Compiler en Release :**
   ```bash
   .\gradlew assembleRelease
   ```
3. **Tester l'APK signé**
4. **Publier sur Play Store** (si souhaité)

---

## 📞 RESSOURCES

### Fichiers de référence :
- `ETAT_PROJET.md` - État complet
- `GUIDE_RAPIDE.md` - Guide utilisateur
- `DIAGNOSTIC_ET_CORRECTION.md` - Détails techniques
- `FIXES.md` - Correctifs appliqués

### Commandes utiles :
```bash
# Voir les logs en temps réel
adb logcat | findstr "myapplication"

# Réinstaller l'app
adb uninstall com.example.myapplication
adb install app\build\outputs\apk\debug\app-debug.apk

# Lister les appareils connectés
adb devices

# Redémarrer ADB si problème
adb kill-server
adb start-server
```

---

**Bonne chance ! 🎯**

Si tu suis ce plan étape par étape, ton application Focus Guard devrait être fonctionnelle en moins de 30 minutes !
