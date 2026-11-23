package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.GenericShape
import com.example.R
import com.example.model.BrowserStyleMode
import com.example.model.ExpressiveShapeStyle
import com.example.model.OrbitIconTheme
import com.example.model.OrbitLogoScale
import com.example.model.OrbitToastNotification
import com.example.model.WallpaperStyle
import com.example.ui.theme.LocalOrbitTokens
import com.example.ui.theme.SpaceGroteskFamily
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun orbitContainerShape(
    radiusDp: Dp = 24.dp,
    styleMode: BrowserStyleMode = LocalOrbitTokens.current.styleMode
): Shape {
    val tokens = LocalOrbitTokens.current
    val studioScale = (tokens.customCornerRadiusDp.toFloat() / 20f).coerceIn(0.1f, 1.8f)
    val cornerDelta = tokens.cornerStyle.radiusDeltaDp
    val adjusted = ((radiusDp.value * studioScale) + cornerDelta).coerceIn(2f, 38f).dp
    return when (styleMode) {
        BrowserStyleMode.LIQUID_GLASS -> RoundedCornerShape(adjusted)
        BrowserStyleMode.M3_EXPRESSIVE -> RoundedCornerShape(
            topStart = (adjusted.value + 12f).dp,
            topEnd = (adjusted.value * 0.35f + 4f).dp,
            bottomEnd = (adjusted.value + 14f).dp,
            bottomStart = (adjusted.value * 0.75f + 8f).dp
        )
        BrowserStyleMode.GAMING_GX -> CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp, topEnd = 4.dp, bottomStart = 4.dp)
        BrowserStyleMode.MINIMALIST -> RoundedCornerShape((adjusted.value * 0.6f).coerceAtLeast(8f).dp)
        BrowserStyleMode.NOTHING_OS -> RoundedCornerShape((adjusted.value * 0.48f).coerceIn(4f, 14.dp.value).dp)
        BrowserStyleMode.KOSMOS -> RoundedCornerShape(adjusted)
        BrowserStyleMode.MINECRAFT -> CutCornerShape(2.dp)
        BrowserStyleMode.SAKURA -> RoundedCornerShape(
            topStart = (adjusted.value + 6f).dp,
            topEnd = (adjusted.value * 0.55f + 6f).dp,
            bottomEnd = (adjusted.value + 8f).dp,
            bottomStart = (adjusted.value * 0.55f + 6f).dp
        )
    }
}

private fun polarGenericShape(
    steps: Int = 72,
    radiusFn: (theta: Double) -> Float
): Shape = GenericShape { size, _ ->
    val cx = size.width / 2f
    val cy = size.height / 2f
    val baseR = minOf(size.width, size.height) / 2f
    for (i in 0 until steps) {
        val theta = (i.toDouble() / steps) * 2.0 * PI
        val r = baseR * radiusFn(theta)
        val x = cx + (r * cos(theta)).toFloat()
        val y = cy + (r * sin(theta)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

val OrbitFlowerShape: Shape = polarGenericShape(84) { theta ->
    0.84f + 0.16f * cos(6.0 * theta).toFloat()
}

val OrbitCloverShape: Shape = polarGenericShape(80) { theta ->
    0.80f + 0.20f * cos(4.0 * theta + PI / 4.0).toFloat()
}

val OrbitStarburstShape: Shape = polarGenericShape(96) { theta ->
    0.85f + 0.15f * cos(8.0 * theta).toFloat()
}

val OrbitSoftHexagonShape: Shape = polarGenericShape(72) { theta ->
    val sector = PI / 3.0
    val local = ((theta % sector) + sector) % sector - sector / 2.0
    (0.88f / cos(local).toFloat()).coerceAtMost(0.99f)
}

val OrbitSquircleShape: Shape = polarGenericShape(64) { theta ->
    val c = abs(cos(theta)).pow(3.6)
    val s = abs(sin(theta)).pow(3.6)
    0.96f * (c + s).pow(-1.0 / 3.6).toFloat()
}

val OrbitArchPillShape: Shape = RoundedCornerShape(
    topStartPercent = 50,
    topEndPercent = 50,
    bottomStartPercent = 20,
    bottomEndPercent = 20
)

/**
 * Returns Material 3 Expressive organic shapes (Flower, Clover, Starburst, Hexagon, Squircle, Arch)
 * when in Material You / M3 Expressive mode, replacing boring uniform circles/squares.
 */
@Composable
fun orbitExpressiveShape(
    variantIndex: Int,
    fallbackShape: Shape = RoundedCornerShape(14.dp),
    styleMode: BrowserStyleMode = LocalOrbitTokens.current.styleMode,
    shapeStyle: ExpressiveShapeStyle = LocalOrbitTokens.current.expressiveShapeStyle
): Shape {
    if (styleMode != BrowserStyleMode.M3_EXPRESSIVE) return fallbackShape
    return when (shapeStyle) {
        ExpressiveShapeStyle.FLOWER -> OrbitFlowerShape
        ExpressiveShapeStyle.CLOVER -> OrbitCloverShape
        ExpressiveShapeStyle.STARBURST -> OrbitStarburstShape
        ExpressiveShapeStyle.HEXAGON -> OrbitSoftHexagonShape
        ExpressiveShapeStyle.SQUIRCLE -> OrbitSquircleShape
        ExpressiveShapeStyle.AUTO_MIX -> when (Math.floorMod(variantIndex, 6)) {
            0 -> OrbitFlowerShape
            1 -> OrbitCloverShape
            2 -> OrbitStarburstShape
            3 -> OrbitSoftHexagonShape
            4 -> OrbitSquircleShape
            else -> OrbitArchPillShape
        }
    }
}

fun Modifier.orbitBouncyClick(
    enabled: Boolean = true,
    playSound: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val animStyle = LocalOrbitTokens.current.animationStyle
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = animStyle.dampingRatio,
            stiffness = animStyle.stiffness
        ),
        label = "orbitPressScale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = androidx.compose.material3.ripple(),
            enabled = enabled,
            onClick = {
                if (playSound) com.example.util.OrbitSoundPlayer.playClick()
                onClick()
            }
        )
}

@Composable
fun OrbitGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = orbitContainerShape(),
    elevated: Boolean = false,
    gamingRgbGlow: Boolean = false,
    accentHighlight: Boolean = false,
    content: @Composable () -> Unit
) {
    val tokens = LocalOrbitTokens.current
    val baseColor = when {
        accentHighlight -> tokens.accentPrimary.copy(alpha = if (tokens.isDark) 0.22f else 0.16f)
        elevated -> tokens.glassSurfaceElevated
        else -> tokens.glassSurface
    }
    val glowFactor = (0.35f + 0.65f * (tokens.borderGlowIntensity.coerceIn(0, 100) / 100f))
    val infiniteTransition = rememberInfiniteTransition(label = "gxRgbBorder")
    val rgbPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gxRgbPhase"
    )

    val borderBrush = when {
        accentHighlight -> {
            Brush.linearGradient(
                colors = listOf(
                    tokens.accentPrimary.copy(alpha = 0.85f * glowFactor),
                    tokens.accentSecondary.copy(alpha = 0.55f * glowFactor)
                )
            )
        }
        tokens.styleMode == BrowserStyleMode.GAMING_GX && gamingRgbGlow -> {
            Brush.linearGradient(
                colors = listOf(
                    tokens.gxGlowColor.copy(alpha = glowFactor.coerceAtMost(1f)),
                    Color(0xFF00F0FF).copy(alpha = (0.7f + 0.3f * rgbPhase) * glowFactor),
                    tokens.accentSecondary.copy(alpha = glowFactor)
                )
            )
        }
        tokens.styleMode == BrowserStyleMode.KOSMOS -> {
            Brush.linearGradient(
                colors = listOf(
                    tokens.accentSecondary.copy(alpha = 0.68f * glowFactor),
                    tokens.accentPrimary.copy(alpha = 0.56f * glowFactor),
                    Color(0xFFEC4899).copy(alpha = 0.45f * glowFactor)
                )
            )
        }
        tokens.styleMode == BrowserStyleMode.NOTHING_OS -> {
            Brush.linearGradient(
                colors = listOf(
                    tokens.glassBorder,
                    if (elevated) Color(0xFFFF3B30).copy(alpha = 0.45f * glowFactor) else tokens.glassBorder
                )
            )
        }
        tokens.styleMode == BrowserStyleMode.MINECRAFT -> {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF81C784).copy(alpha = 0.85f * glowFactor),
                    Color(0xFF2E7D32).copy(alpha = 0.90f * glowFactor)
                )
            )
        }
        tokens.styleMode == BrowserStyleMode.SAKURA -> {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFB3C6).copy(alpha = 0.85f * glowFactor),
                    Color(0xFFFF6B9D).copy(alpha = 0.58f * glowFactor)
                )
            )
        }
        tokens.styleMode == BrowserStyleMode.LIQUID_GLASS -> {
            Brush.linearGradient(
                colors = if (tokens.isDark) {
                    listOf(
                        Color.White.copy(alpha = 0.58f * glowFactor),
                        Color.White.copy(alpha = 0.16f * glowFactor),
                        tokens.accentPrimary.copy(alpha = 0.35f * glowFactor),
                        Color.White.copy(alpha = 0.14f)
                    )
                } else {
                    listOf(
                        Color.White.copy(alpha = 0.96f),
                        Color.White.copy(alpha = 0.55f),
                        tokens.accentPrimary.copy(alpha = 0.28f * glowFactor),
                        Color(0xFF94A3B8).copy(alpha = 0.32f)
                    )
                },
                start = Offset.Zero,
                end = Offset.Infinite
            )
        }
        tokens.styleMode == BrowserStyleMode.M3_EXPRESSIVE -> {
            Brush.linearGradient(
                colors = listOf(
                    tokens.glassBorder,
                    tokens.accentSecondary.copy(alpha = (if (tokens.isDark) 0.28f else 0.22f) * glowFactor)
                )
            )
        }
        else -> {
            Brush.linearGradient(listOf(tokens.glassBorder, tokens.glassBorder))
        }
    }

    val shadowModifier = if (tokens.styleMode == BrowserStyleMode.LIQUID_GLASS || tokens.styleMode == BrowserStyleMode.NOTHING_OS) {
        Modifier
    } else {
        val shadowElevation = when (tokens.styleMode) {
            BrowserStyleMode.M3_EXPRESSIVE -> if (elevated) 4.dp else 2.dp
            BrowserStyleMode.GAMING_GX -> 8.dp
            BrowserStyleMode.KOSMOS -> 6.dp
            BrowserStyleMode.MINECRAFT -> 2.dp
            BrowserStyleMode.SAKURA -> 4.dp
            BrowserStyleMode.MINIMALIST -> 1.dp
            else -> 0.dp
        }
        Modifier.shadow(elevation = shadowElevation, shape = shape, clip = false)
    }

    Box(
        modifier = modifier
            .then(shadowModifier)
            .clip(shape)
            .background(
                brush = when (tokens.styleMode) {
                    BrowserStyleMode.LIQUID_GLASS -> Brush.verticalGradient(
                        colors = listOf(
                            baseColor.copy(alpha = (baseColor.alpha + 0.08f).coerceAtMost(0.75f)),
                            baseColor.copy(alpha = (baseColor.alpha * 0.85f).coerceAtLeast(0.08f)),
                            baseColor
                        )
                    )
                    BrowserStyleMode.M3_EXPRESSIVE -> Brush.verticalGradient(listOf(baseColor, baseColor))
                    else -> Brush.verticalGradient(listOf(baseColor, baseColor))
                }
            )
            .border(
                width = when {
                    tokens.styleMode == BrowserStyleMode.GAMING_GX && gamingRgbGlow -> 1.5.dp
                    tokens.styleMode == BrowserStyleMode.MINECRAFT -> 2.dp
                    tokens.styleMode == BrowserStyleMode.LIQUID_GLASS -> 1.15.dp
                    tokens.styleMode == BrowserStyleMode.M3_EXPRESSIVE -> 1.dp
                    else -> 1.dp
                },
                brush = borderBrush,
                shape = shape
            )
    ) {
        if (tokens.styleMode == BrowserStyleMode.LIQUID_GLASS && tokens.monarchG2Enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (tokens.isDark) 0.16f else 0.28f),
                                Color.White.copy(alpha = if (tokens.isDark) 0.04f else 0.06f),
                                Color.Transparent,
                                tokens.accentPrimary.copy(alpha = if (tokens.isDark) 0.06f else 0.05f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(420f, 420f)
                        )
                    )
            )
        }
        content()
    }
}

