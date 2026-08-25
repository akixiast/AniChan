package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.UserMediaEntry
import com.example.data.repository.AniListRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val mediaType: MediaType = MediaType.ANIME,
    val selectedGenre: String? = null,
    val selectedSeason: String? = null,
    val selectedYear: Int? = null,
    val selectedFormat: String? = null,
    val selectedStatus: String? = null,
    val selectedSort: String = "POPULARITY_DESC",
    val isGridView: Boolean = true,
    val isLoading: Boolean = false,
    val results: List<MediaItem> = emptyList(),
    val totalCount: Int = 0,
    val errorMessage: String? = null
)

class SearchViewModel(
    private val repository: AniListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        performSearch()
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            performSearch()
        }
    }

    fun setMediaType(type: MediaType) {
        _uiState.value = _uiState.value.copy(
            mediaType = type,
            selectedFormat = null // Reset format filter when switching type
        )
        performSearch()
    }

    fun selectGenre(genre: String?) {
        val newGenre = if (_uiState.value.selectedGenre == genre) null else genre
        _uiState.value = _uiState.value.copy(selectedGenre = newGenre)
        performSearch()
    }

    fun selectSeason(season: String?) {
        val newSeason = if (_uiState.value.selectedSeason == season) null else season
        _uiState.value = _uiState.value.copy(selectedSeason = newSeason)
        performSearch()
    }

    fun selectYear(year: Int?) {
        val newYear = if (_uiState.value.selectedYear == year) null else year
        _uiState.value = _uiState.value.copy(selectedYear = newYear)
        performSearch()
    }

    fun selectFormat(format: String?) {
        val newFormat = if (_uiState.value.selectedFormat == format) null else format
        _uiState.value = _uiState.value.copy(selectedFormat = newFormat)
        performSearch()
    }

    fun selectStatus(status: String?) {
        val newStatus = if (_uiState.value.selectedStatus == status) null else status
        _uiState.value = _uiState.value.copy(selectedStatus = newStatus)
        performSearch()
    }

    fun selectSort(sort: String) {
        _uiState.value = _uiState.value.copy(selectedSort = sort)
        performSearch()
    }

    fun toggleLayoutMode() {
        _uiState.value = _uiState.value.copy(isGridView = !_uiState.value.isGridView)
    }

    fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            selectedGenre = null,
            selectedSeason = null,
            selectedYear = null,
            selectedFormat = null,
            selectedStatus = null,
            selectedSort = "POPULARITY_DESC"
        )
        performSearch()
    }

    fun performSearch() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val state = _uiState.value
            val result = repository.searchMedia(
                query = state.query.ifBlank { null },
                type = state.mediaType,
                genre = state.selectedGenre,
                season = state.selectedSeason,
                seasonYear = state.selectedYear,
                format = state.selectedFormat,
                status = state.selectedStatus,
                sort = state.selectedSort,
                page = 1,
                perPage = 30
            )

            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    results = list,
                    totalCount = list.size
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message
                )
            }
        }
    }

    fun saveUserEntry(entry: UserMediaEntry) {
        viewModelScope.launch {
            repository.saveUserEntry(entry)
        }
    }
}

class SearchViewModelFactory(private val repository: AniListRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(repository) as T
    }
}
