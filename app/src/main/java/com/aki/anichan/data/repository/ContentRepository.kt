package com.aki.anichan.data.repository

import com.aki.anichan.data.entity.MediaFilter
import com.aki.anichan.data.response.HomeData
import com.aki.anichan.helper.enums.Source
import com.aki.anichan.data.response.Genre
import com.aki.anichan.data.response.anilist.*
import com.aki.anichan.helper.enums.ReviewSort
import com.aki.anichan.helper.enums.Sort
import com.aki.anichan.type.MediaSeason
import com.aki.anichan.type.MediaType
import com.aki.anichan.type.ReviewRating
import com.aki.anichan.type.UserTitleLanguage
import io.reactivex.rxjava3.core.Observable

interface ContentRepository {
    fun getHomeData(source: Source? = null): Observable<HomeData>
    fun getGenres(): Observable<List<Genre>>
    fun getTags(): Observable<List<MediaTag>>
    fun searchMedia(searchQuery: String, type: MediaType, mediaFilter: MediaFilter?, page: Int): Observable<Page<Media>>
    fun searchCharacter(searchQuery: String, page: Int): Observable<Page<Character>>
    fun searchStaff(searchQuery: String, page: Int): Observable<Page<Staff>>
    fun searchStudio(searchQuery: String, page: Int): Observable<Page<Studio>>
    fun searchUser(searchQuery: String, page: Int): Observable<Page<User>>
    fun getSeasonal(page: Int, year: Int, season: MediaSeason, sort: Sort, titleLanguage: UserTitleLanguage, orderByDescending: Boolean, onlyShowOnList: Boolean?, showAdult: Boolean): Observable<Page<Media>>
    fun getAiringSchedule(page: Int, airingAtGreater: Int, airingAtLesser: Int): Observable<Page<AiringSchedule>>
    fun getReviews(mediaId: Int?, userId: Int?, mediaType: MediaType?, sort: ReviewSort, page: Int): Observable<Page<Review>>
    fun rateReview(id: Int, rating: ReviewRating): Observable<Review>
}