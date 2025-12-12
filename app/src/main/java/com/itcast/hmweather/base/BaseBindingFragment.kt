package com.itcast.hmweather.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * 如果要使用ViewBinding 继承这个类 For Fragment
 * 实现viewbinding的基类，继承于BaseFragment
 */
/**
 * 抽象基类 BaseBindingFragment，用于实现 ViewBinding 的 Fragment 基类
 * 使用泛型 VB 来支持不同的 ViewBinding 类型
 *
 * @param <VB> 该 Fragment 对应的 ViewBinding 类型
 */
abstract class BaseBindingFragment<VB : ViewBinding> : BaseFragment() {

    /**
     * _binding：用于持有 ViewBinding 的可空引用，避免内存泄漏
     * binding：非空引用，在视图初始化后使用
     */
    private var _binding: VB? = null
    lateinit var binding: VB

    /**
     * 检查 binding 是否已初始化
     *
     * @return Boolean 表示 binding 是否已初始化
     */
    fun isBindingInit() = ::binding.isInitialized

    /**
     * 抽象方法，子类必须实现以初始化 ViewBinding
     *
     * @param inflater 用于加载布局的 LayoutInflater
     * @param parent   父 ViewGroup，可能为 null
     * @return 初始化后的 ViewBinding 实例
     */
    abstract fun initBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): VB

    /**
     * 获取 Context，通常在 UI 操作中使用
     *
     * @return Context? 返回当前上下文
     */
    override fun getContext(): Context? {
        return super.getContext()
    }

    /**
     * 创建 Fragment 的视图结构
     *
     * @param inflater           LayoutInflater 用来填充布局
     * @param container          父容器，可能是 null
     * @param savedInstanceState 保存的状态 Bundle
     * @return 创建的 View 或 null
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null) {
            // 初始化 ViewBinding
            _binding = initBinding(inflater, container)
            binding = _binding as VB

            // 设置根视图
            mView = _binding?.root

            // 在 initView 调用之前执行一些预处理操作
            beforeInitView()

            // 如果不使用懒加载，则立即初始化视图
            if (!useLazyLoad()) {
                initView()
                mIsInit = true
            }
        }
        return mView
    }

    /**
     * 当视图被销毁时释放资源
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}