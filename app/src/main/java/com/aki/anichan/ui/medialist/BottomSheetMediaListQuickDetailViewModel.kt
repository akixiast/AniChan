package com.aki.anichan.ui.medialist

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.repository.BrowseRepository
import com.aki.anichan.data.repository.UserRepository
import com.aki.anichan.data.response.anilist.MediaListOptions
import com.aki.anichan.helper.enums.Source
import com.aki.anichan.helper.extensions.applyScheduler
import com.aki.anichan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observable.zip
import io.reactivex.rxjava3.subjects.PublishSubject

class BottomSheetMediaListQuickDetailViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel<BottomSheetMediaListQuickDetailParam>() {

    private val _settings = PublishSubject.create<Pair<MediaListOptions, AppSetting>>()
    val settings: Observable<Pair<MediaListOptions, AppSetting>>
        get() = _settings

    override fun loadData(param: BottomSheetMediaListQuickDetailParam) {
        loadOnce {
            val isViewer = param.userId == 0

            disposables.add(
                zip(
                    if (isViewer) userRepository.getViewer(Source.CACHE) else browseRepository.getUser(param.userId),
                    userRepository.getAppSetting()
                ) { user, appSetting ->
                    user.mediaListOptions to appSetting
                }
                    .applyScheduler()
                    .subscribe {
                        _settings.onNext(it)
                    }
            )
        }
    }
}