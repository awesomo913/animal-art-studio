package com.animalartstudio.kids.ui.parental

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * REVIEW_NOTES C-9 (lite).
 *
 * Single-question "which number is bigger" gate. Not a full COPPA flow — that
 * needs counsel + a verified parental consent path (see C-9 in REVIEW_NOTES).
 * This is the lightweight check used before entering parent settings or any
 * future "share / link account" action.
 *
 * Picks two two-digit numbers; correct = bigger. The bias avoids one-off
 * arithmetic (kids can subtract small numbers, but reliably comparing 47 vs 73
 * needs the kind of attention adults have).
 */
@Composable
fun ParentalGate(
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
) {
  // remember a fresh pair so consecutive opens don't allow guessing.
  val pair by remember { mutableStateOf(generatePair()) }
  var failed by remember { mutableStateOf(false) }
  AlertDialog(
      onDismissRequest = onDismiss,
      title = {
        Text(
            "Grown-up check",
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
        )
      },
      text = {
        Text(
            buildString {
              append("Tap the larger number to continue.\n\n")
              if (failed) append("Not quite — try again with a grown-up.")
            },
            style = MaterialTheme.typography.bodyLarge,
        )
      },
      confirmButton = {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          FilledTonalButton(onClick = { onTap(pair, pair.left, onSuccess) { failed = true } }) {
            Text(pair.left.toString(), style = MaterialTheme.typography.titleLarge)
          }
          FilledTonalButton(onClick = { onTap(pair, pair.right, onSuccess) { failed = true } }) {
            Text(pair.right.toString(), style = MaterialTheme.typography.titleLarge)
          }
        }
      },
      dismissButton = {
        TextButton(onClick = onDismiss, modifier = Modifier.padding(end = 4.dp)) {
          Text("Cancel")
        }
      },
  )
}

private data class NumberPair(val left: Int, val right: Int) {
  val bigger = maxOf(left, right)
}

private fun generatePair(): NumberPair {
  // 2-digit numbers ≥ 13 to avoid friendly small numbers.
  val a = Random.nextInt(13, 99)
  var b = Random.nextInt(13, 99)
  while (b == a) b = Random.nextInt(13, 99)
  return NumberPair(a, b)
}

private fun onTap(pair: NumberPair, chosen: Int, onSuccess: () -> Unit, onWrong: () -> Unit) {
  if (chosen == pair.bigger) onSuccess() else onWrong()
}
