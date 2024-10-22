-flattenpackagehierarchy
-allowaccessmodification
-optimizationpasses 5
-shrinkunusedprotofields
-overloadaggressively
-renamesourcefileattribute SourceFile
-keeppackagenames doNotKeepAThing

-keepclassmembers class com.flyfish233.lain.model.Llama$Companion$IntVar {
    void inc();
    int getValue();
}

-dontwarn okio.AsyncTimeout
-dontwarn okio.Buffer
-dontwarn okio.BufferedSink
-dontwarn okio.BufferedSource
-dontwarn okio.ByteString$Companion
-dontwarn okio.ByteString
-dontwarn okio.FileMetadata
-dontwarn okio.FileSystem
-dontwarn okio.ForwardingFileSystem
-dontwarn okio.ForwardingSink
-dontwarn okio.ForwardingSource
-dontwarn okio.ForwardingTimeout
-dontwarn okio.GzipSource
-dontwarn okio.Okio
-dontwarn okio.Options$Companion
-dontwarn okio.Options
-dontwarn okio.Path$Companion
-dontwarn okio.Path
-dontwarn okio.Sink
-dontwarn okio.Source
-dontwarn okio.Timeout
-dontwarn okio.Utf8