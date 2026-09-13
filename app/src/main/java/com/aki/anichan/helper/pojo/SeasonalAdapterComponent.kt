package com.aki.anichan.helper.pojo

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.helper.enums.ListType

data class SeasonalAdapterComponent(
    val listType: ListType = ListType.LINEAR,
    val appSetting: AppSetting = AppSetting()
)