# filestack-java-specific rules
-keep public class com.filestack.internal.responses.** {
    private *;
    <init>(...);
}

-keep public class com.filestack.CloudResponse {
    private *;
    <init>(...);
}

-keep public class com.filestack.CloudItem {
    private *;
    <init>(...);
}

-keep public class com.filestack.AppInfo {
    private *;
    <init>(...);
}

# OkHttp-specific rules
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# Okio-specific rules
-dontwarn okio.**