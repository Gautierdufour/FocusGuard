# 🔧 Guide d'Installation de la Correction - Détection de Pompes

## 📋 Ce Qui Va Être Corrigé

Le fichier `PushupDetector.kt` utilise actuellement l'**accéléromètre** qui détecte les mouvements du téléphone.  
Nous allons le remplacer par le **capteur de proximité** qui détecte quand vous vous approchez du téléphone.

---

## ⚠️ Avant de Commencer

### **Pré-requis**
- ✅ Android Studio installé
- ✅ Projet ouvert dans Android Studio
- ✅ Appareil Android de test (émulateur ou réel)

### **Vérifier que votre appareil a un capteur de proximité**

La plupart des smartphones modernes en ont un. Pour vérifier :

```kotlin
// Créer un fichier de test temporaire ou ajouter ce code
val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

if (proximitySensor != null) {
    Log.d("TEST", "✅ Capteur de proximité disponible")
    Log.d("TEST", "Portée maximale : ${proximitySensor.maximumRange} cm")
} else {
    Log.e("TEST", "❌ Capteur de proximité NON disponible")
}
```

Si votre appareil **n'a pas** de capteur de proximité, utilisez plutôt :
- Mode **HYBRID** (si vous avez les deux capteurs)
- Mode **MANUAL** (comptage manuel)

---

## 📝 Instructions Étape par Étape

### **Option A : Remplacement Simple** ⭐ RECOMMANDÉ

#### **Étape 1 : Sauvegarder l'ancien fichier**

1. Dans Android Studio, localisez :
   ```
   app/src/main/java/com/example/myapplication/PushupDetector.kt
   ```

2. **Clic droit** sur le fichier → **Refactor** → **Rename**

3. Renommer en :
   ```
   PushupDetector_OLD.kt
   ```

#### **Étape 2 : Renommer le fichier corrigé**

1. Localisez :
   ```
   app/src/main/java/com/example/myapplication/PushupDetector_FIXED.kt
   ```

2. **Clic droit** → **Refactor** → **Rename**

3. Renommer en :
   ```
   PushupDetector.kt
   ```

#### **Étape 3 : Clean & Rebuild**

1. Dans Android Studio :
   ```
   Build → Clean Project
   ```

2. Puis :
   ```
   Build → Rebuild Project
   ```

3. Attendez la fin de la compilation

#### **Étape 4 : Installer sur l'appareil**

1. Connectez votre appareil Android (ou lancez un émulateur)

2. Cliquez sur le bouton **Run** (▶️) dans Android Studio

3. Ou utilisez :
   ```
   Run → Run 'app'
   ```

#### **Étape 5 : Tester**

1. Ouvrez l'application
2. Allez dans les paramètres et configurez une app à bloquer
3. Démarrez le service
4. Ouvrez l'app bloquée
5. Choisissez le défi **Sport**
6. Sélectionnez **Automatique** (Mode AUTO)
7. Posez le téléphone au sol, **écran vers le haut**
8. Faites des pompes au-dessus du téléphone
9. ✅ Le compteur devrait augmenter quand vous approchez votre visage/corps

---

### **Option B : Modification Manuelle du Code**

Si vous préférez modifier directement le fichier existant :

#### **Modifications à faire dans `PushupDetector.kt`**

##### **1. Ligne 14 - Changer le capteur**
```kotlin
// ❌ AVANT
private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

// ✅ APRÈS
private val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
```

##### **2. Ligne 27 - Renommer la variable**
```kotlin
// ❌ AVANT
private val _zAxisValue = MutableStateFlow(0f)
val zAxisValue: StateFlow<Float> = _zAxisValue

// ✅ APRÈS
private val _proximityValue = MutableStateFlow(0f)
val zAxisValue: StateFlow<Float> = _proximityValue  // Nom gardé pour compatibilité UI
```

##### **3. Lignes 55-60 - Ajouter les seuils de proximité**
```kotlin
// Ajouter AVANT la déclaration des variables nearFrames/farFrames
private var maxRange = 5f
private var nearThreshold = 0f
private var farThreshold = 0f
```

