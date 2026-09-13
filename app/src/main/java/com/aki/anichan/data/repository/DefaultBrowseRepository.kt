package com.aki.anichan.data.repository

import com.aki.anichan.data.converter.convert
import com.aki.anichan.data.datasource.BrowseDataSource
import com.aki.anichan.data.manager.BrowseManager
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
import com.aki.anichan.helper.utils.AnimeThemesException
import com.aki.anichan.helper.utils.TimeUtil
import com.aki.anichan.type.*
import convert
import io.reactivex.rxjava3.core.Observable

class DefaultBrowseRepository(
    private val browseDataSource: BrowseDataSource,
    private val browseManager: BrowseManager
) : BrowseRepository {

    private val userIdToUserMap = HashMap<Int, User>()

    override fun getUser(id: Int?, name: String?, sort: List<UserStatisticsSort>): Observable<User> {
        return if (userIdToUserMap.containsKey(id)) {
            Observable.just(userIdToUserMap[id] ?: User())
        } else {
            browseDataSource.getUserQuery(id, name, sort).map {
                val newUser = it.data?.convert()
                if (newUser != null) {
                    userIdToUserMap[newUser.id] = newUser
                }
                newUser ?: User()
            }
        }
    }

    override fun getOthersListType(): Observable<ListType> {
        return Observable.just(browseManager.othersListType)
    }

    override fun updateOthersListType(newListType: ListType) {
        browseManager.othersListType = newListType
    }

    override fun getMedia(id: Int): Observable<Media> {
        return browseDataSource.getMediaQuery(id).map {
            it.data?.convert() ?: Media()
        }
    }

    override fun getMediaCharacters(
        id: Int,
        page: Int,
        language: StaffLanguage
    ): Observable<Pair<PageInfo, List<CharacterEdge>>> {
        return browseDataSource.getMediaCharactersQuery(id, page, language).map {
            val characterConnection = it.data?.convert() ?: return@map Pair(PageInfo(), listOf())
            characterConnection.pageInfo to characterConnection.edges
        }
    }

    override fun getMediaStaff(id: Int, page: Int): Observable<Pair<PageInfo, List<StaffEdge>>> {
        return browseDataSource.getMediaStaffQuery(id, page).map {
            val staffConnection = it.data?.convert() ?: return@map Pair(PageInfo(), listOf())
            staffConnection.pageInfo to staffConnection.edges
        }
    }

    override fun getMediaFollowingMediaList(id: Int, page: Int): Observable<Page<MediaList>> {
        return browseDataSource.getMediaFollowingMediaListQuery(id, page).map {
            it.data?.convert() ?: Page()
        }
    }

    override fun getMediaActivity(id: Int, page: Int): Observable<Page<ListActivity>> {
        return browseDataSource.getMediaActivityQuery(id, page).map {
            it.data?.convert() ?: Page()
        }
    }

    override fun getCharacter(id: Int, page: Int, sort: List<MediaSort>, type: MediaType?, onList: Boolean?): Observable<Character> {
        return browseDataSource.getCharacterQuery(id, page, sort, type, onList).map {
            it.data?.convert() ?: Character()
        }
    }

    override fun getStaff(
        id: Int,
        page: Int,
        staffMediaSort: List<MediaSort>,
        characterSort: List<CharacterSort>,
        characterMediaSort: List<MediaSort>,
        onList: Boolean?
    ): Observable<Staff> {
        return browseDataSource.getStaffQuery(id, page, staffMediaSort, characterSort, characterMediaSort, onList).map {
            it.data?.convert() ?: Staff()
        }
    }

    override fun getStudio(id: Int, page: Int, sort: List<MediaSort>, onList: Boolean?): Observable<Studio> {
        return browseDataSource.getStudioQuery(id, page, sort, onList).map {
            it.data?.convert() ?: Studio()
        }
    }

    override fun getMangaDetails(malId: Int): Observable<Manga> {
        return browseDataSource.getMangaDetails(malId).map {
            it.convert()
        }
    }

    override fun getAnimeDetails(malId: Int): Observable<Anime> {
        var getFromMal = false
        return Observable.just(true)
            .flatMap {
                if (!getFromMal) {
                    getAnimeDetailsFromAnimeThemes(malId)
                        .doOnError { getFromMal = true }
                        .map {
                            if (it.id == 0)
                                throw AnimeThemesException()
                            else
                                it
                        }
                } else {
                    getFromMal = false
                    getAnimeDetailsFromMal(malId)
                }
            }
            .retry { times, throwable ->
                if (throwable is AnimeThemesException) {
                    getFromMal = true
                    true
                } else {
                    false
                }
            }
    }

    private fun getAnimeDetailsFromAnimeThemes(malId: Int): Observable<Anime> {
        return browseDataSource.getAnimeDetailsFromAnimeThemes(malId).map {
            it.convert()
        }
    }

    private fun getAnimeDetailsFromMal(malId: Int): Observable<Anime> {
        return browseDataSource.getAnimeDetailsFromMal(malId).map {
            it.convert()
        }
    }

    override fun getYouTubeVideo(searchQuery: String): Observable<VideoSearch> {
        return browseDataSource.getYouTubeVideo(browseManager.youTubeApiKey, searchQuery).map {
            it.convert()
        }
    }

    override fun getSpotifyTrack(searchQuery: String): Observable<TrackSearch> {
        return Observable.just(browseManager.spotifyAccessToken)
            .flatMap { accessToken ->
                if (accessToken.accessToken == "" || TimeUtil.getCurrentTimeInMillis() >= browseManager.spotifyAccessTokenLastRetrieve + accessToken.expiresIn.toLong() * 1000 ) {
                    browseDataSource.getSpotifyAccessToken()
                        .map {
                            browseManager.spotifyAccessToken = it.convert()
                            browseManager.spotifyAccessTokenLastRetrieve = TimeUtil.getCurrentTimeInMillis()
                            Unit
                        }
                } else {
                    Observable.just(Unit)
                }
            }
            .flatMap {
                browseDataSource.getSpotifyTrack(searchQuery).map { it.convert() }
            }
    }
}