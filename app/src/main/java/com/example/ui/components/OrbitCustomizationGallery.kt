package com.example.ui.components

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserStyleMode
import com.example.model.ColorPalettePreset
import com.example.model.ExpressiveShapeStyle
import com.example.model.HomeLayoutStyle
import com.example.model.OrbitIconTheme
import com.example.model.OrbitSettings
import com.example.model.WallpaperStyle
import com.example.ui.theme.LocalOrbitTokens
import com.example.ui.theme.SpaceGroteskFamily
import com.example.util.OrbitIconManager
import com.example.util.OrbitSoundPlayer

private val GALLERY_RAINBOW = listOf(
    Color(0xFFFF3B30),
    Color(0xFFFF9500),
    Color(0xFFFFCC00),
    Color(0xFF34C759),
    Color(0xFF00C7BE),
    Color(0xFF007AFF),
    Color(0xFFAF52DE)
)

data class OrbitStyleBundle(
    val id: String,
    val titleRu: String,
    val titleEn: String,
    val subtitleRu: String,
    val subtitleEn: String,
    val styleMode: BrowserStyleMode,
    val palette: ColorPalettePreset,
    val wallpaper: WallpaperStyle,
    val iconTheme: OrbitIconTheme,
    val badgeColorHex: Long
)

private val STYLE_BUNDLES_2_0 = listOf(
    OrbitStyleBundle(
        id = "jupiter_gx",
        titleRu = "JupiterGX Cyber",
        titleEn = "JupiterGX Cyber",
        subtitleRu = "Полосы Юпитера · Неон & ОЗУ",
        subtitleEn = "Jupiter Rings · Neon & RAM",
        styleMode = BrowserStyleMode.GAMING_GX,
        palette = ColorPalettePreset.JUPITER_CYBER,
        wallpaper = WallpaperStyle.JUPITER_VORTEX,
        iconTheme = OrbitIconTheme.JUPITER_GX,
        badgeColorHex = 0xFFFF1E56
    ),
    OrbitStyleBundle(
        id = "mercury_quicksilver",
        titleRu = "Ртуть MercuryAI",
        titleEn = "Mercury Quicksilver",
        subtitleRu = "Жидкая ртуть · Алая искра",
        subtitleEn = "Liquid Silver · Crimson Flare",
        styleMode = BrowserStyleMode.LIQUID_GLASS,
        palette = ColorPalettePreset.MERCURY_QUICKSILVER,
        wallpaper = WallpaperStyle.MERCURY_LIQUID,
        iconTheme = OrbitIconTheme.MERCURY_CORE,
        badgeColorHex = 0xFFFF3E66
    ),
    OrbitStyleBundle(
        id = "nothing_glyph",
        titleRu = "NothingOS 3.0",
        titleEn = "NothingOS 3.0",
        subtitleRu = "Глиф-матрица и монохром",
        subtitleEn = "Glyph Matrix & Dot Mono",
        styleMode = BrowserStyleMode.NOTHING_OS,
        palette = ColorPalettePreset.MOON_FROST,
        wallpaper = WallpaperStyle.CYBER_GRID,
        iconTheme = OrbitIconTheme.PURE_WHITE,
        badgeColorHex = 0xFFFF3B30
    ),
    OrbitStyleBundle(
        id = "kosmos_deep",
        titleRu = "Космос Одиссея",
        titleEn = "Kosmos Odyssey",
        subtitleRu = "Туманность Андромеды + Звёзды",
        subtitleEn = "Andromeda Nebula + Starlight",
        styleMode = BrowserStyleMode.KOSMOS,
        palette = ColorPalettePreset.ANDROMEDA_NEBULA,
        wallpaper = WallpaperStyle.KOSMOS_NEBULA,
        iconTheme = OrbitIconTheme.VIOLET_NEBULA,
        badgeColorHex = 0xFF8B5CF6
    ),
    OrbitStyleBundle(
        id = "sakura_silk",
        titleRu = "Сакура Шёлк",
        titleEn = "Sakura Silk",
        subtitleRu = "Лепестки и мягкое стекло",
        subtitleEn = "Blossom Petals & Soft Glass",
        styleMode = BrowserStyleMode.SAKURA,
        palette = ColorPalettePreset.SAKURA_ROSE,
        wallpaper = WallpaperStyle.SAKURA_SILK,
        iconTheme = OrbitIconTheme.SAKURA_BLOSSOM,
        badgeColorHex = 0xFFFF6B9D
    ),
    OrbitStyleBundle(
        id = "m3_expressive",
        titleRu = "Material You 3.0",
        titleEn = "Material You 3.0",
        subtitleRu = "Frosted Glass и живые формы",
        subtitleEn = "Frosted Glass & Living Shapes",
        styleMode = BrowserStyleMode.M3_EXPRESSIVE,
        palette = ColorPalettePreset.EXPRESSIVE_VIOLET,
        wallpaper = WallpaperStyle.MINT_MEADOW,
        iconTheme = OrbitIconTheme.AUTO_THEME,
        badgeColorHex = 0xFF9C6BFF
    ),
    OrbitStyleBundle(
        id = "minecraft_block",
        titleRu = "Minecraft 8-Bit",
        titleEn = "Minecraft 8-Bit",
        subtitleRu = "Блочный неон и кибер-лайм",
        subtitleEn = "Blocky Neon & Cyber Lime",
        styleMode = BrowserStyleMode.MINECRAFT,
        palette = ColorPalettePreset.CYBER_LIME,
        wallpaper = WallpaperStyle.OBSIDIAN_MESH,
        iconTheme = OrbitIconTheme.EMERALD_PULSE,
        badgeColorHex = 0xFF4CAF50
    )
)

/**
 * 0. Новые готовые сборки 2.0 (1-тап применение полного стиля: Режим + Палитра + Обои + Иконка).
 */
