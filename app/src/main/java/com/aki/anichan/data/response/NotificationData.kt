package com.aki.anichan.data.response

import com.aki.anichan.data.response.anilist.Notification
import com.aki.anichan.data.response.anilist.Page

data class NotificationData(
    val page: Page<Notification> = Page()
)