@Composable
fun OrbitWallpaperLayer(
    wallpaper: WallpaperStyle,
    modifier: Modifier = Modifier,
    showStarfield: Boolean = false
) {
    val tokens = LocalOrbitTokens.current
    Box(modifier = modifier.fillMaxSize()) {
        when (wallpaper) {
            WallpaperStyle.NONE -> {
                // Clean canvas with ultra-subtle crystal gradient so transparent Liquid Glass edges catch light
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = if (tokens.isDark) {
                                    listOf(tokens.cleanBackground, Color(0xFF0E1729), tokens.cleanBackground)
                                } else {
                                    listOf(
                                        tokens.cleanBackground,
                                        tokens.accentPrimary.copy(alpha = 0.05f),
                                        tokens.accentSecondary.copy(alpha = 0.04f),
                                        tokens.cleanBackground
                                    )
                                }
                            )
                        )
                )
            }
            WallpaperStyle.MINT_MEADOW -> {
                // Vibrant Apple-green backdrop inspired by user screenshot!
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF0F3822), Color(0xFF144D30), Color(0xFF0B2918))
                                } else {
                                    listOf(Color(0xFF4ADE80), Color(0xFF22C55E), Color(0xFF16A34A))
                                }
                            )
                        )
                )
            }
            WallpaperStyle.PRISM_FROST -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF0F172A))
                                } else {
                                    listOf(Color(0xFFEEF2FF), Color(0xFFE0E7FF), Color(0xFFFDF4FF))
                                }
                            )
                        )
                )
            }
            WallpaperStyle.KOSMOS_NEBULA -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF3B0764), Color(0xFF1E1035), Color(0xFF090314))
                                } else {
                                    listOf(Color(0xFFF3E8FF), Color(0xFFE9D5FF), Color(0xFFDDD6FE))
                                },
                                center = Offset(400f, 300f),
                                radius = 1200f
                            )
                        )
                )
            }
            WallpaperStyle.PLANET_EARTH -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF05192D), Color(0xFF0C3966), Color(0xFF030D17))
                                } else {
                                    listOf(Color(0xFFBAE6FD), Color(0xFF7DD3FC), Color(0xFFE0F2FE))
                                }
                            )
                        )
                )
            }
            WallpaperStyle.DEEP_PULSAR -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF2E0854), Color(0xFF0A0217), Color(0xFF00384D))
                                } else {
                                    listOf(Color(0xFFEDE9FE), Color(0xFFCCFBF1), Color(0xFFF0FDF4))
                                }
                            )
                        )
                )
            }
            WallpaperStyle.LIQUID_AURORA -> {
                Image(
                    painter = painterResource(id = R.drawable.img_bg_aurora),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (!tokens.isDark) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.18f))
                    )
                }
            }
            WallpaperStyle.CYBER_GRID -> {
                Image(
                    painter = painterResource(id = R.drawable.img_bg_cyber_grid),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            WallpaperStyle.OCEAN_MIST -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF061A2E), Color(0xFF0C3559), Color(0xFF13223E))
                                } else {
                                    listOf(Color(0xFFDFF6FF), Color(0xFFBFE9FF), Color(0xFFE0E7FF))
                                }
                            )
                        )
                )
            }
            WallpaperStyle.OBSIDIAN_MESH -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF090D16)),
                                center = Offset(300f, 350f),
                                radius = 1400f
                            )
                        )
                )
            }
            WallpaperStyle.SUNSET_GLASS -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF2B1033), Color(0xFF541C3C), Color(0xFF1A142D))
                                } else {
                                    listOf(Color(0xFFFFE4E6), Color(0xFFFDE68A), Color(0xFFE0E7FF))
                                }
                            )
                        )
                )
            }
            WallpaperStyle.AURORA_BOREALIS -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF042F2E), Color(0xFF065F46), Color(0xFF1E1B4B))
                                } else {
                                    listOf(Color(0xFFA7F3D0), Color(0xFF67E8F9), Color(0xFFC4B5FD))
                                }
                            )
                        )
                )
            }
            WallpaperStyle.SAKURA_SILK -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF500724), Color(0xFF2E1065), Color(0xFF0F172A))
                                } else {
                                    listOf(Color(0xFFFFE4E6), Color(0xFFFBCFE8), Color(0xFFF5D0FE))
                                },
                                center = Offset(320f, 260f),
                                radius = 1400f
                            )
                        )
                )
            }
            WallpaperStyle.SOLAR_ECLIPSE -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF7C2D12), Color(0xFF1C1917), Color(0xFF09090B))
                                } else {
                                    listOf(Color(0xFFFEF3C7), Color(0xFFFED7AA), Color(0xFFF1F5F9))
                                },
                                center = Offset(420f, 380f),
                                radius = 1200f
                            )
                        )
                )
            }
            WallpaperStyle.CYBERPUNK_TOKYO -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF3B0764), Color(0xFF831843), Color(0xFF0F172A))
                                } else {
                                    listOf(Color(0xFFFCE7F3), Color(0xFFDDD6FE), Color(0xFFCFFAFE))
                                }
                            )
                        )
                )
            }
            WallpaperStyle.MERCURY_LIQUID -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF334155), Color(0xFF3F0D24), Color(0xFF090D16))
                                } else {
                                    listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0), Color(0xFFFFD6E0))
                                },
                                center = Offset(280f, 320f),
                                radius = 1350f
                            )
                        )
                )
            }
            WallpaperStyle.JUPITER_VORTEX -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = if (tokens.isDark) {
                                    listOf(Color(0xFF3A071E), Color(0xFF1A0E2E), Color(0xFF061B29))
                                } else {
                                    listOf(Color(0xFFFFE4E6), Color(0xFFFFEDD5), Color(0xFFCFFAFE))
                                }
                            )
                        )
                )
            }
        }

        if (tokens.styleMode == BrowserStyleMode.LIQUID_GLASS && tokens.monarchG2Enabled) {
            OrbitNativeLiquidGlassWallpaper(
                palette = tokens.palette,
                isDark = tokens.isDark,
                alpha = if (tokens.isDark) 0.55f else 0.42f
            )
        }

        if (tokens.styleMode == BrowserStyleMode.M3_EXPRESSIVE) {
            OrbitMaterialYouShapesLayer(
                isDark = tokens.isDark,
                primary = tokens.accentPrimary,
                secondary = tokens.accentSecondary
            )
        }

        if (showStarfield) {
            OrbitAmbientStarfield(
                isDark = tokens.isDark,
                accentPrimary = tokens.accentPrimary,
                accentSecondary = tokens.accentSecondary
            )
        }
    }
}

