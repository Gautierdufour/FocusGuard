# 🎨 GUIDE : Personnaliser l'Icône et Générer l'APK

## 📱 PARTIE 1 : CHANGER L'ICÔNE

### Option A : Utiliser Android Studio (RECOMMANDÉ)

1. **Ouvrir le projet dans Android Studio**
2. **Cliquer droit sur le dossier `res`**
   ```
   app > src > main > res (clic droit)
   ```
3. **Sélectionner : New > Image Asset**
4. **Dans la fenêtre qui s'ouvre :**
   - Icon Type : `Launcher Icons (Adaptive and Legacy)`
   - Name : `ic_launcher`
   - Foreground Layer :
     - Asset Type : `Image` ou `Clip Art`
     - Path : Sélectionner votre image (PNG, SVG)
     - OU choisir une icône de la bibliothèque
   - Background Layer :
     - Couleur : Choisir une couleur de fond (ex: #6C63FF pour violet)
5. **Cliquer sur "Next" puis "Finish"**

✅ L'icône sera automatiquement générée dans toutes les résolutions nécessaires !

### Option B : Manuellement (si pas d'Android Studio)

1. **Préparer votre image :**
   - Format : PNG avec fond transparent (recommandé)
   - Taille recommandée : 512x512 pixels minimum
   - Style : Simple, reconnaissable, pas trop de détails

2. **Générer les icônes en plusieurs tailles :**
   - Utiliser un outil en ligne : https://romannurik.github.io/AndroidAssetStudio/
   - OU https://appicon.co/
   - Uploader votre image
   - Télécharger le pack d'icônes généré

3. **Remplacer les icônes :**
   - Extraire le ZIP téléchargé
   - Copier tous les dossiers `mipmap-*` dans :
     ```
     app\src\main\res\
     ```
   - Écraser les fichiers existants

### Tailles d'Icônes Nécessaires

Si tu veux le faire manuellement, voici les tailles :

```
res/
├── mipmap-mdpi/
│   └── ic_launcher.png (48x48)
├── mipmap-hdpi/
│   └── ic_launcher.png (72x72)
├── mipmap-xhdpi/
│   └── ic_launcher.png (96x96)
├── mipmap-xxhdpi/
│   └── ic_launcher.png (144x144)
└── mipmap-xxxhdpi/
    └── ic_launcher.png (192x192)
```

### Créer une Icône Simple avec Emoji (Rapide !)

Si tu veux une solution ultra-rapide :

1. Aller sur : https://favicon.io/emoji-favicons/
2. Choisir un emoji (🛡️, 🔒, 💪, 🎯)
3. Télécharger et utiliser comme ci-dessus

---

## 📦 PARTIE 2 : GÉNÉRER L'APK INSTALLABLE

### Méthode 1 : APK Debug (Pour Tests)

#### Via le script automatique :
```bash
# Double-cliquer sur :
compile.bat

# Puis choisir "Oui" pour installer automatiquement
```

#### Via ligne de commande :
```bash
cd C:\Users\gauti\AndroidStudioProjects\MyApplication2
.\gradlew clean assembleDebug
```

✅ **L'APK sera généré dans :**
```
app\build\outputs\apk\debug\app-debug.apk
```

### Méthode 2 : APK Release (Pour Distribution)

⚠️ **Important :** L'APK Release doit être signé !

#### Étape A : Créer une Clé de Signature

1. **Via Android Studio :**
   ```
   Build > Generate Signed Bundle / APK
   → Sélectionner "APK"
   → Click "Next"
   → Click "Create new..." (pour créer une nouvelle clé)
   ```

2. **Remplir les informations :**
   ```
   Key store path: C:\Users\gauti\focus-guard-key.jks
   Password: [Choisir un mot de passe fort]
   Alias: focus-guard
   Password (key): [Même mot de passe ou différent]
   
   Validity (years): 25
   First and Last Name: Gautier
   Organizational Unit: [Optionnel]
   Organization: [Optionnel]
   City: Cergy-Pontoise
   State: Île-de-France
   Country Code: FR
   ```

⚠️ **IMPORTANT : Sauvegarder cette clé et les mots de passe !**
   - Sans elle, tu ne pourras pas mettre à jour l'app sur Play Store
   - Faire une copie de sauvegarde du fichier `.jks`

3. **Finaliser :**
   ```
   Build Variants: release
   Signature Versions: ✓ V1 et ✓ V2
   Click "Next" puis "Finish"
   ```

#### Étape B : Générer l'APK Release Signé

**Via Android Studio :**
```
Build > Generate Signed Bundle / APK
→ APK
→ Sélectionner la clé créée précédemment
→ Entrer les mots de passe
→ release
→ Finish
```

**Via ligne de commande (après avoir configuré le keystore) :**
```bash
.\gradlew assembleRelease
```

✅ **L'APK signé sera dans :**
```
app\build\outputs\apk\release\app-release.apk
```

---

## 🔐 CONFIGURATION AUTOMATIQUE DU KEYSTORE (Optionnel)

Pour ne pas entrer les mots de passe à chaque fois :

1. **Créer un fichier `keystore.properties` à la racine du projet :**

```properties
storeFile=C:/Users/gauti/focus-guard-key.jks
storePassword=TON_MOT_DE_PASSE
keyAlias=focus-guard
keyPassword=TON_MOT_DE_PASSE
```

⚠️ **NE JAMAIS commiter ce fichier sur Git !**

2. **Ajouter au `.gitignore` :**
```
keystore.properties
*.jks
```

3. **Modifier `app/build.gradle.kts` pour utiliser le keystore :**

Je peux faire cette modification si tu veux !

---

## 📤 PARTIE 3 : DISTRIBUER L'APK

### Option 1 : Installation Directe (USB)

```bash
# Connecter le téléphone en USB
# Activer le débogage USB sur le téléphone

adb install app\build\outputs\apk\debug\app-debug.apk

# Ou pour la version release :
adb install app\build\outputs\apk\release\app-release.apk
```

### Option 2 : Partage via Fichier

1. Copier l'APK sur le téléphone (USB, email, Drive, etc.)
2. Sur le téléphone : Ouvrir le fichier APK
3. Activer "Sources inconnues" si demandé
4. Installer

### Option 3 : Google Play Store (Distribution Publique)

Pour publier sur le Play Store :

1. **Créer un compte Google Play Developer** ($25 unique)
2. **Créer une nouvelle application**
3. **Générer un AAB (Android App Bundle) au lieu d'un APK :**
   ```
   Build > Generate Signed Bundle / APK
   → Android App Bundle
   → Sélectionner la clé
   → release
   ```
4. **Uploader l'AAB sur Play Console**
5. **Remplir les informations :**
   - Descriptions
   - Screenshots
   - Icônes
   - Classification du contenu
   - Etc.
6. **Soumettre pour review**

---

## 🎯 CHECKLIST COMPLÈTE

### Avant de Générer l'APK :

- [ ] Nom de l'app changé dans `strings.xml` → "Focus Guard"
- [ ] Icône personnalisée ajoutée dans `res/mipmap-*`
- [ ] Version mise à jour dans `build.gradle.kts` :
  ```kotlin
  versionCode = 1
  versionName = "1.0"
  ```
- [ ] Testé sur un appareil debug

### Pour APK Debug :

- [ ] `.\gradlew assembleDebug` ou `compile.bat`
- [ ] APK généré dans `app/build/outputs/apk/debug/`
- [ ] Installé et testé

### Pour APK Release :

- [ ] Clé de signature créée et sauvegardée
- [ ] APK signé généré
- [ ] APK testé sur plusieurs appareils
- [ ] Prêt pour distribution

---

## 🛠️ SCRIPT DE GÉNÉRATION D'APK

Je vais créer un script `generate_apk.bat` qui :
1. Nettoie le projet
2. Génère l'APK debug ET release (si clé configurée)
3. Copie l'APK dans un dossier `releases/`

Veux-tu que je crée ce script ?

---

## 📊 COMPARAISON APK vs AAB

| Critère | APK | AAB (App Bundle) |
|---------|-----|------------------|
| Taille | Plus gros | 20-30% plus petit |
| Distribution | Directe | Via Play Store uniquement |
| Installation | Partout | Play Store seulement |
| Optimisation | Unique | Par appareil |
| **Usage recommandé** | Tests, distribution directe | Publication Play Store |

---

## ❓ QUESTIONS FRÉQUENTES

**Q : Quelle est la différence entre debug et release ?**
R : 
- Debug : Non optimisé, avec logs, pour tests
- Release : Optimisé, minifié, signé, pour production

**Q : Puis-je distribuer l'APK debug ?**
R : Oui, mais pour des tests seulement. Pour distribution, utilise release.

**Q : J'ai perdu ma clé de signature !**
R : Si c'était pour Play Store, impossible de mettre à jour l'app. Tu devras en publier une nouvelle. D'où l'importance de la sauvegarder !

**Q : L'APK est trop gros (>100MB) ?**
R : Active ProGuard/R8 pour réduire la taille (déjà activé dans le projet).

---

## 🎉 RÉSUMÉ ULTRA-RAPIDE

### Pour Tester :
```bash
.\gradlew assembleDebug
→ app\build\outputs\apk\debug\app-debug.apk
```

### Pour Distribuer :
```bash
1. Build > Generate Signed Bundle / APK
2. Créer une clé si besoin
3. Générer APK release
→ app\build\outputs\apk\release\app-release.apk
```

---

**Besoin d'aide pour une étape spécifique ? Dis-moi !**
