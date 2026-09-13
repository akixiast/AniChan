package com.aki.anichan.ui.activity

import com.aki.anichan.helper.enums.ActivityListPage

data class ActivityListParam(
    val activityListPage: ActivityListPage,
    val userId: Int
)