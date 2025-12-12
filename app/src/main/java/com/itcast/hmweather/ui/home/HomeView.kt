package com.itcast.hmweather.ui.home
import com.itcast.hmweather.base.BaseView
import com.qweather.sdk.response.weather.WeatherNowResponse
import com.qweather.sdk.response.weather.WeatherHourlyResponse
import com.itcast.hmweather.bean.ChangeCityBean

interface HomeView: BaseView {
    // 1. 获取当前天气成功
    fun getMainWeatherDataSuccess(response: WeatherNowResponse)
    // 2. 获取当前天气失败
    fun getMainWeatherDataFailed(msg: String)

    // 3. 获取逐小时天气数据成功
    fun getHourlyWeatherDataSuccess(response: WeatherHourlyResponse)
    // 4. 获取逐小时天气数据失败
    fun getHourlyWeatherDataFailure(errorResponse: String)

    // 5. 刷新城市列表数据成功
    fun refreshCityList(cityList: MutableList<ChangeCityBean>)
}