package com.aki.anichan.data.converter

import com.aki.anichan.data.response.TrackSearch
import com.aki.anichan.data.response.spotify.TrackSearchResponse

fun TrackSearchResponse.convert(): TrackSearch {
    return TrackSearch(
        trackUrl = tracks?.items?.firstOrNull()?.externalUrls?.spotify ?: ""
    )
}