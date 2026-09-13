package com.aki.anichan.ui.landing

import com.aki.anichan.data.manager.UserManager
import com.aki.anichan.ui.base.BaseViewModel

class LandingViewModel(private val userManager: UserManager) : BaseViewModel<Unit>() {

    override fun loadData(param: Unit) = Unit

    fun setGuestMode() {
        userManager.isLoggedInAsGuest = true
    }
}