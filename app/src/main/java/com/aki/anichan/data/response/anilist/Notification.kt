package com.aki.anichan.data.response.anilist

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.type.NotificationType

interface Notification {
    val id: Int
    val type: NotificationType
    val createdAt: Int
    fun getMessage(appSetting: AppSetting): String
}