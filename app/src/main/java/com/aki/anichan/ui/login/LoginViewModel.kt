package com.aki.anichan.ui.login

import com.aki.anichan.data.repository.UserRepository
import com.aki.anichan.helper.enums.Source
import com.aki.anichan.helper.extensions.applyScheduler
import com.aki.anichan.helper.extensions.getStringResource
import com.aki.anichan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class LoginViewModel(private val userRepository: UserRepository) : BaseViewModel<Unit>() {

    private val _loginTrigger = PublishSubject.create<Unit>()
    val loginTrigger: Observable<Unit>
        get() = _loginTrigger

    override fun loadData(param: Unit) = Unit

    fun login(bearerToken: String) {
        _loading.onNext(true)
        userRepository.saveBearerToken(bearerToken)

        disposables.add(
            userRepository.getViewer(Source.NETWORK)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        _loginTrigger.onNext(Unit)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun loginAsGuest() {
        userRepository.loginAsGuest()
        _loginTrigger.onNext(Unit)
    }
}