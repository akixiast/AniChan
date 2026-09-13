package com.aki.anichan.data.response

import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.Review

data class HomeData(
    val trendingAnime: List<Media> = listOf(),
    val trendingManga: List<Media> = listOf(),
    val popularAnime: List<Media> = listOf(),
    val popularManga: List<Media> = listOf(),
    val topAnime: List<Media> = listOf(),
    val topManga: List<Media> = listOf(),
    val currentSeasonAnime: List<Media> = listOf(),
    val nextSeasonAnime: List<Media> = listOf(),
    val newAnime: List<Media> = listOf(),
    val newManga: List<Media> = listOf()
)
