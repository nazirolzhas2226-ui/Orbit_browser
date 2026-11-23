package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.browser.AdBlockAndSecurityEngine
import com.example.model.AntivirusProvider
import com.example.model.AntivirusScanResult
import com.example.model.AppLanguage
import com.example.model.BlockedEvent
import com.example.model.BrowserStyleMode
import com.example.model.DeviceTelemetry
import com.example.model.OrbitSettings
import com.example.model.VpnRegion
import com.example.ui.components.JupiterGxLogoEmblem
import com.example.ui.components.OrbitGlassSurface
import com.example.ui.components.orbitContainerShape
import com.example.ui.theme.LocalOrbitTokens
import kotlin.math.roundToInt

@Composable
fun SecurityPrivacyScreen(
    settings: OrbitSettings,
    blockedEvents: List<BlockedEvent>,
    latestScan: AntivirusScanResult?,
    currentTabUrl: String,
    isAiBusy: Boolean,
    onBack: () -> Unit,
    onUpdateSetting: (String, Any) -> Unit,
    onToggleVpn: (Boolean, VpnRegion) -> Unit,
    onRunAiUrlScan: (String) -> Unit,
    onOpenUrlInTab: (String) -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val tokens = LocalOrbitTokens.current
    val isRu = settings.resolvedLanguage == AppLanguage.RU
    var urlToScan by remember(currentTabUrl) {
        mutableStateOf(if (currentTabUrl.startsWith("orbit://")) "https://www.google.com" else currentTabUrl)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .widthIn(max = 680.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = tokens.onCleanBackground)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isRu) "Безопасность, VPN и Антивирус" else "Security, VPN & Antivirus",
                style = MaterialTheme.typography.titleLarge,
                color = tokens.onCleanBackground
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // AdBlock & Tracking Protection Summary
            item {
                OrbitGlassSurface(
                    shape = orbitContainerShape(22.dp),
                    elevated = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Shield, contentDescription = null, tint = tokens.accentPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRu) "Щит Orbit" else "Orbit Shield",
                                style = MaterialTheme.typography.titleMedium,
                                color = tokens.onCleanBackground
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricBox(
                                label = if (isRu) "Реклама" else "Ads Blocked",
                                value = settings.adsBlockedTotal.toString(),
                                color = tokens.accentPrimary
                            )
                            MetricBox(
                                label = if (isRu) "Трекеры" else "Trackers",
                                value = settings.trackersBlockedTotal.toString(),
                                color = tokens.accentSecondary
                            )
                        }
                        ToggleRow(
                            title = if (isRu) "Блокировщик рекламы (AdBlock)" else "AdBlock Engine",
                            checked = settings.adBlockEnabled,
                            onCheckedChange = { onUpdateSetting("adBlockEnabled", it) }
                        )
                        ToggleRow(
                            title = if (isRu) "Защита от отслеживания" else "Anti-Tracking Shield",
                            checked = settings.antiTrackingEnabled,
                            onCheckedChange = { onUpdateSetting("antiTrackingEnabled", it) }
                        )
                        ToggleRow(
                            title = if (isRu) "Только HTTPS" else "HTTPS-Only Mode",
                            checked = settings.httpsOnlyEnabled,
                            onCheckedChange = { onUpdateSetting("httpsOnlyEnabled", it) }
                        )
                        ToggleRow(
                            title = if (isRu) "Защита от утечек WebRTC" else "WebRTC Leak Protection",
                            checked = settings.webRtcLeakProtection,
                            onCheckedChange = { onUpdateSetting("webRtcLeakProtection", it) }
                        )
                        ToggleRow(
                            title = if (isRu) "Скремблер отпечатков Canvas" else "Canvas Fingerprint Scrambler",
                            checked = settings.canvasFingerprintProtection,
                            onCheckedChange = { onUpdateSetting("canvasFingerprintProtection", it) }
                        )
                        ToggleRow(
                            title = if (isRu) "Заголовок 'Не отслеживать' (DNT: 1)" else "Do Not Track Header (DNT)",
                            checked = settings.doNotTrackEnabled,
                            onCheckedChange = { onUpdateSetting("doNotTrackEnabled", it) }
                        )
                        ToggleRow(
                            title = if (isRu) "Автоочистка сторонних куки" else "Purge 3rd-Party Cookies",
                            checked = settings.cookieAutoPurgeEnabled,
                            onCheckedChange = { onUpdateSetting("cookieAutoPurgeEnabled", it) }
                        )
                    }
                }
            }

            // Live Site Shield Inspector Card
            item {
                OrbitGlassSurface(
                    shape = orbitContainerShape(22.dp),
                    elevated = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Security, contentDescription = null, tint = tokens.accentPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRu) "Живой инспектор защиты сайта" else "Live Site Shield Inspector",
                                style = MaterialTheme.typography.titleMedium,
                                color = tokens.onCleanBackground
                            )
                        }
                        Text(
                            text = if (currentTabUrl.startsWith("orbit://")) "orbit://home (Внутренняя среда браузера)" else currentTabUrl,
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.secondaryText,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricBox(
                                label = if (isRu) "Шифрование" else "SSL/TLS",
                                value = if (currentTabUrl.startsWith("https://")) "TLS 1.3" else "Orbit Sec",
                                color = Color(0xFF10E89A)
                            )
                            MetricBox(
                                label = "WebRTC",
                                value = if (settings.webRtcLeakProtection) "IP Masked" else "Standard",
                                color = tokens.accentPrimary
                            )
                            MetricBox(
                                label = "Canvas",
                                value = if (settings.canvasFingerprintProtection) "Noised" else "Raw",
                                color = tokens.accentSecondary
                            )
                        }
                    }
                }
            }

            // Built-in VPN
            item {
                OrbitGlassSurface(
                    shape = orbitContainerShape(22.dp),
                    elevated = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.VpnKey, contentDescription = null, tint = tokens.accentPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isRu) "Встроенный Orbit VPN" else "Built-in Orbit VPN",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = if (settings.vpnEnabled) {
                                            "${if (isRu) settings.resolvedVpnRegion.labelRu else settings.resolvedVpnRegion.labelEn} · ${settings.resolvedVpnRegion.latencyMs} ms"
                                        } else {
                                            if (isRu) "Выключен" else "Disconnected"
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (settings.vpnEnabled) Color(0xFF10E89A) else tokens.secondaryText
                                    )
                                }
                            }
                            Switch(
                                checked = settings.vpnEnabled,
                                onCheckedChange = { onToggleVpn(it, settings.resolvedVpnRegion) },
                                modifier = Modifier.testTag("vpn_switch")
                            )
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(VpnRegion.entries) { region ->
                                FilterChip(
                                    selected = settings.resolvedVpnRegion == region,
                                    onClick = { onToggleVpn(true, region) },
                                    label = { Text("${region.countryCode} · ${region.latencyMs}ms") }
                                )
                            }
                        }
                    }
                }
            }

            // Kaspersky & Dr.Web Antivirus Shield
            item {
                val installed = AdBlockAndSecurityEngine.isCompanionAntivirusInstalled(
                    context = context,
                    provider = settings.resolvedAntivirus
                )
                OrbitGlassSurface(
                    shape = orbitContainerShape(22.dp),
                    elevated = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Security, contentDescription = null, tint = Color(0xFF10E89A))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRu) "Антивирус (Kaspersky / Dr.Web)" else "Antivirus (Kaspersky / Dr.Web)",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(AntivirusProvider.entries) { provider ->
                                FilterChip(
                                    selected = settings.resolvedAntivirus == provider,
                                    onClick = { onUpdateSetting("antivirusProvider", provider.name) },
                                    label = { Text(if (isRu) provider.labelRu else provider.labelEn) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = urlToScan,
                            onValueChange = { urlToScan = it },
                            label = { Text(if (isRu) "URL для проверки угроз" else "URL to inspect") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onRunAiUrlScan(urlToScan) },
                                enabled = !isAiBusy && urlToScan.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = tokens.accentPrimary,
                                    contentColor = Color(0xFF061018)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("antivirus_scan_button")
                            ) {
                                Text(
                                    text = if (isAiBusy) "..." else if (isRu) "Проверить" else "Scan URL",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (settings.resolvedAntivirus != AntivirusProvider.NONE) {
                                OutlinedButton(
                                    onClick = {
                                        val fallbackPortal = AdBlockAndSecurityEngine.openAntivirusCompanionOrPortal(
                                            context,
                                            settings.resolvedAntivirus
                                        )
                                        if (fallbackPortal != null) {
                                            onOpenUrlInTab(fallbackPortal)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (installed) {
                                            if (isRu) "Открыть приложение" else "Open App"
                                        } else {
                                            if (isRu) "Портал проверки" else "Threat Portal"
                                        }
                                    )
                                }
                            }
                        }

                        latestScan?.let { scan ->
                            OrbitGlassSurface(shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = if (scan.isSafe) Icons.Rounded.CheckCircle else Icons.Rounded.WarningAmber,
                                        contentDescription = null,
                                        tint = if (scan.isSafe) Color(0xFF10E89A) else MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(scan.providerLabel, style = MaterialTheme.typography.labelLarge)
                                        Text(scan.verdict, style = MaterialTheme.typography.bodyMedium, color = tokens.secondaryText)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recent Blocked Trackers Log
            if (blockedEvents.isNotEmpty()) {
                item {
                    Text(
                        text = if (isRu) "Последние заблокированные домены" else "Recent Blocked Domains",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(blockedEvents.take(10)) { event ->
                    OrbitGlassSurface(shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Block, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(event.host, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text(event.category, style = MaterialTheme.typography.labelSmall, color = tokens.accentPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GamingPerformanceScreen(
    settings: OrbitSettings,
    telemetry: DeviceTelemetry,
    onBack: () -> Unit,
    onToggleGaming: (Boolean) -> Unit,
    onUpdateSetting: (String, Any) -> Unit,
    onRefreshTelemetry: () -> Unit
) {
    BackHandler(onBack = onBack)
    val tokens = LocalOrbitTokens.current
    val isRu = settings.resolvedLanguage == AppLanguage.RU
    var sliderRam by remember(settings.ramLimitMb) { mutableFloatStateOf(settings.ramLimitMb.toFloat()) }

    LaunchedEffect(Unit) {
        onRefreshTelemetry()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .widthIn(max = 680.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = tokens.onCleanBackground)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isRu) "JupiterGX Mode & Производительность" else "JupiterGX Mode & Performance",
                style = MaterialTheme.typography.titleLarge,
                color = tokens.onCleanBackground
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // JupiterGX Mode Card
            item {
                val gamingOn = settings.resolvedStyleMode == BrowserStyleMode.GAMING_GX
                OrbitGlassSurface(
                    shape = orbitContainerShape(22.dp),
                    elevated = true,
                    gamingRgbGlow = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                JupiterGxLogoEmblem(size = 34.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isRu) "Игровой режим JupiterGX Mode" else "JupiterGX Mode (Gaming Style)",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = if (isRu) "Полосы Юпитера, RGB-контур и игровой HUD" else "Jupiter stripes, RGB glow & gaming HUD",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = tokens.secondaryText
                                    )
                                }
                            }
                            Switch(
                                checked = gamingOn,
                                onCheckedChange = onToggleGaming,
                                modifier = Modifier.testTag("gaming_mode_switch")
                            )
                        }

                        ToggleRow(
                            title = if (isRu) "Динамическая RGB-подсветка" else "Dynamic RGB Border Glow",
                            checked = settings.gamingRgbEnabled,
                            onCheckedChange = { onUpdateSetting("gamingRgbEnabled", it) }
                        )
                        ToggleRow(
                            title = if (isRu) "Тактильный отклик GX" else "GX Tactile Haptics",
                            checked = settings.gamingHapticsEnabled,
                            onCheckedChange = { onUpdateSetting("gamingHapticsEnabled", it) }
                        )
                    }
                }
            }

            // Performance Limiter & Live Android Telemetry
            item {
                OrbitGlassSurface(
                    shape = orbitContainerShape(22.dp),
                    elevated = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Memory, contentDescription = null, tint = Color(0xFF10E89A))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRu) "Режим производительности & RAM Limiter" else "Performance Mode & RAM Limiter",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        val ramRatio = (telemetry.usedRamMb.toFloat() / telemetry.totalRamMb.toFloat()).coerceIn(0.05f, 1f)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isRu) "ОЗУ устройства: ${telemetry.usedRamMb} / ${telemetry.totalRamMb} MB"
                                    else "Device RAM: ${telemetry.usedRamMb} / ${telemetry.totalRamMb} MB",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Heap: ${telemetry.appHeapMb} MB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF10E89A)
                                )
                            }
                            LinearProgressIndicator(
                                progress = { ramRatio },
                                color = Color(0xFF10E89A),
                                trackColor = tokens.glassSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                            )
                            Text(
                                text = if (isRu) {
                                    "Активных вкладок: ${telemetry.activeTabsCount} · В спящем режиме: ${telemetry.hibernatedTabsCount}"
                                } else {
                                    "Active tabs: ${telemetry.activeTabsCount} · Hibernated: ${telemetry.hibernatedTabsCount}"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.secondaryText
                            )
                        }

                        ToggleRow(
                            title = if (isRu) "Активировать режим производительности" else "Enable Performance Mode",
                            checked = settings.performanceModeEnabled,
                            onCheckedChange = { onUpdateSetting("performanceModeEnabled", it) }
                        )
                        ToggleRow(
                            title = if (isRu) "Усыпление фоновых вкладок" else "Tab Hibernation (RAM Saver)",
                            checked = settings.tabHibernationEnabled,
                            onCheckedChange = { onUpdateSetting("tabHibernationEnabled", it) }
                        )

                        Column {
                            Text(
                                text = (if (isRu) "Лимит ОЗУ браузера: " else "Browser RAM Cap: ") + "${sliderRam.roundToInt()} MB",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Slider(
                                value = sliderRam,
                                onValueChange = { sliderRam = it },
                                onValueChangeFinished = {
                                    onUpdateSetting("ramLimitMb", sliderRam.roundToInt().toLong())
                                },
                                valueRange = 512f..4096f,
                                steps = 6,
                                colors = SliderDefaults.colors(thumbColor = tokens.accentPrimary, activeTrackColor = tokens.accentPrimary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(label: String, value: String, color: Color) {
    Column {
        Text(text = value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = LocalOrbitTokens.current.secondaryText)
    }
}

@Composable
private fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
