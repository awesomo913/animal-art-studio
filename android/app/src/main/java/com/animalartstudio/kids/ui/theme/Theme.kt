package com.animalartstudio.kids.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Light =
    lightColorScheme(
        primary = Forest,
        onPrimary = WhiteSoft,
        primaryContainer = DoodleCream,
        onPrimaryContainer = Ink,
        secondary = SkyPop,
        onSecondary = Ink,
        tertiary = CoralHug,
        onTertiary = Ink,
        background = DoodleCream,
        onBackground = Ink,
        surface = WhiteSoft,
        onSurface = Ink,
    )

@Composable
fun PawsDoodlesTheme(
    content: @Composable () -> Unit,
) {
  val view = LocalView.current
  SideEffect {
    val w = (view.context as Activity).window
    w.statusBarColor = DoodleCream.toArgb()
    w.navigationBarColor = DoodleCream.toArgb()
    WindowCompat.getInsetsController(w, w.decorView).isAppearanceLightStatusBars = true
  }
  MaterialTheme(
      colorScheme = Light, typography = PawsDoodlesType, content = content)
}
