package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.data.OrbitFirestoreRepository
import com.example.firebase.attemptAutoSignIn
import com.example.firebase.onGoogleSignInClicked
import com.example.model.PendingNAccountRegistration
import com.example.model.WallpaperStyle
import com.example.ui.components.JupiterGxLogoEmblem
import com.example.ui.components.MercuryAiLogoEmblem
import com.example.ui.components.OrbitBrandHeader
import com.example.ui.components.OrbitGlassSurface
import com.example.ui.components.OrbitWallpaperLayer
import com.example.ui.components.orbitBouncyClick
import com.example.ui.theme.LocalOrbitTokens

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onContinueAsGuest: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val tokens = LocalOrbitTokens.current
    val scrollState = rememberScrollState()

    var isLoading by remember { mutableStateOf(false) }
    var isCheckingAuto by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dual mode: Sign-In vs NAccount Registration
    var isRegisterMode by remember { mutableStateOf(false) }
    var regHandle by remember { mutableStateOf("@") }
    var regDisplayName by remember { mutableStateOf("") }
    var regTelegram by remember { mutableStateOf("") }
    var regVk by remember { mutableStateOf("") }
    var regProton by remember { mutableStateOf("") }
    var regCloudSync by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                isCheckingAuto = false
                onAuthSuccess()
            },
            onUnauthenticated = {
                isCheckingAuto = false
            },
            scope = scope
        )
    }

    fun buildValidatedRegistration(): PendingNAccountRegistration? {
        val rawHandle = regHandle.trim()
        val normalizedHandle = (if (rawHandle.startsWith("@")) rawHandle else "@$rawHandle")
            .replace(" ", "_")
            .take(32)
        val cleanName = regDisplayName.trim().take(60)
        if (normalizedHandle.removePrefix("@").length < 2) {
            errorMessage = "Укажите уникальный NAccount ID (минимум 3 символа, например @orbit_star)"
            return null
        }
        if (cleanName.isEmpty()) {
            errorMessage = "Введите имя профиля для регистрации NAccount"
            return null
        }
        return PendingNAccountRegistration(
            naccountHandle = normalizedHandle,
            displayName = cleanName,
            vkHandle = regVk.trim().takeIf { it.isNotEmpty() },
            protonHandle = regProton.trim().takeIf { it.isNotEmpty() },
            telegramHandle = regTelegram.trim().takeIf { it.isNotEmpty() },
            cloudSyncEnabled = regCloudSync,
            syncBookmarks = true,
            syncSettings = true,
            syncHistory = true
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OrbitWallpaperLayer(wallpaper = WallpaperStyle.LIQUID_AURORA, showStarfield = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .widthIn(max = 520.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OrbitBrandHeader()
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "NAccount Cloud & Orbit Browser",
                style = MaterialTheme.typography.titleMedium,
                color = tokens.onCleanBackground.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Switcher: Вход / Регистрация NAccount
            OrbitGlassSurface(
                shape = RoundedCornerShape(22.dp),
                elevated = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AuthModeTabPill(
                        label = "Вход",
                        icon = Icons.Rounded.Login,
                        selected = !isRegisterMode,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("auth_tab_signin"),
                        onClick = {
                            errorMessage = null
                            isRegisterMode = false
                        }
                    )
                    AuthModeTabPill(
                        label = "Регистрация NAccount",
                        icon = Icons.Rounded.AppRegistration,
                        selected = isRegisterMode,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("auth_tab_register"),
                        onClick = {
                            errorMessage = null
                            isRegisterMode = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            AnimatedContent(
                targetState = isRegisterMode,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "authRegisterContent"
            ) { registerTab ->
                if (!registerTab) {
                    OrbitGlassSurface(
                        shape = RoundedCornerShape(28.dp),
                        elevated = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            FeatureRow(
                                icon = Icons.Rounded.CloudSync,
                                title = "NAccount Облако",
                                subtitle = "Синхронизация закладок, вкладок, тем и настроек"
                            )
                            FeatureRow(
                                icon = Icons.Rounded.AutoAwesome,
                                title = "MercuryAI · Gemini 3.8",
                                subtitle = "ИИ-сводка страниц, чат и выбор новых моделей",
                                customIcon = { MercuryAiLogoEmblem(size = 24.dp) }
                            )
                            FeatureRow(
                                icon = Icons.Rounded.Shield,
                                title = "Щит Orbit + VPN + Антивирус",
                                subtitle = "Блокировка рекламы, трекеров, Kaspersky и Dr.Web"
                            )
                            FeatureRow(
                                icon = Icons.Rounded.SportsEsports,
                                title = "JupiterGX Mode & Производительность",
                                subtitle = "Кибер-Юпитер, RGB-контур и лимитер ОЗУ",
                                customIcon = { JupiterGxLogoEmblem(size = 24.dp) }
                            )
                        }
                    }
                } else {
                    OrbitGlassSurface(
                        shape = RoundedCornerShape(28.dp),
                        elevated = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Создание профиля NAccount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = tokens.onCleanBackground
                            )
                            Text(
                                text = "Задайте личный @handle и имя — они сразу привяжутся к вашему аккаунту Google или локальному профилю.",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.secondaryText
                            )
                            OutlinedTextField(
                                value = regHandle,
                                onValueChange = { regHandle = it; errorMessage = null },
                                label = { Text("NAccount ID (@handle)") },
                                placeholder = { Text("@orbit_explorer") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_handle_input")
                            )
                            OutlinedTextField(
                                value = regDisplayName,
                                onValueChange = { regDisplayName = it; errorMessage = null },
                                label = { Text("Имя профиля") },
                                placeholder = { Text("Алексей") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_name_input")
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = regTelegram,
                                    onValueChange = { regTelegram = it },
                                    label = { Text("Telegram") },
                                    placeholder = { Text("@user") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = regVk,
                                    onValueChange = { regVk = it },
                                    label = { Text("VK ID") },
                                    placeholder = { Text("vk.com/id") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            OutlinedTextField(
                                value = regProton,
                                onValueChange = { regProton = it },
                                label = { Text("ProtonMail (необязательно)") },
                                placeholder = { Text("name@proton.me") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Синхронизация тем, закладок и истории",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.onCleanBackground,
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = regCloudSync,
                                    onCheckedChange = { regCloudSync = it }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            AnimatedVisibility(visible = errorMessage != null, enter = fadeIn()) {
                errorMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    errorMessage = null
                    if (isRegisterMode) {
                        val pending = buildValidatedRegistration() ?: return@Button
                        OrbitFirestoreRepository.savePendingRegistration(context, pending)
                    }
                    isLoading = true
                    onGoogleSignInClicked(
                        context = context,
                        credentialManager = credentialManager,
                        onAuthSuccess = {
                            isLoading = false
                            onAuthSuccess()
                        },
                        onAuthError = { msg ->
                            isLoading = false
                            errorMessage = msg
                        },
                        scope = scope,
                        onAuthCancelled = {
                            isLoading = false
                        }
                    )
                },
                enabled = !isLoading && !isCheckingAuto,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.accentPrimary,
                    contentColor = Color(0xFF061018)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("google_sign_in_button")
            ) {
                if (isLoading || isCheckingAuto) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color(0xFF061018),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = if (isRegisterMode) "Зарегистрировать NAccount через Google" else "Войти в NAccount через Google",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OrbitGlassSurface(
                shape = RoundedCornerShape(24.dp),
                elevated = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .orbitBouncyClick {
                        errorMessage = null
                        if (isRegisterMode) {
                            val reg = buildValidatedRegistration() ?: return@orbitBouncyClick
                            OrbitFirestoreRepository.saveGuestRegistration(context, reg)
                        }
                        onContinueAsGuest()
                    }
                    .testTag("continue_as_guest_button")
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = if (isRegisterMode) "Создать локальный профиль (без Google)" else "Продолжить без аккаунта (Гость)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.onCleanBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isRegisterMode) {
                    "При выборе Google профиль сохранится в облаке Firestore, а при локальном создании — на устройстве."
                } else {
                    "Локальный режим: полная приватность, быстрый старт без входа. Облако NAccount можно включить в любой момент."
                },
                style = MaterialTheme.typography.bodySmall,
                color = tokens.onCleanBackground.copy(alpha = 0.72f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AuthModeTabPill(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val tokens = LocalOrbitTokens.current
    OrbitGlassSurface(
        shape = RoundedCornerShape(16.dp),
        elevated = selected,
        accentHighlight = selected,
        modifier = modifier
            .height(42.dp)
            .orbitBouncyClick(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) tokens.accentPrimary else tokens.secondaryText,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) tokens.onCleanBackground else tokens.secondaryText,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    customIcon: (@Composable () -> Unit)? = null
) {
    val tokens = LocalOrbitTokens.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OrbitGlassSurface(
            shape = RoundedCornerShape(14.dp),
            elevated = true,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (customIcon != null) {
                    customIcon()
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = tokens.accentPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = tokens.onCleanBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.secondaryText
            )
        }
    }
}
