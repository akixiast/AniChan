package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

fun buildAniChanColorScheme(
    themeMode: ThemeMode,
    palette: ColorPalette,
    systemIsDark: Boolean
): ColorScheme {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemIsDark
        ThemeMode.DARK, ThemeMode.AMOLED -> true
        ThemeMode.LIGHT -> false
    }
    val isAmoled = themeMode == ThemeMode.AMOLED

    return if (isDark) {
        if (isAmoled) {
            darkColorScheme(
                primary = palette.primaryColor,
                onPrimary = Color(0xFF000000),
                primaryContainer = palette.containerDark,
                onPrimaryContainer = Color(0xFFFFFFFF),
                inversePrimary = palette.primaryColor,

                secondary = palette.secondaryColor,
                onSecondary = Color(0xFF000000),
                secondaryContainer = palette.containerDark,
                onSecondaryContainer = Color(0xFFFFFFFF),

                tertiary = AniPurpleTertiary,
                onTertiary = Color(0xFF000000),
                tertiaryContainer = AniPurpleContainerDark,
                onTertiaryContainer = AniPurpleOnContainerDark,

                background = Color(0xFF000000),
                onBackground = Color(0xFFF3F4F6),

                surface = Color(0xFF0D0F12),
                onSurface = Color(0xFFF9FAFB),
                surfaceVariant = Color(0xFF161A20),
                onSurfaceVariant = Color(0xFF9CA3AF),
                surfaceTint = palette.primaryColor,

                inverseSurface = Color(0xFFF3F4F6),
                inverseOnSurface = Color(0xFF000000),

                outline = Color(0xFF262C36),
                outlineVariant = Color(0xFF181D24),
                scrim = Color(0xFF000000),

                error = DarkError,
                onError = DarkOnError,
                errorContainer = DarkErrorContainer,
                onErrorContainer = DarkOnErrorContainer
            )
        } else {
            // Dark Mode (Deep Navy or Tinted)
            darkColorScheme(
                primary = palette.primaryColor,
                onPrimary = Color(0xFF002238),
                primaryContainer = palette.containerDark,
                onPrimaryContainer = Color(0xFFE0F2FE),
                inversePrimary = palette.primaryColor,

                secondary = palette.secondaryColor,
                onSecondary = Color(0xFF3F0011),
                secondaryContainer = palette.containerDark,
                onSecondaryContainer = Color(0xFFFFE4E6),

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
                surfaceTint = palette.primaryColor,

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
        }
    } else {
        // Light Mode
        lightColorScheme(
            primary = palette.primaryColor,
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = palette.containerLight,
            onPrimaryContainer = Color(0xFF0F172A),
            inversePrimary = palette.primaryColor,

            secondary = palette.secondaryColor,
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = palette.containerLight,
            onSecondaryContainer = Color(0xFF0F172A),

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
            surfaceTint = palette.primaryColor,

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
    }
}

@Composable
fun AniChanTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    colorPalette: ColorPalette = ColorPalette.BLUE,
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemIsDark
        ThemeMode.DARK, ThemeMode.AMOLED -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = buildAniChanColorScheme(
        themeMode = themeMode,
        palette = colorPalette,
        systemIsDark = systemIsDark
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
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
    AniChanTheme(
        themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
        colorPalette = ColorPalette.BLUE,
        content = content
    )
}
