package com.itcast.hmweather.ui.detail

import com.itcast.hmweather.base.BasePresenter

class WeatherDetailPresenter(val mView: WeatherDetailView,  val mInteractor: WeatherDetailInteractor):
    BasePresenter<WeatherDetailView, WeatherDetailInteractor>(mView, mInteractor),
    WeatherDetailInteractor.Get3DayWeatherCallBack {

    private var mCityName = ""
    private var mLatitude = ""
    private var mLongitude = ""

    /**
     * 设置城市名称
     */
    fun setCityName(cityName: String) {
        mCityName = cityName
    }

    /**
     * 设置经纬度
     */
    fun setLocation(latitude: String, longitude: String) {
        mLatitude = latitude
        mLongitude = longitude
    }

    /**
     *  获取3天天气
     *  参数：经纬度
     */
    fun get3DayWeather() {
        mInteractor.get3DayWeather(mLatitude, mLongitude, this)
    }


    /**
     * 获取3天天气成功
     */
    override fun get3DayWeatherSuccess(weather: com.qweather.sdk.response.weather.WeatherDailyResponse) {
        weather.run {
            mView.refreshTodayDataSuccess(weather.daily[0])
        }
    }

    /**
     * 获取3天天气失败
     */
    override fun get3DayWeatherFailure(error: kotlin.String) {
        mView.refreshTodayDataFail(error)
    }
}