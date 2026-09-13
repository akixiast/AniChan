package com.aki.anichan.helper.service.update

import android.content.Context
import android.content.SharedPreferences
import com.aki.anichan.BuildConfig
import com.aki.anichan.data.network.retrofit.RetrofitHandler
import com.aki.anichan.helper.extensions.applyScheduler
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

class AppUpdateChecker(
    private val context: Context,
    private val retrofitHandler: RetrofitHandler
) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun checkForUpdate(): Observable<AppUpdateInfo> {
        return retrofitHandler.gitHubRetrofitClient()
            .getLatestRelease(LATEST_RELEASE_URL)
            .map { AppUpdateInfo.from(it, BuildConfig.VERSION_NAME) }
            .applyScheduler()
    }

    fun checkBlocking(): AppUpdateInfo? {
        return try {
            val response = retrofitHandler.gitHubRetrofitClient()
                .getLatestRelease(LATEST_RELEASE_URL)
                .blockingFirst()
            AppUpdateInfo.from(response, BuildConfig.VERSION_NAME)
        } catch (e: Exception) {
            null
        }
    }

    fun getLastNotifiedTag(): String {
        return prefs.getString(KEY_LAST_NOTIFIED_TAG, "") ?: ""
    }

    fun setLastNotifiedTag(tag: String) {
        prefs.edit().putString(KEY_LAST_NOTIFIED_TAG, tag).apply()
    }

    companion object {
        const val GITHUB_REPO = "akixiast/AniChanV"
        const val LATEST_RELEASE_URL = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"
        const val RELEASES_PAGE_URL = "https://github.com/$GITHUB_REPO/releases"

        private const val PREFS_NAME = "appUpdatePrefs"
        private const val KEY_LAST_NOTIFIED_TAG = "lastNotifiedTag"
    }
}
