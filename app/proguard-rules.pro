# Add project specific ProGuard rules here.
# By default, the flag in this file are appended to flag specified
# in /usr/local/Cellar/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **_ViewBinding { *; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }
-keepclasseswithmembernames class * { @butterknife.* <methods>; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Firebase & GMS
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# OneSignal
-keep class com.onesignal.** { *; }
-dontwarn com.onesignal.**

# Volley
-keep class com.android.volley.** { *; }

# Keep model classes (to prevent issues with reflection/JSON serialization)
-keep class com.skysoftlk.vpnapp.model.** { *; }
-keep class com.skysoftlk.vpnapp.model.Countries { *; }

# OpenVPN / OneConnect
-keep class top.oneconnectapi.app.** { *; }
-dontwarn top.oneconnectapi.app.**

# Lottie
-keep class com.airbnb.lottie.** { *; }

# Kotlin
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.Metadata { *; }

# Support for deobfuscation in Play Console
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Fix R8 Missing Classes
-dontwarn com.facebook.infer.annotation.Nullsafe$Mode
-dontwarn com.facebook.infer.annotation.Nullsafe
-dontwarn com.google.android.gms.ads.formats.UnifiedNativeAdView
-dontwarn com.google.firebase.ktx.Firebase
-dontwarn com.google.firebase.ktx.FirebaseKt

