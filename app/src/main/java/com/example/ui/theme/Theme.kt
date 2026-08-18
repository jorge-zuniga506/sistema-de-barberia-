package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SophisticatedDarkColorScheme = darkColorScheme(
  primary = SophisticatedPrimary,
  onPrimary = SophisticatedOnPrimary,
  primaryContainer = SophisticatedPrimaryContainer,
  onPrimaryContainer = SophisticatedOnPrimaryContainer,
  secondary = SophisticatedPrimaryHeroLight,
  onSecondary = SophisticatedPrimaryHeroDark,
  secondaryContainer = SophisticatedSurfaceContainer,
  onSecondaryContainer = SophisticatedTextPrimary,
  tertiary = SinpeGreen,
  onTertiary = SophisticatedOnPrimary,
  background = SophisticatedBg,
  onBackground = SophisticatedTextPrimary,
  surface = SophisticatedSurface,
  onSurface = SophisticatedTextPrimary,
  surfaceVariant = SophisticatedSurfaceContainer,
  onSurfaceVariant = SophisticatedTextSecondary,
  outline = SophisticatedBorder,
  error = ErrorRed,
  onError = Color.White
)

private val SophisticatedLightColorScheme = lightColorScheme(
  primary = SophisticatedPrimaryHero,
  onPrimary = Color.White,
  primaryContainer = SophisticatedPrimaryHeroLight,
  onPrimaryContainer = SophisticatedPrimaryHeroDark,
  secondary = SophisticatedPrimaryContainer,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFEADDFF),
  onSecondaryContainer = SophisticatedPrimaryHeroDark,
  tertiary = SinpeGreen,
  onTertiary = Color.White,
  background = SophisticatedBg,
  onBackground = SophisticatedTextPrimary,
  surface = SophisticatedSurface,
  onSurface = SophisticatedTextPrimary,
  surfaceVariant = SophisticatedSurfaceContainer,
  onSurfaceVariant = SophisticatedTextSecondary,
  outline = SophisticatedBorder,
  error = ErrorRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Sophisticated Dark is default
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) SophisticatedDarkColorScheme else SophisticatedLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}


