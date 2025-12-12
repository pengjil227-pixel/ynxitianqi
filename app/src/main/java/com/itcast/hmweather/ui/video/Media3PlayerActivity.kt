package com.itcast.hmweather.ui.video

import android.view.LayoutInflater
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.itcast.hmweather.base.BaseBindingActivity
import com.itcast.hmweather.databinding.AcMedia3PlayerBinding

class Media3PlayerActivity : BaseBindingActivity<AcMedia3PlayerBinding>() {

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    /**
     * 当activity开始时, 初始化播放器
     */
    override fun onStart() {
        super.onStart()
        initPlayer()
    }

    /**
     * 当activity停止时, 释放播放器资源
     */
    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun initBinding(layoutInflater: LayoutInflater): AcMedia3PlayerBinding {
        return AcMedia3PlayerBinding.inflate(layoutInflater)
    }

    /**
     * 初始化播放器
     */
    private fun initPlayer() {
        // 创建并初始化 ExoPlayer 实例
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            // 将 ExoPlayer 与界面中的 playerView 绑定
            binding.playerView.player = exoPlayer

            // 构建要播放的媒体资源 URI，这里是应用内 raw 目录下的视频文件
            val rawUri = "android.resource://${packageName}/raw/weather_demo".toUri()
            val mediaItem = MediaItem.fromUri(rawUri)

            // 设置媒体资源到播放器
            exoPlayer.setMediaItem(mediaItem)
            // 设置是否自动播放（根据之前保存的状态）
            exoPlayer.playWhenReady = playWhenReady
            // 恢复到之前保存的播放位置和窗口
            exoPlayer.seekTo(currentWindow, playbackPosition)
            // 准备播放器，加载媒体资源
            exoPlayer.prepare()
        }
    }

    /**
     * 释放播放器资源
     */
    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentWindow = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player?.release()
    }
}