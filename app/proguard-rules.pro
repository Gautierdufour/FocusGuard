# Add project specific ProGuard rules here.

# Keep line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Supprimer tous les logs debug/verbose en release (pas d'impact runtime)
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static boolean isLoggable(java.lang.String, int);
}

# Keep the service and receiver classes (referenced in AndroidManifest.xml)
-keep class com.focusguard.app.MonitorService { *; }
-keep class com.focusguard.app.BootReceiver { *; }
-keep class com.focusguard.app.ServiceWatchdogWorker { *; }

# Keep data classes used with JSON serialization
-keep class com.focusguard.app.TimeSchedule { *; }
-keep class com.focusguard.app.LocationZone { *; }
-keep class com.focusguard.app.FocusSession { *; }
-keep class com.focusguard.app.InstalledApp { *; }

# Keep Compose-related classes
-keep class androidx.compose.** { *; }

# Keep WorkManager Worker classes
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }
