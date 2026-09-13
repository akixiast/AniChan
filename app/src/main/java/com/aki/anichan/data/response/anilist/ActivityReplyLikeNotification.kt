package com.aki.anichan.data.response.anilist

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.type.NotificationType

data class ActivityReplyLikeNotification(
    override val id: Int = 0,
    val userId: Int = 0,
    override val type: NotificationType = NotificationType.ACTIVITY_REPLY_LIKE,
    val activityId: Int = 0,
    val context: String = "",
    override val createdAt: Int = 0,
    val activity: Activity? = null,
    val user: User = User()
) : Notification {
    override fun getMessage(appSetting: AppSetting): String {
        return "${user.name}${context}"
    }
}