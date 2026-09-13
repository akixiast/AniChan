package com.aki.anichan.data.manager

import android.net.Uri
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.entity.CalendarSetting
import com.aki.anichan.data.entity.MediaFilter
import com.aki.anichan.data.response.anilist.User
import com.aki.anichan.data.entity.ListStyle
import com.aki.anichan.data.response.anilist.MediaListCollection
import com.aki.anichan.helper.pojo.NullableItem
import com.aki.anichan.helper.pojo.SaveItem
import io.reactivex.rxjava3.core.Observable

interface UserManager {
    var bearerToken: String?
    val isAuthenticated: Boolean
    var isLoggedInAsGuest: Boolean

    var animeListStyle: ListStyle
    var mangaListStyle: ListStyle
    var animeFilter: MediaFilter
    var mangaFilter: MediaFilter
    var appSetting: AppSetting
    var calendarSetting: CalendarSetting

    val animeListBackground: Observable<NullableItem<Uri>>
    val mangaListBackground: Observable<NullableItem<Uri>>
    fun saveAnimeListBackground(uri: Uri?): Observable<Unit>
    fun saveMangaListBackground(uri: Uri?): Observable<Unit>

    var viewerData: SaveItem<User>?
    var followingCount: Int?
    var followersCount: Int?
    var animeListEntryCount: Int?
    var mangaListEntryCount: Int?

    var animeList: SaveItem<MediaListCollection>?
    var mangaList: SaveItem<MediaListCollection>?

    var lastNotificationId: Int?

    var lastAnnouncementId: String?
}