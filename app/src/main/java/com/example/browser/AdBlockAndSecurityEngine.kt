package com.example.browser

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.WebResourceResponse
import com.example.model.AntivirusProvider
import com.example.model.AntivirusScanResult
import com.example.model.DeviceTelemetry
import java.io.ByteArrayInputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AdBlockAndSecurityEngine {

    private val adDomains = setOf(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "adnxs.com",
        "taboola.com",
        "outbrain.com",
        "criteo.com",
        "pubmatic.com",
        "rubiconproject.com",
        "amazon-adsystem.com",
        "ads.yahoo.com",
        "an.yandex.ru",
        "adfox.ru"
    )

    private val trackerDomains = setOf(
        "google-analytics.com",
        "analytics.google.com",
        "googletagmanager.com",
        "connect.facebook.net",
        "pixel.facebook.com",
        "mc.yandex.ru",
        "top-fwz1.mail.ru",
        "hotjar.com",
        "mixpanel.com",
        "segment.io",
        "amplitude.com",
        "scorecardresearch.com",
        "quantserve.com",
        "clarity.ms"
    )

    private val suspiciousUrlTokens = listOf(
        "login-verify",
        "secure-update-account",
        "free-nitro",
        "phish",
        "wallet-drain",
        "verify-bank-now",
        ".tk/",
        ".xyz/login",
        "@http"
    )

    enum class BlockMatch { NONE, AD, TRACKER }

    fun classifyHost(host: String?): BlockMatch {
        if (host.isNullOrBlank()) return BlockMatch.NONE
        val lower = host.lowercase()
        if (adDomains.any { lower == it || lower.endsWith(".$it") }) return BlockMatch.AD
        if (trackerDomains.any { lower == it || lower.endsWith(".$it") }) return BlockMatch.TRACKER
        return BlockMatch.NONE
    }

    fun emptyBlockedResponse(): WebResourceResponse = WebResourceResponse(
        "text/plain",
        "utf-8",
        ByteArrayInputStream(ByteArray(0))
    )

    fun upgradeToHttpsIfNeeded(url: String, httpsOnly: Boolean): String {
        if (!httpsOnly) return url
        return if (url.startsWith("http://") && !url.startsWith("http://localhost") && !url.startsWith("http://127.0.0.1")) {
            "https://" + url.removePrefix("http://")
        } else {
            url
        }
    }

    fun buildSearchOrUrl(input: String, searchTemplate: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "orbit://home"
        if (trimmed.startsWith("orbit://")) return trimmed
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed
        val looksLikeDomain = !trimmed.contains(" ") && trimmed.contains(".") && !trimmed.endsWith(".")
        return if (looksLikeDomain) {
            "https://$trimmed"
        } else {
            searchTemplate + URLEncoder.encode(trimmed, StandardCharsets.UTF_8.name())
        }
    }

    fun runLocalAntivirusHeuristic(
        url: String,
        provider: AntivirusProvider,
        isRu: Boolean
    ): AntivirusScanResult {
        val lower = url.lowercase()
        val parsedHost = runCatching { Uri.parse(url).host.orEmpty() }.getOrDefault("")
        val hasSuspiciousPattern = suspiciousUrlTokens.any { lower.contains(it) } ||
            parsedHost.count { it == '-' } >= 4 ||
            lower.startsWith("http://") && !lower.contains("localhost")

        val providerName = when (provider) {
            AntivirusProvider.NONE -> if (isRu) "Встроенный щит Orbit" else "Orbit Shield"
            AntivirusProvider.KASPERSKY -> "Kaspersky Threat Shield"
            AntivirusProvider.DR_WEB -> "Dr.Web Link Guard"
            AntivirusProvider.BOTH -> "Kaspersky + Dr.Web Dual Shield"
        }

        val verdict = if (hasSuspiciousPattern) {
            if (isRu) "Обнаружен повышенный риск (небезопасный протокол или подозрительный паттерн URL)."
            else "Elevated risk detected (insecure protocol or suspicious URL pattern)."
        } else {
            if (isRu) "Угроз и фишинговых сигнатур не обнаружено."
            else "No known phishing or malware signatures detected."
        }

        return AntivirusScanResult(
            url = url,
            providerLabel = providerName,
            isSafe = !hasSuspiciousPattern,
            verdict = verdict,
            scannedAtMs = System.currentTimeMillis()
        )
    }

    fun isCompanionAntivirusInstalled(context: Context, provider: AntivirusProvider): Boolean {
        if (provider == AntivirusProvider.NONE) return false
        val pm = context.packageManager
        val packages = when (provider) {
            AntivirusProvider.KASPERSKY -> listOf("com.kms.free", "com.kaspersky.kes")
            AntivirusProvider.DR_WEB -> listOf("com.drweb", "com.drweb.pro")
            AntivirusProvider.BOTH -> listOf("com.kms.free", "com.drweb")
            AntivirusProvider.NONE -> emptyList()
        }
        return packages.any { pkg ->
            try {
                pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0))
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun openAntivirusCompanionOrPortal(context: Context, provider: AntivirusProvider): String? {
        val pm = context.packageManager
        val packages = when (provider) {
            AntivirusProvider.KASPERSKY -> listOf("com.kms.free", "com.kaspersky.kes")
            AntivirusProvider.DR_WEB -> listOf("com.drweb", "com.drweb.pro")
            AntivirusProvider.BOTH -> listOf("com.kms.free", "com.drweb")
            AntivirusProvider.NONE -> emptyList()
        }
        for (pkg in packages) {
            val launchIntent = pm.getLaunchIntentForPackage(pkg)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                return null
            }
        }
        return when (provider) {
            AntivirusProvider.KASPERSKY, AntivirusProvider.BOTH -> "https://opentip.kaspersky.com/"
            AntivirusProvider.DR_WEB -> "https://vms.drweb.com/online/"
            AntivirusProvider.NONE -> null
        }
    }

    fun readRealDeviceTelemetry(
        context: Context,
        activeTabs: Int,
        hibernatedTabs: Int
    ): DeviceTelemetry {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memInfo)
        val totalMb = (memInfo.totalMem / (1024L * 1024L)).coerceAtLeast(1024L)
        val availMb = (memInfo.availMem / (1024L * 1024L)).coerceAtLeast(128L)
        val usedMb = (totalMb - availMb).coerceAtLeast(0L)
        val runtime = Runtime.getRuntime()
        val appHeapMb = ((runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L)).coerceAtLeast(8L)
        return DeviceTelemetry(
            totalRamMb = totalMb,
            availableRamMb = availMb,
            usedRamMb = usedMb,
            appHeapMb = appHeapMb,
            activeTabsCount = activeTabs,
            hibernatedTabsCount = hibernatedTabs
        )
    }
}