@Composable
private fun OrbitMaterialYouShapesLayer(
    isDark: Boolean,
    primary: Color,
    secondary: Color
) {
    val alphaA = if (isDark) 0.19f else 0.16f
    val alphaB = if (isDark) 0.15f else 0.12f
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Top-right 6-petal Expressive Flower
        val flowerCenter = Offset(size.width * 0.85f, size.height * 0.14f)
        val flowerR = size.minDimension * 0.19f
        val flowerPath = Path().apply {
            for (i in 0 until 84) {
                val theta = (i.toDouble() / 84.0) * 2.0 * PI
                val r = flowerR * (0.82f + 0.18f * cos(6.0 * theta).toFloat())
                val x = flowerCenter.x + (r * cos(theta)).toFloat()
                val y = flowerCenter.y + (r * sin(theta)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(flowerPath, color = primary.copy(alpha = alphaA))
        // Rotated rounded squircle mid-left
        rotate(degrees = 24f, pivot = Offset(size.width * 0.14f, size.height * 0.42f)) {
            drawRoundRect(
                color = secondary.copy(alpha = alphaB),
                topLeft = Offset(size.width * 0.02f, size.height * 0.30f),
                size = androidx.compose.ui.geometry.Size(size.minDimension * 0.38f, size.minDimension * 0.38f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(42.dp.toPx(), 42.dp.toPx())
            )
        }
        // Center-right 8-lobe Material You scalloped starburst
        val burstCenter = Offset(size.width * 0.78f, size.height * 0.46f)
        val burstR = size.minDimension * 0.12f
        for (i in 0 until 8) {
            val ang = (i * 45f) * (kotlin.math.PI / 180f)
            val lx = burstCenter.x + (burstR * 0.55f * kotlin.math.cos(ang)).toFloat()
            val ly = burstCenter.y + (burstR * 0.55f * kotlin.math.sin(ang)).toFloat()
            drawCircle(
                color = (if (i % 2 == 0) secondary else primary).copy(alpha = alphaB * 0.85f),
                radius = burstR * 0.62f,
                center = Offset(lx, ly)
            )
        }
        // Bottom-right 4-lobe Material You clover
        val cloverCenter = Offset(size.width * 0.82f, size.height * 0.78f)
        val lobeR = size.minDimension * 0.14f
        listOf(
            Offset(-lobeR * 0.55f, -lobeR * 0.55f),
            Offset(lobeR * 0.55f, -lobeR * 0.55f),
            Offset(-lobeR * 0.55f, lobeR * 0.55f),
            Offset(lobeR * 0.55f, lobeR * 0.55f)
        ).forEach { off ->
            drawCircle(
                color = primary.copy(alpha = alphaB),
                radius = lobeR * 0.78f,
                center = cloverCenter + off
            )
        }
        // Soft bottom-left pill arc
        rotate(degrees = -18f, pivot = Offset(size.width * 0.18f, size.height * 0.86f)) {
            drawRoundRect(
                color = secondary.copy(alpha = alphaA),
                topLeft = Offset(size.width * -0.05f, size.height * 0.80f),
                size = androidx.compose.ui.geometry.Size(size.minDimension * 0.46f, size.minDimension * 0.18f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(48.dp.toPx(), 48.dp.toPx())
            )
        }
    }
}

@Composable
private fun OrbitAmbientStarfield(
    isDark: Boolean,
    accentPrimary: Color,
    accentSecondary: Color
) {
    val transition = rememberInfiniteTransition(label = "ambientStars")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "starPhase"
    )
    val stars = remember {
        listOf(
            Triple(0.12f, 0.14f, 2.2f),
            Triple(0.28f, 0.22f, 1.6f),
            Triple(0.76f, 0.16f, 2.6f),
            Triple(0.88f, 0.31f, 1.8f),
            Triple(0.18f, 0.45f, 2.0f),
            Triple(0.62f, 0.39f, 1.5f),
            Triple(0.41f, 0.11f, 2.4f),
            Triple(0.84f, 0.62f, 2.1f),
            Triple(0.24f, 0.74f, 2.5f),
            Triple(0.55f, 0.68f, 1.7f),
            Triple(0.72f, 0.84f, 2.3f),
            Triple(0.10f, 0.88f, 1.9f)
        )
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEachIndexed { index, (xFrac, yFrac, rDp) ->
            val driftY = ((yFrac + phase * (0.04f + (index % 3) * 0.02f)) % 1f) * size.height
            val x = xFrac * size.width
            val twinkle = 0.35f + 0.55f * kotlin.math.abs(kotlin.math.sin((phase * 6.28318f) + index * 1.1f))
            val starColor = when (index % 3) {
                0 -> accentPrimary
                1 -> accentSecondary
                else -> if (isDark) Color.White else accentPrimary.copy(alpha = 0.8f)
            }
            drawCircle(
                color = starColor.copy(alpha = (if (isDark) 0.55f else 0.30f) * twinkle),
                radius = rDp.dp.toPx(),
                center = Offset(x, driftY)
            )
        }
    }
}

@Composable
fun OrbitNativeLiquidGlassWallpaper(
    palette: ColorPalettePreset,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    alpha: Float = 0.50f
) {
    val primaryColor = Color(palette.primaryHex)
    val secondaryColor = Color(palette.secondaryHex)

    val infiniteTransition = rememberInfiniteTransition(label = "monarchG2Optics")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha }
    ) {
        val w = size.width
        val h = size.height

        // 1. Refractive deep fluid gradient
        val bgBrush = Brush.linearGradient(
            colors = if (isDark) {
                listOf(
                    Color(0xFF070B14),
                    primaryColor.copy(alpha = 0.16f),
                    secondaryColor.copy(alpha = 0.12f),
                    Color(0xFF04070D)
                )
            } else {
                listOf(
                    Color(0xFFF0F4FA),
                    primaryColor.copy(alpha = 0.18f),
                    secondaryColor.copy(alpha = 0.14f),
                    Color(0xFFE2E8F0)
                )
            },
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )
        drawRect(brush = bgBrush)

        // 2. Liquid Glass Snell Caustic Wave 1 (Primary optics)
        val cx1 = w * (0.35f + 0.12f * sin(wavePhase))
        val cy1 = h * (0.42f + 0.10f * cos(wavePhase * 0.8f))
        val r1 = w * 0.65f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = if (isDark) 0.32f else 0.28f),
                    primaryColor.copy(alpha = if (isDark) 0.12f else 0.10f),
                    Color.Transparent
                ),
                center = Offset(cx1, cy1),
                radius = r1
            ),
            radius = r1,
            center = Offset(cx1, cy1)
        )

        // 3. Chromatic Dispersion Wave 2 (Prism RGB Split shift)
        val cx2 = w * (0.68f + 0.10f * cos(wavePhase * 1.2f))
        val cy2 = h * (0.58f + 0.12f * sin(wavePhase * 0.9f))
        val r2 = w * 0.55f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    secondaryColor.copy(alpha = if (isDark) 0.28f else 0.24f),
                    Color(0xFFFF3B30).copy(alpha = if (isDark) 0.08f else 0.06f),
                    Color.Transparent
                ),
                center = Offset(cx2, cy2),
                radius = r2
            ),
            radius = r2,
            center = Offset(cx2, cy2)
        )

        // 4. Glancing Fresnel Specular Highlight Curve
        val highlightY = h * (0.28f + 0.06f * sin(wavePhase * 1.4f))
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = if (isDark) 0.15f else 0.22f),
                    Color.Transparent
                ),
                startY = highlightY - 90f,
                endY = highlightY + 90f
            )
        )
    }
}

