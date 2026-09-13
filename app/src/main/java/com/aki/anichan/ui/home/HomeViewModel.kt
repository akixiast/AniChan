package com.aki.anichan.ui.home

import com.aki.anichan.R
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.repository.ContentRepository
import com.aki.anichan.data.repository.MediaListRepository
import com.aki.anichan.data.repository.UserRepository
import com.aki.anichan.data.response.anilist.MediaList
import com.aki.anichan.data.response.anilist.User
import com.aki.anichan.helper.enums.MediaType
import com.aki.anichan.helper.enums.SearchCategory
import com.aki.anichan.helper.enums.Source
import com.aki.anichan.helper.extensions.applyScheduler
import com.aki.anichan.helper.extensions.getStringResource
import com.aki.anichan.helper.pojo.HomeAdapterComponent
import com.aki.anichan.helper.pojo.HomeItem
import com.aki.anichan.helper.pojo.ListItem
import com.aki.anichan.helper.pojo.ReleasingTodayItem
import com.aki.anichan.type.MediaListStatus
import com.aki.anichan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlin.math.abs

class HomeViewModel(
    private val contentRepository: ContentRepository,
    private val userRepository: UserRepository,
    private val mediaListRepository: MediaListRepository
) : BaseViewModel<Unit>() {

    private val _homeItemList = BehaviorSubject.createDefault(listOf<HomeItem>())
    val homeItemList: Observable<List<HomeItem>>
        get() = _homeItemList

    private val _adapterComponent = PublishSubject.create<HomeAdapterComponent>()
    val adapterComponent: Observable<HomeAdapterComponent>
        get() = _adapterComponent

    private val _searchCategoryList = PublishSubject.create<List<ListItem<SearchCategory>>>()
    val searchCategoryList: Observable<List<ListItem<SearchCategory>>>
        get() = _searchCategoryList

    private val _exploreCategoryList = PublishSubject.create<List<ListItem<SearchCategory>>>()
    val exploreCategoryList: Observable<List<ListItem<SearchCategory>>>
        get() = _exploreCategoryList

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                userRepository.getAppSetting()
                    .zipWith(userRepository.getViewer(Source.CACHE).onErrorReturnItem(User())) { appSetting, user ->
                        HomeAdapterComponent(user, appSetting)
                    }
                    .applyScheduler()
                    .subscribe(
                        {
                            _adapterComponent.onNext(it)
                            getHomeData()
                        },
                        {
                            // guest mode (no cached viewer): still load home without user header
                            _adapterComponent.onNext(HomeAdapterComponent(null, AppSetting()))
                            getHomeData()
                        }
                    )
            )

            disposables.add(
                mediaListRepository.releasingTodayTrigger
                    .applyScheduler()
                    .subscribe {
                        disposables.add(
                            userRepository.getViewer(Source.CACHE)
                                .flatMap { user ->
                                    mediaListRepository.hasBigList(user, MediaType.ANIME)
                                        .map {
                                            user to it
                                        }
                                }
                                .filter {
                                    if (it.second) {
                                        val currentHomeList = ArrayList(_homeItemList.value ?: listOf())
                                        val index = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_RELEASING_TODAY }
                                        if (index != -1) {
                                            currentHomeList.removeAt(index)
                                        }
                                        _homeItemList.onNext(currentHomeList)
                                    }
                                    !it.second
                                }
                                .map {
                                    it.first
                                }
                                .flatMap {
                                    mediaListRepository.getMediaListCollection(Source.CACHE, it, MediaType.ANIME)
                                }
                                .map {
                                    it.lists.filter { it.status != MediaListStatus.DROPPED }
                                }
                                .map {
                                    val releasingTodayItem = mutableSetOf<ReleasingTodayItem>()

                                    it.forEach {
                                        it.entries.forEach { mediaList ->
                                            val media = mediaList.media
                                            var currentEpisode = 0

                                            if (media.nextAiringEpisode != null) {
                                                if (media.nextAiringEpisode.timeUntilAiring < 3600 * 24) {
                                                    releasingTodayItem.add(ReleasingTodayItem(mediaList, media.nextAiringEpisode.episode, media.nextAiringEpisode.timeUntilAiring))
                                                } else {
                                                    currentEpisode = media.nextAiringEpisode.episode - 1
                                                }
                                            }

                                            if (media.airingSchedule.nodes.isNotEmpty()) {
                                                val currentEpisodeSchedule = media.airingSchedule.nodes.find { it.episode == currentEpisode }
                                                if (currentEpisodeSchedule != null && abs(currentEpisodeSchedule.timeUntilAiring) < 3600 * 24) {
                                                    releasingTodayItem.add(ReleasingTodayItem(mediaList, currentEpisodeSchedule.episode, currentEpisodeSchedule.timeUntilAiring))
                                                }
                                            }
                                        }
                                    }

                                    releasingTodayItem.sortedBy { it.timeUntilAiring }
                                }
                                .subscribe(
                            {
                                val currentHomeList = ArrayList(_homeItemList.value ?: listOf())
                                val releasingTodayIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_RELEASING_TODAY }
                                if (releasingTodayIndex != - 1) {
                                    currentHomeList[releasingTodayIndex] = HomeItem(releasingToday = it, viewType = HomeItem.VIEW_TYPE_RELEASING_TODAY)
                                }
                                _homeItemList.onNext(currentHomeList)
                            },
                            {
                                // guest has no cached list: skip releasing-today silently
                            }
                        )
                        )
                    }
            )
        }
    }

    fun reloadData() {
        // ignore rapid repeated pulls while a load is already in flight
        if (_loading.value == true || state == State.LOADING) return
        getHomeData(true)
    }

    private fun getHomeData(isReloading: Boolean = false) {
        if (!isReloading && state == State.LOADED) return

        if (isReloading)
            _loading.onNext(true)
        else
            _homeItemList.onNext(
                listOf(
                    HomeItem(viewType = HomeItem.VIEW_TYPE_TRENDING_ANIME),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_RELEASING_TODAY),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_TOP_ANIME),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_TOP_MANGA),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_CURRENT_SEASON),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_NEXT_SEASON),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_POPULAR_ANIME),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_NEW_ANIME),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_TRENDING_MANGA),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_POPULAR_MANGA),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_NEW_MANGA)
                )
            )

        requestHomeData(if (isReloading) Source.NETWORK else null)
    }

    private fun requestHomeData(source: Source?) {
        state = State.LOADING

        disposables.add(
            contentRepository.getHomeData(source)
                .applyScheduler()
                .subscribe(
                    {
                        val currentHomeItems = ArrayList(_homeItemList.value ?: listOf())
                        fun setRail(viewType: Int, media: List<com.aki.anichan.data.response.anilist.Media>) {
                            val index = currentHomeItems.indexOfFirst { it.viewType == viewType }
                            if (index != -1) {
                                currentHomeItems[index] = HomeItem(media = media, viewType = viewType)
                            }
                        }
                        setRail(HomeItem.VIEW_TYPE_TRENDING_ANIME, it.trendingAnime)
                        setRail(HomeItem.VIEW_TYPE_TRENDING_MANGA, it.trendingManga)
                        setRail(HomeItem.VIEW_TYPE_POPULAR_ANIME, it.popularAnime)
                        setRail(HomeItem.VIEW_TYPE_POPULAR_MANGA, it.popularManga)
                        setRail(HomeItem.VIEW_TYPE_TOP_ANIME, it.topAnime)
                        setRail(HomeItem.VIEW_TYPE_TOP_MANGA, it.topManga)
                        setRail(HomeItem.VIEW_TYPE_CURRENT_SEASON, it.currentSeasonAnime)
                        setRail(HomeItem.VIEW_TYPE_NEXT_SEASON, it.nextSeasonAnime)
                        setRail(HomeItem.VIEW_TYPE_NEW_ANIME, it.newAnime)
                        setRail(HomeItem.VIEW_TYPE_NEW_MANGA, it.newManga)
                        _homeItemList.onNext(currentHomeItems)
                        _loading.onNext(false)
                        state = State.LOADED
                    },
                    {
                        if (source == Source.CACHE) {
                            _error.onNext(it.getStringResource())
                            _loading.onNext(false)
                            state = State.ERROR
                        } else {
                            requestHomeData(Source.CACHE)
                        }
                    }
                )
        )
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

    fun updateProgress(mediaList: MediaList, newProgress: Int) {
        if (mediaList.progress == newProgress)
            return

        val maxProgress = mediaList.media.episodes

        var targetProgress = newProgress
        var status: MediaListStatus? = null
        var repeat: Int? = null

        if (maxProgress != null && newProgress >= maxProgress) {
            if (mediaList.status == MediaListStatus.REPEATING)
                repeat = mediaList.repeat + 1

            status = MediaListStatus.COMPLETED
            targetProgress = maxProgress
        } else {
            if (mediaList.status == MediaListStatus.PLANNING ||
                mediaList.status == MediaListStatus.PAUSED ||
                mediaList.status == MediaListStatus.DROPPED
            ) {
                status = MediaListStatus.CURRENT
            }
        }
        _loading.onNext(true)

        disposables.add(
            mediaListRepository.updateMediaListProgress(
                MediaType.ANIME,
                mediaList.id ?: 0,
                status,
                repeat,
                targetProgress,
                null
            )
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        // do nothing
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }
}