package com.example.foodxyz

import LoginResponse
import LogoutResponse
import com.example.foodxyz.request.RegisterRequest
import com.example.foodxyz.request.LoginRequest
import com.example.foodxyz.request.TransactionRequest
import com.example.foodxyz.response.RegisterResponse
import com.example.foodxyz.response.ProductResponse
import com.example.foodxyz.response.TransactionResponse
import com.example.foodxyz.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface API {
    @POST("login")
    @Headers("Accept: application/json")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("user")
    @Headers("Accept: application/json")
    suspend fun whoami(@Header("authorization") auth: String): Response<UserResponse>

    @POST("register")
    @Headers("Accept: application/json")
    suspend fun daftar(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("products")
    @Headers("Accept: application/json")
    suspend fun products(@Header("authorization") auth: String): Response<ProductResponse>

    @POST("store-invoice")
    @Headers("Accept: application/json")
    suspend fun transaction(
        @Header("authorization") auth: String,
        @Body transactionRequest: TransactionRequest
    ): Response<TransactionResponse>

    @GET("logout")
    @Headers("Accept: application/json")
    suspend fun logout(@Header("authorization") auth: String): Response<LogoutResponse>
}