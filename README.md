# 🛡️ Focus Guard - Bloquez vos distractions numériques

<div align="center">

**Application Android de blocage d'applications avec défis physiques et mentaux**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![Status](https://img.shields.io/badge/Status-Ready%20to%20Compile-brightgreen.svg)](#)

[📚 Documentation](#documentation) • [🚀 Démarrage Rapide](#démarrage-rapide) • [✨ Fonctionnalités](#fonctionnalités) • [🔧 Installation](#installation)

</div>

---

## 📖 Description

**Focus Guard** est une application Android innovante qui vous aide à reprendre le contrôle de votre temps d'écran en bloquant les applications qui vous distraient. Pour débloquer une application, vous devez relever un défi physique ou mental, transformant chaque tentative de distraction en opportunité de développement personnel.

### 🎯 Pourquoi Focus Guard ?

- 🚫 **Blocage intelligent** : Surveillez et bloquez automatiquement vos apps addictives
- 💪 **Défis physiques** : Faites des pompes pour débloquer (détection automatique par capteur)
- 🧠 **Défis mentaux** : Résolvez des calculs ou exercices de respiration
- 📊 **Statistiques détaillées** : Suivez votre temps économisé et votre progression
- 🏆 **Gamification** : Niveaux, badges, XP et streaks pour rester motivé
- 🔄 **Service persistant** : Fonctionne en arrière-plan, redémarre automatiquement

---

## ✨ Fonctionnalités

### 🔒 Blocage d'Applications
- Surveillance en temps réel des applications actives
- Liste personnalisable d'applications à bloquer
- Écran de verrouillage instantané
- Service foreground persistant

### 💪 Types de Défis

| Défi | Description | Capteur |
|------|-------------|---------|
| **🏋️ Pompes** | Détection automatique par capteur de proximité | ✅ AUTO |
| **🫁 Respiration** | Exercices de cohérence cardiaque guidés | - |
| **🧮 Cognitif** | Calculs mentaux et défis logiques | - |
| **⏱️ Attente** | Timer de réflexion obligatoire | - |
| **✋ Manuel** | Déblocage par bouton | - |

### 📊 Statistiques & Gamification
- Nombre de blocages par application
- Temps total économisé
- Score d'addictivité par app
- Graphiques d'activité hebdomadaire
- Système de niveaux (1-50) et XP
- 15+ badges à débloquer
- Système de streaks quotidiens

### ⚙️ Paramètres Avancés
- Planification intelligente (blocage selon lieu/heure)
- Sélection des méthodes de défis
- Personnalisation des seuils de détection
- Gestion des notifications

---

## 🚀 Démarrage Rapide

### Prérequis
- Android Studio Arctic Fox ou supérieur
- Android SDK 26+ (Android 8.0+)
- Appareil Android avec capteur de proximité (recommandé)

### Compilation Rapide

**Option 1 : Script Automatique** (Recommandé)
```bash
# Double-cliquer sur :
compile.bat
```

**Option 2 : Ligne de Commande**
```bash
git clone <votre-repo>
cd MyApplication2
.\gradlew clean assembleDebug
```

**Option 3 : Android Studio**
```
File > Open > MyApplication2
Build > Make Project (Ctrl+F9)
```

### 📱 Installation

L'APK généré se trouve dans :
```
app/build/outputs/apk/debug/app-debug.apk
```

**Installation via ADB :**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔧 Configuration

### Permissions Nécessaires

#### ⚠️ OBLIGATOIRES
1. **Usage des données d'utilisation**
   - `Paramètres > Applications spéciales > Accès aux données d'utilisation`
   - Activer Focus Guard

2. **Affichage par-dessus d'autres applications**
   - `Paramètres > Applications spéciales > Afficher par-dessus`
   - Activer Focus Guard

#### 📢 RECOMMANDÉES
- **Notifications** : Pour le service en arrière-plan
- **Désactiver optimisation batterie** : Pour persistance maximale

#### 📍 OPTIONNELLES
- **Localisation** : Si vous utilisez la planification intelligente

---

## 📚 Documentation

### Documents Principaux

| Document | Description | Priorité |
|----------|-------------|----------|
| **[INDEX.md](INDEX.md)** | Guide complet de navigation | ⭐⭐⭐ |
| **[RESUME_CORRECTIONS.md](RESUME_CORRECTIONS.md)** | Résumé des corrections appliquées | ⭐⭐⭐ |
| **[PLAN_ACTION.md](PLAN_ACTION.md)** | Plan étape par étape | ⭐⭐⭐ |
| **[GUIDE_RAPIDE.md](GUIDE_RAPIDE.md)** | Guide de démarrage | ⭐⭐ |
| **[ETAT_PROJET.md](ETAT_PROJET.md)** | État complet du projet | ⭐⭐ |
| **[DIAGNOSTIC_ET_CORRECTION.md](DIAGNOSTIC_ET_CORRECTION.md)** | Détails techniques | ⭐ |

### 🗺️ Parcours Recommandé

1. 📖 Lire [RESUME_CORRECTIONS.md](RESUME_CORRECTIONS.md) (2 min)
2. 🔨 Suivre [PLAN_ACTION.md](PLAN_ACTION.md) (30 min)
3. 📱 Consulter [GUIDE_RAPIDE.md](GUIDE_RAPIDE.md) si problèmes

---

## 🏗️ Architecture

### Technologies Utilisées
- **Language** : Kotlin 1.9.22
- **UI Framework** : Jetpack Compose
- **Architecture** : MVVM + StateFlow
- **Services** : Foreground Service + WorkManager
- **Capteurs** : Proximity Sensor, Accelerometer
- **Notifications** : Android NotificationManager
- **Stockage** : SharedPreferences

### Structure du Projet
```
app/src/main/java/com/example/myapplication/
├── 📱 Activités
│   ├── MainActivity.kt              # Écran principal
│   ├── LockActivity.kt              # Écran de blocage
│   ├── StatisticsActivity.kt        # Statistiques
│   ├── SettingsActivity.kt          # Paramètres
│   ├── AppSettingsActivity.kt       # Sélection des apps
│   └── SmartPlanningActivity.kt     # Planification
│
├── 🔧 Services
│   ├── MonitorService.kt            # Surveillance des apps
│   └── ServiceWatchdogWorker.kt     # Relance automatique
│
├── 🎮 Détecteurs de Défis
│   ├── PushupDetector.kt            # Détection pompes (proximité)
│   ├── CognitiveChallenges.kt       # Défis cognitifs
│   └── PushupMethodSelector.kt      # Sélection méthode
│
├── 📊 Managers
│   ├── GamificationManager.kt       # Système XP/badges
│   ├── PreferencesManager.kt        # Gestion préférences
│   └── AppScanner.kt                # Scanner d'apps
│
├── 🎨 UI
│   ├── AppColors.kt                 # Thème dark moderne
│   └── ui/theme/                    # Thème Compose
│
└── 🔄 Receivers
    └── BootReceiver.kt              # Redémarrage auto
```

### Fichiers Clés Corrigés ✅
- **PushupDetector.kt** : Utilise capteur de proximité (au lieu d'accéléromètre)
- **StatisticsActivity.kt** : Icônes et couleurs corrigées
- **AndroidManifest.xml** : Toutes permissions déclarées
- **build.gradle.kts** : Dépendances complètes

---

## 🧪 Tests

### Tests Essentiels

```bash
# Test 1 : Scanner d'apps
1. Lancer Focus Guard
2. "Sélectionner les applications"
3. Vérifier la liste d'apps

# Test 2 : Blocage
1. Sélectionner des apps
2. Activer le blocage
3. Lancer une app bloquée
4. Vérifier l'écran de blocage

# Test 3 : Pompes
1. Choisir "Faire des pompes"
2. Poser téléphone au sol
3. Faire 5 pompes
4. Vérifier le compteur

# Test 4 : Persistance
1. Redémarrer le téléphone
2. Vérifier que le service est actif
3. Tester le blocage
```

### Commandes de Debug

```bash
# Logs en temps réel
adb logcat | findstr "myapplication"

# Logs détecteur de pompes
adb logcat | findstr "PushupDetector"

# Logs service de monitoring
adb logcat | findstr "MonitorService"

# Réinstaller l'app
adb uninstall com.example.myapplication
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🐛 Résolution de Problèmes

### Le blocage ne fonctionne pas
**Solution :** Vérifier les permissions "Usage des données" et "Affichage par-dessus"

### Le détecteur de pompes ne répond pas
**Solutions :**
1. Nettoyer le capteur de proximité
2. Vérifier les logs : `adb logcat | findstr "PushupDetector"`
3. Essayer le mode HYBRID

### L'app ne redémarre pas après reboot
**Solution :** Désactiver l'optimisation de batterie pour Focus Guard

### Erreur de compilation
**Solutions :**
```bash
.\gradlew clean
File > Invalidate Caches > Invalidate and Restart (Android Studio)
```

Pour plus de détails : [PLAN_ACTION.md - Section Problèmes](PLAN_ACTION.md)

---

## 📈 Roadmap

### ✅ Version 1.0 (Actuelle)
- [x] Blocage d'applications
- [x] Défis variés (pompes, respiration, cognitif)
- [x] Statistiques détaillées
- [x] Gamification complète
- [x] Service persistant

### 🔮 Version 1.1 (Planifiée)
- [ ] Support multi-langues
- [ ] Thème clair
- [ ] Export des statistiques
- [ ] Synchronisation cloud
- [ ] Défis personnalisés

### 🚀 Version 2.0 (Future)
- [ ] Mode famille/groupe
- [ ] IA adaptative
- [ ] Intégration wearables
- [ ] Communauté & challenges

---

## 🤝 Contribution

Les contributions sont les bienvenues ! 

### Comment contribuer :
1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. Push sur la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

### Guidelines :
- Suivre le style Kotlin existant
- Commenter les fonctions complexes
- Tester sur plusieurs appareils
- Mettre à jour la documentation

---

## 📄 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

## 👥 Auteurs

- **Gautier** - *Développeur principal* - [@gauti](https://github.com/gauti)

---

## 🙏 Remerciements

- Jetpack Compose team pour l'excellent framework UI
- La communauté Android pour les ressources et tutoriels
- Tous les contributeurs qui ont aidé à améliorer ce projet

---

## 📞 Support

- 📧 Email : [support@focusguard.app](mailto:support@focusguard.app)
- 🐛 Issues : [GitHub Issues](https://github.com/votre-repo/issues)
- 📚 Documentation : [INDEX.md](INDEX.md)

---

<div align="center">

**Fait avec ❤️ pour vous aider à reprendre le contrôle de votre temps**

⭐ Si ce projet vous aide, n'hésitez pas à lui donner une étoile !

[⬆ Retour en haut](#-focus-guard---bloquez-vos-distractions-numériques)

</div>
