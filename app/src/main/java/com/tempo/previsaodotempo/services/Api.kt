package com.tempo.previsaodotempo.services

import com.tempo.previsaodotempo.model.Main
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
// 668c0a9b0bef1091eb4f67b19e4d4c3b


interface Api {
    @GET("weather")

    fun weatherMap(
        @Query("q") cityName: String,
        @Query("appid") api_key: String
    ): Call<Main>
}