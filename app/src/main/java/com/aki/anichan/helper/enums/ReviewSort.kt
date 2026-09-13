package com.aki.anichan.helper.enums

import android.content.Context
import com.aki.anichan.R

enum class ReviewSort {
    NEWEST,
    OLDEST,
    MOST_UPVOTE,
    FEWEST_UPVOTE
}

fun ReviewSort.getString(context: Context): String {
    return context.getString(getStringResource())
}

fun ReviewSort.getStringResource(): Int {
    return when (this) {
        ReviewSort.NEWEST -> R.string.newest
        ReviewSort.OLDEST -> R.string.oldest
        ReviewSort.MOST_UPVOTE -> R.string.most_upvote
        ReviewSort.FEWEST_UPVOTE -> R.string.fewest_upvote
    }
}

fun ReviewSort.getAniListReviewSort(): com.aki.anichan.type.ReviewSort {
    return when (this) {
        ReviewSort.NEWEST -> com.aki.anichan.type.ReviewSort.CREATED_AT_DESC
        ReviewSort.OLDEST -> com.aki.anichan.type.ReviewSort.CREATED_AT
        ReviewSort.MOST_UPVOTE -> com.aki.anichan.type.ReviewSort.RATING_DESC
        ReviewSort.FEWEST_UPVOTE -> com.aki.anichan.type.ReviewSort.RATING
    }
}