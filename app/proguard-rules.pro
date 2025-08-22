# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep AdMob classes
-keep class com.google.android.gms.ads.** { *; }

# Keep data model classes
-keep class com.pisti.game.data.models.** { *; }