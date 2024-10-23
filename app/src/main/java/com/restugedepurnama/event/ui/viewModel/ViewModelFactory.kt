package com.restugedepurnama.event.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.restugedepurnama.event.data.EventRepository
import com.restugedepurnama.event.di.Injection
import com.restugedepurnama.event.settings.SettingPreferences

class ViewModelFactory private constructor(
    private val pref: SettingPreferences,
    private val eventRepository: EventRepository,
    private val workManager: androidx.work.WorkManager
)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref, eventRepository, workManager) as T
            }

            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context, pref: SettingPreferences): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(pref, Injection.providedRepository(context), Injection.provideWorkManager(context)).apply {
                    instance = this
                }
            }.also { instance = it }
    }
}