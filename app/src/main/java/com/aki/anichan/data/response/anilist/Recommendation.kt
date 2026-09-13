package com.aki.anichan.data.response.anilist

import com.aki.anichan.type.RecommendationRating


data class Recommendation(
    val id: Int = 0,
    val rating: Int = 0,
    val userRating: RecommendationRating? = null,
    val mediaRecommendation: Media? = null
)