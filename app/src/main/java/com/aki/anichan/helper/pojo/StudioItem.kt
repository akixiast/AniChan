package com.aki.anichan.helper.pojo

import com.aki.anichan.data.response.anilist.Studio

class StudioItem(
    val studio: Studio = Studio(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_MEDIA = 100
    }
}