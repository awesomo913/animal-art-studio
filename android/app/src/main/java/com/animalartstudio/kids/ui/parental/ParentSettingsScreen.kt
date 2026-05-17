package com.animalartstudio.kids.ui.parental

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.animalartstudio.kids.Graph
import com.animalartstudio.kids.data.ParentSettings
import kotlinx.coroutines.launch

/**
 * REVIEW_NOTES C-8 (lite).
 *
 * Parent-facing settings panel. Gated behind [ParentalGate] in the nav layer.
 * Current toggles: mute sounds, session-length cap, offline-only mode.
 *
 * "offline-only" doesn't enforce anything yet — when the network layer learns
 * to honour it the switch becomes wired. This is intentional so the SettingsScreen
 * captures intent before the plumbing exists (REVIEW_NOTES C-8 covers the wiring).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentSettingsRoute(onBack: () -> Unit) {
  val app = Graph.get()
  val repo = app.parentSettings
  val state by repo.settings.collectAsState(initial = ParentSettings.DEFAULT)
  val scope = rememberCoroutineScope()

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Parent settings", fontWeight = FontWeight.ExtraBold) },
            navigationIcon = {
              IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back to home",
                )
              }
            },
        )
      },
  ) { padding ->
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      Text(
          "These controls don't change Waddles' drawing. They tune the comfort settings around it.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
      )

      ToggleRow(
          label = "Mute all sounds",
          subLabel = "Coach chimes and celebration noises go silent. The animal voice lines still appear on screen.",
          checked = state.muteSounds,
          onCheckedChange = { v -> scope.launch { repo.setMute(v) } },
      )

      ToggleRow(
          label = "Offline-only mode",
          subLabel = "Don't try to reach the art-studio API. Lessons currently need the API — this acts as a hint until that lands.",
          checked = state.offlineOnly,
          onCheckedChange = { v -> scope.launch { repo.setOfflineOnly(v) } },
      )

      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Session length cap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            if (state.maxSessionMinutes == ParentSettings.NO_SESSION_CAP)
              "No cap — the app keeps going until your child quits."
            else "${state.maxSessionMinutes} minutes",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
        )
        Slider(
            value = state.maxSessionMinutes.toFloat(),
            onValueChange = { v -> scope.launch { repo.setMaxSessionMinutes(v.toInt()) } },
            valueRange = 0f..60f,
            steps = 11,
        )
      }
    }
  }
}

@Composable
private fun ToggleRow(
    label: String,
    subLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
  Row(
      Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(Modifier.weight(1f)) {
      Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
      Spacer(Modifier.height(2.dp))
      Text(subLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
    }
    Spacer(Modifier.size(8.dp))
    Box {
      Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
  }
}
