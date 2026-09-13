package com.aki.anichan.data.response.anilist

import com.aki.anichan.type.MediaFormat
import com.aki.anichan.type.MediaRankType
import com.aki.anichan.type.MediaSeason


data class MediaRank(
    val id: Int = 0,
    val rank: Int = 0,
    val type: MediaRankType? = null,
    val format: MediaFormat? = null,
    val year: Int = 0,
    val season: MediaSeason? = null,
    val allTime: Boolean = false,
    val context: String = ""
)