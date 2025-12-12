package com.itcast.hmweather.util

import com.itcast.hmweather.WeatherApplication
import com.itcast.hmweather.apis.ApiService
import com.itcast.hmweather.storage.TokenPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // 定义基础的URL常量
    private const val BASE_URL = "http://172.16.39.120:3000/"

    // 创建okHttpClient实例
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor( // 日志输出拦截器
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .connectTimeout(30, TimeUnit.SECONDS) // 设置超时时间
            .readTimeout(30, TimeUnit.SECONDS) // 读取超时
            .build()
     }

    // 创建一个Token拦截器
    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            // 1. 获取本地网络请求对象
            val request = chain.request()

            // 2. 获取本地存储的token
            val token = TokenPreferences.getInstance(WeatherApplication.getInstance().applicationContext).getToken()

            // 3. 携带Token
            val newRequest = token?.let {
                request.newBuilder()
                    .addHeader("Authorization", "Bearer $it")
                    .build()
            } ?: request

            // 4. 继续发请求
            return chain.proceed(newRequest)
        }
    }


    // 使用延迟初始化，确保在第一次需要使用 ApiService 时才创建实例，避免资源浪费
    val apiService: ApiService by lazy {
        // 创建Retrofit构建器
        Retrofit.Builder()
            .client(okHttpClient)
            // 设置基础URL
            .baseUrl(BASE_URL)
            // 添加GSON数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            // 构建Retrofit实例并生成网络请求接口
            .build()
            .create(ApiService::class.java)
    }
}