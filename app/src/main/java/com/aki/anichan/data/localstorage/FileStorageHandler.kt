package com.aki.anichan.data.localstorage

import android.net.Uri
import com.aki.anichan.helper.pojo.NullableItem
import io.reactivex.rxjava3.core.Observable

interface FileStorageHandler {
    val animeListBackground: Observable<NullableItem<Uri>>
    val mangaListBackground: Observable<NullableItem<Uri>>
    fun saveAnimeListBackground(uri: Uri?): Observable<Unit>
    fun saveMangaListBackground(uri: Uri?): Observable<Unit>
}