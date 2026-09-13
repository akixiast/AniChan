package com.aki.anichan.data.response

import com.aki.anichan.data.response.anilist.Activity

data class SocialData(
    val friendsActivities: List<Activity> = listOf(),
    val globalActivities: List<Activity> = listOf()
)