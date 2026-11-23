package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.HistoryEntry
import com.example.model.NAccountProfile
import com.example.model.OrbitSettings
import com.example.ui.components.OrbitGlassSurface
import com.example.ui.components.orbitBouncyClick
import com.example.ui.components.orbitContainerShape
import com.example.ui.theme.LocalOrbitTokens

@Composable
fun NAccountScreen(
    profile: NAccountProfile?,
    settings: OrbitSettings,
    onBack: () -> Unit,
    onSaveProfile: (
        handle: String,
        displayName: String,
        vkHandle: String?,
        protonHandle: String?,
        telegramHandle: String?,
        cloudSync: Boolean,
        syncBookmarks: Boolean,
        syncSettings: Boolean,
        syncHistory: Boolean
    ) -> Unit,
    onOpenPortalUrl: (String) -> Unit,
    onSignOut: () -> Unit
) {
    BackHandler(onBack = onBack)
    val tokens = LocalOrbitTokens.current
    val isRu = settings.resolvedLanguage == AppLanguage.RU

    var handle by remember(profile?.naccountHandle) { mutableStateOf(profile?.naccountHandle ?: "@orbit_user") }
    var displayName by remember(profile?.displayName) { mutableStateOf(profile?.displayName ?: "Orbit User") }
    var vkHandle by remember(profile?.vkHandle) { mutableStateOf(profile?.vkHandle.orEmpty()) }
    var protonHandle by remember(profile?.protonHandle) { mutableStateOf(profile?.protonHandle.orEmpty()) }
    var telegramHandle by remember(profile?.telegramHandle) { mutableStateOf(profile?.telegramHandle.orEmpty()) }
    var cloudSync by remember(profile?.cloudSyncEnabled) { mutableStateOf(profile?.cloudSyncEnabled ?: true) }
    var syncBookmarks by remember(profile?.syncBookmarks) { mutableStateOf(profile?.syncBookmarks ?: true) }
    var syncSettings by remember(profile?.syncSettings) { mutableStateOf(profile?.syncSettings ?: true) }
    var syncHistory by remember(profile?.syncHistory) { mutableStateOf(profile?.syncHistory ?: true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .widthIn(max = 680.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = tokens.onCleanBackground)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isRu) "NAccount · Облако и Профиль" else "NAccount · Cloud Sync",
                style = MaterialTheme.typography.titleLarge,
                color = tokens.onCleanBackground
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Summary
            item {
                OrbitGlassSurface(
                    shape = orbitContainerShape(22.dp),
                    elevated = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = tokens.accentPrimary.copy(alpha = 0.2f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Rounded.Person, contentDescription = null, tint = tokens.accentPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(displayName, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = profile?.email ?: handle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.secondaryText
                                )
                            }
                            Icon(Icons.Rounded.CloudDone, contentDescription = "Cloud Synced", tint = Color(0xFF10E89A))
                        }

                        OutlinedTextField(
                            value = handle,
                            onValueChange = { handle = it },
                            label = { Text("NAccount ID (@handle)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text(if (isRu) "Имя профиля" else "Display Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Connected Quick-Sign-In Services (Google, VK, ProtonMail, Telegram)
            item {
                OrbitGlassSurface(
                    shape = orbitContainerShape(22.dp),
                    elevated = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isRu) "Подключённые аккаунты и быстрый вход" else "Connected Accounts & Quick Portals",
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Google (Active via Credential Manager)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Google Identity", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Text(profile?.email ?: "Connected", style = MaterialTheme.typography.bodyMedium, color = tokens.secondaryText)
                            }
                            Icon(Icons.Rounded.CheckCircle, contentDescription = "Connected", tint = Color(0xFF10E89A))
                        }

                        ProviderConnectorRow(
                            label = "VK ID",
                            value = vkHandle,
                            placeholder = "vk.com/username",
                            portalUrl = "https://id.vk.com",
                            onValueChange = { vkHandle = it },
                            onOpenPortal = onOpenPortalUrl,
                            isRu = isRu
                        )
                        ProviderConnectorRow(
                            label = "ProtonMail",
                            value = protonHandle,
                            placeholder = "user@proton.me",
                            portalUrl = "https://account.proton.me",
                            onValueChange = { protonHandle = it },
                            onOpenPortal = onOpenPortalUrl,
                            isRu = isRu
                        )
                        ProviderConnectorRow(
                            label = "Telegram",
                            value = telegramHandle,
                            placeholder = "@username",
                            portalUrl = "https://web.telegram.org",
                            onValueChange = { telegramHandle = it },
                            onOpenPortal = onOpenPortalUrl,
                            isRu = isRu
                        )
                    }
                }
            }

            // Cloud Sync Switches
            item {
                OrbitGlassSurface(
                    shape = orbitContainerShape(22.dp),
                    elevated = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isRu) "Облачное хранилище Orbit" else "Orbit Cloud Sync",
                            style = MaterialTheme.typography.titleMedium
                        )
                        SyncSwitchRow(if (isRu) "Облачная синхронизация" else "Master Cloud Sync", cloudSync) { cloudSync = it }
                        SyncSwitchRow(if (isRu) "Избранное и закладки" else "Favorites & Bookmarks", syncBookmarks) { syncBookmarks = it }
                        SyncSwitchRow(if (isRu) "Настройки, темы и режимы" else "Settings & Themes", syncSettings) { syncSettings = it }
                        SyncSwitchRow(if (isRu) "История посещений" else "Browsing History", syncHistory) { syncHistory = it }
                    }
                }
            }

            // Save & Sign Out
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            onSaveProfile(
                                handle,
                                displayName,
                                vkHandle,
                                protonHandle,
                                telegramHandle,
                                cloudSync,
                                syncBookmarks,
                                syncSettings,
                                syncHistory
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentPrimary,
                            contentColor = Color(0xFF061018)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("naccount_save_button")
                    ) {
                        Text(if (isRu) "Сохранить и синхронизировать" else "Save & Sync to Cloud", fontWeight = FontWeight.Bold)
                    }

                    val isGuest = profile?.userId == "guest_user" || profile?.cloudSyncEnabled == false
                    if (isGuest) {
                        Button(
                            onClick = onSignOut,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = tokens.accentPrimary,
                                contentColor = Color(0xFF061018)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("naccount_signin_prompt_button")
                        ) {
                            Icon(Icons.Rounded.Person, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isRu) "Войти через Google и включить Облако" else "Sign In with Google for Cloud Sync",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = onSignOut,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("naccount_signout_button")
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isRu) "Выйти из NAccount" else "Sign Out", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderConnectorRow(
    label: String,
    value: String,
    placeholder: String,
    portalUrl: String,
    onValueChange: (String) -> Unit,
    onOpenPortal: (String) -> Unit,
    isRu: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { onOpenPortal(portalUrl) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                contentDescription = if (isRu) "Открыть $label" else "Open $label",
                tint = LocalOrbitTokens.current.accentPrimary
            )
        }
    }
}

@Composable
private fun SyncSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun HistoryScreen(
    entries: List<HistoryEntry>,
    isRu: Boolean,
    onBack: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    BackHandler(onBack = onBack)
    val tokens = LocalOrbitTokens.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .widthIn(max = 680.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = tokens.onCleanBackground)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isRu) "История (Облако)" else "Cloud History",
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.onCleanBackground
                )
            }
            if (entries.isNotEmpty()) {
                TextButton(onClick = onClearHistory) {
                    Icon(Icons.Rounded.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isRu) "Очистить" else "Clear")
                }
            }
        }

        if (entries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.History, contentDescription = null, tint = tokens.secondaryText, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isRu) "История посещений пока пуста" else "No browsing history yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = tokens.secondaryText
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(entries, key = { it.id }) { item ->
                    OrbitGlassSurface(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .orbitBouncyClick { onOpenUrl(item.url) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = item.title.ifBlank { item.url },
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.url,
                                style = MaterialTheme.typography.bodyMedium,
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
