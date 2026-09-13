package com.aki.anichan.data.response.anilist

data class MediaConnection(
    var edges: List<MediaEdge> = listOf(),
    val nodes: List<Media> = listOf(),
    val pageInfo: PageInfo = PageInfo()
)