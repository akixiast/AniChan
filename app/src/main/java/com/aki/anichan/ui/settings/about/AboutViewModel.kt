package com.aki.anichan.ui.settings.about

import com.aki.anichan.helper.extensions.applyScheduler
import com.aki.anichan.helper.service.update.AppUpdateChecker
import com.aki.anichan.helper.service.update.AppUpdateInfo
import com.aki.anichan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class AboutViewModel(
    private val appUpdateChecker: AppUpdateChecker
) : BaseViewModel<Unit>() {

    private val _updateState = PublishSubject.create<UpdateState>()
    val updateState: Observable<UpdateState>
        get() = _updateState

    override fun loadData(param: Unit) = Unit

    fun checkForUpdate(manual: Boolean = false) {
        _updateState.onNext(UpdateState.Checking)
        disposables.add(
            appUpdateChecker.checkForUpdate()
                .subscribe(
                    { info ->
                        if (info.isNewer) {
                            _updateState.onNext(UpdateState.Available(info))
                        } else {
                            _updateState.onNext(UpdateState.UpToDate(info.latestTag))
                        }
                    },
                    {
                        _updateState.onNext(UpdateState.Error)
                    }
                )
        )
    }

    sealed class UpdateState {
        object Checking : UpdateState()
        data class Available(val info: AppUpdateInfo) : UpdateState()
        data class UpToDate(val latestTag: String) : UpdateState()
        object Error : UpdateState()
    }
}
