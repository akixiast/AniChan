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
            try {
                val trendingResult = repository.getTrending(type = MediaType.ANIME, page = 1, perPage = 10)
                val seasonalResult = repository.getSeasonal(
                    season = _uiState.value.currentSeasonName,
                    year = _uiState.value.currentYear,
                    page = 1,
                    perPage = 10
                )
                val popularResult = repository.getPopular(type = MediaType.ANIME, page = 1, perPage = 10)
                val mangaResult = repository.getTrending(type = MediaType.MANGA, page = 1, perPage = 10)

                val trending = trendingResult.getOrDefault(emptyList())
                val seasonal = seasonalResult.getOrDefault(emptyList())
                val popular = popularResult.getOrDefault(emptyList())
                val manga = mangaResult.getOrDefault(emptyList())

                val heroList = (trending.take(5) + seasonal.take(3)).distinctBy { it.id }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    heroMedia = heroList,
                    trendingAnime = trending,
                    seasonalAnime = seasonal,
                    topRatedAnime = popular,
                    trendingManga = manga
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
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
