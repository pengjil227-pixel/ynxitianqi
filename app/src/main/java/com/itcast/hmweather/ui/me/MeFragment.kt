package com.itcast.hmweather.ui.me

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.itcast.hmweather.R
import com.itcast.hmweather.WeatherApplication
import com.itcast.hmweather.base.BaseBindingFragment
import com.itcast.hmweather.databinding.FragmentMeBinding
import com.itcast.hmweather.storage.TokenPreferences
import com.itcast.hmweather.ui.login.LoginActivity
import com.itcast.hmweather.util.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MeFragment : BaseBindingFragment<FragmentMeBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUserInfo()
    }

    override fun onResume() {
        super.onResume()
        getUserInfo()
    }


    override fun initBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): FragmentMeBinding {
       return FragmentMeBinding.inflate(inflater,parent,false)
    }

    override fun initView() {
        // 实现点击跳转修改头像
        binding.ivAvatar.setOnClickListener {
            goToChangeAvatar()
        }
        binding.ivEditAvatar.setOnClickListener {
            goToChangeAvatar()
        }

        // 退出登录
        binding.btnLogout.setOnClickListener {
            // 清除token
            TokenPreferences.getInstance(requireContext()).clearToken()
            // 跳转到登录页面
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    /**
     * 获取用户信息
     */
    private fun getUserInfo() {
        lifecycleScope.launch(Dispatchers.Main) {
            val res = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.getUserInfoApi()
            }
            if (res.code == 10000) {
                Toast.makeText(requireContext(), "获取用户信息成功", Toast.LENGTH_SHORT).show()
                val userData = res.data

                // 把用户信息存储到全局
                if(userData != null){
                    WeatherApplication.getInstance().initUser(userData)
                }

                binding.tvAccount.text = userData?.username ?: ""
                binding.tvGender.text = userData?.gender ?: ""
                binding.tvNickname.text = userData?.nick_name ?: ""
                binding.tvSignature.text = userData?.motto ?: ""
                loadAvatar(userData?.avatar ?: "")
            } else {
                Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 加载头像(网络图片)
     */
    private fun loadAvatar(url: String) {
        lifecycleScope.launch {
            Glide.with(this@MeFragment)
                .load(url)
                .apply(RequestOptions()
                    .circleCrop()
                    .placeholder(R.mipmap.avatar1)
                    .error(R.mipmap.avatar1))
                .into(binding.ivAvatar)
        }
    }

    /**
     * 跳转到修改头像页面
     */
     private fun goToChangeAvatar() {
        startActivity(Intent(requireContext(), ChangeAvatarActivity::class.java))
    }

}