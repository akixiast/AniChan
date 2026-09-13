package com.aki.anichan.data.network.retrofit

import com.aki.anichan.data.response.github.AnnouncementResponse
import com.aki.anichan.data.response.github.GitHubReleaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface GitHubRestService {

    @GET("docs/json/announcement.json")
    fun getAnnouncement(): Observable<AnnouncementResponse>

    @GET
    fun getLatestRelease(@Url url: String): Observable<GitHubReleaseResponse>
}