package com.aki.anichan.data.response.anilist

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.type.NotificationType

data class ActivityMessageNotification(
    override val id: Int = 0,
    val userId: Int = 0,
    override val type: NotificationType = NotificationType.ACTIVITY_MESSAGE,
    val activityId: Int = 0,
    val context: String = "",
    override val createdAt: Int = 0,
    val message: MessageActivity? = null,
    val user: User = User()
) : Notification {
    override fun getMessage(appSetting: AppSetting): String {
        return "${user.name}${context}"
    }
}