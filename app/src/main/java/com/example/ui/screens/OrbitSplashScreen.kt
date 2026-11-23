package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserStyleMode
import com.example.model.ColorPalettePreset
import com.example.model.OrbitIconTheme
import com.example.ui.theme.LocalOrbitTokens
import com.example.ui.theme.SpaceGroteskFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

private val RAINBOW_SPECTRUM = listOf(
    Color(0xFFFF3B30),
    Color(0xFFFF9500),
    Color(0xFFFFCC00),
    Color(0xFF34C759),
    Color(0xFF00C7BE),
    Color(0xFF007AFF),
    Color(0xFFAF52DE),
    Color(0xFFFF3B30)
)

/**
 * Анимированная тёмная заставка Orbit:
 * Меняется как в зависимости от выбранной темы оформления (ColorPalettePreset и BrowserStyleMode),
 * так и в зависимости от выбранного логотипа Orbit (OrbitIconTheme).
 */
@Composable
fun OrbitSplashScreen(
    styleMode: BrowserStyleMode = LocalOrbitTokens.current.styleMode,
    palette: ColorPalettePreset = LocalOrbitTokens.current.palette,
    iconTheme: OrbitIconTheme = LocalOrbitTokens.current.iconTheme,
    greetingText: String = "Привет",
    isRu: Boolean = true,
    onFinished: () -> Unit
) {
    when (iconTheme) {
        OrbitIconTheme.RAINBOW_COMPASS ->
            RainbowCompassSplashContent(styleMode = styleMode, palette = palette, isRu = isRu, onFinished = onFinished)
        OrbitIconTheme.BLACK_HOLE ->
            BlackHoleSplashContent(styleMode = styleMode, palette = palette, isRu = isRu, onFinished = onFinished)
        OrbitIconTheme.GOLD_SATURN,
        OrbitIconTheme.EMERALD_PULSE,
        OrbitIconTheme.VIOLET_NEBULA,
        OrbitIconTheme.MARS_CORE,
        OrbitIconTheme.NEPTUNE_PRISM,
        OrbitIconTheme.AMBER_SOLAR,
        OrbitIconTheme.SAKURA_BLOSSOM,
        OrbitIconTheme.ARCTIC_CRYSTAL,
        OrbitIconTheme.CYBER_MINT,
        OrbitIconTheme.CHERRY_NEON,
        OrbitIconTheme.MERCURY_CORE ->
            SignatureLogoSplashContent(styleMode = styleMode, palette = palette, iconTheme = iconTheme, isRu = isRu, onFinished = onFinished)
        OrbitIconTheme.GX_CRIMSON,
        OrbitIconTheme.JUPITER_GX ->
            GamingGxSplashContent(palette = palette, iconTheme = iconTheme, styleMode = styleMode, isRu = isRu, onFinished = onFinished)
        OrbitIconTheme.AUTO_THEME,
        OrbitIconTheme.ORBIT_CYAN,
        OrbitIconTheme.PURE_WHITE -> when (styleMode) {
            BrowserStyleMode.GAMING_GX -> GamingGxSplashContent(palette = palette, iconTheme = iconTheme, styleMode = styleMode, isRu = isRu, onFinished = onFinished)
            BrowserStyleMode.M3_EXPRESSIVE -> M3ExpressiveSplashContent(palette = palette, iconTheme = iconTheme, isRu = isRu, onFinished = onFinished)
            BrowserStyleMode.MINIMALIST -> MinimalistSplashContent(palette = palette, iconTheme = iconTheme, isRu = isRu, onFinished = onFinished)
            BrowserStyleMode.LIQUID_GLASS -> LiquidGlassSplashContent(palette = palette, iconTheme = iconTheme, isRu = isRu, onFinished = onFinished)
            BrowserStyleMode.NOTHING_OS -> NothingOsSplashContent(
                palette = palette,
                iconTheme = iconTheme,
                greetingText = greetingText,
                isRu = isRu,
                onFinished = onFinished
            )
            BrowserStyleMode.KOSMOS -> KosmosSplashContent(palette = palette, iconTheme = iconTheme, isRu = isRu, onFinished = onFinished)
            BrowserStyleMode.MINECRAFT -> MinecraftSplashContent(palette = palette, iconTheme = iconTheme, isRu = isRu, onFinished = onFinished)
            BrowserStyleMode.SAKURA -> SakuraSplashContent(palette = palette, iconTheme = iconTheme, isRu = isRu, onFinished = onFinished)
        }
    }
}

private fun resolveSplashColors(
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    styleMode: BrowserStyleMode
): Pair<Color, Color> {
    val themePrimary = Color(palette.primaryHex)
    val themeSecondary = Color(palette.secondaryHex)
    return when (iconTheme) {
        OrbitIconTheme.AUTO_THEME -> when (styleMode) {
            BrowserStyleMode.GAMING_GX -> {
                if (palette == ColorPalettePreset.ORBIT_CYAN) Color(0xFFFF1E56) to Color(0xFF00F0FF)
                else themePrimary to themeSecondary
            }
            else -> themePrimary to themeSecondary
        }
        OrbitIconTheme.PURE_WHITE -> Color(0xFFFFFFFF) to Color(0xFFCBD5E1)
        else -> Color(iconTheme.primaryHex) to Color(iconTheme.secondaryHex)
    }
}

private fun buildThemeDarkBackdrop(
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    styleMode: BrowserStyleMode
): Brush {
    val palettePrimary = Color(palette.primaryHex)
    val paletteSecondary = Color(palette.secondaryHex)
    val coreBias = when (iconTheme) {
        OrbitIconTheme.AUTO_THEME, OrbitIconTheme.ORBIT_CYAN, OrbitIconTheme.PURE_WHITE -> palettePrimary
        OrbitIconTheme.GX_CRIMSON, OrbitIconTheme.JUPITER_GX -> lerp(palettePrimary, Color(0xFFFF1E56), 0.55f)
        OrbitIconTheme.MERCURY_CORE -> lerp(palettePrimary, Color(0xFFFF3E66), 0.45f)
        OrbitIconTheme.BLACK_HOLE -> lerp(palettePrimary, Color(0xFFFF6D00), 0.45f)
        else -> lerp(palettePrimary, Color(iconTheme.primaryHex), 0.40f)
    }
    val secondaryBias = lerp(paletteSecondary, Color(0xFF090D18), 0.25f)
    val baseVoid = when (styleMode) {
        BrowserStyleMode.MINIMALIST -> Color(0xFF07090D)
        BrowserStyleMode.GAMING_GX -> Color(0xFF040308)
        else -> Color(0xFF03050B)
    }
    return Brush.radialGradient(
        colors = listOf(
            lerp(Color(0xFF0A0E1B), coreBias, 0.24f),
            lerp(Color(0xFF060912), secondaryBias, 0.14f),
            baseVoid
        )
    )
}

/**
 * 1. Обычный режим (Liquid Glass / Monarch G2) — на тёмном фоне под цвет темы:
 * Центр: появляется круг -> по нему пролетают звёзды -> в конце появляется орбитальная линия (логотип) ->
 * логотип уходит влево, слово "Orbit" выезжает вправо и образует логотип.
 */
