package com.aki.anichan.data.manager

import com.aki.anichan.data.response.SpotifyAccessToken
import com.aki.anichan.helper.enums.ListType

interface BrowseManager {
    var othersListType: ListType
    val youTubeApiKey: String
    val spotifyApiKey: String
    var spotifyAccessToken: SpotifyAccessToken
    var spotifyAccessTokenLastRetrieve: Long
}