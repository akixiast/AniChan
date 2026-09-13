package com.aki.anichan.data.response.anilist

import com.aki.anichan.type.CharacterRole


data class CharacterEdge(
    val node: Character = Character(),
    val role: CharacterRole? = null,
    val name: String = "",
    val voiceActorRoles: List<StaffRoleType> = listOf(),
    val media: List<Media> = listOf()
)