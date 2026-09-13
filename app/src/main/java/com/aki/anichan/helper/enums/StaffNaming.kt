package com.aki.anichan.helper.enums

import com.aki.anichan.helper.extensions.convertFromSnakeCase

enum class StaffNaming : Naming {
    FOLLOW_ANILIST,
    FIRST_MIDDLE_LAST,
    LAST_MIDDLE_FIRST,
    NATIVE
}

fun StaffNaming.getString(): String {
    return name.convertFromSnakeCase()
}