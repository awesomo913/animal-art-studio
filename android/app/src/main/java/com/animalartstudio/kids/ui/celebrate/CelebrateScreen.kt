package com.animalartstudio.kids.ui.celebrate

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.animalartstudio.kids.ui.shared.DrawScratchpad
import com.animalartstudio.kids.ui.theme.Forest
import com.animalartstudio.kids.ui.theme.SkyPop
import androidx.compose.runtime.LaunchedEffect

@Composable
fun CelebrateRoute(
    onDone: () -> Unit,
) {
  val view = LocalView.current
  val bmp = DrawScratchpad.lastDrawing
  val key = DrawScratchpad.animalKey
  val t = rememberInfiniteTransition(label = "b")
  val bounce = t.animateFloat(0f, 18f, animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b")
  val spin = t.animateFloat(-4f, 4f, animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "s")
  LaunchedEffect(Unit) { view.performHapticFeedback(HapticFeedbackConstants.CONFIRM) }
  Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      val title =
          when (key) {
            "penguin" -> "SPLASH! Your penguin wobbles to life"
            "owl" -> "HOOT! A fluffy blink"
            else -> "Hooray! A wiggly little friend"
          }
      Text(
          title,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
      )
      Text(
          "The studio saved a tiny dance just for you. (You can add real animal sound files later in res/raw/.)",
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
      )
      if (bmp != null) {
        Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = "Your finished drawing, dancing gently",
            modifier =
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .scale(1.02f)
                    .rotate(spin.value)
                    .offset(y = bounce.value.dp)
                    .fillMaxWidth(0.82f)
                    .clip(RoundedCornerShape(20.dp)),
        )
      } else {
        Text("We could not find your last drawing, but the party still counts!")
      }
      Text(
          "Pretend sound: wiggle‑waddle‑sparkle! *boop*",
          color = if (key == "penguin") SkyPop else Forest,
          fontWeight = FontWeight.ExtraBold,
      )
      FilledTonalButton(
          onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            DrawScratchpad.clear()
            onDone()
          },
      ) { Text("Draw another day") }
    }
  }
}
