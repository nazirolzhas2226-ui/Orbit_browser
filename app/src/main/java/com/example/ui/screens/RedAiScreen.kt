package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Summarize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.BrowserTab
import com.example.model.MercuryAiModel
import com.example.model.OrbitSettings
import com.example.model.RedAiMessage
import com.example.ui.components.MercuryAiLogoEmblem
import com.example.ui.components.OrbitGlassSurface
import com.example.ui.components.orbitBouncyClick
import com.example.ui.components.orbitContainerShape
import com.example.ui.theme.LocalOrbitTokens
import com.example.ui.theme.SpaceGroteskFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class RedAiMenuAction(
    val titleRu: String,
    val titleEn: String,
    val subtitleRu: String,
    val subtitleEn: String,
    val promptRu: String,
    val promptEn: String,
    val icon: ImageVector
)

@Composable
fun RedAiScreen(
    settings: OrbitSettings,
    activeTab: BrowserTab,
    messages: List<RedAiMessage>,
    isBusy: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onSelectModel: (MercuryAiModel) -> Unit = {}
) {
    BackHandler(onBack = onBack)
    val tokens = LocalOrbitTokens.current
    val isRu = settings.resolvedLanguage == AppLanguage.RU
    val activeModel = settings.resolvedMercuryModel
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Draw-in & glow intro state when entering MercuryAI
    var showDrawIntro by remember { mutableStateOf(true) }
    var introTriggerKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    val quickPrompts = if (isRu) {
        listOf(
            "Сделай краткую сводку этой страницы",
            "Проверь текущий сайт на безопасность",
            "Придумай настройки Orbit JupiterGX для игр",
            "Объясни, как работает VPN в браузере"
        )
    } else {
        listOf(
            "Summarize this webpage",
            "Check current URL for safety",
            "Suggest Orbit JupiterGX setup",
            "Explain browser tracker protection"
        )
    }

    val menuActions = remember {
        listOf(
            RedAiMenuAction(
                titleRu = "Сводка страницы",
                titleEn = "Page Summary",
                subtitleRu = "Быстрая выжимка",
                subtitleEn = "Instant digest",
                promptRu = "Сделай краткую сводку открытой страницы",
                promptEn = "Summarize the current webpage cleanly",
                icon = Icons.Rounded.Summarize
            ),
            RedAiMenuAction(
                titleRu = "Проверка угроз",
                titleEn = "Security Audit",
                subtitleRu = "Анализ фишинга",
                subtitleEn = "Phishing scan",
                promptRu = "Проверь текущий сайт на безопасность и трекеры",
                promptEn = "Check the current site for security and trackers",
                icon = Icons.Rounded.Shield
            ),
            RedAiMenuAction(
                titleRu = "ИИ-поиск Orbit",
                titleEn = "AI Web Search",
                subtitleRu = "Глубокий поиск",
                subtitleEn = "Deep answers",
                promptRu = "Найди ключевые факты и новости по теме космоса и технологий",
                promptEn = "Search key insights on space and modern tech",
                icon = Icons.Rounded.Search
            ),
            RedAiMenuAction(
                titleRu = "Оптимизация JupiterGX",
                titleEn = "JupiterGX Tuning",
                subtitleRu = "ОЗУ и скорость",
                subtitleEn = "RAM & speed",
                promptRu = "Подскажи лучшие настройки Orbit JupiterGX Mode для игр",
                promptEn = "Recommend the best Orbit JupiterGX Mode settings",
                icon = Icons.Rounded.SportsEsports
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main RedAI Menu & Chat Content (revealed cleanly after logo finishes drawing & glowing)
        AnimatedVisibility(
            visible = !showDrawIntro,
            enter = fadeIn(tween(340)) + slideInVertically(
                initialOffsetY = { it / 10 },
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .widthIn(max = 680.dp)
            ) {
                // Header with MercuryAI custom logo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("redai_back_button")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = tokens.onCleanBackground)
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFF1E38).copy(alpha = 0.14f),
                        modifier = Modifier
                            .size(42.dp)
                            .orbitBouncyClick {
                                introTriggerKey++
                                showDrawIntro = true
                            }
                            .testTag("redai_header_logo")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            MercuryAiLogoEmblem(size = 28.dp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MercuryAI",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = tokens.onCleanBackground
                        )
                        Text(
                            text = "${activeModel.labelEn} · ${settings.resolvedRedAiStyle.let { if (isRu) it.labelRu else it.labelEn }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFF3E55),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (messages.isNotEmpty()) {
                        IconButton(onClick = onClearChat) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "Clear Chat", tint = tokens.secondaryText)
                        }
                    }
                }

                // Selectable Newer Gemini Models Strip
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MercuryAiModel.entries) { modelOption ->
                        val selected = activeModel == modelOption
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectModel(modelOption) },
                            label = {
                                Text(
                                    text = if (isRu) {
                                        "${modelOption.labelRu} · ${modelOption.badgeRu}"
                                    } else {
                                        "${modelOption.labelEn} · ${modelOption.badgeEn}"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("mercury_model_chip_${modelOption.name.lowercase()}")
                        )
                    }
                }

                // Active page context pill
                if (!activeTab.url.startsWith("orbit://")) {
                    OrbitGlassSurface(
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Public, contentDescription = null, tint = tokens.accentPrimary, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = (if (isRu) "Контекст вкладки: " else "Tab context: ") + activeTab.url,
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.accentPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Error Banner
                AnimatedVisibility(visible = errorMessage != null, enter = fadeIn()) {
                    errorMessage?.let { err ->
                        OrbitGlassSurface(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // Messages or RedAI Welcome & Menu Grid
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (messages.isEmpty()) {
                        item {
                            OrbitGlassSurface(
                                shape = orbitContainerShape(24.dp),
                                elevated = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("redai_menu_welcome")
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = Color(0xFFFF3E66).copy(alpha = 0.16f),
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                MercuryAiLogoEmblem(size = 30.dp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = if (isRu) "Меню MercuryAI · ${activeModel.labelRu}" else "MercuryAI Menu · ${activeModel.labelEn}",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = tokens.onCleanBackground
                                            )
                                            Text(
                                                text = if (isRu) "Умный ассистент и поиск нового поколения" else "Next-Gen Intelligent Assistant & Search",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color(0xFFFF3E55)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isRu) {
                                            "Выберите модель выше, режим из меню ниже или введите запрос в поиск MercuryAI:"
                                        } else {
                                            "Pick a model above, a tool below, or search with MercuryAI:"
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = tokens.secondaryText
                                    )
                                }
                            }
                        }

                        // 2x2 RedAI Command Menu Grid
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                menuActions.chunked(2).forEach { pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        pair.forEach { action ->
                                            OrbitGlassSurface(
                                                shape = orbitContainerShape(20.dp),
                                                elevated = true,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .orbitBouncyClick(enabled = !isBusy) {
                                                        onSendMessage(if (isRu) action.promptRu else action.promptEn)
                                                    }
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(14.dp),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(10.dp),
                                                        color = Color(0xFFFF0F24).copy(alpha = 0.14f),
                                                        modifier = Modifier.size(34.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                imageVector = action.icon,
                                                                contentDescription = null,
                                                                tint = Color(0xFFFF2E4D),
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        text = if (isRu) action.titleRu else action.titleEn,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = tokens.onCleanBackground,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = if (isRu) action.subtitleRu else action.subtitleEn,
                                                        style = MaterialTheme.typography.labelSmall,
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
                    } else {
                        items(messages, key = { it.id }) { msg ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                            ) {
                                if (msg.isUser) {
                                    Box(
                                        modifier = Modifier
                                            .widthIn(max = 310.dp)
                                            .background(
                                                Brush.linearGradient(listOf(tokens.accentPrimary, tokens.accentSecondary)),
                                                shape = RoundedCornerShape(20.dp, 20.dp, 6.dp, 20.dp)
                                            )
                                            .padding(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = msg.text,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White
                                        )
                                    }
                                } else {
                                    OrbitGlassSurface(
                                        shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 6.dp),
                                        elevated = true,
                                        modifier = Modifier.widthIn(max = 330.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                MercuryAiLogoEmblem(size = 18.dp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "MercuryAI · ${msg.modelLabel}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFFFF3E55),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            if (msg.text.isBlank() && msg.isStreaming) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(18.dp),
                                                    color = Color(0xFFFF2E4D),
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Text(
                                                    text = msg.text,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = tokens.onCleanBackground
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Prompt Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickPrompts) { suggestion ->
                        AssistChip(
                            onClick = { if (!isBusy) onSendMessage(suggestion) },
                            label = { Text(suggestion, maxLines = 1) }
                        )
                    }
                }

                // RedAI Search / Prompt Input Bar with leading RedAI Logo emblem
                OrbitGlassSurface(
                    shape = orbitContainerShape(28.dp),
                    elevated = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Leading MercuryAI Logo inside the Search/Query field
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF0F24).copy(alpha = 0.14f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                MercuryAiLogoEmblem(size = 22.dp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (input.isEmpty()) {
                                Text(
                                    text = if (isRu) "Поиск и вопросы MercuryAI (${activeModel.labelEn})..." else "Search or ask MercuryAI (${activeModel.labelEn})...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = tokens.secondaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            BasicTextField(
                                value = input,
                                onValueChange = { input = it },
                                textStyle = TextStyle(
                                    color = tokens.onCleanBackground,
                                    fontSize = 15.sp,
                                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                                ),
                                cursorBrush = SolidColor(Color(0xFFFF2E4D)),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        if (input.isNotBlank() && !isBusy) {
                                            val prompt = input
                                            input = ""
                                            onSendMessage(prompt)
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("redai_input_field")
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF1E38),
                            modifier = Modifier
                                .size(40.dp)
                                .orbitBouncyClick(enabled = input.isNotBlank() && !isBusy) {
                                    val prompt = input
                                    input = ""
                                    onSendMessage(prompt)
                                }
                                .testTag("redai_send_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isBusy) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.Send,
                                        contentDescription = "Send",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated Hand-Drawn + Glowing MercuryAI Intro Splash before displaying the menu
        AnimatedVisibility(
            visible = showDrawIntro,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(340)) + scaleOut(targetScale = 1.06f, animationSpec = tween(320))
        ) {
            MercuryAiDrawGlowIntroOverlay(
                triggerKey = introTriggerKey,
                modelLabel = activeModel.labelEn,
                isRu = isRu,
                onComplete = { showDrawIntro = false }
            )
        }
    }
}

/**
 * Пошаговая анимация при входе в MercuryAI:
 * 1. Сферический ртутный логотип MercuryAI плавно рисуется по контуру.
 * 2. Вспыхивает алым и платиновым неоном.
 * 3. Появляется название MercuryAI и открывается чат с выбором моделей.
 */
@Composable
private fun MercuryAiDrawGlowIntroOverlay(
    triggerKey: Int,
    modelLabel: String,
    isRu: Boolean,
    onComplete: () -> Unit
) {
    val drawProgress = remember(triggerKey) { Animatable(0f) }
    val glowIntensity = remember(triggerKey) { Animatable(0f) }
    val titleAlpha = remember(triggerKey) { Animatable(0f) }
    val scale = remember(triggerKey) { Animatable(0.92f) }

    LaunchedEffect(triggerKey) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow)
            )
        }
        drawProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 860, easing = FastOutSlowInEasing)
        )
        launch { titleAlpha.animateTo(1f, tween(280)) }
        glowIntensity.animateTo(
            targetValue = 1.15f,
            animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing)
        )
        glowIntensity.animateTo(
            targetValue = 0.65f,
            animationSpec = tween(durationMillis = 320, easing = LinearEasing)
        )
        delay(240)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2B0610),
                        Color(0xFF10080C),
                        Color(0xFF050204)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onComplete
            )
            .testTag("redai_draw_intro"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
        ) {
            MercuryAiLogoEmblem(
                size = 160.dp,
                drawProgress = drawProgress.value,
                glowIntensity = glowIntensity.value
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "MercuryAI",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                color = Color.White,
                modifier = Modifier.alpha(titleAlpha.value)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isRu) "$modelLabel · Инициализация..." else "$modelLabel · Initializing...",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                letterSpacing = 1.2.sp,
                color = Color(0xFFFF4D64),
                modifier = Modifier.alpha(titleAlpha.value)
            )
        }
    }
}
