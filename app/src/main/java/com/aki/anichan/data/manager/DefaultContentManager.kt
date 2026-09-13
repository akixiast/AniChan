package com.aki.anichan.data.manager

import com.aki.anichan.data.localstorage.JsonStorageHandler
import com.aki.anichan.data.response.HomeData
import com.aki.anichan.data.response.anilist.MediaTag
import com.aki.anichan.data.response.Genre
import com.aki.anichan.helper.pojo.SaveItem

class DefaultContentManager(private val jsonStorageHandler: JsonStorageHandler) : ContentManager {

    override var homeData: SaveItem<HomeData>?
        get() = jsonStorageHandler.homeData
        set(value) { jsonStorageHandler.homeData = value }

    override var genres: SaveItem<List<Genre>>?
        get() = jsonStorageHandler.genres
        set(value) { jsonStorageHandler.genres = value }

    override var tags: SaveItem<List<MediaTag>>?
        get() = jsonStorageHandler.tags
        set(value) { jsonStorageHandler.tags = value }
}