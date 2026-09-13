package com.aki.anichan.ui.main

import com.aki.anichan.helper.enums.MediaType
import com.aki.anichan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class SharedMainViewModel : BaseViewModel<Unit>() {

    private val _scrollHomeToTop = PublishSubject.create<Unit>()
    private val _scrollDiscoverToTop = PublishSubject.create<Unit>()
    private val _scrollLibraryToTop = PublishSubject.create<Unit>()
    private val _scrollNotificationsToTop = PublishSubject.create<Unit>()
    private val _scrollProfileToTop = PublishSubject.create<Unit>()

    private val scrollEvents = linkedMapOf(
        Page.DISCOVER to _scrollDiscoverToTop,
        Page.HOME to _scrollHomeToTop,
        Page.LIBRARY to _scrollLibraryToTop,
        Page.NOTIFICATIONS to _scrollNotificationsToTop,
        Page.PROFILE to _scrollProfileToTop
    )

    private val _bottomSheetNavigation = PublishSubject.create<Int>()
    val bottomSheetNavigation: Observable<Int>
        get() = _bottomSheetNavigation

    override fun loadData(param: Unit) = Unit

    fun scrollToTop(pageIndex: Int) {
        val page = Page.values().getOrNull(pageIndex) ?: return
        scrollEvents[page]?.onNext(Unit)
    }

    fun getScrollToTopObservable(page: Page): Observable<Unit> {
        return scrollEvents[page] ?: _scrollHomeToTop
    }

    fun getPageFromMediaType(mediaType: MediaType): Page {
        return Page.LIBRARY
    }

    fun navigateTo(page: Page) {
        _bottomSheetNavigation.onNext(Page.values().indexOfFirst { it == page })
    }

    enum class Page {
        DISCOVER,
        HOME,
        LIBRARY,
        PROFILE,
        NOTIFICATIONS
    }
}