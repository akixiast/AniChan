package com.aki.anichan.data.localstorage

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.entity.CalendarSetting
import com.aki.anichan.data.entity.MediaFilter
import com.aki.anichan.data.entity.ListStyle
import com.aki.anichan.data.response.SpotifyAccessToken
import com.aki.anichan.helper.enums.ListType

interface SharedPreferencesHandler {
    var bearerToken: String?
    var guestLogin: Boolean?
    var animeListStyle: ListStyle?
    var mangaListStyle: ListStyle?
    var animeFilter: MediaFilter?
    var mangaFilter: MediaFilter?
    var appSetting: AppSetting?
    var calendarSetting: CalendarSetting?
    var followingCount: Int?
    var followersCount: Int?
    var animeListEntryCount: Int?
    var mangaListEntryCount: Int?
    var othersListType: ListType?
    var lastNotificationId: Int?
    var lastAnnouncementId: String?
    var spotifyAccessToken: SpotifyAccessToken?
    var spotifyAccessTokenLastRetrieve: Long?
}