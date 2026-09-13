package com.aki.anichan.data.response.anilist

import com.aki.anichan.type.MediaListStatus


data class ListActivityOption(
    val disabled: Boolean = false,
    val type: MediaListStatus? = null
)