package com.aki.anichan.data.response.anilist

data class UserStatisticTypes(
    val anime: UserStatistics = UserStatistics(),
    val manga: UserStatistics = UserStatistics()
)