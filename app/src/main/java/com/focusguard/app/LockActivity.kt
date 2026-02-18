package com.focusguard.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.focusguard.app.challenges.RedesignedLockScreen
import com.focusguard.app.components.GlassBackground
import com.focusguard.app.ui.theme.FocusGuardTheme
import com.focusguard.app.viewmodel.LockViewModel

class LockActivity : ComponentActivity() {

    private val viewModel: LockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val targetPkg = intent.getStringExtra("pkg") ?: ""
        val delaySeconds = AppPreferences.getWaitingDuration(this)
        val breathingDuration = AppPreferences.getBreathingDuration(this)
        val pushupCount = AppPreferences.getPushupCount(this)

        viewModel.recordBlock(targetPkg)

        setContent {
            BackHandler(enabled = true) { /* Bloquer retour */ }

            FocusGuardTheme {
                GlassBackground(modifier = Modifier.fillMaxSize()) {
                    RedesignedLockScreen(
                        appName = viewModel.getAppDisplayName(targetPkg),
                        delaySeconds = delaySeconds,
                        breathingDuration = breathingDuration,
                        pushupCount = pushupCount,
                        onValidate = { challengeType ->
                            val newLevel = viewModel.recordChallengeCompleted(challengeType, targetPkg)

                            if (newLevel != null) {
                                Toast.makeText(
                                    this@LockActivity,
                                    "NIVEAU $newLevel ! ${viewModel.getLevelTitle(newLevel)}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            viewModel.grantTemporaryAccess(targetPkg)
                            finish()
                        },
                        onCancel = {
                            goHome()
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun goHome() {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}
