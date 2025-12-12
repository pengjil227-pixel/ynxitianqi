package com.itcast.hmweather.base

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * basePresenter
 * presenter的基类, 所有的presenter都要继承它, 链接 view和interactor，并处理网络请求后的数据逻辑
 */
open class BasePresenter<V : BaseView, T : BaseInteractor<V>>
    (baseView: V, baseInteractor: T) : DefaultLifecycleObserver {


    init {
        baseInteractor.bindLifecycle(baseView)
    }

    /**
     * 在收到onDestroy的时候释放掉关联 在子类重写该方法 有可能收不到回调 所以释放方法单独写
     */
    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    private fun release() {
    }
}
