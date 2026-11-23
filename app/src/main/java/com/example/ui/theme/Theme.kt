package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.example.model.BrowserStyleMode
import com.example.model.ColorPalettePreset
import com.example.model.ExpressiveShapeStyle
import com.example.model.GlassTransparencyLevel
import com.example.model.OrbitAnimationStyle
import com.example.model.OrbitCornerStyle
import com.example.model.OrbitIconTheme
import com.example.model.OrbitLogoScale
import com.example.model.ThemeBrightness

data class OrbitVisualTokens(
    val styleMode: BrowserStyleMode,
    val palette: ColorPalettePreset,
    val isDark: Boolean,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val glassSurface: Color,
    val glassSurfaceElevated: Color,
    val glassBorder: Color,
    val cleanBackground: Color,
    val onCleanBackground: Color,
    val secondaryText: Color,
    val gxGlowColor: Color,
    val monarchG2Enabled: Boolean = true,
    val animationStyle: OrbitAnimationStyle = OrbitAnimationStyle.FLUID_SPRING,
    val cornerStyle: OrbitCornerStyle = OrbitCornerStyle.ROUNDED,
    val iconTheme: OrbitIconTheme = OrbitIconTheme.AUTO_THEME,
    val themedIconsAdaptApp: Boolean = true,
    val expressiveShapeStyle: ExpressiveShapeStyle = ExpressiveShapeStyle.AUTO_MIX,
    val homeLogoScale: OrbitLogoScale = OrbitLogoScale.MEDIUM,
    val customCornerRadiusDp: Int = 20,
    val borderGlowIntensity: Int = 50,
    val glassBlurLevel: Int = 50,
    val customIconUri: String = ""
) {
    /** Resolves effective icon tint pair according to iconTheme and active styleMode. */
    fun resolvedIconColors(): Pair<Color, Color> {
        return when (iconTheme) {
            OrbitIconTheme.AUTO_THEME -> when (styleMode) {
                BrowserStyleMode.GAMING_GX -> Color(0xFFFF1E56) to Color(0xFF00F0FF)
                BrowserStyleMode.MINIMALIST -> onCleanBackground to accentPrimary
                BrowserStyleMode.NOTHING_OS -> (if (isDark) Color.White else Color(0xFF111111)) to Color(0xFFFF3B30)
                BrowserStyleMode.MINECRAFT -> Color(0xFF4CAF50) to Color(0xFF22D3EE)
                BrowserStyleMode.SAKURA -> Color(0xFFFF6B9D) to Color(0xFFFFB3C6)
                BrowserStyleMode.KOSMOS -> Color(0xFF8B5CF6) to Color(0xFF00E5FF)
                BrowserStyleMode.M3_EXPRESSIVE -> accentPrimary to accentSecondary
                BrowserStyleMode.LIQUID_GLASS -> accentPrimary to accentSecondary
            }
            OrbitIconTheme.PURE_WHITE -> if (isDark) {
                Color(0xFFFFFFFF) to Color(0xFFCBD5E1)
            } else {
                Color(0xFF1E293B) to Color(0xFF64748B)
            }
            else -> Color(iconTheme.primaryHex) to Color(iconTheme.secondaryHex)
        }
    }
}

val LocalOrbitTokens = staticCompositionLocalOf {
    OrbitVisualTokens(
        styleMode = BrowserStyleMode.LIQUID_GLASS,
        palette = ColorPalettePreset.ORBIT_CYAN,
        isDark = false,
        accentPrimary = Color(0xFF00D4FF),
        accentSecondary = Color(0xFF7B61FF),
        glassSurface = Color.White.copy(alpha = 0.32f),
        glassSurfaceElevated = Color.White.copy(alpha = 0.46f),
        glassBorder = Color.White.copy(alpha = 0.85f),
        cleanBackground = Color(0xFFF3F6FC),
        onCleanBackground = Color(0xFF0F172A),
        secondaryText = Color(0xFF475569),
        gxGlowColor = Color(0xFFFF1E56),
        monarchG2Enabled = true,
        animationStyle = OrbitAnimationStyle.FLUID_SPRING,
        cornerStyle = OrbitCornerStyle.ROUNDED,
        iconTheme = OrbitIconTheme.AUTO_THEME,
        themedIconsAdaptApp = true,
        expressiveShapeStyle = ExpressiveShapeStyle.AUTO_MIX,
        homeLogoScale = OrbitLogoScale.MEDIUM
    )
}

