package com.itcast.hmweather.base

import android.content.Context

/**
 * BaseView接口
 * 作用是为 Android 应用中的视图组件（如 Activity 或 Fragment）提供一个统一的抽象层
 * 以便于实现一些通用的 UI 操作
 */
interface BaseView {
    /**
     * 获取与该视图关联的上下文(Context)
     * @return 返回当前视图所处的上下文环境，可能为null
     */
    fun getContext(): Context?

    /**
     * 显示加载对话框或进度条
     * 用于在数据加载时向用户提供等待提示
     */
    fun showLoading()

    /**
     * 隐藏加载对话框或进度条
     * 用于数据加载完成或取消时移除等待提示
     */
    fun hideLoading()
}
