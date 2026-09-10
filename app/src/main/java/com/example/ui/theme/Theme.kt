package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Indigo400,
    onPrimary = Slate50,
    secondary = Indigo600,
    background = Slate900,
    surface = Slate900,
    surfaceVariant = GlassWhite5,
    onSurface = Slate50,
    onBackground = Slate50,
    error = Red400,
    errorContainer = Red500_20,
    primaryContainer = GlassWhite10,
    onPrimaryContainer = Slate50,
  )

private val LightColorScheme = DarkColorScheme // Force dark theme for this specific glassmorphism effect

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme
  dynamicColor: Boolean = false, // Disable dynamic color to enforce our frosted glass colors
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
