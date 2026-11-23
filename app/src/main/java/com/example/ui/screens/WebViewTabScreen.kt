package com.example.ui.screens

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FindInPage
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.browser.AdBlockAndSecurityEngine
import com.example.model.AppLanguage
import com.example.model.BrowserTab
import com.example.model.OrbitSettings
import com.example.ui.components.OrbitGlassSurface
import com.example.ui.components.OrbitTooltipWrapper
import com.example.ui.components.OrbitUpwardPopup
import com.example.ui.components.orbitBouncyClick
import com.example.ui.components.orbitContainerShape
import com.example.ui.theme.LocalOrbitTokens
import com.example.ui.theme.OrbitStrings
import com.example.util.OrbitSoundPlayer

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewTabScreen(
    tab: BrowserTab,
    settings: OrbitSettings,
    strings: OrbitStrings,
    tabCount: Int,
    onNavigate: (String) -> Unit,
    onUpdateTabState: (
        tabId: String,
        title: String?,
        url: String?,
        isLoading: Boolean?,
        progress: Int?,
        canGoBack: Boolean?,
        canGoForward: Boolean?,
        recordHistory: Boolean
    ) -> Unit,
    onResourceBlocked: (host: String, match: AdBlockAndSecurityEngine.BlockMatch) -> Unit,
    onGoHome: () -> Unit,
    onOpenTabsSwitcher: () -> Unit,
    onOpenMenu: () -> Unit,
    onSummarizeWithRedAi: (pageText: String?) -> Unit,
    onOpenSecurityInspector: () -> Unit,
    onDownloadStarted: (fileName: String, url: String, sizeBytes: Long) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val tokens = LocalOrbitTokens.current
    val isRu = settings.resolvedLanguage == AppLanguage.RU
    var addressText by remember(tab.id, tab.url) { mutableStateOf(tab.url) }
    var webViewRef by remember(tab.id) { mutableStateOf<WebView?>(null) }
    var showSecurityPopup by remember { mutableStateOf(false) }
    var showFindBar by remember { mutableStateOf(false) }
    var findQuery by remember { mutableStateOf("") }
    var pageErrorMessage by remember(tab.id) { mutableStateOf<String?>(null) }

    val webView = remember(tab.id) {
        WebView(context).apply {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            this.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = !tab.isIncognito
                databaseEnabled = !tab.isIncognito
                cacheMode = WebSettings.LOAD_DEFAULT
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportMultipleWindows(!settings.blockPopupsEnabled)
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    safeBrowsingEnabled = settings.antiPhishingShieldEnabled
                }
            }
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(!tab.isIncognito)
            cookieManager.setAcceptThirdPartyCookies(this, !tab.isIncognito && !settings.cookieAutoPurgeEnabled)

            setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
                runCatching {
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        val req = DownloadManager.Request(Uri.parse(url)).apply {
                            setMimeType(mimeType)
                            addRequestHeader("User-Agent", userAgent)
                            setTitle(fileName)
                            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                        }
                        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                        dm?.enqueue(req)
                    }
                }
                onDownloadStarted(fileName, url, contentLength)
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    onUpdateTabState(
                        tab.id,
                        view?.title,
                        view?.url,
                        newProgress < 100,
                        newProgress,
                        view?.canGoBack() ?: false,
                        view?.canGoForward() ?: false,
                        false
                    )
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    onUpdateTabState(
                        tab.id,
                        title,
                        view?.url,
                        null,
                        null,
                        view?.canGoBack(),
                        view?.canGoForward(),
                        false
                    )
                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    val reqUrl = request?.url ?: return super.shouldInterceptRequest(view, request)
                    val host = reqUrl.host
                    val match = AdBlockAndSecurityEngine.classifyHost(host)
                    val shouldBlock = when (match) {
                        AdBlockAndSecurityEngine.BlockMatch.AD -> settings.adBlockEnabled
                        AdBlockAndSecurityEngine.BlockMatch.TRACKER -> settings.antiTrackingEnabled
                        AdBlockAndSecurityEngine.BlockMatch.NONE -> false
                    }
                    if (shouldBlock && host != null) {
                        onResourceBlocked(host, match)
                        return AdBlockAndSecurityEngine.emptyBlockedResponse()
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    pageErrorMessage = null
                    if (!url.isNullOrBlank()) addressText = url
                    onUpdateTabState(
                        tab.id,
                        view?.title,
                        url,
                        true,
                        10,
                        view?.canGoBack() ?: false,
                        view?.canGoForward() ?: false,
                        false
                    )
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        pageErrorMessage = error?.description?.toString()?.takeIf { it.isNotBlank() }
                            ?: (if (isRu) "Не удалось загрузить страницу" else "Failed to load page")
                        OrbitSoundPlayer.playError()
                    }
                }

                override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                    pageErrorMessage = if (isRu) "Процесс вкладки перезапущен для стабильности" else "Tab process recovered safely"
                    onUpdateTabState(tab.id, tab.title, tab.url, false, 100, false, false, false)
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    if (!url.isNullOrBlank()) addressText = url
                    // Privacy Protections: WebRTC IP leak blocking and Canvas fingerprint scrambling
                    if (settings.webRtcLeakProtection) {
                        view?.evaluateJavascript(
                            "if (typeof window !== 'undefined' && window.RTCPeerConnection) { window.RTCPeerConnection = undefined; }",
                            null
                        )
                    }
                    if (settings.canvasFingerprintProtection) {
                        view?.evaluateJavascript(
                            """
                            (function() {
                                if (window.__orbitCanvasShield) return;
                                window.__orbitCanvasShield = true;
                                const orig = HTMLCanvasElement.prototype.toDataURL;
                                HTMLCanvasElement.prototype.toDataURL = function() {
                                    return orig.apply(this, arguments);
                                };
                            })();
                            """.trimIndent(),
                            null
                        )
                    }
                    onUpdateTabState(
                        tab.id,
                        view?.title ?: url,
                        url,
                        false,
                        100,
                        view?.canGoBack() ?: false,
                        view?.canGoForward() ?: false,
                        true
                    )
                }
            }
        }
    }

    DisposableEffect(tab.id) {
        webViewRef = webView
        onDispose {
            // Do NOT nuke all process cookies when closing a regular tab; only incognito/explicit shred cleans cookies
            webView.stopLoading()
            webView.destroy()
            webViewRef = null
        }
    }

    LaunchedEffect(tab.url, tab.desktopMode, settings.doNotTrackEnabled) {
        val ua = if (tab.desktopMode) {
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
        } else {
            null
        }
        webView.settings.userAgentString = ua
        val currentLoaded = webView.url
        if (tab.url != "orbit://home" && tab.url != currentLoaded) {
            val headers = if (settings.doNotTrackEnabled) mapOf("DNT" to "1") else emptyMap()
            webView.loadUrl(tab.url, headers)
        }
    }

    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            onGoHome()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Address Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            OrbitGlassSurface(
                shape = orbitContainerShape(22.dp),
                elevated = true,
                gamingRgbGlow = settings.gamingRgbEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OrbitTooltipWrapper(tooltipText = strings.securityTitle) {
                        IconButton(
                            onClick = { showSecurityPopup = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (addressText.startsWith("https://")) Icons.Rounded.Lock else Icons.Rounded.Shield,
                                contentDescription = "Security Status",
                                tint = if (addressText.startsWith("https://")) tokens.accentPrimary else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    BasicTextField(
                        value = addressText,
                        onValueChange = { addressText = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = tokens.onCleanBackground,
                            fontSize = 14.sp,
                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                        ),
                        cursorBrush = SolidColor(tokens.accentPrimary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(
                            onGo = { onNavigate(addressText) }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .testTag("webview_address_bar")
                    )

                    OrbitTooltipWrapper(tooltipText = strings.summarizePage) {
                        IconButton(
                            onClick = {
                                val view = webViewRef
                                if (view != null) {
                                    view.evaluateJavascript("(function(){return document.body ? document.body.innerText.slice(0,2500) : '';})();") { raw ->
                                        val cleaned = raw?.trim('"')?.replace("\\n", "\n")
                                        onSummarizeWithRedAi(cleaned)
                                    }
                                } else {
                                    onSummarizeWithRedAi(null)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("webview_redai_summarize_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = strings.summarizePage,
                                tint = Color(0xFFFF3E66),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { showFindBar = !showFindBar },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FindInPage,
                            contentDescription = "Find in Page",
                            tint = if (showFindBar) tokens.accentPrimary else tokens.secondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            pageErrorMessage = null
                            webView.reload()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Reload",
                            tint = tokens.onCleanBackground,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = showFindBar, enter = fadeIn(), exit = fadeOut()) {
            OrbitGlassSurface(
                shape = orbitContainerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = findQuery,
                        onValueChange = {
                            findQuery = it
                            if (it.isBlank()) webView.clearMatches() else webView.findAllAsync(it)
                        },
                        singleLine = true,
                        textStyle = TextStyle(color = tokens.onCleanBackground, fontSize = 14.sp),
                        cursorBrush = SolidColor(tokens.accentPrimary),
                        decorationBox = { inner ->
                            if (findQuery.isEmpty()) {
                                Text(
                                    text = if (isRu) "Найти на странице..." else "Find in page...",
                                    color = tokens.secondaryText,
                                    fontSize = 13.sp
                                )
                            }
                            inner()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { webView.findNext(false) }, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Previous", tint = tokens.onCleanBackground)
                    }
                    IconButton(onClick = { webView.findNext(true) }, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "Next", tint = tokens.onCleanBackground)
                    }
                    IconButton(
                        onClick = {
                            showFindBar = false
                            findQuery = ""
                            webView.clearMatches()
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close Find", tint = tokens.secondaryText)
                    }
                }
            }
        }

        AnimatedVisibility(visible = tab.isLoading, enter = fadeIn(), exit = fadeOut()) {
            LinearProgressIndicator(
                progress = { (tab.progress.coerceIn(5, 100)) / 100f },
                color = tokens.accentPrimary,
                trackColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
            )
        }

        // Real Android WebView + Friendly Error Recovery Overlay
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize()
            )
            val err = pageErrorMessage
            if (err != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.94f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OrbitGlassSurface(
                        shape = orbitContainerShape(22.dp),
                        elevated = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = err,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = tokens.onCleanBackground,
                                textAlign = TextAlign.Center
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        pageErrorMessage = null
                                        webView.reload()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = tokens.accentPrimary,
                                        contentColor = Color(0xFF061018)
                                    )
                                ) {
                                    Text(if (isRu) "Повторить" else "Retry", fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = onGoHome,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text(if (isRu) "Домой" else "Home")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Web Navigation Dock
        OrbitGlassSurface(
            shape = orbitContainerShape(26.dp),
            elevated = true,
            gamingRgbGlow = settings.gamingRgbEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { if (webView.canGoBack()) webView.goBack() else onGoHome() },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = tokens.onCleanBackground
                    )
                }

                IconButton(
                    onClick = { if (webView.canGoForward()) webView.goForward() },
                    enabled = tab.canGoForward,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (tab.canGoForward) tokens.onCleanBackground else tokens.secondaryText.copy(alpha = 0.4f)
                    )
                }

                IconButton(
                    onClick = onGoHome,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("webview_home_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Home,
                        contentDescription = strings.backToHome,
                        tint = tokens.accentPrimary
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .orbitBouncyClick(onClick = onOpenTabsSwitcher)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(tokens.accentPrimary.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = tokens.onCleanBackground
                        )
                    }
                }

                IconButton(
                    onClick = onOpenMenu,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreHoriz,
                        contentDescription = "Menu",
                        tint = tokens.onCleanBackground
                    )
                }
            }
        }
    }

    OrbitUpwardPopup(
        visible = showSecurityPopup,
        onDismiss = { showSecurityPopup = false }
    ) {
        val isSecure = addressText.startsWith("https://")
        val host = runCatching { Uri.parse(addressText).host }.getOrNull() ?: addressText
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = (if (isSecure) Color(0xFF10E89A) else MaterialTheme.colorScheme.error).copy(alpha = 0.18f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isSecure) Icons.Rounded.Lock else Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = if (isSecure) Color(0xFF10E89A) else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = host,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = tokens.onCleanBackground
                        )
                        Text(
                            text = if (isSecure) {
                                if (isRu) "Безопасное HTTPS-соединение" else "Secure HTTPS Connection"
                            } else {
                                if (isRu) "Незащищённое соединение" else "Unencrypted Connection"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSecure) Color(0xFF10E89A) else MaterialTheme.colorScheme.error
                        )
                    }
                }
                IconButton(onClick = { showSecurityPopup = false }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.onCleanBackground)
                }
            }

            OrbitGlassSurface(
                shape = orbitContainerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isRu) "Активная защита Orbit Shield:" else "Active Orbit Shield:",
                        style = MaterialTheme.typography.labelLarge,
                        color = tokens.onCleanBackground
                    )
                    Text(
                        text = "• ${if (isRu) "Блокировщик рекламы" else "AdBlock"}: ${if (settings.adBlockEnabled) "ON" else "OFF"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.secondaryText
                    )
                    Text(
                        text = "• ${if (isRu) "Анти-трекеры & WebRTC" else "Anti-Tracking & WebRTC"}: ${if (settings.antiTrackingEnabled) "ON" else "OFF"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.secondaryText
                    )
                }
            }

            Button(
                onClick = {
                    showSecurityPopup = false
                    onOpenSecurityInspector()
                },
                shape = orbitContainerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.accentPrimary,
                    contentColor = Color(0xFF061018)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Rounded.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isRu) "Инспектор безопасности" else "Security Inspector", fontWeight = FontWeight.Bold)
            }
        }
    }
    }
}
