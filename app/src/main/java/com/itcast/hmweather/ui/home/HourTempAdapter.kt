package com.itcast.hmweather.ui.home

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.qweather.sdk.response.weather.WeatherHourly
import com.itcast.hmweather.R
import com.itcast.hmweather.util.WeatherUtils

class HourTempAdapter: BaseQuickAdapter<WeatherHourly, QuickViewHolder>() {

    var mContext: Context? = null
    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: WeatherHourly?
    ) {

        holder.run {
            //时间点
            setText(R.id.tv_hour, WeatherUtils.isoTo24HourTime(item?.fxTime.toString()))
            //天气图标
            mContext?.let {
                var iconCode = "_" + item?.icon?.replace("-", "_")
                if (WeatherUtils.isDrawableInRClass(it, iconCode)) {
                    val drawable = WeatherUtils.getDrawableByName(it, iconCode)
                    drawable?.let {
                        setImageDrawable(R.id.iv_hour_weather, drawable)
                    }
                    setGone(R.id.iv_hour_weather, false)
                } else {
                    setGone(R.id.iv_hour_weather, true)
                }
            }

            //天气和温度拼接，如 晴 | 20°
            var showText = ""
            if (item?.text?.isNotEmpty() == true) {
                showText += item.text
            }
            if (item?.temp?.isNotEmpty() == true) {
                showText += " | " + item.temp + "°"
            }
            if (showText.isNotEmpty()) {
                setText(R.id.tv_hour_temp, showText)
                setGone(R.id.tv_hour_temp, false)
            } else {
                setGone(R.id.tv_hour_temp, true)
            }

            //湿度拼接，如 湿度 80%
            if (item?.humidity?.isNotEmpty() == true) {
                setText(R.id.tv_hour_humidity, "湿度 " + item.humidity + "%")
                setGone(R.id.tv_hour_humidity, false)
            } else {
                setGone(R.id.tv_hour_humidity, true)
            }

            //风向和风力拼接，如 北风 3级
            if (item?.windDir?.isNotEmpty() == true && item.windScale?.isNotEmpty() == true) {
                setText(R.id.tv_hour_wind, item.windDir + "风 " + item.windScale + "级")
                setGone(R.id.tv_hour_wind, false)
            } else {
                setGone(R.id.tv_hour_wind, true)
            }

            //气压拼接，如 气压 1010hPa
            if (item?.pressure?.isNotEmpty() == true) {
                setText(R.id.tv_hour_pressure, "气压 " + item.pressure + "hPa")
                setGone(R.id.tv_hour_pressure, false)
            } else {
                setGone(R.id.tv_hour_pressure, true)
            }
        }

    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        mContext = context
        return QuickViewHolder(R.layout.item_hour_weather,  parent)
    }

}