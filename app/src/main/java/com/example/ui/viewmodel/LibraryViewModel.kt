package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.LibraryStats
import com.example.data.model.UserMediaEntry
import com.example.data.repository.AniListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LibrarySort(val displayName: String) {
    TITLE("Title (A-Z)"),
    SCORE("Score"),
    PROGRESS("Progress"),
    UPDATED("Recently Updated")
}

data class LibraryFilter(
    val mediaType: String = "ALL", // ALL, ANIME, MANGA
    val status: String = "WATCHING", // Changed default to WATCHING
    val searchQuery: String = "",
    val sort: LibrarySort = LibrarySort.TITLE,
    val hideCompletedInAll: Boolean = false,
    val isCleanModeEnabled: Boolean = false
)

class LibraryViewModel(
    application: Application,
    private val repository: AniListRepository
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("anichan_library_prefs", Context.MODE_PRIVATE)

    private val _filter = MutableStateFlow(
        LibraryFilter(
            hideCompletedInAll = prefs.getBoolean("hide_completed_in_all", false),
            isCleanModeEnabled = prefs.getBoolean("is_clean_mode_enabled", false)
        )
    )
    val filter: StateFlow<LibraryFilter> = _filter.asStateFlow()

    private val _allEntries = repository.getAllUserEntries()

    val filteredEntries: StateFlow<List<UserMediaEntry>> = combine(_allEntries, _filter) { entries, filter ->
        var list = entries

        // Library Cleaner: Hide entries not manually added in app if clean mode is ON
        if (filter.isCleanModeEnabled) {
            list = list.filter { it.isManuallyAdded }
        }

        // Type filter
        if (filter.mediaType != "ALL") {
            list = list.filter { it.type == filter.mediaType }
        }

        // Status / Favorite / Hide Completed filter
        if (filter.status == "FAVORITES") {
            list = list.filter { it.isFavorite }
        } else if (filter.status == "ALL") {
            if (filter.hideCompletedInAll) {
                list = list.filter { it.status != "COMPLETED" }
            }
        } else {
            list = list.filter { it.status == filter.status }
        }

        // Search query
        if (filter.searchQuery.isNotBlank()) {
            list = list.filter { it.title.contains(filter.searchQuery, ignoreCase = true) }
        }

        // Sorting
        when (filter.sort) {
            LibrarySort.TITLE -> list.sortedBy { it.title.lowercase() }
            LibrarySort.SCORE -> list.sortedWith(compareByDescending<UserMediaEntry> { it.score }.thenBy { it.title.lowercase() })
            LibrarySort.PROGRESS -> list.sortedWith(compareByDescending<UserMediaEntry> { it.progress }.thenBy { it.title.lowercase() })
            LibrarySort.UPDATED -> list.sortedByDescending { it.updatedAt }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stats: StateFlow<LibraryStats> = _allEntries.combine(_filter) { entries, _ ->
        val animeList = entries.filter { it.type == "ANIME" }
        val mangaList = entries.filter { it.type == "MANGA" }

        val totalEpisodes = animeList.sumOf { it.progress }
        val totalChapters = mangaList.sumOf { it.progress }

        val ratedEntries = entries.filter { it.score > 0f }
        val meanScore = if (ratedEntries.isNotEmpty()) {
            ratedEntries.map { it.score }.average().toFloat()
        } else {
            0f
        }

        val daysWatched = (totalEpisodes * 24f) / (60f * 24f)

        LibraryStats(
            totalAnime = animeList.size,
            totalManga = mangaList.size,
            animeWatching = animeList.count { it.status == "WATCHING" },
            animeCompleted = animeList.count { it.status == "COMPLETED" },
            animePlanning = animeList.count { it.status == "PLANNING" },
            totalEpisodesWatched = totalEpisodes,
            totalChaptersRead = totalChapters,
            meanScore = meanScore,
            estimatedDaysWatched = daysWatched
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryStats()
    )

    fun setMediaTypeFilter(type: String) {
        _filter.value = _filter.value.copy(mediaType = type)
    }

    fun setStatusFilter(status: String) {
        _filter.value = _filter.value.copy(status = status)
    }

    fun setSearchQuery(query: String) {
        _filter.value = _filter.value.copy(searchQuery = query)
    }

    fun setSort(sort: LibrarySort) {
        _filter.value = _filter.value.copy(sort = sort)
    }

    fun toggleHideCompleted() {
        val newVal = !_filter.value.hideCompletedInAll
        _filter.value = _filter.value.copy(hideCompletedInAll = newVal)
        prefs.edit().putBoolean("hide_completed_in_all", newVal).apply()
    }

    fun setHideCompleted(hide: Boolean) {
        _filter.value = _filter.value.copy(hideCompletedInAll = hide)
        prefs.edit().putBoolean("hide_completed_in_all", hide).apply()
    }

    fun setCleanModeEnabled(enabled: Boolean) {
        _filter.value = _filter.value.copy(isCleanModeEnabled = enabled)
        prefs.edit().putBoolean("is_clean_mode_enabled", enabled).apply()
    }

    fun incrementProgress(entry: UserMediaEntry) {
        viewModelScope.launch {
            val total = if (entry.type == "MANGA") entry.totalChapters else entry.totalEpisodes
            val max = total ?: 9999
            if (entry.progress < max) {
                val newProgress = entry.progress + 1
                val newStatus = if (total != null && newProgress >= total) "COMPLETED" else entry.status
                val updatedEntry = entry.copy(
                    progress = newProgress,
                    status = newStatus,
                    isManuallyAdded = true,
                    updatedAt = System.currentTimeMillis()
                )
                repository.saveUserEntry(updatedEntry)
            }
        }
    }

    fun decrementProgress(entry: UserMediaEntry) {
        viewModelScope.launch {
            if (entry.progress > 0) {
                val updatedEntry = entry.copy(
                    progress = entry.progress - 1,
                    isManuallyAdded = true,
                    updatedAt = System.currentTimeMillis()
                )
                repository.saveUserEntry(updatedEntry)
            }
        }
    }

    fun toggleFavorite(entry: UserMediaEntry) {
        viewModelScope.launch {
            val updated = entry.copy(
                isFavorite = !entry.isFavorite,
                isManuallyAdded = true,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveUserEntry(updated)
        }
    }

    fun saveEntry(entry: UserMediaEntry) {
        viewModelScope.launch {
            repository.saveUserEntry(entry)
        }
    }

    fun deleteEntry(mediaId: Int) {
        viewModelScope.launch {
            repository.deleteUserEntry(mediaId)
        }
    }
}

class LibraryViewModelFactory(
    private val application: Application,
    private val repository: AniListRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(application, repository) as T
    }
}
