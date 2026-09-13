package com.aki.anichan.ui.main

import com.aki.anichan.data.manager.UserManager
import com.aki.anichan.data.repository.ContentRepository
import com.aki.anichan.data.repository.UserRepository
import com.aki.anichan.helper.extensions.applyScheduler
import com.aki.anichan.helper.service.pushnotification.PushNotificationService
import com.aki.anichan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class MainViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository,
    private val pushNotificationService: PushNotificationService,
    private val userManager: UserManager
) : BaseViewModel<Unit>() {

    // Don't use blockingFirst() - just check directly
    val isViewerAuthenticated: Boolean
        get() = userManager.isAuthenticated

    private val _unreadNotificationCount = BehaviorSubject.createDefault(0)
    val unreadNotificationCount: Observable<Int>
        get() = _unreadNotificationCount

    override fun loadData(param: Unit) {
        loadOnce {
            // serialized: genres then tags, one request at a time (not parallel)
            disposables.add(
                contentRepository.getGenres()
                    .flatMap { contentRepository.getTags() }
                    .subscribe({}, {})
            )

            disposables.add(
                userRepository.unreadNotificationCount
                    .applyScheduler()
                    .subscribe {
                        _unreadNotificationCount.onNext(it)
                    }
            )

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        pushNotificationService.startPushNotification()
                    }
            )
        }
    }

    fun clearUnreadNotificationCountBadge() {
        userRepository.clearUnreadNotificationCount()
    }
}