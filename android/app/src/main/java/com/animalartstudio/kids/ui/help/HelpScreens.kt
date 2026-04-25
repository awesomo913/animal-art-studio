package com.animalartstudio.kids.ui.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animalartstudio.kids.Graph
import com.animalartstudio.kids.data.HelpArticleDto
import com.animalartstudio.kids.net.ApiException
import com.animalartstudio.kids.ui.theme.Forest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpRoute(
    onBack: () -> Unit,
) {
  val app = Graph.get()
  var loading by remember { mutableStateOf(true) }
  var items by remember { mutableStateOf<List<HelpArticleDto>>(emptyList()) }
  var err by remember { mutableStateOf<String?>(null) }
  LaunchedEffect(Unit) {
    runCatching { withContext(Dispatchers.IO) { app.api.help() } }
        .onSuccess { items = it; err = null }
        .onFailure { e -> err = (e as? ApiException)?.detail ?: e.message }
    loading = false
  }
  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Help & troubleshoot", fontWeight = FontWeight.ExtraBold) },
        )
      },
  ) { p ->
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(p)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      if (loading) {
        CircularProgressIndicator(color = Forest)
      } else {
        if (err != null) {
          Text("Could not load the help shelf. $err", color = MaterialTheme.colorScheme.error)
        } else {
          for (a in items) {
            Text(a.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(a.body, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
          }
        }
      }
      Spacer(Modifier.height(8.dp))
      FilledTonalButton(onClick = onBack) { Text("Back") }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialRoute(
    onBack: () -> Unit,
) {
  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("How to play", fontWeight = FontWeight.ExtraBold) },
        )
      },
  ) { p ->
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(p)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text("Welcome, tiny artist", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
      Text("Pick an animal, follow the small steps, and draw the shapes with your own wiggle — wonky is wonderful here.")
      Text("When you are ready, tap “Ask the coach.” It does not use scary grades; it nudges you with friendly ideas.")
      Text("If you work through several gentle nudges and finish the last step, you might see a wiggly secret surprise. If not yet, the studio still loves your try!")
      FilledTonalButton(onClick = onBack) { Text("Back") }
    }
  }
}
