package com.aki.anichan.data.converter

import com.aki.anichan.GenreQuery
import com.aki.anichan.data.response.Genre

fun GenreQuery.Data.convert(): List<Genre> {
    return GenreCollection?.mapNotNull { Genre(it ?: "") } ?: listOf()
}