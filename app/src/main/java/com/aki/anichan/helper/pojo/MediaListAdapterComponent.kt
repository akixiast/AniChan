package com.aki.anichan.helper.pojo

import android.net.Uri
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.entity.ListStyle
import com.aki.anichan.data.response.anilist.MediaListOptions

data class MediaListAdapterComponent(
    var isViewer: Boolean = false,
    var listStyle: ListStyle = ListStyle(),
    var appSetting: AppSetting = AppSetting(),
    var mediaListOptions: MediaListOptions = MediaListOptions(),
    var backgroundUri: Uri? = null
)