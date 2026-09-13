package com.aki.anichan.data.response.anilist

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.type.NotificationType

data class UnknownNotification(
    override val id: Int = 0,
    override val type: NotificationType = NotificationType.UNKNOWN__,
    override val createdAt: Int = 0
) : Notification {
    override fun getMessage(appSetting: AppSetting): String {
        return ""
    }
}
