package com.itcast.hmweather.ui.home

import com.itcast.hmweather.base.BaseInteractor
import com.itcast.hmweather.util.WeatherUtils
import com.qweather.sdk.QWeather
import com.qweather.sdk.basic.Lang
import com.qweather.sdk.parameter.weather.WeatherParameter
import com.qweather.sdk.response.weather.WeatherNowResponse
import com.qweather.sdk.Callback
import com.qweather.sdk.response.error.ErrorResponse
import android.os.Handler
import android.os.Looper
import com.qweather.sdk.basic.Range
import com.qweather.sdk.parameter.geo.GeoCityTopParameter
import com.qweather.sdk.response.geo.GeoCityTopResponse
import com.qweather.sdk.response.weather.WeatherHourlyResponse

class HomeInteractor: BaseInteractor<HomeView>() {
    // ----------------------
    interface GetWeatherData {
        fun getWeatherDataSuccess(response: WeatherNowResponse)
        fun getWeatherDataFailed(msg: String)
    }

    interface GetHourlyWeatherData {
        /**
         * 每小时天气数据获取成功时调用
         * @param response 包含每小时天气信息的响应对象
         */
        fun getHourlyWeatherDataSuccess(response: WeatherHourlyResponse)

        /**
         * 每小时天气数据获取失败时调用
         * @param errorResponse 错误信息描述
         */
        fun getHourlyWeatherDataFailure(errorResponse: String)
    }

    interface GetTopCityListData {
        fun getTopCityListSuccess(response: GeoCityTopResponse)
        fun getTopCityListFailure(errorResponse: String)
    }
    // -----------------------

    // 1. 获取当前城市的天气数据
    fun getCityCurrentWeather(latitude: String, longitude: String, callback: GetWeatherData){
        // 1.1 准备请求的参数
        var tempParam = WeatherUtils.formatLatLng(longitude) + "," + WeatherUtils.formatLatLng(latitude)
        var parameter = WeatherParameter(tempParam).lang(Lang.ZH_HANS).unit(com.qweather.sdk.basic.Unit.METRIC)

        // 1.2 发起请求
        QWeather.instance.weatherNow(parameter, object: Callback<WeatherNowResponse>{
            override fun onSuccess(res: WeatherNowResponse) {
                // 天气数据请求成功, 切换到主线程调用接口
                // 回到主线程调用数据接口
                Handler(Looper.getMainLooper()).post {
                   callback.getWeatherDataSuccess(res)
                }
            }

            override fun onFailure(error: ErrorResponse) {
                Handler(Looper.getMainLooper()).post {
                    callback.getWeatherDataFailed(error.toString())
                }
            }

            override fun onException(e: Throwable) {
                Handler(Looper.getMainLooper()).post {
                    callback.getWeatherDataFailed(e.toString())
                }
                e.printStackTrace()
            }

        })
    }

    /**
     * 2. 获取当前城市24小时天气
     */
    fun getCityHourlyWeather(latitude: String, longitude: String, callback: GetHourlyWeatherData) {
        var tempParam =
            WeatherUtils.formatLatLng(longitude) + "," + WeatherUtils.formatLatLng(latitude)
        val parameter = WeatherParameter(tempParam)
        QWeather.instance.weather24h(parameter, object : Callback<WeatherHourlyResponse> {
            override fun onSuccess(response: WeatherHourlyResponse) {
                Handler(Looper.getMainLooper()).post {
                    callback.getHourlyWeatherDataSuccess(response)
                }
            }
            override fun onFailure(errorResponse: ErrorResponse) {
                Handler(Looper.getMainLooper()).post {
                    callback.getHourlyWeatherDataFailure(errorResponse.toString())
                }
            }
            override fun onException(e: Throwable) {
                Handler(Looper.getMainLooper()).post {
                    callback.getHourlyWeatherDataFailure(e.toString())
                }
                e.printStackTrace()
            }

        })
    }


    /**
     * 获取热门城市列表
     * 注意： 热门城市列表数据是根据用户的位置进行排序的，所以需要用户授权获取位置信息
     */
    fun getTopCityList(callback: GetTopCityListData) {
        var param = GeoCityTopParameter()
        param.number(20).range(Range.CN).lang(Lang.ZH_HANS)
        QWeather.instance.geoCityTop(param, object : Callback<GeoCityTopResponse> {
            override fun onSuccess(response: GeoCityTopResponse) {
                if (response.code == "200") {
                    Handler(Looper.getMainLooper()).post {
                        callback.getTopCityListSuccess(response)
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        callback.getTopCityListFailure(response.code)
                    }
                }
            }

            override fun onFailure(error: ErrorResponse?) {
                Handler(Looper.getMainLooper()).post {
                    callback.getTopCityListFailure(error.toString())
                }
            }

            override fun onException(e: Throwable?) {
                Handler(Looper.getMainLooper()).post {
                    callback.getTopCityListFailure(e.toString())
                }
            }
        })
    }
}