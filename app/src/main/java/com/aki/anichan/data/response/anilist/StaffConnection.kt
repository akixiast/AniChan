package com.aki.anichan.data.response.anilist

data class StaffConnection(
    val edges: List<StaffEdge> = listOf(),
    val nodes: List<Staff> = listOf(),
    val pageInfo: PageInfo = PageInfo()
)