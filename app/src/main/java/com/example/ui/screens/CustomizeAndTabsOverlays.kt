package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FindInPage
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.VpnKey
import com.example.model.OrbitDockStyle
import com.example.ui.components.OrbitLogoEmblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.BrowserStyleMode
import com.example.model.BrowserTab
import com.example.model.ColorPalettePreset
import com.example.model.DownloadItem
import com.example.model.GlassTransparencyLevel
import com.example.model.HomeLayoutStyle
import com.example.model.OrbitAnimationStyle
import com.example.model.OrbitCornerStyle
import com.example.model.OrbitIconTheme
import com.example.model.OrbitSettings
import com.example.model.ThemeBrightness
import com.example.model.WallpaperStyle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.ui.components.HomeLayoutMockupsSection
import com.example.ui.components.IconTheme4x4Showcase
import com.example.ui.components.InterfaceModeCardsGrid
import com.example.ui.components.JupiterGxLogoEmblem
import com.example.ui.components.MercuryAiLogoEmblem
import com.example.ui.components.MonarchG2GlassLabDialog
import com.example.ui.components.OrbitUpwardPopup
import com.example.ui.components.OrbitGlassSurface
import com.example.ui.components.QuickStyleBundlesCarousel
import com.example.ui.components.ThemeColorLinesList
import com.example.ui.components.ThemeStudioSection
import com.example.ui.components.WallpaperPreviewGallery
import com.example.ui.components.orbitBouncyClick
import com.example.ui.components.orbitContainerShape
import com.example.ui.theme.LocalOrbitTokens
import com.example.ui.theme.OrbitStrings

