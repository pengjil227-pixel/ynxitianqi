package com.itcast.hmweather.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.itcast.hmweather.WeatherGlobal
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AvatarUploader(
    private val context: Context,
    private val authToken: String,
    private val callback: (String?, String?) -> Unit
) {
    fun uploadAvatar(imageUri: Uri) {
        try {
            // 1. 将 Uri 转换为 File 对象
            val imageFile = uriToFile(imageUri) ?: run {
                callback(null, "文件转换失败")
                return
            }

            // 2. 创建 Multipart 请求体
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "avatar",
                    imageFile.name,
                    imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .build()

            // 3. 构建请求
            val request = Request.Builder()
                .url("${WeatherGlobal.getBaseUrl()}upload-avatar") // 替换实际IP
                .addHeader("Authorization", "Bearer $authToken")
                .post(requestBody)
                .build()

            // 4. 异步发送请求
            /*
              创建 OkHttp 客户端实例（用于管理网络请求）
              根据 request 对象（包含 URL、请求方法、Header 等）创建一个网络请求
              将请求加入队列，异步执行（非阻塞）
              object: Callback() 匿名内部类，实现 Callback 接口，处理请求结果（需重写两个方法）
            */
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null, "网络请求失败: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                        // 解析 JSON 获取 avatarUrl
                        val avatarUrl = parseAvatarUrl(responseBody)
                        callback(avatarUrl, null)
                    } else {
                        // 处理错误响应
                        val error = when (response.code) {
                            400 -> "未选择文件"
                            401 -> "Token无效或未提供"
                            500 -> "服务器数据库错误"
                            else -> "上传失败: ${response.code}"
                        }
                        callback(null, "$error | 响应: $responseBody")
                    }
                }
            })

        } catch (e: Exception) {
            callback(null, "上传异常: ${e.message}")
        }
    }

    // 解析响应中的 avatarUrl
    private fun parseAvatarUrl(json: String): String? {
        return try {
            val jsonObject = JSONObject(json)
            jsonObject.getJSONObject("data").getString("avatarUrl")
        } catch (e: Exception) {
            null
        }
    }

    // Uri 转 File 工具方法
    private fun uriToFile(uri: Uri): File? {
        return try {
            val stream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File.createTempFile("avatar_", ".jpg", context.cacheDir)
            FileOutputStream(file).use { output ->
                stream.copyTo(output)
                output.flush()
            }
            file
        } catch (e: Exception) {
            Log.e("AvatarUpload", "文件转换错误", e)
            null
        }
    }
}