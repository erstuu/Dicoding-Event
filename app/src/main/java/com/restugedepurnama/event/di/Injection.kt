package com.restugedepurnama.event.di

import android.content.Context
import com.restugedepurnama.event.data.EventRepository
import com.restugedepurnama.event.data.local.room.EventDatabase
import com.restugedepurnama.event.data.remote.retrofit.ApiConfig

object Injection {
    fun providedRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()

        return EventRepository.getInstance(apiService, dao)
    }

    fun provideWorkManager(context: Context): androidx.work.WorkManager {
        return androidx.work.WorkManager.getInstance(context)
    }
}