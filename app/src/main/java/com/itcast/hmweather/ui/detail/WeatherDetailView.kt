package com.itcast.hmweather.ui.detail

import com.itcast.hmweather.base.BaseView
import com.qweather.sdk.response.weather.WeatherDaily

interface WeatherDetailView : BaseView {

    /**
     * 今日天气数据刷新成功时调用
     * @param weather 当日天气数据对象
     */
    fun refreshTodayDataSuccess(weather: WeatherDaily)

    /**
     * 今日天气数据刷新失败时调用
     * @param error 错误信息
     */
    fun refreshTodayDataFail(error: String)

}