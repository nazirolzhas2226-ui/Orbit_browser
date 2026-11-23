package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class BrowserStyleMode(
    val labelRu: String,
    val labelEn: String,
    val usageRu: String,
    val usageEn: String
) {
    LIQUID_GLASS(
        "Liquid Glass",
        "Liquid Glass",
        "Прозрачность, глубина и блики Monarch G2",
        "For crystal transparency & Monarch G2 optics"
    ),
    M3_EXPRESSIVE(
        "Material You",
        "Material You",
        "Живые цвета Material 3 и разные формы (без Liquid Glass)",
        "Opaque Material 3 tones & organic shapes (no Liquid Glass)"
    ),
    GAMING_GX(
        "JupiterGX Mode",
        "JupiterGX Mode",
        "Игры, полосы Юпитера, неон и контроль ОЗУ",
        "For gaming, Jupiter cyber rings & RAM limiting"
    ),
    MINIMALIST(
        "Минимализм",
        "Minimalist",
        "Чистота интерфейса и максимальный фокус",
        "For ultra-clean focus & pure speed"
    ),
    NOTHING_OS(
        "NothingOS",
        "NothingOS",
        "Глифы, точечная матрица и контрастный стиль",
        "Glyph dot-matrix and monochrome matrix design"
    ),
    KOSMOS(
        "Космос",
        "Kosmos",
        "Глубокий космос, туманности и звёздное свечение",
        "Deep space, nebula starlight & stellar glow"
    ),
    MINECRAFT(
        "Minecraft",
        "Minecraft",
        "Пиксельные блоки и 8-битный игровой стиль",
        "Pixel blocky retro style & 8-bit interface"
    ),
    SAKURA(
        "Сакура",
        "Sakura",
        "Лепестки сакуры и нежные розовые тона",
        "Cherry blossom petals & soft pink pastels"
    );

    fun officialPalette(): ColorPalettePreset = when (this) {
        LIQUID_GLASS -> ColorPalettePreset.ORBIT_CYAN
        M3_EXPRESSIVE -> ColorPalettePreset.EXPRESSIVE_VIOLET
        GAMING_GX -> ColorPalettePreset.JUPITER_CYBER
        MINIMALIST -> ColorPalettePreset.ARCTIC_FROST
        NOTHING_OS -> ColorPalettePreset.MOON_FROST
        KOSMOS -> ColorPalettePreset.ANDROMEDA_NEBULA
        MINECRAFT -> ColorPalettePreset.CYBER_LIME
        SAKURA -> ColorPalettePreset.SAKURA_ROSE
    }

    fun officialWallpaper(): WallpaperStyle = when (this) {
        LIQUID_GLASS -> WallpaperStyle.LIQUID_AURORA
        M3_EXPRESSIVE -> WallpaperStyle.MINT_MEADOW
        GAMING_GX -> WallpaperStyle.JUPITER_VORTEX
        MINIMALIST -> WallpaperStyle.OBSIDIAN_MESH
        NOTHING_OS -> WallpaperStyle.CYBER_GRID
        KOSMOS -> WallpaperStyle.AURORA_BOREALIS
        MINECRAFT -> WallpaperStyle.OBSIDIAN_MESH
        SAKURA -> WallpaperStyle.SAKURA_SILK
    }

    companion object {
        fun safeValueOf(raw: String?): BrowserStyleMode =
            entries.firstOrNull { it.name == raw } ?: LIQUID_GLASS
    }
}

