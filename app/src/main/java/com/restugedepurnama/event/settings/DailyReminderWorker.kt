package com.restugedepurnama.event.settings

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.restugedepurnama.event.data.EventRepository
import com.restugedepurnama.event.data.local.entity.EventEntity
import com.restugedepurnama.event.data.local.room.EventDatabase
import com.restugedepurnama.event.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.coroutineScope

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val repository = EventRepository.getInstance(
                ApiConfig.getApiService(),
                EventDatabase.getInstance(applicationContext).eventDao()
            )
            val nearestEvent = repository.getNearestActiveEvent()

            if (nearestEvent != null) {
                showNotification(nearestEvent)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(event: EventEntity) {
        val notificationHelper = WorkerHelper(applicationContext)
        notificationHelper.showNotification(event)
    }
}