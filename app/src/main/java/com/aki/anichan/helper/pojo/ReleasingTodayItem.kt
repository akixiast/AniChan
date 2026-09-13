package com.aki.anichan.helper.pojo

import com.aki.anichan.data.response.anilist.MediaList

data class ReleasingTodayItem(
    val mediaList: MediaList,
    val episode: Int,
    val timeUntilAiring: Int
)