enum class OrbitIconTheme(
    val labelRu: String,
    val labelEn: String,
    val primaryHex: Long,
    val secondaryHex: Long,
    val isRainbow: Boolean = false,
    val motifRu: String = "",
    val motifEn: String = ""
) {
    AUTO_THEME(
        "По теме", "Match Theme", 0xFF00D4FF, 0xFF7B61FF,
        motifRu = "Адаптивный стиль темы", motifEn = "Adapts to active theme"
    ),
    ORBIT_CYAN(
        "Неон Циан", "Neon Cyan", 0xFF00D4FF, 0xFF3B82F6,
        motifRu = "Классическая сфера Orbit", motifEn = "Classic luminous sphere"
    ),
    GX_CRIMSON(
        "JupiterGX Неон", "JupiterGX Neon", 0xFFFF1E56, 0xFF00F0FF,
        motifRu = "Двойные кибер-дуги Юпитера", motifEn = "Split JupiterGX cyber arcs"
    ),
    JUPITER_GX(
        "JupiterGX Сфера", "JupiterGX Sphere", 0xFFFF4D6D, 0xFF00F0FF,
        motifRu = "Полосы Юпитера и орбита", motifEn = "Banded Jupiter & neon rings"
    ),
    MERCURY_CORE(
        "Ядро MercuryAI", "MercuryAI Core", 0xFFE2E8F0, 0xFFFF3E66,
        motifRu = "Жидкая ртуть и искра ИИ", motifEn = "Liquid silver & crimson flare"
    ),
    PURE_WHITE(
        "Белый лёд", "Pure White", 0xFFFFFFFF, 0xFFE2E8F0,
        motifRu = "Лунное затмение", motifEn = "Silver lunar eclipse"
    ),
    RAINBOW_COMPASS(
        "Радужный Компас", "Rainbow Compass", 0xFFEC4899, 0xFF00E5FF,
        isRainbow = true,
        motifRu = "Стрелка и спектр 360°", motifEn = "Magnetic needle & spectrum"
    ),
    GOLD_SATURN(
        "Кольца Сатурна", "Saturn Gold", 0xFFD4AF37, 0xFFF59E0B,
        motifRu = "Два золотых кольца", motifEn = "Dual golden Saturn rings"
    ),
    EMERALD_PULSE(
        "Изумруд Пульс", "Emerald Pulse", 0xFF10E89A, 0xFF059669,
        motifRu = "Две атомные орбиты", motifEn = "Intersecting twin orbits"
    ),
    VIOLET_NEBULA(
        "Небула Звезда", "Nebula Star", 0xFF8B5CF6, 0xFFEC4899,
        motifRu = "Звёздная вспышка", motifEn = "Core star flare & rings"
    ),
    MARS_CORE(
        "Ядро Марса", "Mars Core", 0xFFFF4500, 0xFFFF9E00,
        motifRu = "Планета со спутником", motifEn = "Crimson core & orbiting moon"
    ),
    NEPTUNE_PRISM(
        "Призма Нептуна", "Neptune Prism", 0xFF0088FF, 0xFF00E5FF,
        motifRu = "Кристалл и грани льда", motifEn = "Diamond prism & ice ring"
    ),
    BLACK_HOLE(
        "Чёрная Дыра", "Black Hole", 0xFFFF6D00, 0xFFFF1E56,
        motifRu = "Аккреционный диск", motifEn = "Singularity & fiery disk"
    ),
    AMBER_SOLAR(
        "Солнечная Корона", "Solar Corona", 0xFFFFB703, 0xFFFF595E,
        motifRu = "Лучи солнечной короны", motifEn = "Radiant sun flare rays"
    ),
    SAKURA_BLOSSOM(
        "Сакура Орбита", "Sakura Orbit", 0xFFFF6B9D, 0xFFFFC2D4,
        motifRu = "Лепестки сакуры", motifEn = "Cherry blossom ring"
    ),
    ARCTIC_CRYSTAL(
        "Арктический Лёд", "Arctic Crystal", 0xFF38BDF8, 0xFFBAE6FD,
        motifRu = "Кристалл снежинки", motifEn = "Ice crystal & frost halo"
    ),
    CYBER_MINT(
        "Кибер Мята", "Cyber Mint", 0xFF00F5D4, 0xFF7B2CBF,
        motifRu = "Неон мята + ультра", motifEn = "Cyber mint & violet orbit"
    ),
    CHERRY_NEON(
        "Вишнёвый Неон", "Cherry Neon", 0xFFFF0055, 0xFF7928CA,
        motifRu = "Двойные звёзды-спутники", motifEn = "Magenta core & twin stars"
    );

    companion object {
        fun safeValueOf(raw: String?): OrbitIconTheme =
            entries.firstOrNull { it.name == raw } ?: AUTO_THEME
    }
}

enum class ExpressiveShapeStyle(val labelRu: String, val labelEn: String) {
    AUTO_MIX("Микс форм", "Auto Mix"),
    FLOWER("Цветок", "Flower"),
    CLOVER("Клевер", "Clover"),
    STARBURST("Звезда", "Starburst"),
    HEXAGON("Шестигранник", "Hexagon"),
    SQUIRCLE("Сквиркл", "Squircle");

