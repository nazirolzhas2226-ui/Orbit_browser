package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.OrbitFirestoreRepository
import com.example.data.RedAiRepository
import com.example.firebase.authStateFlow
import com.example.firebase.configureAppCheck
import com.example.firebase.signOut
import com.example.model.AppLanguage
import com.example.model.BrowserStyleMode
import com.example.model.BrowserTab
import com.example.model.ColorPalettePreset
import com.example.model.ExpressiveShapeStyle
import com.example.model.GlassTransparencyLevel
import com.example.model.HomeLayoutStyle
import com.example.model.MercuryAiModel
import com.example.model.OrbitAnimationStyle
import com.example.model.OrbitCornerStyle
import com.example.model.OrbitDockStyle
import com.example.model.OrbitIconTheme
import com.example.model.OrbitLogoScale
import com.example.model.OrbitSettings
import com.example.model.ThemeBrightness
import com.example.model.WallpaperStyle
import com.example.ui.components.OrbitFloatingToastBanner
import com.example.ui.components.OrbitGlassSurface
import com.example.ui.components.OrbitWallpaperLayer
import com.example.ui.components.orbitContainerShape
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BrowserHomeScreen
import com.example.ui.screens.CustomizeHomeSheet
import com.example.ui.screens.GamingPerformanceScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.NAccountScreen
import com.example.ui.screens.OrbitMenuSheet
import com.example.ui.screens.OrbitSplashScreen
import com.example.ui.screens.PrivateDashboardScreen
import com.example.ui.screens.RedAiScreen
import com.example.ui.screens.SecurityPrivacyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TabsSwitcherOverlay
import com.example.ui.screens.WebViewTabScreen
import com.example.ui.theme.LocalOrbitTokens
import com.example.ui.theme.OrbitTheme
import com.example.ui.theme.getOrbitStrings
import com.example.util.OrbitSoundPlayer
import com.example.viewmodel.CloudUiState
import com.example.viewmodel.OrbitViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

private enum class OrbitRoute {
    BROWSER,
    SETTINGS,
    SECURITY,
    GAMING,
    RED_AI,
    NACCOUNT,
    HISTORY,
    PRIVATE_DASHBOARD
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 1: Configure App Check with launch intent before UI, ViewModels, or Firebase operations
        configureAppCheck(this, intent)

        // Step 2: Edge-to-Edge UI
        enableEdgeToEdge()

        setContent {
            OrbitRootNavigation()
        }
    }
}

