package com.aki.anichan.helper.pojo

import com.aki.anichan.data.response.anilist.MediaList

data class MediaListItem(
    val mediaList: MediaList = MediaList(),
    val title: String = "",
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_TITLE = 100
        const val VIEW_TYPE_MEDIA_LIST = 200
    }
}