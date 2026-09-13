package com.aki.anichan.data.response.anilist

import com.aki.anichan.type.NotificationType


data class NotificationOption(
    val type: NotificationType? = null,
    var enabled: Boolean = false
)