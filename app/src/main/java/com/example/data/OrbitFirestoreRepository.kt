package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.example.model.AntivirusProvider
import com.example.model.AppLanguage
import com.example.model.BrowserStyleMode
import com.example.model.ColorPalettePreset
import com.example.model.ExpressiveShapeStyle
import com.example.model.FavoriteSite
import com.example.model.GlassTransparencyLevel
import com.example.model.HistoryEntry
import com.example.model.HomeLayoutStyle
import com.example.model.MercuryAiModel
import com.example.model.NAccountProfile
import com.example.model.OrbitAnimationStyle
import com.example.model.OrbitCornerStyle
import com.example.model.OrbitDockStyle
import com.example.model.OrbitIconTheme
import com.example.model.OrbitLogoScale
import com.example.model.OrbitSettings
import com.example.model.PendingNAccountRegistration
import com.example.model.RedAiStyle
import com.example.model.SearchEngine
import com.example.model.SyncedTab
import com.example.model.ThemeBrightness
import com.example.model.VpnRegion
import com.example.model.WallpaperStyle
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

class OrbitFirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth,
    private val guestPrefs: SharedPreferences? = null,
    private val appContext: Context? = null
) {
    constructor(context: Context) : this(
        db = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        guestPrefs = context.applicationContext.getSharedPreferences("orbit_guest_prefs", Context.MODE_PRIVATE),
        appContext = context.applicationContext
    )

    private fun userCachePrefs(uid: String): SharedPreferences? {
        if (uid == "guest_user") return guestPrefs
        return appContext?.getSharedPreferences("orbit_uid_cache_$uid", Context.MODE_PRIVATE)
            ?: guestPrefs
    }

    private fun readSettingsFromPrefs(prefs: SharedPreferences?, userId: String): OrbitSettings {
        if (prefs == null) {
            return OrbitSettings(
                userId = userId,
                themeBrightness = ThemeBrightness.LIGHT.name,
                styleMode = BrowserStyleMode.LIQUID_GLASS.name,
                colorPalette = ColorPalettePreset.ORBIT_CYAN.name
            )
        }
        return OrbitSettings(
            userId = userId,
            styleMode = prefs.getString("styleMode", BrowserStyleMode.LIQUID_GLASS.name) ?: BrowserStyleMode.LIQUID_GLASS.name,
            themeBrightness = prefs.getString("themeBrightness", ThemeBrightness.LIGHT.name) ?: ThemeBrightness.LIGHT.name,
            colorPalette = prefs.getString("colorPalette", ColorPalettePreset.ORBIT_CYAN.name) ?: ColorPalettePreset.ORBIT_CYAN.name,
            wallpaperStyle = prefs.getString("wallpaperStyle", WallpaperStyle.NONE.name) ?: WallpaperStyle.NONE.name,
            homeLayout = prefs.getString("homeLayout", HomeLayoutStyle.CLASSIC.name) ?: HomeLayoutStyle.CLASSIC.name,
            dockStyle = prefs.getString("dockStyle", OrbitDockStyle.SAFARI_CAPSULE.name) ?: OrbitDockStyle.SAFARI_CAPSULE.name,
            monarchG2EngineEnabled = prefs.getBoolean("monarchG2EngineEnabled", true),
            iconTheme = prefs.getString("iconTheme", OrbitIconTheme.AUTO_THEME.name) ?: OrbitIconTheme.AUTO_THEME.name,
            glassTransparency = prefs.getString("glassTransparency", GlassTransparencyLevel.FROSTED.name)
                ?: GlassTransparencyLevel.FROSTED.name,
            animationStyle = prefs.getString("animationStyle", OrbitAnimationStyle.FLUID_SPRING.name)
                ?: OrbitAnimationStyle.FLUID_SPRING.name,
            cornerStyle = prefs.getString("cornerStyle", OrbitCornerStyle.ROUNDED.name)
                ?: OrbitCornerStyle.ROUNDED.name,
            starfieldParticlesEnabled = prefs.getBoolean("starfieldParticlesEnabled", true),
            expressiveShapeStyle = prefs.getString("expressiveShapeStyle", ExpressiveShapeStyle.AUTO_MIX.name)
                ?: ExpressiveShapeStyle.AUTO_MIX.name,
            homeLogoScale = prefs.getString("homeLogoScale", OrbitLogoScale.MEDIUM.name) ?: OrbitLogoScale.MEDIUM.name,
            themedIconsAdaptApp = prefs.getBoolean("themedIconsAdaptApp", true),
            compactStatusPills = prefs.getBoolean("compactStatusPills", true),
            showFavorites = prefs.getBoolean("showFavorites", true),
            showPrivacyReport = prefs.getBoolean("showPrivacyReport", true),
            showRedAiBar = prefs.getBoolean("showRedAiBar", true),
            showCosmosWidget = prefs.getBoolean("showCosmosWidget", true),
            showQuickNotesWidget = prefs.getBoolean("showQuickNotesWidget", false),
            quickNotesText = prefs.getString("quickNotesText", "") ?: "",
            showGamingTelemetry = prefs.getBoolean("showGamingTelemetry", false),
            adBlockEnabled = prefs.getBoolean("adBlockEnabled", true),
            antiTrackingEnabled = prefs.getBoolean("antiTrackingEnabled", true),
            httpsOnlyEnabled = prefs.getBoolean("httpsOnlyEnabled", true),
            webRtcLeakProtection = prefs.getBoolean("webRtcLeakProtection", true),
            canvasFingerprintProtection = prefs.getBoolean("canvasFingerprintProtection", true),
            doNotTrackEnabled = prefs.getBoolean("doNotTrackEnabled", true),
            cookieAutoPurgeEnabled = prefs.getBoolean("cookieAutoPurgeEnabled", true),
            vpnEnabled = prefs.getBoolean("vpnEnabled", false),
            vpnRegion = prefs.getString("vpnRegion", VpnRegion.AMSTERDAM.name) ?: VpnRegion.AMSTERDAM.name,
            antivirusProvider = prefs.getString("antivirusProvider", AntivirusProvider.KASPERSKY.name)
                ?: AntivirusProvider.KASPERSKY.name,
            gamingRgbEnabled = prefs.getBoolean("gamingRgbEnabled", true),
            gamingHapticsEnabled = prefs.getBoolean("gamingHapticsEnabled", true),
            performanceModeEnabled = prefs.getBoolean("performanceModeEnabled", false),
            ramLimitMb = prefs.getLong("ramLimitMb", 2048L),
            tabHibernationEnabled = prefs.getBoolean("tabHibernationEnabled", true),
            searchEngine = prefs.getString("searchEngine", SearchEngine.GOOGLE.name) ?: SearchEngine.GOOGLE.name,
            appLanguage = prefs.getString("appLanguage", AppLanguage.RU.name) ?: AppLanguage.RU.name,
            redAiAutoSummarize = prefs.getBoolean("redAiAutoSummarize", false),
            redAiSafeLinkScan = prefs.getBoolean("redAiSafeLinkScan", true),
            redAiStyle = prefs.getString("redAiStyle", RedAiStyle.CONCISE.name) ?: RedAiStyle.CONCISE.name,
            mercuryAiModel = prefs.getString("mercuryAiModel", MercuryAiModel.MERCURY_38_FLASH.modelId)
                ?: MercuryAiModel.MERCURY_38_FLASH.modelId,
            notifyBlockedTrackers = prefs.getBoolean("notifyBlockedTrackers", true),
            notifyCloudSync = prefs.getBoolean("notifyCloudSync", true),
            notifySecurityAlerts = prefs.getBoolean("notifySecurityAlerts", true),
            soundEffectsEnabled = prefs.getBoolean("soundEffectsEnabled", true),
            customAccentHex = prefs.getLong("customAccentHex", 0L),
            customSecondaryHex = prefs.getLong("customSecondaryHex", 0L),
            customCornerRadiusDp = prefs.getLong("customCornerRadiusDp", 20L),
            borderGlowIntensity = prefs.getLong("borderGlowIntensity", 50L),
            glassBlurLevel = prefs.getLong("glassBlurLevel", 50L),
            customIconUri = prefs.getString("customIconUri", "") ?: "",
            dnsOverHttpsEnabled = prefs.getBoolean("dnsOverHttpsEnabled", true),
            blockPopupsEnabled = prefs.getBoolean("blockPopupsEnabled", true),
            antiPhishingShieldEnabled = prefs.getBoolean("antiPhishingShieldEnabled", true),
            floatingSpeedHudEnabled = prefs.getBoolean("floatingSpeedHudEnabled", false),
            adsBlockedTotal = prefs.getLong("adsBlockedTotal", 0L),
            trackersBlockedTotal = prefs.getLong("trackersBlockedTotal", 0L)
        )
    }

    private fun writeSettingsToPrefs(prefs: SharedPreferences?, s: OrbitSettings) {
        if (prefs == null) return
        prefs.edit()
            .putString("styleMode", s.styleMode)
            .putString("themeBrightness", s.themeBrightness)
            .putString("colorPalette", s.colorPalette)
            .putString("wallpaperStyle", s.wallpaperStyle)
            .putString("homeLayout", s.homeLayout)
            .putString("dockStyle", s.dockStyle)
            .putBoolean("monarchG2EngineEnabled", s.monarchG2EngineEnabled)
            .putString("iconTheme", s.iconTheme)
            .putString("glassTransparency", s.glassTransparency)
            .putString("animationStyle", s.animationStyle)
            .putString("cornerStyle", s.cornerStyle)
            .putBoolean("starfieldParticlesEnabled", s.starfieldParticlesEnabled)
            .putString("expressiveShapeStyle", s.expressiveShapeStyle)
            .putString("homeLogoScale", s.homeLogoScale)
            .putBoolean("themedIconsAdaptApp", s.themedIconsAdaptApp)
            .putBoolean("compactStatusPills", s.compactStatusPills)
            .putBoolean("showFavorites", s.showFavorites)
            .putBoolean("showPrivacyReport", s.showPrivacyReport)
            .putBoolean("showRedAiBar", s.showRedAiBar)
            .putBoolean("showCosmosWidget", s.showCosmosWidget)
            .putBoolean("showQuickNotesWidget", s.showQuickNotesWidget)
            .putString("quickNotesText", s.quickNotesText)
            .putBoolean("showGamingTelemetry", s.showGamingTelemetry)
            .putBoolean("adBlockEnabled", s.adBlockEnabled)
            .putBoolean("antiTrackingEnabled", s.antiTrackingEnabled)
            .putBoolean("httpsOnlyEnabled", s.httpsOnlyEnabled)
            .putBoolean("webRtcLeakProtection", s.webRtcLeakProtection)
            .putBoolean("canvasFingerprintProtection", s.canvasFingerprintProtection)
            .putBoolean("doNotTrackEnabled", s.doNotTrackEnabled)
            .putBoolean("cookieAutoPurgeEnabled", s.cookieAutoPurgeEnabled)
            .putBoolean("vpnEnabled", s.vpnEnabled)
            .putString("vpnRegion", s.vpnRegion)
            .putString("antivirusProvider", s.antivirusProvider)
            .putBoolean("gamingRgbEnabled", s.gamingRgbEnabled)
            .putBoolean("gamingHapticsEnabled", s.gamingHapticsEnabled)
            .putBoolean("performanceModeEnabled", s.performanceModeEnabled)
            .putLong("ramLimitMb", s.ramLimitMb)
            .putBoolean("tabHibernationEnabled", s.tabHibernationEnabled)
            .putString("searchEngine", s.searchEngine)
            .putString("appLanguage", s.appLanguage)
            .putBoolean("redAiAutoSummarize", s.redAiAutoSummarize)
            .putBoolean("redAiSafeLinkScan", s.redAiSafeLinkScan)
            .putString("redAiStyle", s.redAiStyle)
            .putString("mercuryAiModel", s.mercuryAiModel)
            .putBoolean("notifyBlockedTrackers", s.notifyBlockedTrackers)
            .putBoolean("notifyCloudSync", s.notifyCloudSync)
            .putBoolean("notifySecurityAlerts", s.notifySecurityAlerts)
            .putBoolean("soundEffectsEnabled", s.soundEffectsEnabled)
            .putLong("customAccentHex", s.customAccentHex)
            .putLong("customSecondaryHex", s.customSecondaryHex)
            .putLong("customCornerRadiusDp", s.customCornerRadiusDp)
            .putLong("borderGlowIntensity", s.borderGlowIntensity)
            .putLong("glassBlurLevel", s.glassBlurLevel)
            .putString("customIconUri", s.customIconUri)
            .putBoolean("dnsOverHttpsEnabled", s.dnsOverHttpsEnabled)
            .putBoolean("blockPopupsEnabled", s.blockPopupsEnabled)
            .putBoolean("antiPhishingShieldEnabled", s.antiPhishingShieldEnabled)
            .putBoolean("floatingSpeedHudEnabled", s.floatingSpeedHudEnabled)
            .putLong("adsBlockedTotal", s.adsBlockedTotal)
            .putLong("trackersBlockedTotal", s.trackersBlockedTotal)
            .apply()
    }

    private fun applyFieldsToSettings(base: OrbitSettings, fields: Map<String, Any>): OrbitSettings {
        var updated = base
        fields.forEach { (k, v) ->
            when (k) {
                "styleMode" -> updated = updated.copy(styleMode = v.toString())
                "themeBrightness" -> updated = updated.copy(themeBrightness = v.toString())
                "colorPalette" -> updated = updated.copy(colorPalette = v.toString())
                "wallpaperStyle" -> updated = updated.copy(wallpaperStyle = v.toString())
                "homeLayout" -> updated = updated.copy(homeLayout = v.toString())
                "dockStyle" -> updated = updated.copy(dockStyle = v.toString())
                "monarchG2EngineEnabled" -> updated = updated.copy(monarchG2EngineEnabled = v as? Boolean ?: true)
                "iconTheme" -> updated = updated.copy(iconTheme = v.toString())
                "glassTransparency" -> updated = updated.copy(glassTransparency = v.toString())
                "animationStyle" -> updated = updated.copy(animationStyle = v.toString())
                "cornerStyle" -> updated = updated.copy(cornerStyle = v.toString())
                "starfieldParticlesEnabled" -> updated = updated.copy(starfieldParticlesEnabled = v as? Boolean ?: true)
                "expressiveShapeStyle" -> updated = updated.copy(expressiveShapeStyle = v.toString())
                "homeLogoScale" -> updated = updated.copy(homeLogoScale = v.toString())
                "themedIconsAdaptApp" -> updated = updated.copy(themedIconsAdaptApp = v as? Boolean ?: true)
                "compactStatusPills" -> updated = updated.copy(compactStatusPills = v as? Boolean ?: true)
                "showFavorites" -> updated = updated.copy(showFavorites = v as? Boolean ?: true)
                "showPrivacyReport" -> updated = updated.copy(showPrivacyReport = v as? Boolean ?: true)
                "showRedAiBar" -> updated = updated.copy(showRedAiBar = v as? Boolean ?: true)
                "showCosmosWidget" -> updated = updated.copy(showCosmosWidget = v as? Boolean ?: true)
                "showQuickNotesWidget" -> updated = updated.copy(showQuickNotesWidget = v as? Boolean ?: false)
                "quickNotesText" -> updated = updated.copy(quickNotesText = v.toString())
                "showGamingTelemetry" -> updated = updated.copy(showGamingTelemetry = v as? Boolean ?: false)
                "adBlockEnabled" -> updated = updated.copy(adBlockEnabled = v as? Boolean ?: true)
                "antiTrackingEnabled" -> updated = updated.copy(antiTrackingEnabled = v as? Boolean ?: true)
                "httpsOnlyEnabled" -> updated = updated.copy(httpsOnlyEnabled = v as? Boolean ?: true)
                "webRtcLeakProtection" -> updated = updated.copy(webRtcLeakProtection = v as? Boolean ?: true)
                "canvasFingerprintProtection" -> updated = updated.copy(canvasFingerprintProtection = v as? Boolean ?: true)
                "doNotTrackEnabled" -> updated = updated.copy(doNotTrackEnabled = v as? Boolean ?: true)
                "cookieAutoPurgeEnabled" -> updated = updated.copy(cookieAutoPurgeEnabled = v as? Boolean ?: true)
                "vpnEnabled" -> updated = updated.copy(vpnEnabled = v as? Boolean ?: false)
                "vpnRegion" -> updated = updated.copy(vpnRegion = v.toString())
                "antivirusProvider" -> updated = updated.copy(antivirusProvider = v.toString())
                "gamingRgbEnabled" -> updated = updated.copy(gamingRgbEnabled = v as? Boolean ?: true)
                "gamingHapticsEnabled" -> updated = updated.copy(gamingHapticsEnabled = v as? Boolean ?: true)
                "performanceModeEnabled" -> updated = updated.copy(performanceModeEnabled = v as? Boolean ?: false)
                "ramLimitMb" -> updated = updated.copy(ramLimitMb = (v as? Number)?.toLong() ?: 2048L)
                "tabHibernationEnabled" -> updated = updated.copy(tabHibernationEnabled = v as? Boolean ?: true)
                "searchEngine" -> updated = updated.copy(searchEngine = v.toString())
                "appLanguage" -> updated = updated.copy(appLanguage = v.toString())
                "redAiAutoSummarize" -> updated = updated.copy(redAiAutoSummarize = v as? Boolean ?: false)
                "redAiSafeLinkScan" -> updated = updated.copy(redAiSafeLinkScan = v as? Boolean ?: true)
                "redAiStyle" -> updated = updated.copy(redAiStyle = v.toString())
                "mercuryAiModel" -> updated = updated.copy(mercuryAiModel = MercuryAiModel.safeValueOf(v.toString()).modelId)
                "notifyBlockedTrackers" -> updated = updated.copy(notifyBlockedTrackers = v as? Boolean ?: true)
                "notifyCloudSync" -> updated = updated.copy(notifyCloudSync = v as? Boolean ?: true)
                "notifySecurityAlerts" -> updated = updated.copy(notifySecurityAlerts = v as? Boolean ?: true)
                "soundEffectsEnabled" -> updated = updated.copy(soundEffectsEnabled = v as? Boolean ?: true)
                "customAccentHex" -> updated = updated.copy(customAccentHex = (v as? Number)?.toLong() ?: 0L)
                "customSecondaryHex" -> updated = updated.copy(customSecondaryHex = (v as? Number)?.toLong() ?: 0L)
                "customCornerRadiusDp" -> updated = updated.copy(customCornerRadiusDp = (v as? Number)?.toLong() ?: 20L)
                "borderGlowIntensity" -> updated = updated.copy(borderGlowIntensity = (v as? Number)?.toLong() ?: 50L)
                "glassBlurLevel" -> updated = updated.copy(glassBlurLevel = (v as? Number)?.toLong() ?: 50L)
                "customIconUri" -> updated = updated.copy(customIconUri = v.toString())
                "dnsOverHttpsEnabled" -> updated = updated.copy(dnsOverHttpsEnabled = v as? Boolean ?: true)
                "blockPopupsEnabled" -> updated = updated.copy(blockPopupsEnabled = v as? Boolean ?: true)
                "antiPhishingShieldEnabled" -> updated = updated.copy(antiPhishingShieldEnabled = v as? Boolean ?: true)
                "floatingSpeedHudEnabled" -> updated = updated.copy(floatingSpeedHudEnabled = v as? Boolean ?: false)
            }
        }
        return updated
    }

    private fun defaultFavorites(): List<FavoriteSite> = listOf(
        FavoriteSite(id = "fav_apple", userId = "guest_user", title = "Apple", url = "https://www.apple.com", iconBadge = "APPLE", accentHex = 0xFF1C1C1EL, sortOrder = 0L),
        FavoriteSite(id = "fav_bing", userId = "guest_user", title = "Bing", url = "https://www.bing.com", iconBadge = "BING", accentHex = 0xFF008373L, sortOrder = 1L),
        FavoriteSite(id = "fav_google", userId = "guest_user", title = "Google", url = "https://www.google.com", iconBadge = "GOOGLE", accentHex = 0xFF4285F4L, sortOrder = 2L),
        FavoriteSite(id = "fav_yahoo", userId = "guest_user", title = "Yahoo", url = "https://www.yahoo.com", iconBadge = "YAHOO", accentHex = 0xFF6001D2L, sortOrder = 3L)
    )

    private fun loadGuestFavorites(): List<FavoriteSite> {
        val raw = guestPrefs?.getString("guest_favorites_json", null) ?: return defaultFavorites()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        FavoriteSite(
                            id = obj.getString("id"),
                            userId = "guest_user",
                            title = obj.getString("title"),
                            url = obj.getString("url"),
                            iconBadge = obj.optString("iconBadge", "WEB"),
                            accentHex = obj.optLong("accentHex", 0xFF00D4FFL),
                            sortOrder = obj.optLong("sortOrder", i.toLong())
                        )
                    )
                }
            }.ifEmpty { defaultFavorites() }
        }.getOrElse { defaultFavorites() }
    }

    private fun persistGuestFavorites(favs: List<FavoriteSite>) {
        val prefs = guestPrefs ?: return
        val arr = JSONArray()
        favs.forEach { fav ->
            arr.put(
                JSONObject().apply {
                    put("id", fav.id)
                    put("title", fav.title)
                    put("url", fav.url)
                    put("iconBadge", fav.iconBadge)
                    put("accentHex", fav.accentHex)
                    put("sortOrder", fav.sortOrder)
                }
            )
        }
        prefs.edit().putString("guest_favorites_json", arr.toString()).apply()
    }

    private fun loadGuestProfile(): NAccountProfile {
        val prefs = guestPrefs
        if (prefs == null) {
            return NAccountProfile(
                userId = "guest_user",
                naccountHandle = "@guest",
                displayName = "Гостевой профиль",
                cloudSyncEnabled = false
            )
        }
        val handle = prefs.getString("guest_naccount_handle", "@guest")?.takeIf { it.isNotBlank() } ?: "@guest"
        val name = prefs.getString("guest_display_name", "Гостевой профиль")?.takeIf { it.isNotBlank() } ?: "Гостевой профиль"
        return NAccountProfile(
            userId = "guest_user",
            naccountHandle = handle,
            displayName = name,
            vkHandle = prefs.getString("guest_vk_handle", null)?.takeIf { it.isNotBlank() },
            protonHandle = prefs.getString("guest_proton_handle", null)?.takeIf { it.isNotBlank() },
            telegramHandle = prefs.getString("guest_telegram_handle", null)?.takeIf { it.isNotBlank() },
            cloudSyncEnabled = false,
            syncBookmarks = prefs.getBoolean("guest_sync_bookmarks", true),
            syncSettings = prefs.getBoolean("guest_sync_settings", true),
            syncHistory = prefs.getBoolean("guest_sync_history", true)
        )
    }

    private fun persistGuestProfile(p: NAccountProfile) {
        guestPrefs?.edit()
            ?.putString("guest_naccount_handle", p.naccountHandle)
            ?.putString("guest_display_name", p.displayName)
            ?.putString("guest_vk_handle", p.vkHandle ?: "")
            ?.putString("guest_proton_handle", p.protonHandle ?: "")
            ?.putString("guest_telegram_handle", p.telegramHandle ?: "")
            ?.putBoolean("guest_sync_bookmarks", p.syncBookmarks)
            ?.putBoolean("guest_sync_settings", p.syncSettings)
            ?.putBoolean("guest_sync_history", p.syncHistory)
            ?.apply()
    }

    // Local guest state backed by SharedPreferences for users browsing without an account
    private val guestSettingsFlow = MutableStateFlow(readSettingsFromPrefs(guestPrefs, "guest_user"))
    private val guestFavoritesFlow = MutableStateFlow(loadGuestFavorites())
    private val guestHistoryFlow = MutableStateFlow<List<HistoryEntry>>(emptyList())
    private val guestTabsFlow = MutableStateFlow<List<SyncedTab>>(emptyList())
    private val guestProfileFlow = MutableStateFlow<NAccountProfile?>(loadGuestProfile())

    private fun isGuestMode(): Boolean = auth.currentUser == null

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Orbit Cloud.")
    }

    private fun handleFlowError(error: Throwable, op: OperationType, path: String, uid: String) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, op, path)
        }
    }

    companion object {
        private const val PENDING_REG_PREFS = "orbit_pending_registration"

        fun savePendingRegistration(context: Context, reg: PendingNAccountRegistration) {
            val cleanHandle = reg.naccountHandle.trim().let {
                if (it.startsWith("@")) it else "@$it"
            }.take(32)
            val cleanName = reg.displayName.trim().take(60)
            context.applicationContext
                .getSharedPreferences(PENDING_REG_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean("pending", true)
                .putString("naccountHandle", cleanHandle)
                .putString("displayName", cleanName)
                .putString("vkHandle", reg.vkHandle?.trim()?.take(60) ?: "")
                .putString("protonHandle", reg.protonHandle?.trim()?.take(100) ?: "")
                .putString("telegramHandle", reg.telegramHandle?.trim()?.take(60) ?: "")
                .putBoolean("cloudSyncEnabled", reg.cloudSyncEnabled)
                .putBoolean("syncBookmarks", reg.syncBookmarks)
                .putBoolean("syncSettings", reg.syncSettings)
                .putBoolean("syncHistory", reg.syncHistory)
                .commit()
        }

        fun consumePendingRegistration(context: Context?): PendingNAccountRegistration? {
            val prefs = context?.applicationContext
                ?.getSharedPreferences(PENDING_REG_PREFS, Context.MODE_PRIVATE)
                ?: return null
            if (!prefs.getBoolean("pending", false)) return null
            val handle = prefs.getString("naccountHandle", "")?.trim().orEmpty()
            val name = prefs.getString("displayName", "")?.trim().orEmpty()
            val vk = prefs.getString("vkHandle", "")?.trim()?.takeIf { it.isNotEmpty() }
            val proton = prefs.getString("protonHandle", "")?.trim()?.takeIf { it.isNotEmpty() }
            val tg = prefs.getString("telegramHandle", "")?.trim()?.takeIf { it.isNotEmpty() }
            val result = if (handle.length >= 3 && name.isNotEmpty()) {
                PendingNAccountRegistration(
                    naccountHandle = handle,
                    displayName = name,
                    vkHandle = vk,
                    protonHandle = proton,
                    telegramHandle = tg,
                    cloudSyncEnabled = prefs.getBoolean("cloudSyncEnabled", true),
                    syncBookmarks = prefs.getBoolean("syncBookmarks", true),
                    syncSettings = prefs.getBoolean("syncSettings", true),
                    syncHistory = prefs.getBoolean("syncHistory", true)
                )
            } else null
            prefs.edit().clear().commit()
            return result
        }

        fun saveGuestRegistration(context: Context, reg: PendingNAccountRegistration) {
            val cleanHandle = reg.naccountHandle.trim().let {
                if (it.startsWith("@")) it else "@$it"
            }.take(32)
            val cleanName = reg.displayName.trim().take(60)
            context.applicationContext
                .getSharedPreferences("orbit_guest_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("guest_naccount_handle", cleanHandle)
                .putString("guest_display_name", cleanName)
                .putString("guest_vk_handle", reg.vkHandle?.trim()?.take(64) ?: "")
                .putString("guest_proton_handle", reg.protonHandle?.trim()?.take(120) ?: "")
                .putString("guest_telegram_handle", reg.telegramHandle?.trim()?.take(64) ?: "")
                .putBoolean("guest_sync_bookmarks", reg.syncBookmarks)
                .putBoolean("guest_sync_settings", reg.syncSettings)
                .putBoolean("guest_sync_history", reg.syncHistory)
                .commit()
        }
    }

    suspend fun ensureInitialUserDocuments(): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.success(Unit)
        val user = auth.currentUser!!
        val userDocPath = "users/$uid"
        val settingsDocPath = "users/$uid/settings/orbit_config"
        val pendingReg = consumePendingRegistration(appContext)
        return try {
            val userRef = db.collection("users").document(uid)
            val userSnap = userRef.get().await()
            if (!userSnap.exists()) {
                val baseName = pendingReg?.displayName?.takeIf { it.isNotBlank() }
                    ?: user.displayName?.takeIf { it.isNotBlank() }
                    ?: "Orbit User"
                val handleBase = baseName.lowercase().replace(Regex("[^a-z0-9_]"), "").take(14).ifBlank { "user" }
                val handle = (pendingReg?.naccountHandle?.takeIf { it.length >= 3 } ?: "@orbit_$handleBase").take(32)
                val providers = buildList {
                    add("GOOGLE")
                    if (!pendingReg?.vkHandle.isNullOrBlank()) add("VK")
                    if (!pendingReg?.protonHandle.isNullOrBlank()) add("PROTON")
                    if (!pendingReg?.telegramHandle.isNullOrBlank()) add("TELEGRAM")
                }
                val profilePayload = mutableMapOf<String, Any?>(
                    "userId" to uid,
                    "naccountHandle" to handle,
                    "displayName" to baseName.take(60),
                    "email" to user.email?.take(120),
                    "linkedProviders" to providers,
                    "vkHandle" to pendingReg?.vkHandle,
                    "protonHandle" to pendingReg?.protonHandle,
                    "telegramHandle" to pendingReg?.telegramHandle,
                    "cloudSyncEnabled" to (pendingReg?.cloudSyncEnabled ?: true),
                    "syncBookmarks" to (pendingReg?.syncBookmarks ?: true),
                    "syncSettings" to (pendingReg?.syncSettings ?: true),
                    "syncHistory" to (pendingReg?.syncHistory ?: true),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                userRef.set(profilePayload).await()
            } else if (pendingReg != null) {
                val providers = buildList {
                    add("GOOGLE")
                    if (!pendingReg.vkHandle.isNullOrBlank()) add("VK")
                    if (!pendingReg.protonHandle.isNullOrBlank()) add("PROTON")
                    if (!pendingReg.telegramHandle.isNullOrBlank()) add("TELEGRAM")
                }
                val updatePayload = mutableMapOf<String, Any?>(
                    "naccountHandle" to pendingReg.naccountHandle.take(32),
                    "displayName" to pendingReg.displayName.take(60),
                    "linkedProviders" to providers,
                    "vkHandle" to pendingReg.vkHandle,
                    "protonHandle" to pendingReg.protonHandle,
                    "telegramHandle" to pendingReg.telegramHandle,
                    "cloudSyncEnabled" to pendingReg.cloudSyncEnabled,
                    "syncBookmarks" to pendingReg.syncBookmarks,
                    "syncSettings" to pendingReg.syncSettings,
                    "syncHistory" to pendingReg.syncHistory,
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                userRef.update(updatePayload).await()
            }

            val settingsRef = userRef.collection("settings").document("orbit_config")
            val settingsSnap = settingsRef.get().await()
            if (!settingsSnap.exists()) {
                val cached = readSettingsFromPrefs(userCachePrefs(uid), uid)
                val defaultSettings = mapOf(
                    "userId" to uid,
                    "styleMode" to cached.styleMode,
                    "themeBrightness" to cached.themeBrightness,
                    "colorPalette" to cached.colorPalette,
                    "wallpaperStyle" to cached.wallpaperStyle,
                    "homeLayout" to cached.homeLayout,
                    "dockStyle" to cached.dockStyle,
                    "monarchG2EngineEnabled" to cached.monarchG2EngineEnabled,
                    "iconTheme" to cached.iconTheme,
                    "glassTransparency" to cached.glassTransparency,
                    "animationStyle" to cached.animationStyle,
                    "cornerStyle" to cached.cornerStyle,
                    "starfieldParticlesEnabled" to cached.starfieldParticlesEnabled,
                    "expressiveShapeStyle" to cached.expressiveShapeStyle,
                    "homeLogoScale" to cached.homeLogoScale,
                    "themedIconsAdaptApp" to cached.themedIconsAdaptApp,
                    "compactStatusPills" to cached.compactStatusPills,
                    "showFavorites" to cached.showFavorites,
                    "showPrivacyReport" to cached.showPrivacyReport,
                    "showRedAiBar" to cached.showRedAiBar,
                    "showCosmosWidget" to cached.showCosmosWidget,
                    "showQuickNotesWidget" to cached.showQuickNotesWidget,
                    "quickNotesText" to cached.quickNotesText,
                    "showGamingTelemetry" to cached.showGamingTelemetry,
                    "adBlockEnabled" to cached.adBlockEnabled,
                    "antiTrackingEnabled" to cached.antiTrackingEnabled,
                    "httpsOnlyEnabled" to cached.httpsOnlyEnabled,
                    "webRtcLeakProtection" to cached.webRtcLeakProtection,
                    "canvasFingerprintProtection" to cached.canvasFingerprintProtection,
                    "doNotTrackEnabled" to cached.doNotTrackEnabled,
                    "cookieAutoPurgeEnabled" to cached.cookieAutoPurgeEnabled,
                    "vpnEnabled" to cached.vpnEnabled,
                    "vpnRegion" to cached.vpnRegion,
                    "antivirusProvider" to cached.antivirusProvider,
                    "gamingRgbEnabled" to cached.gamingRgbEnabled,
                    "gamingHapticsEnabled" to cached.gamingHapticsEnabled,
                    "performanceModeEnabled" to cached.performanceModeEnabled,
                    "ramLimitMb" to cached.ramLimitMb,
                    "tabHibernationEnabled" to cached.tabHibernationEnabled,
                    "searchEngine" to cached.searchEngine,
                    "appLanguage" to cached.appLanguage,
                    "redAiAutoSummarize" to cached.redAiAutoSummarize,
                    "redAiSafeLinkScan" to cached.redAiSafeLinkScan,
                    "redAiStyle" to cached.redAiStyle,
                    "mercuryAiModel" to cached.mercuryAiModel,
                    "notifyBlockedTrackers" to cached.notifyBlockedTrackers,
                    "notifyCloudSync" to cached.notifyCloudSync,
                    "notifySecurityAlerts" to cached.notifySecurityAlerts,
                    "soundEffectsEnabled" to cached.soundEffectsEnabled,
                    "customAccentHex" to cached.customAccentHex,
                    "customSecondaryHex" to cached.customSecondaryHex,
                    "customCornerRadiusDp" to cached.customCornerRadiusDp,
                    "borderGlowIntensity" to cached.borderGlowIntensity,
                    "glassBlurLevel" to cached.glassBlurLevel,
                    "customIconUri" to cached.customIconUri,
                    "dnsOverHttpsEnabled" to cached.dnsOverHttpsEnabled,
                    "blockPopupsEnabled" to cached.blockPopupsEnabled,
                    "antiPhishingShieldEnabled" to cached.antiPhishingShieldEnabled,
                    "floatingSpeedHudEnabled" to cached.floatingSpeedHudEnabled,
                    "adsBlockedTotal" to cached.adsBlockedTotal,
                    "trackersBlockedTotal" to cached.trackersBlockedTotal,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                settingsRef.set(defaultSettings).await()
            }

            val favRef = userRef.collection("favorites")
            val favSnap = favRef.limit(1).get().await()
            if (favSnap.isEmpty) {
                val defaults = listOf(
                    Triple("Apple", "https://www.apple.com", "APPLE" to 0xFF1C1C1EL),
                    Triple("Bing", "https://www.bing.com", "BING" to 0xFF008373L),
                    Triple("Google", "https://www.google.com", "GOOGLE" to 0xFF4285F4L),
                    Triple("Yahoo", "https://www.yahoo.com", "YAHOO" to 0xFF6001D2L)
                )
                defaults.forEachIndexed { index, (title, url, badgeAndColor) ->
                    val (badge, colorHex) = badgeAndColor
                    favRef.document("default_${badge.lowercase()}").set(
                        mapOf(
                            "userId" to uid,
                            "title" to title,
                            "url" to url,
                            "iconBadge" to badge,
                            "accentHex" to colorHex,
                            "sortOrder" to index.toLong(),
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "$userDocPath / $settingsDocPath")
            Result.failure(e)
        }
    }

    fun observeProfile(userId: String): Flow<NAccountProfile?> = flow {
        if (userId == "guest_user" || isGuestMode()) {
            emitAll(guestProfileFlow)
            return@flow
        }
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(NAccountProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    } else null
                }
                .catch { error ->
                    handleFlowError(error, OperationType.GET, path, userId)
                    throw error
                }
        )
    }

    fun observeSettings(userId: String): Flow<OrbitSettings> = flow {
        if (userId == "guest_user" || isGuestMode()) {
            emitAll(guestSettingsFlow)
            return@flow
        }
        val cachePrefs = userCachePrefs(userId)
        val cachedInitial = readSettingsFromPrefs(cachePrefs, userId)
        if (cachePrefs != null && cachePrefs.contains("styleMode")) {
            emit(cachedInitial)
        }
        val path = "users/$userId/settings/orbit_config"
        emitAll(
            db.collection("users").document(userId).collection("settings").document("orbit_config")
                .snapshots()
                .map { snapshot ->
                    val loaded = snapshot.toObject(OrbitSettings::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        ?.copy(userId = userId)
                        ?: cachedInitial
                    writeSettingsToPrefs(cachePrefs, loaded)
                    loaded
                }
                .catch { error ->
                    handleFlowError(error, OperationType.GET, path, userId)
                    emit(readSettingsFromPrefs(cachePrefs, userId))
                }
        )
    }

    fun observeFavorites(userId: String): Flow<List<FavoriteSite>> = flow {
        if (userId == "guest_user" || isGuestMode()) {
            emitAll(guestFavoritesFlow)
            return@flow
        }
        val path = "users/$userId/favorites"
        emitAll(
            db.collection("users").document(userId).collection("favorites")
                .orderBy("sortOrder", Query.Direction.ASCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FavoriteSite::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    handleFlowError(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    fun observeHistory(userId: String): Flow<List<HistoryEntry>> = flow {
        if (userId == "guest_user" || isGuestMode()) {
            emitAll(guestHistoryFlow)
            return@flow
        }
        val path = "users/$userId/history"
        emitAll(
            db.collection("users").document(userId).collection("history")
                .orderBy("visitedAt", Query.Direction.DESCENDING)
                .limit(50)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(HistoryEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    handleFlowError(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    fun observeSyncedTabs(userId: String): Flow<List<SyncedTab>> = flow {
        if (userId == "guest_user" || isGuestMode()) {
            emitAll(guestTabsFlow)
            return@flow
        }
        val path = "users/$userId/tabs"
        emitAll(
            db.collection("users").document(userId).collection("tabs")
                .orderBy("sortOrder", Query.Direction.ASCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(SyncedTab::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
                .catch { error ->
                    handleFlowError(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    suspend fun updateSettingsFields(fields: Map<String, Any>): Result<Unit> {
        if (isGuestMode() || auth.currentUser == null) {
            val updated = applyFieldsToSettings(guestSettingsFlow.value, fields)
            guestSettingsFlow.value = updated
            writeSettingsToPrefs(guestPrefs, updated)
            return Result.success(Unit)
        }
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val cache = userCachePrefs(uid)
            val merged = applyFieldsToSettings(readSettingsFromPrefs(cache, uid), fields)
            writeSettingsToPrefs(cache, merged)
        }
        var docPath = "users/${uid ?: "unauthenticated"}/settings/orbit_config"
        return try {
            val activeUid = requireUserId()
            val docRef = db.collection("users").document(activeUid).collection("settings").document("orbit_config")
            docPath = docRef.path
            val payload = fields.toMutableMap().apply {
                put("updatedAt", FieldValue.serverTimestamp())
            }
            docRef.update(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun incrementPrivacyCounters(adsDelta: Long, trackersDelta: Long): Result<Unit> {
        if (isGuestMode() || auth.currentUser == null) {
            val current = guestSettingsFlow.value
            val updated = current.copy(
                adsBlockedTotal = current.adsBlockedTotal + adsDelta,
                trackersBlockedTotal = current.trackersBlockedTotal + trackersDelta
            )
            guestSettingsFlow.value = updated
            writeSettingsToPrefs(guestPrefs, updated)
            return Result.success(Unit)
        }
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/settings/orbit_config"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("settings").document("orbit_config")
            docPath = docRef.path
            val updates = mutableMapOf<String, Any>("updatedAt" to FieldValue.serverTimestamp())
            if (adsDelta > 0L) updates["adsBlockedTotal"] = FieldValue.increment(adsDelta)
            if (trackersDelta > 0L) updates["trackersBlockedTotal"] = FieldValue.increment(trackersDelta)
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateNAccountProfile(fields: Map<String, Any?>): Result<Unit> {
        if (isGuestMode() || auth.currentUser == null) {
            val current = guestProfileFlow.value ?: NAccountProfile(userId = "guest_user")
            var updated = current
            fields.forEach { (k, v) ->
                when (k) {
                    "naccountHandle" -> updated = updated.copy(naccountHandle = v?.toString() ?: current.naccountHandle)
                    "displayName" -> updated = updated.copy(displayName = v?.toString() ?: current.displayName)
                    "vkHandle" -> updated = updated.copy(vkHandle = v?.toString())
                    "protonHandle" -> updated = updated.copy(protonHandle = v?.toString())
                    "telegramHandle" -> updated = updated.copy(telegramHandle = v?.toString())
                    "cloudSyncEnabled" -> updated = updated.copy(cloudSyncEnabled = v as? Boolean ?: false)
                    "syncBookmarks" -> updated = updated.copy(syncBookmarks = v as? Boolean ?: true)
                    "syncSettings" -> updated = updated.copy(syncSettings = v as? Boolean ?: true)
                    "syncHistory" -> updated = updated.copy(syncHistory = v as? Boolean ?: true)
                }
            }
            guestProfileFlow.value = updated
            persistGuestProfile(updated)
            return Result.success(Unit)
        }
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            val sanitized = fields.filterValues { it != null }.toMutableMap().apply {
                put("updatedAt", FieldValue.serverTimestamp())
            }
            docRef.update(sanitized).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addFavorite(title: String, url: String, iconBadge: String, accentHex: Long, sortOrder: Long): Result<Unit> {
        val cleanTitle = title.trim().take(50)
        val cleanUrl = url.trim().take(500)
        val cleanBadge = iconBadge.trim().take(16).ifBlank { "WEB" }
        require(cleanTitle.isNotEmpty() && cleanUrl.isNotEmpty()) { "Title and URL must not be empty" }

        if (isGuestMode() || auth.currentUser == null) {
            val current = guestFavoritesFlow.value.toMutableList()
            current.add(
                FavoriteSite(
                    id = "guest_fav_${System.currentTimeMillis()}",
                    userId = "guest_user",
                    title = cleanTitle,
                    url = cleanUrl,
                    iconBadge = cleanBadge,
                    accentHex = accentHex,
                    sortOrder = sortOrder
                )
            )
            guestFavoritesFlow.value = current
            persistGuestFavorites(current)
            return Result.success(Unit)
        }
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/favorites"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("favorites").document()
            docPath = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "title" to cleanTitle,
                    "url" to cleanUrl,
                    "iconBadge" to cleanBadge,
                    "accentHex" to accentHex,
                    "sortOrder" to sortOrder.coerceIn(0L, 1000L),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteFavorite(favoriteId: String): Result<Unit> {
        if (isGuestMode() || auth.currentUser == null) {
            val filtered = guestFavoritesFlow.value.filterNot { it.id == favoriteId }
            guestFavoritesFlow.value = filtered
            persistGuestFavorites(filtered)
            return Result.success(Unit)
        }
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/favorites/$favoriteId"
        return try {
            val uid = requireUserId()
            require(favoriteId.isNotBlank())
            val docRef = db.collection("users").document(uid).collection("favorites").document(favoriteId)
            docPath = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addHistoryEntry(title: String, url: String): Result<Unit> {
        val cleanTitle = title.trim().ifBlank { url }.take(120)
        val cleanUrl = url.trim().take(500)
        if (cleanUrl.isBlank() || cleanUrl.startsWith("orbit://")) return Result.success(Unit)

        if (isGuestMode() || auth.currentUser == null) {
            val current = guestHistoryFlow.value.toMutableList()
            current.add(
                0,
                HistoryEntry(
                    id = "guest_hist_${System.currentTimeMillis()}",
                    userId = "guest_user",
                    title = cleanTitle,
                    url = cleanUrl
                )
            )
            guestHistoryFlow.value = current.take(50)
            return Result.success(Unit)
        }
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/history"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("history").document()
            docPath = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "title" to cleanTitle,
                    "url" to cleanUrl,
                    "visitedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun clearHistory(entryIds: List<String>): Result<Unit> {
        if (isGuestMode() || auth.currentUser == null) {
            guestHistoryFlow.value = emptyList()
            return Result.success(Unit)
        }
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/history"
        return try {
            val uid = requireUserId()
            val batch = db.batch()
            val col = db.collection("users").document(uid).collection("history")
            entryIds.take(100).forEach { id ->
                if (id.isNotBlank()) batch.delete(col.document(id))
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun syncTab(tabId: String, title: String, url: String, incognito: Boolean, sortOrder: Long): Result<Unit> {
        if (isGuestMode() || auth.currentUser == null || incognito) return Result.success(Unit)
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/tabs/$tabId"
        return try {
            val uid = requireUserId()
            val safeId = tabId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120)
            val docRef = db.collection("users").document(uid).collection("tabs").document(safeId)
            docPath = docRef.path
            val snap = docRef.get().await()
            val cleanTitle = title.trim().ifBlank { "Orbit" }.take(120)
            val cleanUrl = url.trim().take(500)
            if (snap.exists()) {
                docRef.update(
                    mapOf(
                        "title" to cleanTitle,
                        "url" to cleanUrl,
                        "incognito" to false,
                        "sortOrder" to sortOrder.coerceIn(0L, 500L),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "title" to cleanTitle,
                        "url" to cleanUrl,
                        "incognito" to false,
                        "sortOrder" to sortOrder.coerceIn(0L, 500L),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteSyncedTab(tabId: String): Result<Unit> {
        if (isGuestMode() || auth.currentUser == null) return Result.success(Unit)
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/tabs/$tabId"
        return try {
            val uid = requireUserId()
            val safeId = tabId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120)
            val docRef = db.collection("users").document(uid).collection("tabs").document(safeId)
            docPath = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }
}
