package com.aki.anichan.ui.character

import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.Staff

interface CharacterListener {
    fun toggleShowMore(shouldShowMore: Boolean)
    fun navigateToStaff(staff: Staff)
    fun showStaffMedia(staff: Staff)
    fun navigateToCharacterMedia()

    val characterMediaListener: CharacterMediaListener

    interface CharacterMediaListener {
        fun navigateToMedia(media: Media)
    }
}