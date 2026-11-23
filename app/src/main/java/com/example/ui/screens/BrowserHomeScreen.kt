package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AntivirusProvider
import com.example.model.AppLanguage
import com.example.model.BrowserStyleMode
import com.example.model.DeviceTelemetry
import com.example.model.FavoriteSite
import com.example.model.HomeLayoutStyle
import com.example.model.NAccountProfile
import com.example.model.OrbitDockStyle
import com.example.model.OrbitSettings
import com.example.ui.components.JupiterGxLogoEmblem
import com.example.ui.components.MercuryAiLogoEmblem
import com.example.ui.components.OrbitBrandHeader
import com.example.ui.components.OrbitGlassSurface
import com.example.ui.components.OrbitTooltipWrapper
import com.example.ui.components.OrbitUpwardPopup
import com.example.ui.components.orbitBouncyClick
import com.example.ui.components.orbitContainerShape
import com.example.ui.components.orbitExpressiveShape
import com.example.ui.theme.LocalOrbitTokens
import com.example.ui.theme.OrbitStrings
import com.example.ui.theme.SpaceGroteskFamily
import com.example.util.OrbitSoundPlayer
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BrowserHomeScreen(
    settings: OrbitSettings,
    profile: NAccountProfile?,
    favorites: List<FavoriteSite>,
    strings: OrbitStrings,
    tabCount: Int,
    telemetry: DeviceTelemetry,
    onOpenUrl: (String) -> Unit,
    onAddFavorite: (title: String, url: String, badge: String, accentHex: Long) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    onOpenCustomize: () -> Unit,
    onOpenPrivacyReport: () -> Unit,
    onOpenTabsSwitcher: () -> Unit,
    onOpenMenu: () -> Unit,
    onOpenRedAi: () -> Unit,
    onOpenNAccount: () -> Unit,
    onOpenGamingHub: () -> Unit,
    onSaveQuickNotes: (String) -> Unit = {}
) {
    val tokens = LocalOrbitTokens.current
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddFavDialog by remember { mutableStateOf(false) }
    var editFavoritesMode by remember { mutableStateOf(false) }
    var showVoiceSearchDialog by remember { mutableStateOf(false) }
    var showNAccountPopup by remember { mutableStateOf(false) }
    var selectedFavoritePopup by remember { mutableStateOf<FavoriteSite?>(null) }
    var cosmosFactPopupIndex by remember { mutableStateOf<Int?>(null) }

    val layout = settings.resolvedLayout
    val isRu = settings.resolvedLanguage == AppLanguage.RU
    val (iconPrimary, iconSecondary) = tokens.resolvedIconColors()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .widthIn(max = 640.dp)
                .align(Alignment.TopCenter)
        ) {
            // Top bar: Orbit logo + name centered, NAccount profile icon top-right (matching screenshot!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                OrbitBrandHeader(
                    modifier = Modifier.align(Alignment.Center)
                )

                OrbitTooltipWrapper(tooltipText = "NAccount: ${profile?.naccountHandle ?: "@orbit"}") {
                    OrbitGlassSurface(
                        shape = orbitExpressiveShape(2, CircleShape),
                        elevated = true,
                        gamingRgbGlow = settings.gamingRgbEnabled,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(44.dp)
                            .orbitBouncyClick(onClick = { showNAccountPopup = true })
                            .testTag("naccount_profile_button")
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(orbitExpressiveShape(3, CircleShape))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(tokens.accentPrimary, tokens.accentSecondary)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (profile?.displayName?.firstOrNull() ?: 'O').uppercaseChar().toString(),
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Scrollable home widgets
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 148.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Active Status Chips Row (Focus Timer / VPN / Gaming / Performance)
                item(key = "status_strip") {
                    var timerSeconds by remember { mutableIntStateOf(25 * 60) }
                    var timerRunning by remember { mutableStateOf(false) }

                    LaunchedEffect(timerRunning) {
                        while (timerRunning && timerSeconds > 0) {
                            delay(1000L)
                            timerSeconds--
                            if (timerSeconds == 0) {
                                timerRunning = false
                                OrbitSoundPlayer.playSuccess()
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val mm = timerSeconds / 60
                        val ss = timerSeconds % 60
                        StatusPill(
                            icon = if (timerRunning) Icons.Rounded.Pause else Icons.Rounded.Timer,
                            text = String.format("Focus %02d:%02d", mm, ss),
                            accent = if (timerRunning) Color(0xFF10E89A) else tokens.accentPrimary,
                            onClick = {
                                if (timerSeconds == 0) timerSeconds = 25 * 60
                                timerRunning = !timerRunning
                            }
                        )
                        if (settings.vpnEnabled) {
                            StatusPill(
                                icon = Icons.Rounded.VpnKey,
                                text = "VPN · ${settings.resolvedVpnRegion.countryCode}",
                                accent = tokens.accentPrimary,
                                onClick = onOpenPrivacyReport
                            )
                        }
                        if (settings.resolvedStyleMode == BrowserStyleMode.GAMING_GX) {
                            StatusPill(
                                icon = Icons.Rounded.SportsEsports,
                                text = strings.gamingModeBanner,
                                accent = tokens.gxGlowColor,
                                onClick = onOpenGamingHub
                            )
                        }
                        if (settings.performanceModeEnabled) {
                            StatusPill(
                                icon = Icons.Rounded.Memory,
                                text = "${settings.ramLimitMb} MB RAM",
                                accent = Color(0xFF10E89A),
                                onClick = onOpenGamingHub
                            )
                        }
                    }
                }

                // Favorites Section (right below Orbit header, matching screenshot!)
                if (settings.showFavorites && layout != HomeLayoutStyle.MINIMAL) {
                    item(key = "favorites_section") {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn(initialScale = 0.96f)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Person,
                                            contentDescription = null,
                                            tint = iconPrimary,
                                            modifier = Modifier.size(19.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = strings.favorites,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.onCleanBackground
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (favorites.isNotEmpty()) {
                                            OrbitGlassSurface(
                                                shape = orbitExpressiveShape(4, CircleShape),
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .orbitBouncyClick { editFavoritesMode = !editFavoritesMode }
                                            ) {
                                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                                    Icon(
                                                        imageVector = if (editFavoritesMode) Icons.Rounded.Close else Icons.Rounded.Tune,
                                                        contentDescription = "Edit Favorites",
                                                        tint = iconSecondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                        OrbitGlassSurface(
                                            shape = orbitExpressiveShape(5, CircleShape),
                                            modifier = Modifier
                                                .size(32.dp)
                                                .orbitBouncyClick { showAddFavDialog = true }
                                                .testTag("add_favorite_button")
                                        ) {
                                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Add,
                                                    contentDescription = strings.addFavorite,
                                                    tint = iconPrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                val columns = if (layout == HomeLayoutStyle.COMPACT) 5 else 4
                                val rows = favorites.chunked(columns)
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    rows.forEachIndexed { rowIdx, rowItems ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            rowItems.forEachIndexed { colIdx, fav ->
                                                Box(modifier = Modifier.weight(1f)) {
                                                    FavoriteTileItem(
                                                        favorite = fav,
                                                        index = rowIdx * columns + colIdx,
                                                        isEditing = editFavoritesMode,
                                                        onClick = { onOpenUrl(fav.url) },
                                                        onLongClick = { selectedFavoritePopup = fav },
                                                        onDelete = { onRemoveFavorite(fav.id) }
                                                    )
                                                }
                                            }
                                            repeat(columns - rowItems.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Privacy Report Glass Card (matching screenshot: "Отчёт о конфиденциальности / Защита от трекинга")
                if (settings.showPrivacyReport && layout != HomeLayoutStyle.MINIMAL) {
                    item(key = "privacy_report_card") {
                        val totalBlocked = settings.adsBlockedTotal + settings.trackersBlockedTotal
                        OrbitGlassSurface(
                            shape = orbitContainerShape(22.dp),
                            gamingRgbGlow = settings.gamingRgbEnabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .orbitBouncyClick(onClick = onOpenPrivacyReport)
                                .testTag("privacy_report_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 15.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = orbitExpressiveShape(0, RoundedCornerShape(14.dp)),
                                    color = iconPrimary.copy(alpha = 0.16f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.Shield,
                                            contentDescription = strings.privacyReport,
                                            tint = iconPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.privacyReport,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = tokens.onCleanBackground
                                    )
                                    val avLabel = when (settings.resolvedAntivirus) {
                                        AntivirusProvider.NONE -> ""
                                        AntivirusProvider.KASPERSKY -> " · Kaspersky"
                                        AntivirusProvider.DR_WEB -> " · Dr.Web"
                                        AntivirusProvider.BOTH -> " · Kaspersky + Dr.Web"
                                    }
                                    Text(
                                        text = "${strings.trackingProtection} · ${String.format(strings.blockedCountFormat, totalBlocked)}$avLabel",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = tokens.secondaryText,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = tokens.secondaryText
                                )
                            }
                        }
                    }
                }

                // MercuryAI Quick Card (when enabled or Dashboard layout)
                if ((settings.showRedAiBar || layout == HomeLayoutStyle.DASHBOARD) && layout != HomeLayoutStyle.MINIMAL) {
                    item(key = "redai_quick_card") {
                        OrbitGlassSurface(
                            shape = orbitContainerShape(22.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .orbitBouncyClick(onClick = onOpenRedAi)
                                .testTag("redai_home_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = orbitExpressiveShape(1, RoundedCornerShape(14.dp)),
                                    color = Color(0xFFFF3E66).copy(alpha = 0.16f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        MercuryAiLogoEmblem(size = 26.dp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "MercuryAI · ${settings.resolvedMercuryModel.labelEn}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = tokens.onCleanBackground
                                    )
                                    Text(
                                        text = if (isRu) "Спросить ИИ, проверить ссылку или сгенерировать идею"
                                        else "Ask AI, verify a link, or summarize the web",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = tokens.secondaryText,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = tokens.secondaryText
                                )
                            }
                        }
                    }
                }

                // JupiterGX & Performance Telemetry Card (when enabled or Dashboard layout)
                if (settings.showGamingTelemetry || layout == HomeLayoutStyle.DASHBOARD) {
                    item(key = "gaming_telemetry_card") {
                        AnimatedVisibility(
                            visible = true,
                            enter = expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) + fadeIn()
                        ) {
                            OrbitGlassSurface(
                                shape = orbitContainerShape(22.dp),
                                gamingRgbGlow = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .orbitBouncyClick(onClick = onOpenGamingHub)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = if (settings.resolvedStyleMode == BrowserStyleMode.GAMING_GX) {
                                                "JupiterGX Control · ${telemetry.usedRamMb}/${telemetry.totalRamMb} MB"
                                            } else {
                                                "Performance · ${telemetry.appHeapMb} MB Heap"
                                            },
                                            style = MaterialTheme.typography.titleMedium,
                                            color = tokens.onCleanBackground
                                        )
                                        Text(
                                            text = if (isRu) {
                                                "Лимит: ${settings.ramLimitMb} MB · Активных вкладок: ${telemetry.activeTabsCount}"
                                            } else {
                                                "RAM Limit: ${settings.ramLimitMb} MB · Active tabs: ${telemetry.activeTabsCount}"
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = tokens.secondaryText
                                        )
                                    }
                                    JupiterGxLogoEmblem(size = 28.dp)
                                }
                            }
                        }
                    }
                }

                // Cosmos & Astronomy Fact Widget (James Webb)
                if (settings.showCosmosWidget && layout != HomeLayoutStyle.MINIMAL) {
                    item(key = "cosmos_astronomy_widget") {
                        var factIndex by remember { mutableStateOf(0) }
                        val facts = listOf(
                            Pair(
                                if (isRu) "James Webb: Галактика JADES-GS-z14-0" else "James Webb: Galaxy JADES-GS-z14-0",
                                if (isRu) "Обнаружена самая далёкая галактика Вселенной, возникшая всего через 290 млн лет после Большого Взрыва."
                                else "Discovered the earliest galaxy formed just 290 million years after the Big Bang."
                            ),
                            Pair(
                                if (isRu) "Шторм на Юпитере: Великое Красное Пятно" else "Jupiter: Great Red Spot",
                                if (isRu) "Антициклон шире Земли бушует в атмосфере Юпитера уже более 350 земных лет непрерывно."
                                else "A colossal storm larger than Earth has raged on Jupiter for over 350 years."
                            ),
                            Pair(
                                if (isRu) "Кольца Сатурна: Лёд и Гравитация" else "Saturn's Rings: Ice & Gravity",
                                if (isRu) "Ширина колец составляет 282 000 км, но их толщина всего 10-20 метров!"
                                else "Saturn's rings span 282,000 km across, yet are only 10 to 20 meters thick!"
                            ),
                            Pair(
                                if (isRu) "Пульсар PSR J1748-2446ad" else "Pulsar PSR J1748-2446ad",
                                if (isRu) "Нейтронная звезда делает 716 оборотов в секунду — фантастическая плотность и скорость!"
                                else "Neutron star rotates 716 times per second with incredible cosmic density!"
                            ),
                            Pair(
                                if (isRu) "Туманность Андромеды (M31)" else "Andromeda Nebula (M31)",
                                if (isRu) "Содержит более 1 триллиона звёзд и приближается к нашей галактике со скоростью 110 км/с."
                                else "Contains 1 trillion stars and approaches our galaxy at 110 km/s."
                            )
                        )
                        val currentFact = facts[factIndex % facts.size]

                        OrbitGlassSurface(
                            shape = orbitContainerShape(22.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .orbitBouncyClick { cosmosFactPopupIndex = factIndex % facts.size }
                                .testTag("cosmos_widget_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFF6C5CE7).copy(alpha = 0.2f),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Public,
                                                    contentDescription = "Cosmos",
                                                    tint = Color(0xFF8B5CF6),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = if (isRu) "Космос & Астрономия" else "Cosmos & Astronomy",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = tokens.onCleanBackground
                                            )
                                            Text(
                                                text = "James Webb Space Telescope",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF8B5CF6)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { factIndex = (factIndex + 1) % facts.size },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = "Shuffle",
                                            tint = tokens.secondaryText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = currentFact.first,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = tokens.onCleanBackground
                                )
                                Text(
                                    text = currentFact.second,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.secondaryText
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = { onOpenUrl(cosmosWikipediaUrl(factIndex % facts.size, isRu)) }
                                    ) {
                                        Text(
                                            text = if (isRu) "Исследовать в Orbit →" else "Explore in Orbit →",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = tokens.accentPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Notes / Scratchpad Widget
                if (settings.showQuickNotesWidget && layout != HomeLayoutStyle.MINIMAL) {
                    item(key = "quick_notes_widget") {
                        var noteText by remember(settings.quickNotesText) { mutableStateOf(settings.quickNotesText) }
                        OrbitGlassSurface(
                            shape = orbitContainerShape(22.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_notes_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Tune,
                                            contentDescription = null,
                                            tint = tokens.accentPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isRu) "Быстрые заметки Orbit" else "Orbit Scratchpad",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.onCleanBackground
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        if (noteText != settings.quickNotesText) {
                                            TextButton(onClick = { onSaveQuickNotes(noteText) }) {
                                                Text(
                                                    text = if (isRu) "Сохранить" else "Save",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = tokens.accentPrimary
                                                )
                                            }
                                        }
                                        if (noteText.isNotBlank()) {
                                            TextButton(onClick = {
                                                noteText = ""
                                                onSaveQuickNotes("")
                                            }) {
                                                Text(if (isRu) "Очистить" else "Clear", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(74.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(tokens.glassSurface)
                                        .padding(10.dp)
                                ) {
                                    if (noteText.isEmpty()) {
                                        Text(
                                            text = if (isRu) "Запишите идею, пароль или ссылку..." else "Jot down an idea or quick link...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = tokens.secondaryText
                                        )
                                    }
                                    BasicTextField(
                                        value = noteText,
                                        onValueChange = { noteText = it },
                                        textStyle = TextStyle(
                                            color = tokens.onCleanBackground,
                                            fontSize = 14.sp,
                                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
                                        ),
                                        cursorBrush = SolidColor(tokens.accentPrimary),
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Customize Pill ("Изменить") + Floating Liquid Glass Search Dock (matching screenshot!)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // "Изменить" pill button right above the dock
            OrbitGlassSurface(
                shape = CircleShape,
                elevated = true,
                modifier = Modifier
                    .orbitBouncyClick(onClick = onOpenCustomize)
                    .testTag("customize_home_button")
            ) {
                Text(
                    text = strings.customize,
                    style = MaterialTheme.typography.labelLarge,
                    color = tokens.onCleanBackground,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                )
            }

            // Dynamic Floating Dock according to settings.dockStyle (smoothly animated!)
            androidx.compose.animation.AnimatedContent(
                targetState = settings.resolvedDockStyle,
                transitionSpec = {
                    (androidx.compose.animation.slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = spring(
                            dampingRatio = tokens.animationStyle.dampingRatio,
                            stiffness = tokens.animationStyle.stiffness
                        )
                    ) + fadeIn()) togetherWith (androidx.compose.animation.slideOutVertically { it / 3 } + fadeOut())
                },
                label = "dockStyleTransition"
            ) { dockStyle ->
            when (dockStyle) {
                OrbitDockStyle.SAFARI_CAPSULE -> {
                    // 1: Safari Capsule Dock (Classic 3-segment)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OrbitTooltipWrapper(tooltipText = strings.tabsTitle) {
                            OrbitGlassSurface(
                                shape = CircleShape,
                                elevated = true,
                                gamingRgbGlow = settings.gamingRgbEnabled,
                                modifier = Modifier
                                    .size(50.dp)
                                    .orbitBouncyClick(onClick = onOpenTabsSwitcher)
                                    .testTag("tabs_switcher_button")
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
                            }
                        }

                        // Capsule Search / URL Bar
                        OrbitGlassSurface(
                            shape = orbitContainerShape(28.dp),
                            elevated = true,
                            gamingRgbGlow = settings.gamingRgbEnabled,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = null,
                                    tint = tokens.secondaryText,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = strings.searchPlaceholder,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = tokens.secondaryText
                                        )
                                    }
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            color = tokens.onCleanBackground,
                                            fontSize = 15.sp,
                                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                                        ),
                                        cursorBrush = SolidColor(tokens.accentPrimary),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                        keyboardActions = KeyboardActions(
                                            onSearch = {
                                                if (searchQuery.isNotBlank()) {
                                                    val target = searchQuery
                                                    searchQuery = ""
                                                    onOpenUrl(target)
                                                }
                                            }
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("url_search_input")
                                    )
                                }
                                OrbitTooltipWrapper(tooltipText = if (isRu) "Голосовой поиск" else "Voice Search") {
                                    IconButton(
                                        onClick = { showVoiceSearchDialog = true },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("voice_search_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Mic,
                                            contentDescription = "Voice Search",
                                            tint = tokens.secondaryText,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                OrbitTooltipWrapper(tooltipText = "MercuryAI") {
                                    IconButton(
                                        onClick = onOpenRedAi,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("redai_quick_button")
                                    ) {
                                        MercuryAiLogoEmblem(size = 20.dp)
                                    }
                                }
                            }
                        }

                        OrbitTooltipWrapper(tooltipText = strings.settingsTitle) {
                            OrbitGlassSurface(
                                shape = CircleShape,
                                elevated = true,
                                gamingRgbGlow = settings.gamingRgbEnabled,
                                modifier = Modifier
                                    .size(50.dp)
                                    .orbitBouncyClick(onClick = onOpenMenu)
                                    .testTag("orbit_menu_button")
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Rounded.MoreHoriz,
                                        contentDescription = "Orbit Menu",
                                        tint = iconPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                OrbitDockStyle.MEGA_DOCK -> {
                    // 2: Очень большое (Mega Dock - 2-tier luxury command bar with labeled buttons)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Upper Search Bar
                        OrbitGlassSurface(
                            shape = orbitContainerShape(24.dp),
                            elevated = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Search, contentDescription = null, tint = tokens.secondaryText, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (searchQuery.isEmpty()) {
                                        Text(strings.searchPlaceholder, style = MaterialTheme.typography.bodyLarge, color = tokens.secondaryText)
                                    }
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        singleLine = true,
                                        textStyle = TextStyle(color = tokens.onCleanBackground, fontSize = 15.sp),
                                        cursorBrush = SolidColor(tokens.accentPrimary),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                        keyboardActions = KeyboardActions(
                                            onSearch = {
                                                if (searchQuery.isNotBlank()) {
                                                    val t = searchQuery
                                                    searchQuery = ""
                                                    onOpenUrl(t)
                                                }
                                            }
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("url_search_input")
                                    )
                                }
                                IconButton(onClick = { showVoiceSearchDialog = true }, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Rounded.Mic, contentDescription = "Voice", tint = tokens.secondaryText, modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = onOpenRedAi, modifier = Modifier.size(36.dp)) {
                                    MercuryAiLogoEmblem(size = 20.dp)
                                }
                            }
                        }

                        // Lower Row: 5 Labeled Quick Action Glass Tiles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MegaDockTile(
                                label = if (isRu) "Вкладки ($tabCount)" else "Tabs ($tabCount)",
                                icon = Icons.Rounded.Public,
                                accent = tokens.accentPrimary,
                                modifier = Modifier.weight(1f),
                                onClick = onOpenTabsSwitcher
                            )
                            MegaDockTile(
                                label = if (isRu) "Радар" else "Radar",
                                icon = Icons.Rounded.Radar,
                                accent = Color(0xFF00E5FF),
                                modifier = Modifier.weight(1f),
                                onClick = onOpenPrivacyReport
                            )
                            MegaDockTile(
                                label = "MercuryAI",
                                icon = Icons.Rounded.AutoAwesome,
                                accent = Color(0xFFFF2E55),
                                modifier = Modifier.weight(1f),
                                onClick = onOpenRedAi
                            )
                            MegaDockTile(
                                label = "JupiterGX",
                                icon = Icons.Rounded.SportsEsports,
                                accent = Color(0xFFFF1E56),
                                modifier = Modifier.weight(1f),
                                onClick = onOpenGamingHub
                            )
                            MegaDockTile(
                                label = if (isRu) "Меню" else "Menu",
                                icon = Icons.Rounded.MoreHoriz,
                                accent = tokens.onCleanBackground,
                                modifier = Modifier.weight(1f),
                                onClick = onOpenMenu
                            )
                        }
                    }
                }

                OrbitDockStyle.MINIMAL_PILL -> {
                    // 3: Минималистика (Ultra-sleek 46dp single continuous floating capsule)
                    OrbitGlassSurface(
                        shape = orbitContainerShape(26.dp),
                        elevated = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Search, contentDescription = null, tint = tokens.secondaryText, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = strings.searchPlaceholder,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = tokens.secondaryText,
                                        maxLines = 1
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    textStyle = TextStyle(color = tokens.onCleanBackground, fontSize = 14.sp),
                                    cursorBrush = SolidColor(tokens.accentPrimary),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(
                                        onSearch = {
                                            if (searchQuery.isNotBlank()) {
                                                val t = searchQuery
                                                searchQuery = ""
                                                onOpenUrl(t)
                                            }
                                        }
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("url_search_input")
                                )
                            }
                            IconButton(onClick = { showVoiceSearchDialog = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Rounded.Mic, contentDescription = "Voice", tint = tokens.secondaryText, modifier = Modifier.size(18.dp))
                            }
                            Surface(
                                shape = CircleShape,
                                color = tokens.accentPrimary.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .size(28.dp)
                                    .orbitBouncyClick(onClick = onOpenTabsSwitcher)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = tabCount.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.onCleanBackground
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(onClick = onOpenMenu, modifier = Modifier.size(32.dp).testTag("orbit_menu_button")) {
                                Icon(Icons.Rounded.MoreHoriz, contentDescription = "Menu", tint = tokens.onCleanBackground, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                OrbitDockStyle.KOSMOS_ORBITAL -> {
                    // 4: Kosmos (Orbital cosmic dock with planetary rings and celestial glow)
                    OrbitGlassSurface(
                        shape = orbitContainerShape(28.dp),
                        elevated = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Celestial Saturn/Planet ring tabs button
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF6C5CE7).copy(alpha = 0.25f),
                                modifier = Modifier
                                    .size(42.dp)
                                    .orbitBouncyClick(onClick = onOpenTabsSwitcher)
                                    .testTag("tabs_switcher_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Public,
                                        contentDescription = "Cosmos Tabs",
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(2.dp)
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFD4AF37)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = tabCount.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }

                            // Cosmic Search Capsule with Starlight shimmer
                            Box(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(Color(0xFF0B132B).copy(alpha = 0.5f))
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.Search, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = if (isRu) "Орбитальный поиск..." else "Orbital Search...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color(0xFF8B9CB5)
                                            )
                                        }
                                        BasicTextField(
                                            value = searchQuery,
                                            onValueChange = { searchQuery = it },
                                            singleLine = true,
                                            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                                            cursorBrush = SolidColor(Color(0xFF00E5FF)),
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                            keyboardActions = KeyboardActions(
                                                onSearch = {
                                                    if (searchQuery.isNotBlank()) {
                                                        val t = searchQuery
                                                        searchQuery = ""
                                                        onOpenUrl(t)
                                                    }
                                                }
                                            ),
                                            modifier = Modifier.fillMaxWidth().testTag("url_search_input")
                                        )
                                    }
                                    IconButton(onClick = { showVoiceSearchDialog = true }, modifier = Modifier.size(30.dp)) {
                                        Icon(Icons.Rounded.Mic, contentDescription = "Voice", tint = Color(0xFF8B9CB5), modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = onOpenRedAi, modifier = Modifier.size(30.dp)) {
                                        MercuryAiLogoEmblem(size = 18.dp)
                                    }
                                }
                            }

                            // Orbital Menu Button
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFD4AF37).copy(alpha = 0.22f),
                                modifier = Modifier
                                    .size(42.dp)
                                    .orbitBouncyClick(onClick = onOpenMenu)
                                    .testTag("orbit_menu_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.MoreHoriz,
                                        contentDescription = "Cosmos Menu",
                                        tint = Color(0xFFD4AF37),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            }
        }
    }

    AddFavoriteDialog(
        visible = showAddFavDialog,
        isRu = isRu,
        onDismiss = { showAddFavDialog = false },
        onConfirm = { title, url ->
            val badge = title.trim().take(2).uppercase().ifBlank { "W" }
            val paletteColors = listOf(0xFF00D4FFL, 0xFFFF3E66L, 0xFF7B61FFL, 0xFF10E89AL, 0xFFFF9F1CL)
            val colorHex = paletteColors[(title.length + favorites.size) % paletteColors.size]
            onAddFavorite(title, url, badge, colorHex)
            showAddFavDialog = false
        }
    )

    VoiceSearchDialog(
        visible = showVoiceSearchDialog,
        isRu = isRu,
        onDismiss = { showVoiceSearchDialog = false },
        onQueryRecognized = { query ->
            showVoiceSearchDialog = false
            onOpenUrl(query)
        }
    )

    NAccountQuickPopup(
        visible = showNAccountPopup,
        profile = profile,
        isRu = isRu,
        onDismiss = { showNAccountPopup = false },
        onOpenFullNAccount = {
            showNAccountPopup = false
            onOpenNAccount()
        },
        onOpenPrivateDashboard = {
            showNAccountPopup = false
            onOpenPrivacyReport()
        }
    )

    FavoriteQuickPopup(
        favorite = selectedFavoritePopup,
        isRu = isRu,
        editModeActive = editFavoritesMode,
        onDismiss = { selectedFavoritePopup = null },
        onOpenSite = { url ->
            selectedFavoritePopup = null
            onOpenUrl(url)
        },
        onToggleEditMode = {
            selectedFavoritePopup = null
            editFavoritesMode = !editFavoritesMode
        },
        onDeleteFavorite = { id ->
            selectedFavoritePopup = null
            onRemoveFavorite(id)
        }
    )

    CosmosFactPopup(
        factIndex = cosmosFactPopupIndex,
        isRu = isRu,
        onDismiss = { cosmosFactPopupIndex = null },
        onChangeFact = { cosmosFactPopupIndex = it },
        onExplore = { url ->
            cosmosFactPopupIndex = null
            onOpenUrl(url)
        }
    )
}

@Composable
private fun StatusPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    accent: Color,
    onClick: () -> Unit
) {
    OrbitGlassSurface(
        shape = CircleShape,
        modifier = Modifier.orbitBouncyClick(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = LocalOrbitTokens.current.onCleanBackground
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FavoriteTileItem(
    favorite: FavoriteSite,
    index: Int = 0,
    isEditing: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit
) {
    val tokens = LocalOrbitTokens.current
    val tileColor = Color(favorite.accentHex)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { if (!isEditing) onClick() },
                onLongClick = onLongClick
            )
            .testTag("favorite_tile_${favorite.title.lowercase()}")
    ) {
        Box {
            Surface(
                shape = orbitExpressiveShape(index, orbitContainerShape(18.dp)),
                color = if (favorite.iconBadge == "APPLE" || favorite.iconBadge == "GOOGLE" || favorite.iconBadge == "BING") {
                    Color.White
                } else {
                    tileColor
                },
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(64.dp)
                    .orbitBouncyClick { if (!isEditing) onClick() }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    val glyph = when (favorite.iconBadge.uppercase()) {
                        "APPLE" -> "A"
                        "BING" -> "b"
                        "GOOGLE" -> "G"
                        "YAHOO" -> "Y"
                        else -> favorite.title.take(2).uppercase()
                    }
                    val glyphColor = when (favorite.iconBadge.uppercase()) {
                        "APPLE" -> Color(0xFF111827)
                        "BING" -> Color(0xFF008373)
                        "GOOGLE" -> Color(0xFF4285F4)
                        "YAHOO" -> Color.White
                        else -> Color.White
                    }
                    Text(
                        text = glyph,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = glyphColor
                    )
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isEditing,
                enter = scaleIn() + fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(22.dp)
                        .orbitBouncyClick(onClick = onDelete)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Remove Favorite",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = favorite.title,
            style = MaterialTheme.typography.labelLarge,
            color = tokens.onCleanBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AddFavoriteDialog(
    visible: Boolean,
    isRu: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val tokens = LocalOrbitTokens.current
    var title by remember(visible) { mutableStateOf("") }
    var url by remember(visible) { mutableStateOf("") }

    OrbitUpwardPopup(
        visible = visible,
        onDismiss = onDismiss
    ) {
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
                Text(
                    text = if (isRu) "Добавить в Избранное" else "Add Favorite Site",
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.onCleanBackground
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.onCleanBackground)
                }
            }
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(if (isRu) "Название" else "Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL (e.g. wikipedia.org)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isRu) "Отмена" else "Cancel", color = tokens.secondaryText)
                }
                Button(
                    onClick = {
                        if (title.isNotBlank() && url.isNotBlank()) {
                            onConfirm(title, url)
                        }
                    },
                    shape = orbitContainerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentPrimary,
                        contentColor = Color(0xFF061018)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isRu) "Сохранить" else "Save", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun VoiceSearchDialog(
    visible: Boolean,
    isRu: Boolean,
    onDismiss: () -> Unit,
    onQueryRecognized: (String) -> Unit
) {
    val tokens = LocalOrbitTokens.current
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.24f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micPulseScale"
    )

    OrbitUpwardPopup(
        visible = visible,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isRu) "Голосовой поиск Orbit" else "Orbit Voice Search",
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.onCleanBackground
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.onCleanBackground)
                }
            }
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .graphicsLayer {
                        scaleX = waveScale
                        scaleY = waveScale
                    }
                    .clip(orbitExpressiveShape(1, CircleShape))
                    .background(tokens.accentPrimary.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Mic,
                    contentDescription = null,
                    tint = tokens.accentPrimary,
                    modifier = Modifier.size(38.dp)
                )
            }
            Text(
                text = if (isRu) "Слушаю... Произнесите запрос или выберите быстрый:" else "Listening... Speak or tap a quick prompt:",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.secondaryText
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val quickQueries = if (isRu) listOf("Новости дня", "Погода", "MercuryAI обзор")
                else listOf("Today's News", "Weather", "MercuryAI Overview")
                quickQueries.forEach { q ->
                    OrbitGlassSurface(
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .orbitBouncyClick { onQueryRecognized(q) }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = q,
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.onCleanBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NAccountQuickPopup(
    visible: Boolean,
    profile: NAccountProfile?,
    isRu: Boolean,
    onDismiss: () -> Unit,
    onOpenFullNAccount: () -> Unit,
    onOpenPrivateDashboard: () -> Unit
) {
    val tokens = LocalOrbitTokens.current
    OrbitUpwardPopup(
        visible = visible,
        onDismiss = onDismiss
    ) {
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
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(orbitExpressiveShape(2, CircleShape))
                            .background(Brush.linearGradient(listOf(tokens.accentPrimary, tokens.accentSecondary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (profile?.displayName?.firstOrNull() ?: 'O').uppercaseChar().toString(),
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = profile?.displayName ?: (if (isRu) "Гость Orbit" else "Orbit Guest"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = tokens.onCleanBackground
                        )
                        Text(
                            text = profile?.naccountHandle ?: "@orbit_local",
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.accentPrimary
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.onCleanBackground)
                }
            }

            OrbitGlassSurface(
                shape = orbitContainerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Shield, contentDescription = null, tint = Color(0xFF10E89A), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (profile?.cloudSyncEnabled == true) {
                                if (isRu) "NAccount Облако синхронизировано" else "NAccount Cloud Synced"
                            } else {
                                if (isRu) "Приватный локальный профиль" else "Private Local Session"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.onCleanBackground
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OrbitGlassSurface(
                    shape = orbitContainerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .orbitBouncyClick(onClick = onOpenPrivateDashboard)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Rounded.Radar, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRu) "Радар" else "Radar",
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.onCleanBackground
                        )
                    }
                }
                Button(
                    onClick = onOpenFullNAccount,
                    shape = orbitContainerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentPrimary,
                        contentColor = Color(0xFF061018)
                    ),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Rounded.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRu) "Профиль NAccount" else "NAccount Profile",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteQuickPopup(
    favorite: FavoriteSite?,
    isRu: Boolean,
    editModeActive: Boolean,
    onDismiss: () -> Unit,
    onOpenSite: (String) -> Unit,
    onToggleEditMode: () -> Unit,
    onDeleteFavorite: (String) -> Unit
) {
    val tokens = LocalOrbitTokens.current
    OrbitUpwardPopup(
        visible = favorite != null,
        onDismiss = onDismiss
    ) {
        if (favorite != null) {
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
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = orbitContainerShape(14.dp),
                            color = Color(favorite.accentHex),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = favorite.title.take(2).uppercase(),
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = favorite.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = tokens.onCleanBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = favorite.url,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.secondaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.onCleanBackground)
                    }
                }

                Button(
                    onClick = { onOpenSite(favorite.url) },
                    shape = orbitContainerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentPrimary,
                        contentColor = Color(0xFF061018)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Rounded.Public, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRu) "Открыть сайт" else "Open Website", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OrbitGlassSurface(
                        shape = orbitContainerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .orbitBouncyClick(onClick = onToggleEditMode)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = if (editModeActive) (if (isRu) "Готово" else "Done Editing")
                                else (if (isRu) "Упорядочить" else "Edit Grid"),
                                style = MaterialTheme.typography.labelLarge,
                                color = tokens.onCleanBackground
                            )
                        }
                    }
                    OrbitGlassSurface(
                        shape = orbitContainerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .orbitBouncyClick { onDeleteFavorite(favorite.id) }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = if (isRu) "Удалить" else "Remove",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CosmosFactPopup(
    factIndex: Int?,
    isRu: Boolean,
    onDismiss: () -> Unit,
    onChangeFact: (Int) -> Unit,
    onExplore: (String) -> Unit
) {
    val tokens = LocalOrbitTokens.current
    val facts = listOf(
        Pair(
            if (isRu) "James Webb: Галактика JADES-GS-z14-0" else "James Webb: Galaxy JADES-GS-z14-0",
            if (isRu) "Обнаружена самая далёкая галактика Вселенной, возникшая всего через 290 млн лет после Большого Взрыва."
            else "Discovered the earliest galaxy formed just 290 million years after the Big Bang."
        ),
        Pair(
            if (isRu) "Шторм на Юпитере: Великое Красное Пятно" else "Jupiter: Great Red Spot",
            if (isRu) "Антициклон шире Земли бушует в атмосфере Юпитера уже более 350 земных лет непрерывно."
            else "A colossal storm larger than Earth has raged on Jupiter for over 350 years."
        ),
        Pair(
            if (isRu) "Кольца Сатурна: Лёд и Гравитация" else "Saturn's Rings: Ice & Gravity",
            if (isRu) "Ширина колец составляет 282 000 км, но их толщина всего 10-20 метров!"
            else "Saturn's rings span 282,000 km across, yet are only 10 to 20 meters thick!"
        ),
        Pair(
            if (isRu) "Пульсар PSR J1748-2446ad" else "Pulsar PSR J1748-2446ad",
            if (isRu) "Нейтронная звезда делает 716 оборотов в секунду — фантастическая плотность и скорость!"
            else "Neutron star rotates 716 times per second with incredible cosmic density!"
        ),
        Pair(
            if (isRu) "Туманность Андромеды (M31)" else "Andromeda Nebula (M31)",
            if (isRu) "Содержит более 1 триллиона звёзд и приближается к нашей галактике со скоростью 110 км/с."
            else "Contains 1 trillion stars and approaches our galaxy at 110 km/s."
        )
    )

    OrbitUpwardPopup(
        visible = factIndex != null,
        onDismiss = onDismiss
    ) {
        if (factIndex != null) {
            val safeIdx = ((factIndex % facts.size) + facts.size) % facts.size
            val fact = facts[safeIdx]
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
                            color = Color(0xFF8B5CF6).copy(alpha = 0.20f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Public, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isRu) "Космос & James Webb" else "Cosmos & James Webb",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = tokens.onCleanBackground
                            )
                            Text(
                                text = "${safeIdx + 1} / ${facts.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF8B5CF6)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.onCleanBackground)
                    }
                }

                Text(
                    text = fact.first,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = tokens.onCleanBackground
                )
                Text(
                    text = fact.second,
                    style = MaterialTheme.typography.bodyLarge,
                    color = tokens.secondaryText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OrbitGlassSurface(
                        shape = orbitContainerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .orbitBouncyClick { onChangeFact((safeIdx + 1) % facts.size) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, tint = tokens.onCleanBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isRu) "Следующий" else "Next Fact", style = MaterialTheme.typography.labelLarge, color = tokens.onCleanBackground)
                        }
                    }
                    Button(
                        onClick = { onExplore(cosmosWikipediaUrl(safeIdx, isRu)) },
                        shape = orbitContainerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B5CF6),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                    ) {
                        Text(if (isRu) "Исследовать →" else "Explore →", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MegaDockTile(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OrbitGlassSurface(
        shape = RoundedCornerShape(14.dp),
        elevated = true,
        modifier = modifier
            .height(52.dp)
            .orbitBouncyClick(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = accent, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun cosmosWikipediaUrl(index: Int, isRu: Boolean): String {
    val host = if (isRu) "https://ru.wikipedia.org/wiki/" else "https://en.wikipedia.org/wiki/"
    val topic = when (index) {
        0 -> "JADES-GS-z14-0"
        1 -> if (isRu) "Большое_красное_пятно" else "Great_Red_Spot"
        2 -> if (isRu) "Кольца_Сатурна" else "Rings_of_Saturn"
        3 -> "PSR_J1748-2446ad"
        else -> if (isRu) "Галактика_Андромеды" else "Andromeda_Galaxy"
    }
    return host + Uri.encode(topic)
}

