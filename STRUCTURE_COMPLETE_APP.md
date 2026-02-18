# 📂 Structure Complète de l'Application FocusGuard

## 🎯 Vue d'ensemble
Application Android de blocage d'applications avec défis (pompes, respiration, patience) pour débloquer l'accès.

---

## 📁 Fichiers Source Principaux

### **📱 Activities**

#### `MainActivity.kt`
- **Rôle** : Écran d'accueil principal
- **Fonctions** :
  - Affichage du statut du service
  - Gestion des applications sélectionnées
  - Contrôle démarrage/arrêt du service de monitoring
  - Vérification des permissions
  - Navigation vers paramètres et statistiques
- **UI** : Interface moderne avec cartes (Hero, Apps, Contrôle, Permissions, Status)

#### `LockActivity.kt`
- **Rôle** : Écran de blocage affiché quand une app est bloquée
- **Fonctions** :
  - Menu de sélection des défis (Respiration, Pompes, Patience)
  - Enregistrement des blocages et défis complétés
  - Interface utilisateur des 3 défis
  - Système de validation et retour à l'accueil
- **Défis disponibles** :
  - **Respiration** : Exercice de pleine conscience temporisé
  - **Pompes** : Détection automatique ou manuelle
  - **Patience** : Temps d'attente avant accès

#### `SettingsActivity.kt`
- **Rôle** : Configuration des paramètres de l'application
- **Paramètres** :
  - Durée d'attente (patience)
  - Durée d'accès après défi
  - Durée de respiration
  - Nombre de pompes requis

#### `AppSettingsActivity.kt`
- **Rôle** : Sélection des applications à surveiller
- **Fonctions** :
  - Liste des applications installées
  - Sélection/désélection des apps à bloquer
  - Sauvegarde des préférences

#### `StatisticsActivity.kt`
- **Rôle** : Affichage des statistiques d'utilisation
- **Données affichées** :
  - Nombre total de blocages
  - Défis complétés par type
  - Temps économisé
  - Statistiques par application

---

### **🔧 Services et Composants**

#### `MonitorService.kt`
- **Rôle** : Service en arrière-plan qui surveille l'usage des applications
- **Fonctions** :
  - Surveillance continue des applications ouvertes
  - Détection des apps bloquées
  - Lancement de LockActivity si app bloquée détectée
  - Notification de service actif
  - Gestion du wake lock pour maintenir l'activité
  - Redémarrage automatique en cas d'arrêt

#### `BootReceiver.kt`
- **Rôle** : Redémarre le service au démarrage du téléphone
- **Fonction** : Écoute l'événement `BOOT_COMPLETED`

#### `ServiceWatchdogWorker.kt`
- **Rôle** : Surveille que MonitorService reste actif
- **Fonction** : WorkManager qui vérifie et redémarre le service si nécessaire

---

### **🏋️ Système de Détection de Pompes**

#### `PushupDetector.kt` ❌ **BUGUÉ - À CORRIGER**
- **Rôle** : Méthode AUTO de détection (recommandée)
- **Problème** : Utilise l'**ACCÉLÉROMÈTRE** au lieu du **CAPTEUR DE PROXIMITÉ**
- **Capteur actuel** : `Sensor.TYPE_ACCELEROMETER`
- **Capteur requis** : `Sensor.TYPE_PROXIMITY`
- **Machine à états** : REPOS → DESCENTE → POSITION_BASSE → MONTÉE
- **Symptôme** : Détecte les mouvements du téléphone au lieu de la proximité

#### `PushupDetector_FIXED.kt` ✅ **CORRECTION DISPONIBLE**
- **Rôle** : Version corrigée utilisant le capteur de proximité
- **Capteur** : `Sensor.TYPE_PROXIMITY`
- **Fonctionnement** :
  - Détecte la distance d'objets devant le capteur
  - Seuils : `nearThreshold` (proche), `farThreshold` (loin)
  - Machine à états adaptée aux distances
- **À faire** : Remplacer `PushupDetector.kt` par cette version

#### `ProximityPushupDetector` ✅ **OK**
- **Rôle** : Méthode PROXIMITY
- **Capteur** : `Sensor.TYPE_PROXIMITY`
- **Fonctionnement** : Détecte simplement les passages de loin → près

#### `HybridPushupDetector` ✅ **OK**
- **Rôle** : Méthode HYBRID (double vérification)
- **Capteurs** : `ACCELEROMETER` + `PROXIMITY`
- **Fonctionnement** :
  - Score combiné des deux capteurs
  - Valide une pompe si score total suffisant
  - Affiche un niveau de confiance

#### `ShakePushupDetector` ✅ **OK**
- **Rôle** : Méthode SHAKE (secousses)
- **Capteur** : `Sensor.TYPE_ACCELEROMETER`
- **Fonctionnement** : Détecte des secousses rythmées régulières

#### `PushupMethodSelector.kt`
- **Rôle** : Interface de sélection de la méthode de détection
- **Méthodes disponibles** :
  - AUTO (★ recommandée - actuellement bugée)
  - HYBRID
  - PROXIMITY
  - SHAKE
  - MANUAL
- **Fonctions** :
  - Détection des capteurs disponibles
  - Affichage des instructions par méthode
  - Cartes de sélection avec icônes

#### `PushupAlternatives.kt`
- **Rôle** : Contient les 3 détecteurs alternatifs
- **Classes** :
  - `ProximityPushupDetector`
  - `ShakePushupDetector`
  - `HybridPushupDetector`

---

### **🎨 UI et Thème**

#### `AppColors.kt`
- **Rôle** : Palette de couleurs de l'application
- **Couleurs principales** :
  - Primary, Secondary, Accent
  - Success, Warning, Error, Info
  - Surface, Background, variants