@Composable
fun OrbitLogoEmblem(
    size: Dp = 42.dp,
    animateRing: Boolean = true,
    iconTheme: OrbitIconTheme = LocalOrbitTokens.current.iconTheme,
    styleMode: BrowserStyleMode = LocalOrbitTokens.current.styleMode,
    accentOverride: Pair<Color, Color>? = null
) {
    val tokens = LocalOrbitTokens.current
    val infiniteTransition = rememberInfiniteTransition(label = "orbitLogoSpin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing)
        ),
        label = "orbitRingAngle"
    )
    val compassTilt by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "compassNeedle"
    )

    val isRainbowCompass = iconTheme == OrbitIconTheme.RAINBOW_COMPASS
    val isPureWhite = iconTheme == OrbitIconTheme.PURE_WHITE
    val isGxRed = iconTheme == OrbitIconTheme.GX_CRIMSON ||
        iconTheme == OrbitIconTheme.JUPITER_GX ||
        (iconTheme == OrbitIconTheme.AUTO_THEME && styleMode == BrowserStyleMode.GAMING_GX)
    val isMercuryCore = iconTheme == OrbitIconTheme.MERCURY_CORE

    val (primary, secondary) = accentOverride ?: when {
        isGxRed -> Color(0xFFFF1E56) to Color(0xFF990A2E)
        isPureWhite -> Color(0xFFFFFFFF) to Color(0xFFCBD5E1)
        iconTheme == OrbitIconTheme.AUTO_THEME -> when (styleMode) {
            BrowserStyleMode.MINIMALIST -> tokens.onCleanBackground to tokens.accentPrimary
            else -> tokens.accentPrimary to tokens.accentSecondary
        }
        else -> Color(iconTheme.primaryHex) to Color(iconTheme.secondaryHex)
    }

    val rainbowColors = listOf(
        Color(0xFFFF3B30),
        Color(0xFFFF9500),
        Color(0xFFFFCC00),
        Color(0xFF34C759),
        Color(0xFF00C7BE),
        Color(0xFF007AFF),
        Color(0xFFAF52DE),
        Color(0xFFFF3B30)
    )

    Canvas(modifier = Modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val planetRadius = this.size.minDimension * 0.27f
        val ringSpin = if (animateRing) -26f + (angle * 0.05f) else -26f

        when {
            isRainbowCompass -> {
                drawCircle(
                    brush = Brush.sweepGradient(colors = rainbowColors, center = center),
                    radius = planetRadius,
                    center = center
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.25f),
                    radius = planetRadius * 0.68f,
                    center = center
                )
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(rainbowColors.dropLast(1)),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.082f, cap = StrokeCap.Round)
                    )
                }
                rotate(degrees = if (animateRing) 30f + compassTilt else 30f, pivot = center) {
                    val needleLen = planetRadius * 0.92f
                    val needleHalfW = planetRadius * 0.28f
                    val northPath = Path().apply {
                        moveTo(center.x, center.y - needleLen)
                        lineTo(center.x + needleHalfW, center.y)
                        lineTo(center.x - needleHalfW, center.y)
                        close()
                    }
                    val southPath = Path().apply {
                        moveTo(center.x, center.y + needleLen)
                        lineTo(center.x + needleHalfW, center.y)
                        lineTo(center.x - needleHalfW, center.y)
                        close()
                    }
                    drawPath(northPath, color = Color(0xFFFF2E55))
                    drawPath(southPath, color = Color.White)
                    drawCircle(color = Color(0xFF0F172A), radius = needleHalfW * 0.45f, center = center)
                }
            }

            iconTheme == OrbitIconTheme.BLACK_HOLE -> {
                // Gravitational amber-crimson accretion halo behind black singularity
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFF6D00).copy(alpha = 0.72f), Color(0xFFFF1E56).copy(alpha = 0.28f), Color.Transparent),
                        center = center,
                        radius = planetRadius * 1.65f
                    ),
                    radius = planetRadius * 1.65f,
                    center = center
                )
                // Pitch-black event horizon core
                drawCircle(
                    color = Color(0xFF030306),
                    radius = planetRadius * 0.92f,
                    center = center
                )
                // Crisp photon corona border
                drawCircle(
                    brush = Brush.sweepGradient(listOf(Color(0xFFFF9E00), Color(0xFFFF1E56), Color.White, Color(0xFFFF9E00)), center),
                    radius = planetRadius * 0.94f,
                    center = center,
                    style = Stroke(width = this.size.minDimension * 0.045f)
                )
                // Blazing tilted accretion disk
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color(0xFFFF1E56), Color(0xFFFFD166), Color.White, Color(0xFFFF6D00))),
                        topLeft = Offset(center.x - this.size.width * 0.46f, center.y - this.size.height * 0.14f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.92f, this.size.height * 0.28f),
                        style = Stroke(width = this.size.minDimension * 0.092f, cap = StrokeCap.Round)
                    )
                }
            }

            iconTheme == OrbitIconTheme.GOLD_SATURN -> {
                // Golden banded Saturn sphere
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFF3C4), Color(0xFFD4AF37), Color(0xFF92400E)),
                        start = Offset(center.x - planetRadius, center.y - planetRadius),
                        end = Offset(center.x + planetRadius, center.y + planetRadius)
                    ),
                    radius = planetRadius * 0.95f,
                    center = center
                )
                // Dual concentric golden rings
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color(0xFFFDE68A), Color(0xFFD4AF37), Color(0xFFF59E0B))),
                        topLeft = Offset(center.x - this.size.width * 0.46f, center.y - this.size.height * 0.17f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.92f, this.size.height * 0.34f),
                        style = Stroke(width = this.size.minDimension * 0.068f, cap = StrokeCap.Round)
                    )
                    drawOval(
                        color = Color(0xFFFEF3C7).copy(alpha = 0.85f),
                        topLeft = Offset(center.x - this.size.width * 0.36f, center.y - this.size.height * 0.11f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.72f, this.size.height * 0.22f),
                        style = Stroke(width = this.size.minDimension * 0.036f, cap = StrokeCap.Round)
                    )
                }
            }

            iconTheme == OrbitIconTheme.EMERALD_PULSE -> {
                // Emerald core sphere
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFA7F3D0), Color(0xFF10E89A), Color(0xFF047857)),
                        center = Offset(center.x - planetRadius * 0.25f, center.y - planetRadius * 0.25f),
                        radius = planetRadius * 1.4f
                    ),
                    radius = planetRadius * 0.88f,
                    center = center
                )
                // Twin intersecting atomic orbital rings
                rotate(degrees = 28f + (if (animateRing) angle * 0.05f else 0f), pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color(0xFF10E89A), Color.White, Color(0xFF34D399))),
                        topLeft = Offset(center.x - this.size.width * 0.43f, center.y - this.size.height * 0.15f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.86f, this.size.height * 0.30f),
                        style = Stroke(width = this.size.minDimension * 0.068f, cap = StrokeCap.Round)
                    )
                }
                rotate(degrees = -28f - (if (animateRing) angle * 0.05f else 0f), pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color(0xFF6EE7B7), Color(0xFF059669), Color.White)),
                        topLeft = Offset(center.x - this.size.width * 0.43f, center.y - this.size.height * 0.15f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.86f, this.size.height * 0.30f),
                        style = Stroke(width = this.size.minDimension * 0.068f, cap = StrokeCap.Round)
                    )
                }
            }

            iconTheme == OrbitIconTheme.VIOLET_NEBULA -> {
                // Violet-magenta nebula sphere
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFF5D0FE), Color(0xFF8B5CF6), Color(0xFF4C1D95)),
                        center = Offset(center.x - planetRadius * 0.22f, center.y - planetRadius * 0.22f),
                        radius = planetRadius * 1.4f
                    ),
                    radius = planetRadius * 0.94f,
                    center = center
                )
                // 4-point white star flare at sphere center
                val sr = planetRadius * 0.62f
                val inner = sr * 0.24f
                val starPath = Path().apply {
                    moveTo(center.x, center.y - sr)
                    lineTo(center.x + inner, center.y - inner)
                    lineTo(center.x + sr, center.y)
                    lineTo(center.x + inner, center.y + inner)
                    lineTo(center.x, center.y + sr)
                    lineTo(center.x - inner, center.y + inner)
                    lineTo(center.x - sr, center.y)
                    lineTo(center.x - inner, center.y - inner)
                    close()
                }
                drawPath(starPath, color = Color.White.copy(alpha = 0.94f))
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color(0xFFEC4899), Color.White, Color(0xFF8B5CF6))),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.075f, cap = StrokeCap.Round)
                    )
                }
            }

            iconTheme == OrbitIconTheme.MARS_CORE -> {
                // Fiery Mars terracotta planet
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFED7AA), Color(0xFFFF5722), Color(0xFF9A3412)),
                        center = Offset(center.x - planetRadius * 0.28f, center.y - planetRadius * 0.28f),
                        radius = planetRadius * 1.45f
                    ),
                    radius = planetRadius,
                    center = center
                )
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color(0xFFFF4500), Color(0xFFFFD166), Color(0xFFFF9E00))),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.078f, cap = StrokeCap.Round)
                    )
                }
                // Orbiting Phobos moon dot
                val moonRad = (if (animateRing) angle else 38f) * (kotlin.math.PI / 180f)
                val mx = center.x + (this.size.width * 0.37f * kotlin.math.cos(moonRad)).toFloat()
                val my = center.y + (this.size.height * 0.19f * kotlin.math.sin(moonRad)).toFloat()
                drawCircle(color = Color(0xFFFFEDD5), radius = this.size.minDimension * 0.072f, center = Offset(mx, my))
            }

            iconTheme == OrbitIconTheme.NEPTUNE_PRISM -> {
                // Electric cobalt sphere with crisp crystal diamond facet
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFE0F2FE), Color(0xFF0088FF), Color(0xFF1E3A8A)),
                        center = Offset(center.x - planetRadius * 0.24f, center.y - planetRadius * 0.24f),
                        radius = planetRadius * 1.4f
                    ),
                    radius = planetRadius * 0.96f,
                    center = center
                )
                val dr = planetRadius * 0.72f
                val diamond = Path().apply {
                    moveTo(center.x, center.y - dr)
                    lineTo(center.x + dr * 0.75f, center.y)
                    lineTo(center.x, center.y + dr)
                    lineTo(center.x - dr * 0.75f, center.y)
                    close()
                }
                drawPath(diamond, color = Color.White.copy(alpha = 0.34f))
                drawPath(diamond, color = Color.White.copy(alpha = 0.88f), style = Stroke(width = this.size.minDimension * 0.035f))
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color(0xFF00E5FF), Color.White, Color(0xFF0088FF))),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.078f, cap = StrokeCap.Round)
                    )
                }
            }

            iconTheme == OrbitIconTheme.AMBER_SOLAR -> {
                // Solar corona flare rays
                val rayCount = 8
                for (i in 0 until rayCount) {
                    val rad = (i * 45f + (if (animateRing) angle * 0.2f else 0f)) * (kotlin.math.PI / 180f)
                    val p1 = Offset(
                        center.x + (planetRadius * 1.05f * kotlin.math.cos(rad)).toFloat(),
                        center.y + (planetRadius * 1.05f * kotlin.math.sin(rad)).toFloat()
                    )
                    val p2 = Offset(
                        center.x + (planetRadius * 1.48f * kotlin.math.cos(rad)).toFloat(),
                        center.y + (planetRadius * 1.48f * kotlin.math.sin(rad)).toFloat()
                    )
                    drawLine(
                        color = Color(0xFFFFB703).copy(alpha = 0.85f),
                        start = p1,
                        end = p2,
                        strokeWidth = this.size.minDimension * 0.045f,
                        cap = StrokeCap.Round
                    )
                }
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color(0xFFFFD166), Color(0xFFFF595E)),
                        center = Offset(center.x - planetRadius * 0.2f, center.y - planetRadius * 0.2f),
                        radius = planetRadius * 1.35f
                    ),
                    radius = planetRadius * 0.92f,
                    center = center
                )
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color(0xFFFFD166), Color.White, Color(0xFFFF595E))),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.15f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.30f),
                        style = Stroke(width = this.size.minDimension * 0.075f, cap = StrokeCap.Round)
                    )
                }
            }

            isGxRed -> {
                // Banded Jupiter gas giant core + Great Red Spot + split JupiterGX cyber arcs
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFE0B2),
                            Color(0xFFFF7043),
                            Color(0xFFFF1E56),
                            Color(0xFFFF9F43),
                            Color(0xFF880E4F)
                        ),
                        start = Offset(center.x - planetRadius, center.y - planetRadius),
                        end = Offset(center.x + planetRadius, center.y + planetRadius)
                    ),
                    radius = planetRadius,
                    center = center
                )
                drawOval(
                    color = Color(0xFFFF1E56),
                    topLeft = Offset(center.x + planetRadius * 0.06f, center.y + planetRadius * 0.14f),
                    size = androidx.compose.ui.geometry.Size(planetRadius * 0.56f, planetRadius * 0.34f)
                )
                rotate(degrees = ringSpin, pivot = center) {
                    drawArc(
                        brush = Brush.linearGradient(listOf(Color(0xFFFF1E56), Color.White)),
                        startAngle = 10f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(center.x - this.size.width * 0.45f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.90f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.088f, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.linearGradient(listOf(Color(0xFF00F0FF), Color(0xFFFF1E56))),
                        startAngle = 190f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(center.x - this.size.width * 0.45f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.90f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.088f, cap = StrokeCap.Round)
                    )
                }
            }

            isMercuryCore -> {
                // Liquid silver Mercury core + crimson metallic orbit + 4-point flare
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color(0xFFE2E8F0), Color(0xFF64748B), Color(0xFFFF3E66)),
                        center = Offset(center.x - planetRadius * 0.30f, center.y - planetRadius * 0.30f),
                        radius = planetRadius * 1.48f
                    ),
                    radius = planetRadius * 0.95f,
                    center = center
                )
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color(0xFFFF3E66), Color.White, Color(0xFFCBD5E1))),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.082f, cap = StrokeCap.Round)
                    )
                }
                val flare = buildClassicFourPointStarPath(
                    center = Offset(center.x + planetRadius * 0.72f, center.y - planetRadius * 0.72f),
                    outerR = planetRadius * 0.42f,
                    innerR = planetRadius * 0.14f
                )
                drawPath(flare, color = Color(0xFFFF3E66))
            }

            isPureWhite -> {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color(0xFFF1F5F9), Color(0xFFCBD5E1)),
                        center = Offset(center.x - planetRadius * 0.25f, center.y - planetRadius * 0.25f),
                        radius = planetRadius * 1.4f
                    ),
                    radius = planetRadius,
                    center = center
                )
                drawCircle(
                    color = Color(0xFF94A3B8).copy(alpha = 0.45f),
                    radius = planetRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color.White, Color(0xFFE2E8F0), Color(0xFF94A3B8))),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.085f, cap = StrokeCap.Round)
                    )
                }
            }

            iconTheme == OrbitIconTheme.SAKURA_BLOSSOM -> {
                val pink = Color(0xFFFF6B9D)
                val softPink = Color(0xFFFFC2D4)
                for (i in 0 until 5) {
                    val ang = (i * 72f + (if (animateRing) angle * 0.08f else 0f)) * (PI / 180f)
                    val px = center.x + (planetRadius * 0.45f * cos(ang)).toFloat()
                    val py = center.y + (planetRadius * 0.45f * sin(ang)).toFloat()
                    drawCircle(color = pink.copy(alpha = 0.72f), radius = planetRadius * 0.56f, center = Offset(px, py))
                }
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color.White, softPink, pink)),
                    radius = planetRadius * 0.65f,
                    center = center
                )
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(softPink, pink, Color.White)),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.075f, cap = StrokeCap.Round)
                    )
                }
            }

            iconTheme == OrbitIconTheme.ARCTIC_CRYSTAL -> {
                val sky = Color(0xFF38BDF8)
                val ice = Color(0xFFE0F2FE)
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color.White, ice, sky)),
                    radius = planetRadius * 0.92f,
                    center = center
                )
                for (i in 0 until 6) {
                    val ang = (i * 60f) * (PI / 180f)
                    drawLine(
                        color = Color.White,
                        start = center,
                        end = Offset(
                            center.x + (planetRadius * 0.85f * cos(ang)).toFloat(),
                            center.y + (planetRadius * 0.85f * sin(ang)).toFloat()
                        ),
                        strokeWidth = this.size.minDimension * 0.042f,
                        cap = StrokeCap.Round
                    )
                }
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(Color.White, sky, ice)),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.078f, cap = StrokeCap.Round)
                    )
                }
            }

            iconTheme == OrbitIconTheme.CYBER_MINT -> {
                val mint = Color(0xFF00F5D4)
                val violet = Color(0xFF7B2CBF)
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color.White, mint, violet)),
                    radius = planetRadius * 0.92f,
                    center = center
                )
                rotate(degrees = ringSpin, pivot = center) {
                    drawArc(
                        brush = Brush.linearGradient(listOf(mint, Color.White)),
                        startAngle = 15f,
                        sweepAngle = 155f,
                        useCenter = false,
                        topLeft = Offset(center.x - this.size.width * 0.45f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.90f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.085f, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.linearGradient(listOf(violet, mint)),
                        startAngle = 195f,
                        sweepAngle = 155f,
                        useCenter = false,
                        topLeft = Offset(center.x - this.size.width * 0.45f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.90f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.085f, cap = StrokeCap.Round)
                    )
                }
            }

            iconTheme == OrbitIconTheme.CHERRY_NEON -> {
                val cherry = Color(0xFFFF0055)
                val purple = Color(0xFF7928CA)
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color(0xFFFFB3C6), cherry, purple)),
                    radius = planetRadius * 0.92f,
                    center = center
                )
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(cherry, Color.White, purple)),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.082f, cap = StrokeCap.Round)
                    )
                }
                val moonRad1 = (if (animateRing) angle else 40f) * (PI / 180f)
                val moonRad2 = moonRad1 + PI
                drawCircle(
                    color = Color.White,
                    radius = this.size.minDimension * 0.068f,
                    center = Offset(
                        center.x + (this.size.width * 0.36f * cos(moonRad1)).toFloat(),
                        center.y + (this.size.height * 0.18f * sin(moonRad1)).toFloat()
                    )
                )
                drawCircle(
                    color = Color(0xFFFF85A1),
                    radius = this.size.minDimension * 0.058f,
                    center = Offset(
                        center.x + (this.size.width * 0.36f * cos(moonRad2)).toFloat(),
                        center.y + (this.size.height * 0.18f * sin(moonRad2)).toFloat()
                    )
                )
            }

            else -> {
                // AUTO_THEME & ORBIT_CYAN classic luminous planet + tilted orbital ring
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.92f), primary, secondary),
                        center = Offset(center.x - planetRadius * 0.28f, center.y - planetRadius * 0.28f),
                        radius = planetRadius * 1.45f
                    ),
                    radius = planetRadius,
                    center = center
                )
                rotate(degrees = ringSpin, pivot = center) {
                    drawOval(
                        brush = Brush.linearGradient(listOf(primary, secondary, Color.White)),
                        topLeft = Offset(center.x - this.size.width * 0.44f, center.y - this.size.height * 0.16f),
                        size = androidx.compose.ui.geometry.Size(this.size.width * 0.88f, this.size.height * 0.32f),
                        style = Stroke(width = this.size.minDimension * 0.085f, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

@Composable
fun OrbitBrandHeader(
    modifier: Modifier = Modifier
) {
    val tokens = LocalOrbitTokens.current
    val scaleFactor = tokens.homeLogoScale.factor
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OrbitLogoEmblem(size = (40f * scaleFactor).dp)
        Spacer(modifier = Modifier.width((10f * scaleFactor).dp))
        Text(
            text = "Orbit",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (28f * scaleFactor).sp,
            color = tokens.onCleanBackground,
            letterSpacing = (-0.5).sp
        )
    }
}

/**
 * Bespoke Custom Logo for MercuryAI:
 * Quicksilver metallic Mercury sphere encircled by crimson-silver tilted orbital rings + celestial star spark.
 * Supports animated stroke draw-in (drawProgress + glowIntensity) for the intro overlay and crisp icon rendering.
 */
@Composable
fun MercuryAiLogoEmblem(
    size: Dp = 150.dp,
    modifier: Modifier = Modifier,
    drawProgress: Float = 1f,
    glowIntensity: Float = 0f,
    accentHighlight: Color? = null,
    tintOverride: Color? = null
) {
    val mercuryCrimson = tintOverride ?: Color(0xFFFF3E66)
    val mercurySilver = accentHighlight ?: Color(0xFFE2E8F0)
    val mercuryHighlight = Color(0xFFFFFFFF)
    val mercurySlate = Color(0xFF475569)
    val pathMeasureRing = remember { PathMeasure() }
    val pathMeasureSpark = remember { PathMeasure() }

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w * 0.48f, h * 0.52f)
        val sphereR = w * 0.27f
        val sparkCenter = Offset(w * 0.78f, h * 0.24f)

        // Radiant crimson + quicksilver halo when glowing
        if (glowIntensity > 0.01f) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        mercuryCrimson.copy(alpha = (0.58f * glowIntensity).coerceIn(0f, 1f)),
                        mercurySilver.copy(alpha = (0.28f * glowIntensity).coerceIn(0f, 1f)),
                        Color.Transparent
                    ),
                    center = center,
                    radius = w * 0.68f
                ),
                radius = w * 0.68f,
                center = center
            )
        }

        val clampedDraw = drawProgress.coerceIn(0f, 1f)
        val sphereReveal = (clampedDraw / 0.55f).coerceIn(0f, 1f)
        val ringReveal = ((clampedDraw - 0.15f) / 0.70f).coerceIn(0f, 1f)
        val sparkReveal = ((clampedDraw - 0.55f) / 0.45f).coerceIn(0f, 1f)

        val ringPath = Path().apply {
            addOval(
                androidx.compose.ui.geometry.Rect(
                    left = center.x - w * 0.43f,
                    top = center.y - h * 0.145f,
                    right = center.x + w * 0.43f,
                    bottom = center.y + h * 0.145f
                )
            )
        }
        val sparkPath = buildClassicFourPointStarPath(sparkCenter, outerR = w * 0.135f, innerR = w * 0.042f)

        // Back orbital arc
        if (ringReveal > 0.01f) {
            rotate(degrees = -26f, pivot = center) {
                if (clampedDraw >= 0.995f) {
                    drawArc(
                        brush = Brush.linearGradient(listOf(mercurySlate, mercuryCrimson, mercurySilver)),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(center.x - w * 0.43f, center.y - h * 0.145f),
                        size = androidx.compose.ui.geometry.Size(w * 0.86f, h * 0.29f),
                        style = Stroke(width = (w * 0.055f).coerceAtLeast(1.8.dp.toPx()), cap = StrokeCap.Round)
                    )
                }
            }
        }

        // Liquid Quicksilver Mercury Planet Sphere
        if (sphereReveal > 0.02f) {
            val activeR = sphereR * sphereReveal
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(mercuryHighlight, mercurySilver, Color(0xFF94A3B8), mercurySlate, mercuryCrimson.copy(alpha = 0.85f)),
                    center = Offset(center.x - activeR * 0.34f, center.y - activeR * 0.34f),
                    radius = activeR.coerceAtLeast(1f) * 1.65f
                ),
                radius = activeR,
                center = center,
                alpha = sphereReveal
            )
            // Subtle mercury craters / crescent specular highlight
            drawCircle(
                color = mercuryHighlight.copy(alpha = 0.42f * sphereReveal),
                radius = activeR * 0.22f,
                center = Offset(center.x - activeR * 0.32f, center.y - activeR * 0.28f)
            )
            drawCircle(
                color = mercurySlate.copy(alpha = 0.30f * sphereReveal),
                radius = activeR * 0.16f,
                center = Offset(center.x + activeR * 0.28f, center.y + activeR * 0.22f)
            )
            drawCircle(
                color = mercuryCrimson.copy(alpha = 0.72f * sphereReveal),
                radius = activeR,
                center = center,
                style = Stroke(width = (w * 0.024f).coerceAtLeast(1.dp.toPx()))
            )
        }

        // Front tilted orbital ring (animated laser stroke or full gradient)
        if (ringReveal > 0.01f) {
            rotate(degrees = -26f, pivot = center) {
                if (clampedDraw >= 0.995f && glowIntensity <= 0.01f) {
                    drawArc(
                        brush = Brush.linearGradient(listOf(mercuryCrimson, mercuryHighlight, Color(0xFFFF85A1), mercuryCrimson)),
                        startAngle = 0f,
                        sweepAngle = 185f,
                        useCenter = false,
                        topLeft = Offset(center.x - w * 0.43f, center.y - h * 0.145f),
                        size = androidx.compose.ui.geometry.Size(w * 0.86f, h * 0.29f),
                        style = Stroke(width = (w * 0.068f).coerceAtLeast(2.dp.toPx()), cap = StrokeCap.Round)
                    )
                } else {
                    pathMeasureRing.setPath(ringPath, false)
                    val lenRing = pathMeasureRing.length
                    val strokeW = (w * 0.052f).coerceAtLeast(2.dp.toPx())
                    if (lenRing > 0f) {
                        val partial = Path()
                        pathMeasureRing.getSegment(0f, lenRing * ringReveal, partial, true)
                        drawPath(
                            path = partial,
                            color = mercuryCrimson.copy(alpha = 0.45f + 0.50f * glowIntensity),
                            style = Stroke(width = strokeW * 1.6f, cap = StrokeCap.Round)
                        )
                        drawPath(
                            path = partial,
                            brush = Brush.linearGradient(listOf(mercuryHighlight, mercuryCrimson)),
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                        if (ringReveal < 0.99f) {
                            val tip = pathMeasureRing.getPosition(lenRing * ringReveal)
                            drawCircle(color = mercuryHighlight, radius = strokeW * 0.75f, center = tip)
                        }
                    }
                }
            }
        }

        // Celestial 4-point spark flare
        if (sparkReveal > 0.02f) {
            if (clampedDraw >= 0.995f) {
                drawPath(
                    path = sparkPath,
                    brush = Brush.linearGradient(listOf(mercuryHighlight, mercuryCrimson))
                )
            } else {
                pathMeasureSpark.setPath(sparkPath, true)
                val lenSpark = pathMeasureSpark.length
                val partialSpark = Path()
                pathMeasureSpark.getSegment(0f, lenSpark * sparkReveal, partialSpark, true)
                drawPath(
                    path = partialSpark,
                    color = mercuryCrimson,
                    style = Stroke(width = (w * 0.03f).coerceAtLeast(1.5.dp.toPx()), cap = StrokeCap.Round)
                )
                if (sparkReveal > 0.7f) {
                    drawPath(
                        path = sparkPath,
                        brush = Brush.linearGradient(listOf(mercuryHighlight, mercuryCrimson)),
                        alpha = ((sparkReveal - 0.7f) / 0.3f).coerceIn(0f, 1f)
                    )
                }
            }
        }
    }
}

