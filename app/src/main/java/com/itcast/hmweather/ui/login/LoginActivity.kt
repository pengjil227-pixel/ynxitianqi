package com.itcast.hmweather.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.itcast.hmweather.MainActivity
import com.itcast.hmweather.base.BaseBindingActivity
import com.itcast.hmweather.bean.LoginRequest
import com.itcast.hmweather.databinding.AcLoginBinding
import com.itcast.hmweather.storage.TokenPreferences
import com.itcast.hmweather.util.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.os.Build
import android.Manifest
import android.location.LocationManager
import android.provider.Settings
import com.itcast.hmweather.util.AmapUtils
import pub.devrel.easypermissions.EasyPermissions

class LoginActivity : BaseBindingActivity<AcLoginBinding>() {
    // 申请权限
    private val mPermissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) { // Android 12及以下（API 32）
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 低版本需要此权限
            Manifest.permission.CAMERA
        )
    } else { // Android 13及以上（API 33+）
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )
    }
    private val RC_LOCATION_PERM = 123

    private var hasInitAMap = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 检查权限
        checkRequiredPermissions()

        // 1. 监听登录按钮的点击
        binding.tvLogin.setOnClickListener {
          doLogin()
        }

    }

    fun checkRequiredPermissions() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {

            if (hasInitAMap == false) {
                AmapUtils.updatePrivacyCompliance(this)
                hasInitAMap = true
            }

            if (EasyPermissions.hasPermissions(this, *mPermissions)) {
                if (hasInitAMap == false) {
                    AmapUtils.updatePrivacyCompliance(this)
                    hasInitAMap = true
                }
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "需要位置权限以提供准确的天气信息",
                    RC_LOCATION_PERM,
                    *mPermissions
                )
            }
        }
    }

    override fun initBinding(layoutInflater: LayoutInflater): AcLoginBinding {
        return AcLoginBinding.inflate(layoutInflater)
    }

    /**
     * 登录
     */
    private fun doLogin() {
        // 1. 获取用户输入的用户名和密码
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        // 2. 账号和密码不能为空
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "账号和密码不能为空", Toast.LENGTH_SHORT).show()
             return
        }

        // 3. 登录的业务逻辑
        try {
            // 3.1 创建一个协程
            lifecycleScope.launch(Dispatchers.Main) {
                // 3.2 配置登录的对象数据
                val loginRequest = LoginRequest(username, password)
                // 3.3 在IO线程中进行登录
                val res = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.login(loginRequest)
                }
                if (res.code == 10000) {
                    // --------------------
                    val token = res.data?.token

                    if(token.isNullOrEmpty()){
                        Toast.makeText(this@LoginActivity, "token为空", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // 持久化存储token
                    TokenPreferences.getInstance(this@LoginActivity).saveToken(token.toString())

                    // --------------------

                    // 3.4 登录成功
                    Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
                    // 3.5 跳转到主页
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // 3.6 登录失败
                    Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show()
        }
    }
}