    companion object {
        fun safeValueOf(raw: String?): ExpressiveShapeStyle =
            entries.firstOrNull { it.name == raw } ?: AUTO_MIX
    }
}

enum class OrbitLogoScale(val labelRu: String, val labelEn: String, val factor: Float) {
    COMPACT("Компактный", "Compact", 0.84f),
    MEDIUM("Стандарт", "Standard", 1.0f),
    LARGE("Крупный", "Large", 1.18f);

    companion object {
        fun safeValueOf(raw: String?): OrbitLogoScale =
            entries.firstOrNull { it.name == raw } ?: MEDIUM
    }
}

enum class ThemeBrightness(val labelRu: String, val labelEn: String) {
    SYSTEM("Системная", "System"),
    DARK("Тёмная", "Dark"),
    LIGHT("Светлая", "Light");

    companion object {
        fun safeValueOf(raw: String?): ThemeBrightness =
            entries.firstOrNull { it.name == raw } ?: SYSTEM
    }
}

enum class OrbitDockStyle(val idNumber: Int, val labelRu: String, val labelEn: String, val descriptionRu: String) {
    SAFARI_CAPSULE(1, "1: Safari", "1: Safari", "Классический плавающий капсульный док"),
    MEGA_DOCK(2, "2: Очень большое", "2: Mega Dock", "Полноразмерный док с быстрыми действиями и статусом"),
    MINIMAL_PILL(3, "3: Минималистика", "3: Minimalist", "Ультра-компактная пилюля без лишних деталей"),
    KOSMOS_ORBITAL(4, "4: Kosmos", "4: Kosmos Orbital", "Орбитальный космический док с неоновыми кольцами планет");

    companion object {
        fun safeValueOf(raw: String?): OrbitDockStyle =
            entries.firstOrNull { it.name == raw } ?: SAFARI_CAPSULE
    }
}

enum class GlassTransparencyLevel(
    val labelRu: String,
    val labelEn: String,
    val lightAlpha: Float,
    val darkAlpha: Float
) {
    CRYSTAL("Хрусталь (18%)", "Crystal (18%)", 0.18f, 0.12f),
    FROSTED("Прозрачное (32%)", "Frosted (32%)", 0.32f, 0.22f),
    SILK("Шёлк (50%)", "Silk (50%)", 0.50f, 0.34f);

    companion object {
        fun safeValueOf(raw: String?): GlassTransparencyLevel =
            entries.firstOrNull { it.name == raw } ?: FROSTED
    }
}

enum class OrbitAnimationStyle(
    val labelRu: String,
    val labelEn: String,
    val dampingRatio: Float,
    val stiffness: Float
) {
    FLUID_SPRING("Плавная пружина", "Fluid Spring", 0.65f, 380f),
    TURBO_SNAPPY("Турбо-отклик", "Turbo Snappy", 0.85f, 1100f),
    CINEMATIC("Кинематографичный", "Cinematic", 0.78f, 180f);

    companion object {
        fun safeValueOf(raw: String?): OrbitAnimationStyle =
            entries.firstOrNull { it.name == raw } ?: FLUID_SPRING
    }
}

enum class OrbitCornerStyle(
    val labelRu: String,
    val labelEn: String,
    val radiusDeltaDp: Float
) {
    COMPACT("Строгие", "Compact", -8f),
    ROUNDED("Мягкие", "Rounded", 0f),
    CAPSULE("Капсула", "Capsule", 8f);

    companion object {
        fun safeValueOf(raw: String?): OrbitCornerStyle =
            entries.firstOrNull { it.name == raw } ?: ROUNDED
    }
}