/**
 * Bespoke Custom Logo for JupiterGX Mode:
 * Banded amber-crimson Jupiter gas giant + Great Red Spot + split crimson/cyan cyber orbital rings.
 */
@Composable
fun JupiterGxLogoEmblem(
    size: Dp = 36.dp,
    modifier: Modifier = Modifier
) {
    val gxCrimson = Color(0xFFFF1E56)
    val gxCyan = Color(0xFF00F0FF)
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)
        val r = this.size.minDimension * 0.27f

        // Outer neon aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(gxCrimson.copy(alpha = 0.34f), gxCyan.copy(alpha = 0.16f), Color.Transparent),
                center = center,
                radius = this.size.minDimension * 0.52f
            ),
            radius = this.size.minDimension * 0.52f,
            center = center
        )

        // Banded Jupiter atmosphere sphere
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFE0B2),
                    Color(0xFFFF7043),
                    gxCrimson,
                    Color(0xFFFF9F43),
                    Color(0xFF700028)
                ),
                start = Offset(center.x - r, center.y - r),
                end = Offset(center.x + r, center.y + r)
            ),
            radius = r,
            center = center
        )

        // Jupiter Great Red Spot vortex
        drawOval(
            color = Color(0xFFFF1E56),
            topLeft = Offset(center.x + r * 0.06f, center.y + r * 0.12f),
            size = androidx.compose.ui.geometry.Size(r * 0.58f, r * 0.34f)
        )

        // Dual split JupiterGX cyber rings
        rotate(degrees = -26f, pivot = center) {
            drawArc(
                brush = Brush.linearGradient(listOf(gxCrimson, Color.White)),
                startAngle = 10f,
                sweepAngle = 152f,
                useCenter = false,
                topLeft = Offset(center.x - w * 0.45f, center.y - h * 0.16f),
                size = androidx.compose.ui.geometry.Size(w * 0.90f, h * 0.32f),
                style = Stroke(width = this.size.minDimension * 0.09f, cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.linearGradient(listOf(gxCyan, gxCrimson)),
                startAngle = 190f,
                sweepAngle = 152f,
                useCenter = false,
                topLeft = Offset(center.x - w * 0.45f, center.y - h * 0.16f),
                size = androidx.compose.ui.geometry.Size(w * 0.90f, h * 0.32f),
                style = Stroke(width = this.size.minDimension * 0.09f, cap = StrokeCap.Round)
            )
        }

        // Electric cyan corner spark
        drawCircle(
            color = gxCyan,
            radius = this.size.minDimension * 0.055f,
            center = Offset(center.x + w * 0.34f, center.y - h * 0.25f)
        )
    }
}

