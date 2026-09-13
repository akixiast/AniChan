package com.aki.anichan.ui.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.aki.anichan.R
import com.aki.anichan.data.entity.ListStyle
import com.aki.anichan.data.entity.MediaFilter
import com.aki.anichan.data.response.anilist.Activity
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.Review
import com.aki.anichan.helper.Constant
import com.aki.anichan.helper.enums.ActivityListPage
import com.aki.anichan.helper.enums.Favorite
import com.aki.anichan.helper.enums.MediaType
import com.aki.anichan.helper.enums.SearchCategory
import com.aki.anichan.helper.enums.TextEditorType
import com.aki.anichan.helper.utils.DeepLink
import com.aki.anichan.type.ScoreFormat
import com.aki.anichan.ui.activity.ActivityDetailFragment
import com.aki.anichan.ui.activity.ActivityListFragment
import com.aki.anichan.ui.browse.BrowseFragment
import com.aki.anichan.ui.calendar.CalendarFragment
import com.aki.anichan.ui.character.CharacterFragment
import com.aki.anichan.ui.character.media.CharacterMediaListFragment
import com.aki.anichan.ui.customise.CustomiseFragment
import com.aki.anichan.ui.editor.EditorFragment
import com.aki.anichan.ui.explore.ExploreFragment
import com.aki.anichan.ui.favorite.FavoriteFragment
import com.aki.anichan.ui.filter.FilterFragment
import com.aki.anichan.ui.follow.FollowFragment
import com.aki.anichan.ui.landing.LandingFragment
import com.aki.anichan.ui.login.LoginFragment
import com.aki.anichan.ui.main.MainFragment
import com.aki.anichan.ui.media.MediaFragment
import com.aki.anichan.ui.media.character.MediaCharacterListFragment
import com.aki.anichan.ui.media.mediasocial.MediaSocialFragment
import com.aki.anichan.ui.media.mediastats.MediaStatsFragment
import com.aki.anichan.ui.media.staff.MediaStaffListFragment
import com.aki.anichan.ui.medialist.MediaListFragment
import com.aki.anichan.ui.profile.ProfileFragment
import com.aki.anichan.ui.reorder.ReorderFragment
import com.aki.anichan.ui.review.ReviewFragment
import com.aki.anichan.ui.review.reader.ReaderFragment
import com.aki.anichan.ui.search.SearchFragment
import com.aki.anichan.ui.seasonal.SeasonalFragment
import com.aki.anichan.ui.settings.SettingsFragment
import com.aki.anichan.ui.settings.about.AboutFragment
import com.aki.anichan.ui.settings.account.AccountSettingsFragment
import com.aki.anichan.ui.settings.anilist.AniListSettingsFragment
import com.aki.anichan.ui.settings.app.AppSettingsFragment
import com.aki.anichan.ui.settings.list.ListSettingsFragment
import com.aki.anichan.ui.settings.notifications.NotificationsSettingsFragment
import com.aki.anichan.ui.social.SocialFragment
import com.aki.anichan.ui.splash.SplashFragment
import com.aki.anichan.ui.staff.StaffFragment
import com.aki.anichan.ui.staff.character.StaffCharacterListFragment
import com.aki.anichan.ui.staff.media.StaffMediaListFragment
import com.aki.anichan.ui.studio.StudioFragment
import com.aki.anichan.ui.studio.media.StudioMediaListFragment
import com.aki.anichan.ui.texteditor.TextEditorActivity
import com.aki.anichan.ui.userstats.UserStatsFragment
import io.reactivex.rxjava3.disposables.Disposable


