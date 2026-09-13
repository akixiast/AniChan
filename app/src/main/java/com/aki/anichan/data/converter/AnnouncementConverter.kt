package com.aki.anichan.data.converter

import com.aki.anichan.data.response.Announcement
import com.aki.anichan.data.response.github.AnnouncementResponse

fun AnnouncementResponse.convert(): Announcement {
    return Announcement(
        id = id ?: "",
        fromDate = fromDate ?: "",
        untilDate = untilDate?: "",
        message = message ?: "",
        appVersion = appVersion?.toIntOrNull() ?: 0,
        requiredUpdate = requiredUpdate == "1"
    )
}