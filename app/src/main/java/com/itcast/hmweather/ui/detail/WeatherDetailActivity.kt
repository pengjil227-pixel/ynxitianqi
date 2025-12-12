package com.itcast.hmweather.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.itcast.hmweather.R
import com.itcast.hmweather.base.BaseBindingActivity
import com.itcast.hmweather.bean.CityData
import com.itcast.hmweather.databinding.AcWeatherDetailBinding
import com.itcast.hmweather.ui.map.MapActivity
import com.itcast.hmweather.ui.video.Media3PlayerActivity
import com.itcast.hmweather.util.FavoriteManager
import com.itcast.hmweather.util.WeatherUtils
import com.qweather.sdk.response.weather.WeatherDaily

class WeatherDetailActivity : BaseBindingActivity<AcWeatherDetailBinding>(), WeatherDetailView {

    // 1. 定义属性接收传递的参数
    private var mCityName = ""
    private var mLatitude = ""
    private var mLongitude = ""

    private var mWeather = ""
    private var mTemperature = ""
    /**
     * 设置天气和温度
     */
    fun setWeatherAndTemperature(weather: String, temperature: String) {
        mWeather = weather
        mTemperature = temperature
    }

    private var mPresenter: WeatherDetailPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPresenter = WeatherDetailPresenter(this, WeatherDetailInteractor())

        // 1. 获取intent传递的参数
        getParamFromIntent()

        // 2. 获取数据
        getData()

        // 3. 设置点击事件
        setOnClickEvent()

        // 4. 查询下当前城市是否被收藏
       refreshStarStatus(FavoriteManager.isFavorite(mCityName))

    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    /**
     *  1. 获取传递的参数
     */
    private fun getParamFromIntent() {
        mCityName = intent.getStringExtra("city") ?: ""
        mLatitude = intent.getStringExtra("latitude") ?: ""
        mLongitude = intent.getStringExtra("longitude") ?: ""

        mPresenter?.setCityName(mCityName)
        mPresenter?.setLocation(mLatitude, mLongitude)
        binding.detailTodayCard.tvCityName.text = mCityName
    }

    /**
     * 2. 获取天气数据
     */
    private fun getData() {
        showLoading()
        mPresenter?.get3DayWeather()
    }


    private fun setOnClickEvent() {
        // 1. 跳转到视频播放页面
        binding.detailTodayCard.ivVideoIcon.setOnClickListener {
            var intent = Intent(this, Media3PlayerActivity::class.java)
            startActivity(intent)
        }

        // 2. 收藏城市
        binding.detailTodayCard.ivDetailStarCity.setOnClickListener {
            val isNowFavorite = FavoriteManager.toggleFavorite(
                CityData(mCityName, mLatitude.toDouble(), mLongitude.toDouble(), mWeather, mTemperature)
            )
            refreshStarStatus(isNowFavorite)
            Toast.makeText(this, if (isNowFavorite) "收藏成功" else "取消收藏", Toast.LENGTH_SHORT).show()
        }

        // 3. 跳转到地图页面
        binding.detailTodayCard.ivLocationIcon.setOnClickListener {
            var intent = Intent(this, MapActivity::class.java)
            intent.putExtra("latitude", mLatitude)
            intent.putExtra("longitude", mLongitude)
            startActivity(intent)
        }
    }

    private fun refreshStarStatus(isStar:  Boolean){
        if (isStar){
            binding.detailTodayCard.ivDetailStarCity.setImageResource(R.mipmap.icon_stared)
        } else {
            binding.detailTodayCard.ivDetailStarCity.setImageResource(R.mipmap.icon_detail_star)
        }
    }

    override fun initBinding(layoutInflater: LayoutInflater): AcWeatherDetailBinding {
        return AcWeatherDetailBinding.inflate(layoutInflater)
    }

