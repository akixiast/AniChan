package com.aki.anichan

import android.app.Application
import com.google.gson.GsonBuilder
import com.aki.anichan.data.datasource.*
import com.aki.anichan.data.localstorage.*
import com.aki.anichan.data.manager.*
import com.aki.anichan.data.network.apollo.AniListApolloHandler
import com.aki.anichan.data.network.apollo.ApolloHandler
import com.aki.anichan.data.network.interceptor.AniListHeaderInterceptor
import com.aki.anichan.data.network.interceptor.HeaderInterceptor
import com.aki.anichan.data.network.interceptor.SpotifyAuthHeaderInterceptor
import com.aki.anichan.data.network.interceptor.SpotifyHeaderInterceptor
import com.aki.anichan.data.network.retrofit.DefaultRetrofitHandler
import com.aki.anichan.data.network.retrofit.RetrofitHandler
import com.aki.anichan.data.repository.*
import com.aki.anichan.helper.Constant
import com.aki.anichan.helper.service.clipboard.ClipboardService
import com.aki.anichan.helper.service.clipboard.DefaultClipboardService
import com.aki.anichan.helper.service.pushnotification.DefaultPushNotificationService
import com.aki.anichan.helper.service.pushnotification.PushNotificationService
import com.aki.anichan.ui.activity.ActivityDetailViewModel
import com.aki.anichan.ui.activity.ActivityListViewModel
import com.aki.anichan.ui.base.BaseActivityViewModel
import com.aki.anichan.ui.calendar.CalendarViewModel
import com.aki.anichan.ui.character.CharacterViewModel
import com.aki.anichan.ui.character.media.CharacterMediaListViewModel
import com.aki.anichan.ui.common.BottomSheetMediaQuickDetailViewModel
import com.aki.anichan.ui.customise.CustomiseViewModel
import com.aki.anichan.ui.editor.EditorViewModel
import com.aki.anichan.ui.explore.ExploreViewModel
import com.aki.anichan.ui.favorite.FavoriteViewModel
import com.aki.anichan.ui.filter.FilterViewModel
import com.aki.anichan.ui.follow.FollowViewModel
import com.aki.anichan.ui.discover.DiscoverViewModel
import com.aki.anichan.ui.home.HomeViewModel
import com.aki.anichan.ui.landing.LandingViewModel
import com.aki.anichan.ui.login.LoginViewModel
import com.aki.anichan.ui.main.MainViewModel
import com.aki.anichan.ui.main.SharedMainViewModel
import com.aki.anichan.ui.media.character.MediaCharacterListViewModel
import com.aki.anichan.ui.media.MediaViewModel
import com.aki.anichan.ui.media.mediasocial.MediaSocialViewModel
import com.aki.anichan.ui.media.mediastats.MediaStatsViewModel
import com.aki.anichan.ui.media.staff.MediaStaffListViewModel
import com.aki.anichan.ui.media.themes.BottomSheetMediaThemesViewModel
import com.aki.anichan.ui.medialist.BottomSheetMediaListQuickDetailViewModel
import com.aki.anichan.ui.medialist.MediaListViewModel
import com.aki.anichan.ui.notifications.NotificationsViewModel
import com.aki.anichan.ui.profile.ProfileViewModel
import com.aki.anichan.ui.reorder.ReorderViewModel
import com.aki.anichan.ui.review.ReviewViewModel
import com.aki.anichan.ui.review.reader.ReaderViewModel
import com.aki.anichan.ui.search.SearchViewModel
import com.aki.anichan.ui.seasonal.SeasonalViewModel
import com.aki.anichan.ui.settings.SettingsViewModel
import com.aki.anichan.ui.settings.account.AccountSettingsViewModel
import com.aki.anichan.ui.settings.anilist.AniListSettingsViewModel
import com.aki.anichan.ui.settings.app.AppSettingsViewModel
import com.aki.anichan.ui.settings.list.ListSettingsViewModel
import com.aki.anichan.ui.settings.notifications.NotificationsSettingsViewModel
import com.aki.anichan.ui.social.SocialViewModel
import com.aki.anichan.ui.splash.SplashViewModel
import com.aki.anichan.ui.staff.StaffViewModel
import com.aki.anichan.ui.staff.character.StaffCharacterListViewModel
import com.aki.anichan.ui.staff.media.StaffMediaListViewModel
import com.aki.anichan.ui.studio.StudioViewModel
import com.aki.anichan.ui.studio.media.StudioMediaListViewModel
import com.aki.anichan.ui.texteditor.TextEditorViewModel
import com.aki.anichan.ui.userstats.UserStatsViewModel

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module

class ALchanApplication : Application() {

