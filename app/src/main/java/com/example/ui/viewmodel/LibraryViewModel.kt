package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.LibraryStats
import com.example.data.model.MediaType
import com.example.data.model.UserMediaEntry
import com.example.data.model.UserWatchStatus
import com.example.data.repository.AniListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LibrarySort(val displayName: String) {
    UPDATED("Last Updated"),
    SCORE("Score"),
    TITLE("Title"),
    PROGRESS("Progress")
}

data class LibraryFilter(
    val mediaType: String = "ALL", // ALL, ANIME, MANGA
    val status: String = "ALL", // ALL, WATCHING, COMPLETED, PLANNING, PAUSED, DROPPED, REWATCHING, FAVORITES
    val searchQuery: String = "",
    val sort: LibrarySort = LibrarySort.UPDATED
)

class LibraryViewModel(
    private val repository: AniListRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(LibraryFilter())
    val filter: StateFlow<LibraryFilter> = _filter.asStateFlow()

    private val _allEntries = repository.getAllUserEntries()

    val filteredEntries: StateFlow<List<UserMediaEntry>> = combine(_allEntries, _filter) { entries, filter ->
        var list = entries

        // Type filter
        if (filter.mediaType != "ALL") {
            list = list.filter { it.type == filter.mediaType }
        }

        // Status / Favorite filter
        if (filter.status == "FAVORITES") {
            list = list.filter { it.isFavorite }
        } else if (filter.status != "ALL") {
            list = list.filter { it.status == filter.status }
        }

        // Search query
        if (filter.searchQuery.isNotBlank()) {
            list = list.filter { it.title.contains(filter.searchQuery, ignoreCase = true) }
        }

        // Sorting
        when (filter.sort) {
            LibrarySort.UPDATED -> list.sortedByDescending { it.updatedAt }
            LibrarySort.SCORE -> list.sortedByDescending { it.score }
            LibrarySort.TITLE -> list.sortedBy { it.title }
            LibrarySort.PROGRESS -> list.sortedByDescending { it.progress }
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

        // 24 minutes per episode estimate
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

class LibraryViewModelFactory(private val repository: AniListRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(repository) as T
    }
}
