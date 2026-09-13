package com.aki.anichan.data.response.anilist

data class Page<T>(
    val pageInfo: PageInfo = PageInfo(),
    val data: List<T> = listOf()
)