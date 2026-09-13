package com.aki.anichan.ui.profile

import com.aki.anichan.data.response.anilist.Character
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.Staff
import com.aki.anichan.data.response.anilist.Studio
import com.aki.anichan.helper.enums.MediaType

interface ProfileListener {

    interface StatsListener {
        fun navigateToStatsDetail()
    }

    interface FavoriteMediaListener {
        fun navigateToFavoriteMedia(mediaType: MediaType)
        fun navigateToMedia(media: Media, mediaType: MediaType)
    }

    interface FavoriteCharacterListener {
        fun navigateToFavoriteCharacter()
        fun navigateToCharacter(character: Character)
    }

    interface FavoriteStaffListener {
        fun navigateToFavoriteStaff()
        fun navigateToStaff(staff: Staff)
    }

    interface FavoriteStudioListener {
        fun navigateToFavoriteStudio()
        fun navigateToStudio(studio: Studio)
    }

    val statsListener: StatsListener
    val favoriteMediaListener: FavoriteMediaListener
    val favoriteCharacterListener: FavoriteCharacterListener
    val favoriteStaffListener: FavoriteStaffListener
    val favoriteStudioListener: FavoriteStudioListener
}