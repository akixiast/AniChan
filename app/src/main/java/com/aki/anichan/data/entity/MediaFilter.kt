package com.aki.anichan.data.entity

import com.aki.anichan.data.response.anilist.MediaTag
import com.aki.anichan.helper.enums.Country
import com.aki.anichan.helper.enums.OtherLink
import com.aki.anichan.helper.enums.Sort
import com.aki.anichan.type.MediaFormat
import com.aki.anichan.type.MediaSeason
import com.aki.anichan.type.MediaSource
import com.aki.anichan.type.MediaStatus
import com.aki.anichan.type.UserTitleLanguage

data class MediaFilter(
    var persistFilter: Boolean = false,
    var sort: Sort = Sort.FOLLOW_LIST_SETTINGS,
    var titleLanguage: UserTitleLanguage = UserTitleLanguage.ROMAJI,
    var orderByDescending: Boolean = true,
    var mediaFormats: List<MediaFormat> = listOf(),
    var mediaStatuses: List<MediaStatus> = listOf(),
    var mediaSources: List<MediaSource> = listOf(),
    var countries: List<Country> = listOf(),
    var mediaSeasons: List<MediaSeason> = listOf(),
    var seasonYear: Int? = null,
    var minYear: Int? = null,
    var maxYear: Int? = null,
    var minEpisodes: Int? = null,
    var maxEpisodes: Int? = null,
    var minDuration: Int? = null,
    var maxDuration: Int? = null,
    var minAverageScore: Int? = null,
    var maxAverageScore: Int? = null,
    var minPopularity: Int? = null,
    var maxPopularity: Int? = null,
    var streamingOn: List<OtherLink> = listOf(),
    var includedGenres: List<String> = listOf(),
    var excludedGenres: List<String> = listOf(),
    var includedTags: List<MediaTag> = listOf(),
    var excludedTags: List<MediaTag> = listOf(),
    var minTagPercentage: Int = DEFAULT_MINIMUM_TAG_PERCENTAGE,
    var minUserScore: Int? = null,
    var maxUserScore: Int? = null,
    var minUserStartYear: Int? = null,
    var maxUserStartYear: Int? = null,
    var minUserCompletedYear: Int? = null,
    var maxUserCompletedYear: Int? = null,
    var minUserPriority: Int? = null,
    var maxUserPriority: Int? = null,
    var isDoujin: Boolean? = null,
    var onList: Boolean? = null
) {
    companion object {
        const val DEFAULT_MINIMUM_TAG_PERCENTAGE = 18
    }
}