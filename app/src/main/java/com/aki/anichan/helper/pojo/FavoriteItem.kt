package com.aki.anichan.helper.pojo

import com.aki.anichan.data.response.anilist.Character
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.Staff
import com.aki.anichan.data.response.anilist.Studio
import com.aki.anichan.helper.enums.Favorite

data class FavoriteItem(
    val anime: Media? = null,
    val manga: Media? = null,
    val character: Character? = null,
    val staff: Staff? = null,
    val studio: Studio? = null,
    val favorite: Favorite
)