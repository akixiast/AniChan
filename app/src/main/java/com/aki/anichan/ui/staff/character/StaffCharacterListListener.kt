package com.aki.anichan.ui.staff.character

import com.aki.anichan.data.response.anilist.Character
import com.aki.anichan.data.response.anilist.Media

interface StaffCharacterListListener {
    fun navigateToCharacter(character: Character)
    fun navigateToMedia(media: Media)
}