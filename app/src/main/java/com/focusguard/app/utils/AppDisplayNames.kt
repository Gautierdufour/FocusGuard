package com.focusguard.app.utils

import android.content.Context

/**
 * Centralized utility for mapping package names to display names.
 * Falls back to PackageManager for unknown packages.
 */
object AppDisplayNames {

    private val KNOWN_APPS = mapOf(
        "com.instagram.android" to "Instagram",
        "com.google.android.youtube" to "YouTube",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.facebook.katana" to "Facebook",
        "com.snapchat.android" to "Snapchat",
        "com.twitter.android" to "Twitter",
        "com.spotify.music" to "Spotify",
        "com.netflix.mediaclient" to "Netflix",
        "com.reddit.frontpage" to "Reddit",
        "com.pinterest" to "Pinterest",
        "com.whatsapp" to "WhatsApp",
        "com.facebook.orca" to "Messenger",
        "org.telegram.messenger" to "Telegram",
        "com.discord" to "Discord",
        "com.linkedin.android" to "LinkedIn",
        "com.amazon.avod.thirdpartyclient" to "Prime Video",
        "tv.twitch.android.app" to "Twitch",
        "com.google.android.gm" to "Gmail",
        "com.android.chrome" to "Chrome"
    )

    private val APP_KEYS = mapOf(
        "com.instagram.android" to "instagram",
        "com.google.android.youtube" to "youtube",
        "com.zhiliaoapp.musically" to "tiktok",
        "com.facebook.katana" to "facebook",
        "com.snapchat.android" to "snapchat",
        "com.twitter.android" to "twitter",
        "com.spotify.music" to "spotify",
        "com.netflix.mediaclient" to "netflix",
        "com.reddit.frontpage" to "reddit",
        "com.pinterest" to "pinterest"
    )

    fun getDisplayName(context: Context, packageName: String): String {
        // Check known apps first
        KNOWN_APPS[packageName]?.let { return it }

        // Fallback to PackageManager
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }

    fun getAppKey(packageName: String): String {
        return APP_KEYS[packageName] ?: packageName.replace(".", "_")
    }
}
