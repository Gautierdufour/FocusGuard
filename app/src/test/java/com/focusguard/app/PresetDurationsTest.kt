package com.focusguard.app

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests unitaires pour PresetDurations (formatage, listes de valeurs)
 */
class PresetDurationsTest {

    @Test
    fun formatDuration_seconds() {
        assertEquals("30s", PresetDurations.formatDuration(30))
        assertEquals("45s", PresetDurations.formatDuration(45))
    }

    @Test
    fun formatDuration_exactMinutes() {
        assertEquals("1min", PresetDurations.formatDuration(60))
        assertEquals("2min", PresetDurations.formatDuration(120))
        assertEquals("5min", PresetDurations.formatDuration(300))
    }

    @Test
    fun formatDuration_minutesAndSeconds() {
        assertEquals("1min 30s", PresetDurations.formatDuration(90))
        assertEquals("2min 30s", PresetDurations.formatDuration(150))
    }

    @Test
    fun accessDurations_areSorted() {
        val durations = PresetDurations.ACCESS_DURATIONS
        assertEquals(durations.sorted(), durations)
    }

    @Test
    fun pushupCounts_areSorted() {
        val counts = PresetDurations.PUSHUP_COUNTS
        assertEquals(counts.sorted(), counts)
    }

    @Test
    fun waitingDurations_areSorted() {
        val durations = PresetDurations.WAITING_DURATIONS
        assertEquals(durations.sorted(), durations)
    }

    @Test
    fun breathingDurations_areSorted() {
        val durations = PresetDurations.BREATHING_DURATIONS
        assertEquals(durations.sorted(), durations)
    }

    @Test
    fun allPresets_haveMultipleOptions() {
        assertTrue(PresetDurations.ACCESS_DURATIONS.size >= 2)
        assertTrue(PresetDurations.PUSHUP_COUNTS.size >= 2)
        assertTrue(PresetDurations.WAITING_DURATIONS.size >= 2)
        assertTrue(PresetDurations.BREATHING_DURATIONS.size >= 2)
        assertTrue(PresetDurations.QUIZ_COUNTS.size >= 2)
        assertTrue(PresetDurations.MATH_COUNTS.size >= 2)
        assertTrue(PresetDurations.DAILY_GOALS.size >= 2)
    }

    @Test
    fun allPresets_havePositiveValues() {
        PresetDurations.ACCESS_DURATIONS.forEach { assertTrue(it > 0) }
        PresetDurations.PUSHUP_COUNTS.forEach { assertTrue(it > 0) }
        PresetDurations.WAITING_DURATIONS.forEach { assertTrue(it > 0) }
        PresetDurations.BREATHING_DURATIONS.forEach { assertTrue(it > 0) }
        PresetDurations.DAILY_GOALS.forEach { assertTrue(it > 0) }
    }
}
