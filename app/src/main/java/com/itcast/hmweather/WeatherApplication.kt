package com.itcast.hmweather

import android.app.Application
import com.itcast.hmweather.bean.UserData
import com.itcast.hmweather.util.FavoriteManager
import com.qweather.sdk.JWTGenerator
import com.qweather.sdk.QWeather

/**
 * 主要作用: 全局的初始化配置  全局数据的局部存储
 */
class WeatherApplication: Application() {

    /**
     * 全局上下文获取
     * - 使用整个应用中可以方便获取到上下文
     */
    companion object {
        private var instance: WeatherApplication? = null
        fun getInstance(): WeatherApplication {
            return instance!!
        }

        // 用户信息
        lateinit var user: UserData
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        // 1. 初始化天气SDK
        initWeatherSDK()

        // 2. 初始化收藏城市管理器
        FavoriteManager.init(this)
    }

    fun initUser(u: UserData) {
        user = u
    }

    fun initWeatherSDK() {
        // 1.1 设置Token生成器
        val jwt = JWTGenerator(
            WeatherConstants.WEATHER_PRIVATE_KEY, // 私钥
            WeatherConstants.WEATHER_PROJECT_ID, // 项目ID
            WeatherConstants.WEATHER_JWT_ID // 凭据ID
        )

        // 1.2 初始化天气SDK
        QWeather.getInstance(this, WeatherConstants.WEATHER_HOST) // 初始化服务地址
            .setLogEnable(true)  // 启用调试日志（生产环境建议设置为 false）
            .setTokenGenerator(jwt) // 设置Token生成器
    }
}