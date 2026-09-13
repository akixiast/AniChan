package com.aki.anichan.data.converter

import com.aki.anichan.data.response.SpotifyAccessToken
import com.aki.anichan.data.response.spotify.SpotifyAccessTokenResponse

fun SpotifyAccessTokenResponse.convert(): SpotifyAccessToken {
    return SpotifyAccessToken(
        accessToken = accessToken ?: "",
        expiresIn = expiresIn ?: 0
    )
}