enum class ColorPalettePreset(val labelRu: String, val labelEn: String, val primaryHex: Long, val secondaryHex: Long) {
    ORBIT_CYAN("Orbit Циан", "Orbit Cyan", 0xFF00D4FF, 0xFF7B61FF),
    EMERALD_MINT("Мятный изумруд", "Mint Emerald", 0xFF00D47E, 0xFF00B4D8),
    FROST_PRISM("Призма Ирис", "Prism Iris", 0xFF8B5CF6, 0xFFEC4899),
    NEPTUNE_DEEP("Глубины Нептуна", "Neptune Deep", 0xFF0088FF, 0xFF00E5FF),
    MARS_CRIMSON("Марс Одиссея", "Mars Odyssey", 0xFFFF4500, 0xFFFF8C00),
    JUPITER_STORM("Шторм Юпитера", "Jupiter Storm", 0xFFE08D3C, 0xFFF3C68F),
    SATURN_GOLD("Кольца Сатурна", "Saturn Rings", 0xFFD4AF37, 0xFFE8D382),
    SUPERNOVA_VIOLET("Сверхновая Звезда", "Supernova Violet", 0xFF9933FF, 0xFFFF007F),
    ANDROMEDA_NEBULA("Туманность Андромеды", "Andromeda Nebula", 0xFF6C5CE7, 0xFFFD79A8),
    MOON_FROST("Лунная Пыль", "Lunar Dust", 0xFFCBD5E1, 0xFF64748B),
    NEON_CRIMSON("JupiterGX Неон", "JupiterGX Crimson", 0xFFFF1E56, 0xFFFFAC41),
    EXPRESSIVE_VIOLET("Ультрафиолет", "Expressive Violet", 0xFF9C6BFF, 0xFFFF6EC7),
    AMBER_SOLAR("Солнечный янтарь", "Solar Amber", 0xFFFF9F1C, 0xFFFF595E),
    SAKURA_ROSE("Сакура Роуз", "Sakura Rose", 0xFFFF6B9D, 0xFFFF9EBB),
    ARCTIC_FROST("Арктический Фрост", "Arctic Frost", 0xFF38BDF8, 0xFF818CF8),
    CYBER_LIME("Кибер Лайм", "Cyber Lime", 0xFF84CC16, 0xFF06B6D4),
    OBSIDIAN_GOLD("Обсидиан Золото", "Obsidian Gold", 0xFFFBBF24, 0xFFFB7185),
    MERCURY_QUICKSILVER("Ртуть MercuryAI", "Mercury Quicksilver", 0xFFE2E8F0, 0xFFFF3E66),
    JUPITER_CYBER("Юпитер Кибер", "Jupiter Cyber", 0xFFFF1E56, 0xFF00F0FF);

    companion object {
        fun safeValueOf(raw: String?): ColorPalettePreset =
            entries.firstOrNull { it.name == raw } ?: ORBIT_CYAN
    }
}

enum class WallpaperStyle(val labelRu: String, val labelEn: String) {
    NONE("Без фона (Чистый)", "No Wallpaper"),
    MINT_MEADOW("Мятная поляна", "Mint Meadow"),
    PRISM_FROST("Призматический кристалл", "Prism Crystal"),
    KOSMOS_NEBULA("Космическая туманность", "Cosmic Nebula"),
    PLANET_EARTH("Земля с орбиты", "Earth Orbit"),
    DEEP_PULSAR("Звёздный пульсар", "Deep Pulsar"),
    LIQUID_AURORA("Жидкое сияние", "Liquid Aurora"),
    CYBER_GRID("Кибер-сетка JupiterGX", "Cyber Grid JupiterGX"),
    OCEAN_MIST("Океанский бриз", "Ocean Mist"),
    OBSIDIAN_MESH("Обсидиан", "Obsidian Mesh"),
    SUNSET_GLASS("Закатное стекло", "Sunset Glass"),
    AURORA_BOREALIS("Северное сияние", "Aurora Borealis"),
    SAKURA_SILK("Сакура Шёлк", "Sakura Silk"),
    SOLAR_ECLIPSE("Солнечное затмение", "Solar Eclipse"),
    CYBERPUNK_TOKYO("Неоновый Токио", "Neon Tokyo"),
    MERCURY_LIQUID("Жидкая ртуть Mercury", "Mercury Quicksilver"),
    JUPITER_VORTEX("Вихрь Юпитера GX", "JupiterGX Vortex");

    companion object {
        fun safeValueOf(raw: String?): WallpaperStyle =
            entries.firstOrNull { it.name == raw } ?: NONE
    }
}

enum class HomeLayoutStyle(val labelRu: String, val labelEn: String) {
    CLASSIC("Классический", "Classic"),
    COMPACT("Компактный", "Compact"),
    DASHBOARD("Дашборд", "Dashboard"),
    MINIMAL("Фокус", "Minimal");