private fun buildClassicFourPointStarPath(
    center: Offset,
    outerR: Float,
    innerR: Float
): Path {
    return Path().apply {
        moveTo(center.x, center.y - outerR)
        quadraticTo(center.x + innerR * 0.35f, center.y - innerR * 0.35f, center.x + outerR, center.y)
        quadraticTo(center.x + innerR * 0.35f, center.y + innerR * 0.35f, center.x, center.y + outerR)
        quadraticTo(center.x - innerR * 0.35f, center.y + innerR * 0.35f, center.x - outerR, center.y)
        quadraticTo(center.x - innerR * 0.35f, center.y - innerR * 0.35f, center.x, center.y - outerR)
        close()
    }
}

@Composable
fun OrbitFloatingToastBanner(
    notification: OrbitToastNotification?,
    modifier: Modifier = Modifier
) {
    val tokens = LocalOrbitTokens.current
    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        ) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (notification != null) {
            OrbitGlassSurface(
                shape = RoundedCornerShape(20.dp),
                elevated = true,
                gamingRgbGlow = tokens.styleMode == BrowserStyleMode.GAMING_GX
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (notification.isAlert) MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
                        else tokens.accentPrimary.copy(alpha = 0.20f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (notification.isAlert) Icons.Rounded.Shield else Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = if (notification.isAlert) MaterialTheme.colorScheme.error else tokens.accentPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = notification.title,
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.onCleanBackground
                        )
                        Text(
                            text = notification.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.secondaryText,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrbitTooltipWrapper(
    tooltipText: String,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(text = tooltipText)
            }
        },
        state = rememberTooltipState(),
        content = content
    )
}

