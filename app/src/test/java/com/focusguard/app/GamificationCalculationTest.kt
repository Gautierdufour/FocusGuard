package com.focusguard.app

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests unitaires pour les calculs de gamification (niveau, XP)
 * Ces tests ne nécessitent pas de contexte Android.
 */
class GamificationCalculationTest {

    @Test
    fun calculateLevel_zeroXP_returnsLevel1() {
        assertEquals(1, GamificationManager.calculateLevel(0))
    }

    @Test
    fun calculateLevel_99XP_returnsLevel1() {
        assertEquals(1, GamificationManager.calculateLevel(99))
    }

    @Test
    fun calculateLevel_100XP_returnsLevel2() {
        assertEquals(2, GamificationManager.calculateLevel(100))
    }

    @Test
    fun calculateLevel_400XP_returnsLevel3() {
        assertEquals(3, GamificationManager.calculateLevel(400))
    }

    @Test
    fun calculateLevel_900XP_returnsLevel4() {
        assertEquals(4, GamificationManager.calculateLevel(900))
    }

    @Test
    fun calculateLevel_highXP_returnsHighLevel() {
        // Level = sqrt(10000/100) + 1 = sqrt(100) + 1 = 11
        assertEquals(11, GamificationManager.calculateLevel(10000))
    }

    @Test
    fun getXPForLevel_level1_returns0() {
        assertEquals(0, GamificationManager.getXPForLevel(1))
    }

    @Test
    fun getXPForLevel_level2_returns100() {
        assertEquals(100, GamificationManager.getXPForLevel(2))
    }

    @Test
    fun getXPForLevel_level3_returns400() {
        assertEquals(400, GamificationManager.getXPForLevel(3))
    }

    @Test
    fun getXPForLevel_level5_returns1600() {
        assertEquals(1600, GamificationManager.getXPForLevel(5))
    }

    @Test
    fun levelAndXP_areConsistent() {
        // Pour chaque niveau, vérifier que getXPForLevel correspond à calculateLevel
        for (level in 1..20) {
            val xpRequired = GamificationManager.getXPForLevel(level)
            val calculatedLevel = GamificationManager.calculateLevel(xpRequired)
            assertEquals("Level $level: XP=$xpRequired should give level $level",
                level, calculatedLevel)
        }
    }

    @Test
    fun getLevelTitle_debutant() {
        assertEquals("Débutant", GamificationManager.getLevelTitle(1))
        assertEquals("Débutant", GamificationManager.getLevelTitle(2))
    }

    @Test
    fun getLevelTitle_apprenti() {
        assertEquals("Apprenti", GamificationManager.getLevelTitle(3))
        assertEquals("Apprenti", GamificationManager.getLevelTitle(4))
    }

    @Test
    fun getLevelTitle_intermediaire() {
        assertEquals("Intermédiaire", GamificationManager.getLevelTitle(5))
        assertEquals("Intermédiaire", GamificationManager.getLevelTitle(6))
    }

    @Test
    fun getLevelTitle_confirme() {
        assertEquals("Confirmé", GamificationManager.getLevelTitle(7))
    }

    @Test
    fun getLevelTitle_veteran() {
        assertEquals("Vétéran", GamificationManager.getLevelTitle(10))
    }

    @Test
    fun getLevelTitle_expert() {
        assertEquals("Expert", GamificationManager.getLevelTitle(15))
    }

    @Test
    fun getLevelTitle_maitre() {
        assertEquals("Maître", GamificationManager.getLevelTitle(20))
    }

    @Test
    fun getLevelTitle_maitreSupreme() {
        assertEquals("Maître Suprême", GamificationManager.getLevelTitle(25))
    }

    @Test
    fun getLevelTitle_legende() {
        assertEquals("Légende", GamificationManager.getLevelTitle(30))
        assertEquals("Légende", GamificationManager.getLevelTitle(50))
    }

    @Test
    fun xpConstants_arePositive() {
        assertTrue(GamificationManager.XP_BREATHING > 0)
        assertTrue(GamificationManager.XP_PUSHUPS > 0)
        assertTrue(GamificationManager.XP_WAITING > 0)
        assertTrue(GamificationManager.XP_STREAK_BONUS > 0)
    }

    @Test
    fun xpConstants_pushupsMostValuable() {
        assertTrue("Pushups should give more XP than breathing",
            GamificationManager.XP_PUSHUPS > GamificationManager.XP_BREATHING)
        assertTrue("Breathing should give more XP than waiting",
            GamificationManager.XP_BREATHING > GamificationManager.XP_WAITING)
    }
}
