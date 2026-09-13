package com.aki.anichan.helper.pojo

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.User

data class SocialAdapterComponent(
    val viewer: User? = null,
    val appSetting: AppSetting = AppSetting()
)