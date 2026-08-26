package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.MediaItem
import com.example.data.model.UserMediaEntry
import com.example.data.repository.AniListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val media: MediaItem? = null,
    val isTrackingSheetOpen: Boolean = false,
    val errorMessage: String? = null
)

class DetailViewModel(
    private val repository: AniListRepository,
    private val mediaId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    val userEntry: StateFlow<UserMediaEntry?> = repository.getUserEntry(mediaId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        loadMediaDetails()
    }

    fun loadMediaDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.getMediaDetails(mediaId)
            result.onSuccess { item ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    media = item
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message
                )
            }
        }
    }

    fun setTrackingSheetOpen(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isTrackingSheetOpen = isOpen)
    }

    fun saveUserEntry(entry: UserMediaEntry) {
        viewModelScope.launch {
            repository.saveUserEntry(entry)
        }
    }

    fun quickIncrementProgress() {
        val entry = userEntry.value
        val media = uiState.value.media ?: return
        viewModelScope.launch {
            if (entry != null) {
                val total = if (entry.type == "MANGA") entry.totalChapters else entry.totalEpisodes
                val max = total ?: 9999
                if (entry.progress < max) {
                    val newProgress = entry.progress + 1
                    val newStatus = if (total != null && newProgress >= total) "COMPLETED" else entry.status
                    repository.saveUserEntry(
                        entry.copy(
                            progress = newProgress,
                            status = newStatus,
                            isManuallyAdded = true,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            } else {
                // Add to list and set progress to 1
                val newEntry = UserMediaEntry(
                    mediaId = media.id,
                    type = media.type.apiValue,
                    title = media.displayTitle,
                    coverImage = media.coverImageLarge.ifBlank { media.coverImageExtraLarge },
                    bannerImage = media.bannerImage,
                    totalEpisodes = media.episodes,
                    totalChapters = media.chapters,
                    progress = 1,
                    status = "WATCHING",
                    format = media.format,
                    genresCsv = media.genres.joinToString(","),
                    isManuallyAdded = true,
                    updatedAt = System.currentTimeMillis()
                )
                repository.saveUserEntry(newEntry)
            }
        }
    }

    fun quickDecrementProgress() {
        val entry = userEntry.value ?: return
        viewModelScope.launch {
            if (entry.progress > 0) {
                repository.saveUserEntry(
                    entry.copy(
                        progress = entry.progress - 1,
                        isManuallyAdded = true,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun toggleFavorite() {
        val entry = userEntry.value
        val media = uiState.value.media ?: return
        viewModelScope.launch {
            if (entry != null) {
                repository.saveUserEntry(
                    entry.copy(
                        isFavorite = !entry.isFavorite,
                        isManuallyAdded = true,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } else {
                val newEntry = UserMediaEntry(
                    mediaId = media.id,
                    type = media.type.apiValue,
                    title = media.displayTitle,
                    coverImage = media.coverImageLarge.ifBlank { media.coverImageExtraLarge },
                    bannerImage = media.bannerImage,
                    totalEpisodes = media.episodes,
                    totalChapters = media.chapters,
                    progress = 0,
                    status = "PLANNING",
                    isFavorite = true,
                    format = media.format,
                    genresCsv = media.genres.joinToString(","),
                    isManuallyAdded = true,
                    updatedAt = System.currentTimeMillis()
                )
                repository.saveUserEntry(newEntry)
            }
        }
    }

    fun deleteUserEntry() {
        viewModelScope.launch {
            repository.deleteUserEntry(mediaId)
        }
    }
}

class DetailViewModelFactory(
    private val repository: AniListRepository,
    private val mediaId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(repository, mediaId) as T
    }
}
