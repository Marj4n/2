package com.example.foodxyz

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://103.67.187.184/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}