@Composable
fun QuickStyleBundlesCarousel(
    settings: OrbitSettings,
    isRu: Boolean,
    onApplyBundle: (OrbitStyleBundle) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalOrbitTokens.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("style_bundles_2_0"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isRu) "Готовые сборки 2.0 (1-тап)" else "Style Bundles 2.0 (1-Tap)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = tokens.accentPrimary
            )
            Text(
                text = if (isRu) "Новое" else "NEW",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF3E66)
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(STYLE_BUNDLES_2_0, key = { it.id }) { bundle ->
                val isBundleActive = settings.resolvedStyleMode == bundle.styleMode &&
                    settings.resolvedPalette == bundle.palette &&
                    settings.resolvedWallpaper == bundle.wallpaper
                val badgeColor = Color(bundle.badgeColorHex)
                Surface(
                    shape = orbitContainerShape(18.dp, bundle.styleMode),
                    color = if (isBundleActive) badgeColor.copy(alpha = 0.22f) else tokens.glassSurfaceElevated,
                    border = BorderStroke(
                        width = if (isBundleActive) 1.8.dp else 1.dp,
                        color = if (isBundleActive) badgeColor else tokens.glassBorder
                    ),
                    modifier = Modifier
                        .width(172.dp)
                        .orbitBouncyClick { onApplyBundle(bundle) }
                        .testTag("bundle_card_${bundle.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(badgeColor.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            when (bundle.id) {
                                "jupiter_gx" -> JupiterGxLogoEmblem(size = 28.dp)
                                "mercury_quicksilver" -> MercuryAiLogoEmblem(size = 28.dp)
                                else -> OrbitLogoEmblem(
                                    size = 28.dp,
                                    animateRing = false,
                                    iconTheme = bundle.iconTheme,
                                    styleMode = bundle.styleMode
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isRu) bundle.titleRu else bundle.titleEn,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isBundleActive) badgeColor else tokens.onCleanBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isRu) bundle.subtitleRu else bundle.subtitleEn,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = tokens.secondaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 1. Карточки режимов интерфейса:
 * Вверху каждой карточки — живое лого и превью оформления,
 * Внизу — Имя и Применение.
 */
@Composable
fun InterfaceModeCardsGrid(
    selectedMode: BrowserStyleMode,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onSelectMode: (BrowserStyleMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = BrowserStyleMode.entries.chunked(2)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { mode ->
                    InterfaceModeCard(
                        mode = mode,
                        selected = mode == selectedMode,
                        iconTheme = iconTheme,
                        isRu = isRu,
                        onClick = { onSelectMode(mode) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun InterfaceModeCard(
    mode: BrowserStyleMode,
    selected: Boolean,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalOrbitTokens.current
    val cardShape = when (mode) {
        BrowserStyleMode.GAMING_GX -> CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp, topEnd = 4.dp, bottomStart = 4.dp)
        BrowserStyleMode.M3_EXPRESSIVE -> RoundedCornerShape(topStart = 24.dp, topEnd = 14.dp, bottomEnd = 24.dp, bottomStart = 14.dp)
        BrowserStyleMode.MINIMALIST -> RoundedCornerShape(12.dp)
        BrowserStyleMode.LIQUID_GLASS -> RoundedCornerShape(20.dp)
        BrowserStyleMode.NOTHING_OS -> RoundedCornerShape(10.dp)
        BrowserStyleMode.KOSMOS -> RoundedCornerShape(20.dp)
        BrowserStyleMode.MINECRAFT -> CutCornerShape(3.dp)
        BrowserStyleMode.SAKURA -> RoundedCornerShape(topStart = 22.dp, topEnd = 10.dp, bottomEnd = 22.dp, bottomStart = 10.dp)
    }

    val borderColor = if (selected) tokens.accentPrimary else tokens.glassBorder

    Surface(
        shape = cardShape,
        color = if (mode == BrowserStyleMode.M3_EXPRESSIVE) tokens.glassSurfaceElevated else tokens.glassSurface,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        modifier = modifier
            .orbitBouncyClick(onClick = onClick)
            .testTag("style_mode_card_${mode.name.lowercase()}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top area: Preview Logo + Visual Style Texture
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .background(
                        when (mode) {
                            BrowserStyleMode.LIQUID_GLASS -> Brush.linearGradient(
                                listOf(
                                    Color(0xFF0EA5E9).copy(alpha = 0.30f),
                                    Color(0xFF8B5CF6).copy(alpha = 0.24f)
                                )
                            )
                            BrowserStyleMode.M3_EXPRESSIVE -> Brush.linearGradient(
                                listOf(
                                    tokens.accentPrimary.copy(alpha = 0.22f),
                                    tokens.accentSecondary.copy(alpha = 0.28f)
                                )
                            )
                            BrowserStyleMode.GAMING_GX -> Brush.linearGradient(
                                listOf(Color(0xFF1E0918), Color(0xFF0A0B14))
                            )
                            BrowserStyleMode.MINIMALIST -> Brush.linearGradient(
                                if (tokens.isDark) listOf(Color(0xFF141820), Color(0xFF1C212B))
                                else listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                            )
                            BrowserStyleMode.NOTHING_OS -> Brush.linearGradient(
                                listOf(Color(0xFF060606), Color(0xFF181818))
                            )
                            BrowserStyleMode.KOSMOS -> Brush.linearGradient(
                                listOf(Color(0xFF280B4D), Color(0xFF080214))
                            )
                            BrowserStyleMode.MINECRAFT -> Brush.linearGradient(
                                listOf(Color(0xFF1F401C), Color(0xFF0E1D0C))
                            )
                            BrowserStyleMode.SAKURA -> Brush.linearGradient(
                                if (tokens.isDark) listOf(Color(0xFF3E1025), Color(0xFF230815))
                                else listOf(Color(0xFFFFD6E4), Color(0xFFFF9EBB))
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Decorative mode-specific accents inside preview
                when (mode) {
                    BrowserStyleMode.M3_EXPRESSIVE -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = tokens.accentPrimary.copy(alpha = 0.22f),
                                radius = size.minDimension * 0.34f,
                                center = Offset(size.width * 0.16f, size.height * 0.25f)
                            )
                            drawRoundRect(
                                color = tokens.accentSecondary.copy(alpha = 0.24f),
                                topLeft = Offset(size.width * 0.70f, size.height * 0.48f),
                                size = Size(size.minDimension * 0.45f, size.minDimension * 0.45f),
                                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
                            )
                        }
                    }
                    BrowserStyleMode.LIQUID_GLASS -> {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = if (tokens.isDark) 0.12f else 0.36f))
                                .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                        )
                    }
                    BrowserStyleMode.NOTHING_OS -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val step = 12.dp.toPx()
                            var y = step
                            while (y < size.height) {
                                var x = step
                                while (x < size.width) {
                                    drawCircle(Color.White.copy(alpha = 0.10f), 1.dp.toPx(), Offset(x, y))
                                    x += step
                                }
                                y += step
                            }
                            drawCircle(Color(0xFFFF3B30), 4.dp.toPx(), Offset(size.width * 0.14f, size.height * 0.24f))
                        }
                    }
                    BrowserStyleMode.KOSMOS -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(Color(0xFF00E5FF).copy(alpha = 0.55f), 2.dp.toPx(), Offset(size.width * 0.18f, size.height * 0.28f))
                            drawCircle(Color(0xFFFDE047).copy(alpha = 0.65f), 2.4.dp.toPx(), Offset(size.width * 0.82f, size.height * 0.22f))
                            drawCircle(Color(0xFFEC4899).copy(alpha = 0.45f), 1.8.dp.toPx(), Offset(size.width * 0.72f, size.height * 0.76f))
                        }
                    }
                    BrowserStyleMode.MINECRAFT -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val b = 10.dp.toPx()
                            drawRect(Color(0xFF4CAF50).copy(alpha = 0.26f), Offset(10f, 10f), Size(b, b))
                            drawRect(Color(0xFF22D3EE).copy(alpha = 0.24f), Offset(size.width - b * 2, size.height - b * 1.6f), Size(b, b))
                        }
                    }
                    BrowserStyleMode.SAKURA -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(Color(0xFFFF6B9D).copy(alpha = 0.30f), 8.dp.toPx(), Offset(size.width * 0.18f, size.height * 0.28f))
                            drawCircle(Color(0xFFFFFFFF).copy(alpha = 0.45f), 5.dp.toPx(), Offset(size.width * 0.78f, size.height * 0.72f))
                        }
                    }
                    else -> {}
                }

                // Centered Logo Emblem + "Orbit" preview
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (mode == BrowserStyleMode.GAMING_GX) {
                        JupiterGxLogoEmblem(size = 34.dp)
                    } else {
                        OrbitLogoEmblem(
                            size = 34.dp,
                            animateRing = false,
                            iconTheme = iconTheme,
                            styleMode = mode
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (mode == BrowserStyleMode.NOTHING_OS) "ORBIT" else "Orbit",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = if (mode == BrowserStyleMode.NOTHING_OS) 2.sp else 0.sp,
                        color = when {
                            mode == BrowserStyleMode.GAMING_GX ||
                                mode == BrowserStyleMode.NOTHING_OS ||
                                mode == BrowserStyleMode.KOSMOS -> Color.White
                            mode == BrowserStyleMode.MINECRAFT -> Color(0xFFC8E6C9)
                            mode == BrowserStyleMode.SAKURA && !tokens.isDark -> Color(0xFF4A0E2E)
                            mode == BrowserStyleMode.MINIMALIST && !tokens.isDark -> Color(0xFF0F172A)
                            tokens.isDark -> Color.White
                            else -> tokens.onCleanBackground
                        }
                    )
                }

                if (selected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(tokens.accentPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Selected",
                            tint = Color(0xFF041018),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Bottom area: Name + Application / Use-case ("имя и применение")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = if (isRu) mode.labelRu else mode.labelEn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) tokens.accentPrimary else tokens.onCleanBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isRu) mode.usageRu else mode.usageEn,
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.secondaryText,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 2. Линия для тем (ColorPalettePreset):
 * Слева — цвет иконок и сам цвет оформления, справа от них — надпись названия темы.
 */
