package com.jing.ddys.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.jing.ddys.R


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DdysTheme(content: @Composable () -> Unit) {
    val tvDarkColors = darkColorScheme(
        border = colorResource(id = R.color.cyan400),
        borderVariant = colorResource(id = R.color.rose400)
    )

    val material3ThemeColors = androidx.compose.material3.darkColorScheme(
        primary = tvDarkColors.primary,
        onPrimary = tvDarkColors.onPrimary,
        primaryContainer = tvDarkColors.primaryContainer,
        onPrimaryContainer = tvDarkColors.onPrimaryContainer,
        inversePrimary = tvDarkColors.inversePrimary,
        secondary = tvDarkColors.secondary,
        onSecondary = tvDarkColors.onSecondary,
        secondaryContainer = tvDarkColors.secondaryContainer,
        onSecondaryContainer = tvDarkColors.onSecondaryContainer,
        tertiary = tvDarkColors.tertiary,
        onTertiary = tvDarkColors.onTertiary,
        tertiaryContainer = tvDarkColors.tertiaryContainer,
        onTertiaryContainer = tvDarkColors.onTertiaryContainer,
        background = tvDarkColors.background,
        onBackground = tvDarkColors.onBackground,
        surface = tvDarkColors.surface,
        onSurface = tvDarkColors.onSurface,
        surfaceVariant = tvDarkColors.surfaceVariant,
        onSurfaceVariant = tvDarkColors.onSurfaceVariant,
        surfaceTint = tvDarkColors.surfaceTint,
        inverseSurface = tvDarkColors.inverseSurface,
        inverseOnSurface = tvDarkColors.inverseOnSurface,
        error = tvDarkColors.error,
        onError = tvDarkColors.onError,
        errorContainer = tvDarkColors.errorContainer,
        onErrorContainer = tvDarkColors.onErrorContainer,
        outline = tvDarkColors.border,
        outlineVariant = tvDarkColors.borderVariant,
        scrim = tvDarkColors.scrim
    )
    MaterialTheme(colorScheme = tvDarkColors) {
        androidx.compose.material3.MaterialTheme(
            colorScheme = material3ThemeColors,
            content = content
        )
    }
}