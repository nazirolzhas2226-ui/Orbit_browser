package com.example.data

import android.util.Log
import com.example.model.AppLanguage
import com.example.model.MercuryAiModel
import com.example.model.RedAiStyle
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "MercuryAiRepository"

class RedAiRepository {

    companion object {
        const val MODEL_NAME = "gemini-3.8-flash"
    }

    private val modelCache = mutableMapOf<String, GenerativeModel>()

    @Synchronized
    private fun getModel(option: MercuryAiModel): GenerativeModel {
        return modelCache.getOrPut(option.modelId) {
            Firebase.ai.generativeModel(
                modelName = option.modelId,
                systemInstruction = content {
                    text(
                        "You are MercuryAI powered by Google's ${option.labelEn} (${option.modelId}) engine inside Orbit Browser. " +
                            "Respond cleanly, accurately, and helpfully strictly in the requested language " +
                            "(Russian, English, Kazakh, Spanish, German, or French). " +
                            "Help users summarize web pages, check URL phishing/safety, explain topics or code, " +
                            "and optimize their browsing experience. Keep formatting crisp for mobile reading."
                    )
                }
            )
        }
    }

    fun streamPrompt(
        userPrompt: String,
        pageContextUrl: String?,
        pageContextTitle: String?,
        pageSnippet: String?,
        style: RedAiStyle,
        language: AppLanguage,
        modelOption: MercuryAiModel = MercuryAiModel.MERCURY_38_FLASH,
        conversationHistory: List<Pair<Boolean, String>> = emptyList()
    ): Flow<String> = flow {
        val langName = when (language) {
            AppLanguage.RU -> "Russian"
            AppLanguage.EN -> "English"
            AppLanguage.KK -> "Kazakh"
            AppLanguage.ES -> "Spanish"
            AppLanguage.DE -> "German"
            AppLanguage.FR -> "French"
        }
        val styleDirective = when (style) {
            RedAiStyle.CONCISE -> "Respond in $langName. Be concise, practical, and direct."
            RedAiStyle.DETAILED -> "Respond in $langName. Provide a well-structured, detailed explanation."
            RedAiStyle.TECHNICAL -> "Respond in $langName. Use precise technical terminology and actionable specifics."
        }
        val contextSection = buildString {
            if (!pageContextUrl.isNullOrBlank() && !pageContextUrl.startsWith("orbit://")) {
                append("\nActive browser tab URL: ").append(pageContextUrl)
                if (!pageContextTitle.isNullOrBlank()) {
                    append("\nPage title: ").append(pageContextTitle)
                }
                if (!pageSnippet.isNullOrBlank()) {
                    append("\nPage excerpt: ").append(pageSnippet.take(2500))
                }
            }
            val recentTurns = conversationHistory.filter { it.second.isNotBlank() }.takeLast(6)
            if (recentTurns.isNotEmpty()) {
                append("\n\nRecent conversation:")
                recentTurns.forEach { (isUser, msgText) ->
                    append(if (isUser) "\nUser: " else "\nMercuryAI: ")
                    append(msgText.take(600))
                }
            }
        }
        val finalPrompt = "$styleDirective$contextSection\n\nUser request: $userPrompt"

        val activeModel = getModel(modelOption)
        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                activeModel.generateContentStream(finalPrompt).collect { chunk ->
                    val part = chunk.text.orEmpty()
                    if (part.isNotEmpty()) {
                        accumulated.append(part)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                throw IllegalStateException("MercuryAI returned an empty response.")
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "MercuryAI streaming timed out (${modelOption.modelId})", e)
            throw e
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "MercuryAI inference failed (${modelOption.modelId})", e)
            val raw = e.localizedMessage.orEmpty()
            if (modelOption == MercuryAiModel.MERCURY_4_ARGON &&
                (raw.contains("billing", ignoreCase = true) || raw.contains("quota", ignoreCase = true) || raw.contains("403"))
            ) {
                val hint = if (language == AppLanguage.RU) {
                    "Mercury 4 Argon требует план Blaze. Выберите модель Mercury 3.8 Flash или 3.5 Lite сверху. ($raw)"
                } else {
                    "Mercury 4 Argon requires a Blaze billing plan. Switch to Mercury 3.8 Flash or 3.5 Lite above. ($raw)"
                }
                throw IllegalStateException(hint, e)
            }
            throw e
        }
    }

    suspend fun analyzeUrlSecurity(
        url: String,
        language: AppLanguage,
        modelOption: MercuryAiModel = MercuryAiModel.MERCURY_38_FLASH
    ): String {
        val langName = when (language) {
            AppLanguage.RU -> "Russian"
            AppLanguage.EN -> "English"
            AppLanguage.KK -> "Kazakh"
            AppLanguage.ES -> "Spanish"
            AppLanguage.DE -> "German"
            AppLanguage.FR -> "French"
        }
        val prompt = "Respond in $langName. Analyze this URL for phishing, typosquatting, malware, or privacy risks and give a crisp 2-3 sentence security verdict: $url"
        return try {
            val activeModel = getModel(modelOption)
            withTimeout(45_000L) {
                val response = activeModel.generateContent(prompt)
                response.text?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException("Empty security analysis response.")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "MercuryAI URL security check failed", e)
            throw e
        }
    }
}

