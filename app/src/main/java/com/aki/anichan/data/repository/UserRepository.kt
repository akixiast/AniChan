package com.aki.anichan.data.repository

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.entity.CalendarSetting
import com.aki.anichan.helper.enums.AppTheme
import com.aki.anichan.helper.enums.Source
import com.aki.anichan.data.response.NotificationData
import com.aki.anichan.data.response.anilist.Favourites
import com.aki.anichan.data.response.anilist.ListActivityOption
import com.aki.anichan.data.response.anilist.MediaListTypeOptions
import com.aki.anichan.data.response.anilist.NotificationOption
import com.aki.anichan.data.response.anilist.Page
import com.aki.anichan.data.response.anilist.User
import com.aki.anichan.data.response.anilist.UserStatisticTypes
import com.aki.anichan.helper.enums.Favorite
import com.aki.anichan.type.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface UserRepository {

    val refreshFavoriteTrigger: Observable<User>
    val unreadNotificationCount: Observable<Int>

    fun getIsLoggedInAsGuest(): Observable<Boolean>
    fun getIsAuthenticated(): Observable<Boolean>
    fun getViewer(
        source: Source? = null,
        sort: List<UserStatisticsSort> = listOf(UserStatisticsSort.COUNT_DESC)
    ): Observable<User>
    fun loginAsGuest()
    fun logoutAsGuest()
    fun logout()
    fun saveBearerToken(newBearerToken: String?)

    fun getFollowingAndFollowersCount(
        userId: Int,
        source: Source? = null
    ): Observable<Pair<Int, Int>>

    fun getFollowing(userId: Int, page: Int): Observable<Page<User>>
    fun getFollowers(userId: Int, page: Int): Observable<Page<User>>
    fun toggleFollow(userId: Int): Observable<Boolean>

    fun getUserStatistics(userId: Int, sort: UserStatisticsSort): Observable<UserStatisticTypes>
    fun getFavorites(userId: Int, page: Int): Observable<Favourites>
    fun updateFavoriteOrder(ids: List<Int>, favorite: Favorite): Observable<Favourites>
    fun toggleFavorite(
        animeId: Int? = null,
        mangaId: Int? = null,
        characterId: Int? = null,
        staffId: Int? = null,
        studioId: Int? = null
    ): Completable

    fun getAppSetting(): Observable<AppSetting>
    fun setAppSetting(newAppSetting: AppSetting?): Observable<Unit>

    fun getCalendarSetting(): Observable<CalendarSetting>
    fun setCalendarSetting(newCalendarSetting: CalendarSetting): Observable<Unit>

    fun getAppTheme(): AppTheme

    fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        staffNameLanguage: UserStaffNameLanguage,
        activityMergeTime: Int,
        displayAdultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<User>

    fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions,
        disabledListActivity: List<ListActivityOption>
    ): Observable<User>

    fun updateNotificationsSettings(
        notificationOptions: List<NotificationOption>
    ): Observable<User>

    fun getNotifications(
        page: Int,
        typeIn: List<NotificationType>?,
        resetNotificationCount: Boolean
    ): Observable<NotificationData>

    fun getLatestUnreadNotificationCount(): Observable<Int>

    fun clearUnreadNotificationCount()

    fun getLastNotificationId(): Observable<Int>
    fun setLastNotificationId(lastNotificationId: Int)
}