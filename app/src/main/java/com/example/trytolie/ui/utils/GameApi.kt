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


interface GameAPI {
    @GET("/api/v1/game/{id}")
    suspend fun get(@Header("Authorization") token: String, @Path("id") id: String): retrofit2.Response<JsonObject>

    @POST("/api/v1/game/{id}")
    suspend fun create(@Header("Authorization") token: String, @Path("id") id: String): retrofit2.Response<JsonObject>

    @PUT("/api/v1/game/{id}")
    suspend fun update(@Header("Authorization") token: String, @Path("id") id: String, @Body body: String): retrofit2.Response<JsonObject>

    @DELETE("/api/v1/game/{id}")
    suspend fun delete(@Header("Authorization") token: String, @Path("id") id: String): retrofit2.Response<JsonObject>
}


object HelperGameAPI {
    fun getInstance() : GameAPI {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GameAPI::class.java)
    }
}