package com.animalartstudio.kids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.animalartstudio.kids.ui.celebrate.CelebrateRoute
import com.animalartstudio.kids.ui.help.HelpRoute
import com.animalartstudio.kids.ui.help.TutorialRoute
import com.animalartstudio.kids.ui.home.HomeRoute
import com.animalartstudio.kids.ui.lesson.LessonRoute
import com.animalartstudio.kids.ui.parental.ParentSettingsRoute
import com.animalartstudio.kids.ui.parental.ParentalGate
import com.animalartstudio.kids.ui.theme.PawsDoodlesTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(s: Bundle?) {
    super.onCreate(s)
    setContent { AppRoot() }
  }
}

@Composable
fun AppRoot() {
  PawsDoodlesTheme {
    val nav = rememberNavController()
    // Parental-gate transient state — when set, AlertDialog is shown over current route.
    var pendingGate by remember { mutableStateOf<(() -> Unit)?>(null) }

    NavHost(navController = nav, startDestination = "home") {
      composable("home") {
        HomeRoute(
            onOpenLesson = { id: String -> nav.navigate("lesson/${id}") },
            onTutorial = { nav.navigate("tutorial") },
            onHelp = { nav.navigate("help") },
            onParentSettings = {
              // C-9: parental gate before entering settings.
              pendingGate = { nav.navigate("parent-settings") }
            },
        )
      }
      composable("tutorial") { TutorialRoute(onBack = { nav.popBackStack() }) }
      composable("help") { HelpRoute(onBack = { nav.popBackStack() }) }
      composable("parent-settings") { ParentSettingsRoute(onBack = { nav.popBackStack() }) }
      composable(
          route = "lesson/{id}",
          arguments = listOf(navArgument("id") { type = NavType.StringType }),
      ) { e ->
        val id = e.arguments?.getString("id").orEmpty()
        LessonRoute(
            lessonId = id,
            onBack = { nav.popBackStack() },
            onCelebrate = { nav.navigate("celebrate") },
        )
      }
      composable("celebrate") {
        CelebrateRoute(
            onDone = {
              nav.popBackStack()
              nav.popBackStack()
            },
        )
      }
    }

    pendingGate?.let { onPass ->
      ParentalGate(
          onSuccess = {
            pendingGate = null
            onPass()
          },
          onDismiss = { pendingGate = null },
      )
    }
  }
}

@Preview
@Composable
fun Preview() {
  PawsDoodlesTheme { HomeRoute({}, {}, {}, {}) }
}
