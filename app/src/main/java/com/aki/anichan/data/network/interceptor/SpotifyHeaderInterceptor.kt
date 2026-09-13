package com.aki.anichan.data.network.interceptor

import android.util.Log
import com.aki.anichan.data.manager.BrowseManager
import okhttp3.Interceptor
import okhttp3.Response

class SpotifyHeaderInterceptor(private val browseManager: BrowseManager) : HeaderInterceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request().newBuilder()
                .addHeader("Authorization", "Bearer ${browseManager.spotifyAccessToken.accessToken}")
                .build()
        )
    }
}