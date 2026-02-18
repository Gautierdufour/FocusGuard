package com.focusguard.app.data

import android.content.Context
import android.util.Log
import com.focusguard.app.utils.AppDisplayNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository centralisant l'accès aux statistiques.
 * Utilise Room en interne, avec migration automatique depuis SharedPreferences.
 */
class StatsRepository(private val context: Context) {

    private val db = StatsDatabase.getInstance(context)
    private val blockDao = db.blockEventDao()
    private val challengeDao = db.challengeEventDao()
    private val dailyDao = db.dailySummaryDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    companion object {
        private const val TAG = "StatsRepository"
        private const val MIGRATION_DONE_KEY = "room_migration_done"

        @Volatile
        private var INSTANCE: StatsRepository? = null

        fun getInstance(context: Context): StatsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StatsRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // ==================== RECORDING ====================

    suspend fun recordBlock(packageName: String) = withContext(Dispatchers.IO) {
        val today = dateFormat.format(Date())
        val appName = AppDisplayNames.getDisplayName(context, packageName)

        blockDao.insert(
            BlockEvent(
                packageName = packageName,
                appName = appName,
                date = today
            )
        )

        // Update daily summary
        val existing = dailyDao.getForDate(today)
        dailyDao.insertOrUpdate(
            (existing ?: DailySummary(date = today)).copy(
                totalBlocks = (existing?.totalBlocks ?: 0) + 1
            )
        )
    }

    suspend fun recordChallenge(
        challengeType: String,
        packageName: String,
        xpEarned: Int,
        timeSavedMinutes: Int
    ) = withContext(Dispatchers.IO) {
        val today = dateFormat.format(Date())

        challengeDao.insert(
            ChallengeEvent(
                challengeType = challengeType,
                packageName = packageName,
                date = today,
                xpEarned = xpEarned,
                timeSavedMinutes = timeSavedMinutes
            )
        )

        // Update block event with challenge info
        val existing = dailyDao.getForDate(today)
        dailyDao.insertOrUpdate(
            (existing ?: DailySummary(date = today)).copy(
                totalChallenges = (existing?.totalChallenges ?: 0) + 1,
                totalTimeSaved = (existing?.totalTimeSaved ?: 0) + timeSavedMinutes,
                totalXpEarned = (existing?.totalXpEarned ?: 0) + xpEarned
            )
        )
    }

    // ==================== QUERIES ====================

    suspend fun getTotalBlocks(): Int = withContext(Dispatchers.IO) {
        blockDao.getTotalBlocks()
    }

    suspend fun getTotalTimeSaved(): Int = withContext(Dispatchers.IO) {
        blockDao.getTotalTimeSaved() ?: 0
    }

    suspend fun getTopBlockedApps(limit: Int = 10): List<AppBlockSummary> = withContext(Dispatchers.IO) {
        blockDao.getTopBlockedApps(limit)
    }

    suspend fun getChallengeCounts(): List<ChallengeCount> = withContext(Dispatchers.IO) {
        challengeDao.getAllChallengeCounts()
    }

    suspend fun getWeeklyStats(): List<DailyBlockCount> = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = dateFormat.format(cal.time)
        blockDao.getDailyBlockCounts(startDate)
    }