class DefaultNavigationManager(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val layout: FragmentContainerView
) : NavigationManager {

    override fun navigateToSplash(deepLink: DeepLink?, bypassSplash: Boolean) {
        swapPage(SplashFragment.newInstance(deepLink, bypassSplash), true)
    }

    override fun navigateToLanding() {
        swapPage(LandingFragment.newInstance(), true)
    }

    override fun navigateToLogin(bearerToken: String?, disableAnimation: Boolean) {
        swapPage(LoginFragment.newInstance(bearerToken), true, disableAnimation)
    }

    override fun navigateToMain(deepLink: DeepLink?) {
        swapPage(MainFragment.newInstance(deepLink), true)
    }

    override fun navigateToSearch(searchCategory: SearchCategory) {
        stackPage(SearchFragment.newInstance(searchCategory))
    }

    override fun navigateToSeasonal() {
        stackPage(SeasonalFragment.newInstance())
    }

    override fun navigateToExplore(searchCategory: SearchCategory, mediaFilter: MediaFilter?, action: ((() -> Unit) -> Unit)?) {
        val listener = if (action == null)
            null
        else
            object : ExploreFragment.ExploreListener {
                override fun doNavigation(navigation: () -> Unit) {
                    action.invoke { navigation() }
                }
            }
        stackPage(ExploreFragment.newInstance(searchCategory, mediaFilter, listener))
    }

    override fun navigateToSocial() {
        stackPage(SocialFragment.newInstance())
    }

    override fun navigateToCalendar() {
        stackPage(CalendarFragment.newInstance())
    }

    override fun navigateToReview() {
        stackPage(ReviewFragment.newInstance(null, null))
    }

    override fun navigateToReader(review: Review, action: (review: Review) -> Unit) {
        pushBrowseScreenPage(ReaderFragment.newInstance(review, object : ReaderFragment.ReaderListener {
            override fun updateRating(review: Review) {
                action(review)
            }
        }))
    }

    override fun navigateToActivityDetail(id: Int, action: (activity: Activity, isDeleted: Boolean) -> Unit) {
        pushBrowseScreenPage(ActivityDetailFragment.newInstance(id, object : ActivityDetailFragment.ActivityDetailListener {
            override fun getActivityDetailResult(activity: Activity, isDeleted: Boolean) {
                action(activity, isDeleted)
            }
        }))
    }

    override fun navigateToActivityList(activityListPage: ActivityListPage, id: Int?) {
        pushBrowseScreenPage(ActivityListFragment.newInstance(activityListPage, id))
    }

    override fun navigateToTextEditor(
        textEditorType: TextEditorType,
        activityId: Int?,
        activityReplyId: Int?,
        recipientId: Int?,
        username: String?
    ) {
        val intent = Intent(context, TextEditorActivity::class.java)
        val bundle = Bundle().apply {
            activityId?.let {
                putInt(TextEditorActivity.ACTIVITY_ID, activityId)
            }
            activityReplyId?.let {
                putInt(TextEditorActivity.ACTIVITY_REPLY_ID, activityReplyId)
            }
            recipientId?.let {
                putInt(TextEditorActivity.RECIPIENT_ID, recipientId)
            }
            username?.let {
                putString(TextEditorActivity.USERNAME, username)
            }
            putString(TextEditorActivity.TEXT_EDITOR_TYPE, textEditorType.name)
        }
        intent.putExtras(bundle)
        context.startActivity(intent)
    }

    override fun navigateToSettings() {
        stackPage(SettingsFragment.newInstance())
    }

    override fun navigateToAppSettings() {
        stackPage(AppSettingsFragment.newInstance())
    }

    override fun navigateToAniListSettings() {
        stackPage(AniListSettingsFragment.newInstance())
    }

    override fun navigateToListSettings() {
        stackPage(ListSettingsFragment.newInstance())
    }

    override fun navigateToNotificationsSettings() {
        stackPage(NotificationsSettingsFragment.newInstance())
    }

    override fun navigateToAccountSettings() {
        stackPage(AccountSettingsFragment.newInstance())
    }

    override fun navigateToAbout() {
        stackPage(AboutFragment.newInstance())
    }

    override fun navigateToReorder(itemList: List<String>, action: (reorderResult: List<String>) -> Unit) {
        stackPage(ReorderFragment.newInstance(itemList, object : ReorderFragment.ReorderListener {
            override fun getReorderResult(reorderResult: List<String>) {
                action(reorderResult)
            }
        }))
    }

    override fun navigateToFilter(
        mediaFilter: MediaFilter?,
        mediaType: MediaType,
        scoreFormat: ScoreFormat,
        isUserList: Boolean,
        hasBigList: Boolean,
        isCurrentUser: Boolean,
        action: (filterResult: MediaFilter) -> Unit
    ) {
        stackPage(FilterFragment.newInstance(mediaFilter, mediaType, scoreFormat, isUserList, hasBigList, isCurrentUser, object : FilterFragment.FilterListener {
            override fun getFilterResult(filterResult: MediaFilter) {
                action(filterResult)
            }
        }))
    }

    override fun navigateToCustomise(mediaType: MediaType, action: (customiseResult: ListStyle) -> Unit) {
        stackPage(CustomiseFragment.newInstance(mediaType, object : CustomiseFragment.CustomiseListener {
            override fun getCustomiseResult(customiseResult: ListStyle) {
                action(customiseResult)
            }
        }))
    }

    override fun navigateToEditor(mediaId: Int, fromMediaList: Boolean, action: (() -> Unit)?) {
        stackPage(EditorFragment.newInstance(mediaId, fromMediaList, object : EditorFragment.EditorListener {
            override fun onEntryEdited() {
                action?.invoke()
            }
        }))
    }

    override fun navigateToMedia(id: Int) {
        pushBrowseScreenPage(MediaFragment.newInstance(id))
    }

    override fun navigateToMediaStats(media: Media) {
        pushBrowseScreenPage(MediaStatsFragment.newInstance(media))
    }

    override fun navigateToMediaSocial(media: Media) {
        pushBrowseScreenPage(MediaSocialFragment.newInstance(media))
    }

    override fun navigateToMediaReview(media: Media) {
        pushBrowseScreenPage(ReviewFragment.newInstance(media, null))
    }

    override fun navigateToMediaCharacters(id: Int) {
        pushBrowseScreenPage(MediaCharacterListFragment.newInstance(id))
    }

    override fun navigateToMediaStaff(id: Int) {
        pushBrowseScreenPage(MediaStaffListFragment.newInstance(id))
    }

    override fun navigateToCharacter(id: Int) {
        pushBrowseScreenPage(CharacterFragment.newInstance(id))
    }

    override fun navigateToCharacterMedia(id: Int) {
        pushBrowseScreenPage(CharacterMediaListFragment.newInstance(id))
    }

    override fun navigateToStaff(id: Int) {
        pushBrowseScreenPage(StaffFragment.newInstance(id))
    }

    override fun navigateToStaffCharacter(id: Int) {
        pushBrowseScreenPage(StaffCharacterListFragment.newInstance(id))
    }

    override fun navigateToStaffMedia(id: Int) {
        pushBrowseScreenPage(StaffMediaListFragment.newInstance(id))
    }

    override fun navigateToUser(id: Int?, username: String?) {
        pushBrowseScreenPage(ProfileFragment.newInstance(id, username))
    }

    override fun navigateToUserReview(id: Int) {
        pushBrowseScreenPage(ReviewFragment.newInstance(null, id))
    }

    override fun navigateToStudio(id: Int) {
        pushBrowseScreenPage(StudioFragment.newInstance(id))
    }

    override fun navigateToStudioMedia(id: Int) {
        pushBrowseScreenPage(StudioMediaListFragment.newInstance(id))
    }

    override fun navigateToAnimeMediaList(id: Int) {
        pushBrowseScreenPage(MediaListFragment.newInstance(MediaType.ANIME, id))
    }

    override fun navigateToMangaMediaList(id: Int) {
        pushBrowseScreenPage(MediaListFragment.newInstance(MediaType.MANGA, id))
    }

    override fun navigateToFollowing(id: Int) {
        pushBrowseScreenPage(FollowFragment.newInstance(id, true))
    }

    override fun navigateToFollowers(id: Int) {
        pushBrowseScreenPage(FollowFragment.newInstance(id, false))
    }

    override fun navigateToUserStats(id: Int) {
        pushBrowseScreenPage(UserStatsFragment.newInstance(id))
    }

    override fun navigateToFavorite(id: Int, favorite: Favorite) {
        pushBrowseScreenPage(FavoriteFragment.newInstance(id, favorite))
    }

    override fun openWebView(url: String) {
        launchWebView(Uri.parse(url))
    }

    override fun openWebView(url: NavigationManager.Url, id: Int?) {
        launchWebView(
            Uri.parse(
                when (url) {
                    NavigationManager.Url.ANILIST_WEBSITE -> Constant.ANILIST_WEBSITE_URL
                    NavigationManager.Url.ANILIST_LOGIN -> Constant.ANILIST_LOGIN_URL
                    NavigationManager.Url.ANILIST_REGISTER -> Constant.ANILIST_REGISTER_URL
                    NavigationManager.Url.ANILIST_PROFILE_SETTINGS -> Constant.ANILIST_PROFILE_SETTINGS_URL
                    NavigationManager.Url.ANILIST_ACCOUNT_SETTINGS -> Constant.ANILIST_ACCOUNT_SETTINGS_URL
                    NavigationManager.Url.ANILIST_LISTS_SETTINGS -> Constant.ANILIST_LISTS_SETTINGS_URL
                    NavigationManager.Url.ANILIST_IMPORT_LISTS -> Constant.ANILIST_IMPORT_LISTS_URL
                    NavigationManager.Url.ANILIST_CONNECT_WITH_TWITTER -> Constant.ANILIST_CONNECT_WITH_TWITTER_URL
                    NavigationManager.Url.ANLIST_ACTIVITY -> Constant.ANILIST_ACTIVITY + id
                    NavigationManager.Url.ALCHAN_FORUM_THREAD -> Constant.ALCHAN_FORUM_THREAD_URL
                    NavigationManager.Url.ALCHAN_GITHUB -> Constant.ALCHAN_GITHUB_URL
                    NavigationManager.Url.ALCHAN_PLAY_STORE -> Constant.ALCHAN_PLAY_STORE_URL
                    NavigationManager.Url.ALCHAN_TWITTER -> Constant.ALCHAN_TWITTER_URL
                    NavigationManager.Url.ALCHAN_PRIVACY_POLICY -> Constant.ALCHAN_PRIVACY_POLICY_URL
                }
            )
        )
    }

    override fun openEmailClient() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Constant.ALCHAN_EMAIL_ADDRESS, null))
        context.startActivity(intent)
    }

    override fun openGallery(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        launcher.launch(intent)
    }

    override fun openOnYouTube(videoId: String) {
        launchWebView(Uri.parse("${Constant.YOUTUBE_URL}$videoId"))
    }

    override fun openOnSpotify(url: String) {
        launchWebView(Uri.parse(url))
    }

    override fun isAtPreLoginScreen(): Boolean {
        val fragments = fragmentManager.fragments.filterIsInstance<BaseFragment<*, *>>()
        if (fragments.isEmpty()) return true
        val lastFragment = fragments.last()
        return lastFragment is SplashFragment || lastFragment is LandingFragment || lastFragment is LoginFragment
    }

    override fun isAtBrowseScreen(): Boolean {
        val fragments = fragmentManager.fragments.filterIsInstance<BaseFragment<*, *>>()
        if (fragments.isEmpty()) return false
        val lastFragment = fragments.last()
        return lastFragment is BrowseFragment
    }

    private fun pushBrowseScreenPage(fragment: Fragment, skipBackStack: Boolean = false) {
        if (isAtBrowseScreen()) {
            val browseFragment = fragmentManager.fragments.filterIsInstance<BrowseFragment>().last()
            val fragmentTransaction = browseFragment.childFragmentManager.beginTransaction()
            fragmentTransaction.replace(browseFragment.layout.id, fragment)
            if (!skipBackStack) {
                fragmentTransaction.addToBackStack(fragment.toString())
            }
            fragmentTransaction.commit()
        } else {
            val browseFragment = BrowseFragment.newInstance()
            var disposable: Disposable? = null
            disposable = browseFragment.layoutSet
                .doFinally {
                    disposable?.dispose()
                    disposable = null
                }
                .subscribe {
                    pushBrowseScreenPage(fragment, skipBackStack)
                }
            stackPage(browseFragment)
        }
    }

    override fun popBrowseScreenPage() {
        if (isAtBrowseScreen()) {
            val browseFragment = fragmentManager.fragments.filterIsInstance<BrowseFragment>().last()
            browseFragment.childFragmentManager.popBackStack()
        }
    }

    override fun shouldPopFromBrowseScreen(): Boolean {
        if (isAtBrowseScreen()) {
            val browseFragment = fragmentManager.fragments.filterIsInstance<BrowseFragment>().last()
            return browseFragment.childFragmentManager.backStackEntryCount > 1
        } else {
            return false
        }
    }

    override fun closeBrowseScreen() {
        if (isAtBrowseScreen()) {
            fragmentManager.popBackStack()
        }
    }

    override fun hasBackStack(): Boolean {
        return fragmentManager.backStackEntryCount != 0
    }

    override fun popBackStack() {
        fragmentManager.popBackStack()
    }

    private fun swapPage(fragment: Fragment, skipBackStack: Boolean = false, disableAnimation: Boolean = false) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (!disableAnimation) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
        }
        fragmentTransaction.replace(layout.id, fragment)
        if (!skipBackStack) {
            fragmentTransaction.addToBackStack(fragment.toString())
        }
        fragmentTransaction.commit()
    }

    private fun stackPage(fragment: Fragment, disableAnimation: Boolean = false) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (!disableAnimation) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
        }
        fragmentTransaction.add(layout.id, fragment)
        fragmentTransaction.addToBackStack(fragment.toString())
        fragmentTransaction.commit()
    }

    private fun launchWebView(uri: Uri) {
        fun getBrowserPackageName(uri: Uri): String? {
            return CustomTabsClient.getPackageName(context, emptyList())
        }

        val customTabsIntent = CustomTabsIntent.Builder().build()

        // Force open AniList page with WebView
        if ("${uri.scheme}://${uri.authority}" == Constant.ANILIST_WEBSITE_URL) {
            getBrowserPackageName(uri)?.let {
                customTabsIntent.intent.`package` = it
            }
        }

        customTabsIntent.launchUrl(context, uri)
    }
}