package com.restugedepurnama.event.data.remote.retrofit

import com.restugedepurnama.event.data.remote.response.EventActiveResponse
import retrofit2.http.*

interface ApiService {
    @GET("events")
    suspend fun getEvents(@Query("active") active: Int): EventActiveResponse

}