    private val appModules = module {
        val gson = GsonBuilder()
            .setLenient()
            .serializeSpecialFloatingPointValues()
            .create()

        // local storage
        single<SharedPreferencesHandler> {
            DefaultSharedPreferencesHandler(
                this@ALchanApplication.applicationContext,
                Constant.SHARED_PREFERENCES_NAME,
                gson
            )
        }

        single<JsonStorageHandler> {
            DefaultJsonStorageHandler(
                this@ALchanApplication,
                gson
            )
        }

        single<FileStorageHandler> {
            DefaultFileStorageHandler(this@ALchanApplication)
        }

        // local storage manager
        single<UserManager> { DefaultUserManager(get(), get(), get()) }
        single<ContentManager> { DefaultContentManager(get()) }
        single<BrowseManager> { DefaultBrowseManager(get()) }

        // network
        val aniListHeaderInterceptor = "aniListHeaderInterceptor"
        val spotifyAuthHeaderInterceptor = "spotifyAuthHeaderInterceptor"
        val spotifyHeaderInterceptor = "spotifyHeaderInterceptor"

        single<HeaderInterceptor>(named(aniListHeaderInterceptor)) { AniListHeaderInterceptor(get()) }
        single<HeaderInterceptor>(named(spotifyAuthHeaderInterceptor)) { SpotifyAuthHeaderInterceptor(get()) }
        single<HeaderInterceptor>(named(spotifyHeaderInterceptor)) { SpotifyHeaderInterceptor(get()) }
        single<ApolloHandler> { AniListApolloHandler(get(named(aniListHeaderInterceptor)), Constant.ANILIST_API_BASE_URL) }
        single<RetrofitHandler> {
            DefaultRetrofitHandler(
                Constant.ALCHAN_RAW_GITHUB_URL,
                Constant.JIKAN_API_URL,
                Constant.ANIME_THEMES_API_URL,
                Constant.YOUTUBE_SEARCH_API_URL,
                Constant.SPOTIFY_AUTH_API_URL,
                get(named(spotifyAuthHeaderInterceptor)),
                Constant.SPOTIFY_API_URL,
                get(named(spotifyHeaderInterceptor))
            )
        }

        // data source
        single<ContentDataSource> { DefaultContentDataSource(get(), Constant.ANILIST_API_STATUS_VERSION, Constant.ANILIST_API_SOURCE_VERSION) }
        single<UserDataSource> { DefaultUserDataSource(get()) }
        single<MediaListDataSource> { DefaultMediaListDataSource(get(), Constant.ANILIST_API_STATUS_VERSION, Constant.ANILIST_API_SOURCE_VERSION) }
        single<BrowseDataSource> { DefaultBrowseDataSource(get(), get(), Constant.ANILIST_API_STATUS_VERSION, Constant.ANILIST_API_SOURCE_VERSION, Constant.ANILIST_API_RELATION_TYPE_VERSION) }
        single<SocialDataSource> { DefaultSocialDataSource(get()) }
        single<InfoDataSource> { DefaultInfoDataSource(get()) }

        // repository
        single<ContentRepository> { DefaultContentRepository(get(), get()) }
        single<UserRepository> { DefaultUserRepository(get(), get()) }
        single<MediaListRepository> { DefaultMediaListRepository(get(), get()) }
        single<BrowseRepository> { DefaultBrowseRepository(get(), get()) }
        single<SocialRepository> { DefaultSocialRepository(get()) }
        single<InfoRepository> { DefaultInfoRepository(get(), get()) }

        // service
        single<ClipboardService> { DefaultClipboardService(this.androidContext()) }
        single<PushNotificationService> { DefaultPushNotificationService(this.androidContext(), get()) }

        // view model
        viewModel { BaseActivityViewModel(get()) }

        viewModel { SplashViewModel(get(), get()) }
        viewModel { LandingViewModel(get()) }
        viewModel { LoginViewModel(get()) }

        viewModel { SharedMainViewModel() }
        viewModel { MainViewModel(get(), get(), get(), get()) }

        viewModel { BottomSheetMediaQuickDetailViewModel(get()) }
        viewModel { BottomSheetMediaListQuickDetailViewModel(get(), get()) }
        viewModel { BottomSheetMediaThemesViewModel(get()) }

        viewModel { HomeViewModel(get(), get(), get()) }
        viewModel { DiscoverViewModel() }
        viewModel { SearchViewModel(get(), get()) }
        viewModel { SeasonalViewModel(get(), get(), get()) }
        viewModel { ExploreViewModel(get(), get()) }
        viewModel { CalendarViewModel(get(), get()) }
        viewModel { ReviewViewModel(get(), get()) }
        viewModel { ReaderViewModel(get(), get(), get()) }

        viewModel { MediaListViewModel(get(), get(), get(), get()) }

        viewModel { NotificationsViewModel(get()) }

        viewModel { ProfileViewModel(get(), get(), get(), get()) }
        viewModel { FollowViewModel(get()) }
        viewModel { UserStatsViewModel(get(), get()) }
        viewModel { FavoriteViewModel(get()) }

        viewModel { SettingsViewModel() }
        viewModel { AppSettingsViewModel(get(), get()) }
        viewModel { AniListSettingsViewModel(get()) }
        viewModel { ListSettingsViewModel(get()) }
        viewModel { NotificationsSettingsViewModel(get()) }
        viewModel { AccountSettingsViewModel(get(), get()) }

        viewModel { ReorderViewModel() }

        viewModel { FilterViewModel(get(), get()) }
        viewModel { CustomiseViewModel(get(), get()) }

        viewModel { EditorViewModel(get(), get()) }

        viewModel { MediaViewModel(get(), get(), get(), get()) }
        viewModel { MediaStatsViewModel(get()) }
        viewModel { MediaSocialViewModel(get(), get()) }
        viewModel { MediaCharacterListViewModel(get(), get()) }
        viewModel { MediaStaffListViewModel(get(), get()) }
        viewModel { CharacterViewModel(get(), get(), get()) }
        viewModel { CharacterMediaListViewModel(get(), get()) }
        viewModel { StaffViewModel(get(), get(), get()) }
        viewModel { StaffCharacterListViewModel(get(), get()) }
        viewModel { StaffMediaListViewModel(get(), get()) }
        viewModel { StudioViewModel(get(), get(), get()) }
        viewModel { StudioMediaListViewModel(get(), get()) }

        viewModel { SocialViewModel(get(), get(), get()) }
        viewModel { ActivityDetailViewModel(get(), get(), get()) }
        viewModel { ActivityListViewModel(get(), get(), get()) }
        viewModel { TextEditorViewModel(get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ALchanApplication)
            modules(appModules)
        }
    }
}