package com.restugedepurnama.event.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.restugedepurnama.event.data.local.entity.EventEntity
import com.restugedepurnama.event.data.local.room.EventDao
import com.restugedepurnama.event.data.remote.retrofit.ApiService

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao
){
    fun getUpcomingEvent(): LiveData<Result<List<EventEntity>>> = fetchEvents(1, true)

    fun getFinishedEvent(): LiveData<Result<List<EventEntity>>> = fetchEvents(0, false)

    private fun fetchEvents(eventType: Int, isActive: Boolean): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(eventType)
            val events = response.listEvents

            val eventList = events.map { event ->
                val isFavorite = eventDao.isEventFavorite(event.id)

                EventEntity(
                    event.id,
                    event.name,
                    event.summary,
                    event.description,
                    event.imageLogo,
                    event.mediaCover,
                    event.category,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.registrants,
                    event.beginTime,
                    event.endTime,
                    event.link,
                    isFavorite,
                    isActive
                )
            }
            if (isActive) {
                eventDao.deleteActiveEvents()
            } else {
                eventDao.deleteInactiveEvents()
            }
            eventDao.insertEvent(eventList)
            emit(Result.Success(eventList))

        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> = if (isActive) {
            eventDao.getUpcomingEvent().map {
                Result.Success(it) }
        } else {
            eventDao.getFinishedEvent().map { Result.Success(it) }
        }
        emitSource(localData)
    }

    fun getFavoriteEvent(): LiveData<List<EventEntity>> {
        return eventDao.getFavoriteEvent()
    }

    suspend fun setEventFavorite(event: EventEntity, favoriteState: Boolean) {
        event.favorite = favoriteState
        eventDao.updateEvent(event)
    }

    fun searchEvent(active: Int, query: String): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val localEvent = eventDao.searchEvent(active, query)

            emitSource(localEvent.map { listEvent ->
                if (listEvent.isEmpty()) {
                    Result.Error("No data found")
                } else {
                    Result.Success(listEvent)
                }
            })
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getEvent(id: String): LiveData<Result<EventEntity>> = liveData {
        emit(Result.Loading)
        try {
            val localEvent = eventDao.getEventById(id)

            if (localEvent != null) {
                emit(Result.Success(localEvent))
            } else {
                emit(Result.Error("Event not found"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getNearestActiveEvent(): EventEntity? {
        val currentTime = System.currentTimeMillis()
        val nearestEvent = eventDao.getNearestActiveEvent(currentTime)
        return nearestEvent
    }

    suspend fun setFavoriteEvent(event: EventEntity, favoriteState: Boolean) {
        event.favorite = favoriteState
        eventDao.updateEvent(event)
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(apiService: ApiService, eventDao: EventDao): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao)
            }
    }
}