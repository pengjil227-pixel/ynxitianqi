package com.itcast.hmweather.bean

/*
 * 切换城市的bean
 */
class ChangeCityBean(
    var cityName: String, //城市名称
    var latitude: Double,   //纬度
    var longitude: Double,  //经度
    var isCurrentLocation: Boolean = false //是否是当前位置
)