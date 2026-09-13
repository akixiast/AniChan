package com.aki.anichan.ui.favorite

import com.aki.anichan.helper.enums.Favorite

data class FavoriteParam(
    val userId: Int,
    val favorite: Favorite
)