package com.itcast.hmweather.ui.map

import com.itcast.hmweather.base.BaseView
import com.qweather.sdk.response.weather.WeatherNowResponse

interface MapActView: BaseView {
    fun getWeatherSuccess(address: String, weather: WeatherNowResponse)
    fun getWeatherFailure(error: String)
}