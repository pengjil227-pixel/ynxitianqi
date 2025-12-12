package com.itcast.hmweather.ui.splash


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.itcast.hmweather.MainActivity
import com.itcast.hmweather.R
import com.itcast.hmweather.storage.TokenPreferences
import com.itcast.hmweather.ui.login.LoginActivity



@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkTokenAndRedirect()
    }

    private fun checkTokenAndRedirect() {
        try {
            val token = TokenPreferences.getInstance(this).getToken()
            if (token.isNullOrEmpty()) {
                startActivity(Intent(this,  LoginActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }catch (e: Exception){
            e.printStackTrace()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}