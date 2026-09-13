package com.aki.anichan.data.response.anilist

import com.aki.anichan.type.MediaListStatus


data class StatusDistribution(
    val status: MediaListStatus? = null,
    val amount: Int = 0
)