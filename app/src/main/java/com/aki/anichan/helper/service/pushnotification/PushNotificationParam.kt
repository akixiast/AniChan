package com.aki.anichan.helper.service.pushnotification

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.Notification

data class PushNotificationParam(
    val notifications: List<Notification>,
    val unreadNotificationCount: Int,
    val appSetting: AppSetting,
    val lastNotificationId: Int
)
