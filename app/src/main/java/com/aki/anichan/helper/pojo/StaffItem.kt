package com.aki.anichan.helper.pojo

import com.aki.anichan.data.response.anilist.MediaEdge
import com.aki.anichan.data.response.anilist.Staff

data class StaffItem(
    val staff: Staff = Staff(),
    val media: List<MediaEdge> = listOf(),
    var showFullDescription: Boolean = false,
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_BIO = 100
        const val VIEW_TYPE_CHARACTER = 200
        const val VIEW_TYPE_MEDIA = 300
    }
}