package com.example.util

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.MainActivity
import com.example.model.BrowserStyleMode
import com.example.model.OrbitIconTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object OrbitIconManager {

    private const val TAG = "OrbitIconManager"

    enum class LauncherPreset(
        val aliasSuffix: String,
        val labelRu: String,
        val labelEn: String
    ) {
        DEFAULT(".LauncherDefault", "Orbit Классика", "Orbit Classic"),
        GX(".LauncherGxCrimson", "JupiterGX Неон", "JupiterGX Neon"),
        NOTHING(".LauncherNothingOs", "NothingOS Глиф", "NothingOS Glyph"),
        SAKURA(".LauncherSakura", "Сакура", "Sakura"),
        MINECRAFT(".LauncherMinecraft", "Minecraft", "Minecraft"),
        KOSMOS(".LauncherKosmos", "Космос", "Kosmos")
    }

    fun resolvePresetFor(iconTheme: OrbitIconTheme, styleMode: BrowserStyleMode): LauncherPreset = when {
        iconTheme == OrbitIconTheme.GX_CRIMSON || iconTheme == OrbitIconTheme.JUPITER_GX || styleMode == BrowserStyleMode.GAMING_GX -> LauncherPreset.GX
        styleMode == BrowserStyleMode.NOTHING_OS -> LauncherPreset.NOTHING
        iconTheme == OrbitIconTheme.SAKURA_BLOSSOM || styleMode == BrowserStyleMode.SAKURA -> LauncherPreset.SAKURA
        styleMode == BrowserStyleMode.MINECRAFT -> LauncherPreset.MINECRAFT
        iconTheme == OrbitIconTheme.VIOLET_NEBULA || styleMode == BrowserStyleMode.KOSMOS -> LauncherPreset.KOSMOS
        else -> LauncherPreset.DEFAULT
    }

    fun getActivePreset(context: Context): LauncherPreset {
        val pm = context.packageManager
        val pkg = context.packageName
        return LauncherPreset.entries.firstOrNull { preset ->
            val state = runCatching {
                pm.getComponentEnabledSetting(ComponentName(pkg, "$pkg${preset.aliasSuffix}"))
            }.getOrDefault(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
            state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } ?: LauncherPreset.DEFAULT
    }

    fun applyLauncherIconPreset(context: Context, preset: LauncherPreset): Boolean {
        val pm = context.packageManager
        val pkg = context.packageName
        return runCatching {
            LauncherPreset.entries.forEach { entry ->
                val cn = ComponentName(pkg, "$pkg${entry.aliasSuffix}")
                val targetState = if (entry == preset) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }
                pm.setComponentEnabledSetting(cn, targetState, PackageManager.DONT_KILL_APP)
            }
            true
        }.getOrElse { e ->
            Log.w(TAG, "Failed to switch launcher alias: ${e.message}")
            false
        }
    }

    /**
     * Renders an adaptive-safe high-res Bitmap (256x256) for ANY of the 16 OrbitIconThemes
     * OR a custom user photo URI with an Orbit corner badge.
     */
    fun renderOrbitIconBitmap(
        context: Context,
        iconTheme: OrbitIconTheme,
        styleMode: BrowserStyleMode,
        customPhotoUri: String?,
        customPrimaryHex: Long = 0L,
        customSecondaryHex: Long = 0L,
        sizePx: Int = 256
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val cx = sizePx / 2f
        val cy = sizePx / 2f

        val primaryColor = when {
            customPrimaryHex != 0L -> customPrimaryHex.toInt()
            iconTheme == OrbitIconTheme.AUTO_THEME && styleMode == BrowserStyleMode.GAMING_GX -> 0xFFFF1E56.toInt()
            iconTheme == OrbitIconTheme.AUTO_THEME && styleMode == BrowserStyleMode.NOTHING_OS -> 0xFFFFFFFF.toInt()
            iconTheme == OrbitIconTheme.AUTO_THEME && styleMode == BrowserStyleMode.SAKURA -> 0xFFFF6B9D.toInt()
            iconTheme == OrbitIconTheme.AUTO_THEME && styleMode == BrowserStyleMode.MINECRAFT -> 0xFF4CAF50.toInt()
            else -> iconTheme.primaryHex.toInt()
        }
        val secondaryColor = when {
            customSecondaryHex != 0L -> customSecondaryHex.toInt()
            iconTheme == OrbitIconTheme.AUTO_THEME && styleMode == BrowserStyleMode.GAMING_GX -> 0xFF00F0FF.toInt()
            iconTheme == OrbitIconTheme.AUTO_THEME && styleMode == BrowserStyleMode.NOTHING_OS -> 0xFFFF3B30.toInt()
            else -> iconTheme.secondaryHex.toInt()
        }

        // Custom user photo branch (via Android Photo Picker — zero permissions!)
        val loadedPhoto = loadScaledPhoto(context, customPhotoUri, sizePx)
        if (loadedPhoto != null) {
            canvas.drawBitmap(loadedPhoto, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
            loadedPhoto.recycle()

            // Crisp Orbit badge in bottom-right safe zone
            val badgeR = sizePx * 0.14f
            val bx = sizePx * 0.72f
            val by = sizePx * 0.72f
            val badgeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xEE090D1A.toInt()
            }
            val badgeStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = sizePx * 0.018f
                color = primaryColor
            }
            canvas.drawCircle(bx, by, badgeR, badgeBgPaint)
            canvas.drawCircle(bx, by, badgeR, badgeStroke)
            val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = primaryColor }
            canvas.drawCircle(bx, by, badgeR * 0.42f, dotPaint)
            return bitmap
        }

        // Background gradient tailored to styleMode / iconTheme
        val bgTop: Int
        val bgBottom: Int
        when (styleMode) {
            BrowserStyleMode.NOTHING_OS -> {
                bgTop = 0xFF050505.toInt()
                bgBottom = 0xFF141414.toInt()
            }
            BrowserStyleMode.MINECRAFT -> {
                bgTop = 0xFF1B3018.toInt()
                bgBottom = 0xFF0E190D.toInt()
            }
            BrowserStyleMode.SAKURA -> {
                bgTop = 0xFFFFE4EC.toInt()
                bgBottom = 0xFFFFB8D0.toInt()
            }
            BrowserStyleMode.KOSMOS -> {
                bgTop = 0xFF210845.toInt()
                bgBottom = 0xFF080214.toInt()
            }
            else -> {
                bgTop = if (iconTheme == OrbitIconTheme.PURE_WHITE) 0xFF1E293B.toInt() else 0xFF0B1120.toInt()
                bgBottom = 0xFF050811.toInt()
            }
        }

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), bgTop, bgBottom, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), bgPaint)

        // Subtle glow halo inside adaptive safe zone
        val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx,
                cy,
                sizePx * 0.36f,
                intArrayOf(
                    Color.argb(96, Color.red(primaryColor), Color.green(primaryColor), Color.blue(primaryColor)),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, sizePx * 0.36f, haloPaint)

        // Planet sphere (inside adaptive 66% safe zone)
        val planetR = sizePx * 0.165f
        val isJupiterIcon = iconTheme == OrbitIconTheme.JUPITER_GX ||
            iconTheme == OrbitIconTheme.GX_CRIMSON ||
            (iconTheme == OrbitIconTheme.AUTO_THEME && styleMode == BrowserStyleMode.GAMING_GX)
        val isMercuryIcon = iconTheme == OrbitIconTheme.MERCURY_CORE

        val spherePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = when {
                iconTheme.isRainbow -> SweepGradient(
                    cx,
                    cy,
                    intArrayOf(
                        0xFFFF3B30.toInt(),
                        0xFFFFCC00.toInt(),
                        0xFF34C759.toInt(),
                        0xFF00D4FF.toInt(),
                        0xFFAF52DE.toInt(),
                        0xFFFF3B30.toInt()
                    ),
                    null
                )
                isJupiterIcon -> LinearGradient(
                    cx - planetR,
                    cy - planetR,
                    cx + planetR,
                    cy + planetR,
                    intArrayOf(
                        0xFFFFE0B2.toInt(),
                        0xFFFF6B35.toInt(),
                        0xFFFF1E56.toInt(),
                        0xFFFF9F43.toInt(),
                        0xFF9E1946.toInt()
                    ),
                    floatArrayOf(0f, 0.25f, 0.52f, 0.76f, 1f),
                    Shader.TileMode.CLAMP
                )
                isMercuryIcon -> RadialGradient(
                    cx - planetR * 0.35f,
                    cy - planetR * 0.35f,
                    planetR * 1.55f,
                    intArrayOf(Color.WHITE, 0xFFE2E8F0.toInt(), 0xFFFF3E66.toInt()),
                    floatArrayOf(0f, 0.52f, 1f),
                    Shader.TileMode.CLAMP
                )
                else -> RadialGradient(
                    cx - planetR * 0.32f,
                    cy - planetR * 0.32f,
                    planetR * 1.6f,
                    intArrayOf(Color.WHITE, primaryColor, secondaryColor),
                    floatArrayOf(0f, 0.45f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
        }
        canvas.drawCircle(cx, cy, planetR, spherePaint)

        // Jupiter Great Red Spot storm accent
        if (isJupiterIcon) {
            val spotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFF1E56.toInt() }
            val spotRect = RectF(
                cx + planetR * 0.08f,
                cy + planetR * 0.12f,
                cx + planetR * 0.58f,
                cy + planetR * 0.42f
            )
            canvas.drawOval(spotRect, spotPaint)
        }

        // Tilted orbital ring
        canvas.save()
        canvas.rotate(-24f, cx, cy)
        val ringRect = RectF(cx - sizePx * 0.29f, cy - sizePx * 0.105f, cx + sizePx * 0.29f, cy + sizePx * 0.105f)
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = sizePx * 0.044f
            strokeCap = Paint.Cap.ROUND
            shader = LinearGradient(
                ringRect.left,
                ringRect.top,
                ringRect.right,
                ringRect.bottom,
                intArrayOf(primaryColor, Color.WHITE, secondaryColor),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawOval(ringRect, ringPaint)
        canvas.restore()

        // Optional NothingOS red dot accent or star spark
        if (styleMode == BrowserStyleMode.NOTHING_OS) {
            val glyphDot = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFF3B30.toInt() }
            canvas.drawCircle(cx + sizePx * 0.21f, cy - sizePx * 0.20f, sizePx * 0.036f, glyphDot)
        } else {
            val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
            val sx = cx + sizePx * 0.20f
            val sy = cy - sizePx * 0.20f
            val sr = sizePx * 0.038f
            for (i in 0 until 4) {
                val ang = Math.toRadians(i * 90.0)
                canvas.drawLine(
                    sx,
                    sy,
                    (sx + sr * cos(ang)).toFloat(),
                    (sy + sr * sin(ang)).toFloat(),
                    starPaint.apply { strokeWidth = sizePx * 0.012f; strokeCap = Paint.Cap.ROUND }
                )
            }
        }

        return bitmap
    }

    private fun loadScaledPhoto(context: Context, uriStr: String?, sizePx: Int): Bitmap? {
        if (uriStr.isNullOrBlank()) return null
        return runCatching {
            val uri = Uri.parse(uriStr)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val raw = BitmapFactory.decodeStream(stream) ?: return null
                val minSide = min(raw.width, raw.height).coerceAtLeast(1)
                val xOff = (raw.width - minSide) / 2
                val yOff = (raw.height - minSide) / 2
                val square = Bitmap.createBitmap(raw, xOff, yOff, minSide, minSide)
                if (square !== raw) raw.recycle()
                val matrix = Matrix().apply {
                    val scale = sizePx.toFloat() / minSide.toFloat()
                    postScale(scale, scale)
                }
                val scaled = Bitmap.createBitmap(square, 0, 0, minSide, minSide, matrix, true)
                if (scaled !== square) square.recycle()
                scaled
            }
        }.getOrNull()
    }

    /**
     * Requests an instant home screen shortcut with the rendered adaptive bitmap (no waiting!).
     */
    fun pinIconToHomeScreen(
        context: Context,
        label: String,
        iconTheme: OrbitIconTheme,
        styleMode: BrowserStyleMode,
        customPhotoUri: String?,
        customPrimaryHex: Long = 0L,
        customSecondaryHex: Long = 0L
    ): PinResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return PinResult.UNSUPPORTED
        }
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            ?: return PinResult.UNSUPPORTED
        if (!shortcutManager.isRequestPinShortcutSupported) {
            return PinResult.UNSUPPORTED
        }

        return runCatching {
            val bitmap = renderOrbitIconBitmap(
                context = context,
                iconTheme = iconTheme,
                styleMode = styleMode,
                customPhotoUri = customPhotoUri,
                customPrimaryHex = customPrimaryHex,
                customSecondaryHex = customSecondaryHex,
                sizePx = 256
            )
            val androidIcon = Icon.createWithAdaptiveBitmap(bitmap)
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("selected_icon", iconTheme.name)
            }
            val safeId = "orbit_${iconTheme.name}_${if (!customPhotoUri.isNullOrBlank()) "custom" else styleMode.name}_${System.currentTimeMillis() % 10000}"
            val shortcut = ShortcutInfo.Builder(context, safeId)
                .setShortLabel(label.take(18).ifBlank { "Orbit" })
                .setLongLabel("Orbit: $label".take(36))
                .setIcon(androidIcon)
                .setIntent(launchIntent)
                .build()

            val callbackIntent = PendingIntent.getBroadcast(
                context,
                safeId.hashCode(),
                Intent("com.example.ORBIT_SHORTCUT_PINNED").setPackage(context.packageName),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val dispatched = shortcutManager.requestPinShortcut(shortcut, callbackIntent.intentSender)
            if (dispatched) PinResult.DISPATCHED else PinResult.FAILED
        }.getOrElse { e ->
            Log.w(TAG, "Failed to pin shortcut: ${e.message}")
            PinResult.FAILED
        }
    }

    enum class PinResult {
        DISPATCHED,
        UNSUPPORTED,
        FAILED
    }
}
