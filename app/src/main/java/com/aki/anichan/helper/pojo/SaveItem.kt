package com.aki.anichan.helper.pojo

import com.aki.anichan.helper.utils.TimeUtil

class SaveItem<T>(
    val data: T,
    var saveTime: Long = TimeUtil.getCurrentTimeInMillis()
) {
}