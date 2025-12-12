package com.itcast.hmweather.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.itcast.hmweather.R
import com.itcast.hmweather.base.BaseBindingActivity
import com.itcast.hmweather.databinding.AcLocationMapBinding
import com.qweather.sdk.response.weather.WeatherNowResponse


class MapActivity : BaseBindingActivity<AcLocationMapBinding>(),
    AMap.OnMarkerClickListener, AMap.OnMapClickListener,
        MapActView
{

    private var mLatitude = ""
    private var mLongitude = ""
    private var currLatLng: LatLng? = null
    private var mapPresenter: MapPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapPresenter = MapPresenter(this, MapInteractor())

        binding.mapView.onCreate(savedInstanceState)
        getParamFromIntent()
        initMap()
    }

    fun getParamFromIntent(){
        mLatitude = intent.getStringExtra("latitude") ?: ""
        mLongitude = intent.getStringExtra("longitude") ?: ""
    }

    fun initMap(){
        var map = binding.mapView.map
        map.mapType = AMap.MAP_TYPE_NORMAL
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener(this)

        // 如果经纬度存在，则定位到该位置
        if (mLatitude.isNotEmpty() && mLongitude.isNotEmpty()){
            var latLng = LatLng(mLatitude.toDouble(), mLongitude.toDouble()) // 构造LatLng
            currLatLng = latLng // 保存为当前坐标
            getGeoLocation(latLng) // 获取该位置信息
        }
    }

    // 逆地理编码查询 - 获取位置详细信息
    fun getGeoLocation(latlng: LatLng?){
        showLoading() // 显示加载动画
        latlng?.let {
            var lat = latlng.latitude
            var lng = latlng.longitude
            var geocodeSearch = GeocodeSearch(this) // 创建地理编码搜索对象
            geocodeSearch.setOnGeocodeSearchListener(object: GeocodeSearch.OnGeocodeSearchListener{
                // 逆地理编码回调
                override fun onRegeocodeSearched(
                    regeocodeResult: RegeocodeResult?, code: Int
                ){
                    if(code == 1000){ // 请求成功
                        regeocodeResult?.regeocodeAddress?.formatAddress?.let {
                            hideLoading()
                            mapPresenter?.getWeather(it, lat.toString(), lng.toString())
                        }
                    }else{ // 请求失败
                        hideLoading() // 隐藏加载动画
                    }
                }

                // 普通地理编码回调（未使用）
                override fun onGeocodeSearched(
                    p0: GeocodeResult?,
                    p1: Int
                ) {

                }
            })
            var latLonPoint = LatLonPoint(lat, lng) // 构造高德SDK所需的坐标点
            var geocodeQuery = RegeocodeQuery(latLonPoint, 100f, GeocodeSearch.AMAP) // 创建查询对象
            geocodeSearch.getFromLocationAsyn(geocodeQuery) // 异步执行查询
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }


    override fun initBinding(layoutInflater: LayoutInflater): AcLocationMapBinding {
        return AcLocationMapBinding.inflate(layoutInflater)
    }

    /**
     * 地图点击事件处理
     */
    override fun onMapClick(latlng: LatLng?) {
        currLatLng = latlng
        getGeoLocation(latlng) // 获取点击位置的信息
    }

    /**
     * 点击marker显示位置和天气详情
     */
    override fun onMarkerClick(marker: Marker?): Boolean {
        var latLng = marker?.position // 获取marker的位置
        latLng?.let {
            var lat = latLng.latitude
            var lng = latLng.longitude
            Toast.makeText(this, "点击了marker===lat====$lat====lng====$lng", Toast.LENGTH_SHORT).show()
        }
        marker?.showInfoWindow() // 显示信息窗口
        return true
    }

    override fun getWeatherSuccess(
        address: String,
        weather: WeatherNowResponse
    ) {
        hideLoading() // 隐藏加载动画

        // 数据拼接，如 晴 | 北风3级 | 当前体感20度
        var constructText = ""
        if (weather.now.text.isNotEmpty()) {
            constructText += weather.now.text
        }
        if (weather.now.windDir.isNotEmpty() && weather.now.windScale.isNotEmpty()) {
            constructText += " | " + weather.now.windDir + weather.now.windScale + "级"
        }
        if (weather.now.feelsLike.isNotEmpty()) {
            constructText += " | 当前体感" + weather.now.feelsLike + "°"
        }

        // 添加标记
        var marker = binding.mapView.map.addMarker(
            MarkerOptions()
                .position(currLatLng) // 标记位置
//            .icon(BitmapDescriptorFactory.defaultMarker()) // 高德默认marker样式
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)) // 自定义marker样式
                .title(address) // 标题：地址
                .snippet(constructText)) // 内容：天气信息
        marker.showInfoWindow() // 显示信息窗口
        binding.mapView.map.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, 15f)) // 移动相机并缩放

    }

    override fun getWeatherFailure(error: String) {
        hideLoading() // 隐藏加载动画
    }

}