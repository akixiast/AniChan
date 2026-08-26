package com.example.data.repository

import android.util.Log
import com.example.data.account.AniListAccountManager
import com.example.data.local.UserMediaDao
import com.example.data.model.AiringScheduleItem
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.UserMediaEntry
import com.example.data.remote.AniListApiService
import com.example.data.remote.AniListGraphQL
import com.example.data.remote.OfflineSeedData
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class AniListRepository(
    private val apiService: AniListApiService,
    private val userMediaDao: UserMediaDao,
    private val accountManager: AniListAccountManager? = null
) {

    suspend fun getTrending(type: MediaType = MediaType.ANIME, page: Int = 1, perPage: Int = 20): Result<List<MediaItem>> {
        return try {
            val vars = mapOf(
                "page" to page,
                "perPage" to perPage,
                "type" to type.apiValue
            )
            val json = apiService.executeGraphQL(AniListGraphQL.TRENDING_QUERY, vars)
            val list = AniListGraphQL.parseMediaList(json)
            if (list.isNotEmpty()) {
                Result.success(list)
            } else {
                val seed = if (type == MediaType.ANIME) OfflineSeedData.trendingAnime else OfflineSeedData.trendingManga
                Result.success(seed)
            }
        } catch (e: Exception) {
            Log.w("AniListRepository", "getTrending error: ${e.message}, using seed fallback")
            val seed = if (type == MediaType.ANIME) OfflineSeedData.trendingAnime else OfflineSeedData.trendingManga
            Result.success(seed)
        }
    }

    suspend fun getSeasonal(
        season: String = getCurrentSeason(),
        year: Int = getCurrentYear(),
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<MediaItem>> {
        return try {
            val vars = mapOf(
                "season" to season,
                "seasonYear" to year,
                "page" to page,
                "perPage" to perPage
            )
            val json = apiService.executeGraphQL(AniListGraphQL.SEASONAL_QUERY, vars)
            val list = AniListGraphQL.parseMediaList(json)
            if (list.isNotEmpty()) {
                Result.success(list)
            } else {
                Result.success(OfflineSeedData.trendingAnime)
            }
        } catch (e: Exception) {
            Log.w("AniListRepository", "getSeasonal error: ${e.message}")
            Result.success(OfflineSeedData.trendingAnime)
        }
    }

    suspend fun getPopular(type: MediaType = MediaType.ANIME, page: Int = 1, perPage: Int = 20): Result<List<MediaItem>> {
        return try {
            val vars = mapOf(
                "page" to page,
                "perPage" to perPage,
                "type" to type.apiValue
            )
            val json = apiService.executeGraphQL(AniListGraphQL.POPULAR_QUERY, vars)
            val list = AniListGraphQL.parseMediaList(json)
            if (list.isNotEmpty()) {
                Result.success(list)
            } else {
                val seed = if (type == MediaType.ANIME) OfflineSeedData.trendingAnime else OfflineSeedData.trendingManga
                Result.success(seed)
            }
        } catch (e: Exception) {
            val seed = if (type == MediaType.ANIME) OfflineSeedData.trendingAnime else OfflineSeedData.trendingManga
            Result.success(seed)
        }
    }

    suspend fun searchMedia(
        query: String?,
        type: MediaType = MediaType.ANIME,
        genre: String? = null,
        tag: String? = null,
        season: String? = null,
        seasonYear: Int? = null,
        format: String? = null,
        status: String? = null,
        countryOfOrigin: String? = null,
        sort: String = "POPULARITY_DESC",
        page: Int = 1,
        perPage: Int = 24
    ): Result<List<MediaItem>> {
        val pageResult = searchMediaPage(
            query = query,
            type = type,
            genre = genre,
            tag = tag,
            season = season,
            seasonYear = seasonYear,
            format = format,
            status = status,
            countryOfOrigin = countryOfOrigin,
            sort = sort,
            page = page,
            perPage = perPage
        )
        return pageResult.map { it.items }
    }

    suspend fun searchMediaPage(
        query: String?,
        type: MediaType = MediaType.ANIME,
        genre: String? = null,
        tag: String? = null,
        season: String? = null,
        seasonYear: Int? = null,
        format: String? = null,
        status: String? = null,
        countryOfOrigin: String? = null,
        sort: String = "POPULARITY_DESC",
        page: Int = 1,
        perPage: Int = 24
    ): Result<com.example.data.model.SearchResult> {
        return try {
            val vars = mutableMapOf<String, Any?>(
                "type" to type.apiValue,
                "sort" to listOf(sort),
                "page" to page,
                "perPage" to perPage
            )
            if (!query.isNullOrBlank()) vars["search"] = query.trim()
            if (!genre.isNullOrBlank()) vars["genre"] = genre
            if (!tag.isNullOrBlank()) vars["tag"] = tag
            if (!season.isNullOrBlank()) vars["season"] = season
            if (seasonYear != null && seasonYear > 1940) vars["seasonYear"] = seasonYear
            if (!format.isNullOrBlank()) vars["format"] = format
            if (!status.isNullOrBlank()) vars["status"] = status
            if (!countryOfOrigin.isNullOrBlank()) vars["countryOfOrigin"] = countryOfOrigin

            val json = apiService.executeGraphQL(AniListGraphQL.SEARCH_QUERY, vars)
            val searchResult = AniListGraphQL.parseSearchPage(json)
            Result.success(searchResult)
        } catch (e: Exception) {
            Log.e("AniListRepository", "searchMediaPage error", e)
            val seed = if (type == MediaType.ANIME) OfflineSeedData.trendingAnime else OfflineSeedData.trendingManga
            val filtered = if (!query.isNullOrBlank()) {
                seed.filter { it.displayTitle.contains(query, ignoreCase = true) || it.titleRomaji.contains(query, ignoreCase = true) }
            } else {
                seed
            }
            Result.success(
                com.example.data.model.SearchResult(
                    items = filtered,
                    hasNextPage = false,
                    totalCount = filtered.size,
                    currentPage = page
                )
            )
        }
    }

    suspend fun getMediaDetails(mediaId: Int): Result<MediaItem> {
        return try {
            val vars = mapOf("id" to mediaId)
            val json = apiService.executeGraphQL(AniListGraphQL.DETAILS_QUERY, vars)
            val item = AniListGraphQL.parseSingleMedia(json)
            if (item != null) {
                Result.success(item)
            } else {
                val fallback = OfflineSeedData.trendingAnime.find { it.id == mediaId }
                    ?: OfflineSeedData.trendingManga.find { it.id == mediaId }
                    ?: OfflineSeedData.trendingAnime.first()
                Result.success(fallback)
            }
        } catch (e: Exception) {
            Log.e("AniListRepository", "getMediaDetails error", e)
            val fallback = OfflineSeedData.trendingAnime.find { it.id == mediaId }
                ?: OfflineSeedData.trendingManga.find { it.id == mediaId }
                ?: OfflineSeedData.trendingAnime.first()
            Result.success(fallback)
        }
    }

    suspend fun getAiringSchedule(
        startTimeSeconds: Long,
        endTimeSeconds: Long,
        page: Int = 1,
        perPage: Int = 40
    ): Result<List<AiringScheduleItem>> {
        return try {
            val vars = mapOf(
                "airingAtGreater" to startTimeSeconds.toInt(),
                "airingAtLesser" to endTimeSeconds.toInt(),
                "page" to page,
                "perPage" to perPage
            )
            val json = apiService.executeGraphQL(AniListGraphQL.AIRING_SCHEDULE_QUERY, vars)
            val list = AniListGraphQL.parseAiringSchedule(json)
            Result.success(list)
        } catch (e: Exception) {
            Log.e("AniListRepository", "getAiringSchedule error", e)
            Result.success(emptyList())
        }
    }

    // Room Database Operations & AniList Cloud Sync
    fun getAllUserEntries(): Flow<List<UserMediaEntry>> = userMediaDao.getAllEntries()

    suspend fun getAllUserEntriesList(): List<UserMediaEntry> = userMediaDao.getAllEntriesDirect()

    fun getUserEntry(mediaId: Int): Flow<UserMediaEntry?> = userMediaDao.getEntryFlow(mediaId)

    suspend fun saveUserEntry(entry: UserMediaEntry) {
        val entryToSave = entry.copy(isAddedLocally = true)
        userMediaDao.insertOrUpdate(entryToSave)
        try {
            accountManager?.saveMediaToAniList(
                mediaId = entryToSave.mediaId,
                status = entryToSave.watchStatus,
                progress = entryToSave.progress,
                progressVolumes = entryToSave.volumesProgress,
                score = entryToSave.score,
                notes = entryToSave.notes,
                repeat = entryToSave.repeatCount
            )
        } catch (e: Exception) {
            Log.w("AniListRepository", "Cloud sync save entry failed: ${e.message}")
        }
    }

    suspend fun updateProgress(mediaId: Int, progress: Int) {
        val current = userMediaDao.getEntryById(mediaId)
        if (current != null) {
            val updated = current.copy(
                progress = progress,
                isAddedLocally = true,
                updatedAt = System.currentTimeMillis()
            )
            userMediaDao.insertOrUpdate(updated)
            try {
                accountManager?.saveMediaToAniList(
                    mediaId = mediaId,
                    status = updated.watchStatus,
                    progress = progress,
                    progressVolumes = updated.volumesProgress,
                    score = updated.score,
                    notes = updated.notes,
                    repeat = updated.repeatCount
                )
            } catch (e: Exception) {
                Log.w("AniListRepository", "Cloud sync update progress failed: ${e.message}")
            }
        } else {
            userMediaDao.updateProgress(mediaId, progress)
        }
    }

    suspend fun updateFavorite(mediaId: Int, isFavorite: Boolean) {
        userMediaDao.updateFavorite(mediaId, isFavorite)
    }

    suspend fun deleteUserEntry(mediaId: Int) {
        userMediaDao.deleteById(mediaId)
    }

    companion object {
        fun getCurrentSeason(): String {
            val month = Calendar.getInstance().get(Calendar.MONTH) // 0-11
            return when (month) {
                11, 0, 1 -> "WINTER"
                2, 3, 4 -> "SPRING"
                5, 6, 7 -> "SUMMER"
                else -> "FALL"
            }
        }

        fun getCurrentYear(): Int {
            return Calendar.getInstance().get(Calendar.YEAR)
        }
    }
}
