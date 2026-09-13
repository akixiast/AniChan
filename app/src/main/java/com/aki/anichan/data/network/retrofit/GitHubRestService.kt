package com.aki.anichan.data.network.retrofit

import com.aki.anichan.data.response.github.AnnouncementResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface GitHubRestService {

    @GET("docs/json/announcement.json")
    fun getAnnouncement(): Observable<AnnouncementResponse>
}