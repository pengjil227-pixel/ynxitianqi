package com.itcast.hmweather.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itcast.hmweather.WeatherApplication
import com.itcast.hmweather.bean.CityData
import androidx.core.content.edit

object FavoriteManager {
    // 定义SharedPreferences文件的名称
    private const val PREFS_NAME = "city_favorite_prefs"

    // 定义用于存储收藏城市的键名
    private const val FAVORITE_PREFIX = "favorites_"

    // 定义对象存储
    private lateinit var sharedPreferences: SharedPreferences

    // 初始化Gson对象用于JSON序列化和反序列化
    private val gson = Gson()

    fun init(context: Context){
        // 初始化对象存储
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 获取用户名
     */
    private  fun getCurrentUserName(): String? {
        val user = WeatherApplication.user.username
        return "$FAVORITE_PREFIX$user"
    }

    /**
     * 添加收藏城市
     */
    fun addFavoriteCity(cityData: CityData) {
        val userKey = getCurrentUserName() ?: return
        var currentSet = getFavoritesByUserKey(userKey).toMutableSet()
        currentSet.remove(cityData)
        currentSet.add(cityData)
        // 重新存储到本地
        saveFavoritesForUser(userKey, currentSet)
    }

    /**
     * 删除收藏城市
     */
    fun removeFavoriteCity(cityName: String) {
        val userKey = getCurrentUserName() ?: return
        var currentSet = getFavoritesByUserKey(userKey).toMutableSet()
        val tempCity = CityData(cityName, 0.0, 0.0, "", "")
        currentSet.remove(tempCity)
        saveFavoritesForUser(userKey, currentSet)
    }

    /**
     * 切换城市收藏状态
     * @param cityData 城市数据
     * @return 返回切换后的收藏状态
     */
    fun toggleFavorite(cityData: CityData): Boolean {
        val userKey = getCurrentUserName() ?: return false
        val currentSet = getFavoritesByUserKey(userKey).toMutableSet()
        val tempCity = CityData(cityData.name, 0.0, 0.0, "", "")
        val isFavorite = currentSet.contains(tempCity)

        if (isFavorite) {
            currentSet.remove(tempCity)
        } else {
            currentSet.add(cityData)
        }

        saveFavoritesForUser(userKey, currentSet)
        return !isFavorite
    }

    /**
     * 检查城市是否在收藏列表中
     * @param cityName 要检查的城市名称
     * @return 返回布尔值表示收藏状态
     */
    fun isFavorite(cityName: String): Boolean {
        val userKey = getCurrentUserName() ?: return false
        val tempCity = CityData(cityName, 0.0, 0.0, "", "")
        return getFavoritesByUserKey(userKey).contains(tempCity)
    }


    /**
     * 获取当前用户收藏城市
     */
    fun getCurrentUserFavorites(): Set<CityData> {
        val userKey = getCurrentUserName() ?: return emptySet()
        return getFavoritesByUserKey(userKey)
    }

    /**
     * 根据用户的key获取收藏城市的集合
     */
    private fun getFavoritesByUserKey(userKey: String): Set<CityData> {
        // 1. 从本地获取用户存储的城市(字符串)
        val jsonSet = sharedPreferences.getStringSet(userKey, mutableSetOf()) ?: return emptySet()

        // 2. 将字符串转换成对象
        return try {
            // 如果集合为空，直接返回空集合
            if (jsonSet.isEmpty()) {
                emptySet()
            } else {
                // 使用TypeToken获取目标类型信息
                val type = object : TypeToken<Set<CityData>>() {}.type
                // 使用Gson将字符串转换为CityData对象集合
                gson.fromJson(jsonSet.toString(), type) ?: emptySet()
            }
        } catch (e: Exception){
            emptySet()
        }
    }

    private fun saveFavoritesForUser(userKey: String, favorites: Set<CityData>) {
        // 1. 将收藏城市对象集合转换成JSON字符串
        val jsonSet = favorites.map {
            gson.toJson(it)
        }.toSet()

        // 2. 存储JSON字符串到本地
        sharedPreferences.edit {
            putStringSet(userKey, jsonSet)
            apply()
        }
    }


}