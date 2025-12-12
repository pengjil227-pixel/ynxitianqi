package com.itcast.hmweather.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.SpannableStringBuilder
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.AMapLocationQualityReport
import com.amap.api.maps.MapsInitializer
import com.itcast.hmweather.WeatherGlobal
import com.itcast.hmweather.event.GetLocationEvent
import org.greenrobot.eventbus.EventBus

/**
 * 高德地图工具类
 */
object AmapUtils {

    @SuppressLint("StaticFieldLeak")
    private var locationClient: AMapLocationClient? = null
    private var locationOption: AMapLocationClientOption? = null

    private fun initAmapSdk(context: Context) {

        var sha1 = WeatherUtils.getSHA1(context)
        Log.i("======chenchong====initAmapSdk=======", "sha1===${sha1}")
        // 初始化高德地图SDK
        locationClient = AMapLocationClient(context)
        locationOption = getDefaultOption()
        locationClient?.setLocationOption(locationOption)
        locationClient?.setLocationListener(object : AMapLocationListener {
            override fun onLocationChanged(location: AMapLocation?) {
                if (null != location) {
                    val sb = StringBuffer()
                    //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                    if (location.errorCode == 0) {
                        Log.i("======chenchong====onLocationChanged=======", "errorCode===0==")
                        //定位成功
                        WeatherGlobal.setLatitude(location.latitude.toString())
                        WeatherGlobal.setLongitude(location.longitude.toString())
                        WeatherGlobal.setAdCode(location.adCode)
                        WeatherGlobal.setCurrentCityAreaCode(location.cityCode)
                        WeatherGlobal.setCityName(location.city)
                        WeatherGlobal.setDistrict(location.district)

                        sb.append("定位成功" + "\n")
                        sb.append("定位类型: " + location.locationType + "\n")
                        sb.append("经    度    : " + location.longitude + "\n")
                        sb.append("纬    度    : " + location.latitude + "\n")
                        sb.append("精    度    : " + location.accuracy + "米" + "\n")
                        sb.append("提供者    : " + location.provider + "\n")

                        sb.append("速    度    : " + location.speed + "米/秒" + "\n")
                        sb.append("角    度    : " + location.bearing + "\n")
                        // 获取当前提供定位服务的卫星个数
                        sb.append("星    数    : " + location.satellites + "\n")
                        sb.append("国    家    : " + location.country + "\n")
                        sb.append("省            : " + location.province + "\n")
                        sb.append("市            : " + location.city + "\n")
                        sb.append("城市编码 : " + location.cityCode + "\n")
                        sb.append("区            : " + location.district + "\n")
                        sb.append("区域 码   : " + location.adCode + "\n")
                        sb.append("地    址    : " + location.address + "\n")
                        sb.append("兴趣点    : " + location.poiName + "\n")
                        //定位完成的时间
                        sb.append(
                            "定位时间: " + WeatherUtils.formatUTC(
                                location.time,
                                "yyyy-MM-dd HH:mm:ss"
                            ) + "\n"
                        )
                        sb.append(
                            "格式化时间111: " + WeatherUtils.formatUTC(
                                location.time,
                                "EEEE"
                            ) + "\n"
                        )

                        EventBus.getDefault().post(GetLocationEvent(true))
                    } else {

                        Log.i("======chenchong====onLocationChanged=======", "errorCode===!=0==")
                        //定位失败
                        sb.append("定位失败" + "\n")
                        sb.append("错误码:" + location.errorCode + "\n")
                        sb.append("错误信息:" + location.errorInfo + "\n")
                        sb.append("错误描述:" + location.locationDetail + "\n")

                        EventBus.getDefault().post(GetLocationEvent(false))
                    }
                    sb.append("***定位质量报告***").append("\n")
                    sb.append("* WIFI开关：").append(
                        if (location.locationQualityReport.isWifiAble) "开启" else "关闭"
                    ).append("\n")
                    sb.append("* GPS状态：")
                        .append(getGPSStatusString(location.locationQualityReport.gpsStatus))
                        .append("\n")
                    sb.append("* GPS星数：")
                        .append(location.locationQualityReport.gpsSatellites).append("\n")
                    sb.append("* 网络类型：" + location.locationQualityReport.networkType)
                        .append("\n")
                    sb.append("* 网络耗时：" + location.locationQualityReport.netUseTime)
                        .append("\n")
                    sb.append("****************").append("\n")
                    //定位之后的回调时间
                    sb.append(
                        "回调时间: " + WeatherUtils.formatUTC(
                            System.currentTimeMillis(),
                            "yyyy-MM-dd HH:mm:ss"
                        ) + "\n"
                    )

                    //解析定位结果，
                    val result = sb.toString()
                    Log.i("======chenchong====onLocationChanged=======", "success=====$result")
                } else {
                    EventBus.getDefault().post(GetLocationEvent(false))
                    Log.i("======chenchong====onLocationChanged=======", "fail=====")
                }
            }
        })
        startLocation()
    }

    fun updatePrivacyCompliance(context: Context) {
        MapsInitializer.updatePrivacyShow(context, true, true)
        val spannable =
            SpannableStringBuilder("\"亲，感谢您一直以来的信任！我们依据最新的监管要求更新了《隐私权政策》，特向您说明如下\n1.为向您提供交易相关基本功能，我们会收集、使用必要的信息；\n2.基于您的明示授权，我们可能会获取您的位置（为您提供附近的商品、店铺及优惠资讯等）等信息，您有权拒绝或取消授权；\n3.我们会采取业界先进的安全措施保护您的信息安全；\n4.未经您同意，我们不会从第三方处获取、共享或向提供您的信息；\n")
        AlertDialog.Builder(context)
            .setTitle("温馨提示(隐私合规示例)")
            .setMessage(spannable)
            .setPositiveButton("同意", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface?, i: Int) {
                    Log.i("======chenchong====updatePrivacyAgree=======", "同意===隐私合规==")
                    MapsInitializer.updatePrivacyAgree(context, true)
                    initAmapSdk(context)
                }
            })
            .setNegativeButton("不同意", object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface?, i: Int) {
                    Log.i("======chenchong====updatePrivacyAgree=======", "不同意===隐私合规==")
                    MapsInitializer.updatePrivacyAgree(context, false)
                }
            })
            .show()
    }

    private fun getDefaultOption(): AMapLocationClientOption {
        val mOption = AMapLocationClientOption()
        mOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.isGpsFirst = true //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.httpTimeOut = 30000 //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.interval = 2000 //可选，设置定位间隔。默认为2秒
        mOption.isNeedAddress = true //可选，设置是否返回逆地理地址信息。默认是true
        mOption.isOnceLocation = true //可选，设置是否单次定位。默认是false
        mOption.isOnceLocationLatest = true //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTPS) //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.isSensorEnable = true //可选，设置是否使用传感器。默认是false
        mOption.isWifiScan = false //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.isLocationCacheEnable = true //可选，设置是否使用缓存定位，默认为true
        mOption.geoLanguage = AMapLocationClientOption.GeoLanguage.DEFAULT //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption
    }


    /**
     * 获取GPS状态的字符串
     */
    private fun getGPSStatusString(statusCode: Int): String {
        var str = ""
        when (statusCode) {
            AMapLocationQualityReport.GPS_STATUS_OK -> str = "GPS状态正常"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER -> str =
                "手机中没有GPS Provider，无法进行GPS定位"

            AMapLocationQualityReport.GPS_STATUS_OFF -> str = "GPS关闭，建议开启GPS，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_MODE_SAVING -> str =
                "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量"

            AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION -> str =
                "没有GPS定位权限，建议开启gps定位权限"
        }
        return str
    }

    // 根据控件的选择，重新设置定位参数
    private fun resetOption() {
        // 设置是否需要显示地址信息
        locationOption?.isNeedAddress = true
        locationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption?.isGpsFirst = false
        locationOption?.isBeidouFirst = true
        locationOption?.isNeedAddress = true
        // 设置是否开启缓存
        locationOption?.isLocationCacheEnable = true
        // 设置是否单次定位
        locationOption?.isOnceLocation = true
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
        locationOption?.isOnceLocationLatest = true
        //设置是否使用传感器
        locationOption?.isSensorEnable = false
        //设置是否开启wifi扫描，如果设置为false时同时会停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        locationOption?.isWifiScan = true
        // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
        locationOption?.interval = 2000
        // 设置网络请求超时时间
        locationOption?.httpTimeOut = 30000
    }

    /**
     * 开始定位
     */
    private fun startLocation() {
        try {
            //根据控件的选择，重新设置定位参数
            resetOption()
            // 设置定位参数
            locationClient?.setLocationOption(locationOption)
            // 启动定位
            locationClient?.startLocation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止定位
     */
    private fun stopLocation() {
        try {
            // 停止定位
            locationClient?.stopLocation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 销毁定位
     */
    private fun destroyLocation() {
        /**
         * 如果AMapLocationClient是在当前Activity实例化的，
         * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
         */
        locationClient?.onDestroy()
        locationClient = null
        locationOption = null
    }

}