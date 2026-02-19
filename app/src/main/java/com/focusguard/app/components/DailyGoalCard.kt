package com.focusguard.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors
import com.focusguard.app.R

/**
 * Carte affichant la progression vers l'objectif quotidien de blocages.
 */
@Composable
fun DailyGoalCard(todayBlocks: Int, dailyGoal: Int) {
    val progress = if (dailyGoal > 0) (todayBlocks / dailyGoal.toFloat()).coerceIn(0f, 1f) else 0f
    val goalReached = todayBlocks >= dailyGoal

    val accentColor = when {
        goalReached -> AppColors.Success
        progress >= 0.5f -> AppColors.Warning
        else -> AppColors.Error
    }

    GlassCard(accentColor = accentColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.daily_goal_card_title),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.OnSurface
            )
            Text(
                text = if (goalReached) {
                    stringResource(R.string.daily_goal_reached)
                } else {
                    stringResource(R.string.daily_goal_progress, todayBlocks, dailyGoal)
                },
                fontSize = 13.sp,
                color = accentColor,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassProgressBar(
            progress = progress,
            accentColor = accentColor,
            height = 10.dp
        )
    }
}
