package com.aki.anichan.ui.explore

import com.aki.anichan.data.entity.MediaFilter
import com.aki.anichan.helper.enums.SearchCategory

data class ExploreParam(
    val searchCategory: SearchCategory,
    val mediaFilter: MediaFilter?
)
