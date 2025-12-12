package com.itcast.hmweather.ui.me

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.itcast.hmweather.R
import com.itcast.hmweather.WeatherApplication
import com.itcast.hmweather.base.BaseBindingActivity
import com.itcast.hmweather.databinding.ActChangeAvatarBinding
import com.itcast.hmweather.storage.TokenPreferences
import com.itcast.hmweather.ui.me.MeFragment
import com.itcast.hmweather.util.AvatarUploader
import kotlinx.coroutines.launch

class ChangeAvatarActivity : BaseBindingActivity<ActChangeAvatarBinding>()  {

    // 头像地址
    private var avatarUrl: String? = null

    private val REQUEST_IMAGE_CAPTURE = 1 // 相机请求码
    private val REQUEST_IMAGE_PICK = 2 // 相册请求码

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 从全局获取用户的头像地址
        avatarUrl = WeatherApplication.user.avatar
        loadAvatar(avatarUrl ?: "")

        setOnClickEvent()
    }

    override fun initBinding(layoutInflater: LayoutInflater): ActChangeAvatarBinding {
        return ActChangeAvatarBinding.inflate(layoutInflater)
    }

    private fun setOnClickEvent(){
        // 1. 点击返回
        binding.ivBack.setOnClickListener {
            finish()
        }

        // 2. 点击选择头像
        binding.ivAvatar.setOnClickListener {
            showSelectAvatarDialog()
        }
        binding.ivEditAvatar.setOnClickListener {
            showSelectAvatarDialog()
        }
    }

    /**
     * 显示选择头像弹层
     */
    private fun showSelectAvatarDialog() {
        // 1. 创建一个Alert弹层
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("选择头像")
            .setItems(arrayOf("拍照", "相册", "取消")) { dialog, which ->
                // 2. 根据which选择拍照或者相册
                when (which) {
                    0 -> {
                        // 3. 拍照
                        takePhoto()
                    }
                    1 -> {
                        // 4. 相册
                        openAlbum()
                    }
                    2 -> {
                        // 5. 取消
                        dialog.dismiss()
                    }
                }
            }
            .create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                // 拍照成功
            }
            REQUEST_IMAGE_PICK -> {
                // 获取图片地址
                data?.data?.let { uri ->
                    // 显示图片
                    //  binding.ivAvatar.setImageURI(imageUri)
                    uploadAvatar(uri)
                }

            }
        }
    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        // TODO
    }

    /**
     * 打开相册
     */
    @SuppressLint("IntentReset")
    private fun openAlbum() {
        // 1. 创建一个意图打开相册选择图片
        // 让图片的来源来自外部存储的图片媒体
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // 2. 约束图片的类型
        intent.type = "image/*"
        // 3. 启动意图
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    /**
     * 加载头像(网络图片)
     */
    private fun loadAvatar(url: String) {
        lifecycleScope.launch {
            Glide.with(this@ChangeAvatarActivity)
                .load(url)
                .apply(RequestOptions()
                    .circleCrop()
                    .placeholder(R.mipmap.avatar1)
                    .error(R.mipmap.avatar1))
                .into(binding.ivAvatar)
        }
    }

    /**
     * 上传头像
     */
    private fun uploadAvatar(imageUri: Uri) {

        // 0. 获取token
        val token = TokenPreferences.getInstance(this).getToken()

        // 1. 开启一个协程
        lifecycleScope.launch {
            AvatarUploader(this@ChangeAvatarActivity, token.toString()) { avatarUrl, error ->
                if (error != null){
                    Log.e("ChangeAvatarActivity", "上传头像失败: $error")
                } else {
                    loadAvatar(avatarUrl.toString())
                }
            }.uploadAvatar(imageUri)
        }
    }
}