package com.animalartstudio.kids.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animalartstudio.kids.Graph
import com.animalartstudio.kids.data.CreateSessionRequest
import com.animalartstudio.kids.data.FeedbackResponse
import com.animalartstudio.kids.data.LessonDetailDto
import com.animalartstudio.kids.data.LessonStepDto
import com.animalartstudio.kids.data.SubmitStepRequest
import com.animalartstudio.kids.net.ApiException
import com.animalartstudio.kids.ui.shared.DrawScratchpad
import kotlinx.coroutines.launch

sealed class LessonUi {
  data object Loading : LessonUi()
  data class Board(
      val lesson: LessonDetailDto,
      val step: LessonStepDto,
      val stepIndex: Int,
      val feedback: FeedbackResponse?,
      val err: String?,
  ) : LessonUi()
  data class Oops(
      val message: String,
  ) : LessonUi()
}

class LessonViewModel(
    private val lessonId: String,
) : ViewModel() {
  private val app = Graph.get()
  var ui: LessonUi by mutableStateOf(LessonUi.Loading)
  var sessionId: String? = null
  var stepIndex: Int by mutableIntStateOf(0)
  var busy: Boolean by mutableStateOf(false)

  init {
    boot()
  }

  private fun boot() =
      viewModelScope.launch {
        try {
          val lesson = app.api.getLesson(lessonId)
          val s =
              app.api.createSession(
                  CreateSessionRequest(lessonId = lessonId, deviceId = app.deviceId))
          sessionId = s.sessionId
          stepIndex = 0
          val step = lesson.steps.first { it.index == 0 }
          ui =
              LessonUi.Board(
                  lesson = lesson, step = step, stepIndex = 0, feedback = null, err = null)
        } catch (e: Exception) {
          val msg = (e as? ApiException)?.detail ?: (e.message ?: "network")
          ui = LessonUi.Oops("Could not start the lesson. $msg")
        }
      }

  fun submitPng(
      b64: String,
      onDone: (FeedbackResponse) -> Unit,
  ) =
      viewModelScope.launch {
        val sid = sessionId ?: return@launch
        val cur = ui as? LessonUi.Board ?: return@launch
        val sidx = cur.stepIndex
        busy = true
        try {
          val fb = app.api.submit(sid, SubmitStepRequest(stepIndex = sidx, imageBase64 = b64))
          if (cur.lesson.id != lessonId) return@launch
          val (nextStep, nextDto) = advance(cur.lesson, sidx, fb)
          stepIndex = nextStep
          ui = LessonUi.Board(lesson = cur.lesson, step = nextDto, stepIndex = nextStep, feedback = fb, err = null)
          onDone(fb)
        } catch (e: Exception) {
          val m = (e as? ApiException)?.detail ?: (e.message ?: "not_ok")
          ui = cur.copy(err = m)
        } finally {
          busy = false
        }
      }

  private fun advance(
      l: LessonDetailDto,
      fromIdx: Int,
      fb: FeedbackResponse,
  ): Pair<Int, LessonStepDto> {
    if (fb.stepPassed && fromIdx < l.steps.maxOf { it.index }) {
      return Pair(fromIdx + 1, l.steps.first { it.index == fromIdx + 1 })
    }
    if (fb.stepPassed && fromIdx == l.steps.maxOf { it.index }) {
      // stay on last while showing celebrate — UI navigates
      return Pair(fromIdx, l.steps.first { it.index == fromIdx })
    }
    return Pair(fromIdx, l.steps.first { it.index == fromIdx })
  }

  fun rememberForCelebrate(
      bmp: Bitmap,
      key: String,
  ) {
    DrawScratchpad.lastDrawing = bmp
    DrawScratchpad.animalKey = key
  }
}

