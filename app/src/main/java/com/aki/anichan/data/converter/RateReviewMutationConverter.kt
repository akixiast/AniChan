package com.aki.anichan.data.converter

import com.aki.anichan.RateReviewMutation
import com.aki.anichan.data.response.anilist.Review

fun RateReviewMutation.Data.convert(): Review {
    return Review(
        id = RateReview?.id ?: 0,
        rating = RateReview?.rating ?: 0,
        ratingAmount = RateReview?.ratingAmount ?: 0,
        userRating = RateReview?.userRating
    )
}