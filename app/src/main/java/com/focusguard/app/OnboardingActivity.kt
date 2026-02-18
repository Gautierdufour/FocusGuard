package com.focusguard.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Filled.Lock,
            title = "Bienvenue sur FocusGuard",
            description = "Reprenez le contrôle de votre temps d'écran. FocusGuard vous aide à limiter l'usage des apps addictives grâce à des défis stimulants.",
            color = AppColors.Primary
        ),
        OnboardingPage(
            icon = Icons.Filled.Star,
            title = "Des défis pour se libérer",
            description = "Quand vous ouvrez une app bloquée, complétez un défi : pompes, respiration, attente, quiz... Chaque défi renforce votre discipline.",
            color = AppColors.Success
        ),
        OnboardingPage(
            icon = Icons.Filled.Favorite,
            title = "Gamification motivante",
            description = "Gagnez de l'XP, montez en niveau, débloquez des badges et maintenez votre série quotidienne. Transformez la discipline en jeu !",
            color = AppColors.Warning
        ),
        OnboardingPage(
            icon = Icons.Filled.Settings,
            title = "Permissions requises",
            description = "FocusGuard a besoin de l'accès aux statistiques d'utilisation pour détecter quand vous ouvrez une app bloquée. Cette permission est essentielle au fonctionnement.",
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
                    Text("Autoriser l'accès aux stats", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                GlassButton(
                    onClick = onComplete,
                    accentColor = AppColors.Primary
                ) {
                    Text("Commencer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                        Text("Passer", color = AppColors.OnSurfaceVariant)
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
                        Text("Suivant", fontWeight = FontWeight.Bold)
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
