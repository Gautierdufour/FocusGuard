package com.focusguard.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.focusguard.app.R
import com.focusguard.app.components.GlassBackground
import com.focusguard.app.components.GlassButton
import com.focusguard.app.components.GlassIconBadge
import com.focusguard.app.ui.theme.FocusGuardTheme
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("onboarding", Context.MODE_PRIVATE)
        if (prefs.getBoolean("completed", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            FocusGuardTheme {
                GlassBackground {
                    OnboardingScreen(
                        onComplete = {
                            prefs.edit().putBoolean("completed", true).apply()
                            startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
                            finish()
                        },
                        onRequestUsageAccess = {
                            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                        }
                    )
                }
            }
        }
    }
}

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreen(
    onComplete: () -> Unit,
    onRequestUsageAccess: () -> Unit
) {
    var showCelebration by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = showCelebration,
        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
        label = "onboarding_transition"
    ) { celebrating ->
        if (celebrating) {
            OnboardingCelebrationScreen(onAnimationEnd = onComplete)
        } else {
            OnboardingPagerScreen(
                onComplete = { showCelebration = true },
                onRequestUsageAccess = onRequestUsageAccess
            )
        }
    }
}

@Composable
private fun OnboardingCelebrationScreen(onAnimationEnd: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "celebration_scale"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1600)
        onAnimationEnd()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "🎉",
                fontSize = 72.sp,
                modifier = Modifier.scale(scale)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.onboarding_ready_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.onboarding_ready_subtitle),
                fontSize = 16.sp,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingPagerScreen(
    onComplete: () -> Unit,
    onRequestUsageAccess: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Filled.Lock,
            title = stringResource(R.string.onboarding_welcome),
            description = stringResource(R.string.onboarding_desc1),
            color = AppColors.Primary
        ),
        OnboardingPage(
            icon = Icons.Filled.Star,
            title = stringResource(R.string.onboarding_challenges),
            description = stringResource(R.string.onboarding_desc2),
            color = AppColors.Success
        ),
        OnboardingPage(
            icon = Icons.Filled.Favorite,
            title = stringResource(R.string.onboarding_gamification),
            description = stringResource(R.string.onboarding_desc3),
            color = AppColors.Warning
        ),
        OnboardingPage(
            icon = Icons.Filled.Settings,
            title = stringResource(R.string.onboarding_permissions),
            description = stringResource(R.string.onboarding_desc4),
            color = AppColors.Info
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnboardingPageContent(pages[page])
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                pages.indices.forEach { index ->
                    val isActive = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isActive) 32.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) pages[index].color
                                else AppColors.GlassBgElevated
                            )
                            .animateContentSize()
                    )
                }
            }

            if (isLastPage) {
                GlassButton(
                    onClick = onRequestUsageAccess,
                    accentColor = AppColors.Info
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.allow_stats_access), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                GlassButton(
                    onClick = onComplete,
                    accentColor = AppColors.Primary
                ) {
                    Text(stringResource(R.string.get_started), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pages.size - 1)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.skip), color = AppColors.OnSurfaceVariant)
                    }

                    GlassButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier.width(140.dp),
                        accentColor = pages[pagerState.currentPage].color
                    ) {
                        Text(stringResource(R.string.next), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlassIconBadge(
                icon = page.icon,
                accentColor = page.color,
                size = 110.dp,
                iconSize = 52.dp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = page.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = page.color,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = page.description,
                fontSize = 16.sp,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}
