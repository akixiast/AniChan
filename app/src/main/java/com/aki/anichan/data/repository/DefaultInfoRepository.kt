package com.aki.anichan.data.repository

import com.aki.anichan.data.converter.convert
import com.aki.anichan.data.datasource.InfoDataSource
import com.aki.anichan.data.manager.UserManager
import com.aki.anichan.data.response.Announcement
import io.reactivex.rxjava3.core.Observable

class DefaultInfoRepository(private val infoDataSource: InfoDataSource, private val userManager: UserManager) : InfoRepository {

    override fun getAnnouncement(): Observable<Announcement> {
        return infoDataSource.getAnnouncement().map {
            it.convert()
        }
    }

    override fun getLastAnnouncementId(): Observable<String> {
        return Observable.just(userManager.lastAnnouncementId ?: "")
    }

    override fun setLastAnnouncementId(announcementId: String) {
        userManager.lastAnnouncementId = announcementId
    }
}