package com.example.foodxyz

import LoginResponse
import com.example.foodxyz.request.DaftarRequest
import com.example.foodxyz.request.LoginRequest
import com.example.foodxyz.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserAPI {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("whoami")
    suspend fun whoami(@Header("authorization") auth : String): Response<UserResponse>

    @POST("daftar")
    suspend fun daftar(@Body daftarRequest: DaftarRequest): Response<UserResponse>

    @GET("logout")
    suspend fun logout(@Header("authorization") auth : String): Response<UserResponse>
}