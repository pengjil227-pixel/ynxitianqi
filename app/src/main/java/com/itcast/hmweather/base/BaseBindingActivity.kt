package com.itcast.hmweather.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

/**
 * ViewBinding For Activity
 * 支持binding的基类，继承于BaseActivity
 */
abstract class BaseBindingActivity<VB : ViewBinding> : BaseActivity() {

    // 使用lateinit延迟初始化binding对象，避免每次访问都判空
    private lateinit var _binding: VB
    // 提供binding的访问方法，直接返回绑定对象
    val binding: VB get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 在onCreate中设置binding相关内容
        setBindingContent()
    }

    // 初始化并设置binding内容
    private fun setBindingContent() {
        // 调用initBinding方法初始化binding
        _binding = initBinding(layoutInflater)
        // 设置Activity的内容视图为binding.root
        setContentView(_binding.root)
    }

    // 抽象方法用于初始化具体的ViewBinding对象
    // 子类需要实现此方法来绑定具体布局
    protected abstract fun initBinding(layoutInflater: LayoutInflater): VB

}