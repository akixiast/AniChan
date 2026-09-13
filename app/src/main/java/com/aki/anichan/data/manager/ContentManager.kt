package com.aki.anichan.data.manager

import com.aki.anichan.data.response.HomeData
import com.aki.anichan.data.response.anilist.MediaTag
import com.aki.anichan.data.response.Genre
import com.aki.anichan.helper.pojo.SaveItem

interface ContentManager {
    var homeData: SaveItem<HomeData>?
    var genres: SaveItem<List<Genre>>?
    var tags: SaveItem<List<MediaTag>>?
}