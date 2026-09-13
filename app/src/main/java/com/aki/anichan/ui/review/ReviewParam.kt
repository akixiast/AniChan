package com.aki.anichan.ui.review

import com.aki.anichan.data.response.anilist.Media

data class ReviewParam(
    val media: Media?,
    val userId: Int?
)
