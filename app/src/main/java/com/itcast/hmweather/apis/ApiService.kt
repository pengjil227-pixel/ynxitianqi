package com.itcast.hmweather.apis

import com.itcast.hmweather.bean.LoginRequest
import com.itcast.hmweather.bean.LoginResponse
import com.itcast.hmweather.bean.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // 1. 登录接口
    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    // 2. 获取用户信息
    @GET("user-info")
    suspend fun getUserInfoApi(): UserResponse
}