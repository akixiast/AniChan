package com.aki.anichan.helper.enums

import com.aki.anichan.R

enum class MediaType {
    ANIME,
    MANGA
}

fun MediaType.getAniListMediaType(): com.aki.anichan.type.MediaType {
    return when (this) {
        MediaType.ANIME -> com.aki.anichan.type.MediaType.ANIME
        MediaType.MANGA -> com.aki.anichan.type.MediaType.MANGA
    }
}

fun MediaType.getStringResource(): Int {
    return when (this) {
        MediaType.ANIME -> R.string.anime
        MediaType.MANGA -> R.string.manga
    }
}