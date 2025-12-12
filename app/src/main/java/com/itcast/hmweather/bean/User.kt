package com.itcast.hmweather.bean

// 1. 基础响应封装类，适用于所有接口返回
data class BaseResponse<T>(
    val code: Int,           // 状态码
    val message: String,     // 响应消息
    val data: T? = null      // 泛型数据体，可为空
)

// 2. 登录相关
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginData(
    val token: String
)
typealias LoginResponse = BaseResponse<LoginData>

// 3. 用户信息
data class UserData(
    val username: String = "",
    val avatar: String = "",
    val gender: String = "",
    val nick_name: String = "",
    val motto: String = "",
)
typealias UserResponse = BaseResponse<UserData>