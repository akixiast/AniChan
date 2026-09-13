package com.aki.anichan.data.repository

import com.aki.anichan.data.response.Anime
import com.aki.anichan.data.response.Manga
import com.aki.anichan.data.response.TrackSearch
import com.aki.anichan.data.response.VideoSearch
import com.aki.anichan.data.response.anilist.*
import com.aki.anichan.data.response.anilist.Character
import com.aki.anichan.data.response.anilist.CharacterEdge
import com.aki.anichan.data.response.anilist.ListActivity
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.MediaList
import com.aki.anichan.data.response.anilist.Page
import com.aki.anichan.data.response.anilist.PageInfo
import com.aki.anichan.data.response.anilist.Staff
import com.aki.anichan.data.response.anilist.StaffEdge
import com.aki.anichan.data.response.anilist.Studio
import com.aki.anichan.data.response.anilist.User
import com.aki.anichan.helper.enums.ListType
import com.aki.anichan.type.*
import io.reactivex.rxjava3.core.Observable

interface BrowseRepository {
    fun getUser(id: Int? = null, name: String? = null, sort: List<UserStatisticsSort> = listOf(UserStatisticsSort.COUNT_DESC)): Observable<User>
    fun getOthersListType(): Observable<ListType>
    fun updateOthersListType(newListType: ListType)
    fun getMedia(id: Int): Observable<Media>
    fun getMediaCharacters(id: Int, page: Int, language: StaffLanguage): Observable<Pair<PageInfo, List<CharacterEdge>>>
    fun getMediaStaff(id: Int, page: Int): Observable<Pair<PageInfo, List<StaffEdge>>>
    fun getMediaFollowingMediaList(id: Int, page: Int): Observable<Page<MediaList>>
    fun getMediaActivity(id: Int, page: Int): Observable<Page<ListActivity>>
    fun getCharacter(id: Int, page: Int, sort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC), type: MediaType? = null, onList: Boolean? = null): Observable<Character>
    fun getStaff(
        id: Int,
        page: Int,
        staffMediaSort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC),
        characterSort: List<CharacterSort> = listOf(CharacterSort.FAVOURITES_DESC),
        characterMediaSort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC),
        onList: Boolean? = null
    ): Observable<Staff>
    fun getStudio(id: Int, page: Int, sort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC), onList: Boolean? = null): Observable<Studio>

    fun getMangaDetails(malId: Int): Observable<Manga>
    fun getAnimeDetails(malId: Int): Observable<Anime>
    fun getYouTubeVideo(searchQuery: String): Observable<VideoSearch>
    fun getSpotifyTrack(searchQuery: String): Observable<TrackSearch>
}