@Composable
private fun OrbitRootNavigation() {
    val context = LocalContext.current
    val launchPrefs = remember(context) {
        context.getSharedPreferences("orbit_launch_theme", Context.MODE_PRIVATE)
    }
    val initialMode = remember {
        runCatching {
            BrowserStyleMode.valueOf(launchPrefs.getString("styleMode", BrowserStyleMode.LIQUID_GLASS.name) ?: BrowserStyleMode.LIQUID_GLASS.name)
        }.getOrDefault(BrowserStyleMode.LIQUID_GLASS)
    }
    val initialBrightness = remember {
        runCatching {
            ThemeBrightness.valueOf(launchPrefs.getString("brightness", ThemeBrightness.LIGHT.name) ?: ThemeBrightness.LIGHT.name)
        }.getOrDefault(ThemeBrightness.LIGHT)
    }
    val initialPalette = remember {
        runCatching {
            ColorPalettePreset.valueOf(launchPrefs.getString("palette", ColorPalettePreset.ORBIT_CYAN.name) ?: ColorPalettePreset.ORBIT_CYAN.name)
        }.getOrDefault(ColorPalettePreset.ORBIT_CYAN)
    }
    val initialIconTheme = remember {
        runCatching {
            OrbitIconTheme.valueOf(launchPrefs.getString("iconTheme", OrbitIconTheme.AUTO_THEME.name) ?: OrbitIconTheme.AUTO_THEME.name)
        }.getOrDefault(OrbitIconTheme.AUTO_THEME)
    }
    val initialCustomAccent = remember { launchPrefs.getLong("customAccentHex", 0L) }
    val initialCustomSecondary = remember { launchPrefs.getLong("customSecondaryHex", 0L) }
    val initialCornerRadius = remember { launchPrefs.getLong("customCornerRadiusDp", 20L).toInt() }
    val initialBorderGlow = remember { launchPrefs.getLong("borderGlowIntensity", 50L).toInt() }
    val initialGlassBlur = remember { launchPrefs.getLong("glassBlurLevel", 50L).toInt() }
    val initialCustomIconUri = remember { launchPrefs.getString("customIconUri", "") ?: "" }
    val initialThemedAdapt = remember { launchPrefs.getBoolean("themedIconsAdaptApp", true) }
    val initialExpressiveShape = remember {
        ExpressiveShapeStyle.safeValueOf(launchPrefs.getString("expressiveShapeStyle", ExpressiveShapeStyle.AUTO_MIX.name))
    }
    val initialLogoScale = remember {
        OrbitLogoScale.safeValueOf(launchPrefs.getString("homeLogoScale", OrbitLogoScale.MEDIUM.name))
    }
    val initialLanguage = remember {
        AppLanguage.safeValueOf(launchPrefs.getString("appLanguage", AppLanguage.RU.name))
    }
    LaunchedEffect(Unit) {
        OrbitSoundPlayer.isEnabled = launchPrefs.getBoolean("soundEffectsEnabled", true)
    }

    val auth = remember { Firebase.auth }
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser
    var isGuestMode by remember {
        mutableStateOf(launchPrefs.getBoolean("isGuestMode", false))
    }
    var showInitialSplash by rememberSaveable { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (user == null && !isGuestMode) {
            OrbitTheme(
                styleMode = initialMode,
                brightness = initialBrightness,
                palette = initialPalette,
                iconTheme = initialIconTheme,
                themedIconsAdaptApp = initialThemedAdapt,
                expressiveShapeStyle = initialExpressiveShape,
                homeLogoScale = initialLogoScale,
                customAccentHex = initialCustomAccent,
                customSecondaryHex = initialCustomSecondary,
                customCornerRadiusDp = initialCornerRadius,
                borderGlowIntensity = initialBorderGlow,
                glassBlurLevel = initialGlassBlur,
                customIconUri = initialCustomIconUri
            ) {
                AuthScreen(
                    onAuthSuccess = { /* authStateFlow updates automatically */ },
                    onContinueAsGuest = {
                        isGuestMode = true
                        launchPrefs.edit().putBoolean("isGuestMode", true).apply()
                    }
                )
            }
        } else {
            val effectiveUserId = user?.uid ?: "guest_user"
            key(effectiveUserId) {
                OrbitAuthenticatedApp(
                    currentUserId = effectiveUserId,
                    isGuest = user == null,
                    onPromptSignIn = {
                        isGuestMode = false
                        launchPrefs.edit().putBoolean("isGuestMode", false).apply()
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = showInitialSplash,
            exit = fadeOut(tween(340))
        ) {
            OrbitTheme(
                styleMode = initialMode,
                brightness = ThemeBrightness.DARK,
                palette = initialPalette,
                iconTheme = initialIconTheme,
                customAccentHex = initialCustomAccent,
                customSecondaryHex = initialCustomSecondary
            ) {
                OrbitSplashScreen(
                    styleMode = initialMode,
                    palette = initialPalette,
                    iconTheme = initialIconTheme,
                    greetingText = localizedSplashGreeting(initialLanguage),
                    isRu = initialLanguage == AppLanguage.RU,
                    onFinished = { showInitialSplash = false }
                )
            }
        }
    }
}

private fun localizedSplashGreeting(lang: AppLanguage): String = when (lang) {
    AppLanguage.RU -> "Привет"
    AppLanguage.EN -> "Hello"
    AppLanguage.KK -> "Сәлем"
    AppLanguage.ES -> "Hola"
    AppLanguage.DE -> "Hallo"
    AppLanguage.FR -> "Bonjour"
}

@Composable
private fun OrbitAuthenticatedApp(
    currentUserId: String,
    isGuest: Boolean = false,
    onPromptSignIn: () -> Unit = {}
) {
    val context = LocalContext.current
    val launchPrefs = remember(context) {
        context.getSharedPreferences("orbit_launch_theme", Context.MODE_PRIVATE)
    }
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val viewModel: OrbitViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                OrbitViewModel(
                    firestoreRepository = OrbitFirestoreRepository(app),
                    redAiRepository = RedAiRepository(),
                    currentUserId = currentUserId
                )
            }
        }
    )

    val settingsUi by viewModel.settingsState.collectAsStateWithLifecycle()
    val profileUi by viewModel.profileState.collectAsStateWithLifecycle()
    val favoritesUi by viewModel.favoritesState.collectAsStateWithLifecycle()
    val historyUi by viewModel.historyState.collectAsStateWithLifecycle()

    // While loading, restore last persisted appearance preferences so theme NEVER resets or flickers!
    val settings = (settingsUi as? CloudUiState.Success)?.data ?: remember(currentUserId, launchPrefs) {
        OrbitSettings(
            userId = currentUserId,
            styleMode = launchPrefs.getString("styleMode", BrowserStyleMode.LIQUID_GLASS.name) ?: BrowserStyleMode.LIQUID_GLASS.name,
            themeBrightness = launchPrefs.getString("brightness", ThemeBrightness.LIGHT.name) ?: ThemeBrightness.LIGHT.name,
            colorPalette = launchPrefs.getString("palette", ColorPalettePreset.ORBIT_CYAN.name) ?: ColorPalettePreset.ORBIT_CYAN.name,
            wallpaperStyle = launchPrefs.getString("wallpaperStyle", WallpaperStyle.NONE.name) ?: WallpaperStyle.NONE.name,
            homeLayout = launchPrefs.getString("homeLayout", HomeLayoutStyle.CLASSIC.name) ?: HomeLayoutStyle.CLASSIC.name,
            dockStyle = launchPrefs.getString("dockStyle", OrbitDockStyle.SAFARI_CAPSULE.name) ?: OrbitDockStyle.SAFARI_CAPSULE.name,
            monarchG2EngineEnabled = launchPrefs.getBoolean("monarchG2EngineEnabled", true),
            iconTheme = launchPrefs.getString("iconTheme", OrbitIconTheme.AUTO_THEME.name) ?: OrbitIconTheme.AUTO_THEME.name,
            glassTransparency = launchPrefs.getString("glassTransparency", GlassTransparencyLevel.FROSTED.name)
                ?: GlassTransparencyLevel.FROSTED.name,
            animationStyle = launchPrefs.getString("animationStyle", OrbitAnimationStyle.FLUID_SPRING.name)
                ?: OrbitAnimationStyle.FLUID_SPRING.name,
            cornerStyle = launchPrefs.getString("cornerStyle", OrbitCornerStyle.ROUNDED.name)
                ?: OrbitCornerStyle.ROUNDED.name,
            starfieldParticlesEnabled = launchPrefs.getBoolean("starfieldParticlesEnabled", true),
            themedIconsAdaptApp = launchPrefs.getBoolean("themedIconsAdaptApp", true),
            expressiveShapeStyle = launchPrefs.getString("expressiveShapeStyle", ExpressiveShapeStyle.AUTO_MIX.name) ?: ExpressiveShapeStyle.AUTO_MIX.name,
            homeLogoScale = launchPrefs.getString("homeLogoScale", OrbitLogoScale.MEDIUM.name) ?: OrbitLogoScale.MEDIUM.name,
            customAccentHex = launchPrefs.getLong("customAccentHex", 0L),
            customSecondaryHex = launchPrefs.getLong("customSecondaryHex", 0L),
            customCornerRadiusDp = launchPrefs.getLong("customCornerRadiusDp", 20L),
            borderGlowIntensity = launchPrefs.getLong("borderGlowIntensity", 50L),
            glassBlurLevel = launchPrefs.getLong("glassBlurLevel", 50L),
            customIconUri = launchPrefs.getString("customIconUri", "") ?: "",
            soundEffectsEnabled = launchPrefs.getBoolean("soundEffectsEnabled", true),
            floatingSpeedHudEnabled = launchPrefs.getBoolean("floatingSpeedHudEnabled", false),
            mercuryAiModel = launchPrefs.getString("mercuryAiModel", MercuryAiModel.MERCURY_38_FLASH.modelId)
                ?: MercuryAiModel.MERCURY_38_FLASH.modelId,
            appLanguage = launchPrefs.getString("appLanguage", AppLanguage.RU.name) ?: AppLanguage.RU.name
        )
    }
    val profile = (profileUi as? CloudUiState.Success)?.data
    val favorites = (favoritesUi as? CloudUiState.Success)?.data ?: emptyList()
    val history = (historyUi as? CloudUiState.Success)?.data ?: emptyList()

    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val activeTabId by viewModel.activeTabId.collectAsStateWithLifecycle()
    val activeTab = tabs.firstOrNull { it.id == activeTabId } ?: tabs.firstOrNull()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()

    val toast by viewModel.toast.collectAsStateWithLifecycle()
    val blockedEvents by viewModel.blockedEvents.collectAsStateWithLifecycle()
    val redAiMessages by viewModel.redAiMessages.collectAsStateWithLifecycle()
    val redAiBusy by viewModel.redAiBusy.collectAsStateWithLifecycle()
    val redAiError by viewModel.redAiError.collectAsStateWithLifecycle()
    val latestScan by viewModel.latestAntivirusScan.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()

    var currentRoute by remember { mutableStateOf(OrbitRoute.BROWSER) }
    var showCustomizeSheet by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }
    var showTabsOverlay by remember { mutableStateOf(false) }
    var replaySplashSpec by remember { mutableStateOf<Triple<BrowserStyleMode, ColorPalettePreset, OrbitIconTheme>?>(null) }

    LaunchedEffect(settings.soundEffectsEnabled) {
        OrbitSoundPlayer.isEnabled = settings.soundEffectsEnabled
    }

    // Persist to orbit_launch_theme synchronously once loaded so theme NEVER resets on restart!
    LaunchedEffect(settingsUi) {
        val loaded = (settingsUi as? CloudUiState.Success)?.data ?: return@LaunchedEffect
        launchPrefs.edit()
            .putString("styleMode", loaded.resolvedStyleMode.name)
            .putString("brightness", loaded.resolvedBrightness.name)
            .putString("palette", loaded.resolvedPalette.name)
            .putString("wallpaperStyle", loaded.resolvedWallpaper.name)
            .putString("homeLayout", loaded.resolvedLayout.name)
            .putString("dockStyle", loaded.resolvedDockStyle.name)
            .putBoolean("monarchG2EngineEnabled", loaded.monarchG2EngineEnabled)
            .putString("iconTheme", loaded.resolvedIconTheme.name)
            .putString("glassTransparency", loaded.resolvedGlassTransparency.name)
            .putString("animationStyle", loaded.resolvedAnimationStyle.name)
            .putString("cornerStyle", loaded.resolvedCornerStyle.name)
            .putBoolean("starfieldParticlesEnabled", loaded.starfieldParticlesEnabled)
            .putBoolean("themedIconsAdaptApp", loaded.themedIconsAdaptApp)
            .putString("expressiveShapeStyle", loaded.resolvedExpressiveShapeStyle.name)
            .putString("homeLogoScale", loaded.resolvedHomeLogoScale.name)
            .putLong("customAccentHex", loaded.customAccentHex)
            .putLong("customSecondaryHex", loaded.customSecondaryHex)
            .putLong("customCornerRadiusDp", loaded.customCornerRadiusDp)
            .putLong("borderGlowIntensity", loaded.borderGlowIntensity)
            .putLong("glassBlurLevel", loaded.glassBlurLevel)
            .putString("customIconUri", loaded.customIconUri)
            .putBoolean("soundEffectsEnabled", loaded.soundEffectsEnabled)
            .putBoolean("floatingSpeedHudEnabled", loaded.floatingSpeedHudEnabled)
            .putString("mercuryAiModel", loaded.resolvedMercuryModel.modelId)
            .putString("appLanguage", loaded.resolvedLanguage.name)
            .apply()
    }

    val strings = getOrbitStrings(settings.resolvedLanguage)
    val isRu = settings.resolvedLanguage == AppLanguage.RU
    val activeAnimStyle = settings.resolvedAnimationStyle

    // 3D Perspective Tabs Switcher Animation as requested:
    // "когда заходишь в вкладки то оно выдвигает главный экран и показывает все вкладки а когда закрываешь секцию то оно возращается"
    val tabsPerspectiveScale by animateFloatAsState(
        targetValue = if (showTabsOverlay) 0.82f else 1.0f,
        animationSpec = spring(
            dampingRatio = activeAnimStyle.dampingRatio,
            stiffness = activeAnimStyle.stiffness
        ),
        label = "tabsScale"
    )
    val tabsPerspectiveTransY by animateDpAsState(
        targetValue = if (showTabsOverlay) (-55).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = activeAnimStyle.dampingRatio,
            stiffness = activeAnimStyle.stiffness
        ),
        label = "tabsTransY"
    )
    val tabsPerspectiveCorner by animateDpAsState(
        targetValue = if (showTabsOverlay) 30.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = activeAnimStyle.dampingRatio,
            stiffness = activeAnimStyle.stiffness
        ),
        label = "tabsCorner"
    )

    OrbitTheme(
        styleMode = settings.resolvedStyleMode,
        brightness = settings.resolvedBrightness,
        palette = settings.resolvedPalette,
        glassTransparency = settings.resolvedGlassTransparency,
        animationStyle = activeAnimStyle,
        cornerStyle = settings.resolvedCornerStyle,
        monarchG2Enabled = settings.monarchG2EngineEnabled,
        iconTheme = settings.resolvedIconTheme,
        themedIconsAdaptApp = settings.themedIconsAdaptApp,
        expressiveShapeStyle = settings.resolvedExpressiveShapeStyle,
        homeLogoScale = settings.resolvedHomeLogoScale,
        customAccentHex = settings.customAccentHex,
        customSecondaryHex = settings.customSecondaryHex,
        customCornerRadiusDp = settings.customCornerRadiusDp.toInt(),
        borderGlowIntensity = settings.borderGlowIntensity.toInt(),
        glassBlurLevel = settings.glassBlurLevel.toInt(),
        customIconUri = settings.customIconUri
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main App Viewport with 3D Depth Card perspective when tabs overlay is active
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = tabsPerspectiveScale
                        scaleY = tabsPerspectiveScale
                        translationY = tabsPerspectiveTransY.toPx()
                        rotationX = if (showTabsOverlay) 4.5f else 0f
                        cameraDistance = 16f * density
                    }
                    .clip(RoundedCornerShape(tabsPerspectiveCorner))
            ) {
                // Dynamic Wallpaper Layer with optional cosmic starfield particles
                OrbitWallpaperLayer(
                    wallpaper = settings.resolvedWallpaper,
                    showStarfield = settings.starfieldParticlesEnabled
                )

            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = {
                    if (targetState == OrbitRoute.BROWSER) {
                        (slideInHorizontally { -it / 4 } + fadeIn()) togetherWith
                            (slideOutHorizontally { it / 4 } + fadeOut())
                    } else {
                        (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 4 } + fadeOut())
                    }
                },
                label = "orbitMainRoute"
            ) { route ->
                when (route) {
                    OrbitRoute.BROWSER -> {
                        val isHome = activeTab != null && activeTab.url == "orbit://home"
                        if (activeTab == null) {
                            EmptyTabsPlaceholderScreen(
                                isRu = isRu,
                                onOpenTabsSwitcher = { showTabsOverlay = true },
                                onCreateTab = { incognito -> viewModel.createNewTab(incognito = incognito) }
                            )
                        } else {
                            AnimatedContent(
                                targetState = isHome,
                                transitionSpec = {
                                    (scaleIn(
                                        initialScale = 0.94f,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                    ) + fadeIn()) togetherWith (scaleOut(targetScale = 0.96f) + fadeOut())
                                },
                                label = "homeToWebTransition"
                            ) { homeMode ->
                                if (homeMode) {
                                    BrowserHomeScreen(
                                        settings = settings,
                                        profile = profile,
                                        favorites = favorites,
                                        strings = strings,
                                        tabCount = tabs.size,
                                        telemetry = telemetry,
                                        onOpenUrl = { raw -> viewModel.navigateActiveTab(raw) },
                                        onAddFavorite = { title, url, badge, hex ->
                                            viewModel.addFavorite(title, url, badge, hex)
                                        },
                                        onRemoveFavorite = { id -> viewModel.removeFavorite(id) },
                                        onOpenCustomize = { showCustomizeSheet = true },
                                        onOpenPrivacyReport = { currentRoute = OrbitRoute.PRIVATE_DASHBOARD },
                                        onOpenTabsSwitcher = { showTabsOverlay = true },
                                        onOpenMenu = { showMenuSheet = true },
                                        onOpenRedAi = { currentRoute = OrbitRoute.RED_AI },
                                        onOpenNAccount = { currentRoute = OrbitRoute.NACCOUNT },
                                        onOpenGamingHub = { currentRoute = OrbitRoute.GAMING },
                                        onSaveQuickNotes = { text -> viewModel.updateSetting("quickNotesText", text) }
                                    )
                                } else {
                                    WebViewTabScreen(
                                        tab = activeTab,
                                        settings = settings,
                                        strings = strings,
                                        tabCount = tabs.size,
                                        onNavigate = { raw -> viewModel.navigateActiveTab(raw) },
                                        onUpdateTabState = { id, title, url, loading, progress, back, fwd, record ->
                                            viewModel.updateActiveTabWebState(
                                                tabId = id,
                                                title = title,
                                                url = url,
                                                isLoading = loading,
                                                progress = progress,
                                                canGoBack = back,
                                                canGoForward = fwd,
                                                recordHistory = record
                                            )
                                        },
                                        onResourceBlocked = { host, match ->
                                            viewModel.onResourceBlocked(host, match)
                                        },
                                        onGoHome = { viewModel.navigateActiveTab("orbit://home") },
                                        onOpenTabsSwitcher = { showTabsOverlay = true },
                                        onOpenMenu = { showMenuSheet = true },
                                        onSummarizeWithRedAi = { snippet ->
                                            val prompt = if (isRu) {
                                                "Сделай краткую, понятную выжимку этой веб-страницы"
                                            } else {
                                                "Summarize the key points of this webpage"
                                            }
                                            currentRoute = OrbitRoute.RED_AI
                                            viewModel.sendRedAiMessage(prompt, pageSnippet = snippet)
                                        },
                                        onOpenSecurityInspector = { currentRoute = OrbitRoute.SECURITY },
                                        onDownloadStarted = { name, url, size ->
                                            viewModel.addDownload(name, url, size)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    OrbitRoute.SETTINGS -> {
                        SettingsScreen(
                            settings = settings,
                            strings = strings,
                            onBack = { currentRoute = OrbitRoute.BROWSER },
                            onUpdateSetting = { key, value -> viewModel.updateSetting(key, value) },
                            onOpenNAccount = { currentRoute = OrbitRoute.NACCOUNT },
                            onOpenSecurity = { currentRoute = OrbitRoute.SECURITY },
                            onOpenGaming = { currentRoute = OrbitRoute.GAMING },
                            onOpenPrivateDashboard = { currentRoute = OrbitRoute.PRIVATE_DASHBOARD },
                            onUpdateSettingsBatch = { fields -> viewModel.updateSettingsBatch(fields) },
                            onReplaySplash = { mode, palette, iconTheme ->
                                replaySplashSpec = Triple(mode, palette, iconTheme)
                            }
                        )
                    }

                    OrbitRoute.SECURITY -> {
                        SecurityPrivacyScreen(
                            settings = settings,
                            blockedEvents = blockedEvents,
                            latestScan = latestScan,
                            currentTabUrl = activeTab?.url ?: "orbit://home",
                            isAiBusy = redAiBusy,
                            onBack = { currentRoute = OrbitRoute.BROWSER },
                            onUpdateSetting = { key, value -> viewModel.updateSetting(key, value) },
                            onToggleVpn = { enable, region -> viewModel.toggleVpn(enable, region) },
                            onRunAiUrlScan = { url -> viewModel.runAntivirusCheckWithAi(url) },
                            onOpenUrlInTab = { url ->
                                currentRoute = OrbitRoute.BROWSER
                                viewModel.navigateActiveTab(url)
                            }
                        )
                    }

                    OrbitRoute.GAMING -> {
                        GamingPerformanceScreen(
                            settings = settings,
                            telemetry = telemetry,
                            onBack = { currentRoute = OrbitRoute.BROWSER },
                            onToggleGaming = { enable -> viewModel.toggleGamingMode(enable) },
                            onUpdateSetting = { key, value -> viewModel.updateSetting(key, value) },
                            onRefreshTelemetry = { viewModel.refreshTelemetry(context) }
                        )
                    }

                    OrbitRoute.RED_AI -> {
                        RedAiScreen(
                            settings = settings,
                            activeTab = activeTab ?: BrowserTab(id = "home_fallback", title = "Orbit", url = "orbit://home"),
                            messages = redAiMessages,
                            isBusy = redAiBusy,
                            errorMessage = redAiError,
                            onBack = { currentRoute = OrbitRoute.BROWSER },
                            onSendMessage = { prompt -> viewModel.sendRedAiMessage(prompt) },
                            onClearChat = { viewModel.clearRedAiChat() },
                            onSelectModel = { model -> viewModel.selectMercuryModel(model) }
                        )
                    }

                    OrbitRoute.NACCOUNT -> {
                        NAccountScreen(
                            profile = profile,
                            settings = settings,
                            onBack = { currentRoute = OrbitRoute.BROWSER },
                            onSaveProfile = { handle, name, vk, proton, tg, sync, bm, set, hist ->
                                viewModel.updateNAccount(
                                    naccountHandle = handle,
                                    displayName = name,
                                    vkHandle = vk,
                                    protonHandle = proton,
                                    telegramHandle = tg,
                                    cloudSyncEnabled = sync,
                                    syncBookmarks = bm,
                                    syncSettings = set,
                                    syncHistory = hist
                                )
                            },
                            onOpenPortalUrl = { url ->
                                currentRoute = OrbitRoute.BROWSER
                                viewModel.navigateActiveTab(url)
                            },
                            onSignOut = {
                                if (isGuest) {
                                    onPromptSignIn()
                                } else {
                                    signOut(
                                        credentialManager = credentialManager,
                                        scope = scope
                                    )
                                }
                            }
                        )
                    }

                    OrbitRoute.HISTORY -> {
                        HistoryScreen(
                            entries = history,
                            isRu = isRu,
                            onBack = { currentRoute = OrbitRoute.BROWSER },
                            onOpenUrl = { url ->
                                currentRoute = OrbitRoute.BROWSER
                                viewModel.navigateActiveTab(url)
                            },
                            onClearHistory = { viewModel.clearHistory() }
                        )
                    }

                    OrbitRoute.PRIVATE_DASHBOARD -> {
                        PrivateDashboardScreen(
                            settings = settings,
                            blockedEvents = blockedEvents,
                            onBack = { currentRoute = OrbitRoute.BROWSER },
                            onUpdateSetting = { key, value -> viewModel.updateSetting(key, value) },
                            onShredSession = {
                                viewModel.shredAllBrowserData(context)
                            }
                        )
                    }
                }
            }

            // Darkening scrim when tabs overlay is active - tapping shrinks/restores the card
            if (showTabsOverlay) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                        .clickable { showTabsOverlay = false }
                )
            }
        } // Close Main 3D Perspective Card Box

            // Optional Floating Speed & RAM HUD Pill
            if (settings.floatingSpeedHudEnabled && !showTabsOverlay) {
                OrbitGlassSurface(
                    shape = CircleShape,
                    elevated = true,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(start = 14.dp, top = 8.dp)
                        .testTag("floating_speed_hud")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Speed,
                            contentDescription = null,
                            tint = Color(0xFF10E89A),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isRu) "${telemetry.appHeapMb} МБ · ${tabs.size} вкл." else "${telemetry.appHeapMb}MB · ${tabs.size} tabs",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LocalOrbitTokens.current.onCleanBackground
                        )
                    }
                }
            }

            // Tab Switcher Overlay
            TabsSwitcherOverlay(
                visible = showTabsOverlay,
                tabs = tabs,
                activeTabId = activeTabId,
                strings = strings,
                isRu = isRu,
                onDismiss = { showTabsOverlay = false },
                onSelectTab = { id ->
                    viewModel.selectTab(id)
                    currentRoute = OrbitRoute.BROWSER
                },
                onCloseTab = { id -> viewModel.closeTab(id) },
                onNewTab = { incognito ->
                    viewModel.createNewTab(incognito = incognito)
                    currentRoute = OrbitRoute.BROWSER
                }
            )

            // Home Customization Upward Popup
            CustomizeHomeSheet(
                visible = showCustomizeSheet,
                settings = settings,
                onDismiss = { showCustomizeSheet = false },
                onUpdateSetting = { key, value -> viewModel.updateSetting(key, value) },
                onUpdateSettingsBatch = { fields -> viewModel.updateSettingsBatch(fields) },
                onReplaySplash = { mode, palette, iconTheme ->
                    replaySplashSpec = Triple(mode, palette, iconTheme)
                }
            )

            // Orbit Command Center Menu Upward Popup
            OrbitMenuSheet(
                visible = showMenuSheet,
                settings = settings,
                strings = strings,
                canToggleDesktop = activeTab != null && activeTab.url != "orbit://home",
                desktopEnabled = activeTab?.desktopMode == true,
                onDismiss = { showMenuSheet = false },
                onOpenSettings = { currentRoute = OrbitRoute.SETTINGS },
                onOpenSecurity = { currentRoute = OrbitRoute.SECURITY },
                onOpenGaming = { currentRoute = OrbitRoute.GAMING },
                onOpenRedAi = { currentRoute = OrbitRoute.RED_AI },
                onOpenNAccount = { currentRoute = OrbitRoute.NACCOUNT },
                onOpenHistory = { currentRoute = OrbitRoute.HISTORY },
                onOpenPrivateDashboard = { currentRoute = OrbitRoute.PRIVATE_DASHBOARD },
                onToggleDesktop = { viewModel.toggleDesktopMode() },
                onToggleTheme = {
                    val next = if (settings.resolvedBrightness == ThemeBrightness.DARK) {
                        ThemeBrightness.LIGHT
                    } else {
                        ThemeBrightness.DARK
                    }
                    viewModel.updateSetting("themeBrightness", next.name)
                },
                onToggleAdBlock = {
                    viewModel.updateSetting("adBlockEnabled", !settings.adBlockEnabled)
                },
                onToggleStealth = {
                    viewModel.updateSetting("antiTrackingEnabled", !settings.antiTrackingEnabled)
                },
                onToggleReaderMode = { viewModel.toggleReaderMode() },
                readerModeActive = activeTab?.readerMode == true,
                onSharePage = {
                    val shareUrl = if (activeTab == null || activeTab.url == "orbit://home") "https://orbit.browser" else activeTab.url
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, shareUrl)
                        putExtra(Intent.EXTRA_TITLE, activeTab?.title ?: "Orbit")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, if (isRu) "Поделиться" else "Share"))
                },
                downloads = downloads,
                onClearDownloads = { viewModel.clearDownloads() }
            )

            // Animated Top Notification Banner
            OrbitFloatingToastBanner(
                notification = toast,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Replayable Animated Orbit Splash Overlay (always dark mode for clear contrast)
            val activePreviewSpec = replaySplashSpec
            AnimatedVisibility(
                visible = activePreviewSpec != null,
                enter = fadeIn(tween(180)),
                exit = fadeOut(tween(320))
            ) {
                if (activePreviewSpec != null) {
                    val (previewMode, previewPalette, previewIconTheme) = activePreviewSpec
                    OrbitTheme(
                        styleMode = previewMode,
                        brightness = ThemeBrightness.DARK,
                        palette = previewPalette,
                        glassTransparency = settings.resolvedGlassTransparency,
                        animationStyle = activeAnimStyle,
                        cornerStyle = settings.resolvedCornerStyle,
                        monarchG2Enabled = settings.monarchG2EngineEnabled,
                        iconTheme = previewIconTheme,
                        customAccentHex = settings.customAccentHex,
                        customSecondaryHex = settings.customSecondaryHex
                    ) {
                        OrbitSplashScreen(
                            styleMode = previewMode,
                            palette = previewPalette,
                            iconTheme = previewIconTheme,
                            greetingText = localizedSplashGreeting(settings.resolvedLanguage),
                            isRu = isRu,
                            onFinished = { replaySplashSpec = null }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTabsPlaceholderScreen(
    isRu: Boolean,
    onOpenTabsSwitcher: () -> Unit,
    onCreateTab: (incognito: Boolean) -> Unit
) {
    val tokens = LocalOrbitTokens.current
    var showTip by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // Top question-mark help affordance requested by user
        OrbitGlassSurface(
            shape = CircleShape,
            elevated = true,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
                .testTag("empty_state_help_button")
        ) {
            IconButton(onClick = { showTip = !showTip }) {
                Icon(
                    imageVector = Icons.Rounded.HelpOutline,
                    contentDescription = if (isRu) "Подсказка" else "Help",
                    tint = tokens.accentPrimary
                )
            }
        }

        // Bottom prompt card when last tab was closed
        OrbitGlassSurface(
            shape = orbitContainerShape(26.dp),
            elevated = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isRu) "Ой! А где страница?" else "Oops! Where's the page?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = tokens.onCleanBackground,
                    textAlign = TextAlign.Center
                )
                if (showTip) {
                    Text(
                        text = if (isRu) {
                            "Нажми в вкладки и нажми плюсик для работы — или открой новую вкладку прямо здесь."
                        } else {
                            "Tap Tabs and hit the + button to start working — or open a new tab below."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.secondaryText,
                        textAlign = TextAlign.Center
                    )
                }
                OrbitGlassSurface(
                    shape = orbitContainerShape(16.dp),
                    accentHighlight = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable(onClick = onOpenTabsSwitcher)
                        .testTag("empty_state_open_tabs")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Layers,
                            contentDescription = null,
                            tint = tokens.accentPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRu) "Открыть вкладки (+)" else "Open Tabs (+)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = tokens.onCleanBackground
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onCreateTab(false) },
                        shape = orbitContainerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentPrimary,
                            contentColor = Color(0xFF061018)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("empty_state_new_tab")
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRu) "Вкладка" else "New Tab", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onCreateTab(true) },
                        shape = orbitContainerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Rounded.VisibilityOff, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRu) "Инкогнито" else "Incognito")
                    }
                }
            }
        }
    }
}

