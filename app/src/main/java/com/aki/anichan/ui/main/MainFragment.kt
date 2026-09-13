package com.aki.anichan.ui.main

import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.aki.anichan.R
import com.aki.anichan.databinding.FragmentMainBinding
import com.aki.anichan.helper.utils.DeepLink
import com.aki.anichan.helper.utils.PushNotificationUtil
import com.aki.anichan.ui.base.BaseFragment
import com.aki.anichan.ui.discover.DiscoverFragment
import com.aki.anichan.ui.home.HomeFragment
import com.aki.anichan.ui.library.LibraryFragment
import com.aki.anichan.ui.notifications.NotificationsFragment
import com.aki.anichan.ui.profile.ProfileFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var viewPagerAdapter: MainViewPagerAdapter? = null

    private var fragments: List<Fragment?>? = null
    private var discoverFragment: DiscoverFragment? = null
    private var homeFragment: HomeFragment? = null
    private var libraryFragment: LibraryFragment? = null
    private var profileFragment: ProfileFragment? = null
    private var notificationsFragment: NotificationsFragment? = null

    private var deepLink: DeepLink? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            val isViewerAuthenticated = viewModel.isViewerAuthenticated

            discoverFragment = DiscoverFragment.newInstance()
            homeFragment = HomeFragment.newInstance()
            libraryFragment = LibraryFragment.newInstance()
            profileFragment = ProfileFragment.newInstance()
            notificationsFragment = NotificationsFragment.newInstance()

            // tab order matches menu order: Discover / Home / Library / Profile / Notifications
            fragments = if (isViewerAuthenticated) {
                listOf(
                    discoverFragment,
                    homeFragment,
                    libraryFragment,
                    profileFragment,
                    notificationsFragment
                )
            } else {
                listOf(
                    discoverFragment,
                    homeFragment,
                    libraryFragment,
                    profileFragment
                )
            }

            // stable id mapping: pager position -> menu item id (never index into menu by position)
            val tabMenuIds = if (isViewerAuthenticated) {
                listOf(
                    R.id.menuDiscover,
                    R.id.menuHome,
                    R.id.menuLibrary,
                    R.id.menuProfile,
                    R.id.menuNotifications
                )
            } else {
                listOf(
                    R.id.menuDiscover,
                    R.id.menuHome,
                    R.id.menuLibrary,
                    R.id.menuProfile
                )
            }

            viewPagerAdapter = MainViewPagerAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                fragments?.filterNotNull() ?: listOf()
            )

            binding.mainViewPager.adapter = viewPagerAdapter
            mainViewPager.isUserInputEnabled = false
            mainViewPager.offscreenPageLimit = 5

            binding.mainBottomNavigation.menu.findItem(R.id.menuNotifications).isVisible = isViewerAuthenticated

            mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabMenuIds.getOrNull(position)?.let {
                        mainBottomNavigation.menu.findItem(it).isChecked = true
                    }
                }
            })

            mainBottomNavigation.setOnItemSelectedListener {
                if (it.itemId == R.id.menuNotifications)
                    viewModel.clearUnreadNotificationCountBadge()

                val index = tabMenuIds.indexOf(it.itemId)
                if (index != -1)
                    mainViewPager.setCurrentItem(index, true)
                true
            }

            mainBottomNavigation.setOnItemReselectedListener {
                val index = tabMenuIds.indexOf(it.itemId)
                if (index != -1)
                    sharedViewModel.scrollToTop(index)
            }

            // default landing tab: Home (pager + checked tab set together)
            mainViewPager.setCurrentItem(1, false)
            binding.mainBottomNavigation.menu.findItem(R.id.menuHome).isChecked = true
        }
    }

    override fun setUpObserver() {
        viewModel.loadData(Unit)

        if (!sharedDisposablesAdded) {
            sharedDisposables.add(
                incomingDeepLink.subscribe {
                    handleDeepLinkNavigation(it)
                }
            )

            sharedDisposables.add(
                sharedViewModel.bottomSheetNavigation.subscribe {
                    if (it in 0 until (fragments?.size ?: 0))
                        binding.mainViewPager.setCurrentItem(it, true)
                    else if (it == SharedMainViewModel.Page.PROFILE.ordinal)
                        navigation.navigateToUser()
                }
            )

            sharedDisposablesAdded = true
        }

        disposables.add(
            viewModel.unreadNotificationCount.subscribe {
                if (it == 0)
                    binding.mainBottomNavigation.removeBadge(R.id.menuNotifications)
                else
                    binding.mainBottomNavigation.getOrCreateBadge(R.id.menuNotifications).number = it
            }
        )

        deepLink?.let {
            handleDeepLinkNavigation(it)
        }
    }

    private fun handleDeepLinkNavigation(deepLink: DeepLink) {
        val isViewerAuthenticated = viewModel.isViewerAuthenticated

        when {
            deepLink.isHome() -> binding.mainViewPager.currentItem = 1
            (deepLink.isAnimeList() || deepLink.isMangaList()) && isViewerAuthenticated -> {
                val libraryIndex = fragments?.indexOfFirst { it == libraryFragment }
                if (libraryIndex != null && libraryIndex != -1) {
                    changeTabWithDelay(libraryIndex)
                }
            }
            deepLink.isNotifications() && isViewerAuthenticated -> {
                val notificationsIndex = fragments?.indexOfFirst { it == notificationsFragment }
                if (notificationsIndex != null && notificationsIndex != -1) {
                    changeTabWithDelay(notificationsIndex)
                    context?.let { PushNotificationUtil.clearAllPushNotification(it) }
                }
            }
            deepLink.isProfile() -> {
                navigation.navigateToUser()
            }
            deepLink.isAppSettings() -> {
                navigation.navigateToSettings()
                navigation.navigateToAppSettings()
            }
            deepLink.isAniListSettings() -> {
                navigation.navigateToSettings()
                navigation.navigateToAniListSettings()
            }
            deepLink.isListSettings() -> {
                navigation.navigateToSettings()
                navigation.navigateToListSettings()
            }
            deepLink.isSpoiler() -> {
                dialog.showSpoilerDialog(deepLink.getQueryParamOfOrNull("data") ?: "", null)
            }
            deepLink.isAnime() || deepLink.isManga() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToMedia(it.toInt()) }
            }
            deepLink.isCharacter() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToCharacter(it.toInt()) }
            }
            deepLink.isStaff() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToStaff(it.toInt()) }
            }
            deepLink.isStudio() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToStudio(it.toInt()) }
            }
            deepLink.isUser() -> {
                deepLink.getAniListPageId()?.let {
                    val isUsername = it.toIntOrNull() == null
                    if (isUsername)
                        navigation.navigateToUser(username = it)
                    else
                        navigation.navigateToUser(id = it.toInt())
                }
            }
            deepLink.isActivity() -> {
                deepLink.getAniListPageId()?.let { navigation.navigateToActivityDetail(it.toInt()) { _, _ -> } }
            }
        }

        this.deepLink = null
    }

    private fun changeTabWithDelay(index: Int) {
        Single.timer(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    binding.mainViewPager.currentItem = index
                },
                {
                }
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPagerAdapter = null
        fragments = null
        discoverFragment = null
        homeFragment = null
        libraryFragment = null
        profileFragment = null
        notificationsFragment = null
    }

    companion object {
        @JvmStatic
        fun newInstance(deepLink: DeepLink?) = MainFragment().apply {
            this.deepLink = deepLink
        }
    }
}
