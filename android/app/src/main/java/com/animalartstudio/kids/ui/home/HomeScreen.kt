package com.animalartstudio.kids.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.animalartstudio.kids.data.LessonSummaryDto
import com.animalartstudio.kids.Graph
import com.animalartstudio.kids.ui.star.starShowForLesson
import com.animalartstudio.kids.ui.theme.CoralHug
import com.animalartstudio.kids.ui.theme.Forest
import com.animalartstudio.kids.ui.theme.SkyPop
import com.animalartstudio.kids.net.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    onOpenLesson: (String) -> Unit,
    onTutorial: () -> Unit,
    onHelp: () -> Unit,
    onParentSettings: () -> Unit,
) {
  val app = Graph.get()
  var loading by remember { mutableStateOf(true) }
  var items by remember { mutableStateOf<List<LessonSummaryDto>>(emptyList()) }
  var err by remember { mutableStateOf<String?>(null) }
  LaunchedEffect(Unit) {
    runCatching {
      withContext(Dispatchers.IO) { app.api.listLessons() }
    }
        .onSuccess {
          items = it
          err = null
          app.log.append("lessons loaded ${it.size}")
        }
        .onFailure { e ->
          err = (e as? ApiException)?.detail ?: e.message
          app.log.append("lessons err ${e.message}")
        }
    loading = false
  }
  val wiggle = remember { Animatable(0f) }
  LaunchedEffect(Unit) {
    while (true) {
      wiggle.animateTo(6f, tween(500, easing = LinearEasing))
      wiggle.animateTo(-6f, tween(500, easing = LinearEasing))
    }
  }
  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Paws & Doodles", fontWeight = FontWeight.ExtraBold) },
            actions = {
              AssistChip(
                  onClick = onTutorial,
                  label = { Text("How to play") },
                  leadingIcon = {
                    Icon(
                        Icons.Rounded.School,
                        contentDescription = "How-to-play tutorial",
                    )
                  },
              )
              IconButton(onClick = onParentSettings) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Parent settings (grown-up check required)",
                )
              }
            },
        )
      },
  ) { p ->
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(p)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
      Text(
          "Gentle animals. Happy lines. A tiny wiggle at the end.",
          style = MaterialTheme.typography.bodyLarge,
      )
      Spacer(Modifier.height(8.dp))
      Text(
          "You have a kind drawing coach, not a grumpy scorekeeper.",
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
      )
      Spacer(Modifier.height(16.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        FilledTonalButton(onClick = onHelp) { Text("Help & troubleshoot") }
        Icon(
            imageVector = Icons.Rounded.Star,
            // C-6: decorative-only icon — null contentDescription so TalkBack skips it.
            contentDescription = null,
            modifier = Modifier
                .rotate(wiggle.value)
                .semantics { contentDescription = "Decorative wiggling star" },
            tint = CoralHug,
        )
      }
      Spacer(Modifier.height(20.dp))
      if (loading) {
        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = Forest)
        }
      } else if (err != null) {
        Text("Could not reach the art studio. ${err}\n(Ask a parent to start the art studio on the computer, or check Wi‑Fi.)", color = MaterialTheme.colorScheme.error)
      } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
          items(items, key = { it.id }) { li ->
            LessonCard(li, onOpenLesson = { onOpenLesson(li.id) })
          }
        }
      }
    }
  }
}

@Composable
private fun LessonCard(
    l: LessonSummaryDto,
    onOpenLesson: () -> Unit,
) {
  val star = starShowForLesson(l.id, l.animalKey)
  val chip =
      when (l.animalKey) {
        "penguin" -> "Waddle" to SkyPop
        "owl" -> "Hoo‑ray" to Forest
        "cat" -> "Purr" to SkyPop
        "dog" -> "Woof" to Forest
        "bunny" -> "Hop" to CoralHug
        "fish" -> "Glub" to SkyPop
        "dino" -> "Stomp" to Forest
        "unicorn" -> "Sparkle" to CoralHug
        else -> "Yay" to SkyPop
      }
  Row(
      modifier =
          Modifier
              .fillMaxWidth()
              .background(
                  color = MaterialTheme.colorScheme.surface,
                  shape = MaterialTheme.shapes.extraLarge,
              )
              .clickable { onOpenLesson() }
              .semantics { contentDescription = "Open lesson ${l.title}, starring ${star.buddyName}, ${l.estMinutes} minutes" }
              .padding(18.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
      Text(l.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
      Text(
          l.subtitle ?: star.homeCardLine,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
      )
      Text(
          "${l.estMinutes} min • starring ${star.buddyName}",
          color = MaterialTheme.colorScheme.tertiary,
          fontWeight = FontWeight.SemiBold,
      )
    }
    Icon(
        Icons.Rounded.EmojiEmotions,
        contentDescription = null,
        modifier = Modifier.padding(4.dp),
        tint = chip.second,
    )
  }
}
