package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MediaType(val apiValue: String, val displayName: String) {
    ANIME("ANIME", "Anime"),
    MANGA("MANGA", "Manga")
}

enum class MediaStatus(val apiValue: String, val displayName: String) {
    FINISHED("FINISHED", "Finished"),
    RELEASING("RELEASING", "Releasing"),
    NOT_YET_RELEASED("NOT_YET_RELEASED", "Upcoming"),
    CANCELLED("CANCELLED", "Cancelled"),
    HIATUS("HIATUS", "Hiatus"),
    UNKNOWN("UNKNOWN", "Unknown")
}

enum class UserWatchStatus(val displayName: String) {
    WATCHING("Watching"),
    COMPLETED("Completed"),
    PLANNING("Planning"),
    PAUSED("Paused"),
    DROPPED("Dropped"),
    REWATCHING("Rewatching");

    fun getMangaName(): String = when (this) {
        WATCHING -> "Reading"
        REWATCHING -> "Rereading"
        else -> displayName
    }
}

data class MediaItem(
    val id: Int,
    val idMal: Int? = null,
    val titleRomaji: String = "",
    val titleEnglish: String? = null,
    val titleNative: String? = null,
    val type: MediaType = MediaType.ANIME,
    val format: String = "TV",
    val status: MediaStatus = MediaStatus.UNKNOWN,
    val description: String = "",
    val season: String? = null,
    val seasonYear: Int? = null,
    val episodes: Int? = null,
    val duration: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val coverImageExtraLarge: String = "",
    val coverImageLarge: String = "",
    val coverImageMedium: String = "",
    val bannerImage: String? = null,
    val genres: List<String> = emptyList(),
    val averageScore: Int? = null, // e.g. 85
    val meanScore: Int? = null,
    val popularity: Int = 0,
    val favourites: Int = 0,
    val trending: Int = 0,
    val studios: List<String> = emptyList(),
    val source: String? = null,
    val trailerId: String? = null,
    val trailerSite: String? = null,
    val nextAiringEpisode: Int? = null,
    val nextAiringTimeUntilAiring: Long? = null, // seconds
    val characters: List<CharacterItem> = emptyList(),
    val staff: List<StaffItem> = emptyList(),
    val relations: List<MediaRelation> = emptyList(),
    val recommendations: List<MediaRecommendation> = emptyList(),
    val reviews: List<ReviewItem> = emptyList()
) {
    val displayTitle: String
        get() = if (!titleEnglish.isNullOrBlank()) titleEnglish else titleRomaji.ifBlank { "Untitled" }

    val cleanDescription: String
        get() = description
            .replace("<br>", "\n")
            .replace("<br/>", "\n")
            .replace("<br />", "\n")
            .replace(Regex("<[^>]*>"), "")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .trim()
}

data class CharacterItem(
    val id: Int,
    val nameFull: String,
    val nameNative: String? = null,
    val role: String = "MAIN",
    val imageLarge: String = "",
    val imageMedium: String = "",
    val voiceActorName: String? = null,
    val voiceActorImage: String? = null,
    val voiceActorLanguage: String? = null
)

data class StaffItem(
    val id: Int,
    val nameFull: String,
    val role: String,
    val imageMedium: String = ""
)

data class MediaRelation(
    val id: Int,
    val relationType: String,
    val media: MediaItem
)

data class MediaRecommendation(
    val id: Int,
    val rating: Int = 0,
    val media: MediaItem
)

data class ReviewItem(
    val id: Int,
    val summary: String,
    val score: Int = 0,
    val userName: String = "",
    val userAvatar: String = ""
)

data class AiringScheduleItem(
    val id: Int,
    val episode: Int,
    val airingAt: Long, // timestamp seconds
    val timeUntilAiring: Long, // seconds from now
    val media: MediaItem
)

@Entity(tableName = "user_media_entries")
data class UserMediaEntry(
    @PrimaryKey val mediaId: Int,
    val type: String = "ANIME", // ANIME or MANGA
    val title: String,
    val coverImage: String,
    val bannerImage: String? = null,
    val totalEpisodes: Int? = null,
    val totalChapters: Int? = null,
    val progress: Int = 0,
    val volumesProgress: Int = 0,
    val status: String = "WATCHING", // WATCHING, COMPLETED, PLANNING, PAUSED, DROPPED, REWATCHING
    val score: Float = 0f, // 0.0 to 10.0 or 0 to 100
    val isFavorite: Boolean = false,
    val isAddedLocally: Boolean = true, // true if explicitly added or tracked in-app
    val notes: String = "",
    val repeatCount: Int = 0,
    val format: String = "TV",
    val genresCsv: String = "",
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val watchStatus: UserWatchStatus
        get() = try {
            UserWatchStatus.valueOf(status)
        } catch (e: Exception) {
            UserWatchStatus.WATCHING
        }

    val mediaType: MediaType
        get() = if (type == "MANGA") MediaType.MANGA else MediaType.ANIME
}

data class LibraryStats(
    val totalAnime: Int = 0,
    val totalManga: Int = 0,
    val animeWatching: Int = 0,
    val animeCompleted: Int = 0,
    val animePlanning: Int = 0,
    val totalEpisodesWatched: Int = 0,
    val totalChaptersRead: Int = 0,
    val meanScore: Float = 0f,
    val estimatedDaysWatched: Float = 0f
)

data class SearchResult(
    val items: List<MediaItem>,
    val hasNextPage: Boolean = false,
    val totalCount: Int = 0,
    val currentPage: Int = 1
)

enum class AuthType {
    NONE,
    USERNAME_SYNC,
    TOKEN_AUTH
}

data class AniListUserProfile(
    val id: Int,
    val name: String,
    val avatarUrl: String = "",
    val bannerUrl: String? = null,
    val about: String = "",
    val animeCount: Int = 0,
    val episodesWatched: Int = 0,
    val minutesWatched: Int = 0,
    val animeMeanScore: Float = 0f,
    val mangaCount: Int = 0,
    val chaptersRead: Int = 0,
    val volumesRead: Int = 0,
    val mangaMeanScore: Float = 0f
)

