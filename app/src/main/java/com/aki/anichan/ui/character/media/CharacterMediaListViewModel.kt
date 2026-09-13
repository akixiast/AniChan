package com.aki.anichan.ui.character.media

import com.aki.anichan.R
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.repository.BrowseRepository
import com.aki.anichan.data.repository.UserRepository
import com.aki.anichan.data.response.anilist.MediaEdge
import com.aki.anichan.helper.extensions.applyScheduler
import com.aki.anichan.helper.extensions.getStringResource
import com.aki.anichan.helper.pojo.CharacterMediaListAdapterComponent
import com.aki.anichan.helper.pojo.ListItem
import com.aki.anichan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.aki.anichan.type.MediaSort
import com.aki.anichan.type.MediaType

class CharacterMediaListViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel<CharacterMediaListParam>() {

    private val _adapterComponent = PublishSubject.create<CharacterMediaListAdapterComponent>()
    val adapterComponent: Observable<CharacterMediaListAdapterComponent>
        get() = _adapterComponent

    private val _media = BehaviorSubject.createDefault<List<MediaEdge>>(listOf())
    val media: Observable<List<MediaEdge>>
        get() = _media

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private val _mediaSortList = PublishSubject.create<List<ListItem<MediaSort>>>()
    val mediaSortList: Observable<List<ListItem<MediaSort>>>
        get() = _mediaSortList

    private val _mediaTypeList = PublishSubject.create<List<ListItem<MediaType?>>>()
    val mediaTypeList: Observable<List<ListItem<MediaType?>>>
        get() = _mediaTypeList

    private val _showHideOnListList = PublishSubject.create<List<ListItem<Boolean?>>>()
    val showHideOnListList: Observable<List<ListItem<Boolean?>>>
        get() = _showHideOnListList

    private var appSetting = AppSetting()

    private var characterId = 0
    private var mediaSort = MediaSort.POPULARITY_DESC
    private var mediaType: MediaType? = null
    private var onList: Boolean? = null

    private var hasNextPage = false
    private var currentPage = 0

    override fun loadData(param: CharacterMediaListParam) {
        loadOnce {
            characterId = param.characterId

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        appSetting = it
                        _adapterComponent.onNext(CharacterMediaListAdapterComponent(appSetting, mediaSort))
                        loadMedia()
                    }
            )
        }
    }

    fun reloadData() {
        loadMedia()
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentMedia = ArrayList(_media.value ?: listOf())
            currentMedia.add(null)
            _media.onNext(currentMedia)

            loadMedia(true)
        }
    }

    private fun loadMedia(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        disposables.add(
            browseRepository.getCharacter(characterId, if (isLoadingNextPage) currentPage + 1 else 1, listOf(mediaSort), mediaType, onList)
                .applyScheduler()
                .doFinally {
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                        _emptyLayoutVisibility.onNext(_media.value.isNullOrEmpty())
                    }
                }
                .subscribe(
                    { character ->
                        hasNextPage = character.media.pageInfo.hasNextPage
                        currentPage = character.media.pageInfo.currentPage

                        if (isLoadingNextPage) {
                            val currentMedia = ArrayList(_media.value ?: listOf())
                            currentMedia.remove(null)
                            currentMedia.addAll(character.media.edges)
                            _media.onNext(currentMedia)
                        } else {
                            _media.onNext(character.media.edges)
                        }

                        state = State.LOADED
                    },
                    {
                        if (isLoadingNextPage) {
                            val currentMedia = ArrayList(_media.value ?: listOf())
                            currentMedia.remove(null)
                            _media.onNext(currentMedia)
                        }

                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    fun updateMediaSort(newMediaSort: MediaSort) {
        mediaSort = newMediaSort
        _adapterComponent.onNext(CharacterMediaListAdapterComponent(appSetting, mediaSort))
        reloadData()
    }

    fun loadMediaSorts() {
        val mediaSorts = listOf(
            MediaSort.POPULARITY_DESC,
            MediaSort.START_DATE_DESC,
            MediaSort.START_DATE,
            MediaSort.FAVOURITES_DESC,
            MediaSort.SCORE_DESC
        )

        _mediaSortList.onNext(
            mediaSorts.map {
                ListItem(it.getStringResource(), it)
            }
        )
    }

    fun updateMediaType(newMediaType: MediaType?) {
        mediaType = newMediaType
        reloadData()
    }

    fun loadMediaTypes() {
        val mediaTypes = listOf(
            null to R.string.all,
            MediaType.ANIME to R.string.anime,
            MediaType.MANGA to R.string.manga
        )

        _mediaTypeList.onNext(
            mediaTypes.map {
                ListItem(it.second, it.first)
            }
        )
    }

    fun updateShowHideOnList(newShowHideOnList: Boolean?) {
        onList = newShowHideOnList
        reloadData()
    }

    fun loadShowHideOnLists() {
        val showHideOnLists = listOf(
            null to R.string.show_all,
            true to R.string.only_show_series_on_my_list,
            false to R.string.hide_series_on_my_list
        )

        _showHideOnListList.onNext(
            showHideOnLists.map {
                ListItem(it.second, it.first)
            }
        )
    }
}