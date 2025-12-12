package com.itcast.hmweather.ui.map

import com.itcast.hmweather.ui.map.MapActView
import com.itcast.hmweather.ui.map.MapInteractor
import com.itcast.hmweather.base.BasePresenter
import com.qweather.sdk.response.weather.WeatherNowResponse

class MapPresenter(val mView: MapActView, val mInteractor: MapInteractor):
    BasePresenter<MapActView, MapInteractor>(mView, mInteractor), MapInteractor.GetWeatherCallBack {

    /**
     * 调用天气获取方法，传递地址、纬度和经度参数给数据交互层
     */
    fun getWeather(address: String, latitude: String, longitude: String){
        mInteractor.getWeather(address, latitude, longitude, this)
    }

    /**
     * 实现接口 GetWeatherCallBack 的成功回调方法
     * 用于在获取天气信息成功时通知视图层
     */
    override fun getWeatherSuccess(address: String, weather: WeatherNowResponse) {
        mView.getWeatherSuccess(address, weather)
    }

    /**
     * 实现接口 GetWeatherCallBack 的失败回调方法
     * 用于在获取天气信息失败时通知视图层
     */
    override fun getWeatherFailure(error: String) {
        mView.getWeatherFailure(error)
    }

}