    suspend fun getMonthlyStats(): List<DailyBlockCount> = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -29)
        val startDate = dateFormat.format(cal.time)
        blockDao.getDailyBlockCounts(startDate)
    }

    suspend fun getBlocksForDate(date: String): Int = withContext(Dispatchers.IO) {
        blockDao.getBlocksForDate(date)
    }

    suspend fun getTodayBlocks(): Int = withContext(Dispatchers.IO) {
        val today = dateFormat.format(Date())
        blockDao.getBlocksForDate(today)
    }

    suspend fun getDailySummariesSince(startDate: String): List<DailySummary> = withContext(Dispatchers.IO) {
        dailyDao.getSummariesSince(startDate)
    }

    suspend fun getChallengeCountByType(type: String): Int = withContext(Dispatchers.IO) {
        challengeDao.getTotalCountByType(type)
    }

    suspend fun getBlocksForApp(packageName: String): Int = withContext(Dispatchers.IO) {
        blockDao.getBlocksForApp(packageName)
    }

    suspend fun getTimeSavedForApp(packageName: String): Long = withContext(Dispatchers.IO) {
        blockDao.getTimeSavedForApp(packageName) ?: 0L
    }

    suspend fun getDailyBlockCountsRange(startDate: String, endDate: String): List<DailyBlockCount> = withContext(Dispatchers.IO) {
        blockDao.getDailyBlockCountsRange(startDate, endDate)
    }

    // ==================== RESET ====================

    suspend fun resetAllStats() = withContext(Dispatchers.IO) {
        blockDao.deleteAll()
        challengeDao.deleteAll()
        dailyDao.deleteAll()
    }

    // ==================== MIGRATION ====================

    suspend fun migrateFromSharedPreferencesIfNeeded() = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences("app_blocker_stats", Context.MODE_PRIVATE)
        val migrationPrefs = context.getSharedPreferences("migration", Context.MODE_PRIVATE)

        if (migrationPrefs.getBoolean(MIGRATION_DONE_KEY, false)) return@withContext

        try {
            val totalBlocks = prefs.getInt("total_blocks", 0)
            if (totalBlocks == 0) {
                migrationPrefs.edit().putBoolean(MIGRATION_DONE_KEY, true).apply()
                return@withContext
            }

            Log.d(TAG, "Migration depuis SharedPreferences: $totalBlocks blocs")

            val today = dateFormat.format(Date())

            // Migrer les stats par app
            val apps = listOf(
                "com.instagram.android" to "instagram",
                "com.google.android.youtube" to "youtube",
                "com.zhiliaoapp.musically" to "tiktok",
                "com.facebook.katana" to "facebook",
                "com.snapchat.android" to "snapchat",
                "com.twitter.android" to "twitter"
            )

            var migratedBlocks = 0
            apps.forEach { (pkg, key) ->
                val blocksCount = prefs.getInt("blocks_$key", 0)
                val timeSaved = prefs.getLong("time_saved_$key", 0L)
                val appName = AppDisplayNames.getDisplayName(context, pkg)

                if (blocksCount > 0) {
                    // Créer un événement agrégé pour la migration
                    blockDao.insert(
                        BlockEvent(
                            packageName = pkg,
                            appName = appName,
                            date = today,
                            timeSavedMinutes = timeSaved.toInt()
                        )
                    )
                    // Add remaining as separate events for count accuracy
                    repeat(blocksCount - 1) {
                        blockDao.insert(
                            BlockEvent(
                                packageName = pkg,
                                appName = appName,
                                date = today
                            )
                        )
                    }
                    migratedBlocks += blocksCount
                }
            }

            // Migrer les challenges
            val challengeTypes = listOf("breathing", "pushups", "waiting", "quiz", "math", "puzzle", "meditation")
            challengeTypes.forEach { type ->
                val count = prefs.getInt("completed_$type", 0)
                repeat(count) {
                    challengeDao.insert(
                        ChallengeEvent(
                            challengeType = type,
                            packageName = "",
                            date = today
                        )
                    )
                }
            }

            // Migrer le résumé quotidien
            val totalTimeSaved = prefs.getInt("total_time_saved", 0)
            dailyDao.insertOrUpdate(
                DailySummary(
                    date = today,
                    totalBlocks = migratedBlocks,
                    totalTimeSaved = totalTimeSaved
                )
            )

            migrationPrefs.edit().putBoolean(MIGRATION_DONE_KEY, true).apply()
            Log.d(TAG, "Migration terminée: $migratedBlocks blocs migrés")

        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la migration", e)
        }
    }
}
