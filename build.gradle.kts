// Fichier à la RACINE du projet (pas dans app/)
plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.google.firebase.crashlytics") version "3.0.1" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}