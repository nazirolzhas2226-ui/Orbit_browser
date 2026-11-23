package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.BlockedEvent
import com.example.model.OrbitSettings
import com.example.ui.components.OrbitGlassSurface
import com.example.ui.components.orbitBouncyClick
import com.example.ui.components.orbitContainerShape
import com.example.ui.theme.LocalOrbitTokens
import com.example.ui.theme.SpaceGroteskFamily

@Composable
fun PrivateDashboardScreen(
    settings: OrbitSettings,
    blockedEvents: List<BlockedEvent>,
    onBack: () -> Unit,
    onUpdateSetting: (String, Any) -> Unit,
    onShredSession: () -> Unit
) {
    BackHandler(onBack = onBack)
    val tokens = LocalOrbitTokens.current
    val isRu = settings.resolvedLanguage == AppLanguage.RU
    var shredConfirmState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .widthIn(max = 680.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = tokens.onCleanBackground
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (isRu) "Приватный дэшборд" else "Private Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        color = tokens.onCleanBackground
                    )
                    Text(
                        text = if (isRu) "Центр приватности & Stealth Shield" else "Privacy Hub & Stealth Shield",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.secondaryText
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = tokens.accentPrimary.copy(alpha = 0.16f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.VisibilityOff,
                        contentDescription = null,
                        tint = tokens.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Live Privacy Radar Scanner
            item {
                OrbitGlassSurface(
                    shape = orbitContainerShape(24.dp),
                    elevated = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Radar, contentDescription = null, tint = tokens.accentPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isRu) "Радар угроз и трекеров в реальном времени" else "Real-Time Threat Radar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF10E89A).copy(alpha = 0.20f)
                            ) {
                                Text(
                                    text = "LIVE",
                                    color = Color(0xFF10E89A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Animated Radar Canvas
                        PrivacyRadarView(
                            modifier = Modifier
                                .size(140.dp)
                                .padding(vertical = 4.dp),
                            primaryColor = tokens.accentPrimary,
                            secondaryColor = tokens.accentSecondary
                        )

                        Text(
                            text = if (isRu) {
                                "Активно перехвачено: ${settings.adsBlockedTotal + settings.trackersBlockedTotal} элементов слежки и рекламных сетей"
                            } else {
                                "Actively intercepted: ${settings.adsBlockedTotal + settings.trackersBlockedTotal} surveillance & tracker elements"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.onCleanBackground,
                            fontWeight = FontWeight.Medium
                        )

                        // Session Shredder Button ("Уничтожить следы")
                        Button(
                            onClick = {
                                shredConfirmState = true
                                onShredSession()
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (shredConfirmState) Color(0xFF10E89A) else MaterialTheme.colorScheme.error.copy(alpha = 0.22f),
                                contentColor = if (shredConfirmState) Color(0xFF061018) else MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("shred_session_button")
                        ) {
                            Icon(
                                imageVector = if (shredConfirmState) Icons.Rounded.CleaningServices else Icons.Rounded.DeleteForever,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (shredConfirmState) {
                                    if (isRu) "Следы сессии уничтожены!" else "Session Shredded!"
                                } else {
                                    if (isRu) "Уничтожить следы сессии (Куки & Кэш)" else "Shred Session Data (Cookies & Cache)"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Core Protection Grid: 4 Pillars
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrivacyPillarCard(
                        icon = Icons.Rounded.Shield,
                        label = if (isRu) "Блокировщик" else "AdBlock",
                        value = "${settings.adsBlockedTotal}",
                        subtitle = if (isRu) "Реклама скрыта" else "Ads blocked",
                        accentColor = tokens.accentPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    PrivacyPillarCard(
                        icon = Icons.Rounded.Fingerprint,
                        label = if (isRu) "Скремблер" else "Canvas Noise",
                        value = if (settings.canvasFingerprintProtection) "Active" else "Off",
                        subtitle = if (isRu) "Шум в отпечатках" else "Noise spoofed",
                        accentColor = tokens.accentSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrivacyPillarCard(
                        icon = Icons.Rounded.VpnKey,
                        label = "WebRTC",
                        value = if (settings.webRtcLeakProtection) "Shielded" else "Open",
                        subtitle = if (isRu) "IP защищен" else "IP Masked",
                        accentColor = Color(0xFF10E89A),
                        modifier = Modifier.weight(1f)
                    )
                    PrivacyPillarCard(
                        icon = Icons.Rounded.Lock,
                        label = "HTTPS Armor",
                        value = if (settings.httpsOnlyEnabled) "100%" else "Standard",
                        subtitle = if (isRu) "TLS 1.3 шифрование" else "TLS 1.3 Strict",
                        accentColor = Color(0xFFFF9F1C),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Stealth Shield Toggles
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
                            Icon(Icons.Rounded.Security, contentDescription = null, tint = tokens.accentPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRu) "Настройки приватности Orbit" else "Orbit Privacy Settings",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DashboardSwitchRow(
                            title = if (isRu) "Защита от утечек IP через WebRTC" else "WebRTC IP Leak Shield",
                            subtitle = if (isRu) "Блокирует раскрытие локального IP в P2P соединениях" else "Blocks private IP exposure via WebRTC calls",
                            checked = settings.webRtcLeakProtection,
                            onCheckedChange = { onUpdateSetting("webRtcLeakProtection", it) }
                        )

                        DashboardSwitchRow(
                            title = if (isRu) "Рандомизация отпечатков Canvas" else "Canvas Fingerprint Scrambler",
                            subtitle = if (isRu) "Добавляет незаметный субпиксельный шум в методы считывания канваса" else "Injects micro-noise into HTML5 canvas reads",
                            checked = settings.canvasFingerprintProtection,
                            onCheckedChange = { onUpdateSetting("canvasFingerprintProtection", it) }
                        )

                        DashboardSwitchRow(
                            title = if (isRu) "Заголовок 'Не отслеживать' (DNT: 1)" else "Do Not Track Header (DNT)",
                            subtitle = if (isRu) "Требует у сайтов прекратить профилирование" else "Requests websites to stop behavioral profiling",
                            checked = settings.doNotTrackEnabled,
                            onCheckedChange = { onUpdateSetting("doNotTrackEnabled", it) }
                        )

                        DashboardSwitchRow(
                            title = if (isRu) "Автоочистка сторонних куки" else "Purge 3rd-Party Cookies",
                            subtitle = if (isRu) "Удаляет межсайтовые куки при закрытии вкладки" else "Purges cross-site cookies on tab destruction",
                            checked = settings.cookieAutoPurgeEnabled,
                            onCheckedChange = { onUpdateSetting("cookieAutoPurgeEnabled", it) }
                        )

                        DashboardSwitchRow(
                            title = if (isRu) "DNS поверх HTTPS (DoH)" else "DNS-over-HTTPS (DoH)",
                            subtitle = if (isRu) "Шифрует DNS-запросы от перехвата провайдером" else "Encrypts domain lookups against ISP sniffing",
                            checked = settings.dnsOverHttpsEnabled,
                            onCheckedChange = { onUpdateSetting("dnsOverHttpsEnabled", it) }
                        )

                        DashboardSwitchRow(
                            title = if (isRu) "Блокировка всплывающих окон" else "Popup & Redirect Blocker",
                            subtitle = if (isRu) "Пресекает агрессивные редиректы и лишние окна" else "Blocks unsolicited popup windows & redirects",
                            checked = settings.blockPopupsEnabled,
                            onCheckedChange = { onUpdateSetting("blockPopupsEnabled", it) }
                        )

                        DashboardSwitchRow(
                            title = if (isRu) "Защита от фишинга (Safe Browsing)" else "Anti-Phishing Shield",
                            subtitle = if (isRu) "Предупреждает о вредоносных и поддельных доменах" else "Guards against phishing & malware domains",
                            checked = settings.antiPhishingShieldEnabled,
                            onCheckedChange = { onUpdateSetting("antiPhishingShieldEnabled", it) }
                        )
                    }
                }
            }

            // 4. Live Blocked Events Stream
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isRu) "Журнал заблокированных угроз" else "Blocked Threats Feed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tokens.onCleanBackground
                    )
                    Text(
                        text = "${blockedEvents.size} " + if (isRu) "записей" else "entries",
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.secondaryText
                    )
                }
            }

            if (blockedEvents.isEmpty()) {
                item {
                    OrbitGlassSurface(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isRu) "Журнал чист: при переходе на сайты здесь будут отображаться заблокированные трекеры"
                                else "Feed is clear: blocked trackers and scripts will appear here as you browse",
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.secondaryText
                            )
                        }
                    }
                }
            } else {
                items(blockedEvents.take(15)) { event ->
                    OrbitGlassSurface(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (event.category.equals("Ad", ignoreCase = true)) tokens.accentPrimary.copy(alpha = 0.2f)
                                    else tokens.accentSecondary.copy(alpha = 0.2f),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.Shield,
                                            contentDescription = null,
                                            tint = if (event.category.equals("Ad", ignoreCase = true)) tokens.accentPrimary else tokens.accentSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = event.host,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (event.category.equals("Ad", ignoreCase = true)) {
                                            if (isRu) "Рекламный баннер / скрипт" else "Ad Server / Script"
                                        } else {
                                            if (isRu) "Аналитический трекер" else "Analytics Tracker"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.secondaryText
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isRu) "БЛОК" else "BLOCKED",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyRadarView(
    modifier: Modifier = Modifier,
    primaryColor: Color,
    secondaryColor: Color
) {
    val transition = rememberInfiniteTransition(label = "radarSpin")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAngle"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxR = size.minDimension / 2f

        // Concentric radar circles
        drawCircle(color = primaryColor.copy(alpha = 0.15f), radius = maxR, center = center, style = Stroke(1.2.dp.toPx()))
        drawCircle(color = primaryColor.copy(alpha = 0.25f), radius = maxR * 0.68f, center = center, style = Stroke(1.dp.toPx()))
        drawCircle(color = primaryColor.copy(alpha = 0.35f), radius = maxR * 0.36f, center = center, style = Stroke(1.dp.toPx()))
        drawCircle(color = primaryColor, radius = 4.dp.toPx(), center = center)

        // Axis crosshairs
        drawLine(
            color = primaryColor.copy(alpha = 0.25f),
            start = Offset(center.x, center.y - maxR),
            end = Offset(center.x, center.y + maxR),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = primaryColor.copy(alpha = 0.25f),
            start = Offset(center.x - maxR, center.y),
            end = Offset(center.x + maxR, center.y),
            strokeWidth = 1.dp.toPx()
        )

        // Rotating radar sweep line with sweep beam
        rotate(degrees = angle, pivot = center) {
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.1f), primaryColor),
                    start = center,
                    end = Offset(center.x, center.y - maxR)
                ),
                start = center,
                end = Offset(center.x, center.y - maxR),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun PrivacyPillarCard(
    icon: ImageVector,
    label: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val tokens = LocalOrbitTokens.current
    OrbitGlassSurface(
        shape = orbitContainerShape(18.dp),
        modifier = modifier.height(106.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.secondaryText
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    color = tokens.onCleanBackground
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.secondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DashboardSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val tokens = LocalOrbitTokens.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = tokens.secondaryText)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
