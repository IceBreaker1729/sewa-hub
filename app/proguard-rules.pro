# Keep JS interface bridges intact (used by WebView extractor)
-keepclassmembers class com.upsewa.hub.webview.ExtractorBridge {
    public *;
}
-keepattributes JavascriptInterface
