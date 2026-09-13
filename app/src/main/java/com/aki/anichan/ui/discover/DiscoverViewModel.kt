package com.aki.anichan.ui.discover

import com.aki.anichan.R
import com.aki.anichan.helper.enums.SearchCategory
import com.aki.anichan.helper.pojo.ListItem
import com.aki.anichan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class DiscoverViewModel : BaseViewModel<Unit>() {

    private val _searchCategoryList = PublishSubject.create<List<ListItem<SearchCategory>>>()
    val searchCategoryList: Observable<List<ListItem<SearchCategory>>>
        get() = _searchCategoryList

    private val _exploreCategoryList = PublishSubject.create<List<ListItem<SearchCategory>>>()
    val exploreCategoryList: Observable<List<ListItem<SearchCategory>>>
        get() = _exploreCategoryList

    override fun loadData(param: Unit) {
        state = State.LOADED
    }

    fun loadSearchCategories() {
        val list = ArrayList<ListItem<SearchCategory>>()
        list.add(ListItem(R.string.search_anime, SearchCategory.ANIME))
        list.add(ListItem(R.string.search_manga, SearchCategory.MANGA))
        list.add(ListItem(R.string.search_characters, SearchCategory.CHARACTER))
        list.add(ListItem(R.string.search_staff, SearchCategory.STAFF))
        list.add(ListItem(R.string.search_studios, SearchCategory.STUDIO))
        list.add(ListItem(R.string.search_users, SearchCategory.USER))
        _searchCategoryList.onNext(list)
    }

    fun loadExploreCategories() {
        val list = ArrayList<ListItem<SearchCategory>>()
        list.add(ListItem(R.string.explore_anime, SearchCategory.ANIME))
        list.add(ListItem(R.string.explore_manga, SearchCategory.MANGA))
        list.add(ListItem(R.string.explore_characters, SearchCategory.CHARACTER))
        list.add(ListItem(R.string.explore_staff, SearchCategory.STAFF))
        list.add(ListItem(R.string.explore_studios, SearchCategory.STUDIO))
        _exploreCategoryList.onNext(list)
    }
}
