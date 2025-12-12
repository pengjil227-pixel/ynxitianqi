package com.itcast.hmweather.base

import androidx.lifecycle.LifecycleOwner

/**
 * Interactor（模型交互层）基类
 * 页面通用接口
 * 和presenter交互的请求基类，如果多个业务模块有共同的请求，可以放在这里
 */
open class BaseInteractor<V : BaseView?> {
    var baseView: BaseView? = null

    // 绑定生命周期方法，将视图层（Activity/Fragment）的生命周期与Interactor绑定
    fun bindLifecycle(baseView: V) {
        this.baseView = baseView
    }

    // 获取绑定的生命周期拥有者（可以是Activity或Fragment）
    val lifecycleOwner: LifecycleOwner?
        get() {
            return when (baseView) {
                // 如果绑定的是BaseActivity，则作为LifecycleOwner返回
                is BaseActivity -> {
                    baseView as BaseActivity
                }

                // 如果绑定的是BaseFragment，则作为LifecycleOwner返回
                is BaseFragment -> {
                    baseView as BaseFragment
                }

                // 不支持的类型，返回null
                else -> {
                    null
                }
            }
        }
}
