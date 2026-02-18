package com.focusguard.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.color.ColorProvider
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.focusguard.app.GamificationManager
import com.focusguard.app.MainActivity
import com.focusguard.app.data.StatsRepository
import kotlinx.coroutines.runBlocking

class FocusGuardWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stats = loadWidgetStats(context)

        provideContent {
            WidgetContent(stats)
        }
    }

    private fun loadWidgetStats(context: Context): WidgetStats {
        val level = GamificationManager.getCurrentLevel(context)
        val xp = GamificationManager.getTotalXP(context)
        val streak = GamificationManager.getCurrentStreak(context)
        val title = GamificationManager.getLevelTitle(level)

        val todayBlocks = try {
            runBlocking { StatsRepository.getInstance(context).getTodayBlocks() }
        } catch (e: Exception) { 0 }

        val totalBlocks = try {
            runBlocking { StatsRepository.getInstance(context).getTotalBlocks() }
        } catch (e: Exception) { 0 }

        return WidgetStats(level, xp, streak, title, todayBlocks, totalBlocks)
    }
}

data class WidgetStats(
    val level: Int,
    val xp: Int,
    val streak: Int,
    val levelTitle: String,
    val todayBlocks: Int,
    val totalBlocks: Int
)

@Composable
fun WidgetContent(stats: WidgetStats) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(android.graphics.Color.parseColor("#FF0A0E27"))
            .cornerRadius(20.dp)
            .clickable(actionStartActivity<MainActivity>())
            .padding(16.dp)
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FocusGuard",
                    style = TextStyle(
                        color = ColorProvider(
                            day = androidx.compose.ui.graphics.Color(0xFF00D9FF),
                            night = androidx.compose.ui.graphics.Color(0xFF00D9FF)
                        ),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                if (stats.streak > 0) {
                    Text(
                        text = "\uD83D\uDD25 ${stats.streak}",
                        style = TextStyle(
                            color = ColorProvider(
                                day = androidx.compose.ui.graphics.Color(0xFFFF3D00),
                                night = androidx.compose.ui.graphics.Color(0xFFFF3D00)
                            ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Level info
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stats.levelTitle,
                        style = TextStyle(
                            color = ColorProvider(
                                day = androidx.compose.ui.graphics.Color(0xFFE8EAF6),
                                night = androidx.compose.ui.graphics.Color(0xFFE8EAF6)
                            ),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Niveau ${stats.level} - ${stats.xp} XP",
                        style = TextStyle(
                            color = ColorProvider(
                                day = androidx.compose.ui.graphics.Color(0xFFB0B3C7),
                                night = androidx.compose.ui.graphics.Color(0xFFB0B3C7)
                            ),
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Stats row
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Today blocks
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stats.todayBlocks.toString(),
                        style = TextStyle(
                            color = ColorProvider(
                                day = androidx.compose.ui.graphics.Color(0xFF00E676),
                                night = androidx.compose.ui.graphics.Color(0xFF00E676)
                            ),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Aujourd'hui",
                        style = TextStyle(
                            color = ColorProvider(
                                day = androidx.compose.ui.graphics.Color(0xFFB0B3C7),
                                night = androidx.compose.ui.graphics.Color(0xFFB0B3C7)
                            ),
                            fontSize = 10.sp
                        )
                    )
                }

                // Total blocks
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stats.totalBlocks.toString(),
                        style = TextStyle(
                            color = ColorProvider(
                                day = androidx.compose.ui.graphics.Color(0xFFFFD600),
                                night = androidx.compose.ui.graphics.Color(0xFFFFD600)
                            ),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Total",
                        style = TextStyle(
                            color = ColorProvider(
                                day = androidx.compose.ui.graphics.Color(0xFFB0B3C7),
                                night = androidx.compose.ui.graphics.Color(0xFFB0B3C7)
                            ),
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

class FocusGuardWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FocusGuardWidget()
}
