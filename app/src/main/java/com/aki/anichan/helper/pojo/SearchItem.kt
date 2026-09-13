package com.aki.anichan.helper.pojo

import com.aki.anichan.data.response.anilist.*
import com.aki.anichan.helper.enums.SearchCategory

data class SearchItem(
    val media: Media = Media(),
    val character: Character = Character(),
    val staff: Staff = Staff(),
    val studio: Studio = Studio(),
    val user: User = User(),
    val searchCategory: SearchCategory = SearchCategory.ANIME
)