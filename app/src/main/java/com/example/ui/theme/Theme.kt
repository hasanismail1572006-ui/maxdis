package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SoftBlueColorScheme = lightColorScheme(
    primary = SoftBluePrimary,
    onPrimary = SoftBlueOnPrimary,
    primaryContainer = SoftBluePrimaryContainer,
    onPrimaryContainer = SoftBlueOnPrimaryContainer,
    secondary = SoftBlueSecondary,
    onSecondary = SoftBlueOnSecondary,
    secondaryContainer = SoftBlueSecondaryContainer,
    onSecondaryContainer = SoftBlueOnSecondaryContainer,
    tertiary = SoftBlueTertiary,
    onTertiary = SoftBlueOnTertiary,
    background = SoftBlueBackground,
    onBackground = SoftBlueOnBackground,
    surface = SoftBlueSurface,
    onSurface = SoftBlueOnSurface,
    surfaceVariant = SoftBlueSurfaceVariant,
    onSurfaceVariant = SoftBlueOnSurfaceVariant,
    outline = SoftBlueOutline,
    outlineVariant = SoftBlueOutlineVariant,
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Keep consistent Soft Blue theme without dynamic system override
    MaterialTheme(
        colorScheme = SoftBlueColorScheme,
        typography = Typography,
        content = content
    )
}
