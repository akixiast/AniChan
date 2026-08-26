package com.example.data.account

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.local.UserMediaDao
import com.example.data.model.AniListUserProfile
import com.example.data.model.AuthType
import com.example.data.model.UserMediaEntry
import com.example.data.model.UserWatchStatus
import com.example.data.remote.AniListApiService
import com.example.data.remote.AniListGraphQL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

data class AniListAccountState(
    val isConnected: Boolean = false,
    val authType: AuthType = AuthType.NONE,
    val token: String? = null,
    val userId: Int? = null,
    val userName: String? = null,
    val avatarUrl: String? = null,
    val bannerUrl: String? = null,
    val animeCount: Int = 0,
    val episodesWatched: Int = 0,
    val minutesWatched: Int = 0,
    val animeMeanScore: Float = 0f,
    val mangaCount: Int = 0,
    val chaptersRead: Int = 0,
    val volumesRead: Int = 0,
    val mangaMeanScore: Float = 0f,
    val lastSyncTime: Long = 0L,
    val isSyncing: Boolean = false,
    val syncError: String? = null,
    val lastSyncCount: Int = 0
)

class AniListAccountManager(
    context: Context,
    private val apiService: AniListApiService,
    private val userMediaDao: UserMediaDao
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("anichan_account_prefs", Context.MODE_PRIVATE)

    private val _accountState = MutableStateFlow(loadState())
    val accountState: StateFlow<AniListAccountState> = _accountState.asStateFlow()

    private fun loadState(): AniListAccountState {
        val isConnected = prefs.getBoolean("is_connected", false)
        val authTypeName = prefs.getString("auth_type", AuthType.NONE.name)
        val authType = try {
            AuthType.valueOf(authTypeName ?: AuthType.NONE.name)
        } catch (e: Exception) {
            AuthType.NONE
        }
        val token = prefs.getString("token", null)
        val userId = if (prefs.contains("user_id")) prefs.getInt("user_id", 0) else null
        val userName = prefs.getString("user_name", null)
        val avatarUrl = prefs.getString("avatar_url", null)
        val bannerUrl = prefs.getString("banner_url", null)
        val animeCount = prefs.getInt("anime_count", 0)
        val episodesWatched = prefs.getInt("episodes_watched", 0)
        val minutesWatched = prefs.getInt("minutes_watched", 0)
        val animeMeanScore = prefs.getFloat("anime_mean_score", 0f)
        val mangaCount = prefs.getInt("manga_count", 0)
        val chaptersRead = prefs.getInt("chapters_read", 0)
        val volumesRead = prefs.getInt("volumes_read", 0)
        val mangaMeanScore = prefs.getFloat("manga_mean_score", 0f)
        val lastSyncTime = prefs.getLong("last_sync_time", 0L)
        val lastSyncCount = prefs.getInt("last_sync_count", 0)

        return AniListAccountState(
            isConnected = isConnected && (!userName.isNullOrBlank() || !token.isNullOrBlank()),
            authType = authType,
            token = token,
            userId = userId,
            userName = userName,
            avatarUrl = avatarUrl,
            bannerUrl = bannerUrl,
            animeCount = animeCount,
            episodesWatched = episodesWatched,
            minutesWatched = minutesWatched,
            animeMeanScore = animeMeanScore,
            mangaCount = mangaCount,
            chaptersRead = chaptersRead,
            volumesRead = volumesRead,
            mangaMeanScore = mangaMeanScore,
            lastSyncTime = lastSyncTime,
            lastSyncCount = lastSyncCount
        )
    }

    suspend fun loginWithEmailOrUsername(input: String): Result<AniListUserProfile> = withContext(Dispatchers.IO) {
        val cleanInput = input.trim()
        if (cleanInput.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Email or username cannot be empty"))
        }

        try {
            _accountState.value = _accountState.value.copy(isSyncing = true, syncError = null)
            
            // If it's an email format, first try the username prefix before '@', then full input
            val candidates = if (cleanInput.contains("@")) {
                listOf(cleanInput.substringBefore("@").trim(), cleanInput)
            } else {
                listOf(cleanInput)
            }

            var matchedProfile: AniListUserProfile? = null

            for (candidate in candidates) {
                if (candidate.isBlank()) continue

                // 1. Try exact username match
                try {
                    val json = apiService.executeGraphQL(
                        query = AniListGraphQL.USER_BY_NAME_QUERY,
                        variables = mapOf("userName" to candidate)
                    )
                    val profile = AniListGraphQL.parseUserProfile(json)
                    if (profile != null && profile.id > 0) {
                        matchedProfile = profile
                        break
                    }
                } catch (_: Exception) {}

                // 2. Try user search query
                try {
                    val json = apiService.executeGraphQL(
                        query = AniListGraphQL.USER_SEARCH_QUERY,
                        variables = mapOf("search" to candidate)
                    )
                    val profile = AniListGraphQL.parseUserProfile(json)
                    if (profile != null && profile.id > 0) {
                        matchedProfile = profile
                        break
                    }
                } catch (_: Exception) {}
            }

            val profile = matchedProfile ?: return@withContext Result.failure(
                Exception("Could not find AniList user '$cleanInput'. Please verify your AniList username or use 'Sign in with AniList Email' below.")
            )

            saveProfile(profile, AuthType.USERNAME_SYNC, token = null)
            
            // Sync watchlist immediately
            syncWatchlistInternal(profile.id, profile.name, token = null)

            Result.success(profile)
        } catch (e: Exception) {
            Log.e("AniListAccountManager", "Login with email/username failed", e)
            _accountState.value = _accountState.value.copy(
                isSyncing = false,
                syncError = e.message ?: "Failed to connect to AniList"
            )
            Result.failure(e)
        }
    }

    suspend fun loginWithUsername(userName: String): Result<AniListUserProfile> = loginWithEmailOrUsername(userName)

    suspend fun loginWithToken(tokenInput: String): Result<AniListUserProfile> = withContext(Dispatchers.IO) {
        val cleanToken = tokenInput.trim()
        if (cleanToken.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Token cannot be empty"))
        }

        try {
            _accountState.value = _accountState.value.copy(isSyncing = true, syncError = null)
            val json = apiService.executeGraphQL(
                query = AniListGraphQL.VIEWER_QUERY,
                variables = emptyMap(),
                authToken = cleanToken
            )
            val profile = AniListGraphQL.parseUserProfile(json)
                ?: return@withContext Result.failure(Exception("Invalid AniList token or authorization failed"))

            saveProfile(profile, AuthType.TOKEN_AUTH, token = cleanToken)

            // Sync watchlist immediately
            syncWatchlistInternal(profile.id, profile.name, token = cleanToken)

            Result.success(profile)
        } catch (e: Exception) {
            Log.e("AniListAccountManager", "Login with token failed", e)
            _accountState.value = _accountState.value.copy(
                isSyncing = false,
                syncError = e.message ?: "Failed to authenticate with AniList token"
            )
            Result.failure(e)
        }
    }

    private fun saveProfile(profile: AniListUserProfile, authType: AuthType, token: String?) {
        prefs.edit()
            .putBoolean("is_connected", true)
            .putString("auth_type", authType.name)
            .putString("token", token)
            .putInt("user_id", profile.id)
            .putString("user_name", profile.name)
            .putString("avatar_url", profile.avatarUrl)
            .putString("banner_url", profile.bannerUrl)
            .putInt("anime_count", profile.animeCount)
            .putInt("episodes_watched", profile.episodesWatched)
            .putInt("minutes_watched", profile.minutesWatched)
            .putFloat("anime_mean_score", profile.animeMeanScore)
            .putInt("manga_count", profile.mangaCount)
            .putInt("chapters_read", profile.chaptersRead)
            .putInt("volumes_read", profile.volumesRead)
            .putFloat("manga_mean_score", profile.mangaMeanScore)
            .apply()

        _accountState.value = _accountState.value.copy(
            isConnected = true,
            authType = authType,
            token = token,
            userId = profile.id,
            userName = profile.name,
            avatarUrl = profile.avatarUrl,
            bannerUrl = profile.bannerUrl,
            animeCount = profile.animeCount,
            episodesWatched = profile.episodesWatched,
            minutesWatched = profile.minutesWatched,
            animeMeanScore = profile.animeMeanScore,
            mangaCount = profile.mangaCount,
            chaptersRead = profile.chaptersRead,
            volumesRead = profile.volumesRead,
            mangaMeanScore = profile.mangaMeanScore,
            syncError = null
        )
    }

    suspend fun syncWatchlist(): Result<Int> = withContext(Dispatchers.IO) {
        val state = _accountState.value
        if (!state.isConnected) {
            return@withContext Result.failure(IllegalStateException("No AniList account connected"))
        }

        try {
            _accountState.value = _accountState.value.copy(isSyncing = true, syncError = null)
            val count = syncWatchlistInternal(state.userId, state.userName, state.token)
            Result.success(count)
        } catch (e: Exception) {
            Log.e("AniListAccountManager", "Sync watchlist failed", e)
            _accountState.value = _accountState.value.copy(
                isSyncing = false,
                syncError = e.message ?: "Failed to sync watchlist"
            )
            Result.failure(e)
        }
    }

    private suspend fun syncWatchlistInternal(userId: Int?, userName: String?, token: String?): Int {
        val allEntries = mutableListOf<UserMediaEntry>()

        // 1. Fetch Anime List
        val animeVars = mutableMapOf<String, Any?>("type" to "ANIME")
        if (userId != null && userId > 0) {
            animeVars["userId"] = userId
        } else if (!userName.isNullOrBlank()) {
            animeVars["userName"] = userName
        }

        try {
            val animeJson = apiService.executeGraphQL(
                query = AniListGraphQL.MEDIA_LIST_COLLECTION_QUERY,
                variables = animeVars,
                authToken = token
            )
            val animeEntries = AniListGraphQL.parseUserMediaList(animeJson, defaultType = "ANIME")
            allEntries.addAll(animeEntries)
        } catch (e: Exception) {
            Log.e("AniListAccountManager", "Failed to fetch anime list", e)
        }

        // 2. Fetch Manga List
        val mangaVars = mutableMapOf<String, Any?>("type" to "MANGA")
        if (userId != null && userId > 0) {
            mangaVars["userId"] = userId
        } else if (!userName.isNullOrBlank()) {
            mangaVars["userName"] = userName
        }

        try {
            val mangaJson = apiService.executeGraphQL(
                query = AniListGraphQL.MEDIA_LIST_COLLECTION_QUERY,
                variables = mangaVars,
                authToken = token
            )
            val mangaEntries = AniListGraphQL.parseUserMediaList(mangaJson, defaultType = "MANGA")
            allEntries.addAll(mangaEntries)
        } catch (e: Exception) {
            Log.e("AniListAccountManager", "Failed to fetch manga list", e)
        }

        // 3. Merge with existing entries to preserve local flags like isManuallyAdded
        if (allEntries.isNotEmpty()) {
            val existingEntries = userMediaDao.getAllEntriesDirect().associateBy { it.mediaId }
            val mergedEntries = allEntries.map { newEntry ->
                val existing = existingEntries[newEntry.mediaId]
                if (existing != null) {
                    newEntry.copy(
                        isManuallyAdded = existing.isManuallyAdded,
                        isFavorite = existing.isFavorite
                    )
                } else {
                    newEntry
                }
            }
            userMediaDao.insertOrUpdateAll(mergedEntries)
        }

        // 4. Update Profile Statistics
        val now = System.currentTimeMillis()
        prefs.edit()
            .putLong("last_sync_time", now)
            .putInt("last_sync_count", allEntries.size)
            .apply()

        // Also fetch fresh user stats if possible
        try {
            val profileJson = if (!token.isNullOrBlank()) {
                apiService.executeGraphQL(AniListGraphQL.VIEWER_QUERY, emptyMap(), token)
            } else if (!userName.isNullOrBlank()) {
                apiService.executeGraphQL(AniListGraphQL.USER_BY_NAME_QUERY, mapOf("userName" to userName))
            } else null

            if (profileJson != null) {
                AniListGraphQL.parseUserProfile(profileJson)?.let { freshProfile ->
                    prefs.edit()
                        .putInt("anime_count", freshProfile.animeCount)
                        .putInt("episodes_watched", freshProfile.episodesWatched)
                        .putInt("minutes_watched", freshProfile.minutesWatched)
                        .putFloat("anime_mean_score", freshProfile.animeMeanScore)
                        .putInt("manga_count", freshProfile.mangaCount)
                        .putInt("chapters_read", freshProfile.chaptersRead)
                        .putInt("volumes_read", freshProfile.volumesRead)
                        .putFloat("manga_mean_score", freshProfile.mangaMeanScore)
                        .apply()

                    _accountState.value = _accountState.value.copy(
                        animeCount = freshProfile.animeCount,
                        episodesWatched = freshProfile.episodesWatched,
                        minutesWatched = freshProfile.minutesWatched,
                        animeMeanScore = freshProfile.animeMeanScore,
                        mangaCount = freshProfile.mangaCount,
                        chaptersRead = freshProfile.chaptersRead,
                        volumesRead = freshProfile.volumesRead,
                        mangaMeanScore = freshProfile.mangaMeanScore
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("AniListAccountManager", "Failed to refresh user stats", e)
        }

        _accountState.value = _accountState.value.copy(
            lastSyncTime = now,
            lastSyncCount = allEntries.size,
            isSyncing = false,
            syncError = null
        )

        return allEntries.size
    }

    suspend fun saveMediaToAniList(
        mediaId: Int,
        status: UserWatchStatus,
        progress: Int,
        progressVolumes: Int = 0,
        score: Float = 0f,
        notes: String = "",
        repeat: Int = 0
    ): Boolean = withContext(Dispatchers.IO) {
        val token = _accountState.value.token
        if (token.isNullOrBlank()) {
            return@withContext false
        }

        val anilistStatus = when (status) {
            UserWatchStatus.WATCHING -> "CURRENT"
            UserWatchStatus.COMPLETED -> "COMPLETED"
            UserWatchStatus.PLANNING -> "PLANNING"
            UserWatchStatus.PAUSED -> "PAUSED"
            UserWatchStatus.DROPPED -> "DROPPED"
            UserWatchStatus.REWATCHING -> "REPEATING"
        }

        try {
            val vars = mapOf(
                "mediaId" to mediaId,
                "status" to anilistStatus,
                "progress" to progress,
                "progressVolumes" to progressVolumes,
                "score" to score,
                "notes" to notes,
                "repeat" to repeat
            )
            apiService.executeGraphQL(
                query = AniListGraphQL.SAVE_MEDIA_LIST_ENTRY_MUTATION,
                variables = vars,
                authToken = token
            )
            true
        } catch (e: Exception) {
            Log.e("AniListAccountManager", "Failed to save entry to AniList", e)
            false
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _accountState.value = AniListAccountState()
    }

    companion object {
        @Volatile
        private var INSTANCE: AniListAccountManager? = null

        fun getInstance(
            context: Context,
            apiService: AniListApiService,
            userMediaDao: UserMediaDao
        ): AniListAccountManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AniListAccountManager(
                    context = context.applicationContext,
                    apiService = apiService,
                    userMediaDao = userMediaDao
                )
                INSTANCE = instance
                instance
            }
        }
    }
}
