package com.focusguard.app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.sqrt

/**
 * MÉTHODE : Détection par capteur de proximité
 * Le téléphone détecte quand votre visage s'approche
 */
class ProximityPushupDetector(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    private val _pushupCount = MutableStateFlow(0)
    val pushupCount: StateFlow<Int> = _pushupCount

    private var isNearPhone = false
    private var lastDetectionTime = 0L
    private val minTimeBetweenPushups = 1000L

    fun start() {
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_PROXIMITY) return

        val distance = event.values[0]
        val maxRange = event.sensor.maximumRange
        val isCurrentlyNear = distance < maxRange / 2

        val currentTime = System.currentTimeMillis()

        // Détecter le passage de loin à près (descente)
        if (isCurrentlyNear && !isNearPhone) {
            if (currentTime - lastDetectionTime > minTimeBetweenPushups) {
                _pushupCount.value += 1
                lastDetectionTime = currentTime
            }
        }

        isNearPhone = isCurrentlyNear
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

/**
 * MÉTHODE : Détection par secousses rythmées
 * Détecte un pattern de secousses régulières
 */
class ShakePushupDetector(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _pushupCount = MutableStateFlow(0)
    val pushupCount: StateFlow<Int> = _pushupCount

    private val _feedback = MutableStateFlow("Secouez le téléphone rythmiquement")
    val feedback: StateFlow<String> = _feedback

    private val shakeHistory = mutableListOf<Long>()
    private var lastShakeTime = 0L
    private val shakeThreshold = 15f

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt(x * x + y * y + z * z)
        val currentTime = System.currentTimeMillis()

        // Détecter une secousse
        if (acceleration > shakeThreshold) {
            if (currentTime - lastShakeTime > 500L) {
                shakeHistory.add(currentTime)
                lastShakeTime = currentTime

                if (shakeHistory.size > 10) {
                    shakeHistory.removeAt(0)
                }

                if (shakeHistory.size >= 3) {
                    val intervals = mutableListOf<Long>()
                    for (i in 1 until shakeHistory.size) {
                        intervals.add(shakeHistory[i] - shakeHistory[i - 1])
                    }

                    val avgInterval = intervals.average()
                    val isRhythmic = intervals.all { kotlin.math.abs(it - avgInterval) < 500 }

                    if (isRhythmic && avgInterval in 800.0..2000.0) {
                        _pushupCount.value += 1
                        _feedback.value = "✅ Pompe ${_pushupCount.value} - Bon rythme !"
                        shakeHistory.clear()
                    }
                }
            }
        }

        if (currentTime - lastShakeTime > 5000L) {
            shakeHistory.clear()
            if (_pushupCount.value == 0) {
                _feedback.value = "Secouez rythmiquement pour commencer"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

/**
 * MÉTHODE : Combinaison de capteurs (la plus fiable)
 * Combine accéléromètre + proximité pour plus de précision
 */
class HybridPushupDetector(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    private val _pushupCount = MutableStateFlow(0)
    val pushupCount: StateFlow<Int> = _pushupCount

    private val _confidence = MutableStateFlow(0)
    val confidence: StateFlow<Int> = _confidence

    private var accelerationScore = 0
    private var proximityScore = 0
    private var lastValidationTime = 0L

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        proximity?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val currentTime = System.currentTimeMillis()

        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val acceleration = sqrt(x * x + y * y + z * z)

                if (acceleration > 12f) {
                    accelerationScore++
                }
            }
            Sensor.TYPE_PROXIMITY -> {
                val distance = event.values[0]
                val maxRange = event.sensor.maximumRange

                if (distance < maxRange / 2) {
                    proximityScore++
                }
            }
        }

        val totalScore = accelerationScore + proximityScore
        _confidence.value = (totalScore * 10).coerceAtMost(100)

        if (totalScore >= 5 && currentTime - lastValidationTime > 1500L) {
            _pushupCount.value += 1
            lastValidationTime = currentTime
            accelerationScore = 0
            proximityScore = 0
            _confidence.value = 0
        }

        if (currentTime - lastValidationTime > 3000L) {
            accelerationScore = 0
            proximityScore = 0
            _confidence.value = 0
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}