    companion object {
        fun safeValueOf(raw: String?): HomeLayoutStyle =
            entries.firstOrNull { it.name == raw } ?: CLASSIC
    }
}

enum class VpnRegion(val labelRu: String, val labelEn: String, val latencyMs: Int, val countryCode: String) {
    AMSTERDAM("Амстердам, NL", "Amsterdam, NL", 24, "NL"),
    FRANKFURT("Франкфурт, DE", "Frankfurt, DE", 29, "DE"),
    ZURICH("Цюрих, CH", "Zurich, CH", 33, "CH"),
    TOKYO("Токио, JP", "Tokyo, JP", 94, "JP"),
    SINGAPORE("Сингапур, SG", "Singapore, SG", 88, "SG"),
    NEW_YORK("Нью-Йорк, US", "New York, US", 76, "US");

    companion object {
        fun safeValueOf(raw: String?): VpnRegion =
            entries.firstOrNull { it.name == raw } ?: AMSTERDAM
    }
}

enum class AntivirusProvider(val labelRu: String, val labelEn: String, val packageHint: String) {
    NONE("Выключен", "Disabled", ""),
    KASPERSKY("Kaspersky Shield", "Kaspersky Shield", "com.kms.free"),
    DR_WEB("Dr.Web Link Guard", "Dr.Web Link Guard", "com.drweb"),
    BOTH("Kaspersky + Dr.Web", "Kaspersky + Dr.Web", "com.kms.free");

    companion object {
        fun safeValueOf(raw: String?): AntivirusProvider =
            entries.firstOrNull { it.name == raw } ?: KASPERSKY
    }
}

enum class SearchEngine(val displayName: String, val searchUrlTemplate: String) {
    GOOGLE("Google", "https://www.google.com/search?q="),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q="),
    BING("Bing", "https://www.bing.com/search?q="),
    YANDEX("Яндекс", "https://yandex.ru/search/?text="),
    STARTPAGE("Startpage", "https://www.startpage.com/sp/search?query=");

    companion object {
        fun safeValueOf(raw: String?): SearchEngine =
            entries.firstOrNull { it.name == raw } ?: GOOGLE
    }
}

enum class AppLanguage(val label: String) {
    RU("Русский"),
    EN("English"),
    KK("Қазақша"),
    ES("Español"),
    DE("Deutsch"),
    FR("Français");

    companion object {
        fun safeValueOf(raw: String?): AppLanguage =
            entries.firstOrNull { it.name == raw } ?: RU
    }
}

enum class RedAiStyle(val labelRu: String, val labelEn: String) {
    CONCISE("Кратко", "Concise"),
    DETAILED("Подробно", "Detailed"),
    TECHNICAL("Технически", "Technical");

    companion object {
        fun safeValueOf(raw: String?): RedAiStyle =
            entries.firstOrNull { it.name == raw } ?: CONCISE
    }
}

enum class MercuryAiModel(
    val modelId: String,
    val labelRu: String,
    val labelEn: String,
    val badgeRu: String,
    val badgeEn: String
) {
    MERCURY_38_FLASH(
        modelId = "gemini-3.8-flash",
        labelRu = "Mercury 3.8 Flash",
        labelEn = "Mercury 3.8 Flash",
        badgeRu = "Основная · Gemini 3.8",
        badgeEn = "Default · Gemini 3.8"
    ),
    MERCURY_35_LITE(
        modelId = "gemini-3.5-flash-lite",
        labelRu = "Mercury 3.5 Lite",
        labelEn = "Mercury 3.5 Lite",
        badgeRu = "Молниеносная · Lite",
        badgeEn = "Ultra-fast · Lite"
    ),
    MERCURY_4_ARGON(
        modelId = "gemini-4-argon",
        labelRu = "Mercury 4 Argon",
        labelEn = "Mercury 4 Argon",
        badgeRu = "Профи STEM · Blaze",
        badgeEn = "Flagship STEM · Blaze"
    );

    companion object {
        fun safeValueOf(raw: String?): MercuryAiModel =
            entries.firstOrNull { it.modelId == raw || it.name == raw } ?: MERCURY_38_FLASH
    }
}

data class PendingNAccountRegistration(
    val naccountHandle: String,
    val displayName: String,
    val vkHandle: String? = null,
    val protonHandle: String? = null,
    val telegramHandle: String? = null,
    val cloudSyncEnabled: Boolean = true,
    val syncBookmarks: Boolean = true,
    val syncSettings: Boolean = true,
    val syncHistory: Boolean = true
)