/**
 * Reusable Upward-Emerging Liquid Glass Popup ("выход вверх" + плавное скрытие вниз).
 * Animates smoothly upward from the bottom/origin with spring physics,
 * and glides downward + scales down gracefully when dismissed or swiped down.
 */
@Composable
fun OrbitUpwardPopup(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomCenter,
    maxWidth: Dp = 560.dp,
    maxHeightFraction: Float = 0.86f,
    gamingRgbGlow: Boolean = false,
    showHandle: Boolean = true,
    content: @Composable () -> Unit
) {
    val tokens = LocalOrbitTokens.current
    val animStyle = tokens.animationStyle

    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val animatedDragY by animateFloatAsState(
        targetValue = dragOffsetY,
        animationSpec = spring(
            dampingRatio = animStyle.dampingRatio,
            stiffness = animStyle.stiffness
        ),
        label = "popupDragY"
    )

    LaunchedEffect(visible) {
        if (visible) {
            dragOffsetY = 0f
        }
    }

    if (visible) {
        BackHandler(onBack = onDismiss)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Translucent backdrop scrim with smooth fade in & out
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(240)),
            exit = fadeOut(animationSpec = tween(280, easing = FastOutSlowInEasing))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (tokens.isDark) 0.56f else 0.32f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
        }

        // Upward-emerging & downward-collapsing Liquid Glass card container
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> (fullHeight * 0.85f).toInt().coerceAtLeast(220) },
                animationSpec = spring(
                    dampingRatio = animStyle.dampingRatio,
                    stiffness = animStyle.stiffness
                )
            ) + scaleIn(
                initialScale = 0.84f,
                transformOrigin = TransformOrigin(0.5f, 1f),
                animationSpec = spring(
                    dampingRatio = animStyle.dampingRatio,
                    stiffness = animStyle.stiffness
                )
            ) + fadeIn(animationSpec = tween(220)),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight + 120 },
                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
            ) + scaleOut(
                targetScale = 0.82f,
                transformOrigin = TransformOrigin(0.5f, 1f),
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(durationMillis = 220, delayMillis = 70)),
            modifier = Modifier
                .align(alignment)
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = animatedDragY.coerceAtLeast(0f)
                        val dragProgress = (animatedDragY / 420f).coerceIn(0f, 1f)
                        val dragScale = 1f - (dragProgress * 0.08f)
                        scaleX = dragScale
                        scaleY = dragScale
                        alpha = 1f - (dragProgress * 0.35f)
                    },
                contentAlignment = Alignment.Center
            ) {
                OrbitGlassSurface(
                    shape = orbitContainerShape(28.dp),
                    elevated = true,
                    gamingRgbGlow = gamingRgbGlow,
                    modifier = Modifier
                        .widthIn(max = maxWidth)
                        .fillMaxWidth()
                        .heightIn(max = 720.dp * maxHeightFraction)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (tokens.styleMode == BrowserStyleMode.M3_EXPRESSIVE) {
                                    tokens.glassSurface.copy(alpha = (tokens.glassSurface.alpha + 0.08f).coerceAtMost(0.94f))
                                } else {
                                    tokens.cleanBackground.copy(
                                        alpha = if (tokens.isDark) 0.56f else 0.52f
                                    )
                                }
                            )
                    ) {
                        if (showHandle) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(visible) {
                                        detectVerticalDragGestures(
                                            onVerticalDrag = { _, dragAmount ->
                                                dragOffsetY = (dragOffsetY + dragAmount).coerceAtLeast(0f)
                                            },
                                            onDragEnd = {
                                                if (dragOffsetY > 150f) {
                                                    onDismiss()
                                                } else {
                                                    dragOffsetY = 0f
                                                }
                                            },
                                            onDragCancel = {
                                                dragOffsetY = 0f
                                            }
                                        )
                                    }
                                    .padding(top = 12.dp, bottom = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(44.dp)
                                        .height(4.5.dp)
                                        .clip(CircleShape)
                                        .background(tokens.onCleanBackground.copy(alpha = 0.28f))
                                )
                            }
                        }
                        content()
                    }
                }
            }
        }
    }
}
