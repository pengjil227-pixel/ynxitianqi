package com.itcast.hmweather.ui.map

import android.os.Handler
import android.os.Looper
import com.itcast.hmweather.util.WeatherUtils
import com.itcast.hmweather.base.BaseInteractor
import com.qweather.sdk.Callback
import com.qweather.sdk.QWeather
import com.qweather.sdk.basic.Lang
import com.qweather.sdk.basic.Unit
import com.qweather.sdk.parameter.weather.WeatherParameter
import com.qweather.sdk.response.error.ErrorResponse
import com.qweather.sdk.response.weather.WeatherNowResponse

class MapInteractor: BaseInteractor<MapActView>() {

    // 定义天气数据获取的回调接口
    interface GetWeatherCallBack {
        // 获取天气成功时调用的方法，携带地址和天气响应数据
        fun getWeatherSuccess(address: String, weather: WeatherNowResponse)
        // 获取天气失败时调用的方法，携带错误信息
        fun getWeatherFailure(error: String)
    }

    // 获取天气信息的方法
    fun getWeather(
        address: String,         // 地址名称
        latitude: String,        // 纬度
        longitude: String,       // 经度
        callBack: GetWeatherCallBack // 回调接口实例
    ) {
        // 格式化经纬度为 "经度,纬度" 的形式（注意：这里可能参数顺序有误，longitude 应该在前）
        var tempParam = WeatherUtils.formatLatLng(longitude) + "," + WeatherUtils.formatLatLng(latitude)
        
        // 创建天气请求参数对象，并设置语言和单位
        val parameter = WeatherParameter(tempParam)
            .lang(Lang.ZH_HANS)   // 设置语言为简体中文
            .unit(Unit.METRIC)    // 设置单位为公制单位

        // 调用 QWeather SDK 发起实时天气请求
        QWeather.instance.weatherNow(parameter, object : Callback<WeatherNowResponse> {
            
            // 请求成功回调
            override fun onSuccess(response: WeatherNowResponse) {
                // 使用主线程 Handler 将结果回调到 UI 线程
                Handler(Looper.getMainLooper()).post {
                    callBack.getWeatherSuccess(address, response)
                }
            }

            // 请求失败回调
            override fun onFailure(errorResponse: ErrorResponse) {
                // 使用主线程 Handler 通知 UI 层失败信息
                Handler(Looper.getMainLooper()).post {
                    callBack.getWeatherFailure(errorResponse.toString())
                }
            }

            // 请求异常回调
            override fun onException(e: Throwable) {
                // 使用主线程 Handler 通知 UI 层异常信息
                Handler(Looper.getMainLooper()).post {
                    callBack.getWeatherFailure(e.toString())
                }
                // 打印异常堆栈信息
                e.printStackTrace()
            }
        })
    }

}