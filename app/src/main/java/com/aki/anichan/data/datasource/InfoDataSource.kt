package com.aki.anichan.data.datasource

import com.aki.anichan.data.response.github.AnnouncementResponse
import io.reactivex.rxjava3.core.Observable

interface InfoDataSource {
    fun getAnnouncement(): Observable<AnnouncementResponse>
}