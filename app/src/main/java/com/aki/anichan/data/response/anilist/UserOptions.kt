package com.aki.anichan.data.response.anilist

import com.aki.anichan.type.UserStaffNameLanguage
import com.aki.anichan.type.UserTitleLanguage


data class UserOptions(
    var titleLanguage: UserTitleLanguage? = null,
    var displayAdultContent: Boolean = false,
    var airingNotifications: Boolean = false,
    val notificationOptions: List<NotificationOption> = listOf(),
    val timezone: String? = null,
    var activityMergeTime: Int = 0,
    var staffNameLanguage: UserStaffNameLanguage? = null,
    var restrictMessagesToFollowing: Boolean = false,
    var disabledListActivity: List<ListActivityOption> = listOf()

)