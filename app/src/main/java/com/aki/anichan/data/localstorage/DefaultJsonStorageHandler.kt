package com.aki.anichan.data.localstorage

import android.content.Context
import com.google.gson.Gson
import com.aki.anichan.data.response.Genre
import com.aki.anichan.data.response.HomeData
import com.aki.anichan.data.response.anilist.MediaListCollection
import com.aki.anichan.data.response.anilist.MediaTag
import com.aki.anichan.data.response.anilist.User
import com.aki.anichan.helper.pojo.SaveItem

class DefaultJsonStorageHandler(
    context: Context,
    private val gson: Gson
) : JsonStorageHandler, BaseJsonStorageHandler(context) {

    override var homeData: SaveItem<HomeData>?
        get() = gson.fromJson(getData(HOME_DATA), getType<SaveItem<HomeData>>())
        set(value) { setData(HOME_DATA, gson.toJson(value)) }

    override var viewerData: SaveItem<User>?
        get() = gson.fromJson(getData(VIEWER_DATA), getType<SaveItem<User>>())
        set(value) { setData(VIEWER_DATA, gson.toJson(value)) }

    override var genres: SaveItem<List<Genre>>?
        get() = gson.fromJson(getData(GENRES), getType<SaveItem<List<Genre>>>())
        set(value) { setData(GENRES, gson.toJson(value)) }

    override var tags: SaveItem<List<MediaTag>>?
        get() = gson.fromJson(getData(TAGS), getType<SaveItem<List<MediaTag>>>())
        set(value) { setData(TAGS, gson.toJson(value)) }

    override var animeList: SaveItem<MediaListCollection>?
        get() = gson.fromJson(getData(ANIME_LIST), getType<SaveItem<MediaListCollection>>())
        set(value) { setData(ANIME_LIST, gson.toJson(value)) }

    override var mangaList: SaveItem<MediaListCollection>?
        get() = gson.fromJson(getData(MANGA_LIST), getType<SaveItem<MediaListCollection>>())
        set(value) { setData(MANGA_LIST, gson.toJson(value)) }

    companion object {
        private const val HOME_DATA = "home_data.json"
        private const val VIEWER_DATA = "viewer_data.json"
        private const val GENRES = "genres.json"
        private const val TAGS = "tags.json"
        private const val ANIME_LIST = "anime_list.json"
        private const val MANGA_LIST = "manga_list.json"
    }
}