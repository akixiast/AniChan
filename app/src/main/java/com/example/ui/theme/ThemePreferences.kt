package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val displayName: String) {
    SYSTEM("System Default"),
    DARK("Dark Mode"),
    LIGHT("Light Mode"),
    AMOLED("Pure Black / AMOLED")
}

enum class ColorPalette(
    val displayName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val containerLight: Color,
    val containerDark: Color
) {
    BLUE(
        displayName = "Electric Blue (Default)",
        primaryColor = Color(0xFF3DB4F2),
        secondaryColor = Color(0xFFFF5376),
        containerLight = Color(0xFFD4EFFF),
        containerDark = Color(0xFF00446A)
    ),
    PASTEL_PINK(
        displayName = "Pastel Sakura",
        primaryColor = Color(0xFFF472B6),
        secondaryColor = Color(0xFFFB7185),
        containerLight = Color(0xFFFCE7F3),
        containerDark = Color(0xFF501332)
    ),
    PASTEL_MINT(
        displayName = "Pastel Mint",
        primaryColor = Color(0xFF34D399),
        secondaryColor = Color(0xFF2DD4BF),
        containerLight = Color(0xFFD1FAE5),
        containerDark = Color(0xFF064E3B)
    ),
    PASTEL_LAVENDER(
        displayName = "Pastel Lavender",
        primaryColor = Color(0xFFA78BFA),
        secondaryColor = Color(0xFFC084FC),
        containerLight = Color(0xFFEDE9FE),
        containerDark = Color(0xFF3B1A66)
    ),
    PASTEL_PEACH(
        displayName = "Pastel Peach",
        primaryColor = Color(0xFFFB923C),
        secondaryColor = Color(0xFFF472B6),
        containerLight = Color(0xFFFFEDD5),
        containerDark = Color(0xFF5A2506)
    ),
    PASTEL_CYAN(
        displayName = "Pastel Ice Cyan",
        primaryColor = Color(0xFF38BDF8),
        secondaryColor = Color(0xFF22D3EE),
        containerLight = Color(0xFFE0F2FE),
        containerDark = Color(0xFF0C4A6E)
    ),
    CRIMSON(
        displayName = "Crimson Ruby",
        primaryColor = Color(0xFFE11D48),
        secondaryColor = Color(0xFFF43F5E),
        containerLight = Color(0xFFFFE4E6),
        containerDark = Color(0xFF60081C)
    ),
    MONOCHROME(
        displayName = "Monochrome Slate",
        primaryColor = Color(0xFF94A3B8),
        secondaryColor = Color(0xFF64748B),
        containerLight = Color(0xFFF1F5F9),
        containerDark = Color(0xFF1E293B)
    )
}

data class AppThemeState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorPalette: ColorPalette = ColorPalette.BLUE,
    val titleLanguage: String = "ROMAJI" // "ROMAJI", "ENGLISH", "NATIVE"
)

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("anichan_theme_prefs", Context.MODE_PRIVATE)

    private val _themeState = MutableStateFlow(
        AppThemeState(
            themeMode = loadThemeMode(),
            colorPalette = loadColorPalette(),
            titleLanguage = prefs.getString("title_language", "ROMAJI") ?: "ROMAJI"
        )
    )
    val themeState: StateFlow<AppThemeState> = _themeState.asStateFlow()

    private fun loadThemeMode(): ThemeMode {
        val saved = prefs.getString("theme_mode", ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(saved ?: ThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    private fun loadColorPalette(): ColorPalette {
        val saved = prefs.getString("color_palette", ColorPalette.BLUE.name)
        return try {
            ColorPalette.valueOf(saved ?: ColorPalette.BLUE.name)
        } catch (e: Exception) {
            ColorPalette.BLUE
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeState.value = _themeState.value.copy(themeMode = mode)
    }

    fun setColorPalette(palette: ColorPalette) {
        prefs.edit().putString("color_palette", palette.name).apply()
        _themeState.value = _themeState.value.copy(colorPalette = palette)
    }

    fun setTitleLanguage(lang: String) {
        prefs.edit().putString("title_language", lang).apply()
        _themeState.value = _themeState.value.copy(titleLanguage = lang)
    }

    companion object {
        @Volatile
        private var INSTANCE: ThemePreferences? = null

        fun getInstance(context: Context): ThemePreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = ThemePreferences(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
