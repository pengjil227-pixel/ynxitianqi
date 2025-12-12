package com.itcast.hmweather

object WeatherGlobal {

    private var currentCityAreaCode: String = "" //当前城市编码
    private var adCode: String = "" //当前区编码
    private var latitude: String = "" //纬度
    private var longitude: String = "" //经度

    private var defaultCityAreaCode = "110105"
    private var defaultLatitude = "39.96613"
    private var defaultLongitude = "116.491571"

    private var cityName: String = "" //城市名称
    private var district: String = "" //区名称

    private val baseUrl = "http://172.16.39.120:3000/"


    //城市编码
    fun getCurrentCityAreaCode(): String {
        if (currentCityAreaCode.isEmpty()) {
            currentCityAreaCode = defaultCityAreaCode
        }
        return currentCityAreaCode
    }

    //城市编码
    fun setCurrentCityAreaCode(value: String) {
        currentCityAreaCode = value
    }

    //区编码
    fun getAdCode(): String {
        return adCode
    }

    //区编码
    fun setAdCode(value: String) {
        adCode = value
    }

    //纬度
    fun getLatitude(): String {
        if (latitude.isEmpty()) {
            latitude = defaultLatitude
        }
        return latitude
    }

    //纬度
    fun setLatitude(value: String) {
        latitude = value
    }

    //经度
    fun getLongitude(): String {
        if (longitude.isEmpty()) {
            longitude = defaultLongitude
        }
        return longitude
    }

    //经度
    fun setLongitude(value: String) {
        longitude = value
    }

    //城市名称
    fun getCityName(): String {
        if (cityName.isEmpty()) {
            cityName = "北京市"
        }
        return cityName
    }

    //城市名称
    fun setCityName(value: String) {
        cityName = value
    }

    //区名称
    fun getDistrict(): String {
        return district
    }

    //区名称
    fun setDistrict(value: String) {
        district = value
    }

    // 获取baseUrl
    fun getBaseUrl(): String {
        return baseUrl
    }

}