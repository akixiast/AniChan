package com.aki.anichan.data.repository

import android.net.Uri
import com.aki.anichan.data.entity.ListStyle
import com.aki.anichan.data.entity.MediaFilter
import com.aki.anichan.data.response.anilist.FuzzyDate
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.MediaList
import com.aki.anichan.data.response.anilist.MediaListCollection
import com.aki.anichan.data.response.anilist.User
import com.aki.anichan.helper.enums.MediaType
import com.aki.anichan.helper.enums.Source
import com.aki.anichan.helper.pojo.NullableItem
import com.aki.anichan.type.MediaListStatus
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface MediaListRepository {
    val defaultAnimeList: List<String>
    val defaultAnimeListSplitCompletedSectionByFormat: List<String>
    val defaultMangaList: List<String>
    val defaultMangaListSplitCompletedSectionByFormat: List<String>
    val refreshMediaListTrigger: Observable<Pair<MediaType, MediaList?>>
    val releasingTodayTrigger: Observable<Unit>

    fun getMediaListCollection(source: Source = Source.NETWORK, user: User, mediaType: MediaType): Observable<MediaListCollection>
    fun hasBigList(user: User, mediaType: MediaType) : Observable<Boolean>
    fun updateCacheMediaList(mediaType: MediaType, mediaListCollection: MediaListCollection)
    fun getMediaWithMediaList(mediaId: Int): Observable<Media>
    fun updateMediaListEntry(
        mediaType: MediaType,
        id: Int?,
        mediaId: Int?,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        progressVolumes: Int?,
        repeat: Int,
        priority: Int,
        isPrivate: Boolean,
        notes: String,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?
    ): Observable<MediaList>
    fun deleteMediaListEntry(mediaType: MediaType, id: Int): Completable
    fun updateMediaListScore(mediaType: MediaType, id: Int, score: Double, advancedScores: List<Double>?): Observable<MediaList>
    fun updateMediaListProgress(
        mediaType: MediaType,
        id: Int,
        status: MediaListStatus?,
        repeat: Int?,
        progress: Int?,
        progressVolumes: Int?
    ): Observable<MediaList>
    fun updateMediaListStatus(mediaType: MediaType, mediaId: Int, status: MediaListStatus): Observable<MediaList>

    fun getListStyle(mediaType: MediaType): Observable<ListStyle>
    fun setListStyle(mediaType: MediaType, newListStyle: ListStyle)
    fun getListBackground(mediaType: MediaType): Observable<NullableItem<Uri>>
    fun setListBackground(mediaType: MediaType, newUri: Uri?): Observable<Unit>
    fun getMediaFilter(mediaType: MediaType): Observable<MediaFilter>
    fun setMediaFilter(mediaType: MediaType, newMediaFilter: MediaFilter)
    fun triggerReleasingToday()
}