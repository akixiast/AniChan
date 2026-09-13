package com.aki.anichan.helper.pojo

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.type.MediaSort

data class StaffMediaListAdapterComponent(
    val appSetting: AppSetting = AppSetting(),
    val mediaSort: MediaSort = MediaSort.POPULARITY_DESC
)