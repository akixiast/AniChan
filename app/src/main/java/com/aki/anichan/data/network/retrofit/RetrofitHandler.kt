package com.aki.anichan.data.network.retrofit

interface RetrofitHandler {
    fun gitHubRetrofitClient(): GitHubRestService
    fun jikanRetrofitClient(): JikanRestService
    fun animeThemesRetrofitClient(): AnimeThemesRestService
    fun youTubeRetrofitClient(): YouTubeRestService
    fun spotifyAuthRetrofitClient(): SpotifyAuthRestService
    fun spotifyRetrofitClient(): SpotifyRestService
}