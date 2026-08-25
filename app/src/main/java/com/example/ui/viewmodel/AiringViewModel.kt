package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiringScheduleItem
import com.example.data.model.UserMediaEntry
import com.example.data.repository.AniListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

data class AiringUiState(
    val selectedDay: CalendarDay = CalendarDay.TODAY,
    val isLoading: Boolean = false,
    val scheduleItems: List<AiringScheduleItem> = emptyList(),
    val currentDateFormatted: String = "",
    val errorMessage: String? = null
)

class AiringViewModel(
    private val repository: AniListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiringUiState())
    val uiState: StateFlow<AiringUiState> = _uiState.asStateFlow()

    init {
        selectDay(CalendarDay.TODAY)
    }

    fun selectDay(day: CalendarDay) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
        loadScheduleForDay(day)
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

            val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
            val dateStr = sdf.format(Date(startTimeSec * 1000))

            val result = repository.getAiringSchedule(startTimeSec, endTimeSec)
            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    scheduleItems = list.sortedBy { it.airingAt },
                    currentDateFormatted = dateStr
                )
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

class AiringViewModelFactory(private val repository: AniListRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AiringViewModel(repository) as T
    }
}
