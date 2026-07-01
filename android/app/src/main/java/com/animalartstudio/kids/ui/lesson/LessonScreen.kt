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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import com.animalartstudio.kids.data.FeedbackResponse
import com.animalartstudio.kids.draw.InkStroke
import com.animalartstudio.kids.draw.StrokesBitmap
import com.animalartstudio.kids.ui.LessonUi
import com.animalartstudio.kids.ui.LessonViewModel
import com.animalartstudio.kids.ui.draw.DoodleCanvas
import com.animalartstudio.kids.ui.draw.blueprintFor
import com.animalartstudio.kids.ui.star.StarShowUi
import com.animalartstudio.kids.ui.star.heroes.AnimalHero
import com.animalartstudio.kids.ui.star.starShowForLesson
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

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
      val warm = starShowForLesson(lessonId, "penguin")
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
              "${warm.buddyName} is fluffing the spotlight…",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(24.dp),
          )
        }
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
      val star = starShowForLesson(b.lesson.id, b.lesson.animalKey)
      var w by remember { mutableFloatStateOf(1f) }
      var hgt by remember { mutableFloatStateOf(1f) }
      val inkOpts = star.inkChoices
      // The animal's ordered "answer key" — the dotted parts the kid traces.
      val blueprint = remember(b.lesson.animalKey) { blueprintFor(b.lesson.animalKey) }
      // These persist across STEPS within a lesson so the drawing accumulates;
      // they reset only when the lesson (lessonId) changes.
      var idx by remember(lessonId) { mutableStateOf(0) }
      var ink by remember(lessonId) { mutableStateOf(inkOpts[0]) }
      var strokes by remember(lessonId) { mutableStateOf(listOf<InkStroke>()) }
      var startOver by remember(lessonId) { mutableStateOf(0) }
      val paperArgb = star.paper.toArgb()
      val scroll = rememberScrollState()
      Column(
          modifier =
              Modifier
                  .fillMaxSize()
                  .background(
                      Brush.verticalGradient(listOf(star.gradientTop, star.gradientBottom)),
                  ),
      ) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(scroll)
                .padding(16.dp),
        ) {
          Text(
              star.showTitle,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onBackground,
          )
          b.lesson.subtitle?.takeIf { it.isNotBlank() }?.let { sub ->
            Spacer(Modifier.height(6.dp))
            Text(sub, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = star.accent.copy(alpha = 1f))
          }
          Spacer(Modifier.height(8.dp))
          Card(
              colors =
                  CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
              shape = RoundedCornerShape(22.dp),
          ) {
            Column(
                Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Text(star.stageLine, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(Modifier.height(4.dp))
              AnimalHero(b.lesson.animalKey, modifier = Modifier.fillMaxWidth())
              Spacer(Modifier.height(8.dp))
              Text(
                  "Part ${b.stepIndex + 1} of ${b.lesson.steps.size} · ${b.step.title}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Black,
              )
              Spacer(Modifier.height(10.dp))
              Text(b.step.instruction, lineHeight = 26.sp, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
              Spacer(Modifier.height(10.dp))
              Text("${star.trickLabel} · ${b.step.technique}", fontWeight = FontWeight.SemiBold, color = star.accent)
            }
          }
          Spacer(Modifier.height(10.dp))
          Row(
              Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically,
          ) {
            inkOpts.forEachIndexed { i, c ->
              FilledTonalButton(
                  onClick = { idx = i; ink = c; view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK) },
                  colors =
                      if (i == idx) {
                        ButtonDefaults.filledTonalButtonColors(containerColor = c.copy(alpha = 0.32f))
                      } else {
                        ButtonDefaults.filledTonalButtonColors()
                      },
                  modifier = Modifier.height(52.dp),
              ) {
                Text("Ink ${i + 1}", fontWeight = FontWeight.Bold)
              }
            }
          }
          Spacer(Modifier.height(12.dp))
          Box(
              Modifier
                  .fillMaxWidth()
                  .height(280.dp)
                  .clip(RoundedCornerShape(20.dp))
                  .background(star.paper),
          ) {
            DoodleCanvas(
                paper = star.paper,
                ink = ink,
                strokeWidth = 9f,
                demoKey = lessonId.hashCode() + b.stepIndex,
                clearKey = lessonId.hashCode() + startOver,
                blueprint = blueprint,
                featureIndex = b.stepIndex,
                onSize = { ww, hh ->
                  w = ww
                  hgt = hh
                },
                onStrokesChange = { strokes = it },
            )
          }
          b.feedback?.let { fb -> CoachBubble(star, fb) }
          if (!b.err.isNullOrBlank()) {
            Text(
                "Tiny tip for ${star.buddyName}: ${b.err}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp),
            )
          }
          Spacer(Modifier.height(8.dp))
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilledTonalButton(onClick = { startOver++; strokes = emptyList() }) { Text("Start over") }
            Button(
                onClick = {
                  view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                  val png = StrokesBitmap.toPngBase64(strokes, w, hgt, paperArgb)
                  vm.submitPng(png, strokeCount = strokes.size) { res ->
                    val bmp = StrokesBitmap.toPreviewBitmap(strokes, w, hgt, 900, 900, paperArgb)
                    vm.rememberForCelebrate(bmp, res.animalKey, b.lesson.id)
                    if (res.bringToLifeUnlocked) {
                      view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                      onCelebrate()
                    } else if (res.magicRequiresMorePractice) {
                      view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    }
                  }
                },
                enabled = !vm.busy && w > 2f,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = star.accent,
                        contentColor = Color.White,
                    ),
            ) { Text(star.askBuddyButton) }
          }
          Spacer(Modifier.height(6.dp))
          FilledTonalButton(onClick = onBack) { Text("Back home") }
        }
      }
    }
  }
}

// CoachBubble extracted to CoachBubble.kt (REVIEW_NOTES C-11).
