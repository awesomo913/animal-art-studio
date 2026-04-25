package com.animalartstudio.server.service

data class LessonStepInfo(
    val lessonId: String,
    val stepIndex: Int,
    val title: String,
    val instruction: String,
    val technique: String,
    val minCoverage: Double,
    val maxCoverage: Double,
    val hintEmpty: String,
    val hintMore: String,
    val hintAlmost: String,
    val celebrate: String,
)
