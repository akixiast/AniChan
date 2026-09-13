package com.aki.anichan.data.converter

import com.aki.anichan.MediaActivityQuery
import com.aki.anichan.data.response.anilist.Activity
import com.aki.anichan.data.response.anilist.ListActivity
import com.aki.anichan.data.response.anilist.Page
import com.aki.anichan.data.response.anilist.PageInfo

fun MediaActivityQuery.Data.convert(): Page<ListActivity> {
    return Page(
        pageInfo = PageInfo(
            total = Page?.pageInfo?.total ?: 0,
            perPage = Page?.pageInfo?.perPage ?: 0,
            currentPage = Page?.pageInfo?.currentPage ?: 0,
            lastPage = Page?.pageInfo?.lastPage ?: 0,
            hasNextPage = Page?.pageInfo?.hasNextPage ?: false
        ),
        data = Page?.activities?.filterNotNull()?.map { activity ->
            activity.onListActivity?.convert() ?: ListActivity()
        } ?: listOf()
    )
}