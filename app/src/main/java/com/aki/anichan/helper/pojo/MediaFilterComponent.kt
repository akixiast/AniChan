package com.aki.anichan.helper.pojo

import com.aki.anichan.data.entity.MediaFilter
import com.aki.anichan.helper.enums.MediaType
import com.aki.anichan.type.ScoreFormat

data class MediaFilterComponent(
    val mediaFilter: MediaFilter,
    val mediaType: MediaType,
    val scoreFormat: ScoreFormat,
    val isUserList: Boolean,
    val hasBigList: Boolean,
    val isViewer: Boolean
)