@Composable
fun CustomizeHomeSheet(
    visible: Boolean,
    settings: OrbitSettings,
    onDismiss: () -> Unit,
    onUpdateSetting: (String, Any) -> Unit,
    onUpdateSettingsBatch: (Map<String, Any>) -> Unit = { map ->
        map.forEach { (k, v) -> onUpdateSetting(k, v) }
    },
    onReplaySplash: (BrowserStyleMode, ColorPalettePreset, OrbitIconTheme) -> Unit = { _, _, _ -> }
) {
    val tokens = LocalOrbitTokens.current
    val isRu = settings.resolvedLanguage == AppLanguage.RU
    var showGlassLabDialog by remember { mutableStateOf(false) }

    OrbitUpwardPopup(
        visible = visible,
        onDismiss = onDismiss,
        gamingRgbGlow = settings.gamingRgbEnabled
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isRu) "Настроить главную Orbit" else "Customize Orbit Home",
                        style = MaterialTheme.typography.headlineMedium,
                        color = tokens.onCleanBackground
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = tokens.onCleanBackground
                        )
                    }
                }
            }

            // 0. Style Bundles 2.0 (1-Tap)
            item {
                QuickStyleBundlesCarousel(
                    settings = settings,
                    isRu = isRu,
                    onApplyBundle = { bundle ->
                        onUpdateSettingsBatch(
                            mapOf(
                                "styleMode" to bundle.styleMode.name,
                                "colorPalette" to bundle.palette.name,
                                "wallpaperStyle" to bundle.wallpaper.name,
                                "iconTheme" to bundle.iconTheme.name,
                                "customAccentHex" to 0L,
                                "customSecondaryHex" to 0L
                            )
                        )
                    }
                )
            }

            // 1. UI Style Modes with top logo, bottom name+usage, and instant splash preview
            item {
                Text(
                    text = if (isRu) "Режим интерфейса" else "Interface Mode",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                InterfaceModeCardsGrid(
                    selectedMode = settings.resolvedStyleMode,
                    iconTheme = settings.resolvedIconTheme,
                    isRu = isRu,
                    onSelectMode = { mode ->
                        onUpdateSettingsBatch(
                            mapOf(
                                "styleMode" to mode.name,
                                "colorPalette" to mode.officialPalette().name,
                                "wallpaperStyle" to mode.officialWallpaper().name,
                                "iconTheme" to OrbitIconTheme.AUTO_THEME.name,
                                "customAccentHex" to 0L,
                                "customSecondaryHex" to 0L
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onReplaySplash(settings.resolvedStyleMode, settings.resolvedPalette, settings.resolvedIconTheme) },
                    shape = orbitContainerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentPrimary.copy(alpha = 0.18f),
                        contentColor = tokens.accentPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customize_replay_mode_splash_button")
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRu) "Запустить заставку режима" else "Preview Mode Splash"
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = { showGlassLabDialog = true },
                    shape = orbitContainerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentPrimary.copy(alpha = 0.16f),
                        contentColor = tokens.accentPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customize_open_monarch_glass_lab_button")
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRu) "WebGL2 · Жидкое стекло (MonarchG2)" else "WebGL2 · Liquid Glass (MonarchG2)"
                    )
                }
            }

            // 2. Theme Brightness (Light / Dark / System)
            item {
                Text(
                    text = if (isRu) "Тема оформления" else "Theme Brightness",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeBrightness.entries.forEach { b ->
                        FilterChip(
                            selected = settings.resolvedBrightness == b,
                            onClick = { onUpdateSetting("themeBrightness", b.name) },
                            label = { Text(if (isRu) b.labelRu else b.labelEn) }
                        )
                    }
                }
            }

            // 3. Color Palettes as Theme Lines (immediately switches app palette and clears icon/Studio override)
            item {
                Text(
                    text = if (isRu) "Цветовая расцветка тем" else "Color Theme Lines",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                ThemeColorLinesList(
                    selectedPalette = settings.resolvedPalette,
                    iconTheme = settings.resolvedIconTheme,
                    isRu = isRu,
                    onSelectPalette = { preset ->
                        onUpdateSettingsBatch(
                            mapOf(
                                "colorPalette" to preset.name,
                                "iconTheme" to OrbitIconTheme.AUTO_THEME.name,
                                "customAccentHex" to 0L,
                                "customSecondaryHex" to 0L
                            )
                        )
                    }
                )
            }

            // Theme Studio ("Студио тем"): Custom Accents, M3 Expressive Shapes, Corner & Glow sliders
            item {
                Text(
                    text = if (isRu) "Студио тем и живые формы" else "Theme Studio & Expressive Shapes",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                ThemeStudioSection(
                    settings = settings,
                    isRu = isRu,
                    onUpdateSetting = onUpdateSetting
                )
            }

            // Monarch G2 Realistic Optical Engine Toggle (only in Liquid Glass mode)
            if (settings.resolvedStyleMode == BrowserStyleMode.LIQUID_GLASS) {
                item {
                    OrbitGlassSurface(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isRu) "Оптический движок Monarch G2" else "Monarch G2 Optical Engine",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = tokens.onCleanBackground
                                )
                                Text(
                                    text = if (isRu) "Реалистичное стекло, преломление света и двойные блики"
                                    else "Realistic optical glass, light refraction & dual-bevel sheen",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.secondaryText
                                )
                            }
                            Switch(
                                checked = settings.monarchG2EngineEnabled,
                                onCheckedChange = { onUpdateSetting("monarchG2EngineEnabled", it) }
                            )
                        }
                    }
                }
            }

            // 1.5 Icon Themes + 4x4 Icon Showcase Grid with Labels
            item {
                Text(
                    text = if (isRu) "Иконки и темы логотипа (4×4)" else "Icon Themes (4×4 Preview)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconTheme4x4Showcase(
                    selectedIconTheme = settings.resolvedIconTheme,
                    styleMode = settings.resolvedStyleMode,
                    isRu = isRu,
                    onSelectIconTheme = { iconTheme -> onUpdateSetting("iconTheme", iconTheme.name) },
                    customIconUri = settings.customIconUri,
                    customAccentHex = settings.customAccentHex,
                    customSecondaryHex = settings.customSecondaryHex,
                    onUpdateCustomIconUri = { uri -> onUpdateSetting("customIconUri", uri) }
                )
            }

            // 4. Home Layout Presets with Live Transparent / Shape Mockups
            item {
                Text(
                    text = if (isRu) "Макеты главного экрана" else "Home Screen Mockups",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                HomeLayoutMockupsSection(
                    selectedLayout = settings.resolvedLayout,
                    styleMode = settings.resolvedStyleMode,
                    iconTheme = settings.resolvedIconTheme,
                    isRu = isRu,
                    onSelectLayout = { layout -> onUpdateSetting("homeLayout", layout.name) }
                )
            }

            // Dock & Menu Style selector as requested ("выбор меню как 1:Safari 2:Очень больше 3:Минималистика 4:Kosmos")
            item {
                Text(
                    text = if (isRu) "Стиль дока и меню" else "Dock & Menu Style",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(OrbitDockStyle.entries) { dock ->
                        FilterChip(
                            selected = settings.resolvedDockStyle == dock,
                            onClick = { onUpdateSetting("dockStyle", dock.name) },
                            label = { Text(if (isRu) dock.labelRu else dock.labelEn) }
                        )
                    }
                }
            }

            // 5. Wallpapers Gallery with Live Preview & Apply
            item {
                Text(
                    text = if (isRu) "Превью и выбор обоев" else "Wallpaper Previews",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                WallpaperPreviewGallery(
                    selectedWallpaper = settings.resolvedWallpaper,
                    styleMode = settings.resolvedStyleMode,
                    iconTheme = settings.resolvedIconTheme,
                    starfieldEnabled = settings.starfieldParticlesEnabled,
                    isRu = isRu,
                    onSelectWallpaper = { wp -> onUpdateSetting("wallpaperStyle", wp.name) }
                )
            }

            // 5.5 Glass Transparency (disabled when in M3 Expressive mode), Animation Physics & Corner Style
            if (settings.resolvedStyleMode != BrowserStyleMode.M3_EXPRESSIVE && settings.resolvedStyleMode != BrowserStyleMode.MINIMALIST) {
                item {
                    Text(
                        text = if (isRu) "Прозрачность Liquid Glass" else "Liquid Glass Transparency",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(GlassTransparencyLevel.entries) { level ->
                            FilterChip(
                                selected = settings.resolvedGlassTransparency == level,
                                onClick = { onUpdateSetting("glassTransparency", level.name) },
                                label = { Text(if (isRu) level.labelRu else level.labelEn) }
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = if (isRu) "Физика и скорость анимаций" else "Animation Physics",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(OrbitAnimationStyle.entries) { anim ->
                        FilterChip(
                            selected = settings.resolvedAnimationStyle == anim,
                            onClick = { onUpdateSetting("animationStyle", anim.name) },
                            label = { Text(if (isRu) anim.labelRu else anim.labelEn) }
                        )
                    }
                }
            }

            item {
                Text(
                    text = if (isRu) "Скругление углов интерфейса" else "Corner Roundness",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OrbitCornerStyle.entries.forEach { corner ->
                        FilterChip(
                            selected = settings.resolvedCornerStyle == corner,
                            onClick = { onUpdateSetting("cornerStyle", corner.name) },
                            label = { Text(if (isRu) corner.labelRu else corner.labelEn) }
                        )
                    }
                }
            }

            // 6. Widget Visibility Toggles & Starfield
            item {
                Text(
                    text = if (isRu) "Приборы и виджеты" else "Home Widgets",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                WidgetToggleRow(
                    title = if (isRu) "Звёздные частицы фона" else "Ambient Starfield Particles",
                    checked = settings.starfieldParticlesEnabled,
                    onCheckedChange = { onUpdateSetting("starfieldParticlesEnabled", it) }
                )
                WidgetToggleRow(
                    title = if (isRu) "Избранные сайты" else "Favorites Grid",
                    checked = settings.showFavorites,
                    onCheckedChange = { onUpdateSetting("showFavorites", it) }
                )
                WidgetToggleRow(
                    title = if (isRu) "Отчёт о конфиденциальности" else "Privacy Report Card",
                    checked = settings.showPrivacyReport,
                    onCheckedChange = { onUpdateSetting("showPrivacyReport", it) }
                )
                WidgetToggleRow(
                    title = if (isRu) "Виджет MercuryAI" else "MercuryAI Quick Card",
                    checked = settings.showRedAiBar,
                    onCheckedChange = { onUpdateSetting("showRedAiBar", it) }
                )
                WidgetToggleRow(
                    title = if (isRu) "Виджет Космос & Астрономия (James Webb)" else "Cosmos & Astronomy Widget",
                    checked = settings.showCosmosWidget,
                    onCheckedChange = { onUpdateSetting("showCosmosWidget", it) }
                )
                WidgetToggleRow(
                    title = if (isRu) "Быстрые заметки (Scratchpad)" else "Quick Notes Scratchpad",
                    checked = settings.showQuickNotesWidget,
                    onCheckedChange = { onUpdateSetting("showQuickNotesWidget", it) }
                )
                WidgetToggleRow(
                    title = if (isRu) "Телеметрия Gaming / ОЗУ" else "Gaming & RAM Telemetry",
                    checked = settings.showGamingTelemetry,
                    onCheckedChange = { onUpdateSetting("showGamingTelemetry", it) }
                )
                WidgetToggleRow(
                    title = if (isRu) "Звуковые эффекты UI" else "UI Sound Effects",
                    checked = settings.soundEffectsEnabled,
                    onCheckedChange = { onUpdateSetting("soundEffectsEnabled", it) }
                )
                WidgetToggleRow(
                    title = if (isRu) "Плавающий HUD скорости" else "Floating Speed HUD",
                    checked = settings.floatingSpeedHudEnabled,
                    onCheckedChange = { onUpdateSetting("floatingSpeedHudEnabled", it) }
                )
                WidgetToggleRow(
                    title = if (isRu) "Адаптация цветов (Themed Icons)" else "Themed Icons Adaptation",
                    checked = settings.themedIconsAdaptApp,
                    onCheckedChange = { onUpdateSetting("themedIconsAdaptApp", it) }
                )
            }

            // 7. Replay Intro Animation (for current mode, theme, and any Orbit logo)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onDismiss()
                            onReplaySplash(
                                settings.resolvedStyleMode,
                                settings.resolvedPalette,
                                settings.resolvedIconTheme
                            )
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentPrimary.copy(alpha = 0.16f),
                            contentColor = tokens.accentPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRu) {
                                "Повторить заставку (${settings.resolvedIconTheme.labelRu})"
                            } else {
                                "Replay Splash (${settings.resolvedIconTheme.labelEn})"
                            },
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(BrowserStyleMode.entries) { mode ->
                            FilterChip(
                                selected = settings.resolvedStyleMode == mode && settings.resolvedIconTheme == OrbitIconTheme.AUTO_THEME,
                                onClick = {
                                    onDismiss()
                                    onReplaySplash(mode, settings.resolvedPalette, OrbitIconTheme.AUTO_THEME)
                                },
                                label = {
                                    Text(
                                        text = if (isRu) "▶ ${mode.labelRu}" else "▶ ${mode.labelEn}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                        items(OrbitIconTheme.entries.filter { it != OrbitIconTheme.AUTO_THEME }) { logoTheme ->
                            FilterChip(
                                selected = settings.resolvedIconTheme == logoTheme,
                                onClick = {
                                    onDismiss()
                                    onReplaySplash(settings.resolvedStyleMode, settings.resolvedPalette, logoTheme)
                                },
                                label = {
                                    Text(
                                        text = if (isRu) "▶ ${logoTheme.labelRu}" else "▶ ${logoTheme.labelEn}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    MonarchG2GlassLabDialog(
        visible = showGlassLabDialog,
        isRu = isRu,
        onDismiss = { showGlassLabDialog = false }
    )
}

@Composable
private fun WidgetToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun OrbitMenuSheet(
    visible: Boolean,
    settings: OrbitSettings,
    strings: OrbitStrings,
    canToggleDesktop: Boolean,
    desktopEnabled: Boolean,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSecurity: () -> Unit,
    onOpenGaming: () -> Unit,
    onOpenRedAi: () -> Unit,
    onOpenNAccount: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenPrivateDashboard: () -> Unit,
    onToggleDesktop: () -> Unit,
    onToggleTheme: () -> Unit = {},
    onToggleAdBlock: () -> Unit = {},
    onToggleStealth: () -> Unit = {},
    onToggleReaderMode: (() -> Unit)? = null,
    readerModeActive: Boolean = false,
    onSharePage: () -> Unit = {},
    downloads: List<DownloadItem> = emptyList(),
    onClearDownloads: () -> Unit = {}
) {
    val tokens = LocalOrbitTokens.current
    val isRu = settings.resolvedLanguage == AppLanguage.RU
    var showDownloadsModal by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        OrbitUpwardPopup(
            visible = visible,
            onDismiss = onDismiss,
            gamingRgbGlow = settings.gamingRgbEnabled
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // Header with Logo, Title, and Orbit Shield indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OrbitLogoEmblem(size = 32.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Orbit Command Hub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isRu) "Молниеносный защищённый сёрфинг" else "Lightning-fast stealth browsing",
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.secondaryText
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close Menu")
                }
            }

            // Quick Toggle Strip: [Тема (Свет/Тень)] [Щит рекламы] [Скрытность] [Версия для ПК]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickTogglePill(
                    icon = if (tokens.isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                    label = if (tokens.isDark) (if (isRu) "Свет" else "Light") else (if (isRu) "Тьма" else "Dark"),
                    active = false,
                    modifier = Modifier.weight(1f),
                    onClick = onToggleTheme
                )
                QuickTogglePill(
                    icon = Icons.Rounded.Shield,
                    label = if (isRu) "Антиреклама" else "AdBlock",
                    active = settings.adBlockEnabled,
                    modifier = Modifier.weight(1f),
                    onClick = onToggleAdBlock
                )
                QuickTogglePill(
                    icon = Icons.Rounded.VisibilityOff,
                    label = if (isRu) "Скрытность" else "Stealth",
                    active = settings.antiTrackingEnabled,
                    modifier = Modifier.weight(1f),
                    onClick = onToggleStealth
                )
                if (canToggleDesktop) {
                    QuickTogglePill(
                        icon = Icons.Rounded.DesktopWindows,
                        label = if (isRu) "ПК-версия" else "Desktop",
                        active = desktopEnabled,
                        modifier = Modifier.weight(1f),
                        onClick = onToggleDesktop
                    )
                }
            }

            // 3x3 Illustrated Hub Grid
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: Приватный дэшборд, RedAI, Gaming GX
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MenuTile(
                        icon = Icons.Rounded.Radar,
                        label = if (isRu) "Приватный дэшборд" else "Private Dashboard",
                        subtitle = if (isRu) "Радар и шредер" else "Radar & Shredder",
                        accent = Color(0xFF00E5FF),
                        badge = if (isRu) "Новинка" else "New",
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss(); onOpenPrivateDashboard() }
                    )
                    MenuTile(
                        icon = Icons.Rounded.AutoAwesome,
                        label = "MercuryAI",
                        subtitle = if (isRu) "ИИ · Gemini 3.8" else "Smart Engine",
                        accent = Color(0xFFFF3E66),
                        badge = "v3.8",
                        customIcon = { MercuryAiLogoEmblem(size = 24.dp) },
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss(); onOpenRedAi() }
                    )
                    MenuTile(
                        icon = Icons.Rounded.SportsEsports,
                        label = "JupiterGX",
                        subtitle = if (isRu) "ОЗУ & RGB" else "RAM Limiter",
                        accent = Color(0xFFFF1E56),
                        customIcon = { JupiterGxLogoEmblem(size = 24.dp) },
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss(); onOpenGaming() }
                    )
                }

                // Row 2: NAccount, Режим чтения, Загрузки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MenuTile(
                        icon = Icons.Rounded.Person,
                        label = "NAccount & Облако",
                        subtitle = if (isRu) "Синхронизация" else "Cloud Sync",
                        accent = Color(0xFF9D4EDD),
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss(); onOpenNAccount() }
                    )
                    MenuTile(
                        icon = Icons.Rounded.MenuBook,
                        label = if (isRu) "Режим чтения" else "Reader Mode",
                        subtitle = if (readerModeActive) (if (isRu) "Включён" else "Active") else (if (isRu) "Без лишнего" else "Clean"),
                        accent = Color(0xFFFFB703),
                        badge = if (readerModeActive) "ON" else null,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onToggleReaderMode?.invoke()
                            onDismiss()
                        }
                    )
                    MenuTile(
                        icon = Icons.Rounded.Download,
                        label = if (isRu) "Загрузки" else "Downloads",
                        subtitle = if (isRu) "Файлы" else "Files",
                        accent = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f),
                        onClick = { showDownloadsModal = true }
                    )
                }

                // Row 3: История, Настройки, Поделиться
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MenuTile(
                        icon = Icons.Rounded.History,
                        label = strings.historyTitle,
                        subtitle = if (isRu) "Журнал посещений" else "Visited links",
                        accent = Color(0xFF10E89A),
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss(); onOpenHistory() }
                    )
                    MenuTile(
                        icon = Icons.Rounded.Settings,
                        label = strings.settingsTitle,
                        subtitle = if (isRu) "Параметры" else "Preferences",
                        accent = tokens.onCleanBackground,
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss(); onOpenSettings() }
                    )
                    MenuTile(
                        icon = Icons.Rounded.Share,
                        label = if (isRu) "Поделиться" else "Share Page",
                        subtitle = if (isRu) "Экспорт ссылки" else "Link or QR",
                        accent = Color(0xFF14B8A6),
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss(); onSharePage() }
                    )
                }
            }

            // Bottom Security Status Strip
            OrbitGlassSurface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10E89A).copy(alpha = 0.2f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFF10E89A),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isRu) "Orbit Shield v2.4 · Защита WebRTC и Canvas активна"
                            else "Orbit Shield v2.4 · WebRTC & Canvas Defense Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.secondaryText
                        )
                    }
                }
            }
        }
        }
        DownloadsManagerDialog(
            visible = showDownloadsModal,
            downloads = downloads,
            isRu = isRu,
            onClearDownloads = onClearDownloads,
            onDismiss = { showDownloadsModal = false }
        )
    }
}

