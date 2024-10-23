package com.restugedepurnama.event.data.remote.retrofit

import com.restugedepurnama.event.data.remote.response.DetailEventResponse
import com.restugedepurnama.event.data.remote.response.EventActiveResponse
import com.restugedepurnama.event.data.remote.response.SearchEventResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("events")
    suspend fun getEvents(@Query("active") active: Int): EventActiveResponse

    @GET("events/{id}")
    fun getEventDetailById(@Path("id") id: String): Call<DetailEventResponse>

    @GET("events")
    fun getEventsByName(@Query("active") active: Int, @Query("q") query: String): Call<SearchEventResponse>

}