package com.aki.anichan.helper.enums

import com.aki.anichan.helper.extensions.convertFromSnakeCase

enum class ListType {
    LINEAR,
    GRID,
    SIMPLIFIED,
    ALBUM
}

fun ListType.getString(): String {
    return name.convertFromSnakeCase()
}