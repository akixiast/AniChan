package com.aki.anichan.ui.common

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.repository.UserRepository
import com.aki.anichan.helper.enums.Source
import com.aki.anichan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class BottomSheetMediaQuickDetailViewModel(private val userRepository: UserRepository) : BaseViewModel<Unit>() {

    private val _appSetting = PublishSubject.create<AppSetting>()
    val appSetting: Observable<AppSetting>
        get() = _appSetting

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                userRepository.getAppSetting()
                    .subscribe {
                        _appSetting.onNext(it)
                    }
            )
        }
    }
}