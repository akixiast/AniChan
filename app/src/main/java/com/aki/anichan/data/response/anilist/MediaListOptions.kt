package com.aki.anichan.data.response.anilist

import com.aki.anichan.type.ScoreFormat


data class MediaListOptions(
    var scoreFormat: ScoreFormat? = null,
    var rowOrder: String = "",
    val animeList: MediaListTypeOptions = MediaListTypeOptions(),
    val mangaList: MediaListTypeOptions = MediaListTypeOptions()
)