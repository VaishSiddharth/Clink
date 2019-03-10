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
-keep class com.jpardogo.android.googleprogressbar.** { *; }
-keep class com.android.vending.billing.**
-ignorewarnings

-keep class * {
    public private *;
}

-keep class com.testlabic.datenearu.Models.** { *; }

-keep class com.testlabic.datenearu.viewHolders.** { *; }

-keep class com.testlabic.datenearu.Adapters.** { *; }


-keep class com.testlabic.datenearu.TransitionUtils.** { *; }

-keep class com.testlabic.datenearu.ArchitectureUtils.** { *; }

-keep class com.testlabic.datenearu.NewQuestionUtils.** { *; }
-keep class com.testlabic.datenearu.AttemptMatchUtils.** { *; }

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keep class com.startapp.** {
      *;
}

-keep class com.truenet.** {
      *;
}

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**

-dontwarn org.jetbrains.annotations.**