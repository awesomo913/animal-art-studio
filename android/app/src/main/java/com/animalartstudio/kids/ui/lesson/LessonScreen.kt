package com.animalartstudio.kids.ui.lesson

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.animalartstudio.kids.data.FeedbackResponse
import com.animalartstudio.kids.draw.InkStroke
import com.animalartstudio.kids.draw.StrokesBitmap
import com.animalartstudio.kids.ui.LessonUi
import com.animalartstudio.kids.ui.LessonViewModel
import com.animalartstudio.kids.ui.draw.DoodleCanvas
import com.animalartstudio.kids.ui.theme.DoodleCream
import com.animalartstudio.kids.ui.theme.Forest
import com.animalartstudio.kids.ui.theme.SkyPop
import com.animalartstudio.kids.ui.theme.Ink
import com.animalartstudio.kids.ui.theme.CoralHug

@Composable
fun LessonRoute(
    lessonId: String,
    onBack: () -> Unit,
    onCelebrate: () -> Unit,
) {
  val vm: LessonViewModel = viewModel(key = lessonId) { LessonViewModel(lessonId) }
  val view = LocalView.current
  when (val s = vm.ui) {
    LessonUi.Loading -> {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Mixing the paints…", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
      }
    }
    is LessonUi.Oops -> {
      Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Whoops, little friend.", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
          Text(s.message, style = MaterialTheme.typography.bodyLarge)
          FilledTonalButton(onClick = onBack) { Text("Back home") }
        }
      }
    }
    is LessonUi.Board -> {
      val b = s
      var w by remember { mutableFloatStateOf(1f) }
      var hgt by remember { mutableFloatStateOf(1f) }
      val inkOpts = listOf(Forest, Color(0xFF4C3A7C), Color(0xFFCD4C4C))
      var idx by remember(lessonId, b.stepIndex) { mutableStateOf(0) }
      var ink by remember(lessonId, b.stepIndex) { mutableStateOf(inkOpts[0]) }
      var strokes by remember(lessonId, b.stepIndex) { mutableStateOf(listOf<InkStroke>()) }
      var clearSalt by remember(lessonId, b.stepIndex) { mutableStateOf(0) }
      LaunchedEffect(lessonId, b.stepIndex) {
        idx = 0
        ink = inkOpts[0]
        strokes = emptyList()
        clearSalt = 0
      }
      Column(
          modifier =
              Modifier
                  .fillMaxSize()
                  .background(DoodleCream)
                  .padding(16.dp),
      ) {
        Text(b.lesson.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(
            "Step ${b.stepIndex + 1} of ${b.lesson.steps.size}: ${b.step.title}",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )
        Spacer(Modifier.height(6.dp))
        Text(b.step.instruction, lineHeight = 24.sp, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(
            "Technique: ${b.step.technique}",
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
          inkOpts.forEachIndexed { i, c ->
            FilledTonalButton(
                onClick = { idx = i; ink = c; view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK) },
                colors =
                    if (i == idx) {
                      ButtonDefaults.filledTonalButtonColors(containerColor = c.copy(alpha = 0.3f), contentColor = Ink)
                    } else {
                      ButtonDefaults.filledTonalButtonColors()
                    },
            ) { Text(" ") }
          }
        }
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().weight(1f)) {
          DoodleCanvas(
              paper = DoodleCream,
              ink = ink,
              strokeWidth = 7f,
              stepKey = b.stepIndex * 1_000_000 + clearSalt,
              onSize = { ww, hh ->
                w = ww
                hgt = hh
              },
              onStrokesChange = { strokes = it },
          )
        }
        b.feedback?.let { fb -> CoachCard(fb) }
        if (!b.err.isNullOrBlank()) {
          Text(
              " Tiny tip: ${b.err}",
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(top = 4.dp),
          )
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilledTonalButton(
              onClick = { clearSalt++; strokes = emptyList() },
          ) { Text("Clear this step") }
          Button(
              onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                val png = StrokesBitmap.toPngBase64(strokes, w, hgt)
                vm.submitPng(png) { res ->
                  val bmp = StrokesBitmap.toPreviewBitmap(strokes, w, hgt, 900, 900)
                  vm.rememberForCelebrate(bmp, res.animalKey)
                  if (res.bringToLifeUnlocked) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    onCelebrate()
                  } else if (res.magicRequiresMorePractice) {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                  }
                }
              },
              enabled = !vm.busy && w > 2f,
              colors = ButtonDefaults.buttonColors(containerColor = SkyPop, contentColor = Ink),
          ) { Text("Ask the coach") }
        }
        FilledTonalButton(onClick = onBack) { Text("Back home") }
      }
    }
  }
}

@Composable
private fun CoachCard(
    res: FeedbackResponse,
) {
  val t = rememberInfiniteTransition(label = "spark")
  val pulse = t.animateFloat(0.99f, 1.01f, animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse")
  val bg = if (res.tone == "celebrate") CoralHug.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
  val view = LocalView.current
  LaunchedEffect(res.message) {
    if (res.tone == "celebrate") {
      view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }
  }
  Text(
      res.message,
      modifier = Modifier
          .fillMaxWidth()
          .background(bg, shape = MaterialTheme.shapes.extraLarge)
          .padding(12.dp)
          .scale(pulse.value),
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.SemiBold,
  )
}
