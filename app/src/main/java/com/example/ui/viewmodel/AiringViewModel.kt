package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiringScheduleItem
import com.example.data.model.UserMediaEntry
import com.example.data.notification.EpisodeNotificationManager
import com.example.data.repository.AniListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class CalendarDay(val dayIndex: Int, val shortName: String, val fullName: String) {
    TODAY(0, "Today", "Today"),
    MONDAY(Calendar.MONDAY, "Mon", "Monday"),
    TUESDAY(Calendar.TUESDAY, "Tue", "Tuesday"),
    WEDNESDAY(Calendar.WEDNESDAY, "Wed", "Wednesday"),
    THURSDAY(Calendar.THURSDAY, "Thu", "Thursday"),
    FRIDAY(Calendar.FRIDAY, "Fri", "Friday"),
    SATURDAY(Calendar.SATURDAY, "Sat", "Saturday"),
    SUNDAY(Calendar.SUNDAY, "Sun", "Sunday")
}

data class AiringItemWithUserData(
    val schedule: AiringScheduleItem,
    val userEntry: UserMediaEntry?,
    val timeSlot: String // "Airing Soon", "Morning (12am-12pm)", "Afternoon (12pm-6pm)", "Evening (6pm-12am)"
)

data class AiringUiState(
    val selectedDay: CalendarDay = CalendarDay.TODAY,
    val isLoading: Boolean = false,
    val rawScheduleItems: List<AiringScheduleItem> = emptyList(),
    val currentDateFormatted: String = "",
    val errorMessage: String? = null,
    val filterWatchlistOnly: Boolean = false,
    val searchQuery: String = ""
)

class AiringViewModel(
    application: Application,
    private val repository: AniListRepository
) : AndroidViewModel(application) {

    private val notificationManager = EpisodeNotificationManager.getInstance(application)
    private val _uiState = MutableStateFlow(AiringUiState())
    val uiState: StateFlow<AiringUiState> = _uiState.asStateFlow()

    private val _userEntries = repository.getAllUserEntries()

    val displayItems: StateFlow<List<AiringItemWithUserData>> = combine(
        _uiState,
        _userEntries
    ) { state, userList ->
        val userMap = userList.associateBy { it.mediaId }
        val currentTimeSec = System.currentTimeMillis() / 1000

        var items = state.rawScheduleItems.map { schedule ->
            val userEntry = userMap[schedule.media.id]
            val timeUntil = schedule.airingAt - currentTimeSec
            val timeSlot = when {
                timeUntil in 0..7200 -> "Airing Soon"
                timeUntil < 0 -> "Aired"
                else -> {
                    val cal = Calendar.getInstance().apply { timeInMillis = schedule.airingAt * 1000 }
                    val hour = cal.get(Calendar.HOUR_OF_DAY)
                    when {
                        hour < 12 -> "Morning"
                        hour < 18 -> "Afternoon"
                        else -> "Evening & Night"
                    }
                }
            }
            AiringItemWithUserData(
                schedule = schedule,
                userEntry = userEntry,
                timeSlot = timeSlot
            )
        }

        if (state.filterWatchlistOnly) {
            items = items.filter { it.userEntry != null }
        }

        if (state.searchQuery.isNotBlank()) {
            items = items.filter {
                it.schedule.media.displayTitle.contains(state.searchQuery, ignoreCase = true) ||
                        it.schedule.media.genres.any { g -> g.contains(state.searchQuery, ignoreCase = true) }
            }
        }

        items
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        selectDay(CalendarDay.TODAY)
    }

    fun selectDay(day: CalendarDay) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
        loadScheduleForDay(day)
    }

    fun toggleWatchlistOnly() {
        _uiState.value = _uiState.value.copy(filterWatchlistOnly = !_uiState.value.filterWatchlistOnly)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    private fun loadScheduleForDay(day: CalendarDay) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val cal = Calendar.getInstance()
            if (day != CalendarDay.TODAY) {
                val currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                val targetDayOfWeek = day.dayIndex
                val diff = targetDayOfWeek - currentDayOfWeek
                cal.add(Calendar.DAY_OF_YEAR, diff)
            }

            // Start of day
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val startTimeSec = cal.timeInMillis / 1000

            // End of day
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            val endTimeSec = cal.timeInMillis / 1000

            val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            val dateStr = sdf.format(Date(startTimeSec * 1000))

            val result = repository.getAiringSchedule(startTimeSec, endTimeSec)
            result.onSuccess { list ->
                val sorted = list.sortedBy { it.airingAt }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    rawScheduleItems = sorted,
                    currentDateFormatted = dateStr
                )

                // Trigger notification checking in background
                viewModelScope.launch {
                    try {
                        val entries = repository.getAllUserEntriesList()
                        notificationManager.checkAndNotifyUpcomingEpisodes(sorted, entries)
                    } catch (_: Exception) {}
                }
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message,
                    currentDateFormatted = dateStr
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

class AiringViewModelFactory(
    private val application: Application,
    private val repository: AniListRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AiringViewModel(application, repository) as T
    }
}
