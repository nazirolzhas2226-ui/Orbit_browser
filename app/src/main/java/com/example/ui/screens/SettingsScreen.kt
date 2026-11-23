package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.rounded.Radar
import com.example.model.AppLanguage
import com.example.model.BrowserStyleMode
import com.example.model.ColorPalettePreset
import com.example.model.ExpressiveShapeStyle
import com.example.model.GlassTransparencyLevel
import com.example.model.HomeLayoutStyle
import com.example.model.MercuryAiModel
import com.example.model.OrbitAnimationStyle
import com.example.model.OrbitCornerStyle
import com.example.model.OrbitDockStyle
import com.example.model.OrbitIconTheme
import com.example.model.OrbitSettings
import com.example.model.RedAiStyle
import com.example.model.SearchEngine
import com.example.model.SettingsCategory
import com.example.model.ThemeBrightness
import com.example.model.WallpaperStyle
import com.example.ui.components.HomeLayoutMockupsSection
import com.example.ui.components.IconTheme4x4Showcase
import com.example.ui.components.InterfaceModeCardsGrid
import com.example.ui.components.JupiterGxLogoEmblem
import com.example.ui.components.MercuryAiLogoEmblem
import com.example.ui.components.MonarchG2GlassLabDialog
import com.example.ui.components.OrbitNativeLiquidGlassWallpaper
import com.example.ui.components.OrbitGlassSurface
import com.example.ui.components.QuickStyleBundlesCarousel
import com.example.ui.components.ThemeColorLinesList
import com.example.ui.components.ThemeStudioSection
import com.example.ui.components.WallpaperPreviewGallery
import com.example.ui.components.orbitBouncyClick
import com.example.ui.components.orbitContainerShape
import com.example.ui.theme.LocalOrbitTokens
import com.example.ui.theme.OrbitStrings

private data class SettingsCategoryItem(
    val category: SettingsCategory,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tint: Color,
    val keywords: String
)