enum class SettingsCategory {
    ACCOUNT,
    APPEARANCE,
    SECURITY,
    GAMING_PERFORMANCE,
    RED_AI,
    SEARCH_ENGINE,
    LANGUAGES,
    NOTIFICATIONS
}

@Keep
@IgnoreExtraProperties
data class NAccountProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val naccountHandle: String = "@orbit_user",
    val displayName: String = "Orbit User",
    val email: String? = null,
    val linkedProviders: List<String> = listOf("GOOGLE"),
    val vkHandle: String? = null,
    val protonHandle: String? = null,
    val telegramHandle: String? = null,
    val cloudSyncEnabled: Boolean = true,
    val syncBookmarks: Boolean = true,
    val syncSettings: Boolean = true,
    val syncHistory: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class OrbitSettings(
    @DocumentId val id: String = "",
    val userId: String = "",
    val styleMode: String = BrowserStyleMode.LIQUID_GLASS.name,
    val themeBrightness: String = ThemeBrightness.LIGHT.name,
    val colorPalette: String = ColorPalettePreset.ORBIT_CYAN.name,
    val wallpaperStyle: String = WallpaperStyle.NONE.name,
    val homeLayout: String = HomeLayoutStyle.CLASSIC.name,
    val dockStyle: String = OrbitDockStyle.SAFARI_CAPSULE.name,
    val monarchG2EngineEnabled: Boolean = true,
    val iconTheme: String = OrbitIconTheme.AUTO_THEME.name,
    val glassTransparency: String = GlassTransparencyLevel.FROSTED.name,
    val animationStyle: String = OrbitAnimationStyle.FLUID_SPRING.name,
    val cornerStyle: String = OrbitCornerStyle.ROUNDED.name,
    val starfieldParticlesEnabled: Boolean = true,
    val expressiveShapeStyle: String = ExpressiveShapeStyle.AUTO_MIX.name,
    val homeLogoScale: String = OrbitLogoScale.MEDIUM.name,
    val themedIconsAdaptApp: Boolean = true,
    val compactStatusPills: Boolean = true,
    val showFavorites: Boolean = true,
    val showPrivacyReport: Boolean = true,
    val showRedAiBar: Boolean = true,
    val showCosmosWidget: Boolean = true,
    val showQuickNotesWidget: Boolean = false,
    val quickNotesText: String = "",
    val showGamingTelemetry: Boolean = false,
    val adBlockEnabled: Boolean = true,
    val antiTrackingEnabled: Boolean = true,
    val httpsOnlyEnabled: Boolean = true,
    val webRtcLeakProtection: Boolean = true,
    val canvasFingerprintProtection: Boolean = true,
    val doNotTrackEnabled: Boolean = true,
    val cookieAutoPurgeEnabled: Boolean = true,
    val vpnEnabled: Boolean = false,
    val vpnRegion: String = VpnRegion.AMSTERDAM.name,
    val antivirusProvider: String = AntivirusProvider.KASPERSKY.name,
    val gamingRgbEnabled: Boolean = true,
    val gamingHapticsEnabled: Boolean = true,
    val performanceModeEnabled: Boolean = false,
    val ramLimitMb: Long = 2048L,
    val tabHibernationEnabled: Boolean = true,
    val searchEngine: String = SearchEngine.GOOGLE.name,
    val appLanguage: String = AppLanguage.RU.name,
    val redAiAutoSummarize: Boolean = false,
    val redAiSafeLinkScan: Boolean = true,
    val redAiStyle: String = RedAiStyle.CONCISE.name,
    val mercuryAiModel: String = MercuryAiModel.MERCURY_38_FLASH.modelId,
    val notifyBlockedTrackers: Boolean = true,
    val notifyCloudSync: Boolean = true,
    val notifySecurityAlerts: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val customAccentHex: Long = 0L,
    val customSecondaryHex: Long = 0L,
    val customCornerRadiusDp: Long = 20L,
    val borderGlowIntensity: Long = 50L,
    val glassBlurLevel: Long = 50L,
    val customIconUri: String = "",
    val dnsOverHttpsEnabled: Boolean = true,
    val blockPopupsEnabled: Boolean = true,
    val antiPhishingShieldEnabled: Boolean = true,
    val floatingSpeedHudEnabled: Boolean = false,
    val adsBlockedTotal: Long = 0L,
    val trackersBlockedTotal: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val resolvedStyleMode: BrowserStyleMode get() = BrowserStyleMode.safeValueOf(styleMode)
    val resolvedBrightness: ThemeBrightness get() = ThemeBrightness.safeValueOf(themeBrightness)
    val resolvedPalette: ColorPalettePreset get() = ColorPalettePreset.safeValueOf(colorPalette)
    val resolvedWallpaper: WallpaperStyle get() = WallpaperStyle.safeValueOf(wallpaperStyle)
    val resolvedLayout: HomeLayoutStyle get() = HomeLayoutStyle.safeValueOf(homeLayout)
    val resolvedDockStyle: OrbitDockStyle get() = OrbitDockStyle.safeValueOf(dockStyle)
    val resolvedIconTheme: OrbitIconTheme get() = OrbitIconTheme.safeValueOf(iconTheme)
    val resolvedGlassTransparency: GlassTransparencyLevel get() = GlassTransparencyLevel.safeValueOf(glassTransparency)
    val resolvedAnimationStyle: OrbitAnimationStyle get() = OrbitAnimationStyle.safeValueOf(animationStyle)
    val resolvedCornerStyle: OrbitCornerStyle get() = OrbitCornerStyle.safeValueOf(cornerStyle)
    val resolvedExpressiveShapeStyle: ExpressiveShapeStyle get() = ExpressiveShapeStyle.safeValueOf(expressiveShapeStyle)
    val resolvedHomeLogoScale: OrbitLogoScale get() = OrbitLogoScale.safeValueOf(homeLogoScale)
    val resolvedVpnRegion: VpnRegion get() = VpnRegion.safeValueOf(vpnRegion)
    val resolvedAntivirus: AntivirusProvider get() = AntivirusProvider.safeValueOf(antivirusProvider)
    val resolvedSearchEngine: SearchEngine get() = SearchEngine.safeValueOf(searchEngine)
    val resolvedLanguage: AppLanguage get() = AppLanguage.safeValueOf(appLanguage)
    val resolvedRedAiStyle: RedAiStyle get() = RedAiStyle.safeValueOf(redAiStyle)
    val resolvedMercuryModel: MercuryAiModel get() = MercuryAiModel.safeValueOf(mercuryAiModel)
}