@Composable
fun OrbitTheme(
    styleMode: BrowserStyleMode = BrowserStyleMode.LIQUID_GLASS,
    brightness: ThemeBrightness = ThemeBrightness.LIGHT,
    palette: ColorPalettePreset = ColorPalettePreset.ORBIT_CYAN,
    glassTransparency: GlassTransparencyLevel = GlassTransparencyLevel.FROSTED,
    animationStyle: OrbitAnimationStyle = OrbitAnimationStyle.FLUID_SPRING,
    cornerStyle: OrbitCornerStyle = OrbitCornerStyle.ROUNDED,
    monarchG2Enabled: Boolean = true,
    iconTheme: OrbitIconTheme = OrbitIconTheme.AUTO_THEME,
    themedIconsAdaptApp: Boolean = true,
    expressiveShapeStyle: ExpressiveShapeStyle = ExpressiveShapeStyle.AUTO_MIX,
    homeLogoScale: OrbitLogoScale = OrbitLogoScale.MEDIUM,
    customAccentHex: Long = 0L,
    customSecondaryHex: Long = 0L,
    customCornerRadiusDp: Int = 20,
    borderGlowIntensity: Int = 50,
    glassBlurLevel: Int = 50,
    customIconUri: String = "",
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = when (brightness) {
        ThemeBrightness.SYSTEM -> when (styleMode) {
            BrowserStyleMode.GAMING_GX,
            BrowserStyleMode.NOTHING_OS,
            BrowserStyleMode.KOSMOS,
            BrowserStyleMode.MINECRAFT -> true
            else -> systemDark
        }
        ThemeBrightness.DARK -> true
        ThemeBrightness.LIGHT -> false
    }

    val palettePrimary = Color(palette.primaryHex)
    val paletteSecondary = Color(palette.secondaryHex)

    val shouldAdaptToIcon = themedIconsAdaptApp && iconTheme != OrbitIconTheme.AUTO_THEME
    val (basePrimary, baseSecondary) = if (shouldAdaptToIcon) {
        when (iconTheme) {
            OrbitIconTheme.PURE_WHITE -> if (useDark) {
                Color(0xFFE2E8F0) to Color(0xFF94A3B8)
            } else {
                Color(0xFF334155) to Color(0xFF64748B)
            }
            OrbitIconTheme.BLACK_HOLE -> Color(0xFFFF6D00) to Color(0xFFFF1E56)
            OrbitIconTheme.RAINBOW_COMPASS -> Color(0xFFEC4899) to Color(0xFF00C7BE)
            else -> Color(iconTheme.primaryHex) to Color(iconTheme.secondaryHex)
        }
    } else {
        palettePrimary to paletteSecondary
    }

    // Theme Studio custom color overrides take top precedence when set
    val primary = if (customAccentHex != 0L) Color(customAccentHex) else basePrimary
    val secondary = if (customSecondaryHex != 0L) Color(customSecondaryHex) else baseSecondary
    val hasRichThemeTint = shouldAdaptToIcon || customAccentHex != 0L ||
        palette != ColorPalettePreset.ORBIT_CYAN || styleMode != BrowserStyleMode.LIQUID_GLASS

    val colorScheme: ColorScheme = if (useDark) {
        val baseBg = when (styleMode) {
            BrowserStyleMode.GAMING_GX -> Color(0xFF09080D)
            BrowserStyleMode.M3_EXPRESSIVE -> Color(0xFF141320)
            BrowserStyleMode.MINIMALIST -> Color(0xFF0D0F12)
            BrowserStyleMode.NOTHING_OS -> Color(0xFF000000)
            BrowserStyleMode.KOSMOS -> Color(0xFF060212)
            BrowserStyleMode.MINECRAFT -> Color(0xFF0C160B)
            BrowserStyleMode.SAKURA -> Color(0xFF1E0914)
            BrowserStyleMode.LIQUID_GLASS -> Color(0xFF0B101D)
        }
        val bg = if (hasRichThemeTint) lerp(baseBg, primary, if (styleMode == BrowserStyleMode.NOTHING_OS) 0.05f else 0.13f) else baseBg
        val baseSurface = when (styleMode) {
            BrowserStyleMode.GAMING_GX -> Color(0xFF14111C)
            BrowserStyleMode.M3_EXPRESSIVE -> Color(0xFF211F33)
            BrowserStyleMode.MINIMALIST -> Color(0xFF16191E)
            BrowserStyleMode.NOTHING_OS -> Color(0xFF0E0E0E)
            BrowserStyleMode.KOSMOS -> Color(0xFF130829)
            BrowserStyleMode.MINECRAFT -> Color(0xFF172B15)
            BrowserStyleMode.SAKURA -> Color(0xFF301022)
            BrowserStyleMode.LIQUID_GLASS -> Color(0xFF151C2E)
        }
        val surface = if (hasRichThemeTint) lerp(baseSurface, primary, if (styleMode == BrowserStyleMode.NOTHING_OS) 0.07f else 0.16f) else baseSurface
        darkColorScheme(
            primary = primary,
            onPrimary = Color(0xFF061018),
            primaryContainer = primary.copy(alpha = 0.24f),
            onPrimaryContainer = Color.White,
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.24f),
            onSecondaryContainer = Color.White,
            background = bg,
            onBackground = Color(0xFFF1F5F9),
            surface = surface,
            onSurface = Color(0xFFF1F5F9),
            surfaceVariant = lerp(surface, secondary, 0.16f).copy(alpha = 0.94f),
            onSurfaceVariant = Color(0xFFCBD5E1),
            outline = if (hasRichThemeTint) primary.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.16f),
            error = Color(0xFFFF5A79)
        )
    } else {
        val baseBg = when (styleMode) {
            BrowserStyleMode.M3_EXPRESSIVE -> Color(0xFFF5F1FF)
            BrowserStyleMode.MINIMALIST -> Color(0xFFFAFAFA)
            BrowserStyleMode.NOTHING_OS -> Color(0xFFF4F5F7)
            BrowserStyleMode.KOSMOS -> Color(0xFFF3EEFF)
            BrowserStyleMode.MINECRAFT -> Color(0xFFECF8EB)
            BrowserStyleMode.SAKURA -> Color(0xFFFFF2F6)
            BrowserStyleMode.LIQUID_GLASS,
            BrowserStyleMode.GAMING_GX -> Color(0xFFF2F6FC)
        }
        val bg = if (hasRichThemeTint) lerp(baseBg, primary, 0.11f) else lerp(baseBg, primary, 0.04f)
        val lightSurface = if (hasRichThemeTint) lerp(Color.White, primary, 0.07f) else Color.White
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.18f),
            onPrimaryContainer = Color(0xFF0F172A),
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.16f),
            onSecondaryContainer = Color(0xFF0F172A),
            background = bg,
            onBackground = Color(0xFF0F172A),
            surface = lightSurface,
            onSurface = Color(0xFF0F172A),
            surfaceVariant = lerp(Color(0xFFE2E8F0), primary, if (hasRichThemeTint) 0.16f else 0.06f),
            onSurfaceVariant = Color(0xFF475569),
            outline = primary.copy(alpha = 0.22f),
            error = Color(0xFFDC2626)
        )
    }

    val blurFactor = (glassBlurLevel.coerceIn(0, 100) / 100f)
    val baseGlassAlpha = ((if (useDark) glassTransparency.darkAlpha else glassTransparency.lightAlpha) * (0.65f + 0.70f * blurFactor))
        .coerceIn(0.08f, 0.85f)
    val elevatedGlassAlpha = (baseGlassAlpha + 0.14f).coerceAtMost(0.88f)

    val m3TonalBase = if (useDark) {
        lerp(Color(0xFF1F1D30), primary, 0.14f)
    } else {
        lerp(Color(0xFFF6F2FF), primary, 0.08f)
    }
    val m3TonalElevatedBase = if (useDark) {
        lerp(Color(0xFF2B2742), secondary, 0.16f)
    } else {
        lerp(Color(0xFFEDE6FF), secondary, 0.10f)
    }

    val effectiveMonarchG2 = monarchG2Enabled && styleMode == BrowserStyleMode.LIQUID_GLASS
    val glowAlphaFactor = (0.35f + 0.65f * (borderGlowIntensity.coerceIn(0, 100) / 100f))

    val tokens = OrbitVisualTokens(
        styleMode = styleMode,
        palette = palette,
        isDark = useDark,
        accentPrimary = primary,
        accentSecondary = secondary,
        glassSurface = when (styleMode) {
            BrowserStyleMode.M3_EXPRESSIVE -> m3TonalBase
            BrowserStyleMode.GAMING_GX -> lerp(Color(0xB8161221), primary, 0.14f)
            BrowserStyleMode.NOTHING_OS -> if (useDark) Color(0xE60B0B0B) else Color(0xEEFFFFFF)
            BrowserStyleMode.MINECRAFT -> if (useDark) Color(0xDF162614) else Color(0xE8F3FAF1)
            BrowserStyleMode.KOSMOS -> if (useDark) Color(0xCA14092E) else Color(0xDCFAF7FF)
            BrowserStyleMode.SAKURA -> if (useDark) Color(0xCE2F1020) else Color(0xE6FFF5F8)
            else -> if (useDark) colorScheme.surface.copy(alpha = baseGlassAlpha.coerceAtLeast(0.45f)) else Color.White.copy(alpha = baseGlassAlpha)
        },
        glassSurfaceElevated = when (styleMode) {
            BrowserStyleMode.M3_EXPRESSIVE -> m3TonalElevatedBase
            BrowserStyleMode.GAMING_GX -> lerp(Color(0xD91E182E), primary, 0.18f)
            BrowserStyleMode.NOTHING_OS -> if (useDark) Color(0xF2151515) else Color(0xF6FFFFFF)
            BrowserStyleMode.MINECRAFT -> if (useDark) Color(0xEC1E351B) else Color(0xF2FAFFF8)
            BrowserStyleMode.KOSMOS -> if (useDark) Color(0xDE1D0E42) else Color(0xEBFFFFFF)
            BrowserStyleMode.SAKURA -> if (useDark) Color(0xDC3D1529) else Color(0xF0FFFFFF)
            else -> if (useDark) colorScheme.surface.copy(alpha = elevatedGlassAlpha.coerceAtLeast(0.55f)) else Color.White.copy(alpha = elevatedGlassAlpha)
        },
        glassBorder = when (styleMode) {
            BrowserStyleMode.M3_EXPRESSIVE -> primary.copy(alpha = (if (useDark) 0.34f else 0.26f) * glowAlphaFactor)
            BrowserStyleMode.GAMING_GX -> primary.copy(alpha = 0.62f * glowAlphaFactor)
            BrowserStyleMode.NOTHING_OS -> if (useDark) Color.White.copy(alpha = 0.36f * glowAlphaFactor) else Color(0xFF1F2937).copy(alpha = 0.24f * glowAlphaFactor)
            BrowserStyleMode.MINECRAFT -> primary.copy(alpha = 0.72f * glowAlphaFactor)
            BrowserStyleMode.KOSMOS -> secondary.copy(alpha = 0.56f * glowAlphaFactor)
            BrowserStyleMode.SAKURA -> Color(0xFFFF6B9D).copy(alpha = 0.52f * glowAlphaFactor)
            else -> when {
                useDark -> if (shouldAdaptToIcon || customAccentHex != 0L) primary.copy(alpha = 0.45f * glowAlphaFactor) else Color.White.copy(alpha = 0.36f * glowAlphaFactor)
                else -> if (shouldAdaptToIcon || customAccentHex != 0L) lerp(Color.White, primary, 0.34f).copy(alpha = 0.88f * glowAlphaFactor) else Color.White.copy(alpha = 0.88f)
            }
        },
        cleanBackground = colorScheme.background,
        onCleanBackground = colorScheme.onBackground,
        secondaryText = colorScheme.onSurfaceVariant,
        gxGlowColor = if (customAccentHex == 0L && palette == ColorPalettePreset.ORBIT_CYAN && styleMode == BrowserStyleMode.GAMING_GX && !shouldAdaptToIcon) {
            Color(0xFFFF1E56)
        } else {
            primary
        },
        monarchG2Enabled = effectiveMonarchG2,
        animationStyle = animationStyle,
        cornerStyle = cornerStyle,
        iconTheme = iconTheme,
        themedIconsAdaptApp = themedIconsAdaptApp,
        expressiveShapeStyle = expressiveShapeStyle,
        homeLogoScale = homeLogoScale,
        customCornerRadiusDp = customCornerRadiusDp,
        borderGlowIntensity = borderGlowIntensity,
        glassBlurLevel = glassBlurLevel,
        customIconUri = customIconUri
    )

    CompositionLocalProvider(LocalOrbitTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = OrbitTypography,
            content = content
        )
    }
}