@Composable
fun SettingsScreen(
    settings: OrbitSettings,
    strings: OrbitStrings,
    onBack: () -> Unit,
    onUpdateSetting: (String, Any) -> Unit,
    onOpenNAccount: () -> Unit,
    onOpenSecurity: () -> Unit,
    onOpenGaming: () -> Unit,
    onOpenPrivateDashboard: () -> Unit = onOpenSecurity,
    onUpdateSettingsBatch: (Map<String, Any>) -> Unit = { map ->
        map.forEach { (k, v) -> onUpdateSetting(k, v) }
    },
    onReplaySplash: (BrowserStyleMode, ColorPalettePreset, OrbitIconTheme) -> Unit = { _, _, _ -> }
) {
    val tokens = LocalOrbitTokens.current
    val isRu = settings.resolvedLanguage == AppLanguage.RU
    var selectedCategory by remember { mutableStateOf<SettingsCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showGlassLabDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (selectedCategory != null) selectedCategory = null else onBack()
    }

    val categories = listOf(
        SettingsCategoryItem(
            SettingsCategory.ACCOUNT,
            strings.accountTitle,
            strings.accountSubtitle,
            Icons.Rounded.Person,
            Color(0xFF00D4FF),
            "naccount google vk proton telegram sync облако аккаунт"
        ),
        SettingsCategoryItem(
            SettingsCategory.APPEARANCE,
            strings.appearanceTitle,
            strings.appearanceSubtitle,
            Icons.Rounded.Palette,
            Color(0xFF9C6BFF),
            "liquid glass material 3 expressive тема светлая тёмная фон цвет расцветка лейаут"
        ),
        SettingsCategoryItem(
            SettingsCategory.SECURITY,
            strings.securityTitle,
            strings.securitySubtitle,
            Icons.Rounded.Shield,
            Color(0xFF10E89A),
            "adblock vpn kaspersky dr.web трекеры https безопасность"
        ),
        SettingsCategoryItem(
            SettingsCategory.GAMING_PERFORMANCE,
            strings.gamingPerformanceTitle,
            strings.gamingPerformanceSubtitle,
            Icons.Rounded.SportsEsports,
            Color(0xFFFF1E56),
            "jupitergx gaming rgb озу ram производительность вкладки"
        ),
        SettingsCategoryItem(
            SettingsCategory.RED_AI,
            strings.redAiTitle,
            strings.redAiSubtitle,
            Icons.Rounded.AutoAwesome,
            Color(0xFFFF3E66),
            "mercuryai gemini ии сводка ссылки"
        ),
        SettingsCategoryItem(
            SettingsCategory.SEARCH_ENGINE,
            strings.searchEngineTitle,
            settings.resolvedSearchEngine.displayName,
            Icons.Rounded.Search,
            Color(0xFF3B82F6),
            "поиск google duckduckgo bing yandex startpage"
        ),
        SettingsCategoryItem(
            SettingsCategory.LANGUAGES,
            strings.languagesTitle,
            settings.resolvedLanguage.label,
            Icons.Rounded.Language,
            Color(0xFFF59E0B),
            "язык русский english language"
        ),
        SettingsCategoryItem(
            SettingsCategory.NOTIFICATIONS,
            strings.notificationsTitle,
            strings.notificationsSubtitle,
            Icons.Rounded.Notifications,
            Color(0xFFEC4899),
            "уведомления notifications alerts трекеры облако"
        )
    )

    val filteredCategories = if (searchQuery.isBlank()) {
        categories
    } else {
        val q = searchQuery.trim().lowercase()
        categories.filter {
            it.title.lowercase().contains(q) ||
                it.subtitle.lowercase().contains(q) ||
                it.keywords.lowercase().contains(q)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .widthIn(max = 680.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (selectedCategory != null) selectedCategory = null else onBack() },
                modifier = Modifier.testTag("settings_back_button")
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = tokens.onCleanBackground)
            }
            Spacer(modifier = Modifier.width(6.dp))
            if (selectedCategory == SettingsCategory.RED_AI) {
                MercuryAiLogoEmblem(size = 26.dp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = when (selectedCategory) {
                    null -> strings.settingsTitle
                    SettingsCategory.APPEARANCE -> strings.appearanceTitle
                    SettingsCategory.RED_AI -> strings.redAiTitle
                    SettingsCategory.SEARCH_ENGINE -> strings.searchEngineTitle
                    SettingsCategory.LANGUAGES -> strings.languagesTitle
                    SettingsCategory.NOTIFICATIONS -> strings.notificationsTitle
                    else -> strings.settingsTitle
                },
                style = MaterialTheme.typography.titleLarge,
                color = tokens.onCleanBackground
            )
        }

        AnimatedContent(
            targetState = selectedCategory,
            transitionSpec = {
                if (targetState != null) {
                    (slideInHorizontally { it / 3 } + fadeIn()) togetherWith (slideOutHorizontally { -it / 4 } + fadeOut())
                } else {
                    (slideInHorizontally { -it / 3 } + fadeIn()) togetherWith (slideOutHorizontally { it / 4 } + fadeOut())
                }
            },
            label = "settingsCategoryTransition",
            modifier = Modifier.weight(1f)
        ) { category ->
            when (category) {
                null -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                                placeholder = { Text(strings.searchSettingsHint) },
                                singleLine = true,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                                    .testTag("settings_search_input")
                            )
                        }

                        items(filteredCategories, key = { it.category.name }) { item ->
                            OrbitGlassSurface(
                                shape = orbitContainerShape(20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .orbitBouncyClick {
                                        when (item.category) {
                                            SettingsCategory.ACCOUNT -> onOpenNAccount()
                                            SettingsCategory.SECURITY -> onOpenSecurity()
                                            SettingsCategory.GAMING_PERFORMANCE -> onOpenGaming()
                                            else -> selectedCategory = item.category
                                        }
                                    }
                                    .testTag("settings_cat_${item.category.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(item.tint.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(item.icon, contentDescription = item.title, tint = item.tint, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                                        Text(item.subtitle, style = MaterialTheme.typography.bodyMedium, color = tokens.secondaryText)
                                    }
                                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = tokens.secondaryText)
                                }
                            }
                        }
                    }
                }

                SettingsCategory.APPEARANCE -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                        item {
                            SettingGroupCard(title = if (isRu) "Режим интерфейса (с лого и применением)" else "UI Mode") {
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
                                        .testTag("settings_replay_mode_splash_button")
                                ) {
                                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isRu) "Запустить заставку режима" else "Preview Mode Splash"
                                    )
                                }
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Светлая / Тёмная тема" else "Light / Dark Theme") {
                                ThemeBrightness.entries.forEach { br ->
                                    RadioOptionRow(
                                        label = if (isRu) br.labelRu else br.labelEn,
                                        selected = settings.resolvedBrightness == br,
                                        onClick = { onUpdateSetting("themeBrightness", br.name) }
                                    )
                                }
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Цветовые линии тем" else "Color Theme Lines") {
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
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Студио тем и формы Material 3" else "Theme Studio & M3 Shapes") {
                                ThemeStudioSection(
                                    settings = settings,
                                    isRu = isRu,
                                    onUpdateSetting = onUpdateSetting
                                )
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Иконки, Themed Icons и макет 4×4" else "Icon Pack, Themed Icons & 4×4") {
                                SettingsSwitchRow(
                                    title = if (isRu) "Адаптация цветов под иконку (Themed Icons)" else "Themed Icons App Adaptation",
                                    checked = settings.themedIconsAdaptApp,
                                    onCheckedChange = { onUpdateSetting("themedIconsAdaptApp", it) }
                                )
                                Spacer(modifier = Modifier.height(6.dp))
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
                        }
                        item {
                            SettingGroupCard(
                                title = if (isRu) "WebGL2 · Оптический движок Monarch G2" else "WebGL2 · Monarch G2 Optics Engine"
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    SettingsSwitchRow(
                                        title = if (isRu) "Реалистичное жидкое стекло (WebGL2)" else "Realistic Liquid Glass (WebGL2)",
                                        checked = settings.monarchG2EngineEnabled,
                                        onCheckedChange = { onUpdateSetting("monarchG2EngineEnabled", it) }
                                    )
                                    Text(
                                        text = if (isRu) {
                                            "Аппаратный шейдер GLSL ES 3.00: физическое преломление по закону Снеллиуса n₁·sinθ₁ = n₂·sinθ₂, хроматическая дисперсия света (RGB-призма) и волны ряби от касаний."
                                        } else {
                                            "Hardware GLSL ES 3.00 shader: Snell's Law optical refraction, chromatic dispersion RGB prism split and dynamic fluid ripples."
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.secondaryText
                                    )

                                    if (settings.monarchG2EngineEnabled) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(130.dp)
                                                .clip(orbitContainerShape(16.dp, BrowserStyleMode.LIQUID_GLASS))
                                                .border(1.dp, tokens.glassBorder, orbitContainerShape(16.dp, BrowserStyleMode.LIQUID_GLASS))
                                        ) {
                                            OrbitNativeLiquidGlassWallpaper(
                                                palette = settings.resolvedPalette,
                                                isDark = tokens.isDark,
                                                alpha = 0.75f
                                            )
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.Black.copy(alpha = 0.50f),
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(8.dp)
                                            ) {
                                                Text(
                                                    text = "Оптика MonarchG2",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = { showGlassLabDialog = true },
                                        shape = orbitContainerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = tokens.accentPrimary.copy(alpha = 0.16f),
                                            contentColor = tokens.accentPrimary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("open_monarch_glass_lab_button")
                                    ) {
                                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isRu) "Лаборатория оптики WebGL2 MonarchG2" else "Open WebGL2 Optics Lab",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Превью всех обоев" else "Wallpaper Previews") {
                                WallpaperPreviewGallery(
                                    selectedWallpaper = settings.resolvedWallpaper,
                                    styleMode = settings.resolvedStyleMode,
                                    iconTheme = settings.resolvedIconTheme,
                                    starfieldEnabled = settings.starfieldParticlesEnabled,
                                    isRu = isRu,
                                    onSelectWallpaper = { wp -> onUpdateSetting("wallpaperStyle", wp.name) }
                                )
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Макеты главного экрана" else "Home Screen Mockups") {
                                HomeLayoutMockupsSection(
                                    selectedLayout = settings.resolvedLayout,
                                    styleMode = settings.resolvedStyleMode,
                                    iconTheme = settings.resolvedIconTheme,
                                    isRu = isRu,
                                    onSelectLayout = { layout -> onUpdateSetting("homeLayout", layout.name) }
                                )
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Стиль дока и меню Orbit" else "Orbit Dock & Menu Style") {
                                OrbitDockStyle.entries.forEach { dock ->
                                    RadioOptionRow(
                                        label = "${if (isRu) dock.labelRu else dock.labelEn} (${if (isRu) dock.descriptionRu else dock.name})",
                                        selected = settings.resolvedDockStyle == dock,
                                        onClick = { onUpdateSetting("dockStyle", dock.name) }
                                    )
                                }
                            }
                        }
                        if (settings.resolvedStyleMode == BrowserStyleMode.LIQUID_GLASS) {
                            item {
                                SettingGroupCard(title = if (isRu) "Прозрачность Liquid Glass" else "Liquid Glass Transparency") {
                                    GlassTransparencyLevel.entries.forEach { level ->
                                        RadioOptionRow(
                                            label = if (isRu) level.labelRu else level.labelEn,
                                            selected = settings.resolvedGlassTransparency == level,
                                            onClick = { onUpdateSetting("glassTransparency", level.name) }
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Физика и скорость анимаций" else "Animation Physics") {
                                OrbitAnimationStyle.entries.forEach { anim ->
                                    RadioOptionRow(
                                        label = if (isRu) anim.labelRu else anim.labelEn,
                                        selected = settings.resolvedAnimationStyle == anim,
                                        onClick = { onUpdateSetting("animationStyle", anim.name) }
                                    )
                                }
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Скругление углов" else "Corner Roundness") {
                                OrbitCornerStyle.entries.forEach { corner ->
                                    RadioOptionRow(
                                        label = if (isRu) corner.labelRu else corner.labelEn,
                                        selected = settings.resolvedCornerStyle == corner,
                                        onClick = { onUpdateSetting("cornerStyle", corner.name) }
                                    )
                                }
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Эффекты фона и интерфейса" else "Backdrop, Sound & HUD") {
                                SettingsSwitchRow(
                                    title = if (isRu) "Звёздные частицы фона" else "Ambient Starfield Particles",
                                    checked = settings.starfieldParticlesEnabled,
                                    onCheckedChange = { onUpdateSetting("starfieldParticlesEnabled", it) }
                                )
                                SettingsSwitchRow(
                                    title = if (isRu) "Звуковые эффекты UI и браузера" else "UI & Browser Sound Effects",
                                    checked = settings.soundEffectsEnabled,
                                    onCheckedChange = { onUpdateSetting("soundEffectsEnabled", it) }
                                )
                                SettingsSwitchRow(
                                    title = if (isRu) "Плавающий HUD скорости и ОЗУ" else "Floating Speed & RAM HUD",
                                    checked = settings.floatingSpeedHudEnabled,
                                    onCheckedChange = { onUpdateSetting("floatingSpeedHudEnabled", it) }
                                )
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Заставка Orbit" else "Orbit Splash Intro") {
                                Button(
                                    onClick = { onReplaySplash(settings.resolvedStyleMode, settings.resolvedPalette, settings.resolvedIconTheme) },
                                    shape = orbitContainerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = tokens.accentPrimary.copy(alpha = 0.18f),
                                        contentColor = tokens.accentPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isRu) "Посмотреть заставку" else "Preview Splash Animation"
                                    )
                                }
                            }
                        }
                    }
                }

                SettingsCategory.RED_AI -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            OrbitGlassSurface(
                                shape = orbitContainerShape(20.dp),
                                elevated = true,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFFFF3E66).copy(alpha = 0.16f),
                                        modifier = Modifier.size(50.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            MercuryAiLogoEmblem(size = 34.dp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "MercuryAI · ${settings.resolvedMercuryModel.labelEn}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = tokens.onCleanBackground
                                        )
                                        Text(
                                            text = if (isRu) "Умный ассистент, поиск и анализ безопасности"
                                            else "Smart assistant, AI search & link defense",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = tokens.secondaryText
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Модель MercuryAI (Gemini)" else "MercuryAI Model (Gemini)") {
                                MercuryAiModel.entries.forEach { modelOption ->
                                    RadioOptionRow(
                                        label = if (isRu) {
                                            "${modelOption.labelRu} (${modelOption.badgeRu})"
                                        } else {
                                            "${modelOption.labelEn} (${modelOption.badgeEn})"
                                        },
                                        selected = settings.resolvedMercuryModel == modelOption,
                                        onClick = { onUpdateSetting("mercuryAiModel", modelOption.modelId) }
                                    )
                                }
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Функции MercuryAI" else "MercuryAI Features") {
                                SettingsSwitchRow(
                                    title = if (isRu) "Автопроверка ссылок через MercuryAI" else "MercuryAI Safe Link Scan",
                                    checked = settings.redAiSafeLinkScan,
                                    onCheckedChange = { onUpdateSetting("redAiSafeLinkScan", it) }
                                )
                                SettingsSwitchRow(
                                    title = if (isRu) "Показывать карточку MercuryAI на главной" else "Show MercuryAI Card on Home",
                                    checked = settings.showRedAiBar,
                                    onCheckedChange = { onUpdateSetting("showRedAiBar", it) }
                                )
                            }
                        }
                        item {
                            SettingGroupCard(title = if (isRu) "Стиль ответов MercuryAI" else "MercuryAI Response Style") {
                                RedAiStyle.entries.forEach { style ->
                                    RadioOptionRow(
                                        label = if (isRu) style.labelRu else style.labelEn,
                                        selected = settings.resolvedRedAiStyle == style,
                                        onClick = { onUpdateSetting("redAiStyle", style.name) }
                                    )
                                }
                            }
                        }
                    }
                }

                SettingsCategory.SEARCH_ENGINE -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        item {
                            SettingGroupCard(title = strings.searchEngineTitle) {
                                SearchEngine.entries.forEach { engine ->
                                    RadioOptionRow(
                                        label = engine.displayName,
                                        selected = settings.resolvedSearchEngine == engine,
                                        onClick = { onUpdateSetting("searchEngine", engine.name) }
                                    )
                                }
                            }
                        }
                    }
                }

                SettingsCategory.LANGUAGES -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        item {
                            SettingGroupCard(title = strings.languagesTitle) {
                                AppLanguage.entries.forEach { lang ->
                                    RadioOptionRow(
                                        label = lang.label,
                                        selected = settings.resolvedLanguage == lang,
                                        onClick = { onUpdateSetting("appLanguage", lang.name) }
                                    )
                                }
                            }
                        }
                    }
                }

                SettingsCategory.NOTIFICATIONS -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        item {
                            SettingGroupCard(title = strings.notificationsTitle) {
                                SettingsSwitchRow(
                                    title = if (isRu) "Уведомления о блокировке трекеров" else "Tracker Block Alerts",
                                    checked = settings.notifyBlockedTrackers,
                                    onCheckedChange = { onUpdateSetting("notifyBlockedTrackers", it) }
                                )
                                SettingsSwitchRow(
                                    title = if (isRu) "Статус облачной синхронизации" else "Cloud Sync Toasts",
                                    checked = settings.notifyCloudSync,
                                    onCheckedChange = { onUpdateSetting("notifyCloudSync", it) }
                                )
                                SettingsSwitchRow(
                                    title = if (isRu) "Предупреждения безопасности и антивируса" else "Security & Antivirus Warnings",
                                    checked = settings.notifySecurityAlerts,
                                    onCheckedChange = { onUpdateSetting("notifySecurityAlerts", it) }
                                )
                            }
                        }
                    }
                }
                else -> {}
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
private fun SettingGroupCard(
    title: String,
    content: @Composable () -> Unit
) {
    OrbitGlassSurface(
        shape = orbitContainerShape(20.dp),
        elevated = true,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = LocalOrbitTokens.current.accentPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            content()
        }
    }
}

@Composable
private fun RadioOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .orbitBouncyClick(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SettingsSwitchRow(
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
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
