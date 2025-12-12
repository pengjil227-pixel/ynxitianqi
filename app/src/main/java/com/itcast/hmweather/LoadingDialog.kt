package com.itcast.hmweather

import android.content.Context
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.app.AppCompatDialog

/**
 * 自定义加载对话框-初始化-主要用于全局配置
 * 继承自AppCompatDialog（兼容旧版Android的对话框）
 * 使用@JvmOverloads支持Java调用时的重载
 * 默认使用R.style.LoadingDialog主题样式
 */
class LoadingDialog @JvmOverloads constructor(
    context: Context,
    theme: Int = R.style.LoadingDialog
) : AppCompatDialog(context, theme) {
    class Builder(private val mContext: Context) {
        @JvmOverloads
        fun create(cancelable: Boolean = true): LoadingDialog {
            // 创建对话框实例
            val dialog = LoadingDialog(mContext)
            // 配置对话框属性
            dialog.setCancelable(cancelable) // 是否可取消
            dialog.setContentView(R.layout.dialog_loading) // 加载布局
            dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT) // 设置窗口尺寸
            return dialog
        }
    }

}