package com.aki.anichan.data.converter

import com.aki.anichan.data.response.Manga
import com.aki.anichan.data.response.MangaSerialization
import com.aki.anichan.data.response.mal.MangaResponse

fun MangaResponse.convert(): Manga {
    return Manga(
        malId = data?.malId ?: 0,
        title = data?.title ?: "",
        serializations = data?.serializations?.map {
            MangaSerialization(
                name = it.name ?: "",
                url = it.url ?: ""
            )
        } ?: listOf()
    )
}