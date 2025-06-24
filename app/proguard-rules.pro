# TaleTail ProGuard Rules for Release

# Keep Compose specific classes
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep data classes for JSON parsing
-keep class com.timor.kidsstory.domain.model.** { *; }
-keep class com.timor.kidsstory.presentation.**.model.** { *; }

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep serialization classes
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Navigation destinations
-keep class com.timor.kidsstory.presentation.navigation.** { *; }

# Hilt specific rules
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }

# Ktor specific rules
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Gson specific rules (if using Gson)
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# Room specific rules
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# OkHttp and Retrofit (if using)
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# Keep TaleTail application class
-keep class com.timor.kidsstory.TaleTailApplication { *; }

# Generative AI (Gemini)
-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
