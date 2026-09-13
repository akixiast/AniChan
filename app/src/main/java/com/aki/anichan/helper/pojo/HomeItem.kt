package com.aki.anichan.helper.pojo

import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.Review

data class HomeItem(
    val media: List<Media> = listOf(),
    val releasingToday: List<ReleasingTodayItem> = listOf(),
    val review: Review = Review(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_HEADER = 100
        const val VIEW_TYPE_MENU = 101
        const val VIEW_TYPE_RELEASING_TODAY = 200
        const val VIEW_TYPE_TRENDING_ANIME = 300
        const val VIEW_TYPE_TRENDING_MANGA = 301
        const val VIEW_TYPE_POPULAR_ANIME = 302
        const val VIEW_TYPE_POPULAR_MANGA = 303
        const val VIEW_TYPE_TOP_ANIME = 304
        const val VIEW_TYPE_TOP_MANGA = 305
        const val VIEW_TYPE_CURRENT_SEASON = 306
        const val VIEW_TYPE_NEXT_SEASON = 307
        const val VIEW_TYPE_NEW_ANIME = 400
        const val VIEW_TYPE_NEW_MANGA = 401
        const val VIEW_TYPE_FIRST_REVIEW =  500
        const val VIEW_TYPE_REVIEW = 501
        const val VIEW_TYPE_SOCIAL = 600
    }
}