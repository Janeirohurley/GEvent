package com.janeirohurley.gevent.data.api

import com.janeirohurley.gevent.data.model.AuthResponse
import com.janeirohurley.gevent.data.model.LoginRequest
import com.janeirohurley.gevent.data.model.RegisterRequest
import com.janeirohurley.gevent.data.model.User
import retrofit2.http.*

/**
 * API Service pour l'authentification
 */
interface AuthApiService {

    @POST("auth/login/")
    suspend fun login(@Body loginRequest: LoginRequest): AuthResponse

    @POST("auth/register/")
    suspend fun register(@Body registerRequest: RegisterRequest): AuthResponse

    @GET("auth/user/")
    suspend fun getUserProfile(): User

    @PUT("auth/user/")
    suspend fun updateUserProfile(@Body userProfile: User): User

    @POST("auth/logout/")
    suspend fun logout()
}
