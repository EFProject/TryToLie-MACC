package com.example.trytolie.ui.utils

import com.example.trytolie.BuildConfig
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface RoomAPI {
    @GET("/api/v1/room/{id}")
    suspend fun get(@Header("Authorization") token: String, @Path("id") id: String): retrofit2.Response<JsonObject>

    @POST("/api/v1/room")
    suspend fun create(@Header("Authorization") token: String, @Body body: String): retrofit2.Response<JsonObject>

    @PUT("/api/v1/room")
    suspend fun getFreeRoom(@Header("Authorization") token: String, @Body body: String): retrofit2.Response<JsonObject>

    @PUT("/api/v1/room/{id}")
    suspend fun updateGameState(@Header("Authorization") token: String, @Path("id") id: String, @Body body: String): retrofit2.Response<JsonObject>

    @DELETE("/api/v1/room/{id}")
    suspend fun delete(@Header("Authorization") token: String, @Path("id") id: String): retrofit2.Response<JsonObject>
}


object HelperRoomAPI {
    fun getInstance() : RoomAPI {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RoomAPI::class.java)
    }
}