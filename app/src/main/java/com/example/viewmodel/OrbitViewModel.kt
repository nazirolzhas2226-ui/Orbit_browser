package com.example.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.browser.AdBlockAndSecurityEngine
import com.example.data.OrbitFirestoreRepository
import com.example.data.RedAiRepository
import com.example.model.AntivirusProvider
import com.example.model.AntivirusScanResult
import com.example.model.AppLanguage
import com.example.model.BlockedEvent
import com.example.model.BrowserStyleMode
import com.example.model.BrowserTab
import com.example.model.ColorPalettePreset
import com.example.model.DeviceTelemetry
import com.example.model.DownloadItem
import com.example.model.FavoriteSite
import com.example.model.HistoryEntry
import com.example.model.HomeLayoutStyle
import com.example.model.NAccountProfile
import com.example.model.OrbitSettings
import com.example.model.OrbitToastNotification
import com.example.model.RedAiMessage
import com.example.model.RedAiStyle
import com.example.model.SearchEngine
import com.example.model.ThemeBrightness
import com.example.model.VpnRegion
import com.example.model.WallpaperStyle
import com.example.util.OrbitSoundPlayer
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface CloudUiState<out T> {
    data object Loading : CloudUiState<Nothing>
    data class Success<T>(val data: T) : CloudUiState<T>
    data class Error(val message: String) : CloudUiState<Nothing>
}

