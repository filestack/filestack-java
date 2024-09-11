# filestack-java-specific rules
-keep public class org.filestack.internal.responses.** {
    private *;
    <init>(...);
}

-keep public class org.filestack.CloudResponse {
    private *;
    <init>(...);
}

-keep public class org.filestack.CloudItem {
    private *;
    <init>(...);
}

-keep public class org.filestack.AppInfo {
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