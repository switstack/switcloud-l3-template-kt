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

#=============== Keep this rule, it's a good default =================
-keep class com.google.android.material.internal.CheckableImageButton { *; }

#========================= Androidx / Jetpack ========================
# This is a general rule that helps with many AndroidX libraries that
# might use reflection or have dynamic dependencies.
-keep class androidx.core.app.** { *; }
-keep class androidx.lifecycle.** { *; }

#========================= Jetpack Compose ===========================
# Compose uses reflection and code generation extensively. These rules are
# essential to prevent crashes in release builds.
-keep class androidx.compose.runtime.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <fields>;
}

#======================== Kotlin Coroutines & Reflection =============
# These rules ensure that Kotlin's coroutines and reflection mechanisms
# are not stripped out by ProGuard, which can cause subtle runtime crashes.
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory
-keepnames class kotlinx.coroutines.flow.**
-keep class kotlin.reflect.KClass
-keep class kotlin.reflect.KFunction
-keep class kotlin.reflect.KProperty*

# If you use kotlin.Result, it's good to keep its members
-keepclassmembers class kotlin.Result {
    *;
}

#======================= BER-TLV Library ===============================
# Keep classes from the ber-tlv library to prevent issues with reflection.
-keep class io.switstack.switcloud.switcloudl2.** { *; }
-keep class com.payneteasy.tlv.** { *; }