class OrbitViewModel(
    private val firestoreRepository: OrbitFirestoreRepository,
    private val redAiRepository: RedAiRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _toast = MutableStateFlow<OrbitToastNotification?>(null)
    val toast: StateFlow<OrbitToastNotification?> = _toast.asStateFlow()
    private var toastJob: Job? = null

    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    private val _tabs = MutableStateFlow(
        listOf(
            BrowserTab(
                id = "tab_main_1",
                title = "Orbit",
                url = "orbit://home"
            )
        )
    )
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow("tab_main_1")
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    private val _blockedEvents = MutableStateFlow<List<BlockedEvent>>(emptyList())
    val blockedEvents: StateFlow<List<BlockedEvent>> = _blockedEvents.asStateFlow()

    private val _redAiMessages = MutableStateFlow<List<RedAiMessage>>(emptyList())
    val redAiMessages: StateFlow<List<RedAiMessage>> = _redAiMessages.asStateFlow()

    private val _redAiBusy = MutableStateFlow(false)
    val redAiBusy: StateFlow<Boolean> = _redAiBusy.asStateFlow()

    private val _redAiError = MutableStateFlow<String?>(null)
    val redAiError: StateFlow<String?> = _redAiError.asStateFlow()

    private val _latestAntivirusScan = MutableStateFlow<AntivirusScanResult?>(null)
    val latestAntivirusScan: StateFlow<AntivirusScanResult?> = _latestAntivirusScan.asStateFlow()

    private val _telemetry = MutableStateFlow(
        DeviceTelemetry(
            totalRamMb = 4096L,
            availableRamMb = 2048L,
            usedRamMb = 2048L,
            appHeapMb = 64L,
            activeTabsCount = 1,
            hibernatedTabsCount = 0
        )
    )
    val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

    private var pendingCounterDeltaAds = 0L
    private var pendingCounterDeltaTrackers = 0L
    private var counterSyncJob: Job? = null

    val settingsState: StateFlow<CloudUiState<OrbitSettings>> =
        firestoreRepository.observeSettings(currentUserId)
            .map<OrbitSettings, CloudUiState<OrbitSettings>> { CloudUiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Settings observation error", root)
                emit(CloudUiState.Error(root.message ?: "Failed to load Orbit cloud settings"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = CloudUiState.Loading
            )

    val profileState: StateFlow<CloudUiState<NAccountProfile?>> =
        firestoreRepository.observeProfile(currentUserId)
            .map<NAccountProfile?, CloudUiState<NAccountProfile?>> { CloudUiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Profile observation error", root)
                emit(CloudUiState.Error(root.message ?: "Failed to load NAccount profile"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = CloudUiState.Loading
            )

    val favoritesState: StateFlow<CloudUiState<List<FavoriteSite>>> =
        firestoreRepository.observeFavorites(currentUserId)
            .map<List<FavoriteSite>, CloudUiState<List<FavoriteSite>>> { CloudUiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Favorites observation error", root)
                emit(CloudUiState.Error(root.message ?: "Failed to load Favorites"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = CloudUiState.Loading
            )

    val historyState: StateFlow<CloudUiState<List<HistoryEntry>>> =
        firestoreRepository.observeHistory(currentUserId)
            .map<List<HistoryEntry>, CloudUiState<List<HistoryEntry>>> { CloudUiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "History observation error", root)
                emit(CloudUiState.Error(root.message ?: "Failed to load History"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = CloudUiState.Loading
            )

    init {
        viewModelScope.launch {
            val initResult = firestoreRepository.ensureInitialUserDocuments()
            initResult.onFailure { e ->
                Log.w(TAG, "Initial cloud bootstrap failed: ${e.message}")
            }
        }
    }

    fun currentSettings(): OrbitSettings =
        (settingsState.value as? CloudUiState.Success)?.data ?: OrbitSettings(userId = currentUserId)

    fun activeTabOrNull(): BrowserTab? =
        _tabs.value.firstOrNull { it.id == _activeTabId.value } ?: _tabs.value.firstOrNull()

    fun activeTab(): BrowserTab =
        activeTabOrNull() ?: BrowserTab(id = "tab_empty", title = "Orbit", url = "orbit://home")

    fun showToast(title: String, subtitle: String, isAlert: Boolean = false) {
        toastJob?.cancel()
        _toast.value = OrbitToastNotification(
            id = System.currentTimeMillis(),
            title = title,
            subtitle = subtitle,
            isAlert = isAlert
        )
        toastJob = viewModelScope.launch {
            delay(2800L)
            _toast.value = null
        }
    }

    fun refreshTelemetry(context: Context) {
        runCatching {
            val currentTabs = _tabs.value
            val hibernated = currentTabs.count { it.isHibernated }
            _telemetry.value = AdBlockAndSecurityEngine.readRealDeviceTelemetry(
                context = context,
                activeTabs = (currentTabs.size - hibernated).coerceAtLeast(0),
                hibernatedTabs = hibernated
            )
        }
    }

    fun navigateActiveTab(rawInput: String) {
        val settings = currentSettings()
        val resolvedUrl = AdBlockAndSecurityEngine.buildSearchOrUrl(
            input = rawInput,
            searchTemplate = settings.resolvedSearchEngine.searchUrlTemplate
        )
        val finalUrl = AdBlockAndSecurityEngine.upgradeToHttpsIfNeeded(resolvedUrl, settings.httpsOnlyEnabled)
        if (_tabs.value.isEmpty()) {
            createNewTab(initialUrl = finalUrl, incognito = false)
        } else {
            val currentId = _activeTabId.value
            val targetId = if (_tabs.value.any { it.id == currentId }) currentId else _tabs.value.first().id
            _activeTabId.value = targetId
            _tabs.value = _tabs.value.map { tab ->
                if (tab.id == targetId) {
                    tab.copy(
                        url = finalUrl,
                        title = if (finalUrl == "orbit://home") "Orbit" else finalUrl,
                        isHibernated = false,
                        isLoading = finalUrl != "orbit://home"
                    )
                } else tab
            }
            activeTabOrNull()?.let { syncTabToCloud(it) }
        }
        if (settings.redAiSafeLinkScan && !finalUrl.startsWith("orbit://")) {
            val scan = AdBlockAndSecurityEngine.runLocalAntivirusHeuristic(
                url = finalUrl,
                provider = settings.resolvedAntivirus,
                isRu = settings.resolvedLanguage == AppLanguage.RU
            )
            _latestAntivirusScan.value = scan
            if (!scan.isSafe && settings.notifySecurityAlerts) {
                showToast(scan.providerLabel, scan.verdict, isAlert = true)
            }
        }
    }

    fun updateActiveTabWebState(
        tabId: String,
        title: String? = null,
        url: String? = null,
        isLoading: Boolean? = null,
        progress: Int? = null,
        canGoBack: Boolean? = null,
        canGoForward: Boolean? = null,
        recordHistory: Boolean = false
    ) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                tab.copy(
                    title = title ?: tab.title,
                    url = url ?: tab.url,
                    isLoading = isLoading ?: tab.isLoading,
                    progress = progress ?: tab.progress,
                    canGoBack = canGoBack ?: tab.canGoBack,
                    canGoForward = canGoForward ?: tab.canGoForward
                )
            } else tab
        }
        if (recordHistory && !url.isNullOrBlank() && !url.startsWith("orbit://")) {
            val tab = _tabs.value.firstOrNull { it.id == tabId }
            val profile = (profileState.value as? CloudUiState.Success)?.data
            if (tab != null && !tab.isIncognito && (profile?.syncHistory != false)) {
                viewModelScope.launch {
                    firestoreRepository.addHistoryEntry(title ?: url, url)
                }
            }
            if (tab != null) syncTabToCloud(tab)
        }
    }

    private fun syncTabToCloud(tab: BrowserTab) {
        val profile = (profileState.value as? CloudUiState.Success)?.data
        if (profile?.cloudSyncEnabled == false || tab.isIncognito) return
        val order = _tabs.value.indexOfFirst { it.id == tab.id }.coerceAtLeast(0).toLong()
        viewModelScope.launch {
            firestoreRepository.syncTab(tab.id, tab.title, tab.url, tab.isIncognito, order)
        }
    }

    fun createNewTab(initialUrl: String = "orbit://home", incognito: Boolean = false) {
        val settings = currentSettings()
        val id = "tab_${UUID.randomUUID().toString().take(8)}"
        val maxActive = if (settings.performanceModeEnabled || settings.ramLimitMb <= 1536L) 2 else 4
        val updatedExisting = _tabs.value.mapIndexed { idx, tab ->
            if (settings.tabHibernationEnabled && _tabs.value.size - idx >= maxActive && tab.url != "orbit://home") {
                tab.copy(isHibernated = true)
            } else tab
        }
        val newTab = BrowserTab(
            id = id,
            title = if (incognito) "Incognito" else "Orbit",
            url = initialUrl,
            isIncognito = incognito,
            isLoading = initialUrl != "orbit://home"
        )
        _tabs.value = updatedExisting + newTab
        _activeTabId.value = id
        OrbitSoundPlayer.playTabSwoop()
        syncTabToCloud(newTab)
    }

    fun selectTab(tabId: String) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) tab.copy(isHibernated = false) else tab
        }
        _activeTabId.value = tabId
        OrbitSoundPlayer.playClick()
    }

    fun closeTab(tabId: String) {
        val remaining = _tabs.value.filterNot { it.id == tabId }
        _tabs.value = remaining
        if (remaining.isEmpty()) {
            _activeTabId.value = ""
        } else if (_activeTabId.value == tabId || remaining.none { it.id == _activeTabId.value }) {
            _activeTabId.value = remaining.last().id
        }
        OrbitSoundPlayer.playTabSwoop()
        viewModelScope.launch {
            try {
                firestoreRepository.deleteSyncedTab(tabId)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete tab from cloud", e)
            }
        }
    }

    fun toggleDesktopMode() {
        val id = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == id) tab.copy(desktopMode = !tab.desktopMode) else tab
        }
    }

    fun toggleReaderMode() {
        val id = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == id) tab.copy(readerMode = !tab.readerMode) else tab
        }
    }

    fun addDownload(fileName: String, url: String, contentLengthBytes: Long) {
        val ext = fileName.substringAfterLast('.', "bin").take(8)
        val item = DownloadItem(
            id = "dl_${UUID.randomUUID().toString().take(8)}",
            fileName = fileName.ifBlank { "orbit_download" },
            fileSizeBytes = contentLengthBytes.coerceAtLeast(0L),
            downloadedBytes = contentLengthBytes.coerceAtLeast(0L),
            isComplete = true,
            fileExtension = ext,
            sourceUrl = url
        )
        _downloads.value = listOf(item) + _downloads.value
        OrbitSoundPlayer.playSuccess()
        val lang = currentSettings().resolvedLanguage
        showToast(
            title = when (lang) {
                AppLanguage.RU -> "Загрузка"
                AppLanguage.KK -> "Жүктеу"
                AppLanguage.ES -> "Descarga"
                AppLanguage.DE -> "Download"
                AppLanguage.FR -> "Téléchargement"
                AppLanguage.EN -> "Download"
            },
            subtitle = item.fileName
        )
    }

    fun clearDownloads() {
        _downloads.value = emptyList()
    }

    fun shredAllBrowserData(context: Context) {
        runCatching {
            android.webkit.CookieManager.getInstance().removeAllCookies(null)
            android.webkit.CookieManager.getInstance().flush()
            android.webkit.WebStorage.getInstance().deleteAllData()
            android.webkit.WebViewDatabase.getInstance(context).clearFormData()
            android.webkit.WebViewDatabase.getInstance(context).clearHttpAuthUsernamePassword()
        }
        clearHistory()
        OrbitSoundPlayer.playSuccess()
        val lang = currentSettings().resolvedLanguage
        showToast(
            title = when (lang) {
                AppLanguage.RU -> "Приватность"
                AppLanguage.KK -> "Құпиялылық"
                AppLanguage.ES -> "Privacidad"
                AppLanguage.DE -> "Datenschutz"
                AppLanguage.FR -> "Confidentialité"
                AppLanguage.EN -> "Privacy"
            },
            subtitle = when (lang) {
                AppLanguage.RU -> "Куки, кэш и история уничтожены!"
                AppLanguage.KK -> "Cookie, кэш және тарих жойылды!"
                AppLanguage.ES -> "¡Cookies, caché e historial eliminados!"
                AppLanguage.DE -> "Cookies, Cache & Verlauf gelöscht!"
                AppLanguage.FR -> "Cookies, cache et historique supprimés !"
                AppLanguage.EN -> "Cookies, cache & history shredded!"
            }
        )
    }

    fun onResourceBlocked(host: String, match: AdBlockAndSecurityEngine.BlockMatch) {
        val category = if (match == AdBlockAndSecurityEngine.BlockMatch.AD) "Ad" else "Tracker"
        val event = BlockedEvent(host = host, category = category, timestampMs = System.currentTimeMillis())
        _blockedEvents.value = (listOf(event) + _blockedEvents.value).take(30)

        if (match == AdBlockAndSecurityEngine.BlockMatch.AD) pendingCounterDeltaAds++ else pendingCounterDeltaTrackers++
        counterSyncJob?.cancel()
        counterSyncJob = viewModelScope.launch {
            delay(1500L)
            val ads = pendingCounterDeltaAds
            val trackers = pendingCounterDeltaTrackers
            pendingCounterDeltaAds = 0L
            pendingCounterDeltaTrackers = 0L
            if (ads > 0L || trackers > 0L) {
                firestoreRepository.incrementPrivacyCounters(ads, trackers)
            }
        }
    }

    fun updateSetting(key: String, value: Any, toastTitle: String? = null, toastSubtitle: String? = null) {
        viewModelScope.launch {
            val res = firestoreRepository.updateSettingsFields(mapOf(key to value))
            res.onSuccess {
                val settings = currentSettings()
                if (toastTitle != null && toastSubtitle != null && settings.notifyCloudSync) {
                    showToast(toastTitle, toastSubtitle)
                }
            }.onFailure { e ->
                showToast("Cloud Sync", e.localizedMessage ?: "Sync failed", isAlert = true)
            }
        }
    }

    fun updateSettingsBatch(fields: Map<String, Any>, toastTitle: String? = null, toastSubtitle: String? = null) {
        viewModelScope.launch {
            val res = firestoreRepository.updateSettingsFields(fields)
            res.onSuccess {
                if (toastTitle != null && toastSubtitle != null) {
                    showToast(toastTitle, toastSubtitle)
                }
            }.onFailure { e ->
                showToast("Cloud Sync", e.localizedMessage ?: "Sync failed", isAlert = true)
            }
        }
    }

    fun selectMercuryModel(model: com.example.model.MercuryAiModel) {
        val isRu = currentSettings().resolvedLanguage == AppLanguage.RU
        updateSetting(
            key = "mercuryAiModel",
            value = model.modelId,
            toastTitle = "MercuryAI",
            toastSubtitle = if (isRu) "Модель: ${model.labelRu}" else "Model: ${model.labelEn}"
        )
    }

    fun toggleGamingMode(enable: Boolean) {
        val isRu = currentSettings().resolvedLanguage == AppLanguage.RU
        if (enable) {
            updateSettingsBatch(
                mapOf(
                    "styleMode" to BrowserStyleMode.GAMING_GX.name,
                    "colorPalette" to ColorPalettePreset.JUPITER_CYBER.name,
                    "wallpaperStyle" to BrowserStyleMode.GAMING_GX.officialWallpaper().name,
                    "showGamingTelemetry" to true,
                    "gamingRgbEnabled" to true
                ),
                toastTitle = if (isRu) "Игровой режим JupiterGX Mode включён" else "JupiterGX Mode Enabled",
                toastSubtitle = if (isRu) "Неон RGB + полосы Юпитера и лимиты ОЗУ" else "Jupiter cyber neon, official wallpaper & RAM controls active"
            )
        } else {
            updateSettingsBatch(
                mapOf(
                    "styleMode" to BrowserStyleMode.LIQUID_GLASS.name,
                    "colorPalette" to ColorPalettePreset.ORBIT_CYAN.name,
                    "wallpaperStyle" to BrowserStyleMode.LIQUID_GLASS.officialWallpaper().name,
                    "showGamingTelemetry" to false
                ),
                toastTitle = "Liquid Glass",
                toastSubtitle = if (isRu) "Стандартный режим Orbit восстановлен" else "Standard Orbit Liquid Glass restored"
            )
        }
    }

    fun toggleVpn(enable: Boolean, region: VpnRegion = currentSettings().resolvedVpnRegion) {
        val isRu = currentSettings().resolvedLanguage == AppLanguage.RU
        updateSettingsBatch(
            mapOf("vpnEnabled" to enable, "vpnRegion" to region.name),
            toastTitle = if (enable) {
                if (isRu) "Orbit VPN: ${region.labelRu}" else "Orbit VPN: ${region.labelEn}"
            } else {
                if (isRu) "VPN отключён" else "VPN Disconnected"
            },
            toastSubtitle = if (enable) {
                if (isRu) "Защищённый туннель (${region.latencyMs} мс)" else "Encrypted node (${region.latencyMs} ms)"
            } else {
                if (isRu) "Прямое соединение" else "Direct routing"
            }
        )
    }

    fun addFavorite(title: String, url: String, badge: String, accentHex: Long) {
        val currentFavs = (favoritesState.value as? CloudUiState.Success)?.data ?: emptyList()
        val normalizedUrl = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
        viewModelScope.launch {
            firestoreRepository.addFavorite(
                title = title,
                url = normalizedUrl,
                iconBadge = badge.uppercase().take(12),
                accentHex = accentHex,
                sortOrder = currentFavs.size.toLong()
            ).onSuccess {
                val isRu = currentSettings().resolvedLanguage == AppLanguage.RU
                showToast(title, if (isRu) "Добавлено в Избранное" else "Pinned to Favorites")
            }
        }
    }

    fun removeFavorite(favoriteId: String) {
        viewModelScope.launch {
            firestoreRepository.deleteFavorite(favoriteId)
        }
    }

    fun clearHistory() {
        val entries = (historyState.value as? CloudUiState.Success)?.data ?: emptyList()
        viewModelScope.launch {
            firestoreRepository.clearHistory(entries.map { it.id })
        }
    }

    fun updateNAccount(
        naccountHandle: String,
        displayName: String,
        vkHandle: String?,
        protonHandle: String?,
        telegramHandle: String?,
        cloudSyncEnabled: Boolean,
        syncBookmarks: Boolean,
        syncSettings: Boolean,
        syncHistory: Boolean
    ) {
        val providers = mutableListOf("GOOGLE")
        if (!vkHandle.isNullOrBlank()) providers.add("VK")
        if (!protonHandle.isNullOrBlank()) providers.add("PROTONMAIL")
        if (!telegramHandle.isNullOrBlank()) providers.add("TELEGRAM")

        viewModelScope.launch {
            val payload = mutableMapOf<String, Any?>(
                "naccountHandle" to naccountHandle.trim().take(32).ifBlank { "@orbit_user" },
                "displayName" to displayName.trim().take(60).ifBlank { "Orbit User" },
                "linkedProviders" to providers,
                "vkHandle" to vkHandle?.trim()?.take(64)?.takeIf { it.isNotEmpty() },
                "protonHandle" to protonHandle?.trim()?.take(120)?.takeIf { it.isNotEmpty() },
                "telegramHandle" to telegramHandle?.trim()?.take(64)?.takeIf { it.isNotEmpty() },
                "cloudSyncEnabled" to cloudSyncEnabled,
                "syncBookmarks" to syncBookmarks,
                "syncSettings" to syncSettings,
                "syncHistory" to syncHistory
            )
            firestoreRepository.updateNAccountProfile(payload).onSuccess {
                val isRu = currentSettings().resolvedLanguage == AppLanguage.RU
                showToast(
                    "NAccount",
                    if (isRu) "Профиль синхронизирован с облаком" else "Profile synced to cloud"
                )
            }
        }
    }

    fun runAntivirusCheckWithAi(urlToCheck: String) {
        val settings = currentSettings()
        val isRu = settings.resolvedLanguage == AppLanguage.RU
        val modelOption = settings.resolvedMercuryModel
        val localResult = AdBlockAndSecurityEngine.runLocalAntivirusHeuristic(
            url = urlToCheck,
            provider = settings.resolvedAntivirus,
            isRu = isRu
        )
        _latestAntivirusScan.value = localResult
        viewModelScope.launch {
            _redAiBusy.value = true
            _redAiError.value = null
            try {
                val aiVerdict = redAiRepository.analyzeUrlSecurity(
                    url = urlToCheck,
                    language = settings.resolvedLanguage,
                    modelOption = modelOption
                )
                _latestAntivirusScan.value = localResult.copy(
                    verdict = "${localResult.verdict}\n\nMercuryAI (${modelOption.labelEn}): $aiVerdict"
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _redAiError.value = e.localizedMessage ?: "MercuryAI URL check failed"
            } finally {
                _redAiBusy.value = false
            }
        }
    }

    fun sendRedAiMessage(prompt: String, pageSnippet: String? = null) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty() || _redAiBusy.value) return
        val settings = currentSettings()
        val modelOption = settings.resolvedMercuryModel
        val tab = activeTab()
        val userMsg = RedAiMessage(id = UUID.randomUUID().toString(), isUser = true, text = trimmed)
        val aiMsgId = UUID.randomUUID().toString()
        val placeholderAiMsg = RedAiMessage(
            id = aiMsgId,
            isUser = false,
            text = "",
            isStreaming = true,
            modelLabel = modelOption.labelEn
        )
        val priorHistory = _redAiMessages.value
            .filter { !it.isStreaming && it.text.isNotBlank() }
            .takeLast(6)
            .map { it.isUser to it.text }
        _redAiMessages.value = _redAiMessages.value + userMsg + placeholderAiMsg
        _redAiBusy.value = true
        _redAiError.value = null

        viewModelScope.launch {
            try {
                redAiRepository.streamPrompt(
                    userPrompt = trimmed,
                    pageContextUrl = tab.url,
                    pageContextTitle = tab.title,
                    pageSnippet = pageSnippet,
                    style = settings.resolvedRedAiStyle,
                    language = settings.resolvedLanguage,
                    modelOption = modelOption,
                    conversationHistory = priorHistory
                ).collect { partial ->
                    _redAiMessages.value = _redAiMessages.value.map { msg ->
                        if (msg.id == aiMsgId) msg.copy(text = partial, isStreaming = true) else msg
                    }
                }
                _redAiMessages.value = _redAiMessages.value.map { msg ->
                    if (msg.id == aiMsgId) msg.copy(isStreaming = false) else msg
                }
            } catch (e: TimeoutCancellationException) {
                _redAiError.value = when (settings.resolvedLanguage) {
                    AppLanguage.RU -> "Таймаут ответа MercuryAI (60с). Попробуйте снова."
                    AppLanguage.KK -> "MercuryAI жауап уақыты аяқталды (60с). Қайта көріңіз."
                    AppLanguage.ES -> "MercuryAI agotó el tiempo (60s). Inténtalo de nuevo."
                    AppLanguage.DE -> "MercuryAI Zeitüberschreitung (60s). Bitte erneut versuchen."
                    AppLanguage.FR -> "Délai MercuryAI expiré (60s). Veuillez réessayer."
                    AppLanguage.EN -> "MercuryAI timed out after 60s. Please retry."
                }
                _redAiMessages.value = _redAiMessages.value.filterNot { it.id == aiMsgId && it.text.isBlank() }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _redAiError.value = e.localizedMessage ?: "MercuryAI failed to respond"
                _redAiMessages.value = _redAiMessages.value.filterNot { it.id == aiMsgId && it.text.isBlank() }
            } finally {
                _redAiBusy.value = false
            }
        }
    }

    fun clearRedAiChat() {
        _redAiMessages.value = emptyList()
        _redAiError.value = null
    }

    private companion object {
        const val TAG = "OrbitVM"
    }
}
