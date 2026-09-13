package com.aki.anichan.data.converter

import com.aki.anichan.data.response.VideoSearch
import com.aki.anichan.data.response.youtube.VideoSearchResponse

fun VideoSearchResponse.convert(): VideoSearch {
    return VideoSearch(
        videoId = items?.firstOrNull()?.id?.videoId ?: ""
    )
}