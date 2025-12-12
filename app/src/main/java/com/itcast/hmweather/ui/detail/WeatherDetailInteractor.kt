package com.itcast.hmweather.ui.detail

import android.os.Handler
import android.os.Looper
import com.itcast.hmweather.base.BaseInteractor
import com.itcast.hmweather.util.WeatherUtils
import com.qweather.sdk.QWeather
import com.qweather.sdk.basic.Lang
import com.qweather.sdk.parameter.weather.WeatherParameter
import com.qweather.sdk.response.weather.WeatherDailyResponse
import com.qweather.sdk.basic.Unit
import com.qweather.sdk.Callback
import com.qweather.sdk.response.error.ErrorResponse

class WeatherDetailInteractor: BaseInteractor<WeatherDetailView>() {

    /**
     * 获取3天天气回调
     */
    interface Get3DayWeatherCallBack {
        /**
         * 获取3天天气成功
         */
        fun get3DayWeatherSuccess(weather: WeatherDailyResponse)

        /**
         * 获取3天天气失败
         */
        fun get3DayWeatherFailure(error: String)
    }

    /**
     * 获取3天天气
     */
    fun get3DayWeather(latitude: String, longitude: String, callBack: Get3DayWeatherCallBack) {
        var tempParam = WeatherUtils.formatLatLng(longitude) + "," + WeatherUtils.formatLatLng(latitude)
        val parameter = WeatherParameter(tempParam)
            .lang(Lang.ZH_HANS)
            .unit(Unit.METRIC)

        QWeather.instance.weather3d(parameter, object : Callback<WeatherDailyResponse> {
            override fun onSuccess(response: WeatherDailyResponse) {
                Handler(Looper.getMainLooper()).post {
                    callBack.get3DayWeatherSuccess(response)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                Handler(Looper.getMainLooper()).post {
                    callBack.get3DayWeatherFailure(errorResponse.toString())
                }
            }

            override fun onException(e: Throwable) {
                Handler(Looper.getMainLooper()).post {
                    callBack.get3DayWeatherFailure(e.toString())
                }
                e.printStackTrace()
            }
        })
    }
}