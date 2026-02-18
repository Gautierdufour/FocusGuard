package com.focusguard.app.data

import android.content.Context
import androidx.room.*

// ==================== ENTITIES ====================

@Entity(tableName = "block_events")
data class BlockEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "date") val date: String, // yyyy-MM-dd
    @ColumnInfo(name = "challenge_type") val challengeType: String? = null,
    @ColumnInfo(name = "time_saved_minutes") val timeSavedMinutes: Int = 0
)

@Entity(tableName = "challenge_events")
data class ChallengeEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "challenge_type") val challengeType: String,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "xp_earned") val xpEarned: Int = 0,
    @ColumnInfo(name = "time_saved_minutes") val timeSavedMinutes: Int = 0
)

@Entity(tableName = "daily_summaries")
data class DailySummary(
    @PrimaryKey val date: String, // yyyy-MM-dd
    @ColumnInfo(name = "total_blocks") val totalBlocks: Int = 0,
    @ColumnInfo(name = "total_challenges") val totalChallenges: Int = 0,
    @ColumnInfo(name = "total_time_saved") val totalTimeSaved: Int = 0,
    @ColumnInfo(name = "total_xp_earned") val totalXpEarned: Int = 0
)

// ==================== DAOs ====================

@Dao
interface BlockEventDao {
    @Insert
    suspend fun insert(event: BlockEvent): Long

    @Query("SELECT COUNT(*) FROM block_events")
    suspend fun getTotalBlocks(): Int

    @Query("SELECT COUNT(*) FROM block_events WHERE package_name = :packageName")
    suspend fun getBlocksForApp(packageName: String): Int

    @Query("SELECT SUM(time_saved_minutes) FROM block_events")
    suspend fun getTotalTimeSaved(): Int?

    @Query("SELECT SUM(time_saved_minutes) FROM block_events WHERE package_name = :packageName")
    suspend fun getTimeSavedForApp(packageName: String): Long?

    @Query("SELECT COUNT(*) FROM block_events WHERE date = :date")
    suspend fun getBlocksForDate(date: String): Int

    @Query("""
        SELECT package_name, app_name,
               COUNT(*) as block_count,
               SUM(time_saved_minutes) as total_time
        FROM block_events
        GROUP BY package_name
        ORDER BY block_count DESC
        LIMIT :limit
    """)
    suspend fun getTopBlockedApps(limit: Int = 10): List<AppBlockSummary>

    @Query("""
        SELECT date, COUNT(*) as block_count
        FROM block_events
        WHERE date >= :startDate
        GROUP BY date
        ORDER BY date ASC
    """)
    suspend fun getDailyBlockCounts(startDate: String): List<DailyBlockCount>

    @Query("""
        SELECT date, COUNT(*) as block_count
        FROM block_events
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY date
        ORDER BY date ASC
    """)
    suspend fun getDailyBlockCountsRange(startDate: String, endDate: String): List<DailyBlockCount>

    @Query("SELECT DISTINCT date FROM block_events ORDER BY date DESC")
    suspend fun getAllDates(): List<String>

    @Query("DELETE FROM block_events")
    suspend fun deleteAll()
}

@Dao
interface ChallengeEventDao {
    @Insert
    suspend fun insert(event: ChallengeEvent): Long

    @Query("SELECT COUNT(*) FROM challenge_events WHERE challenge_type = :type")
    suspend fun getCountByType(type: String): Int

    @Query("""
        SELECT challenge_type, COUNT(*) as count
        FROM challenge_events
        GROUP BY challenge_type
    """)
    suspend fun getAllChallengeCounts(): List<ChallengeCount>

    @Query("SELECT COUNT(*) FROM challenge_events WHERE challenge_type = :type")
    suspend fun getTotalCountByType(type: String): Int

    @Query("DELETE FROM challenge_events")
    suspend fun deleteAll()
}

@Dao
interface DailySummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(summary: DailySummary)

    @Query("SELECT * FROM daily_summaries WHERE date = :date")
    suspend fun getForDate(date: String): DailySummary?

    @Query("SELECT * FROM daily_summaries WHERE date >= :startDate ORDER BY date ASC")
    suspend fun getSummariesSince(startDate: String): List<DailySummary>

    @Query("SELECT * FROM daily_summaries ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentSummaries(limit: Int): List<DailySummary>

    @Query("SELECT SUM(total_blocks) FROM daily_summaries")
    suspend fun getAllTimeBlocks(): Int?

    @Query("SELECT SUM(total_time_saved) FROM daily_summaries")
    suspend fun getAllTimeSaved(): Int?

    @Query("DELETE FROM daily_summaries")
    suspend fun deleteAll()
}

// ==================== DATA CLASSES FOR QUERIES ====================

data class AppBlockSummary(
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "block_count") val blockCount: Int,
    @ColumnInfo(name = "total_time") val totalTime: Long?
)

data class DailyBlockCount(
    val date: String,
    @ColumnInfo(name = "block_count") val blockCount: Int
)

data class ChallengeCount(
    @ColumnInfo(name = "challenge_type") val challengeType: String,
    val count: Int
)

// ==================== DATABASE ====================

@Database(
    entities = [BlockEvent::class, ChallengeEvent::class, DailySummary::class],
    version = 1,
    exportSchema = false
)
abstract class StatsDatabase : RoomDatabase() {
    abstract fun blockEventDao(): BlockEventDao
    abstract fun challengeEventDao(): ChallengeEventDao
    abstract fun dailySummaryDao(): DailySummaryDao

    companion object {
        @Volatile
        private var INSTANCE: StatsDatabase? = null

        fun getInstance(context: Context): StatsDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StatsDatabase::class.java,
                    "focusguard_stats.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
