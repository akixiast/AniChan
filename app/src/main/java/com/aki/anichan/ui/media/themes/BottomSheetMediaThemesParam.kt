package com.aki.anichan.ui.media.themes

import com.aki.anichan.data.response.AnimeTheme
import com.aki.anichan.data.response.AnimeThemeEntry
import com.aki.anichan.data.response.anilist.Media

data class BottomSheetMediaThemesParam(
    val media: Media,
    val animeTheme: AnimeTheme,
    val animeThemeEntry: AnimeThemeEntry?
)