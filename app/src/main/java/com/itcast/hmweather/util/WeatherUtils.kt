package com.itcast.hmweather.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import androidx.core.content.ContextCompat
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 项目的工具类
 */
object WeatherUtils {

    // 用于日期格式化的SimpleDateFormat实例，初始为null
    private var sdf: SimpleDateFormat? = null

    /**
     * 将UTC时间戳转换为指定格式的字符串
     * @param l 时间戳（毫秒）
     * @param strPattern 输出格式，默认为"yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    fun formatUTC(l: Long, strPattern: String?): String? {
        var strPattern = strPattern
        // 如果未提供格式，则使用默认格式
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss"
        }
        // 复用已有的SimpleDateFormat实例或创建新的实例
        if (sdf != null) {
            sdf?.applyPattern(strPattern)
        } else {
            sdf = SimpleDateFormat(strPattern, Locale.CHINA)
        }
        return sdf?.format(l)
    }

    /**
     * 获取当前应用的SHA1签名值
     * @param context 上下文对象
     * @return SHA1签名值（字符串形式）
     */
    fun getSHA1(context: Context): String? {
        // 获取应用包信息
        val info = context.packageManager.getPackageInfo(
            context.packageName, PackageManager.GET_SIGNATURES
        )
        // 获取第一个签名证书并转换为字节数组
        val cert = info.signatures?.get(0)?.toByteArray()
        // 获取SHA1摘要算法实例
        val md = MessageDigest.getInstance("SHA1")
        // 执行摘要计算
        val publicKey = md.digest(cert as ByteArray)
        // 构建十六进制字符串
        val hexString = StringBuffer()
        publicKey.forEach {
            var appendString = Integer.toHexString(0xFF and it.toInt())
            if (appendString.length == 1)
                hexString.append("0")
            hexString.append(appendString)
            hexString.append(":")
        }
        return hexString.substring(0, hexString.length - 1)
    }

    /**
     * 将经纬度字符串保留两位小数
     * @param str 经纬度字符串
     * @return 保留两位小数的字符串表示
     */
    fun formatLatLng(str: String): String {
        if (str.isEmpty()) {
            return "0.00"
        }
        val tempPam = str.toDouble()
        return "%.2f".format(tempPam)
    }

    /**
     * 格式化时间：将ISO8601格式的时间字符串转为"yyyy-MM-dd HH:mm"格式
     * @param time ISO8601格式的时间字符串
     * @return 转换后的时间字符串
     */
    fun formatTime(time: String): String {
        return try {
            // 定义输入格式（ISO 8601）
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX", Locale.getDefault())

            // 解析时间
            val date: Date = inputFormat.parse(time) ?: return ""

            // 定义输出格式（如 "yyyy-MM-dd HH:mm"）
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            // 返回格式化后的时间
            outputFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 格式化时间：将ISO8601格式的时间字符串转为仅显示小时和分钟（24小时制）
     * @param isoTime ISO8601格式的时间字符串
     * @return 仅包含小时和分钟的字符串
     */
    fun isoTo24HourTime(isoTime: String): String {
        return try {
            // 定义输入格式（ISO 8601）
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX", Locale.getDefault())

            // 解析时间
            val date: Date = inputFormat.parse(isoTime) ?: return ""

            // 定义输出格式（24小时制，如 "02:00"）
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            // 返回格式化后的时间
            outputFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 判断指定名称的drawable资源是否存在于R类中
     * @param context 上下文对象
     * @param resourceName 资源名称
     * @return 如果存在且可加载返回true，否则返回false
     */
    fun isDrawableInRClass(context: Context, resourceName: String): Boolean {
        return try {
            // 获取资源ID
            val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            if (resId == 0) return false

            // 检查资源是否能正常加载
            val drawable = ContextCompat.getDrawable(context, resId)
            drawable != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 根据资源名称获取对应的Drawable对象
     * @param context 上下文对象
     * @param name 资源名称
     * @return 对应的Drawable对象或null
     */
    fun getDrawableByName(context: Context, name: String): Drawable? {
        val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
        return ContextCompat.getDrawable(context, resId)
    }

    /**
     * 将dp单位转换为像素（px）
     * @param context 上下文对象
     * @param dipValue dp值
     * @return 对应的像素值
     */
    fun dip2px(context: Context, dipValue: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.resources.displayMetrics).toInt()
    }
}