##### **4. Ligne 72+ - Modifier la fonction start()**
```kotlin
fun start() {
    // ❌ AVANT
    if (accelerometer == null) {
        _feedbackMessage.value = "❌ Accéléromètre non disponible"
        Log.e(TAG, "Accéléromètre non trouvé")
        return
    }

    sensorManager.registerListener(
        this,
        accelerometer,
        SensorManager.SENSOR_DELAY_GAME
    )

    // ✅ APRÈS
    if (proximitySensor == null) {
        _feedbackMessage.value = "❌ Capteur de proximité non disponible"
        Log.e(TAG, "Capteur de proximité non trouvé")
        return
    }

    maxRange = proximitySensor.maximumRange
    nearThreshold = maxRange * 0.3f
    farThreshold = maxRange * 0.7f

    sensorManager.registerListener(
        this,
        proximitySensor,
        SensorManager.SENSOR_DELAY_GAME
    )

    _isDetecting.value = true
    _feedbackMessage.value = "📱 Posez le téléphone au sol, capteur vers le haut"
    Log.d(TAG, "✅ Détection démarrée - Portée: ${maxRange}cm")
}
```

##### **5. Ligne 110+ - Modifier onSensorChanged()**
```kotlin
override fun onSensorChanged(event: SensorEvent?) {
    // ❌ AVANT
    if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

    val x = event.values[0]
    val y = event.values[1]
    val z = event.values[2]

    _zAxisValue.value = z

    // Ajouter à l'historique pour filtrage
    zHistory.add(z)
    // ... suite du code avec machine à états basée sur Z

    // ✅ APRÈS
    if (event?.sensor?.type != Sensor.TYPE_PROXIMITY) return

    val distance = event.values[0]
    _proximityValue.value = distance

    // Machine à états basée sur la distance
    val currentTime = System.currentTimeMillis()
    val timeSinceLastChange = currentTime - lastStateChange

    when (currentState) {
        PushupPhase.REPOS -> {
            _currentPhase.value = "REPOS"
            _feedbackMessage.value = "💪 Prêt ! (${String.format("%.1f", distance)}cm)"

            if (distance < farThreshold) {
                nearFrames++
                if (nearFrames >= requiredFrames) {
                    currentState = PushupPhase.DESCENTE
                    lastStateChange = currentTime
                    nearFrames = 0
                    farFrames = 0
                }
            } else {
                nearFrames = 0
            }
        }

        PushupPhase.DESCENTE -> {
            _currentPhase.value = "DESCENTE"
            _feedbackMessage.value = "⬇️ Descendez... ${String.format("%.1f", distance)}cm"

            if (distance < nearThreshold && timeSinceLastChange > minStateTime) {
                currentState = PushupPhase.POSITION_BASSE
                lastStateChange = currentTime
            }

            if (distance > farThreshold && timeSinceLastChange > 500L) {
                currentState = PushupPhase.REPOS
                _feedbackMessage.value = "❌ Descente incomplète"
            }
        }

        PushupPhase.POSITION_BASSE -> {
            _currentPhase.value = "BAS"
            _feedbackMessage.value = "🔻 Position basse - Remontez !"

            if (distance > nearThreshold * 1.5f && timeSinceLastChange > minStateTime) {
                currentState = PushupPhase.MONTEE
                lastStateChange = currentTime
            }
        }

        PushupPhase.MONTEE -> {
            _currentPhase.value = "MONTEE"
            _feedbackMessage.value = "⬆️ Remontez... ${String.format("%.1f", distance)}cm"

            if (distance > farThreshold) {
                farFrames++
                if (farFrames >= requiredFrames && timeSinceLastChange > minStateTime) {
                    _pushupCount.value += 1
                    currentState = PushupPhase.REPOS
                    lastStateChange = currentTime
                    farFrames = 0
                    nearFrames = 0
                    _feedbackMessage.value = "✅ Pompe ${_pushupCount.value} validée !"
                }
            } else {
                farFrames = 0
            }
        }
    }
}
```

##### **6. Supprimer les fonctions inutiles**
```kotlin
// ❌ SUPPRIMER ces fonctions qui n'ont plus de sens avec le capteur de proximité
private fun calibrate() { ... }
private fun isPhoneFlat(z: Float): Boolean { ... }
```

---

## 🧪 Tests de Validation

