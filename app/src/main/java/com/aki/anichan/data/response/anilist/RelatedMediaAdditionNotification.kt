package com.aki.anichan.data.response.anilist

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.type.NotificationType

data class RelatedMediaAdditionNotification(
    override val id: Int = 0,
    override val type: NotificationType = NotificationType.RELATED_MEDIA_ADDITION,
    val mediaId: Int = 0,
    val context: String = "",
    override val createdAt: Int = 0,
    val media: Media = Media()
) : Notification {
    override fun getMessage(appSetting: AppSetting): String {
        return "${media.getTitle(appSetting)}${context}"
    }
}