package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.UserMediaEntry
import com.example.data.repository.AniListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExploreUiState(
    val isLoading: Boolean = true,
    val heroMedia: List<MediaItem> = emptyList(),
    val trendingAnime: List<MediaItem> = emptyList(),
    val seasonalAnime: List<MediaItem> = emptyList(),
    val topRatedAnime: List<MediaItem> = emptyList(),
    val trendingManga: List<MediaItem> = emptyList(),
    val selectedMediaType: MediaType = MediaType.ANIME,
    val currentSeasonName: String = "",
    val currentYear: Int = 2024,
    val errorMessage: String? = null
)

class ExploreViewModel(
    private val repository: AniListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        val season = AniListRepository.getCurrentSeason()
        val year = AniListRepository.getCurrentYear()
        _uiState.value = _uiState.value.copy(
            currentSeasonName = season,
            currentYear = year
        )
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Load sections in parallel to improve performance on slow data
            launch {
                val result = repository.getTrending(type = MediaType.ANIME, page = 1, perPage = 10)
                val items = result.getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(
                    trendingAnime = items,
                    heroMedia = (items.take(5) + _uiState.value.seasonalAnime.take(3)).distinctBy { it.id },
                    isLoading = false // Show content as soon as trending is ready
                )
            }

            launch {
                val result = repository.getSeasonal(
                    season = _uiState.value.currentSeasonName,
                    year = _uiState.value.currentYear,
                    page = 1,
                    perPage = 10
                )
                val items = result.getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(
                    seasonalAnime = items,
                    heroMedia = (_uiState.value.trendingAnime.take(5) + items.take(3)).distinctBy { it.id }
                )
            }

            launch {
                val result = repository.getPopular(type = MediaType.ANIME, page = 1, perPage = 10)
                _uiState.value = _uiState.value.copy(topRatedAnime = result.getOrDefault(emptyList()))
            }

            launch {
                val result = repository.getTrending(type = MediaType.MANGA, page = 1, perPage = 10)
                _uiState.value = _uiState.value.copy(trendingManga = result.getOrDefault(emptyList()))
            }
        }
    }

    fun setMediaType(type: MediaType) {
        _uiState.value = _uiState.value.copy(selectedMediaType = type)
    }

    fun saveUserEntry(entry: UserMediaEntry) {
        viewModelScope.launch {
            repository.saveUserEntry(entry)
        }
    }
}

class ExploreViewModelFactory(private val repository: AniListRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExploreViewModel(repository) as T
    }
}