#### `Color.kt`, `Theme.kt`, `Type.kt` (ui/theme/)
- **Rôle** : Configuration Jetpack Compose Material3
- **Contenu** : Thème sombre, typographie, formes

---

### **💾 Gestion des Données**

#### `PreferencesManager.kt`
- **Rôle** : Gestion centralisée des SharedPreferences
- **Préférences** :
  - `app_blocker_settings` : Apps sélectionnées
  - `lock` : Permissions d'accès temporaires
  - `app_blocker_stats` : Statistiques

#### `PreferencesReset.kt`
- **Rôle** : Utilitaire pour réinitialiser les préférences
- **Usage** : Debug et tests

#### `AppPreferences.kt`
- **Rôle** : Accès aux paramètres utilisateur
- **Valeurs** :
  - Durée d'attente
  - Durée de respiration
  - Nombre de pompes
  - Durée d'accès

---

### **🔍 Utilitaires**

#### `AppScanner.kt`
- **Rôle** : Scan des applications installées
- **Fonction** : Liste toutes les apps non-système avec leurs infos

---

## 📄 Fichiers de Configuration

### `AndroidManifest.xml`
- Permissions requises
- Déclaration des Activities
- Déclaration du Service
- Déclaration du BroadcastReceiver
- Configuration de WorkManager

### `build.gradle` (app level)
- Dépendances :
  - Jetpack Compose
  - Material3
  - Coroutines
  - WorkManager
  - Lifecycle & ViewModel

---

## 🐛 Problèmes Identifiés

### **1. PushupDetector.kt - Mauvais capteur** ❌ CRITIQUE
- **Fichier** : `PushupDetector.kt`
- **Ligne** : 14
- **Problème** : Utilise `TYPE_ACCELEROMETER` au lieu de `TYPE_PROXIMITY`
- **Impact** : Le mode AUTO ne fonctionne pas correctement
- **Solution** : Remplacer par `PushupDetector_FIXED.kt`

---

## ✅ État des Composants

| Composant | État | Notes |
|-----------|------|-------|
| MainActivity | ✅ OK | Interface fonctionnelle |
| LockActivity | ✅ OK | Défis fonctionnels |
| SettingsActivity | ✅ OK | Paramètres OK |
| AppSettingsActivity | ✅ OK | Sélection apps OK |
| StatisticsActivity | ✅ OK | Stats OK |
| MonitorService | ✅ OK | Surveillance fonctionnelle |
| BootReceiver | ✅ OK | Redémarrage OK |
| ServiceWatchdog | ✅ OK | Surveillance OK |
| **PushupDetector (AUTO)** | ❌ **BUGUÉ** | **Mauvais capteur** |
| ProximityDetector | ✅ OK | Fonctionne bien |
| HybridDetector | ✅ OK | Fonctionne bien |
| ShakeDetector | ✅ OK | Fonctionne bien |
| Manual | ✅ OK | Toujours fonctionnel |
| PushupMethodSelector | ✅ OK | Sélection OK |
| AppColors | ✅ OK | Thème OK |
| PreferencesManager | ✅ OK | Sauvegarde OK |
| AppScanner | ✅ OK | Scan apps OK |

---

## 🔄 Flux de l'Application

```
[Démarrage App]
    ↓
[MainActivity]
    ├─→ [AppSettingsActivity] → Sélectionner apps à bloquer
    ├─→ [SettingsActivity] → Configurer paramètres
    ├─→ [StatisticsActivity] → Voir statistiques
    └─→ Démarrer Service
        ↓
    [MonitorService] (Surveillance continue)
        ↓
    App bloquée détectée
        ↓
    [LockActivity]
        ├─→ Défi Respiration → Valide → Accès temporaire
        ├─→ Défi Pompes → [PushupMethodSelector] → [PushupDetector] → Valide → Accès
        └─→ Défi Patience → Attente → Valide → Accès
```

---

## 📊 Statistiques et Logs

### **SharedPreferences utilisées**
- `app_blocker_settings` : Configuration générale
- `lock` : Gestion des accès temporaires
- `app_blocker_stats` : Statistiques d'utilisation

### **Logs principaux**
- `MainActivity` : Navigation et statut service
- `MonitorService` : Surveillance et détections
- `LockActivity` : Défis et validations
- `PushupDetector` : Détection pompes (DEBUG très verbeux)

---

## 🚀 À Faire (TODO)

### **Priorité HAUTE** 🔴
1. ✅ **Corriger PushupDetector.kt**
   - Remplacer par PushupDetector_FIXED.kt
   - Tester sur appareil réel

### **Priorité MOYENNE** 🟡
2. Améliorer feedback visuel mode AUTO
3. Ajouter calibration automatique des seuils
4. Optimiser consommation batterie du service

### **Priorité BASSE** 🟢
5. Ajouter plus de défis (squats, abdos, etc.)
6. Mode sombre/clair configurable
7. Export des statistiques
8. Graphiques d'évolution

---

## 📞 Informations Techniques

- **Langage** : Kotlin
- **Framework UI** : Jetpack Compose
- **Min SDK** : 24 (Android 7.0)
- **Target SDK** : 34 (Android 14)
- **Architecture** : MVVM avec StateFlow
- **Permissions** :
  - `USAGE_STATS` : Surveillance apps
  - `SYSTEM_ALERT_WINDOW` : Overlay LockActivity
  - `FOREGROUND_SERVICE` : Service persistant
  - `RECEIVE_BOOT_COMPLETED` : Redémarrage auto

---

**Dernière mise à jour** : 2025-01-10  
**Version documentation** : 1.0
