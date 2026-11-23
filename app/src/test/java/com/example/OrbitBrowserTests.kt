package com.example

import android.app.Application
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.browser.AdBlockAndSecurityEngine
import com.example.data.OrbitFirestoreRepository
import com.example.data.RedAiRepository
import com.example.model.AppLanguage
import com.example.model.BrowserStyleMode
import com.example.model.ColorPalettePreset
import com.example.model.ExpressiveShapeStyle
import com.example.model.OrbitIconTheme
import com.example.model.ThemeBrightness
import com.example.model.SearchEngine
import com.example.util.OrbitIconManager
import com.example.viewmodel.OrbitViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OrbitBrowserTests {

    private lateinit var app: Application
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        app = ApplicationProvider.getApplicationContext()
        if (FirebaseApp.getApps(app).isEmpty()) {
            FirebaseApp.initializeApp(app)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guest theme settings and favorites persist across repository restarts`() = runTest(testDispatcher) {
        val repo1 = OrbitFirestoreRepository(app)
        val updateResult = repo1.updateSettingsFields(
            mapOf(
                "styleMode" to BrowserStyleMode.NOTHING_OS.name,
                "themeBrightness" to ThemeBrightness.DARK.name,
                "colorPalette" to ColorPalettePreset.SATURN_GOLD.name,
                "expressiveShapeStyle" to ExpressiveShapeStyle.STARBURST.name,
                "customAccentHex" to 0xFFFF3B30L,
                "appLanguage" to AppLanguage.KK.name,
                "soundEffectsEnabled" to false
            )
        )
        assertTrue(updateResult.isSuccess)

        val favResult = repo1.addFavorite(
            title = "GitHub",
            url = "https://github.com",
            iconBadge = "GIT",
            accentHex = 0xFF24292EL,
            sortOrder = 4L
        )
        assertTrue(favResult.isSuccess)

        // Simulate app restart with a brand new repository instance reading SharedPreferences
        val repo2 = OrbitFirestoreRepository(app)
        val restored = repo2.observeSettings("guest_user").first()
        assertEquals(BrowserStyleMode.NOTHING_OS.name, restored.styleMode)
        assertEquals(ThemeBrightness.DARK.name, restored.themeBrightness)
        assertEquals(ColorPalettePreset.SATURN_GOLD.name, restored.colorPalette)
        assertEquals(ExpressiveShapeStyle.STARBURST.name, restored.expressiveShapeStyle)
        assertEquals(0xFFFF3B30L, restored.customAccentHex)
        assertEquals(AppLanguage.KK.name, restored.appLanguage)
        assertFalse(restored.soundEffectsEnabled)

        val restoredFavs = repo2.observeFavorites("guest_user").first()
        assertTrue(restoredFavs.any { it.title == "GitHub" && it.url == "https://github.com" })
    }

    @Test
    fun `closing all tabs does not crash and safely recreates on navigation`() = runTest(testDispatcher) {
        val repo = OrbitFirestoreRepository(app)
        val vm = OrbitViewModel(repo, RedAiRepository(), "guest_user")
        advanceUntilIdle()

        val initialTabs = vm.tabs.value
        assertEquals(1, initialTabs.size)

        vm.closeTab(initialTabs.first().id)
        advanceUntilIdle()

        assertTrue(vm.tabs.value.isEmpty())
        assertEquals("", vm.activeTabId.value)
        assertNull(vm.activeTabOrNull())
        assertEquals("orbit://home", vm.activeTab().url)

        // Navigating with 0 tabs creates a clean new tab instead of throwing NoSuchElementException
        vm.navigateActiveTab("wikipedia.org")
        advanceUntilIdle()

        assertEquals(1, vm.tabs.value.size)
        assertEquals("https://wikipedia.org", vm.activeTab().url)

        vm.toggleDesktopMode()
        assertTrue(vm.activeTab().desktopMode)
    }

    @Test
    fun `downloads and blocker telemetry update cleanly in viewmodel`() = runTest(testDispatcher) {
        val repo = OrbitFirestoreRepository(app)
        val vm = OrbitViewModel(repo, RedAiRepository(), "guest_user")
        advanceUntilIdle()

        vm.addDownload("orbit_installer.apk", "https://example.com/orbit_installer.apk", 4096L)
        assertEquals(1, vm.downloads.value.size)
        assertEquals("apk", vm.downloads.value.first().fileExtension)

        vm.onResourceBlocked("doubleclick.net", AdBlockAndSecurityEngine.BlockMatch.AD)
        assertEquals(1, vm.blockedEvents.value.size)
        assertEquals("Ad", vm.blockedEvents.value.first().category)

        vm.clearDownloads()
        assertTrue(vm.downloads.value.isEmpty())
    }

    @Test
    fun `icon manager renders adaptive bitmaps across themes and modes`() {
        val themes = listOf(
            OrbitIconTheme.RAINBOW_COMPASS,
            OrbitIconTheme.BLACK_HOLE,
            OrbitIconTheme.GX_CRIMSON,
            OrbitIconTheme.SAKURA_BLOSSOM,
            OrbitIconTheme.PURE_WHITE
        )
        val modes = listOf(
            BrowserStyleMode.LIQUID_GLASS,
            BrowserStyleMode.NOTHING_OS,
            BrowserStyleMode.KOSMOS,
            BrowserStyleMode.MINECRAFT,
            BrowserStyleMode.SAKURA
        )

        themes.zip(modes).forEach { (theme, mode) ->
            val bmp = OrbitIconManager.renderOrbitIconBitmap(
                context = app,
                iconTheme = theme,
                styleMode = mode,
                customPhotoUri = null,
                sizePx = 256
            )
            assertNotNull(bmp)
            assertEquals(256, bmp.width)
            assertEquals(256, bmp.height)
            assertEquals(Bitmap.Config.ARGB_8888, bmp.config)
            bmp.recycle()
        }

        assertEquals(
            OrbitIconManager.LauncherPreset.NOTHING,
            OrbitIconManager.resolvePresetFor(OrbitIconTheme.AUTO_THEME, BrowserStyleMode.NOTHING_OS)
        )
        assertEquals(
            OrbitIconManager.LauncherPreset.GX,
            OrbitIconManager.resolvePresetFor(OrbitIconTheme.GX_CRIMSON, BrowserStyleMode.LIQUID_GLASS)
        )
    }

    @Test
    fun `search builder, https upgrade, blocker classification and official wallpapers work`() {
        val searchUrl = AdBlockAndSecurityEngine.buildSearchOrUrl(
            input = "Туманность Ориона",
            searchTemplate = SearchEngine.GOOGLE.searchUrlTemplate
        )
        assertTrue(searchUrl.startsWith("https://www.google.com/search?q="))
        assertFalse(searchUrl.contains(" "))

        assertEquals(
            "https://example.org",
            AdBlockAndSecurityEngine.upgradeToHttpsIfNeeded("http://example.org", httpsOnly = true)
        )
        assertEquals(
            AdBlockAndSecurityEngine.BlockMatch.AD,
            AdBlockAndSecurityEngine.classifyHost("pagead2.googlesyndication.com")
        )
        assertEquals(
            AdBlockAndSecurityEngine.BlockMatch.TRACKER,
            AdBlockAndSecurityEngine.classifyHost("mc.yandex.ru")
        )

        BrowserStyleMode.entries.forEach { mode ->
            assertNotNull(mode.officialPalette())
            assertNotNull(mode.officialWallpaper())
        }
        assertEquals(ColorPalettePreset.MOON_FROST, BrowserStyleMode.NOTHING_OS.officialPalette())
        assertEquals(ColorPalettePreset.SAKURA_ROSE, BrowserStyleMode.SAKURA.officialPalette())
        assertEquals(ColorPalettePreset.JUPITER_CYBER, BrowserStyleMode.GAMING_GX.officialPalette())

        assertEquals("gemini-3.8-flash", RedAiRepository.MODEL_NAME)
    }

    @Test
    fun `mercury ai models and registration persistence helpers work correctly`() {
        assertEquals("gemini-3.8-flash", com.example.model.MercuryAiModel.MERCURY_38_FLASH.modelId)
        assertEquals("gemini-3.5-flash-lite", com.example.model.MercuryAiModel.MERCURY_35_LITE.modelId)
        assertEquals("gemini-4-argon", com.example.model.MercuryAiModel.MERCURY_4_ARGON.modelId)

        assertEquals(
            com.example.model.MercuryAiModel.MERCURY_38_FLASH,
            com.example.model.MercuryAiModel.safeValueOf("gemini-3.8-flash")
        )
        assertEquals(
            com.example.model.MercuryAiModel.MERCURY_35_LITE,
            com.example.model.MercuryAiModel.safeValueOf("gemini-3.5-flash-lite")
        )

        // Pending registration save & consume
        val reg = com.example.model.PendingNAccountRegistration(
            naccountHandle = "orbit_pro",
            displayName = "Orbit Explorer",
            telegramHandle = "@explorer_tg"
        )
        OrbitFirestoreRepository.savePendingRegistration(app, reg)
        val consumed = OrbitFirestoreRepository.consumePendingRegistration(app)
        assertNotNull(consumed)
        assertEquals("@orbit_pro", consumed?.naccountHandle)
        assertEquals("Orbit Explorer", consumed?.displayName)
        assertEquals("@explorer_tg", consumed?.telegramHandle)

        // Once consumed, subsequent reads must return null
        assertNull(OrbitFirestoreRepository.consumePendingRegistration(app))

        // Guest registration persistence
        OrbitFirestoreRepository.saveGuestRegistration(app, reg)
        val guestPrefs = app.getSharedPreferences("orbit_guest_prefs", android.content.Context.MODE_PRIVATE)
        assertEquals("@orbit_pro", guestPrefs.getString("guest_naccount_handle", null))
        assertEquals("Orbit Explorer", guestPrefs.getString("guest_display_name", null))

        // JUPITER_GX and MERCURY_CORE icon bitmaps
        listOf(OrbitIconTheme.JUPITER_GX, OrbitIconTheme.MERCURY_CORE).forEach { theme ->
            val bmp = OrbitIconManager.renderOrbitIconBitmap(
                context = app,
                iconTheme = theme,
                styleMode = BrowserStyleMode.LIQUID_GLASS,
                customPhotoUri = null,
                sizePx = 128
            )
            assertNotNull(bmp)
            assertEquals(128, bmp.width)
            bmp.recycle()
        }
    }

    @Test
    fun `webgl2 glass html generator produces valid glsl es 300 shader document`() {
        val html = com.example.ui.components.buildWebGL2GlassHtml(
            primaryHex = "#00D4FF",
            secondaryHex = "#8B5CF6",
            isDark = true,
            ior = 1.52f,
            dispersion = 0.042f,
            interactiveTouch = true
        )
        assertNotNull(html)
        assertTrue(html.contains("#version 300 es"))
        assertTrue(html.contains("u_ior"))
        assertTrue(html.contains("u_dispersion"))
        assertTrue(html.contains("getContext('webgl2'"))
        assertTrue(html.contains("updateOptics"))
    }
}
