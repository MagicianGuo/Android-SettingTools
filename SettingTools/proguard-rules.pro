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
-keep class com.magicianguo.settingtoolsaidl.* { *; }
-keep class android.** { *; }
-keep class com.android.** { *; }
-dontwarn dev.rikka.tools.refine.RefineProcessor$Descriptor
-dontwarn refine.android.app.ActivityManager$RunningAppProcessInfo
-dontwarn refine.android.app.ActivityManager
-dontwarn refine.android.app.AppOpsManager$AttributedHistoricalOps
-dontwarn refine.android.app.AppOpsManager$AttributedOpEntry
-dontwarn refine.android.app.AppOpsManager$HistoricalOp
-dontwarn refine.android.app.AppOpsManager$HistoricalOps
-dontwarn refine.android.app.AppOpsManager$HistoricalOpsVisitor
-dontwarn refine.android.app.AppOpsManager$HistoricalPackageOps
-dontwarn refine.android.app.AppOpsManager$HistoricalUidOps
-dontwarn refine.android.app.AppOpsManager$NoteOpEvent
-dontwarn refine.android.app.AppOpsManager$OpEntry
-dontwarn refine.android.app.AppOpsManager$OpEventProxyInfo
-dontwarn refine.android.app.AppOpsManager$PackageOps
-dontwarn refine.android.app.AppOpsManager$RestrictionBypass
-dontwarn refine.android.app.AppOpsManager
-dontwarn refine.android.app.IActivityManager$ContentProviderHolder
-dontwarn refine.android.app.IActivityManager
-dontwarn refine.android.content.Context
-dontwarn refine.android.content.pm.IShortcutService$Stub
-dontwarn refine.android.content.pm.IShortcutService
-dontwarn refine.android.content.pm.PackageInfo
-dontwarn refine.android.content.pm.PackageInstaller$Session
-dontwarn refine.android.content.pm.PackageInstaller$SessionParams
-dontwarn refine.android.content.pm.PackageInstaller
-dontwarn refine.android.content.pm.PackageManager
-dontwarn refine.android.hardware.display.DisplayManager
-dontwarn refine.android.os.UserHandle
-dontwarn refine.android.view.WindowManager$LayoutParams
-dontwarn refine.android.view.WindowManager