@Keep
@IgnoreExtraProperties
data class FavoriteSite(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val url: String = "",
    val iconBadge: String = "WEB",
    val accentHex: Long = 0xFF00D4FFL,
    val sortOrder: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class HistoryEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val url: String = "",
    @ServerTimestamp val visitedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SyncedTab(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "Orbit Start",
    val url: String = "orbit://home",
    val incognito: Boolean = false,
    val sortOrder: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class BrowserTab(
    val id: String,
    val title: String = "Orbit",
    val url: String = "orbit://home",
    val isIncognito: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isHibernated: Boolean = false,
    val desktopMode: Boolean = false,
    val readerMode: Boolean = false
)

data class RedAiMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val isStreaming: Boolean = false,
    val modelLabel: String = "Mercury 3.8"
)

data class BlockedEvent(
    val host: String,
    val category: String,
    val timestampMs: Long
)

data class DeviceTelemetry(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val usedRamMb: Long,
    val appHeapMb: Long,
    val activeTabsCount: Int,
    val hibernatedTabsCount: Int
)

data class AntivirusScanResult(
    val url: String,
    val providerLabel: String,
    val isSafe: Boolean,
    val verdict: String,
    val scannedAtMs: Long
)

data class OrbitToastNotification(
    val id: Long,
    val title: String,
    val subtitle: String,
    val isAlert: Boolean = false
)

data class DownloadItem(
    val id: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val downloadedBytes: Long,
    val isComplete: Boolean,
    val isPaused: Boolean = false,
    val fileExtension: String = "pdf",
    val sourceUrl: String = ""
)

data class CosmosAstronomyFact(
    val titleRu: String,
    val titleEn: String,
    val descriptionRu: String,
    val descriptionEn: String,
    val celestialBody: String,
    val distanceLightYears: String,
    val tag: String
)
