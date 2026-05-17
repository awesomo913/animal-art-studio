package com.animalartstudio.kids.ui.lesson

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.animalartstudio.kids.data.FeedbackResponse
import com.animalartstudio.kids.ui.star.StarShowUi

/**
 * REVIEW_NOTES C-11: extracted from [LessonRoute] so its haptic + pulse animation
 * can be tested in isolation and reused on the celebrate screen later.
 *
 * Also C-6: announces the coach message + tone for TalkBack via semantics.
 */
@Composable
fun CoachBubble(
    star: StarShowUi,
    res: FeedbackResponse,
    modifier: Modifier = Modifier,
) {
  val t = rememberInfiniteTransition(label = "bubble")
  val pulse =
      t.animateFloat(
          0.99f,
          1.01f,
          animationSpec =
              infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "pulse",
      )
  val bg =
      if (res.tone == "celebrate") {
        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
      } else {
        MaterialTheme.colorScheme.surface
      }
  val view = LocalView.current
  LaunchedEffect(res.message, res.tone) {
    if (res.tone == "celebrate") {
      view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }
  }
  val a11yPrefix = if (res.tone == "celebrate") "Celebration from coach" else "Hint from coach"
  Column(
      modifier
          .padding(top = 10.dp)
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(bg)
          .padding(14.dp)
          .scale(pulse.value)
          .semantics { contentDescription = "$a11yPrefix: ${res.message}" },
  ) {
    Text(
        "${star.coachBubblePrefix} 💬",
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
    )
    Spacer(Modifier.height(4.dp))
    Text(res.message, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
  }
}
