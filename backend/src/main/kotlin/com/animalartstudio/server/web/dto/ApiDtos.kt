package com.animalartstudio.server.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorBody(val error: String, val detail: String? = null)

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
    val minCoverage: Double,
    val maxCoverage: Double,
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
    val steps: List<LessonStepDto>,
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
    val tone: String, // "coach" | "celebrate"
    val coverage: Double,
    val nudgeCount: Int,
    val stepIndex: Int,
    val stepPassed: Boolean,
    val stepComplete: Boolean,
    val lessonComplete: Boolean,
    val nextStepIndex: Int,
    val bringToLifeUnlocked: Boolean,
    val animalKey: String,
    val animationPreset: String,
    val soundPackId: String,
    val magicRequiresMorePractice: Boolean,
    val nudgesRequiredForMagic: Int,
    val technique: String,
)

@Serializable
data class HelpArticleDto(
    val id: String,
    val title: String,
    val body: String,
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
    val id: Long,
    val stored: Boolean = true,
)
