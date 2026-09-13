package com.aki.anichan.helper.pojo

import com.aki.anichan.data.response.anilist.Activity
import com.aki.anichan.data.response.anilist.ActivityReply
import com.aki.anichan.data.response.anilist.ListActivity
import com.aki.anichan.data.response.anilist.MediaList

data class MediaSocialItem(
    val activity: ListActivity? = null,
    val mediaList: MediaList? = null,
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_FOLLOWING_MEDIA_LIST_HEADER = 600
        const val VIEW_TYPE_FOLLOWING_MEDIA_LIST = 601
        const val VIEW_TYPE_FOLLOWING_MEDIA_LIST_SEE_MORE = 602
        const val VIEW_TYPE_MEDIA_ACTIVITY_HEADER = 700
        const val VIEW_TYPE_MEDIA_ACTIVITY = 701
        const val VIEW_TYPE_MEDIA_ACTIVITY_SEE_MORE = 702
    }
}