    /**
     * 今日天气数据刷新成功时调用
     * @param weather 当日天气数据对象
     */
    @SuppressLint("SetTextI18n")
    override fun refreshTodayDataSuccess(weather: WeatherDaily) {
        hideLoading()

        binding.detailTodayCard.llCurrentWeatherContainer.visibility = View.VISIBLE

        var todayTemp = weather.tempMin + "° / " + weather.tempMax + "°"
        binding.detailTodayCard.tvTemperature.text = todayTemp

        // mPresenter?.setWeatherAndTemperature(weather.textDay, todayTemp)

        //白天、夜间天气
        refreshDayNightMoonPhase(weather)

        //湿度、紫外线强度指数、气压
        binding.detailTodayCard.humidityUvPressure.tvName1.text = "湿度"
        binding.detailTodayCard.humidityUvPressure.tvValue1.text = weather.humidity
        binding.detailTodayCard.humidityUvPressure.tvName2.text = "紫外线强度指数"
        binding.detailTodayCard.humidityUvPressure.tvValue2.text = weather.uvIndex
        binding.detailTodayCard.humidityUvPressure.tvName3.text = "气压"
        binding.detailTodayCard.humidityUvPressure.tvValue3.text = weather.pressure + "hPa"

        //日降水量，能见度
        binding.detailTodayCard.rainVisContainer.tvName1.text = "日降水量"
        binding.detailTodayCard.rainVisContainer.tvValue1.text = weather.precip + " mm"
        binding.detailTodayCard.rainVisContainer.tvName2.text = "能见度"
        binding.detailTodayCard.rainVisContainer.tvValue2.text = weather.vis + "km"
        binding.detailTodayCard.rainVisContainer.tvName3.text = ""
        binding.detailTodayCard.rainVisContainer.tvValue3.text = ""


        //日出日落
        binding.detailTodayCard.tvSunriseSunset.text =
            "日出时间：" + weather.sunrise + " 日落时间：" + weather.sunset
        //月出月落
        binding.detailTodayCard.tvMoonriseMoonset.text =
            "月出时间：" + weather.moonrise + " 月落时间：" + weather.moonset
        //风况
        binding.detailTodayCard.tvDayWindNightWind.text =
            "白天风况：" + weather.windDirDay + weather.windScaleDay + "级 风速：" + weather.windSpeedDay + "km/h\n夜间风况：" + weather.windDirNight + weather.windScaleNight + "级 风速：" + weather.windSpeedNight + "km/h"
    }

    /**
     * 刷新白天、夜间天气, 月相
     */
    fun refreshDayNightMoonPhase(weather: WeatherDaily) {
        binding.detailTodayCard.weatherMoonPhase.tvName1.text = "白天天气"
        binding.detailTodayCard.weatherMoonPhase.tvValue1.text = weather.textDay
        var textDayIconCode = "_" + weather.iconDay.replace("-", "_")
        if (WeatherUtils.isDrawableInRClass(this, textDayIconCode)) {
            val drawable = WeatherUtils.getDrawableByName(this, textDayIconCode)
            drawable?.let {
                binding.detailTodayCard.weatherMoonPhase.ivIcon1.setImageDrawable(drawable)
            }
            binding.detailTodayCard.weatherMoonPhase.ivIcon1.visibility = View.VISIBLE
        } else {
            binding.detailTodayCard.weatherMoonPhase.ivIcon1.visibility = View.GONE
        }

        binding.detailTodayCard.weatherMoonPhase.tvName2.text = "夜间天气"
        binding.detailTodayCard.weatherMoonPhase.tvValue2.text = weather.textNight
        var textNightIconCode = "_" + weather.iconNight.replace("-", "_")
        if (WeatherUtils.isDrawableInRClass(this, textNightIconCode)) {
            val drawable = WeatherUtils.getDrawableByName(this, textNightIconCode)
            drawable?.let {
                binding.detailTodayCard.weatherMoonPhase.ivIcon2.setImageDrawable(drawable)
            }
            binding.detailTodayCard.weatherMoonPhase.ivIcon2.visibility = View.VISIBLE
        } else {
            binding.detailTodayCard.weatherMoonPhase.ivIcon2.visibility = View.GONE
        }

        binding.detailTodayCard.weatherMoonPhase.tvName3.text = "月相"
        binding.detailTodayCard.weatherMoonPhase.tvValue3.text = weather.moonPhase

        var moonPhaseIconCode = "_" + weather.moonPhaseIcon.replace("-", "_")
        if (WeatherUtils.isDrawableInRClass(this, moonPhaseIconCode)) {
            val drawable = WeatherUtils.getDrawableByName(this, moonPhaseIconCode)
            drawable?.let {
                binding.detailTodayCard.weatherMoonPhase.ivIcon3.setImageDrawable(drawable)
            }
            binding.detailTodayCard.weatherMoonPhase.ivIcon3.visibility = View.VISIBLE
        } else {
            binding.detailTodayCard.weatherMoonPhase.ivIcon3.visibility = View.GONE
        }
    }

    /**
     * 今日天气数据刷新失败时调用
     * @param error 错误信息
     */
    override fun refreshTodayDataFail(error: String) {
       hideLoading()
    }
}