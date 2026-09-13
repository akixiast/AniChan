package com.aki.anichan.ui.settings.account

import com.aki.anichan.data.manager.UserManager
import com.aki.anichan.data.repository.UserRepository
import com.aki.anichan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable

class AccountSettingsViewModel(private val userRepository: UserRepository, private val userManager: UserManager) : BaseViewModel<Unit>() {

    override fun loadData(param: Unit) = Unit

    fun logout() {
        userRepository.logout()
        userManager.isLoggedInAsGuest = true
    }

    val isLoggedIn: Observable<Boolean> = userRepository.getIsAuthenticated()
}