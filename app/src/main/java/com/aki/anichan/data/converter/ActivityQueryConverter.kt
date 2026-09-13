package com.aki.anichan.data.converter

import com.aki.anichan.ActivityQuery
import com.aki.anichan.data.response.anilist.Activity
import com.aki.anichan.data.response.anilist.ListActivity
import com.aki.anichan.data.response.anilist.MessageActivity
import com.aki.anichan.data.response.anilist.TextActivity

fun ActivityQuery.Data.convert(): Activity {
    return when (Activity?.__typename) {
        "TextActivity" -> {
            Activity?.onTextActivity?.convert() ?: TextActivity()
        }
        "ListActivity" -> {
            Activity?.onListActivity?.convert() ?: ListActivity()
        }
        "MessageActivity" -> {
            Activity?.onMessageActivity?.convert() ?: MessageActivity()
        }
        else -> TextActivity()
    }
}