### **Test 1 : Vérifier le capteur**
Ajoutez temporairement dans `start()` :
```kotlin
Log.d(TAG, "Capteur proximité: ${proximitySensor?.name}")
Log.d(TAG, "Portée max: ${proximitySensor?.maximumRange}cm")
```

### **Test 2 : Voir les valeurs en temps réel**
Dans `onSensorChanged()` :
```kotlin
Log.v(TAG, "Distance: ${String.format("%.2f", distance)}cm - État: $currentState")
```

### **Test 3 : Tester les seuils**
```kotlin
Log.d(TAG, "Seuils - Proche: ${nearThreshold}cm, Loin: ${farThreshold}cm")
```

---

## 🎯 Ajustements Possibles

Si la détection est **trop sensible** ou **pas assez** :

### **Rendre PLUS sensible**
```kotlin
nearThreshold = maxRange * 0.25f  // 25% au lieu de 30%
farThreshold = maxRange * 0.75f   // 75% au lieu de 70%
```

### **Rendre MOINS sensible**
```kotlin
nearThreshold = maxRange * 0.35f  // 35% au lieu de 30%
farThreshold = maxRange * 0.65f   // 65% au lieu de 70%
```

### **Ajuster le temps minimum entre états**
```kotlin
private val minStateTime = 300L  // 300ms au lieu de 200ms
```

---

## 🚨 Dépannage

### **Problème : "Capteur de proximité non disponible"**

**Cause** : L'appareil n'a pas de capteur de proximité

**Solutions** :
1. Utiliser le mode **HYBRID** (combine accel + proximité s'il y en a un)
2. Utiliser le mode **MANUAL** (comptage manuel)
3. Utiliser un autre appareil de test

---

### **Problème : Le compteur n'augmente pas**

**Diagnostic** :

1. Vérifier les logs :
   ```
   adb logcat | grep PushupDetector
   ```

2. Vérifier que le téléphone est bien **à plat**

3. Vérifier que vous faites bien des pompes **au-dessus** du téléphone

4. Ajuster les seuils (voir section Ajustements)

---

### **Problème : Trop de faux positifs**

**Solution** : Augmenter `requiredFrames`
```kotlin
private val requiredFrames = 3  // Au lieu de 2
```

---

### **Problème : Pompes non détectées même avec bonne position**

**Cause possible** : Capteur trop éloigné de votre corps

**Solutions** :
1. Positionner le téléphone **exactement sous votre torse**
2. S'assurer que le **capteur de proximité** (en haut de l'écran) est orienté **vers vous**
3. Réduire les seuils (rendre plus sensible)

---

## 📱 Position Optimale du Téléphone

```
        👤 Vous (en position de pompe)
        |
        |  <- Votre corps descend et monte
        |
    ════════════
    ║ 📷 [•] ←─── Capteur de proximité (vers vous)
    ║  📱
    ║
    ════════════
        SOL
```

**Important** :
- Capteur de proximité **vers le haut**
- Téléphone **sous votre torse**
- Distance : **20-40cm** entre votre corps et le téléphone

---

## ✅ Checklist Post-Installation

- [ ] PushupDetector.kt remplacé ou modifié
- [ ] Projet recompilé sans erreurs
- [ ] App installée sur appareil de test
- [ ] Capteur de proximité vérifié disponible
- [ ] Mode AUTO testé avec pompes réelles
- [ ] Compteur augmente correctement
- [ ] Pas de faux positifs
- [ ] Feedback visuel clair
- [ ] Logs vérifiés

---

## 🎉 Résultat Attendu

Après correction :
- ✅ Le téléphone **ne réagit plus** quand vous le **bougez**
- ✅ Le compteur **augmente** quand vous **faites des pompes** au-dessus
- ✅ Le **capteur de proximité** détecte votre corps qui s'approche
- ✅ Les **valeurs affichées** correspondent à la **distance** (en cm)

---

## 📞 Support

Si vous rencontrez des problèmes :

1. **Vérifier** les logs Android Studio
2. **Tester** avec le mode **HYBRID** pour comparer
3. **Ajuster** les seuils selon votre appareil
4. **Documenter** le problème avec logs et appareil utilisé

---

**Date de création** : 2025-01-10  
**Version** : 1.0  
**Statut** : Prêt à installer
