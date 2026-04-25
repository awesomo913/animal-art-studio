package com.animalartstudio.kids.data

import kotlinx.serialization.Serializable

@Serializable
data class LessonSummaryDto(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val animalKey: String,
    val orderIndex: Int,
    val estMinutes: Int,
    val version: Int,
)

@Serializable
data class LessonStepDto(
    val index: Int,
    val title: String,
    val instruction: String,
    val technique: String,
    val minCoverage: Double = 0.0,
    val maxCoverage: Double = 1.0,
    val colorHint: String? = null,
)

@Serializable
data class LessonDetailDto(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val animalKey: String,
    val estMinutes: Int,
    val version: Int,
    val steps: List<LessonStepDto> = emptyList(),
)

@Serializable
data class CreateSessionRequest(
    val lessonId: String,
    val deviceId: String? = null,
)

@Serializable
data class SessionResponse(
    val sessionId: String,
    val lessonId: String,
    val nudgeCount: Int,
    val highestStepCompleted: Int,
    val version: Int,
)

@Serializable
data class SubmitStepRequest(
    val stepIndex: Int,
    val imageBase64: String,
)

@Serializable
data class FeedbackResponse(
    val message: String,
    val tone: String = "coach",
    val coverage: Double = 0.0,
    val nudgeCount: Int = 0,
    val stepIndex: Int = 0,
    val stepPassed: Boolean = false,
    val stepComplete: Boolean = false,
    val lessonComplete: Boolean = false,
    val nextStepIndex: Int = 0,
    val bringToLifeUnlocked: Boolean = false,
    val animalKey: String = "friend",
    val animationPreset: String = "friend",
    val soundPackId: String = "friend",
    val magicRequiresMorePractice: Boolean = false,
    val nudgesRequiredForMagic: Int = 5,
    val technique: String = "",
)

@Serializable
data class HelpArticleDto(
    val id: String,
    val title: String,
    val body: String,
)

@Serializable
data class ErrorBody(
    val error: String,
    val detail: String? = null,
)

@Serializable
data class ClientCrashIngest(
    val deviceId: String,
    val appVersion: String,
    val payloadJson: String = "{}",
    val recentLogLines: List<String> = emptyList(),
)

@Serializable
data class ClientCrashAck(
    val id: Long = 0,
    val stored: Boolean = true,
)
