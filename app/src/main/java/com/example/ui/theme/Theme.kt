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

private val LightColorScheme = lightColorScheme(
    primary = MinimalTeal,
    onPrimary = MinimalOnTeal,
    primaryContainer = MinimalTealContainer,
    onPrimaryContainer = MinimalOnTealContainer,
    secondary = MinimalTeal,
    onSecondary = MinimalOnTeal,
    secondaryContainer = MinimalTealContainer,
    onSecondaryContainer = MinimalOnTealContainer,
    background = MinimalBackground,
    onBackground = MinimalOnBackground,
    surface = MinimalSurface,
    onSurface = MinimalOnSurface,
    surfaceVariant = MinimalSurfaceVariant,
    onSurfaceVariant = MinimalOnSurfaceVariant,
    outline = MinimalOutline,
    outlineVariant = MinimalOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkTeal,
    onPrimary = MinimalOnTeal,
    primaryContainer = MinimalTeal,
    onPrimaryContainer = MinimalOnTealContainer,
    secondary = DarkTeal,
    onSecondary = MinimalOnTeal,
    secondaryContainer = MinimalTeal,
    onSecondaryContainer = MinimalOnTealContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkSurfaceVariant,
    outlineVariant = DarkSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep Clean Minimalism theme consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
