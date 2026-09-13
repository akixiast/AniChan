package com.aki.anichan.data.datasource

import com.aki.anichan.data.network.retrofit.RetrofitHandler
import com.aki.anichan.data.response.github.AnnouncementResponse
import io.reactivex.rxjava3.core.Observable

class DefaultInfoDataSource(private val retrofitHandler: RetrofitHandler) : InfoDataSource {

    override fun getAnnouncement(): Observable<AnnouncementResponse> {
        return retrofitHandler.gitHubRetrofitClient().getAnnouncement()
    }
}