@Composable
private fun LiquidGlassSplashContent(
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onFinished: () -> Unit
) {
    val (primary, secondary) = resolveSplashColors(palette, iconTheme, BrowserStyleMode.LIQUID_GLASS)
    val themePrimary = Color(palette.primaryHex)
    val themeSecondary = Color(palette.secondaryHex)
    val isPureWhite = iconTheme == OrbitIconTheme.PURE_WHITE

    val circleScale = remember { Animatable(0f) }
    val circleAlpha = remember { Animatable(0f) }
    val starsProgress = remember { Animatable(0f) }
    val starsAlpha = remember { Animatable(0f) }
    val ringSweep = remember { Animatable(0f) }
    val logoSplitProgress = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch { circleAlpha.animateTo(1f, tween(300)) }
        circleScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )

        launch { starsAlpha.animateTo(1f, tween(180)) }
        starsProgress.animateTo(1f, tween(820, easing = FastOutSlowInEasing))
        launch { starsAlpha.animateTo(0f, tween(240)) }

        ringSweep.animateTo(360f, tween(600, easing = FastOutSlowInEasing))
        delay(110)

        logoSplitProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)
        )
        subtitleAlpha.animateTo(1f, tween(280))
        delay(420)

        launch { exitScale.animateTo(1.05f, tween(340, easing = FastOutSlowInEasing)) }
        exitAlpha.animateTo(0f, tween(340, easing = LinearEasing))
        onFinished()
    }

    val split = logoSplitProgress.value
    val emblemSlideDp = (-56f * split)
    val wordSlideDp = (-10f + 52f * split)

    SplashBackdropContainer(
        backgroundBrush = buildThemeDarkBackdrop(palette, iconTheme, BrowserStyleMode.LIQUID_GLASS),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.height(140.dp)) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(emblemSlideDp.dp.roundToPx(), 0) }
                        .size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val coreRadius = (size.minDimension * 0.22f) * circleScale.value

                        if (circleAlpha.value > 0.01f) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        themePrimary.copy(alpha = 0.36f * circleAlpha.value),
                                        themeSecondary.copy(alpha = 0.16f * circleAlpha.value),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = size.minDimension * 0.50f * circleScale.value.coerceAtLeast(0.1f)
                                ),
                                radius = size.minDimension * 0.50f,
                                center = center
                            )

                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = if (isPureWhite) {
                                        listOf(Color.White, Color(0xFFE2E8F0), Color(0xFF94A3B8))
                                    } else {
                                        listOf(Color.White.copy(alpha = 0.92f), primary, secondary)
                                    },
                                    center = Offset(center.x - coreRadius * 0.3f, center.y - coreRadius * 0.3f),
                                    radius = coreRadius.coerceAtLeast(1f) * 1.45f
                                ),
                                radius = coreRadius,
                                center = center,
                                alpha = circleAlpha.value
                            )
                        }

                        if (starsAlpha.value > 0.01f) {
                            val t = starsProgress.value
                            val starConfigs = listOf(
                                Triple(-24f, 0.0f, themePrimary),
                                Triple(-24f, 0.33f, Color.White),
                                Triple(-24f, 0.66f, themeSecondary),
                                Triple(18f, 0.18f, primary)
                            )
                            starConfigs.forEachIndexed { idx, (tiltDeg, phaseOffset, starColor) ->
                                val p = (t * 1.35f - phaseOffset * 0.45f).coerceIn(0f, 1f)
                                if (p in 0.02f..0.98f) {
                                    val angleRad = (-150f + p * 330f) * (PI / 180f)
                                    val rx = size.width * (0.42f + idx * 0.03f)
                                    val ry = size.height * (0.15f + idx * 0.015f)
                                    val rawX = (rx * cos(angleRad)).toFloat()
                                    val rawY = (ry * sin(angleRad)).toFloat()
                                    val tiltRad = tiltDeg * (PI / 180f)
                                    val x = center.x + rawX * cos(tiltRad).toFloat() - rawY * sin(tiltRad).toFloat()
                                    val y = center.y + rawX * sin(tiltRad).toFloat() + rawY * cos(tiltRad).toFloat()

                                    val prevAngle = angleRad - 0.32f
                                    val prevRawX = (rx * cos(prevAngle)).toFloat()
                                    val prevRawY = (ry * sin(prevAngle)).toFloat()
                                    val px = center.x + prevRawX * cos(tiltRad).toFloat() - prevRawY * sin(tiltRad).toFloat()
                                    val py = center.y + prevRawX * sin(tiltRad).toFloat() + prevRawY * cos(tiltRad).toFloat()

                                    drawLine(
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color.Transparent, starColor.copy(alpha = 0.85f * starsAlpha.value)),
                                            start = Offset(px, py),
                                            end = Offset(x, y)
                                        ),
                                        start = Offset(px, py),
                                        end = Offset(x, y),
                                        strokeWidth = 2.6.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                    drawFourPointStar(
                                        center = Offset(x, y),
                                        radius = 6.dp.toPx(),
                                        color = starColor.copy(alpha = starsAlpha.value)
                                    )
                                }
                            }
                        }

                        if (ringSweep.value > 0.5f) {
                            rotate(degrees = -26f, pivot = center) {
                                drawArc(
                                    brush = Brush.linearGradient(listOf(primary, Color.White, secondary, primary)),
                                    startAngle = 195f,
                                    sweepAngle = ringSweep.value,
                                    useCenter = false,
                                    topLeft = Offset(center.x - size.width * 0.36f, center.y - size.height * 0.13f),
                                    size = Size(size.width * 0.72f, size.height * 0.26f),
                                    style = Stroke(width = size.minDimension * 0.068f, cap = StrokeCap.Round)
                                )
                            }
                        }
                    }
                }

                if (split > 0.01f) {
                    Row(
                        modifier = Modifier
                            .offset { IntOffset(wordSlideDp.dp.roundToPx(), 0) }
                            .clipToBounds()
                            .graphicsLayer { alpha = split.coerceIn(0f, 1f) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Orbit",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 40.sp,
                            letterSpacing = (-1).sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isRu) "Жидкое стекло · ${palette.labelRu}" else "Liquid Glass · ${palette.labelEn}",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                letterSpacing = 1.2.sp,
                color = themePrimary.copy(alpha = 0.85f),
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}

/**
 * 2. Gaming GX / GX Crimson Splash:
 * Круг в центре -> флеш-телепорт влево -> сходящиеся лучи -> вспыхивает разделённое или сплошное кольцо ->
 * буквы "O r b i t" выходят в центр рядом с лого.
 */
@Composable
private fun GamingGxSplashContent(
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    styleMode: BrowserStyleMode,
    isRu: Boolean,
    onFinished: () -> Unit
) {
    val (gxMain, gxAccent) = if (iconTheme == OrbitIconTheme.GX_CRIMSON || iconTheme == OrbitIconTheme.JUPITER_GX) {
        Color(0xFFFF1E56) to Color(0xFF00F0FF)
    } else {
        resolveSplashColors(palette, iconTheme, BrowserStyleMode.GAMING_GX)
    }

    val coreAppear = remember { Animatable(0f) }
    val flashTeleport = remember { Animatable(0f) }
    val flashStreakAlpha = remember { Animatable(0f) }
    val convergingLines = remember { Animatable(0f) }
    val ringSnap = remember { Animatable(0f) }
    val lettersReveal = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        coreAppear.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium)
        )
        delay(80)

        launch {
            flashStreakAlpha.animateTo(1f, tween(70))
            flashStreakAlpha.animateTo(0f, tween(260))
        }
        flashTeleport.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 190, easing = CubicBezierEasing(0.12f, 0.9f, 0.25f, 1f))
        )

        convergingLines.animateTo(1f, tween(durationMillis = 480, easing = FastOutSlowInEasing))
        ringSnap.animateTo(360f, tween(durationMillis = 360, easing = FastOutSlowInEasing))
        lettersReveal.animateTo(1f, tween(durationMillis = 460, easing = FastOutSlowInEasing))
        subtitleAlpha.animateTo(1f, tween(240))
        delay(440)

        launch { exitScale.animateTo(1.08f, tween(280, easing = FastOutSlowInEasing)) }
        exitAlpha.animateTo(0f, tween(280))
        onFinished()
    }

    SplashBackdropContainer(
        backgroundBrush = buildThemeDarkBackdrop(palette, iconTheme, styleMode),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val screenCenter = Offset(size.width / 2f, size.height / 2f)
                val targetX = screenCenter.x - (62.dp.toPx() * flashTeleport.value)
                val sphereCenter = Offset(targetX, screenCenter.y - 12.dp.toPx())
                val coreRadius = 26.dp.toPx() * coreAppear.value

                if (flashStreakAlpha.value > 0.01f) {
                    val streakStart = Offset(screenCenter.x + 12.dp.toPx(), sphereCenter.y)
                    listOf(-12.dp.toPx(), 0f, 12.dp.toPx()).forEachIndexed { idx, yOff ->
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    (if (idx == 1) gxAccent else gxMain).copy(alpha = 0.85f * flashStreakAlpha.value),
                                    Color.White.copy(alpha = flashStreakAlpha.value)
                                ),
                                startX = streakStart.x,
                                endX = sphereCenter.x
                            ),
                            start = Offset(streakStart.x, sphereCenter.y + yOff),
                            end = Offset(sphereCenter.x, sphereCenter.y + yOff * 0.25f),
                            strokeWidth = (3f - idx * 0.4f).dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    listOf(0.25f, 0.55f, 0.80f).forEachIndexed { i, frac ->
                        val gxPos = Offset(screenCenter.x - (62.dp.toPx() * frac), sphereCenter.y)
                        drawCircle(
                            color = (if (i % 2 == 0) gxMain else gxAccent).copy(alpha = 0.35f * flashStreakAlpha.value),
                            radius = coreRadius * (0.7f + frac * 0.25f),
                            center = gxPos
                        )
                    }
                }

                val lineP = convergingLines.value
                if (lineP > 0.01f && ringSnap.value < 355f) {
                    val beamFade = (1f - (ringSnap.value / 360f)).coerceIn(0f, 1f)
                    val anglesDeg = listOf(-155f, -110f, -45f, 15f, 65f, 125f, 175f)
                    anglesDeg.forEachIndexed { idx, deg ->
                        val rad = deg * (PI / 180f)
                        val startDist = size.minDimension * 0.58f
                        val headDist = startDist * (1f - lineP)
                        val tailDist = (headDist + 110.dp.toPx()).coerceAtMost(startDist)
                        val head = Offset(
                            sphereCenter.x + (headDist * cos(rad)).toFloat(),
                            sphereCenter.y + (headDist * sin(rad)).toFloat()
                        )
                        val tail = Offset(
                            sphereCenter.x + (tailDist * cos(rad)).toFloat(),
                            sphereCenter.y + (tailDist * sin(rad)).toFloat()
                        )
                        val beamColor = if (idx % 2 == 0) gxMain else gxAccent
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Transparent, beamColor.copy(alpha = 0.9f * beamFade)),
                                start = tail,
                                end = head
                            ),
                            start = tail,
                            end = head,
                            strokeWidth = 2.8.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    if (lineP > 0.72f) {
                        val shockP = ((lineP - 0.72f) / 0.28f).coerceIn(0f, 1f)
                        drawCircle(
                            color = gxMain.copy(alpha = (1f - shockP) * 0.65f),
                            radius = coreRadius + shockP * 38.dp.toPx(),
                            center = sphereCenter,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                if (coreAppear.value > 0.01f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(gxMain.copy(alpha = 0.5f), gxAccent.copy(alpha = 0.14f), Color.Transparent),
                            center = sphereCenter,
                            radius = 64.dp.toPx()
                        ),
                        radius = 64.dp.toPx(),
                        center = sphereCenter
                    )
                    // Banded Jupiter core + Great Red Spot
                    drawCircle(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFFE0B2), Color(0xFFFF7043), gxMain, Color(0xFFFF9F43), Color(0xFF600018)),
                            start = Offset(sphereCenter.x - coreRadius, sphereCenter.y - coreRadius),
                            end = Offset(sphereCenter.x + coreRadius, sphereCenter.y + coreRadius)
                        ),
                        radius = coreRadius,
                        center = sphereCenter
                    )
                    drawOval(
                        color = gxMain,
                        topLeft = Offset(sphereCenter.x + coreRadius * 0.06f, sphereCenter.y + coreRadius * 0.14f),
                        size = Size(coreRadius * 0.56f, coreRadius * 0.34f)
                    )
                }

                if (ringSnap.value > 0.5f) {
                    rotate(degrees = -26f, pivot = sphereCenter) {
                        val splitFrac = (ringSnap.value / 360f).coerceIn(0f, 1f)
                        if (iconTheme == OrbitIconTheme.GX_CRIMSON || iconTheme == OrbitIconTheme.JUPITER_GX || styleMode == BrowserStyleMode.GAMING_GX) {
                            drawArc(
                                brush = Brush.linearGradient(listOf(gxMain, Color.White)),
                                startAngle = 10f,
                                sweepAngle = 150f * splitFrac,
                                useCenter = false,
                                topLeft = Offset(sphereCenter.x - 44.dp.toPx(), sphereCenter.y - 16.dp.toPx()),
                                size = Size(88.dp.toPx(), 32.dp.toPx()),
                                style = Stroke(width = 7.5.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                brush = Brush.linearGradient(listOf(gxAccent, gxMain)),
                                startAngle = 190f,
                                sweepAngle = 150f * splitFrac,
                                useCenter = false,
                                topLeft = Offset(sphereCenter.x - 44.dp.toPx(), sphereCenter.y - 16.dp.toPx()),
                                size = Size(88.dp.toPx(), 32.dp.toPx()),
                                style = Stroke(width = 7.5.dp.toPx(), cap = StrokeCap.Round)
                            )
                        } else {
                            drawArc(
                                brush = Brush.linearGradient(listOf(gxMain, Color.White, gxAccent, gxMain)),
                                startAngle = 195f,
                                sweepAngle = ringSnap.value,
                                useCenter = false,
                                topLeft = Offset(sphereCenter.x - 44.dp.toPx(), sphereCenter.y - 16.dp.toPx()),
                                size = Size(88.dp.toPx(), 32.dp.toPx()),
                                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.height(100.dp)) {
                    val wordProgress = lettersReveal.value
                    if (wordProgress > 0.01f) {
                        Row(
                            modifier = Modifier.offset(x = 42.dp, y = (-12).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val letters = listOf("O", "r", "b", "i", "t")
                            letters.forEachIndexed { index, ch ->
                                val startAt = index * 0.14f
                                val localP = ((wordProgress - startAt) / 0.44f).coerceIn(0f, 1f)
                                Text(
                                    text = ch,
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 40.sp,
                                    color = if (index == 0) gxMain else Color.White,
                                    modifier = Modifier.graphicsLayer {
                                        alpha = localP
                                        translationY = (1f - localP) * 26.dp.toPx()
                                        translationX = (1f - localP) * (-12).dp.toPx()
                                        scaleX = 0.72f + 0.28f * localP
                                        scaleY = 0.72f + 0.28f * localP
                                    }
                                )
                            }
                        }
                    }
                }
                Text(
                    text = if (isRu) "${styleMode.labelRu.uppercase()} · ${palette.labelRu}" else "${styleMode.labelEn.uppercase()} · ${palette.labelEn}",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.8.sp,
                    color = gxMain,
                    modifier = Modifier.alpha(subtitleAlpha.value)
                )
            }
        }
    }
}

/**
 * 3. Material You Mode (на тёмном фоне под цвет выбранной темы):
 * Круг -> превращается в Сквиркл -> Клевер -> 8-лучевую Звезду -> снова в Круг -> линии образуют лого -> Orbit.
 */
@Composable
private fun M3ExpressiveSplashContent(
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onFinished: () -> Unit
) {
    val (primary, secondary) = resolveSplashColors(palette, iconTheme, BrowserStyleMode.M3_EXPRESSIVE)

    val appearScale = remember { Animatable(0f) }
    val shapeMorphProgress = remember { Animatable(0f) }
    val shapeSpin = remember { Animatable(0f) }
    val ringSweep = remember { Animatable(0f) }
    val splitProgress = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        appearScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
        )
        delay(80)

        launch {
            shapeSpin.animateTo(360f, tween(durationMillis = 1550, easing = FastOutSlowInEasing))
        }
        shapeMorphProgress.animateTo(
            targetValue = 4f,
            animationSpec = tween(durationMillis = 1550, easing = FastOutSlowInEasing)
        )

        ringSweep.animateTo(360f, tween(durationMillis = 520, easing = FastOutSlowInEasing))
        delay(90)

        splitProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)
        )
        subtitleAlpha.animateTo(1f, tween(250))
        delay(420)

        launch { exitScale.animateTo(1.05f, tween(300)) }
        exitAlpha.animateTo(0f, tween(300))
        onFinished()
    }

    val split = splitProgress.value
    val emblemSlideDp = (-56f * split)
    val wordSlideDp = (-8f + 50f * split)

    SplashBackdropContainer(
        backgroundBrush = buildThemeDarkBackdrop(palette, iconTheme, BrowserStyleMode.M3_EXPRESSIVE),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.height(140.dp)) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(emblemSlideDp.dp.roundToPx(), 0) }
                        .size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val baseRadius = size.minDimension * 0.24f * appearScale.value
                        val morph = shapeMorphProgress.value

                        if (appearScale.value > 0.01f) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(primary.copy(alpha = 0.30f), Color.Transparent),
                                    center = center,
                                    radius = size.minDimension * 0.48f
                                ),
                                radius = size.minDimension * 0.48f,
                                center = center
                            )

                            val morphPath = buildMaterialYouMorphPath(
                                center = center,
                                baseRadius = baseRadius,
                                morphStage = morph,
                                spinDeg = shapeSpin.value
                            )
                            drawPath(
                                path = morphPath,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.White.copy(alpha = 0.85f), primary, secondary),
                                    start = Offset(center.x - baseRadius, center.y - baseRadius),
                                    end = Offset(center.x + baseRadius, center.y + baseRadius)
                                )
                            )
                        }

                        if (ringSweep.value > 0.5f) {
                            rotate(degrees = -26f, pivot = center) {
                                drawArc(
                                    brush = Brush.linearGradient(listOf(primary, Color.White, secondary)),
                                    startAngle = 195f,
                                    sweepAngle = ringSweep.value,
                                    useCenter = false,
                                    topLeft = Offset(center.x - size.width * 0.36f, center.y - size.height * 0.13f),
                                    size = Size(size.width * 0.72f, size.height * 0.26f),
                                    style = Stroke(width = size.minDimension * 0.072f, cap = StrokeCap.Round)
                                )
                            }
                        }
                    }
                }

                if (split > 0.01f) {
                    Text(
                        text = "Orbit",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        letterSpacing = (-1).sp,
                        color = Color.White,
                        modifier = Modifier
                            .offset { IntOffset(wordSlideDp.dp.roundToPx(), 0) }
                            .graphicsLayer {
                                alpha = split.coerceIn(0f, 1f)
                                scaleX = 0.85f + 0.15f * split
                                scaleY = 0.85f + 0.15f * split
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isRu) "Материал You · ${palette.labelRu}" else "Material You · ${palette.labelEn}",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                letterSpacing = 1.2.sp,
                color = secondary,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}

/**
 * 4. Minimalist Mode (всегда на тёмном фоне, цвета адаптируются под тему)
 */
@Composable
private fun MinimalistSplashContent(
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onFinished: () -> Unit
) {
    val (primary, _) = resolveSplashColors(palette, iconTheme, BrowserStyleMode.MINIMALIST)

    val dotExpand = remember { Animatable(0f) }
    val ringDraw = remember { Animatable(0f) }
    val splitProgress = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        dotExpand.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
        ringDraw.animateTo(360f, tween(460, easing = FastOutSlowInEasing))
        delay(80)
        splitProgress.animateTo(1f, tween(480, easing = FastOutSlowInEasing))
        subtitleAlpha.animateTo(1f, tween(220))
        delay(380)

        launch { exitScale.animateTo(1.03f, tween(280)) }
        exitAlpha.animateTo(0f, tween(280))
        onFinished()
    }

    val split = splitProgress.value
    val emblemSlideDp = (-54f * split)
    val wordSlideDp = (-8f + 48f * split)

    SplashBackdropContainer(
        backgroundBrush = buildThemeDarkBackdrop(palette, iconTheme, BrowserStyleMode.MINIMALIST),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.height(130.dp)) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(emblemSlideDp.dp.roundToPx(), 0) }
                        .size(104.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val r = size.minDimension * 0.22f * dotExpand.value
                        if (dotExpand.value > 0.01f) {
                            drawCircle(
                                color = Color.White,
                                radius = r,
                                center = center,
                                style = Stroke(width = 3.5.dp.toPx())
                            )
                            drawCircle(
                                color = primary,
                                radius = r * 0.36f,
                                center = center
                            )
                        }
                        if (ringDraw.value > 0.5f) {
                            rotate(degrees = -24f, pivot = center) {
                                drawArc(
                                    color = primary,
                                    startAngle = 195f,
                                    sweepAngle = ringDraw.value,
                                    useCenter = false,
                                    topLeft = Offset(center.x - size.width * 0.36f, center.y - size.height * 0.12f),
                                    size = Size(size.width * 0.72f, size.height * 0.24f),
                                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }
                    }
                }

                if (split > 0.01f) {
                    Row(
                        modifier = Modifier
                            .offset { IntOffset(wordSlideDp.dp.roundToPx(), 0) }
                            .clipToBounds()
                            .graphicsLayer { alpha = split },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(26.dp)
                                .background(primary.copy(alpha = 0.8f))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Orbit",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 38.sp,
                            letterSpacing = (-0.5).sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isRu) "Минимализм · ${palette.labelRu}" else "Minimalist · ${palette.labelEn}",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                letterSpacing = 1.6.sp,
                color = primary.copy(alpha = 0.8f),
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}

/**
 * 5. Заставка «Радужный Компас» (Rainbow Compass) с адаптацией фона под тему
 */
@Composable
private fun RainbowCompassSplashContent(
    styleMode: BrowserStyleMode,
    palette: ColorPalettePreset,
    isRu: Boolean,
    onFinished: () -> Unit
) {
    val sphereScale = remember { Animatable(0f) }
    val needleSpin = remember { Animatable(-210f) }
    val needleAlpha = remember { Animatable(0f) }
    val ringSweep = remember { Animatable(0f) }
    val splitProgress = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        sphereScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
        )
        launch { needleAlpha.animateTo(1f, tween(220)) }
        needleSpin.animateTo(
            targetValue = 28f,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)
        )
        ringSweep.animateTo(360f, tween(560, easing = FastOutSlowInEasing))
        delay(90)
        splitProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)
        )
        subtitleAlpha.animateTo(1f, tween(260))
        delay(440)
        launch { exitScale.animateTo(1.06f, tween(320)) }
        exitAlpha.animateTo(0f, tween(320))
        onFinished()
    }

    val split = splitProgress.value
    val emblemSlideDp = (-56f * split)
    val wordSlideDp = (-10f + 52f * split)

    SplashBackdropContainer(
        backgroundBrush = buildThemeDarkBackdrop(palette, OrbitIconTheme.RAINBOW_COMPASS, styleMode),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.height(140.dp)) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(emblemSlideDp.dp.roundToPx(), 0) }
                        .size(124.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val r = size.minDimension * 0.24f * sphereScale.value

                        if (sphereScale.value > 0.01f) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFEC4899).copy(alpha = 0.25f),
                                        Color(palette.primaryHex).copy(alpha = 0.16f),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = size.minDimension * 0.48f
                                ),
                                radius = size.minDimension * 0.48f,
                                center = center
                            )

                            val tickRadiusOuter = r * 1.48f
                            val tickRadiusInner = r * 1.30f
                            for (i in 0 until 24) {
                                val deg = i * 15f
                                val rad = deg * (PI / 180f)
                                val p1 = Offset(
                                    center.x + (tickRadiusInner * cos(rad)).toFloat(),
                                    center.y + (tickRadiusInner * sin(rad)).toFloat()
                                )
                                val p2 = Offset(
                                    center.x + (tickRadiusOuter * cos(rad)).toFloat(),
                                    center.y + (tickRadiusOuter * sin(rad)).toFloat()
                                )
                                drawLine(
                                    color = RAINBOW_SPECTRUM[i % (RAINBOW_SPECTRUM.size - 1)].copy(alpha = 0.72f * needleAlpha.value),
                                    start = p1,
                                    end = p2,
                                    strokeWidth = if (i % 6 == 0) 2.2.dp.toPx() else 1.2.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }

                            drawCircle(
                                brush = Brush.sweepGradient(colors = RAINBOW_SPECTRUM, center = center),
                                radius = r,
                                center = center
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = 0.20f),
                                radius = r * 0.72f,
                                center = center
                            )
                        }

                        if (ringSweep.value > 0.5f) {
                            rotate(degrees = -26f, pivot = center) {
                                drawArc(
                                    brush = Brush.linearGradient(RAINBOW_SPECTRUM.dropLast(1)),
                                    startAngle = 195f,
                                    sweepAngle = ringSweep.value,
                                    useCenter = false,
                                    topLeft = Offset(center.x - size.width * 0.37f, center.y - size.height * 0.135f),
                                    size = Size(size.width * 0.74f, size.height * 0.27f),
                                    style = Stroke(width = size.minDimension * 0.068f, cap = StrokeCap.Round)
                                )
                            }
                        }

                        if (needleAlpha.value > 0.01f) {
                            rotate(degrees = needleSpin.value, pivot = center) {
                                val needleLen = r * 0.96f
                                val halfW = r * 0.28f
                                val north = Path().apply {
                                    moveTo(center.x, center.y - needleLen)
                                    lineTo(center.x + halfW, center.y)
                                    lineTo(center.x - halfW, center.y)
                                    close()
                                }
                                val south = Path().apply {
                                    moveTo(center.x, center.y + needleLen)
                                    lineTo(center.x + halfW, center.y)
                                    lineTo(center.x - halfW, center.y)
                                    close()
                                }
                                drawPath(north, color = Color(0xFFFF2E55).copy(alpha = needleAlpha.value))
                                drawPath(south, color = Color.White.copy(alpha = needleAlpha.value))
                                drawCircle(
                                    color = Color(0xFF0A0D16),
                                    radius = halfW * 0.45f,
                                    center = center
                                )
                            }
                        }
                    }
                }

                if (split > 0.01f) {
                    Text(
                        text = "Orbit",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        letterSpacing = (-1).sp,
                        color = Color.White,
                        modifier = Modifier
                            .offset { IntOffset(wordSlideDp.dp.roundToPx(), 0) }
                            .graphicsLayer { alpha = split.coerceIn(0f, 1f) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isRu) "Радужный Компас · ${palette.labelRu}" else "Rainbow Compass · ${palette.labelEn}",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                letterSpacing = 1.2.sp,
                color = Color(0xFF38BDF8),
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}

/**
 * 6. Фирменная гравитационная заставка «Чёрная Дыра» (Black Hole Singularity):
 * Горизонт событий притягивает спиральные частицы материи -> вспыхивает огненный аккреционный диск ->
 * сдвигается влево и открывает "Orbit".
 */
@Composable
private fun BlackHoleSplashContent(
    styleMode: BrowserStyleMode,
    palette: ColorPalettePreset,
    isRu: Boolean,
    onFinished: () -> Unit
) {
    val horizonScale = remember { Animatable(0f) }
    val spiralProgress = remember { Animatable(0f) }
    val diskSweep = remember { Animatable(0f) }
    val splitProgress = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        horizonScale.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow))
        spiralProgress.animateTo(1f, tween(820, easing = FastOutSlowInEasing))
        diskSweep.animateTo(360f, tween(520, easing = FastOutSlowInEasing))
        delay(80)
        splitProgress.animateTo(1f, spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow))
        subtitleAlpha.animateTo(1f, tween(260))
        delay(440)
        launch { exitScale.animateTo(1.06f, tween(320)) }
        exitAlpha.animateTo(0f, tween(320))
        onFinished()
    }

    val split = splitProgress.value
    val emblemSlideDp = (-56f * split)
    val wordSlideDp = (-10f + 52f * split)

    SplashBackdropContainer(
        backgroundBrush = buildThemeDarkBackdrop(palette, OrbitIconTheme.BLACK_HOLE, styleMode),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.height(140.dp)) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(emblemSlideDp.dp.roundToPx(), 0) }
                        .size(124.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val r = size.minDimension * 0.22f * horizonScale.value

                        // Inward spiraling plasma particles
                        val sp = spiralProgress.value
                        if (sp in 0.02f..0.98f) {
                            for (i in 0 until 10) {
                                val baseDeg = i * 36f + sp * 420f
                                val dist = (size.minDimension * 0.46f) * (1f - sp) + r * 0.9f
                                val rad = baseDeg * (PI / 180f)
                                val px = center.x + (dist * cos(rad)).toFloat()
                                val py = center.y + (dist * sin(rad)).toFloat()
                                drawCircle(
                                    color = if (i % 2 == 0) Color(0xFFFF9E00) else Color(0xFFFF1E56),
                                    radius = 2.6.dp.toPx() * (1f - sp * 0.5f),
                                    center = Offset(px, py),
                                    alpha = (1f - sp * 0.7f)
                                )
                            }
                        }

                        if (horizonScale.value > 0.01f) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFF6D00).copy(alpha = 0.72f), Color(0xFFFF1E56).copy(alpha = 0.25f), Color.Transparent),
                                    center = center,
                                    radius = r * 1.9f
                                ),
                                radius = r * 1.9f,
                                center = center
                            )
                            drawCircle(color = Color(0xFF030306), radius = r, center = center)
                            drawCircle(
                                brush = Brush.sweepGradient(listOf(Color(0xFFFF9E00), Color(0xFFFF1E56), Color.White, Color(0xFFFF9E00)), center),
                                radius = r,
                                center = center,
                                style = Stroke(width = 2.2.dp.toPx())
                            )
                        }

                        if (diskSweep.value > 0.5f) {
                            rotate(degrees = -24f, pivot = center) {
                                drawArc(
                                    brush = Brush.linearGradient(listOf(Color(0xFFFF1E56), Color(0xFFFFD166), Color.White, Color(0xFFFF6D00))),
                                    startAngle = 195f,
                                    sweepAngle = diskSweep.value,
                                    useCenter = false,
                                    topLeft = Offset(center.x - size.width * 0.40f, center.y - size.height * 0.12f),
                                    size = Size(size.width * 0.80f, size.height * 0.24f),
                                    style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }
                    }
                }

                if (split > 0.01f) {
                    Text(
                        text = "Orbit",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        letterSpacing = (-1).sp,
                        color = Color.White,
                        modifier = Modifier
                            .offset { IntOffset(wordSlideDp.dp.roundToPx(), 0) }
                            .graphicsLayer { alpha = split.coerceIn(0f, 1f) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isRu) "Чёрная Дыра · ${palette.labelRu}" else "Black Hole · ${palette.labelEn}",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                letterSpacing = 1.2.sp,
                color = Color(0xFFFF9E00),
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}

/**
 * 7. Тематические заставки для коллекционных логотипов:
 * GOLD_SATURN (двойные кольца), EMERALD_PULSE (атомные орбиты), VIOLET_NEBULA (звёздная вспышка),
 * MARS_CORE (спутник Фобос), NEPTUNE_PRISM (гранёный кристалл), AMBER_SOLAR (лучи солнечной короны).
 */
@Composable
private fun SignatureLogoSplashContent(
    styleMode: BrowserStyleMode,
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onFinished: () -> Unit
) {
    val primary = Color(iconTheme.primaryHex)
    val secondary = Color(iconTheme.secondaryHex)
    val themeGlow = Color(palette.primaryHex)

    val coreScale = remember { Animatable(0f) }
    val motifProgress = remember { Animatable(0f) }
    val ringSweep = remember { Animatable(0f) }
    val splitProgress = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        coreScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
        )
        motifProgress.animateTo(1f, tween(760, easing = FastOutSlowInEasing))
        ringSweep.animateTo(360f, tween(560, easing = FastOutSlowInEasing))
        delay(80)
        splitProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)
        )
        subtitleAlpha.animateTo(1f, tween(250))
        delay(420)
        launch { exitScale.animateTo(1.05f, tween(300)) }
        exitAlpha.animateTo(0f, tween(300))
        onFinished()
    }

    val split = splitProgress.value
    val emblemSlideDp = (-56f * split)
    val wordSlideDp = (-10f + 52f * split)

    SplashBackdropContainer(
        backgroundBrush = buildThemeDarkBackdrop(palette, iconTheme, styleMode),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.height(140.dp)) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(emblemSlideDp.dp.roundToPx(), 0) }
                        .size(124.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val r = size.minDimension * 0.22f * coreScale.value
                        val mp = motifProgress.value

                        if (coreScale.value > 0.01f) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(primary.copy(alpha = 0.36f), themeGlow.copy(alpha = 0.16f), Color.Transparent),
                                    center = center,
                                    radius = size.minDimension * 0.48f
                                ),
                                radius = size.minDimension * 0.48f,
                                center = center
                            )

                            // Amber Solar rays behind sun sphere
                            if (iconTheme == OrbitIconTheme.AMBER_SOLAR && mp > 0.05f) {
                                for (i in 0 until 8) {
                                    val rad = (i * 45f + mp * 45f) * (PI / 180f)
                                    val p1 = Offset(center.x + (r * 1.05f * cos(rad)).toFloat(), center.y + (r * 1.05f * sin(rad)).toFloat())
                                    val p2 = Offset(center.x + (r * (1.05f + 0.55f * mp) * cos(rad)).toFloat(), center.y + (r * (1.05f + 0.55f * mp) * sin(rad)).toFloat())
                                    drawLine(
                                        color = Color(0xFFFFB703).copy(alpha = 0.85f * mp),
                                        start = p1,
                                        end = p2,
                                        strokeWidth = 3.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                            }

                            // Planet Sphere
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.White.copy(alpha = 0.88f), primary, secondary),
                                    center = Offset(center.x - r * 0.25f, center.y - r * 0.25f),
                                    radius = r.coerceAtLeast(1f) * 1.45f
                                ),
                                radius = r,
                                center = center
                            )

                            // Icon-specific animated overlay inside sphere
                            when (iconTheme) {
                                OrbitIconTheme.VIOLET_NEBULA -> {
                                    drawFourPointStar(
                                        center = center,
                                        radius = r * 0.65f * mp,
                                        color = Color.White.copy(alpha = 0.95f * mp)
                                    )
                                }
                                OrbitIconTheme.NEPTUNE_PRISM -> {
                                    val dr = r * 0.72f * mp
                                    val diamond = Path().apply {
                                        moveTo(center.x, center.y - dr)
                                        lineTo(center.x + dr * 0.75f, center.y)
                                        lineTo(center.x, center.y + dr)
                                        lineTo(center.x - dr * 0.75f, center.y)
                                        close()
                                    }
                                    drawPath(diamond, color = Color.White.copy(alpha = 0.30f * mp))
                                    drawPath(diamond, color = Color.White.copy(alpha = 0.92f * mp), style = Stroke(width = 1.8.dp.toPx()))
                                }
                                OrbitIconTheme.MARS_CORE -> {
                                    val moonRad = (mp * 390f) * (PI / 180f)
                                    val mx = center.x + (size.width * 0.34f * cos(moonRad)).toFloat()
                                    val my = center.y + (size.height * 0.17f * sin(moonRad)).toFloat()
                                    drawCircle(color = Color(0xFFFFEDD5), radius = 4.8.dp.toPx(), center = Offset(mx, my))
                                }
                                OrbitIconTheme.EMERALD_PULSE -> {
                                    drawCircle(
                                        color = primary.copy(alpha = (1f - mp) * 0.7f),
                                        radius = r + mp * 32.dp.toPx(),
                                        center = center,
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                }
                                else -> Unit
                            }
                        }

                        // Ring sweeps tailored to logo geometry
                        if (ringSweep.value > 0.5f) {
                            when (iconTheme) {
                                OrbitIconTheme.GOLD_SATURN -> {
                                    rotate(degrees = -26f, pivot = center) {
                                        drawArc(
                                            brush = Brush.linearGradient(listOf(Color(0xFFFDE68A), primary, secondary)),
                                            startAngle = 195f,
                                            sweepAngle = ringSweep.value,
                                            useCenter = false,
                                            topLeft = Offset(center.x - size.width * 0.40f, center.y - size.height * 0.145f),
                                            size = Size(size.width * 0.80f, size.height * 0.29f),
                                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                        drawArc(
                                            color = Color(0xFFFEF3C7).copy(alpha = 0.85f),
                                            startAngle = 15f,
                                            sweepAngle = -ringSweep.value,
                                            useCenter = false,
                                            topLeft = Offset(center.x - size.width * 0.31f, center.y - size.height * 0.10f),
                                            size = Size(size.width * 0.62f, size.height * 0.20f),
                                            style = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                    }
                                }
                                OrbitIconTheme.EMERALD_PULSE -> {
                                    rotate(degrees = 28f, pivot = center) {
                                        drawArc(
                                            brush = Brush.linearGradient(listOf(primary, Color.White, secondary)),
                                            startAngle = 180f,
                                            sweepAngle = ringSweep.value,
                                            useCenter = false,
                                            topLeft = Offset(center.x - size.width * 0.37f, center.y - size.height * 0.13f),
                                            size = Size(size.width * 0.74f, size.height * 0.26f),
                                            style = Stroke(width = 5.5.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                    }
                                    rotate(degrees = -28f, pivot = center) {
                                        drawArc(
                                            brush = Brush.linearGradient(listOf(Color.White, secondary, primary)),
                                            startAngle = 0f,
                                            sweepAngle = ringSweep.value,
                                            useCenter = false,
                                            topLeft = Offset(center.x - size.width * 0.37f, center.y - size.height * 0.13f),
                                            size = Size(size.width * 0.74f, size.height * 0.26f),
                                            style = Stroke(width = 5.5.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                    }
                                }
                                else -> {
                                    rotate(degrees = -26f, pivot = center) {
                                        drawArc(
                                            brush = Brush.linearGradient(listOf(primary, Color.White, secondary, primary)),
                                            startAngle = 195f,
                                            sweepAngle = ringSweep.value,
                                            useCenter = false,
                                            topLeft = Offset(center.x - size.width * 0.37f, center.y - size.height * 0.135f),
                                            size = Size(size.width * 0.74f, size.height * 0.27f),
                                            style = Stroke(width = 6.5.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (split > 0.01f) {
                    Text(
                        text = "Orbit",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        letterSpacing = (-1).sp,
                        color = Color.White,
                        modifier = Modifier
                            .offset { IntOffset(wordSlideDp.dp.roundToPx(), 0) }
                            .graphicsLayer { alpha = split.coerceIn(0f, 1f) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isRu) "${iconTheme.labelRu} · ${palette.labelRu}" else "${iconTheme.labelEn} · ${palette.labelEn}",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                letterSpacing = 1.2.sp,
                color = primary,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}

/**
 * Polar morph path for Material You shapes:
 * 0 Circle -> 1 Squircle -> 2 Clover -> 3 Starburst -> 4 Back to Circle!
 */
private fun buildMaterialYouMorphPath(
    center: Offset,
    baseRadius: Float,
    morphStage: Float,
    spinDeg: Float
): Path {
    val steps = 96
    val path = Path()
    val clamped = morphStage.coerceIn(0f, 4f)
    val spinRad = spinDeg * (PI / 180f)

    fun radiusForShape(theta: Double, stageIndex: Int): Float {
        return when (stageIndex) {
            0, 4 -> baseRadius
            1 -> {
                val c = abs(cos(theta)).pow(4.0)
                val s = abs(sin(theta)).pow(4.0)
                baseRadius * 1.06f * (c + s).pow(-0.25).toFloat()
            }
            2 -> baseRadius * (0.86f + 0.24f * cos(4.0 * theta).toFloat())
            else -> baseRadius * (0.90f + 0.16f * cos(8.0 * theta).toFloat())
        }
    }

    val idxA = clamped.toInt().coerceIn(0, 3)
    val idxB = (idxA + 1).coerceIn(1, 4)
    val frac = clamped - idxA

    for (i in 0 until steps) {
        val theta = (i.toDouble() / steps) * 2.0 * PI
        val rA = radiusForShape(theta, idxA)
        val rB = radiusForShape(theta, idxB)
        val r = rA + (rB - rA) * frac
        val worldAngle = theta + spinRad
        val x = center.x + (r * cos(worldAngle)).toFloat()
        val y = center.y + (r * sin(worldAngle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

@Composable
private fun SplashBackdropContainer(
    backgroundBrush: Brush,
    exitAlpha: Float,
    exitScale: Float,
    onSkip: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = exitAlpha
                scaleX = exitScale
                scaleY = exitScale
            }
            .background(backgroundBrush)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSkip
            )
            .testTag("orbit_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

private fun DrawScope.drawFourPointStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    val inner = radius * 0.28f
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        lineTo(center.x + inner, center.y - inner)
        lineTo(center.x + radius, center.y)
        lineTo(center.x + inner, center.y + inner)
        lineTo(center.x, center.y + radius)
        lineTo(center.x - inner, center.y + inner)
        lineTo(center.x - radius, center.y)
        lineTo(center.x - inner, center.y - inner)
        close()
    }
    drawPath(path = path, color = color)
}

@Composable
private fun NothingOsSplashContent(
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    greetingText: String = "Привет",
    isRu: Boolean = true,
    onFinished: () -> Unit
) {
    // Sequence:
    // 1) Orbit dot-matrix logo blinks 3 times like Nothing Glyph LEDs
    // 2) Logo smoothly slides left
    // 3) Word "Orbit" reveals with synchronized Glyph pulse
    // 4) "Привет" (localized) blinks right below like a Glyph notification strip!
    val glyphPulse = remember { Animatable(0f) }
    val slideLeft = remember { Animatable(0f) }
    val wordAlpha = remember { Animatable(0f) }
    val wordPulse = remember { Animatable(0.92f) }
    val greetAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // 3 Glyph LED blinks
        repeat(3) { idx ->
            com.example.util.OrbitSoundPlayer.playGlyphBlip()
            glyphPulse.animateTo(1f, tween(150, easing = FastOutSlowInEasing))
            delay(80)
            if (idx < 2) {
                glyphPulse.animateTo(0.22f, tween(130))
                delay(60)
            }
        }
        // Slide logo left & reveal dot-matrix "Orbit" with synchronized pulse
        launch {
            slideLeft.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)
            )
        }
        delay(120)
        launch { wordAlpha.animateTo(1f, tween(340, easing = FastOutSlowInEasing)) }
        wordPulse.animateTo(1.06f, tween(180, easing = FastOutSlowInEasing))
        wordPulse.animateTo(1f, tween(180, easing = FastOutSlowInEasing))

        // Blinking localized greeting ("Привет") synced with Orbit glyph pulse
        repeat(3) { idx ->
            launch { glyphPulse.animateTo(if (idx % 2 == 0) 0.55f else 1f, tween(170)) }
            greetAlpha.animateTo(1f, tween(190))
            delay(150)
            if (idx < 2) {
                greetAlpha.animateTo(0.18f, tween(150))
            }
        }
        glyphPulse.animateTo(1f, tween(150))
        com.example.util.OrbitSoundPlayer.playSuccess()
        delay(320)

        launch { exitScale.animateTo(1.04f, tween(280)) }
        exitAlpha.animateTo(0f, tween(280))
        onFinished()
    }

    val slide = slideLeft.value
    val logoOffsetDp = (-58f * slide)
    val wordOffsetDp = (46f * slide)

    SplashBackdropContainer(
        backgroundBrush = Brush.radialGradient(listOf(Color(0xFF141414), Color(0xFF040404), Color(0xFF000000))),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        // Subtle dot-matrix background grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 22.dp.toPx()
            var y = step
            while (y < size.height) {
                var x = step
                while (x < size.width) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.045f),
                        radius = 1.2.dp.toPx(),
                        center = Offset(x, y)
                    )
                    x += step
                }
                y += step
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.height(124.dp)) {
                // Blinking Glyph Orbit Dot-Matrix Emblem
                Box(
                    modifier = Modifier
                        .offset { IntOffset(logoOffsetDp.dp.roundToPx(), 0) }
                        .size(88.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val g = glyphPulse.value
                        // Outer blinking Glyph C-arcs
                        drawArc(
                            color = Color.White.copy(alpha = 0.25f + 0.75f * g),
                            startAngle = -55f,
                            sweepAngle = 110f,
                            useCenter = false,
                            topLeft = Offset(8.dp.toPx(), 8.dp.toPx()),
                            size = Size(size.width - 16.dp.toPx(), size.height - 16.dp.toPx()),
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = Color.White.copy(alpha = 0.25f + 0.75f * g),
                            startAngle = 125f,
                            sweepAngle = 110f,
                            useCenter = false,
                            topLeft = Offset(8.dp.toPx(), 8.dp.toPx()),
                            size = Size(size.width - 16.dp.toPx(), size.height - 16.dp.toPx()),
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Center pulsing dot-matrix planet
                        drawCircle(
                            color = Color.White.copy(alpha = 0.35f + 0.65f * g),
                            radius = size.minDimension * 0.20f,
                            center = center
                        )
                        // Red Nothing recording dot
                        drawCircle(
                            color = Color(0xFFFF3B30).copy(alpha = g),
                            radius = 5.5.dp.toPx(),
                            center = Offset(center.x + size.width * 0.26f, center.y - size.height * 0.26f)
                        )
                    }
                }

                // Dot-matrix "Orbit" emerges to the right with synchronized pulse
                if (wordAlpha.value > 0.01f) {
                    Row(
                        modifier = Modifier
                            .offset { IntOffset(wordOffsetDp.dp.roundToPx(), 0) }
                            .graphicsLayer {
                                alpha = wordAlpha.value * (0.65f + 0.35f * glyphPulse.value)
                                scaleX = wordPulse.value
                                scaleY = wordPulse.value
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Orbit",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            letterSpacing = 3.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .background(Color(0xFFFF3B30).copy(alpha = glyphPulse.value), CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Blinking Glyph greeting ("Привет")
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFF1A1A1A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f * greetAlpha.value)),
                modifier = Modifier.alpha(greetAlpha.value)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color(0xFFFF3B30), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = greetingText.uppercase(),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 2.6.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isRu) "NOTHING OS · ГЛИФ-МАТРИЦА" else "NOTHING OS · GLYPH MATRIX",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.62f * wordAlpha.value)
            )
        }
    }
}

@Composable
private fun KosmosSplashContent(
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onFinished: () -> Unit
) {
    val orbitProgress = remember { Animatable(0f) }
    val split = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        com.example.util.OrbitSoundPlayer.playGlyphBlip()
        orbitProgress.animateTo(1f, tween(850, easing = FastOutSlowInEasing))
        split.animateTo(1f, spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow))
        com.example.util.OrbitSoundPlayer.playSuccess()
        delay(380)
        launch { exitScale.animateTo(1.06f, tween(280)) }
        exitAlpha.animateTo(0f, tween(280))
        onFinished()
    }

    SplashBackdropContainer(
        backgroundBrush = Brush.radialGradient(listOf(Color(0xFF330C5E), Color(0xFF110424), Color(0xFF030009))),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val t = orbitProgress.value
            // Twinkling stars
            for (i in 0 until 18) {
                val ang = i * 20.0 * (PI / 180.0)
                val dist = size.minDimension * (0.20f + (i % 4) * 0.11f)
                drawCircle(
                    color = (if (i % 2 == 0) Color(0xFF00E5FF) else Color(0xFFFDE047)).copy(alpha = 0.45f * t),
                    radius = (1.5f + (i % 3)).dp.toPx(),
                    center = Offset(
                        center.x + (dist * cos(ang)).toFloat(),
                        center.y + (dist * sin(ang)).toFloat()
                    )
                )
            }
            // Orbiting comet sweep
            rotate(degrees = -28f + t * 180f, pivot = center) {
                drawArc(
                    brush = Brush.sweepGradient(listOf(Color.Transparent, Color(0xFF00E5FF), Color(0xFFEC4899), Color.White)),
                    startAngle = 0f,
                    sweepAngle = 290f * t,
                    useCenter = false,
                    topLeft = Offset(center.x - 95.dp.toPx(), center.y - 95.dp.toPx()),
                    size = Size(190.dp.toPx(), 190.dp.toPx()),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset((-42f * split.value).dp.roundToPx(), 0) }
                        .size(72.dp)
                        .background(
                            Brush.radialGradient(listOf(Color.White, Color(0xFF8B5CF6), Color(0xFF2E1065))),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Explore, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(34.dp))
                }
                if (split.value > 0.05f) {
                    Text(
                        text = "Orbit",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp,
                        color = Color.White,
                        modifier = Modifier
                            .offset { IntOffset((22f * split.value).dp.roundToPx(), 0) }
                            .alpha(split.value)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isRu) "КОСМОС · ОРБИТА" else "KOSMOS · DEEP SPACE",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 2.4.sp,
                color = Color(0xFF00E5FF),
                modifier = Modifier.alpha(split.value)
            )
        }
    }
}

@Composable
private fun MinecraftSplashContent(
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val slide = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        com.example.util.OrbitSoundPlayer.playClick()
        progress.animateTo(1f, tween(820, easing = FastOutSlowInEasing))
        slide.animateTo(1f, spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow))
        com.example.util.OrbitSoundPlayer.playSuccess()
        delay(340)
        launch { exitScale.animateTo(1.03f, tween(260)) }
        exitAlpha.animateTo(0f, tween(260))
        onFinished()
    }

    SplashBackdropContainer(
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF193215), Color(0xFF0C180A))),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val block = 26.dp.toPx()
            for (r in 0..8) {
                for (c in 0..6) {
                    if ((r + c) % 3 == 0) {
                        drawRect(
                            color = Color(0xFF4CAF50).copy(alpha = 0.05f),
                            topLeft = Offset(c * block * 2.2f, r * block * 3f),
                            size = Size(block, block)
                        )
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = androidx.compose.foundation.shape.CutCornerShape(3.dp),
                    color = Color(0xFF388E3C),
                    border = BorderStroke(3.dp, Color(0xFF81C784)),
                    modifier = Modifier
                        .offset { IntOffset((-34f * slide.value).dp.roundToPx(), 0) }
                        .size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Layers, contentDescription = null, tint = Color(0xFFE8F5E9), modifier = Modifier.size(38.dp))
                    }
                }
                if (slide.value > 0.05f) {
                    Text(
                        text = "ORBIT",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp,
                        letterSpacing = 2.sp,
                        color = Color(0xFFA5D6A7),
                        modifier = Modifier
                            .offset { IntOffset((20f * slide.value).dp.roundToPx(), 0) }
                            .alpha(slide.value)
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .width(210.dp)
                    .height(16.dp)
                    .background(Color(0xFF1C291B), androidx.compose.foundation.shape.CutCornerShape(2.dp))
                    .border(2.dp, Color(0xFF4CAF50), androidx.compose.foundation.shape.CutCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.value)
                        .background(Brush.horizontalGradient(listOf(Color(0xFF4CAF50), Color(0xFF22D3EE))))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isRu) "БЛОЧНЫЙ ДВИЖОК · ${(progress.value * 100).toInt()}%" else "BLOCK ENGINE · ${(progress.value * 100).toInt()}%",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.6.sp,
                color = Color(0xFF81C784)
            )
        }
    }
}

