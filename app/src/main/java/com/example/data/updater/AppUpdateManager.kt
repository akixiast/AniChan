package com.example.data.updater

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

data class AppRelease(
    val tagName: String,
    val name: String,
    val body: String,
    val publishedAt: String,
    val htmlUrl: String,
    val downloadUrl: String,
    val isPrerelease: Boolean,
    val hasNewerVersion: Boolean
)

sealed class UpdateCheckState {
    object Idle : UpdateCheckState()
    object Checking : UpdateCheckState()
    data class UpdateAvailable(val release: AppRelease) : UpdateCheckState()
    data class UpToDate(val currentVersion: String) : UpdateCheckState()
    data class Error(val message: String) : UpdateCheckState()
}

class AppUpdateManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("anichan_update_prefs", Context.MODE_PRIVATE)

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val _updateState = MutableStateFlow<UpdateCheckState>(UpdateCheckState.Idle)
    val updateState: StateFlow<UpdateCheckState> = _updateState.asStateFlow()

    val currentVersion: String = "v1.1 beta"
    val currentVersionCode: String = "1.1.0"

    // Built-in GitHub repo
    val repo: String = "akixiast/AniChan"
    val repoUrl: String = "https://github.com/akixiast/AniChan"

    fun isAutoCheckEnabled(): Boolean {
        return prefs.getBoolean("auto_check_updates", true)
    }

    fun setAutoCheckEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("auto_check_updates", enabled).apply()
    }

    fun resetState() {
        _updateState.value = UpdateCheckState.Idle
    }

    suspend fun checkForUpdates(): UpdateCheckState = withContext(Dispatchers.IO) {
        _updateState.value = UpdateCheckState.Checking

        try {
            val url = "https://api.github.com/repos/$repo/releases/latest"
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "AniChan-Android-App")
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                if (response.code == 404) {
                    val error = UpdateCheckState.Error("No releases found on GitHub for repository: $repo")
                    _updateState.value = error
                    return@withContext error
                } else {
                    val error = UpdateCheckState.Error("GitHub API response error (${response.code})")
                    _updateState.value = error
                    return@withContext error
                }
            }

            val json = JSONObject(responseBody)
            val tagName = json.optString("tag_name", "")
            val name = json.optString("name", tagName)
            val body = json.optString("body", "No release notes provided.")
            val rawDate = json.optString("published_at", "")
            val htmlUrl = json.optString("html_url", "https://github.com/$repo/releases")
            val isPrerelease = json.optBoolean("prerelease", false)

            var downloadUrl = htmlUrl
            val assets = json.optJSONArray("assets")
            if (assets != null && assets.length() > 0) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val assetName = asset.optString("name", "")
                    if (assetName.endsWith(".apk", ignoreCase = true)) {
                        downloadUrl = asset.optString("browser_download_url", downloadUrl)
                        break
                    }
                }
            }

            val formattedDate = formatPublishedDate(rawDate)
            val hasNewer = isNewerVersion(tagName, currentVersionCode)

            val release = AppRelease(
                tagName = tagName,
                name = if (name.isNotBlank()) name else tagName,
                body = body,
                publishedAt = formattedDate,
                htmlUrl = htmlUrl,
                downloadUrl = downloadUrl,
                isPrerelease = isPrerelease,
                hasNewerVersion = hasNewer
            )

            val resultState = if (hasNewer) {
                UpdateCheckState.UpdateAvailable(release)
            } else {
                UpdateCheckState.UpToDate(currentVersion)
            }

            _updateState.value = resultState
            return@withContext resultState
        } catch (e: Exception) {
            Log.e("AppUpdateManager", "Failed to check update for $repo", e)
            val error = UpdateCheckState.Error(e.localizedMessage ?: "Failed to connect to GitHub.")
            _updateState.value = error
            return@withContext error
        }
    }

    private fun isNewerVersion(remoteTag: String, currentVer: String): Boolean {
        try {
            val cleanRemote = remoteTag.removePrefix("v").removePrefix("V").trim()
            val cleanCurrent = currentVer.removePrefix("v").removePrefix("V").trim()

            val remoteParts = cleanRemote.split("-", "_", "+")[0].split(".").mapNotNull { it.toIntOrNull() }
            val currentParts = cleanCurrent.split("-", "_", "+")[0].split(".").mapNotNull { it.toIntOrNull() }

            val maxLen = maxOf(remoteParts.size, currentParts.size)
            for (i in 0 until maxLen) {
                val r = remoteParts.getOrElse(i) { 0 }
                val c = currentParts.getOrElse(i) { 0 }
                if (r > c) return true
                if (r < c) return false
            }

            // If parsed numeric parts are equal, check if cleanRemote is different from cleanCurrent
            return cleanRemote != cleanCurrent && cleanRemote > cleanCurrent
        } catch (e: Exception) {
            return remoteTag.isNotBlank() && !remoteTag.equals(currentVersion, ignoreCase = true)
        }
    }

    private fun formatPublishedDate(raw: String): String {
        if (raw.isBlank()) return "Recently"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val date = parser.parse(raw)
            if (date != null) {
                val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                formatter.format(date)
            } else {
                raw.take(10)
            }
        } catch (e: Exception) {
            raw.take(10)
        }
    }

    fun openUpdateUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    companion object {
        @Volatile
        private var INSTANCE: AppUpdateManager? = null

        fun getInstance(context: Context): AppUpdateManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AppUpdateManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
