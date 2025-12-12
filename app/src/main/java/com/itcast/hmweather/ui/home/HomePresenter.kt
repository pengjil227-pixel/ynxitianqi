package com.itcast.hmweather.ui.home

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itcast.hmweather.R
import com.itcast.hmweather.base.BasePresenter
import com.itcast.hmweather.bean.ChangeCityBean
import com.qweather.sdk.response.geo.GeoCityTopResponse
import com.qweather.sdk.response.weather.WeatherNowResponse

class HomePresenter(val mView: HomeView, val mInteractor: HomeInteractor) :
    BasePresenter<HomeView, HomeInteractor>(mView, mInteractor),
    HomeInteractor.GetWeatherData,
    HomeInteractor.GetHourlyWeatherData,
    HomeInteractor.GetTopCityListData
{

    private var currShowCityName = ""
    private var currShowLatitude = ""
    private var currShowLongitude = ""

    fun getCurrShowCityName(): String {
        return currShowCityName
    }

    fun getCurrShowLatitude(): String {
        return currShowLatitude
    }

    fun getCurrShowLongitude(): String {
        return currShowLongitude
    }

    private var cityList = arrayListOf<ChangeCityBean>()

    /**
     *  设置当前展示的城市名和经纬度数据
     */
    fun setCityLocationParams(cityName: String, latitude: String, longitude: String) {
        currShowCityName = cityName
        currShowLatitude = latitude
        currShowLongitude = longitude
    }

    fun requestMainWeatherData() {
        mInteractor.getCityCurrentWeather(currShowLatitude, currShowLongitude, this)
        mInteractor.getCityHourlyWeather(currShowLatitude, currShowLongitude, this)
    }

    fun requestTopCityListData() {
        mInteractor.getTopCityList(this)
    }

    // --------------------------------------
    override fun getWeatherDataSuccess(response: WeatherNowResponse) {
        mView.getMainWeatherDataSuccess(response)
    }

    override fun getWeatherDataFailed(msg: String) {
        mView.getMainWeatherDataFailed(msg)
    }

    /**
     * 每小时天气数据获取成功时调用
     * @param response 包含每小时天气信息的响应对象
     */
    override fun getHourlyWeatherDataSuccess(response: com.qweather.sdk.response.weather.WeatherHourlyResponse) {
        mView.getHourlyWeatherDataSuccess(response)
    }

    /**
     * 每小时天气数据获取失败时调用
     * @param errorResponse 错误信息描述
     */
    override fun getHourlyWeatherDataFailure(errorResponse: kotlin.String) {
        mView.getHourlyWeatherDataFailure(errorResponse)
    }

    override fun getTopCityListSuccess(response: GeoCityTopResponse) {
        cityList.clear()
        // 1. 要把当前展示的城市添加到最前面
        var currentBean = ChangeCityBean(
            currShowCityName,
            currShowLatitude.toDouble(),
            currShowLongitude.toDouble(),
            true
        )
        cityList.add(currentBean)

        // 2. 从本地获取json文件
        var cityListFromRaw = readCitiesFromRaw(mView.getContext()!!, R.raw.city_list)
        cityListFromRaw.forEach {
            if(it.cityName != currShowCityName){
                var bean = ChangeCityBean(
                    it.cityName,
                    it.latitude.toDouble(),
                    it.longitude.toDouble(),
                    false
                )
                cityList.add(bean)
            }
        }

        mView.refreshCityList(cityList)
    }

    /**
     * 读取raw中的城市数据
     */
    fun readCitiesFromRaw(context: Context, resId: Int): List<ChangeCityBean> {
        return try {
            // 打开指定的raw资源文件并读取为字符串
            val jsonString = context.resources.openRawResource(resId)
                .bufferedReader()
                .use { it.readText() }

            // 使用TypeToken定义目标类型，用于Gson解析泛型List<ChangeCityBean>
            val type = object : TypeToken<List<ChangeCityBean>>() {}.type

            // 使用Gson将JSON字符串转换为List<ChangeCityBean>对象
            Gson().fromJson(jsonString, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getTopCityListFailure(errorResponse: String) {
        Toast.makeText(mView.getContext(), "获取城市信息失败", Toast.LENGTH_SHORT).show()
    }

}