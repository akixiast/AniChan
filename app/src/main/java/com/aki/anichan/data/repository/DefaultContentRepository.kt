package com.aki.anichan.data.repository

import android.util.Log
import com.aki.anichan.data.datasource.ContentDataSource
import com.aki.anichan.data.converter.convert
import com.aki.anichan.data.entity.MediaFilter
import com.aki.anichan.data.manager.ContentManager
import com.aki.anichan.data.response.HomeData
import com.aki.anichan.helper.enums.Source
import com.aki.anichan.helper.extensions.moreThanADay
import com.aki.anichan.data.response.Genre
import com.aki.anichan.data.response.anilist.*
import com.aki.anichan.helper.enums.ReviewSort
import com.aki.anichan.helper.enums.Sort
import com.aki.anichan.helper.pojo.SaveItem
import com.aki.anichan.helper.utils.NotInStorageException
import com.aki.anichan.type.MediaSeason
import com.aki.anichan.type.MediaType
import com.aki.anichan.type.ReviewRating
import com.aki.anichan.type.UserTitleLanguage
import io.reactivex.rxjava3.core.Observable

class DefaultContentRepository(
    private val contentDataSource: ContentDataSource,
    private val contentManager: ContentManager
) : BaseRepository(), ContentRepository {

    override fun getHomeData(source: Source?): Observable<HomeData> {
        return when(source) {
            Source.NETWORK -> getHomeDataFromNetwork()
            Source.CACHE -> getHomeDataFromCache()
            else -> {
                val savedItem = contentManager.homeData
                if (savedItem == null || savedItem.saveTime.moreThanADay()) {
                    getHomeDataFromNetwork()
                } else {
                    Observable.just(savedItem.data)
                }
            }
        }
    }

    private fun getHomeDataFromNetwork(): Observable<HomeData> {
        return contentDataSource.getHomeQuery().map {
            val newHomeData = it.data?.convert() ?: HomeData()
            contentManager.homeData = SaveItem(newHomeData)
            newHomeData
        }
            .doOnError { Log.w(TAG, "getHomeData network failed, will retry once", it) }
            .retry(1)
    }

    private fun getHomeDataFromCache(): Observable<HomeData> {
        val savedItem = contentManager.homeData?.data
        return if (savedItem != null) Observable.just(savedItem) else Observable.error(NotInStorageException())
    }

    override fun getGenres(): Observable<List<Genre>> {
        val savedItem = contentManager.genres
        return if (savedItem == null || savedItem.saveTime.moreThanADay()) {
            contentDataSource.getGenres()
                .map {
                    val newGenres = it.data?.convert() ?: listOf()
                    contentManager.genres = SaveItem(newGenres)
                    newGenres
                }
                .doOnError { Log.w(TAG, "getGenres network failed, will retry once", it) }
                .retry(1)
                .onErrorReturn {
                    savedItem?.data ?: listOf()
                }

        } else {
            Observable.just(savedItem.data)
        }
    }

    override fun getTags(): Observable<List<MediaTag>> {
        val savedItem = contentManager.tags
        return if (savedItem == null || savedItem.saveTime.moreThanADay()) {
            contentDataSource.getTags()
                .map {
                    val newTags = it.data?.convert() ?: listOf()
                    contentManager.tags = SaveItem(newTags)
                    newTags
                }
                .doOnError { Log.w(TAG, "getTags network failed, will retry once", it) }
                .retry(1)
                .onErrorReturn {
                    savedItem?.data ?: listOf()
                }
        } else {
            Observable.just(savedItem.data)
        }
    }

    override fun searchMedia(
        searchQuery: String,
        type: MediaType,
        mediaFilter: MediaFilter?,
        page: Int
    ): Observable<Page<Media>> {
        return contentDataSource.searchMedia(searchQuery, type, mediaFilter, page).map {
            it.data?.convert() ?: Page()
        }
    }

    override fun searchCharacter(searchQuery: String, page: Int): Observable<Page<Character>> {
        return contentDataSource.searchCharacter(searchQuery, page).map {
            it.data?.convert() ?: Page()
        }
    }

    override fun searchStaff(searchQuery: String, page: Int): Observable<Page<Staff>> {
        return contentDataSource.searchStaff(searchQuery, page).map {
            it.data?.convert() ?: Page()
        }
    }

    override fun searchStudio(searchQuery: String, page: Int): Observable<Page<Studio>> {
        return contentDataSource.searchStudio(searchQuery, page).map {
            it.data?.convert() ?: Page()
        }
    }

    override fun searchUser(searchQuery: String, page: Int): Observable<Page<User>> {
        return contentDataSource.searchUser(searchQuery, page).map {
            it.data?.convert() ?: Page()
        }
    }

    override fun getSeasonal(
        page: Int,
        year: Int,
        season: MediaSeason,
        sort: Sort,
        titleLanguage: UserTitleLanguage,
        orderByDescending: Boolean,
        onlyShowOnList: Boolean?,
        showAdult: Boolean
    ): Observable<Page<Media>> {
        return contentDataSource.getSeasonal(page, year, season, sort, titleLanguage, orderByDescending, onlyShowOnList, showAdult).map {
            it.data?.convert() ?: Page()
        }
    }

    override fun getAiringSchedule(
        page: Int,
        airingAtGreater: Int,
        airingAtLesser: Int
    ): Observable<Page<AiringSchedule>> {
        return contentDataSource.getAiringSchedule(page, airingAtGreater, airingAtLesser).map {
            it.data?.convert() ?: Page()
        }
    }

    override fun getReviews(
        mediaId: Int?,
        userId: Int?,
        mediaType: MediaType?,
        sort: ReviewSort,
        page: Int
    ): Observable<Page<Review>> {
        return contentDataSource.getReviews(mediaId, userId, mediaType, sort, page).map {
            it.data?.convert() ?: Page()
        }
    }

    override fun rateReview(id: Int, rating: ReviewRating): Observable<Review> {
        return contentDataSource.rateReview(id, rating).map {
            it.data?.convert() ?: Review()
        }
    }

    companion object {
        private const val TAG = "AniChanNetwork"
    }
}