@Composable
fun ThemeColorLinesList(
    selectedPalette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onSelectPalette: (ColorPalettePreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalOrbitTokens.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ColorPalettePreset.entries.forEach { preset ->
            val selected = preset == selectedPalette
            val themePrimary = Color(preset.primaryHex)
            val themeSecondary = Color(preset.secondaryHex)
            val effectiveIconColor = when (iconTheme) {
                OrbitIconTheme.AUTO_THEME -> themePrimary
                OrbitIconTheme.PURE_WHITE -> Color.White
                else -> Color(iconTheme.primaryHex)
            }

            OrbitGlassSurface(
                shape = orbitContainerShape(16.dp),
                elevated = selected,
                modifier = Modifier
                    .fillMaxWidth()
                    .orbitBouncyClick { onSelectPalette(preset) }
                    .testTag("theme_line_${preset.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left 1: Icon Color preview badge
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                if (iconTheme == OrbitIconTheme.RAINBOW_COMPASS) {
                                    Brush.sweepGradient(GALLERY_RAINBOW)
                                } else {
                                    Brush.linearGradient(
                                        listOf(
                                            effectiveIconColor.copy(alpha = 0.24f),
                                            effectiveIconColor.copy(alpha = 0.14f)
                                        )
                                    )
                                }
                            )
                            .border(
                                1.2.dp,
                                if (iconTheme == OrbitIconTheme.PURE_WHITE) Color(0xFF94A3B8) else effectiveIconColor.copy(alpha = 0.7f),
                                RoundedCornerShape(9.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (iconTheme == OrbitIconTheme.RAINBOW_COMPASS) Icons.Rounded.Explore else Icons.Rounded.Public,
                            contentDescription = null,
                            tint = if (iconTheme == OrbitIconTheme.RAINBOW_COMPASS) Color.White else effectiveIconColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Left 2: Theme Appearance Color pill (цвет оформления)
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Brush.linearGradient(listOf(themePrimary, themeSecondary)))
                            .border(
                                width = if (selected) 1.5.dp else 0.8.dp,
                                color = Color.White.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(9.dp)
                            )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Label (надпись)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isRu) preset.labelRu else preset.labelEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) themePrimary else tokens.onCleanBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (selected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(themePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFF061018),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class ShowcaseIconItem(
    val titleRu: String,
    val titleEn: String,
    val icon: ImageVector,
    val isOrbitEmblem: Boolean = false
)

private val ICON_SHOWCASE_4X4 = listOf(
    ShowcaseIconItem("Orbit", "Orbit", Icons.Rounded.Public, isOrbitEmblem = true),
    ShowcaseIconItem("Поиск", "Search", Icons.Rounded.Search),
    ShowcaseIconItem("Вкладки", "Tabs", Icons.Rounded.Layers),
    ShowcaseIconItem("MercuryAI", "MercuryAI", Icons.Rounded.AutoAwesome),
    ShowcaseIconItem("Щит", "Shield", Icons.Rounded.Shield),
    ShowcaseIconItem("VPN", "VPN", Icons.Rounded.VpnKey),
    ShowcaseIconItem("Радар", "Radar", Icons.Rounded.Radar),
    ShowcaseIconItem("JupiterGX", "JupiterGX", Icons.Rounded.SportsEsports),
    ShowcaseIconItem("NAccount", "Account", Icons.Rounded.Person),
    ShowcaseIconItem("История", "History", Icons.Rounded.History),
    ShowcaseIconItem("Загрузки", "Downloads", Icons.Rounded.Download),
    ShowcaseIconItem("Закладки", "Bookmarks", Icons.Rounded.Bookmark),
    ShowcaseIconItem("Чтение", "Reader", Icons.Rounded.MenuBook),
    ShowcaseIconItem("Космос", "Cosmos", Icons.Rounded.Explore),
    ShowcaseIconItem("Голос", "Voice", Icons.Rounded.Mic),
    ShowcaseIconItem("Темы", "Themes", Icons.Rounded.Palette)
)

/**
 * 3. Темы иконок + Макет показа 4x4 с надписями иконок:
 * Переключение стилей иконок (По теме, GX Красный, Белый лёд, Радужный Компас и др.)
 * и живая сетка 4×4 (16 иконок с подписями) под выбранную тему.
 */
@Composable
fun IconTheme4x4Showcase(
    selectedIconTheme: OrbitIconTheme,
    styleMode: BrowserStyleMode,
    isRu: Boolean,
    onSelectIconTheme: (OrbitIconTheme) -> Unit,
    modifier: Modifier = Modifier,
    customIconUri: String = "",
    customAccentHex: Long = 0L,
    customSecondaryHex: Long = 0L,
    onUpdateCustomIconUri: (String) -> Unit = {}
) {
    val tokens = LocalOrbitTokens.current
    val isRainbow = selectedIconTheme == OrbitIconTheme.RAINBOW_COMPASS
    val isPureWhite = selectedIconTheme == OrbitIconTheme.PURE_WHITE
    val isGxRed = selectedIconTheme == OrbitIconTheme.GX_CRIMSON ||
        (selectedIconTheme == OrbitIconTheme.AUTO_THEME && styleMode == BrowserStyleMode.GAMING_GX)

    val (iconPrimary, iconSecondary) = when {
        isGxRed -> Color(0xFFFF1E56) to Color(0xFFFF6B6B)
        isPureWhite -> Color(0xFFFFFFFF) to Color(0xFFCBD5E1)
        selectedIconTheme == OrbitIconTheme.AUTO_THEME -> tokens.accentPrimary to tokens.accentSecondary
        else -> Color(selectedIconTheme.primaryHex) to Color(selectedIconTheme.secondaryHex)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 3-Column Visual Gallery of ALL Orbit Logos for Different Themes
        val logoRows = OrbitIconTheme.entries.chunked(3)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("orbit_logos_theme_grid"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            logoRows.forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { logoItem ->
                        val active = logoItem == selectedIconTheme
                        val itemAccent = Color(logoItem.primaryHex)
                        Surface(
                            shape = orbitContainerShape(16.dp, styleMode),
                            color = if (active) itemAccent.copy(alpha = 0.16f) else tokens.glassSurface,
                            border = BorderStroke(
                                width = if (active) 1.8.dp else 1.dp,
                                color = if (active) {
                                    if (logoItem.isRainbow) Color(0xFFEC4899) else itemAccent
                                } else {
                                    tokens.glassBorder
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .orbitBouncyClick { onSelectIconTheme(logoItem) }
                                .testTag("orbit_logo_card_${logoItem.name.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(Color(0xFF141B2D), Color(0xFF070A12))
                                            )
                                        )
                                        .border(
                                            width = 0.8.dp,
                                            color = if (logoItem.isRainbow) Color(0xFFEC4899).copy(alpha = 0.6f)
                                            else itemAccent.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    OrbitLogoEmblem(
                                        size = 34.dp,
                                        animateRing = active,
                                        iconTheme = logoItem,
                                        styleMode = styleMode
                                    )
                                }
                                Text(
                                    text = if (isRu) logoItem.labelRu else logoItem.labelEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (active) {
                                        if (logoItem == OrbitIconTheme.PURE_WHITE && !tokens.isDark) tokens.onCleanBackground
                                        else if (logoItem.isRainbow) Color(0xFFEC4899)
                                        else itemAccent
                                    } else {
                                        tokens.onCleanBackground
                                    },
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isRu) logoItem.motifRu else logoItem.motifEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    lineHeight = 11.sp,
                                    color = tokens.secondaryText,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    repeat(3 - pair.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        val context = LocalContext.current
        var activePreset by remember { mutableStateOf(OrbitIconManager.getActivePreset(context)) }
        val renderedPreviewBitmap: Bitmap? = remember(
            selectedIconTheme,
            styleMode,
            customIconUri,
            customAccentHex,
            customSecondaryHex
        ) {
            runCatching {
                OrbitIconManager.renderOrbitIconBitmap(
                    context = context,
                    iconTheme = selectedIconTheme,
                    styleMode = styleMode,
                    customPhotoUri = customIconUri.takeIf { it.isNotBlank() },
                    customPrimaryHex = customAccentHex,
                    customSecondaryHex = customSecondaryHex,
                    sizePx = 192
                )
            }.getOrNull()
        }

        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                runCatching {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                onUpdateCustomIconUri(uri.toString())
                OrbitSoundPlayer.playSuccess()
                Toast.makeText(
                    context,
                    if (isRu) "Своя иконка выбрана! Нажмите кнопку ниже, чтобы закрепить на экран."
                    else "Custom icon selected! Tap below to pin to home screen.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Custom Photo Icon + Instant Adaptive Preview Strip
        OrbitGlassSurface(
            shape = orbitContainerShape(18.dp, styleMode),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (renderedPreviewBitmap != null) {
                        Image(
                            bitmap = renderedPreviewBitmap.asImageBitmap(),
                            contentDescription = "Adaptive Icon Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.2.dp, tokens.accentPrimary.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isRu) "Своя иконка & Themed Icon" else "Custom Icon & Adaptive Launcher",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = tokens.onCleanBackground
                        )
                        Text(
                            text = if (customIconUri.isNotBlank()) {
                                if (isRu) "Установлено своё фото" else "Custom photo active"
                            } else {
                                if (isRu) "Адаптивная иконка Android 13+ (Themed Icons)" else "Android 13+ Themed Icon Ready"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.secondaryText
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = orbitContainerShape(14.dp, styleMode),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentSecondary.copy(alpha = 0.18f),
                            contentColor = tokens.accentSecondary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pick_custom_icon_button")
                    ) {
                        Icon(Icons.Rounded.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRu) "Своя иконка из галереи" else "Pick Custom Icon",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (customIconUri.isNotBlank()) {
                        FilterChip(
                            selected = false,
                            onClick = { onUpdateCustomIconUri("") },
                            label = {
                                Text(if (isRu) "Сброс" else "Reset")
                            },
                            leadingIcon = {
                                Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }

                // Launcher Icon Alias Presets (actually changes the app's home-screen icon preset!)
                Text(
                    text = if (isRu) "Предустановленные иконки лаунчера:" else "Launcher Icon Presets:",
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.secondaryText
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(OrbitIconManager.LauncherPreset.entries) { preset ->
                        FilterChip(
                            selected = activePreset == preset,
                            onClick = {
                                val applied = OrbitIconManager.applyLauncherIconPreset(context, preset)
                                if (applied) {
                                    activePreset = preset
                                    OrbitSoundPlayer.playSuccess()
                                    Toast.makeText(
                                        context,
                                        if (isRu) "Иконка приложения переключена: ${preset.labelRu}"
                                        else "Launcher icon switched: ${preset.labelEn}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            label = {
                                Text(
                                    text = if (isRu) preset.labelRu else preset.labelEn,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                val res = OrbitIconManager.pinIconToHomeScreen(
                    context = context,
                    label = if (isRu) selectedIconTheme.labelRu else selectedIconTheme.labelEn,
                    iconTheme = selectedIconTheme,
                    styleMode = styleMode,
                    customPhotoUri = customIconUri.takeIf { it.isNotBlank() },
                    customPrimaryHex = customAccentHex,
                    customSecondaryHex = customSecondaryHex
                )
                when (res) {
                    OrbitIconManager.PinResult.DISPATCHED -> {
                        OrbitSoundPlayer.playSuccess()
                        Toast.makeText(
                            context,
                            if (isRu) "Иконка мгновенно отправлена на главный экран!" else "Adaptive icon pinned to home screen!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    OrbitIconManager.PinResult.UNSUPPORTED -> {
                        Toast.makeText(
                            context,
                            if (isRu) "Лаунчер не поддерживает закрепление ярлыков" else "Launcher pinning unsupported",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    OrbitIconManager.PinResult.FAILED -> {
                        OrbitSoundPlayer.playError()
                        Toast.makeText(
                            context,
                            if (isRu) "Не удалось создать ярлык" else "Could not pin shortcut",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("apply_icon_to_home_screen"),
            shape = orbitContainerShape(16.dp, styleMode),
            colors = ButtonDefaults.buttonColors(
                containerColor = tokens.accentPrimary,
                contentColor = Color(0xFF061018)
            ),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Icon(Icons.Rounded.PhoneAndroid, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isRu) "Прикрепить иконку на главный экран" else "Pin Icon to Phone Home Screen",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 4x4 Grid Showcase Container
        OrbitGlassSurface(
            shape = orbitContainerShape(22.dp),
            elevated = true,
            gamingRgbGlow = isGxRed || styleMode == BrowserStyleMode.GAMING_GX,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("icon_showcase_4x4")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRu) "Предпросмотр иконок 4×4" else "4×4 Icon Pack Preview",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = tokens.onCleanBackground
                    )
                    Text(
                        text = if (isRu) selectedIconTheme.labelRu else selectedIconTheme.labelEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isRainbow) Color(0xFFEC4899) else iconPrimary
                    )
                }

                // 4 rows x 4 columns = 16 icons with labels
                val rows = ICON_SHOWCASE_4X4.chunked(4)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    rows.forEachIndexed { rowIdx, rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEachIndexed { colIdx, item ->
                                val idx = rowIdx * 4 + colIdx
                                val tileAccent = when {
                                    isRainbow -> GALLERY_RAINBOW[idx % GALLERY_RAINBOW.size]
                                    isPureWhite -> Color.White
                                    idx % 2 == 0 -> iconPrimary
                                    else -> iconSecondary
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(orbitContainerShape(14.dp, styleMode))
                                            .background(
                                                when {
                                                    isRainbow -> Brush.linearGradient(
                                                        listOf(
                                                            tileAccent.copy(alpha = 0.30f),
                                                            GALLERY_RAINBOW[(idx + 2) % GALLERY_RAINBOW.size].copy(alpha = 0.22f)
                                                        )
                                                    )
                                                    isPureWhite -> Brush.linearGradient(
                                                        listOf(
                                                            Color(0xFF1E293B).copy(alpha = if (tokens.isDark) 0.72f else 0.88f),
                                                            Color(0xFF0F172A).copy(alpha = if (tokens.isDark) 0.85f else 0.94f)
                                                        )
                                                    )
                                                    else -> Brush.linearGradient(
                                                        listOf(
                                                            tileAccent.copy(alpha = 0.22f),
                                                            tileAccent.copy(alpha = 0.12f)
                                                        )
                                                    )
                                                }
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isPureWhite) Color.White.copy(alpha = 0.65f) else tileAccent.copy(alpha = 0.50f),
                                                shape = orbitContainerShape(14.dp, styleMode)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when {
                                            item.isOrbitEmblem -> {
                                                OrbitLogoEmblem(
                                                    size = 30.dp,
                                                    animateRing = false,
                                                    iconTheme = selectedIconTheme,
                                                    styleMode = styleMode
                                                )
                                            }
                                            item.titleEn == "MercuryAI" -> {
                                                MercuryAiLogoEmblem(size = 28.dp)
                                            }
                                            item.titleEn == "JupiterGX" -> {
                                                JupiterGxLogoEmblem(size = 28.dp)
                                            }
                                            else -> {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = if (isRu) item.titleRu else item.titleEn,
                                                    tint = if (isPureWhite) Color.White else tileAccent,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isRu) item.titleRu else item.titleEn,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = tokens.onCleanBackground,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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

/**
 * 4. Макеты главного экрана:
 * Наглядно показывают, как будет выглядеть прозрачный (или Material You) главный экран для каждого макета.
 */
@Composable
fun HomeLayoutMockupsSection(
    selectedLayout: HomeLayoutStyle,
    styleMode: BrowserStyleMode,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onSelectLayout: (HomeLayoutStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = HomeLayoutStyle.entries.chunked(2)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { layout ->
                    HomeLayoutMiniMockupCard(
                        layout = layout,
                        selected = layout == selectedLayout,
                        styleMode = styleMode,
                        iconTheme = iconTheme,
                        isRu = isRu,
                        onClick = { onSelectLayout(layout) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HomeLayoutMiniMockupCard(
    layout: HomeLayoutStyle,
    selected: Boolean,
    styleMode: BrowserStyleMode,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalOrbitTokens.current
    val isGlassMode = styleMode == BrowserStyleMode.LIQUID_GLASS
    val isM3 = styleMode == BrowserStyleMode.M3_EXPRESSIVE
    val cardColor = when {
        isGlassMode -> Color.White.copy(alpha = 0.28f)
        isM3 -> tokens.glassSurfaceElevated
        styleMode == BrowserStyleMode.GAMING_GX -> Color(0xCC161022)
        else -> Color.White.copy(alpha = 0.85f)
    }
    val cardBorder = if (isGlassMode) Color.White.copy(alpha = 0.72f) else tokens.accentPrimary.copy(alpha = 0.35f)

    Surface(
        shape = orbitContainerShape(20.dp),
        color = tokens.glassSurface,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) tokens.accentPrimary else tokens.glassBorder),
        modifier = modifier
            .orbitBouncyClick(onClick = onClick)
            .testTag("layout_mockup_${layout.name.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Mini Phone Screen showing the transparent home screen (or M3 shapes) over colorful wallpaper
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(142.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = when (styleMode) {
                                BrowserStyleMode.GAMING_GX -> listOf(Color(0xFF28081C), Color(0xFF0A0F24), Color(0xFF061B29))
                                BrowserStyleMode.M3_EXPRESSIVE -> listOf(tokens.cleanBackground, tokens.accentPrimary.copy(alpha = 0.22f))
                                else -> listOf(
                                    tokens.accentPrimary.copy(alpha = 0.55f),
                                    tokens.accentSecondary.copy(alpha = 0.50f),
                                    Color(0xFF0F172A)
                                )
                            }
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .padding(8.dp)
            ) {
                // Decorative wallpaper orb behind transparent cards so transparency is crystal-clear!
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = tokens.accentPrimary.copy(alpha = 0.45f),
                        radius = size.minDimension * 0.30f,
                        center = Offset(size.width * 0.82f, size.height * 0.22f)
                    )
                    drawCircle(
                        color = tokens.accentSecondary.copy(alpha = 0.40f),
                        radius = size.minDimension * 0.24f,
                        center = Offset(size.width * 0.18f, size.height * 0.74f)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Mini Orbit Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OrbitLogoEmblem(
                            size = 16.dp,
                            animateRing = false,
                            iconTheme = iconTheme,
                            styleMode = styleMode
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Orbit",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Middle preview widgets based on HomeLayoutStyle
                    when (layout) {
                        HomeLayoutStyle.CLASSIC -> {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                // 4x2 favorites
                                repeat(2) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        repeat(4) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(16.dp)
                                                    .clip(RoundedCornerShape(5.dp))
                                                    .background(cardColor)
                                                    .border(0.6.dp, cardBorder, RoundedCornerShape(5.dp))
                                            )
                                        }
                                    }
                                }
                                // Transparent Privacy Glass Card
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(22.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(cardColor)
                                        .border(0.7.dp, cardBorder, RoundedCornerShape(8.dp))
                                )
                            }
                        }
                        HomeLayoutStyle.COMPACT -> {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                // 5 compact tiles
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    repeat(5) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(14.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(cardColor)
                                                .border(0.5.dp, cardBorder, RoundedCornerShape(4.dp))
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(cardColor)
                                        .border(0.6.dp, cardBorder, RoundedCornerShape(7.dp))
                                )
                            }
                        }
                        HomeLayoutStyle.DASHBOARD -> {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    repeat(4) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(13.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(cardColor)
                                                .border(0.5.dp, cardBorder, RoundedCornerShape(4.dp))
                                        )
                                    }
                                }
                                // Stacked Telemetry + RedAI + Cosmos cards
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(14.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(cardColor)
                                            .border(0.6.dp, cardBorder, RoundedCornerShape(6.dp))
                                    )
                                }
                            }
                        }
                        HomeLayoutStyle.MINIMAL -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isRu) "Чистый экран" else "Clean Glass",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    // Bottom Floating Transparent Search Dock
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(cardColor)
                                .border(0.7.dp, cardBorder, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(16.dp)
                                .clip(CircleShape)
                                .background(cardColor)
                                .border(0.7.dp, cardBorder, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(cardColor)
                                .border(0.7.dp, cardBorder, CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isRu) layout.labelRu else layout.labelEn,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) tokens.accentPrimary else tokens.onCleanBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (selected) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Selected",
                        tint = tokens.accentPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * 5. Галерея превью всех обоев для применения:
 * Большой интерактивный предпросмотр выбранных обоев с мини-интерфейсом Orbit и кнопкой «Применить»,
 * а ниже — сетка превью всех 11 вариантов обоев.
 */
@Composable
fun WallpaperPreviewGallery(
    selectedWallpaper: WallpaperStyle,
    styleMode: BrowserStyleMode,
    iconTheme: OrbitIconTheme,
    starfieldEnabled: Boolean,
    isRu: Boolean,
    onSelectWallpaper: (WallpaperStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalOrbitTokens.current
    var previewWallpaper by remember { mutableStateOf(selectedWallpaper) }

    LaunchedEffect(selectedWallpaper) {
        previewWallpaper = selectedWallpaper
    }

    val isPreviewApplied = previewWallpaper == selectedWallpaper
    val isGlassMode = styleMode == BrowserStyleMode.LIQUID_GLASS
    val miniUiCardColor = when {
        isGlassMode -> Color.White.copy(alpha = if (tokens.isDark) 0.20f else 0.34f)
        styleMode == BrowserStyleMode.M3_EXPRESSIVE -> tokens.glassSurfaceElevated
        styleMode == BrowserStyleMode.GAMING_GX -> Color(0xCC161022)
        else -> Color.White.copy(alpha = 0.85f)
    }
    val miniUiBorder = if (isGlassMode) Color.White.copy(alpha = 0.72f) else tokens.accentPrimary.copy(alpha = 0.4f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Large Live Preview Card before applying
        OrbitGlassSurface(
            shape = orbitContainerShape(22.dp),
            elevated = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("wallpaper_live_preview")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(152.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, tokens.glassBorder, RoundedCornerShape(16.dp))
                ) {
                    OrbitWallpaperLayer(
                        wallpaper = previewWallpaper,
                        showStarfield = starfieldEnabled,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Mini Browser Home Overlay on top of the previewed wallpaper
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OrbitLogoEmblem(
                                    size = 22.dp,
                                    animateRing = false,
                                    iconTheme = iconTheme,
                                    styleMode = styleMode
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Orbit",
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (previewWallpaper == WallpaperStyle.NONE && !tokens.isDark) {
                                        tokens.onCleanBackground
                                    } else {
                                        Color.White
                                    }
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.36f),
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (isRu) "Предпросмотр" else "Preview",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Mini transparent glass / M3 widget + floating dock
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(4) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(22.dp)
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(miniUiCardColor)
                                            .border(0.7.dp, miniUiBorder, RoundedCornerShape(7.dp))
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(26.dp)
                                    .clip(CircleShape)
                                    .background(miniUiCardColor)
                                    .border(0.8.dp, miniUiBorder, CircleShape)
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = if (isRu) "Поиск или введите URL..." else "Search or enter URL...",
                                    fontSize = 10.sp,
                                    color = if (previewWallpaper == WallpaperStyle.NONE && !tokens.isDark) {
                                        tokens.secondaryText
                                    } else {
                                        Color.White.copy(alpha = 0.85f)
                                    }
                                )
                            }
                        }
                    }
                }

                // Active preview name + Apply button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isRu) previewWallpaper.labelRu else previewWallpaper.labelEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = tokens.onCleanBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isPreviewApplied) {
                                if (isRu) "Сейчас установлено на главном экране" else "Currently active on home screen"
                            } else {
                                if (isRu) "Нажмите «Применить» для установки" else "Tap Apply to set as home background"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.secondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSelectWallpaper(previewWallpaper) },
                        enabled = !isPreviewApplied,
                        shape = orbitContainerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentPrimary,
                            contentColor = Color(0xFF061018),
                            disabledContainerColor = tokens.accentPrimary.copy(alpha = 0.20f),
                            disabledContentColor = tokens.accentPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("apply_wallpaper_button")
                    ) {
                        if (isPreviewApplied) {
                            Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = if (isPreviewApplied) {
                                if (isRu) "Применено" else "Applied"
                            } else {
                                if (isRu) "Применить" else "Apply"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2-Column Grid of ALL 11 Wallpapers
        val rows = WallpaperStyle.entries.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            rows.forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach { wp ->
                        val isApplied = wp == selectedWallpaper
                        val isPreviewing = wp == previewWallpaper
                        WallpaperPreviewTileCard(
                            wallpaper = wp,
                            isApplied = isApplied,
                            isPreviewing = isPreviewing,
                            miniUiCardColor = miniUiCardColor,
                            miniUiBorder = miniUiBorder,
                            isRu = isRu,
                            onPreview = { previewWallpaper = wp },
                            onApply = {
                                previewWallpaper = wp
                                onSelectWallpaper(wp)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun WallpaperPreviewTileCard(
    wallpaper: WallpaperStyle,
    isApplied: Boolean,
    isPreviewing: Boolean,
    miniUiCardColor: Color,
    miniUiBorder: Color,
    isRu: Boolean,
    onPreview: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalOrbitTokens.current
    val borderColor = when {
        isApplied -> tokens.accentPrimary
        isPreviewing -> tokens.accentSecondary
        else -> tokens.glassBorder
    }

    Surface(
        shape = orbitContainerShape(18.dp),
        color = tokens.glassSurface,
        border = BorderStroke(if (isApplied || isPreviewing) 2.dp else 1.dp, borderColor),
        modifier = modifier
            .orbitBouncyClick(onClick = onPreview)
            .testTag("wallpaper_preview_${wallpaper.name.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(0.8.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            ) {
                OrbitWallpaperLayer(
                    wallpaper = wallpaper,
                    showStarfield = false,
                    modifier = Modifier.fillMaxSize()
                )

                // Mini transparent glass bar overlay inside thumbnail
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (isApplied) {
                            Surface(
                                shape = CircleShape,
                                color = tokens.accentPrimary,
                                contentColor = Color(0xFF041018)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = if (isRu) "Активно" else "Active",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else if (isPreviewing) {
                            Surface(
                                shape = CircleShape,
                                color = tokens.accentSecondary,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (isRu) "Просмотр" else "Preview",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(CircleShape)
                            .background(miniUiCardColor)
                            .border(0.6.dp, miniUiBorder, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isRu) wallpaper.labelRu else wallpaper.labelEn,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isApplied || isPreviewing) FontWeight.Bold else FontWeight.Medium,
                color = if (isApplied) tokens.accentPrimary else tokens.onCleanBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isApplied) tokens.accentPrimary.copy(alpha = 0.16f) else tokens.accentPrimary.copy(alpha = 0.10f),
                modifier = Modifier
                    .fillMaxWidth()
                    .orbitBouncyClick(onClick = onApply)
            ) {
                Text(
                    text = if (isApplied) {
                        if (isRu) "✓ Выбрано" else "✓ Selected"
                    } else {
                        if (isRu) "Применить" else "Apply"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.accentPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
        }
    }
}

private val STUDIO_ACCENT_SWATCHES = listOf(
    0L,
    0xFF00D4FFL,
    0xFFFF1E56L,
    0xFF10E89AL,
    0xFF8B5CF6L,
    0xFFFF9F1CL,
    0xFFFF6B9DL
)

private val STUDIO_SECONDARY_SWATCHES = listOf(
    0L,
    0xFF7B61FFL,
    0xFF00F0FFL,
    0xFFEC4899L,
    0xFF34C759L,
    0xFFFACC15L
)

/**
 * 6. Студио тем (Theme Studio):
 * Тонкая настройка цвета акцента, второго цвета, радиуса скругления углов,
 * интенсивности неоновой обводки, глубины размытия/плотности стекла и живых форм M3 Expressive.
 */
@Composable
fun ThemeStudioSection(
    settings: OrbitSettings,
    isRu: Boolean,
    onUpdateSetting: (String, Any) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalOrbitTokens.current
    var cornerSlider by remember(settings.customCornerRadiusDp) {
        mutableFloatStateOf(settings.customCornerRadiusDp.toFloat().coerceIn(0f, 32f))
    }
    var glowSlider by remember(settings.borderGlowIntensity) {
        mutableFloatStateOf(settings.borderGlowIntensity.toFloat().coerceIn(0f, 100f))
    }
    var blurSlider by remember(settings.glassBlurLevel) {
        mutableFloatStateOf(settings.glassBlurLevel.toFloat().coerceIn(0f, 100f))
    }

    val previewAccent = if (settings.customAccentHex != 0L) Color(settings.customAccentHex) else tokens.accentPrimary
    val previewSecondary = if (settings.customSecondaryHex != 0L) Color(settings.customSecondaryHex) else tokens.accentSecondary
    val previewShape = RoundedCornerShape(cornerSlider.dp)

    OrbitGlassSurface(
        shape = orbitContainerShape(22.dp),
        elevated = true,
        gamingRgbGlow = glowSlider > 60f || settings.gamingRgbEnabled,
        modifier = modifier
            .fillMaxWidth()
            .testTag("theme_studio_section")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(previewShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                previewAccent.copy(alpha = 0.22f * (0.4f + 0.6f * (blurSlider / 100f))),
                                previewSecondary.copy(alpha = 0.20f * (0.4f + 0.6f * (blurSlider / 100f)))
                            )
                        )
                    )
                    .border(
                        width = (0.8f + 1.6f * (glowSlider / 100f)).dp,
                        brush = Brush.linearGradient(
                            listOf(
                                previewAccent.copy(alpha = 0.5f + 0.5f * (glowSlider / 100f)),
                                previewSecondary.copy(alpha = 0.4f + 0.5f * (glowSlider / 100f))
                            )
                        ),
                        shape = previewShape
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OrbitLogoEmblem(
                            size = 34.dp,
                            animateRing = false,
                            iconTheme = settings.resolvedIconTheme,
                            styleMode = settings.resolvedStyleMode
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isRu) "Студио тем · Предпросмотр" else "Theme Studio · Live Preview",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = tokens.onCleanBackground
                            )
                            Text(
                                text = "R ${cornerSlider.toInt()}dp · Glow ${glowSlider.toInt()}% · Frost ${blurSlider.toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.secondaryText
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(orbitExpressiveShape(0, CircleShape))
                            .background(Brush.linearGradient(listOf(previewAccent, previewSecondary)))
                    )
                }
            }

            Text(
                text = if (isRu) "Свой основной цвет акцента:" else "Custom Primary Accent:",
                style = MaterialTheme.typography.labelMedium,
                color = tokens.onCleanBackground
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(STUDIO_ACCENT_SWATCHES) { hex ->
                    val isSelected = settings.customAccentHex == hex
                    Surface(
                        shape = CircleShape,
                        color = if (hex == 0L) tokens.glassSurfaceElevated else Color(hex),
                        border = BorderStroke(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) tokens.onCleanBackground else tokens.glassBorder
                        ),
                        modifier = Modifier
                            .size(34.dp)
                            .orbitBouncyClick { onUpdateSetting("customAccentHex", hex) }
                            .testTag("studio_accent_$hex")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (hex == 0L) {
                                Text("Auto", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = tokens.onCleanBackground)
                            } else if (isSelected) {
                                Icon(Icons.Rounded.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Text(
                text = if (isRu) "Вторичный цвет градиента:" else "Custom Secondary Color:",
                style = MaterialTheme.typography.labelMedium,
                color = tokens.onCleanBackground
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(STUDIO_SECONDARY_SWATCHES) { hex ->
                    val isSelected = settings.customSecondaryHex == hex
                    Surface(
                        shape = CircleShape,
                        color = if (hex == 0L) tokens.glassSurfaceElevated else Color(hex),
                        border = BorderStroke(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) tokens.onCleanBackground else tokens.glassBorder
                        ),
                        modifier = Modifier
                            .size(30.dp)
                            .orbitBouncyClick { onUpdateSetting("customSecondaryHex", hex) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (hex == 0L) {
                                Text("Auto", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = tokens.onCleanBackground)
                            } else if (isSelected) {
                                Icon(Icons.Rounded.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            Text(
                text = if (isRu) "Формы Material 3 Expressive (для иконок и карточек):" else "Material 3 Expressive Shapes:",
                style = MaterialTheme.typography.labelMedium,
                color = tokens.onCleanBackground
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ExpressiveShapeStyle.entries) { shapeStyle ->
                    FilterChip(
                        selected = settings.resolvedExpressiveShapeStyle == shapeStyle,
                        onClick = { onUpdateSetting("expressiveShapeStyle", shapeStyle.name) },
                        label = { Text(if (isRu) shapeStyle.labelRu else shapeStyle.labelEn, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (isRu) "Радиус углов карточек" else "Card Corner Radius",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text("${cornerSlider.toInt()} dp", style = MaterialTheme.typography.labelSmall, color = tokens.accentPrimary)
                }
                Slider(
                    value = cornerSlider,
                    onValueChange = { cornerSlider = it },
                    onValueChangeFinished = { onUpdateSetting("customCornerRadiusDp", cornerSlider.toLong()) },
                    valueRange = 0f..32f,
                    colors = SliderDefaults.colors(thumbColor = tokens.accentPrimary, activeTrackColor = tokens.accentPrimary)
                )
            }

            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (isRu) "Сила свечения рамок" else "Border Glow Intensity",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text("${glowSlider.toInt()}%", style = MaterialTheme.typography.labelSmall, color = tokens.accentPrimary)
                }
                Slider(
                    value = glowSlider,
                    onValueChange = { glowSlider = it },
                    onValueChangeFinished = { onUpdateSetting("borderGlowIntensity", glowSlider.toLong()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = tokens.accentPrimary, activeTrackColor = tokens.accentPrimary)
                )
            }

            if (settings.resolvedStyleMode != BrowserStyleMode.M3_EXPRESSIVE &&
                settings.resolvedStyleMode != BrowserStyleMode.MINIMALIST
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = if (isRu) "Плотность и размытие фона" else "Glass Density & Frost",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text("${blurSlider.toInt()}%", style = MaterialTheme.typography.labelSmall, color = tokens.accentPrimary)
                    }
                    Slider(
                        value = blurSlider,
                        onValueChange = { blurSlider = it },
                        onValueChangeFinished = { onUpdateSetting("glassBlurLevel", blurSlider.toLong()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = tokens.accentPrimary, activeTrackColor = tokens.accentPrimary)
                    )
                }
            } else if (settings.resolvedStyleMode == BrowserStyleMode.M3_EXPRESSIVE) {
                Text(
                    text = if (isRu) "Liquid Glass отключён в режиме Material You" else "Liquid Glass disabled in Material You mode",
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.accentSecondary
                )
            }
        }
    }
}


