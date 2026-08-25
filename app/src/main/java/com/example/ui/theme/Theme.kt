package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Custom Material 3 Dark ColorScheme tailored for AniChan's signature
 * deep navy aesthetic (#0B1622) with electric sky blue and anime coral accents.
 */
val AniChanDarkColorScheme: ColorScheme = darkColorScheme(
    primary = AniBluePrimary,
    onPrimary = Color(0xFF003350),
    primaryContainer = AniBlueContainerDark,
    onPrimaryContainer = AniBlueOnContainerDark,
    inversePrimary = Color(0xFF006497),

    secondary = AniCoralSecondary,
    onSecondary = Color(0xFF5F0019),
    secondaryContainer = AniCoralContainerDark,
    onSecondaryContainer = AniCoralOnContainerDark,

    tertiary = AniPurpleTertiary,
    onTertiary = Color(0xFF450073),
    tertiaryContainer = AniPurpleContainerDark,
    onTertiaryContainer = AniPurpleOnContainerDark,

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceTint = DarkSurfaceTint,

    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,

    outline = DarkCardBorder,
    outlineVariant = DarkOutlineVariant,
    scrim = Color(0xFF000000),

    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer
)

/**
 * Custom Material 3 Light ColorScheme featuring a clean, high-contrast cool slate
 * canvas with vibrant anime brand accents.
 */
val AniChanLightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = AniBlueContainerLight,
    onPrimaryContainer = AniBlueOnContainerLight,
    inversePrimary = AniBluePrimary,

    secondary = AniCoralDark,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = AniCoralContainerLight,
    onSecondaryContainer = AniCoralOnContainerLight,

    tertiary = AniPurpleDark,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = AniPurpleContainerLight,
    onTertiaryContainer = AniPurpleOnContainerLight,

    background = LightBackground,
    onBackground = LightOnBackground,

    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceTint = LightSurfaceTint,

    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,

    outline = LightCardBorder,
    outlineVariant = LightOutlineVariant,
    scrim = Color(0xFF000000),

    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer
)

/**
 * Extended custom branding colors for Anime Score Badges & Watch Status tags.
 */
@Immutable
data class ExtendedAniColors(
    val scoreGreen: Color = ScoreGreen,
    val scoreYellow: Color = ScoreYellow,
    val scoreOrange: Color = ScoreOrange,
    val scoreRed: Color = ScoreRed,
    val statusWatching: Color = StatusWatching,
    val statusCompleted: Color = StatusCompleted,
    val statusPlanning: Color = StatusPlanning,
    val statusPaused: Color = StatusPaused,
    val statusDropped: Color = StatusDropped,
    val statusRewatching: Color = StatusRewatching
)

val LocalExtendedAniColors = staticCompositionLocalOf { ExtendedAniColors() }

@Composable
fun AniChanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve AniChan's custom signature branding by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AniChanDarkColorScheme
        else -> AniChanLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalExtendedAniColors provides ExtendedAniColors()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Template compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AniChanTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