@Composable
private fun SakuraSplashContent(
    palette: ColorPalettePreset,
    iconTheme: OrbitIconTheme,
    isRu: Boolean,
    onFinished: () -> Unit
) {
    val bloom = remember { Animatable(0f) }
    val split = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        com.example.util.OrbitSoundPlayer.playGlyphBlip()
        bloom.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        split.animateTo(1f, spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow))
        com.example.util.OrbitSoundPlayer.playSuccess()
        delay(360)
        launch { exitScale.animateTo(1.04f, tween(280)) }
        exitAlpha.animateTo(0f, tween(280))
        onFinished()
    }

    SplashBackdropContainer(
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF3F0D26), Color(0xFF250817), Color(0xFF16040D))),
        exitAlpha = exitAlpha.value,
        exitScale = exitScale.value,
        onSkip = onFinished
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val t = bloom.value
            for (i in 0 until 14) {
                val px = ((i * 0.17f + t * 0.25f) % 1f) * size.width
                val py = ((i * 0.13f + t * 0.55f) % 1f) * size.height
                rotate(degrees = (i * 35f + t * 140f), pivot = Offset(px, py)) {
                    drawOval(
                        color = Color(0xFFFFB3C6).copy(alpha = 0.55f),
                        topLeft = Offset(px - 7.dp.toPx(), py - 4.dp.toPx()),
                        size = Size(14.dp.toPx(), 8.dp.toPx())
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(
                    modifier = Modifier
                        .offset { IntOffset((-42f * split.value).dp.roundToPx(), 0) }
                        .size((78 * bloom.value.coerceAtLeast(0.2f)).dp)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val r = size.minDimension * 0.28f
                    for (i in 0 until 5) {
                        val ang = (i * 72f + bloom.value * 40f) * (PI / 180f)
                        val petalC = Offset(
                            center.x + (r * 0.7f * cos(ang)).toFloat(),
                            center.y + (r * 0.7f * sin(ang)).toFloat()
                        )
                        drawCircle(color = Color(0xFFFF6B9D).copy(alpha = 0.85f), radius = r * 0.78f, center = petalC)
                    }
                    drawCircle(color = Color.White, radius = r * 0.48f, center = center)
                }
                if (split.value > 0.05f) {
                    Text(
                        text = "Orbit",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp,
                        color = Color(0xFFFFE4EC),
                        modifier = Modifier
                            .offset { IntOffset((20f * split.value).dp.roundToPx(), 0) }
                            .alpha(split.value)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isRu) "САКУРА · ЦВЕТЕНИЕ" else "SAKURA · BLOSSOM",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 2.4.sp,
                color = Color(0xFFFFB3C6),
                modifier = Modifier.alpha(split.value)
            )
        }
    }
}

