package com.aki.anichan.data.localstorage

import com.aki.anichan.data.response.HomeData
import com.aki.anichan.data.response.anilist.MediaTag
import com.aki.anichan.data.response.anilist.User
import com.aki.anichan.data.response.Genre
import com.aki.anichan.data.response.anilist.MediaListCollection
import com.aki.anichan.helper.pojo.SaveItem

interface JsonStorageHandler {
    var homeData: SaveItem<HomeData>?
    var viewerData: SaveItem<User>?
    var genres: SaveItem<List<Genre>>?
    var tags: SaveItem<List<MediaTag>>?
    var animeList: SaveItem<MediaListCollection>?
    var mangaList: SaveItem<MediaListCollection>?
}