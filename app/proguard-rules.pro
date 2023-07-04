# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#
#-dontwarn com.google.firebase.appcheck.interop.InternalAppCheckTokenProvider
#
## General rules for Firebase
#-keep class com.google.firebase.** { *; }
#-dontwarn com.google.firebase.**
#
## Rules for Firebase Auth
#-keep class com.google.firebase.auth.** { *; }
#-dontwarn com.google.firebase.auth.**
#
## Rules for Firebase Crashlytics
#-keep class com.google.firebase.crashlytics.** { *; }
#-dontwarn com.google.firebase.crashlytics.**
#
## Rules for Firebase Analytics
#-keep class com.google.firebase.analytics.** { *; }
#-dontwarn com.google.firebase.analytics.**
#
## Rules for Firebase Firestore
#-keep class com.google.firebase.firestore.** { *; }
#-dontwarn com.google.firebase.firestore.**
#
## Rules for Firebase Storage
#-keep class com.google.firebase.storage.** { *; }
#-dontwarn com.google.firebase.storage.**

#-keep class com.google.android.gms.** { *; }
#-dontwarn com.google.android.gms.**

-keep class com.google.android.gms.internal.** {*;}

-keep class com.arjun.headout.data.model.** { *; }
