package com.upsewa.hub.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.upsewa.hub.ui.theme.UPSewaTheme

/**
 * Single activity that opens any govt service URL **inside the app**.
 * - Native Material 3 toolbar (back / title / progress / overflow)
 * - WebView with JS enabled + per-domain CSS injection (see [SiteInjections])
 * - Hardware back navigates the WebView's history before exiting the activity
 * - Toggle "Clean UI" off to see the original govt site (useful for debugging)
 */
class ServiceWebViewActivity : ComponentActivity() {

    companion object {
        const val EXTRA_URL = "extra_url"
        const val EXTRA_TITLE = "extra_title"
    }

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val url = intent.getStringExtra(EXTRA_URL) ?: "https://www.google.com"
        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""

        setContent {
            UPSewaTheme {
                WebViewScreen(
                    initialUrl = url,
                    titleText = title,
                    onWebViewCreated = { webView = it },
                    onBack = { onBackPressedDispatcher.onBackPressed() }
                )
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (webView?.canGoBack() == true) webView?.goBack() else finish()
        }
    }

    override fun onDestroy() {
        webView?.apply {
            (parent as? ViewGroup)?.removeView(this)
            stopLoading(); destroy()
        }
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewScreen(
    initialUrl: String,
    titleText: String,
    onWebViewCreated: (WebView) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var loadingProgress by remember { mutableStateOf(0) }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var cleanUi by rememberSaveable { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White,
                    ),
                    title = {
                        Column {
                            Text(titleText, maxLines = 1, style = MaterialTheme.typography.titleMedium)
                            Text(
                                Uri.parse(currentUrl).host ?: "",
                                maxLines = 1,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            cleanUi = !cleanUi
                            webViewRef?.let { wv ->
                                if (cleanUi) wv.evaluateJavascript(SiteInjections.buildInjection(currentUrl), null)
                                else wv.evaluateJavascript(
                                    "var s=document.getElementById('upsh-injected-style');if(s)s.remove();", null
                                )
                            }
                        }) {
                            Icon(
                                Icons.Default.AutoFixHigh,
                                contentDescription = "Clean UI",
                                tint = if (cleanUi) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                        }
                        IconButton(onClick = { webViewRef?.reload() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Share link") },
                                    leadingIcon = { Icon(Icons.Default.Share, null) },
                                    onClick = {
                                        showMenu = false
                                        val s = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, currentUrl)
                                        }
                                        context.startActivity(Intent.createChooser(s, "Share"))
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Open in browser") },
                                    leadingIcon = { Icon(Icons.Default.OpenInBrowser, null) },
                                    onClick = {
                                        showMenu = false
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                                        )
                                    }
                                )
                            }
                        }
                    }
                )
                if (loadingProgress in 1..99) {
                    LinearProgressIndicator(
                        progress = { loadingProgress / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFC79A3A),
                        trackColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.apply {
                            javaScriptEnabled = true                     // many govt sites require JS
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            userAgentString = userAgentString + " UPSewaHub/1.0"
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                loadingProgress = newProgress
                            }
                        }
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView, request: WebResourceRequest
                            ): Boolean {
                                val u = request.url.toString()
                                // Stay inside the WebView for http/https; bounce others (tel:, mailto:) to system
                                return if (u.startsWith("http")) {
                                    false
                                } else {
                                    runCatching {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, request.url))
                                    }
                                    true
                                }
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                url?.let { currentUrl = it }
                                // Inject early so it covers initial paint
                                if (cleanUi) view?.evaluateJavascript(SiteInjections.buildInjection(url ?: ""), null)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                url?.let { currentUrl = it }
                                if (cleanUi) view?.evaluateJavascript(SiteInjections.buildInjection(url ?: ""), null)
                            }
                        }
                        loadUrl(initialUrl)
                        webViewRef = this
                        onWebViewCreated(this)
                    }
                }
            )
        }
    }
}
