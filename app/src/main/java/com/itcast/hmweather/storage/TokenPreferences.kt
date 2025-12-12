package com.itcast.hmweather.storage

import android.content.Context
import android.content.SharedPreferences

class TokenPreferences private constructor(context: Context){

    // 1. 希望以单例的模式进行token的操作处理
    companion object {
        @Volatile private var instance: TokenPreferences? = null
        fun getInstance(context: Context): TokenPreferences {
            // 第一次检查：如果实例已存在直接同步返回
            return instance ?: synchronized(this) {
                // 第二次检查：确保只有一个实例被创建
                instance ?: TokenPreferences(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    // 2. 创建SharedPreferences
    private val prefs: SharedPreferences by lazy {
        // Context.MODE_PRIVATE 表示该 SharedPreferences 文件是私有的，只能被当前应用访问
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }

    // 3. 创建token的存储方法
    fun saveToken(token: String) {
        prefs.edit().apply {
            putString("token", token)
            apply()
        }
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun clearToken() {
        prefs.edit().apply {
            remove("token")
            apply()
        }
    }
}