@Composable
private fun QuickTogglePill(
    icon: ImageVector,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val tokens = LocalOrbitTokens.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (active) tokens.accentPrimary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        contentColor = if (active) tokens.accentPrimary else tokens.onCleanBackground,
        modifier = modifier
            .height(44.dp)
            .orbitBouncyClick(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MenuTile(
    icon: ImageVector,
    label: String,
    subtitle: String,
    accent: Color,
    badge: String? = null,
    customIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val tokens = LocalOrbitTokens.current
    OrbitGlassSurface(
        shape = orbitContainerShape(18.dp),
        modifier = modifier
            .height(96.dp)
            .orbitBouncyClick(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accent.copy(alpha = 0.18f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (customIcon != null) {
                            customIcon()
                        } else {
                            Icon(imageVector = icon, contentDescription = label, tint = accent, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.secondaryText,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = accent,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadsManagerDialog(
    visible: Boolean,
    downloads: List<DownloadItem> = emptyList(),
    isRu: Boolean,
    onClearDownloads: () -> Unit = {},
    onDismiss: () -> Unit
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
                    Icon(Icons.Rounded.Download, contentDescription = null, tint = Color(0xFF2196F3))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRu) "Менеджер загрузок Orbit" else "Orbit Download Manager",
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.onCleanBackground
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.onCleanBackground)
                }
            }

            if (downloads.isEmpty()) {
                OrbitGlassSurface(
                    shape = orbitContainerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isRu) "Загрузок пока нет. Скачиваемые в браузере файлы автоматически появятся здесь."
                        else "No downloads yet. Files downloaded while browsing will appear here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.secondaryText,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                downloads.take(8).forEach { dl ->
                    val statusColor = if (dl.isComplete) Color(0xFF10E89A) else tokens.accentPrimary
                    val sizeLabel = when {
                        dl.fileSizeBytes >= 1_048_576L -> String.format("%.1f MB", dl.fileSizeBytes / 1_048_576.0)
                        dl.fileSizeBytes >= 1024L -> "${dl.fileSizeBytes / 1024L} KB"
                        dl.fileSizeBytes > 0L -> "${dl.fileSizeBytes} B"
                        else -> if (isRu) "Файл" else "File"
                    }
                    OrbitGlassSurface(
                        shape = orbitContainerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = statusColor.copy(alpha = 0.16f),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Rounded.Download, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dl.fileName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = tokens.onCleanBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$sizeLabel · ${if (dl.isComplete) (if (isRu) "Завершено" else "Complete") else (if (isRu) "Загрузка" else "Downloading")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.secondaryText
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (downloads.isNotEmpty()) {
                    OrbitGlassSurface(
                        shape = orbitContainerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .orbitBouncyClick(onClick = onClearDownloads)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = if (isRu) "Очистить" else "Clear",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Button(
                    onClick = onDismiss,
                    shape = orbitContainerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentPrimary,
                        contentColor = Color(0xFF061018)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isRu) "Готово" else "Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TabsSwitcherOverlay(
    visible: Boolean,
    tabs: List<BrowserTab>,
    activeTabId: String,
    strings: OrbitStrings,
    isRu: Boolean,
    onDismiss: () -> Unit,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: (incognito: Boolean) -> Unit
) {
    val tokens = LocalOrbitTokens.current
    if (visible) {
        BackHandler(onBack = onDismiss)
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = spring(
                dampingRatio = tokens.animationStyle.dampingRatio,
                stiffness = tokens.animationStyle.stiffness
            )
        ) + fadeIn(tween(220)) + scaleIn(
            initialScale = 0.92f,
            animationSpec = spring(
                dampingRatio = tokens.animationStyle.dampingRatio,
                stiffness = tokens.animationStyle.stiffness
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { it / 2 },
            animationSpec = tween(280, easing = FastOutSlowInEasing)
        ) + scaleOut(
            targetScale = 0.88f,
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(220, delayMillis = 40))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (tokens.isDark) Color(0xCC080D1A)
                    else Color(0xCCF8FAFC)
                )
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${strings.tabsTitle} (${tabs.size})",
                        style = MaterialTheme.typography.headlineMedium,
                        color = tokens.onCleanBackground
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close Tabs View")
                    }
                }

                if (tabs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = tokens.accentPrimary.copy(alpha = 0.2f),
                                modifier = Modifier.size(76.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.HelpOutline,
                                        contentDescription = "Question",
                                        tint = tokens.accentPrimary,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isRu) "Ой! А где вкладка? Нажми в вкладки и нажми плюсик для работы"
                                else "Oops! Where is the tab? Tap tabs and press plus to work",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = tokens.onCleanBackground,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    onNewTab(false)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = tokens.accentPrimary),
                                modifier = Modifier.testTag("empty_tabs_new_tab_button")
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isRu) "Новая вкладка" else "New Tab")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tabs, key = { it.id }) { tab ->
                            val isActive = tab.id == activeTabId
                            OrbitGlassSurface(
                                shape = orbitContainerShape(20.dp),
                                elevated = isActive,
                                gamingRgbGlow = isActive,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .orbitBouncyClick {
                                        onSelectTab(tab.id)
                                        onDismiss()
                                    }
                                    .testTag("tab_card_${tab.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (tab.isIncognito) Color(0xFF334155) else tokens.accentPrimary.copy(alpha = 0.18f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (tab.isIncognito) Icons.Rounded.VisibilityOff else Icons.Rounded.Public,
                                                contentDescription = null,
                                                tint = if (tab.isIncognito) Color.White else tokens.accentPrimary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = tab.title.ifBlank { "Orbit" },
                                                style = MaterialTheme.typography.titleMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            if (tab.isHibernated) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Rounded.Memory,
                                                    contentDescription = "Hibernated",
                                                    tint = Color(0xFF10E89A),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = if (tab.isHibernated) {
                                                (if (isRu) "Спящая (экономия ОЗУ) · " else "Hibernated · ") + tab.url
                                            } else tab.url,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = tokens.secondaryText,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    IconButton(
                                        onClick = { onCloseTab(tab.id) },
                                        modifier = Modifier.testTag("close_tab_${tab.id}")
                                    ) {
                                        Icon(Icons.Rounded.Close, contentDescription = "Close Tab")
                                    }
                                }
                            }
                        }
                    }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onNewTab(false); onDismiss() },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = tokens.accentPrimary, contentColor = Color(0xFF061018)),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("new_tab_button")
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.newTab, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onNewTab(true); onDismiss() },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(Icons.Rounded.VisibilityOff, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.incognitoTab)
                    }
                }
            }
        }
    }
}
}
