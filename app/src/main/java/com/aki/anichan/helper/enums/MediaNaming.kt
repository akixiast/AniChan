package com.aki.anichan.helper.enums

import com.aki.anichan.helper.extensions.convertFromSnakeCase

enum class MediaNaming : Naming {
    FOLLOW_ANILIST,
    ENGLISH,
    ROMAJI,
    NATIVE
}

fun MediaNaming.getString(): String {
    return name.convertFromSnakeCase()
}