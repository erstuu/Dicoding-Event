package com.restugedepurnama.event.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import com.restugedepurnama.event.data.EventRepository
import com.restugedepurnama.event.data.local.entity.EventEntity
import com.restugedepurnama.event.settings.DailyReminderWorker
import com.restugedepurnama.event.settings.SettingPreferences
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val pref: SettingPreferences,
    private val eventRepository: EventRepository,
    private val workManager: androidx.work.WorkManager
) : ViewModel() {

    private val _isReminderEnabled = MutableLiveData<Boolean>()
    val isReminderEnabled: LiveData<Boolean> = _isReminderEnabled

    init {
        viewModelScope.launch {
            pref.getReminderSetting().collect {
                _isReminderEnabled.value = it
            }
        }
    }

    fun finishedEvent() = eventRepository.getFinishedEvent()

    fun activeEvent() = eventRepository.getUpcomingEvent()

    fun getDetailEvent(id: String) = eventRepository.getEvent(id)

    fun searchEvent(active: Int, query: String) = eventRepository.searchEvent(active, query)

    fun getFavoriteEvent() = eventRepository.getFavoriteEvent()

    fun saveEvent(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setEventFavorite(event, true)
        }
    }

    fun updateFavoriteStatus(event: EventEntity, favoriteState: Boolean) {
        viewModelScope.launch {
            setFavoriteEvent(event, favoriteState)
        }
    }

    private suspend fun setFavoriteEvent(event: EventEntity, favoriteState: Boolean) {
        eventRepository.setFavoriteEvent(event, favoriteState)
    }

    fun getThemeSetting(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun toggleReminder(enabled: Boolean) {
        viewModelScope.launch {
            pref.saveReminderSetting(enabled)
            _isReminderEnabled.value = enabled
            if (enabled) {
                scheduleReminder()
            } else {
                cancelReminder()
            }
        }
    }

    private fun scheduleReminder() {
        val reminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderRequest
        )
    }

    private fun cancelReminder() {
        workManager.cancelUniqueWork(REMINDER_WORK_NAME)
    }

    companion object {
        const val REMINDER_WORK_NAME = "Daily_Reminder_Worker"
    }

}