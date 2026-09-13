package com.aki.anichan.helper.pojo

import com.aki.anichan.data.entity.AppSetting

data class ReviewAdapterComponent(
    val appSetting: AppSetting = AppSetting(),
    val isMediaReview: Boolean = true,
    val isUserReview: Boolean = true
)
