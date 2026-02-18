📱 **NOUVEAU !** Comment personnaliser l'icône et générer un APK installable :

## 🎨 Changer l'Icône et le Nom

### Nom de l'Application
✅ **Déjà changé !** Le nom est maintenant "Focus Guard" au lieu de "App Blocker"

### Icône Personnalisée

**Méthode Rapide (Android Studio) :**
1. Clic droit sur `app > res`
2. `New > Image Asset`
3. Choisir ton image ou un emoji 🛡️
4. Next > Finish

**Méthode Alternative (En ligne) :**
1. https://romannurik.github.io/AndroidAssetStudio/
2. Uploader ton image (PNG 512x512)
3. Télécharger le pack d'icônes
4. Copier dans `app\src\main\res\`

**Voir le guide complet :** [GUIDE_ICONE_ET_APK.md](GUIDE_ICONE_ET_APK.md) ou [GUIDE_ICONE_RAPIDE.txt](GUIDE_ICONE_RAPIDE.txt)

---

## 📦 Générer un APK Installable

### Méthode Rapide (Recommandée)
```bash
# Double-cliquer sur :
generate_apk.bat
```
✅ L'APK sera dans : `releases\FocusGuard-debug.apk`

### Autres Méthodes

**Ligne de commande :**
```bash
.\gradlew assembleDebug
# APK dans : app\build\outputs\apk\debug\app-debug.apk
```

**Android Studio :**
```
Build > Build Bundle(s) / APK(s) > Build APK(s)
```

---

## 📤 Installer l'APK

**Via ADB (USB) :**
```bash
adb install releases\FocusGuard-debug.apk
```

**Manuellement :**
1. Copier l'APK sur le téléphone
2. Ouvrir le fichier
3. Activer "Sources inconnues" si demandé
4. Installer

---

## 🔐 APK Release (Distribution)

Pour créer un APK signé pour distribution publique :

1. **Android Studio :**
   ```
   Build > Generate Signed Bundle / APK
   ```
2. Créer une clé de signature (la sauvegarder !)
3. Générer l'APK release signé

**Voir :** [GUIDE_ICONE_ET_APK.md](GUIDE_ICONE_ET_APK.md) pour le guide détaillé

---

## 📚 Nouveaux Fichiers Créés

- **[GUIDE_ICONE_ET_APK.md](GUIDE_ICONE_ET_APK.md)** - Guide complet icône + APK
- **[GUIDE_ICONE_RAPIDE.txt](GUIDE_ICONE_RAPIDE.txt)** - Guide visuel rapide
- **[generate_apk.bat](generate_apk.bat)** - Script de génération automatique
- **strings.xml** - Modifié avec le nouveau nom "Focus Guard"
