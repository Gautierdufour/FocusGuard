package com.focusguard.app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.ToneGenerator
import android.media.AudioManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Détecteur de pompes optimisé pour capteurs de proximité BINAIRES
 * Compatible avec les capteurs qui ne renvoient que 2 valeurs (0cm et max)
 */
class PushupDetector(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    // Générateur de son pour feedback
    private var toneGenerator: ToneGenerator? = null

    private val _pushupCount = MutableStateFlow(0)
    val pushupCount: StateFlow<Int> = _pushupCount

    private val _isDetecting = MutableStateFlow(false)
    val isDetecting: StateFlow<Boolean> = _isDetecting

    /** False si le capteur de proximité est absent sur cet appareil */
    val sensorAvailable: Boolean = proximitySensor != null

    private val _feedbackMessage = MutableStateFlow("Placez le téléphone au sol, capteur vers le haut")
    val feedbackMessage: StateFlow<String> = _feedbackMessage

    private val _currentPhase = MutableStateFlow("REPOS")
    val currentPhase: StateFlow<String> = _currentPhase

    private val _proximityValue = MutableStateFlow(0f)
    val zAxisValue: StateFlow<Float> = _proximityValue

    // États simplifiés pour capteur binaire
    private enum class SimplePushupPhase {
        WAIT_FAR,    // Attente position haute (loin)
        WAIT_NEAR    // Attente position basse (proche)
    }

    private var currentState = SimplePushupPhase.WAIT_FAR
    private var lastTransitionTime = 0L
    private val minTransitionTime = 300L // Temps minimum entre transitions (anti-rebond)

    private var maxRange = 5f
    private var isNear = false
    private var lastIsNear = false

    companion object {
        private const val TAG = "PushupDetector"
    }

    fun start() {
        if (proximitySensor == null) {
            _feedbackMessage.value = "❌ Capteur de proximité non disponible"
            Log.e(TAG, "Capteur de proximité non trouvé sur cet appareil")
            return
        }

        // Initialiser le générateur de son
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            Log.d(TAG, "✅ Générateur de son initialisé")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erreur initialisation son: ${e.message}")
        }

        maxRange = proximitySensor.maximumRange
        
        sensorManager.registerListener(
            this,
            proximitySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        _isDetecting.value = true
        _feedbackMessage.value = "📱 Posez le téléphone au sol, faites des pompes au-dessus"
        Log.d(TAG, "✅ Détection démarrée - Portée capteur: ${maxRange}cm")
        Log.d(TAG, "Mode: Capteur binaire (détecte proche/loin uniquement)")
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        _isDetecting.value = false
        
        // Libérer le générateur de son
        toneGenerator?.release()
        toneGenerator = null
        
        Log.d(TAG, "🛑 Détection arrêtée - Total: ${_pushupCount.value} pompes")
    }

    fun reset() {
        _pushupCount.value = 0
        currentState = SimplePushupPhase.WAIT_FAR
        isNear = false
        lastIsNear = false
        _feedbackMessage.value = "📱 Posez le téléphone au sol, faites des pompes au-dessus"
        _currentPhase.value = "REPOS"
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_PROXIMITY) return

        val distance = event.values[0]
        _proximityValue.value = distance

        // Déterminer si on est "proche" ou "loin"
        // Pour capteur binaire : 0 = proche, maxRange = loin
        isNear = distance < maxRange / 2

        Log.v(TAG, "Distance: ${String.format("%.1f", distance)}cm - État: ${if (isNear) "PROCHE" else "LOIN"}")

        val currentTime = System.currentTimeMillis()
        val timeSinceLastTransition = currentTime - lastTransitionTime

        // Ignorer si transition trop rapide (anti-rebond)
        if (timeSinceLastTransition < minTransitionTime) {
            return
        }

        // Détecter les transitions uniquement
        if (isNear != lastIsNear) {
            handleTransition(isNear, currentTime, distance)
            lastIsNear = isNear
            lastTransitionTime = currentTime
        }
    }

    private fun handleTransition(isNowNear: Boolean, currentTime: Long, distance: Float) {
        when (currentState) {
            SimplePushupPhase.WAIT_FAR -> {
                if (isNowNear) {
                    // Transition LOIN → PROCHE : début de descente
                    _currentPhase.value = "DESCENTE"
                    _feedbackMessage.value = "⬇️ Descente détectée !"
                    currentState = SimplePushupPhase.WAIT_NEAR
                    Log.d(TAG, "⬇️ DESCENTE (${String.format("%.1f", distance)}cm)")
                }
            }

            SimplePushupPhase.WAIT_NEAR -> {
                if (!isNowNear) {
                    // Transition PROCHE → LOIN : fin de pompe
                    _pushupCount.value += 1
                    _currentPhase.value = "REPOS"
                    _feedbackMessage.value = "✅ Pompe ${_pushupCount.value} validée !"
                    currentState = SimplePushupPhase.WAIT_FAR
                    
                    // 🔊 JOUER LE SON DE VALIDATION
                    playSuccessSound()
                    
                    Log.d(TAG, "✅ POMPE ${_pushupCount.value} VALIDÉE ! (${String.format("%.1f", distance)}cm)")
                }
            }
        }
    }

    /**
     * Joue un son de validation quand une pompe est réussie
     */
    private fun playSuccessSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 150) // Beep court de 150ms
            Log.v(TAG, "🔊 Son de validation joué")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erreur lecture son: ${e.message}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        when (accuracy) {
            SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                Log.w(TAG, "⚠️ Capteur peu fiable")
                _feedbackMessage.value = "⚠️ Nettoyez le capteur de proximité"
            }
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
                Log.w(TAG, "⚠️ Précision basse")
            }
        }
    }
}
